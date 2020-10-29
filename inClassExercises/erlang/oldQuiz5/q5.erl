-module(q5).
-compile(export_all).

-type btree() :: {empty}
	      |  {node,number(),btree(),btree()}.

-spec t1() -> btree().
t1() ->
    {node,1,{node,2,{empty},{empty}},{node,3,{empty},{empty}}}.

-spec t2() -> btree().
t2() ->
    {node,1,
     {node,2,{empty},{empty}},
     {node,3,{empty},
      {node,3,{empty},{empty}}}}.

all_empty(Q) ->
	case queue:out(Q) of
		{empty, _} -> 
			true;
		{{value, {empty}}, Q2} ->
			all_empty(Q2);
		{node, _D, _LT, _RT} ->
			false
	end.

ic_helper(Q) ->
	{{value, T}, Q2} = queue:out(Q),
	case T of 
		{empty} ->
			all_empty(Q2);
		{node, _D, LT, RT} ->
			ic_helper(queue:in(RT, queue:in(LT, Q2)))
	end.

-spec ic(btree()) -> boolean().
ic(T) ->
    ic_helper(queue:in(T,queue:new())).

