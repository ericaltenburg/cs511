-module(ex).
-author("E.B.").
-compile(export_all).

%   fact(0)->
% 	1;
% fact(N) when N>0 ->    
% 	N*fact(N-1).

%% Empty tree: {empty.
%% Non-Empty tree: {node,Data,LT,RT. 
-type btree() :: {empty} | {node,number(),btree(),btree()}.
-spec t() -> btree().
t() ->    
	{node,12,          		
		{node,7,{empty},{empty}},          
		{node,24,	         
			{node,18,{empty},{empty}},
	        {empty}}}.

% -spec sum(btree()) -> number().
% sum({empty}) ->   
% 	0;
% sum({node,D,LT,RT}) ->    
% 	D + sum(LT) + sum(RT). 

% bump({empty}) ->    
% 	{empty};
% bump({node,D,LT,RT}) ->    
% 	{node, D+1,bump(LT),bump(RT)}.  

% mirror({empty}) ->   
% 	{empty};
% mirror({node,D,LT,RT}) ->     
% 	{node,D,mirror(RT),mirror(LT)}.  
% bumpl([]) ->    
% 	[];
% bumpl([H | T]) ->    
% 	[ H+1 | bumpl(T)].  

%% Exercise: implement a function pre that given a binary tree produces th.
%% pre-order traversal of the tree.%% For example.
%%  > ex:pre(ex:t()).
%%  > [12,7,24,18]. 

pre({empty}) ->
	[];
pre({node, D, LT, RT}) ->
	[D] ++ pre(LT) ++ pre(RT).

in({empty}) ->
	[];
in({node, D, LT, RT}) ->
	in(LT) ++ [D] ++ in(RT).

post({empty}) ->
	[];
post({node, D, LT, RT}) ->
	post(LT) ++ post(RT) ++ [D].

mapf(_F, []) ->
	[].
mapf(F, [H | T]) ->
	[F(H) | mapf(F, T)];









