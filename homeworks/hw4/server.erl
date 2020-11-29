% Names: Eric Altenburg and Sarvani Patel
% Pledge: I pledge my honor that I have abided by the Stevens Honor System.

-module(server).

-export([start_server/0]).

-include_lib("./defs.hrl").

-spec start_server() -> _.
-spec loop(_State) -> _.
-spec do_join(_ChatName, _ClientPID, _Ref, _State) -> _.
-spec do_leave(_ChatName, _ClientPID, _Ref, _State) -> _.
-spec do_new_nick(_State, _Ref, _ClientPID, _NewNick) -> _.
-spec do_client_quit(_State, _Ref, _ClientPID) -> _NewState.

start_server() ->
    catch(unregister(server)),
    register(server, self()),
    case whereis(testsuite) of
	undefined -> ok;
	TestSuitePID -> TestSuitePID!{server_up, self()}
    end,
    loop(
      #serv_st{
	 nicks = maps:new(), %% nickname map. client_pid => "nickname"
	 registrations = maps:new(), %% registration map. "chat_name" => [client_pids]
	 chatrooms = maps:new() %% chatroom map. "chat_name" => chat_pid
	}
     ).

loop(State) ->
    receive 
	%% initial connection
	{ClientPID, connect, ClientNick} ->
	    NewState =
		#serv_st{
		   nicks = maps:put(ClientPID, ClientNick, State#serv_st.nicks),
		   registrations = State#serv_st.registrations,
		   chatrooms = State#serv_st.chatrooms
		  },
	    loop(NewState);
	%% client requests to join a chat
	{ClientPID, Ref, join, ChatName} ->
	    NewState = do_join(ChatName, ClientPID, Ref, State),
	    loop(NewState);
	%% client requests to join a chat
	{ClientPID, Ref, leave, ChatName} ->
	    NewState = do_leave(ChatName, ClientPID, Ref, State),
	    loop(NewState);
	%% client requests to register a new nickname
	{ClientPID, Ref, nick, NewNick} ->
	    NewState = do_new_nick(State, Ref, ClientPID, NewNick),
	    loop(NewState);
	%% client requests to quit
	{ClientPID, Ref, quit} ->
	    NewState = do_client_quit(State, Ref, ClientPID),
	    loop(NewState);
	{TEST_PID, get_state} ->
	    TEST_PID!{get_state, State},
	    loop(State)
    end.

%% executes join protocol from server perspective
do_join(ChatName, ClientPID, Ref, State) ->
    Rooms = maps:keys(State#serv_st.chatrooms),
    case lists:member(ChatName, Rooms) of
    	true ->
    		UpdateRooms = State#serv_st.chatrooms,
    		{ok, RoomValue} = maps:find(ChatName, UpdateRooms),
			{ok, Nickname} = maps:find(ClientPID, State#serv_st.nicks),
			RoomValue!{self(), Ref, register, ClientPID, Nickname},
			#serv_st {
				nicks = State#serv_st.nicks,
				registrations = maps:update(
    						ChatName,
    						lists:append([ClientPID], maps:get(ChatName, State#serv_st.registrations)),
    						State#serv_st.registrations),
				chatrooms = UpdateRooms
			};
		false ->
			NewChatRoom = spawn(chatroom, start_chatroom, [ChatName]),
			UpdateRooms = maps:put(ChatName, NewChatRoom, State#serv_st.chatrooms),
			{ok, RoomValue} = maps:find(ChatName, UpdateRooms),
			{ok, Nickname} = maps:find(ClientPID, State#serv_st.nicks),
			RoomValue!{self(), Ref, register, ClientPID, Nickname},
			#serv_st {
				nicks = State#serv_st.nicks,
				registrations = maps:put(ChatName, [ClientPID], State#serv_st.registrations),
				chatrooms = UpdateRooms
			}
	end.

%% executes leave protocol from server perspective
do_leave(ChatName, ClientPID, Ref, State) ->
    RoomPID = maps:get(ChatName, State#serv_st.chatrooms),
    ClientPIDs = maps:get(ChatName, State#serv_st.registrations),
    NewRegs = maps:put(
    	ChatName,
    	lists:delete(ClientPID, ClientPIDs),
    	State#serv_st.registrations),
    RoomPID!{self(), Ref, unregister, ClientPID},
    ClientPID!{self(), Ref, ack_leave},
    #serv_st {
    	nicks = State#serv_st.nicks,
    	registrations = NewRegs,
    	chatrooms = State#serv_st.chatrooms
    }.

%% executes new nickname protocol from server perspective
do_new_nick(State, Ref, ClientPID, NewNick) ->
    Nicknames = maps:values(State#serv_st.nicks),
    case lists:member(NewNick, Nicknames) of
    	true ->
    		ClientPID!{self(), Ref, err_nick_used},
    		State;
		false ->
			RoomNames = maps:filter(
				fun(_, Clients) ->
					lists:member(ClientPID, Clients)
				end,
				State#serv_st.registrations),
			RoomPIDs = maps:filter(
				fun(Name, _) ->
					lists:member(Name, maps:keys(RoomNames))
				end,
				State#serv_st.chatrooms),
			maps:map(
				fun(_, RoomPID) ->
					RoomPID!{self(), Ref, update_nick, ClientPID, NewNick},
					RoomPID
				end,
				RoomPIDs),
			NewNicknames = maps:update(ClientPID, NewNick, State#serv_st.nicks),
			ClientPID!{self(), Ref, ok_nick},
			#serv_st {
				nicks = NewNicknames,
				registrations = State#serv_st.registrations,
				chatrooms = State#serv_st.chatrooms
			}
	end.

%% executes client quit protocol from server perspective
do_client_quit(State, Ref, ClientPID) ->
	io:format("Quitting...~n"),
    RoomNames = maps:filter(
    	fun(_, Clients) ->
			lists:member(ClientPID, Clients)
		end,
		State#serv_st.registrations),
    RoomPIDs = maps:filter(
    	fun(Nm, _) -> 
	   		lists:member(Nm, maps:keys(RoomNames))
	   	end, 
	   	State#serv_st.chatrooms),
    _Extra = maps:map(
    	fun(_,Chat_PID) ->
	     	Chat_PID!{self(), Ref, upregister, ClientPID},
	     	Chat_PID
     	end, 
     	RoomPIDs),
    UpdatedRegs = maps:map(
    	fun(_, Clients) ->
	    	case lists:member(ClientPID, Clients) of
				true ->
				    lists:delete(ClientPID, Clients);
				false ->
				    Clients
		    end
	    end, 
	    State#serv_st.registrations),
    ClientPID!{self(), Ref, ack_quit},
    #serv_st{
   		nicks = maps:remove(ClientPID,State#serv_st.nicks), 
       	registrations = UpdatedRegs,
       	chatrooms = State#serv_st.chatrooms
    }.







