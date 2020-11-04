-module(fs).
-compile(export_all).

fact(0) ->
	1;
fact(N) ->
	N*fact(N-1).

start(N) ->
	S = spawn(?MODULE,server,[]),
	[spawn(?MODULE,client,[S]) || _ <- lists:seq(1, N)],
	ok.

server() ->
	receive
		{From, req, N} ->
			From!{self(), ans, fact(N)},
			server()
	end.

client(S) ->
	N = rand:uniform(20),
	S ! {self(), req, N},
	receive
		{S, ans, F} ->
			io:format("~p says: The factorial of ~w is ~w~n", [self(),N, F])
	end.
