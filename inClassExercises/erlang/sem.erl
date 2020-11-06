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
	receive
		{_From, release} ->
			semaphore(1);
	end;
semaphore(N) when N>0 ->
	receive
		{_From, release} ->
			semaphore(N+1);
		{_From, acquire} ->
			From!{self(), ok},
			semaphore(N-1)
	end;

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