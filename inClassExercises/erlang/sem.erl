-module(sem).
-compile(export_all).

start(Permits) ->
	S = spawn(?MODULE, semaphore, [Permits]),
	%% spawn client1 and client2
	spawn(?MODULE, client1, [S]),
	spawn(?MODULE, client2, [S]),
	% todo.
	ok.

semaphore(0) ->
	todo;
semaphore(N) when N>0 ->
	todo.

acquire(S) ->
	S ! {self(), acquire}, 
	receive
			{S, ok} -> ok
	end.

release(S) ->
	S ! {self(), release}.

%% Print ab only after printing cd

client1(S) ->
	acquire(S),
	io:format("a"),
	io:format("b").


client2(S) ->
	io:format("c"),
	io:format("d"),
	release(S).