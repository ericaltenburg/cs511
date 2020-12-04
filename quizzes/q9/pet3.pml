// I pledge my honor that I have abided by the Stevens Honor System. - Eric Altenburg

bool wantP=false;
bool wantQ=false;
byte last=1;
byte cs=0;

active proctype P() {
	do
		::	last=1;
			wantP=true;
			!wantQ || last==2;
			cs++;
			assert(cs==1);
			cs--;
			wantP=false
		od
}

active proctype Q() {
	do
		::	last=2;
			wantQ=true;
			!wantP || last==1;
			cs++;
			assert(cs==1);
			cs--;
			wantQ=false
		od
}