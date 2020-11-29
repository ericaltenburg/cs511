-module(gg).
-compile(export_all).

% Completed with Jared Weinblatt
% I pledge my honor that I have abided by the Stevens Honor System.

start() ->
	S = spawn(fun server/0),
	spawn(?MODULE, client, [S]).

server() ->
	receive
		{From, start} ->
			Servlet = spawn(?MODULE, servlet, [From, rand:uniform(10)]),
			From!{ok, Servlet},
			server()
	end.

servlet(Client, Number) ->
	receive
		{Client, Guess} ->
			if 
				Number == Guess ->
					Client!{gotIt};
				true ->
					Client!{tryAgain},
					servlet(Client, Number)
			end
	end.

client(S) ->
	S!{self(), start},
	receive
		{ok, Servlet} ->
			client_loop(Servlet, rand:uniform(10), 0)
	end.

client_loop(Servlet, Guess, N) ->
	Servlet!{self(), Guess},
		receive
			{gotIt} ->
				io:format("Client ~p guessed ~w in ~w attempts~n", [self(), Guess, N]),
				ok;
			{tryAgain} ->
				client_loop(Servlet, rand:uniform(10), N+1)
		end.