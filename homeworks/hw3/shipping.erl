-module(shipping).
-compile(export_all).
-include_lib("./shipping.hrl").

% Authors: Eric Altenburg and Sarvani Patel
% Pledge: I pledge my honor that I have abided by the Stevens Honor System

get_ship(Shipping_State, Ship_ID) ->
	S = lists:keyfind(Ship_ID, #ship.id, Shipping_State#shipping_state.ships),
    case S of
		false ->
			error;
		_ ->
			S
	end.

get_container(Shipping_State, Container_ID) ->
	C = lists:keyfind(Container_ID, #container.id, Shipping_State#shipping_state.containers),
	case C of
		false ->
			error;
		_ ->
			C
	end.

get_port(Shipping_State, Port_ID) ->
    L = lists:keyfind(Port_ID, #port.id, Shipping_State#shipping_state.ports),
    case L of
    	false ->
    		error;
		_ ->
			L		
	end.

get_occupied_docks(Shipping_State, Port_ID) ->
    lists:filtermap(
    	fun(Param) ->
    		case Param of
    			{Port_ID, Dock_id, _Shipping_id} ->
    				{true, Dock_id};
    			_ ->
    				false
			end
		end,
		Shipping_State#shipping_state.ship_locations).

get_ship_location(Shipping_State, Ship_ID) ->
    L = lists:keyfind(
    	Ship_ID,
    	3,
    	Shipping_State#shipping_state.ship_locations),
    case L of
    	{Port_id, Dock_id, _Ship_id} ->
    		{Port_id, Dock_id};
		false ->
			error
	end.

get_container_weight(Shipping_State, Container_IDs) ->
    L = lists:map(
    	fun(Container_id) ->
    		Container = shipping:get_container(Shipping_State, Container_id),
    		case Container of 
    			error ->
    				error;
				_ ->
					Container#container.weight
			end
		end,
		Container_IDs),
    case lists:member(error, L) of
    	true ->
    		error;
		false ->
			lists:sum(L)
	end.

get_ship_weight(Shipping_State, Ship_ID) ->
	M = maps:find(Ship_ID, Shipping_State#shipping_state.ship_inventory),
	case M of
		{ok, Value} ->
			get_container_weight(Shipping_State, Value);
		_ ->
			error
	end.

load_ship(Shipping_State, Ship_ID, Container_IDs) ->
    case get_ship_location(Shipping_State, Ship_ID) of
    	error ->
    		error;
    	{Port_id, _} ->
    		case maps:find(Ship_ID, Shipping_State#shipping_state.ship_inventory) of
    			{ok, Value} ->
    				Current_ship_inventory = length(Value) + length(Container_IDs),
    				Ship = shipping:get_ship(Shipping_State, Ship_ID),
    				case Ship of
    					error ->
    						error;
						_ ->
							Capacity = Ship#ship.container_cap,
							Containers_in_port = lists:all(
								fun(X) ->
									lists:member(
										X, 
										maps:get(Port_id, Shipping_State#shipping_state.port_inventory))
								end,
								Container_IDs),
							case {Containers_in_port, Capacity >= Current_ship_inventory} of
								{true, true} ->
									{ok, #shipping_state{
										ships = Shipping_State#shipping_state.ships,
										containers = Shipping_State#shipping_state.containers,
										ports = Shipping_State#shipping_state.ports,
										ship_locations = Shipping_State#shipping_state.ship_locations,
										ship_inventory = maps:put(Ship_ID, 
											lists:merge(
												maps:get(Ship_ID, Shipping_State#shipping_state.ship_inventory), 
												Container_IDs), 
											Shipping_State#shipping_state.ship_inventory),
										port_inventory = maps:put(Port_id, 
											lists:filter(
												fun(X) ->
													not lists:member(X, Container_IDs)
												end,
												maps:get(Port_id, Shipping_State#shipping_state.port_inventory)),
											maps:remove(Port_id, Shipping_State#shipping_state.port_inventory))}};
								_ ->
									error
							end
					end;
				_ ->
					error
			end
	end.


unload_ship_all(Shipping_State, Ship_ID) ->
	Ship = maps:find(Ship_ID, Shipping_State#shipping_state.ship_inventory),
	case Ship of
		{ok, Value} ->
			shipping:unload_ship(Shipping_State, Ship_ID, Value);
		_ ->
			error
	end.

unload_ship(Shipping_State, Ship_ID, Container_IDs) ->
	case get_ship_location(Shipping_State, Ship_ID) of
    	error ->
    		error;
    	{Port_id, _} ->
    		case maps:find(Port_id, Shipping_State#shipping_state.port_inventory) of
    			{ok, Value} ->
    				Containers_to_be_in_port = length(Value) + length(Container_IDs),
    				Port_container_cap = (shipping:get_port(Shipping_State, Port_id))#port.container_cap,
    				Are_containers_on_ship = lists:all(
    					fun(X) ->
    						lists:member(X, maps:get(Ship_ID, Shipping_State#shipping_state.ship_inventory))
						end,
						Container_IDs),
    				case {Are_containers_on_ship, Port_container_cap >= Containers_to_be_in_port} of
    					{true, true} ->
    						{ok, #shipping_state{
	       						ships = Shipping_State#shipping_state.ships,
       							containers = Shipping_State#shipping_state.containers,
       							ports = Shipping_State#shipping_state.ports,
	       						ship_locations = Shipping_State#shipping_state.ship_locations,
	      					 	ship_inventory =  maps:put(Ship_ID,lists:filter(
		      					 		fun(X) -> 
		      					 			not lists:member(X, Container_IDs) 
		      					 		end, 
		      					 		maps:get(Ship_ID, Shipping_State#shipping_state.ship_inventory)), 
		      					 	maps:remove(Ship_ID, Shipping_State#shipping_state.ship_inventory)),
       							port_inventory = maps:put(Port_id,
       								lists:merge(
	       								maps:get(Port_id, Shipping_State#shipping_state.port_inventory),
	       								Container_IDs), 
       								Shipping_State#shipping_state.port_inventory)
   							}};
						{false, true} ->
							io:format("The given containers are not all on the same ship...~n"),
							error;
						_ -> 
							error
					end;
				_ ->
					error
			end
	end.

set_sail(Shipping_State, Ship_ID, {Port_ID, Dock}) ->
    Is_empty = lists:all(
    	fun(X) ->
    		case X of
    			{Port_ID, Dock, _} ->
    				false;
				_ ->
					true
			end
		end,
		Shipping_State#shipping_state.ship_locations),
    case Is_empty of
    	false ->
    		error;
		true ->
			{ok, #shipping_state{
				ships = Shipping_State#shipping_state.ships,
				containers = Shipping_State#shipping_state.containers,
				ports = Shipping_State#shipping_state.ports,
				ship_locations = lists:merge(
					lists:keydelete(
						Ship_ID,
						3,
						Shipping_State#shipping_state.ship_locations),
					[{Port_ID, Dock, Ship_ID}]),
				ship_inventory = Shipping_State#shipping_state.ship_inventory,
				port_inventory = Shipping_State#shipping_state.port_inventory
			}}
	end.




%% Determines whether all of the elements of Sub_List are also elements of Target_List
%% @returns true is all elements of Sub_List are members of Target_List; false otherwise
is_sublist(Target_List, Sub_List) ->
    lists:all(fun (Elem) -> lists:member(Elem, Target_List) end, Sub_List).




%% Prints out the current shipping state in a more friendly format
print_state(Shipping_State) ->
    io:format("--Ships--~n"),
    _ = print_ships(Shipping_State#shipping_state.ships, Shipping_State#shipping_state.ship_locations, Shipping_State#shipping_state.ship_inventory, Shipping_State#shipping_state.ports),
    io:format("--Ports--~n"),
    _ = print_ports(Shipping_State#shipping_state.ports, Shipping_State#shipping_state.port_inventory).


%% helper function for print_ships
get_port_helper([], _Port_ID) -> error;
get_port_helper([ Port = #port{id = Port_ID} | _ ], Port_ID) -> Port;
get_port_helper( [_ | Other_Ports ], Port_ID) -> get_port_helper(Other_Ports, Port_ID).


print_ships(Ships, Locations, Inventory, Ports) ->
    case Ships of
        [] ->
            ok;
        [Ship | Other_Ships] ->
            {Port_ID, Dock_ID, _} = lists:keyfind(Ship#ship.id, 3, Locations),
            Port = get_port_helper(Ports, Port_ID),
            {ok, Ship_Inventory} = maps:find(Ship#ship.id, Inventory),
            io:format("Name: ~s(#~w)    Location: Port ~s, Dock ~s    Inventory: ~w~n", [Ship#ship.name, Ship#ship.id, Port#port.name, Dock_ID, Ship_Inventory]),
            print_ships(Other_Ships, Locations, Inventory, Ports)
    end.

print_containers(Containers) ->
    io:format("~w~n", [Containers]).

print_ports(Ports, Inventory) ->
    case Ports of
        [] ->
            ok;
        [Port | Other_Ports] ->
            {ok, Port_Inventory} = maps:find(Port#port.id, Inventory),
            io:format("Name: ~s(#~w)    Docks: ~w    Inventory: ~w~n", [Port#port.name, Port#port.id, Port#port.docks, Port_Inventory]),
            print_ports(Other_Ports, Inventory)
    end.
%% This functions sets up an initial state for this shipping simulation. You can add, remove, or modidfy any of this content. This is provided to you to save some time.
%% @returns {ok, shipping_state} where shipping_state is a shipping_state record with all the initial content.
shipco() ->
    Ships = [#ship{id=1,name="Santa Maria",container_cap=20},
              #ship{id=2,name="Nina",container_cap=20},
              #ship{id=3,name="Pinta",container_cap=20},
              #ship{id=4,name="SS Minnow",container_cap=20},
              #ship{id=5,name="Sir Leaks-A-Lot",container_cap=20}
             ],
    Containers = [
                  #container{id=1,weight=200},
                  #container{id=2,weight=215},
                  #container{id=3,weight=131},
                  #container{id=4,weight=62},
                  #container{id=5,weight=112},
                  #container{id=6,weight=217},
                  #container{id=7,weight=61},
                  #container{id=8,weight=99},
                  #container{id=9,weight=82},
                  #container{id=10,weight=185},
                  #container{id=11,weight=282},
                  #container{id=12,weight=312},
                  #container{id=13,weight=283},
                  #container{id=14,weight=331},
                  #container{id=15,weight=136},
                  #container{id=16,weight=200},
                  #container{id=17,weight=215},
                  #container{id=18,weight=131},
                  #container{id=19,weight=62},
                  #container{id=20,weight=112},
                  #container{id=21,weight=217},
                  #container{id=22,weight=61},
                  #container{id=23,weight=99},
                  #container{id=24,weight=82},
                  #container{id=25,weight=185},
                  #container{id=26,weight=282},
                  #container{id=27,weight=312},
                  #container{id=28,weight=283},
                  #container{id=29,weight=331},
                  #container{id=30,weight=136}
                 ],
    Ports = [
             #port{
                id=1,
                name="New York",
                docks=['A','B','C','D'],
                container_cap=200
               },
             #port{
                id=2,
                name="San Francisco",
                docks=['A','B','C','D'],
                container_cap=200
               },
             #port{
                id=3,
                name="Miami",
                docks=['A','B','C','D'],
                container_cap=200
               }
            ],
    %% {port, dock, ship}
    Locations = [
                 {1,'B',1},
                 {1, 'A', 3},
                 {3, 'C', 2},
                 {2, 'D', 4},
                 {2, 'B', 5}
                ],
    Ship_Inventory = #{
      1=>[14,15,9,2,6],
      2=>[1,3,4,13],
      3=>[],
      4=>[2,8,11,7],
      5=>[5,10,12]},
    Port_Inventory = #{
      1=>[16,17,18,19,20],
      2=>[21,22,23,24,25],
      3=>[26,27,28,29,30]
     },
    #shipping_state{ships = Ships, containers = Containers, ports = Ports, ship_locations = Locations, ship_inventory = Ship_Inventory, port_inventory = Port_Inventory}.
