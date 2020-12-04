// I pledge my honor that I have abided by the Stevens Honor System. - Eric Altenburg

bool wantP=false;
bool wantQ=false;
byte last=1;

active proctype P() {
	do
		:: 	wantP=true;
			last=1;
			!wantQ || last==2;
			wantP=false
		od
}

active proctype Q() {
	do
		::	wantQ=true;
			last=2;
			!wantP || last==1;
			wantQ=false
		od
}