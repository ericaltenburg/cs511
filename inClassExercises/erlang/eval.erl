-module(eval).
-compile(export_all).


%% Exercise 1


%% Syntax of a simple arithmetic language
%%
%% <exp> ::= 
%%    | {const ,<integer >} 
%%    | {add ,<exp >,<exp >} 
%%    | {sub ,<exp >,<exp >} 
%%    | {mul ,<exp >,<exp >} 
%%    | {divi ,<exp >,<exp >}
%%
%% Example:  3 + (4/2)
%%
%% Example of evaluation:
%%
%% > eval:calc(eval:e1()).
%% {val,5}

e1() ->
   {add, 
       {const,3},
       {divi,
	   {const,4},
	   {const,2}}}.

calc({const,N}) ->
    implement;
calc({add,E1,E2}) ->
    implement;
calc({sub,E1,E2}) ->
    implement;
calc({mult,E1,E2}) ->
    implement;
calc({divi,E1,E2}) ->
    implement.



%% Exercise 2


%% Syntax of a simple arithmetic language
%%
%% <exp> ::= 
%%    | {var, <string>}
%%    | {const ,<integer >} 
%%    | {add ,<exp >,<exp >} 
%%    | {sub ,<exp >,<exp >} 
%%    | {mul ,<exp >,<exp >} 
%%    | {divi ,<exp >,<exp >}
%%
%% Example:  3 + (x/2)
%%
%% Example of evaluation:
%%
%% > eval:calc(eval:e2()),[{"x",8}, {"y",2}]).
%% {val,7}

e2() ->
   {add, 
       {const,3},
       {divi,
	   {var,"x"},
	   {const,2}}}.

calc2(E,Env) ->
   implement.


    

