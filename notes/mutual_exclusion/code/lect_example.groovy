// Turnstile example

int counter = 0;

def P = Thread.start {
	10.times {
		int temp = counter;
		counter = temp + 1;
	}
}

def Q = Thread.start {
	10.times {
		int temp = counter;
		counter = temp + 1;
	}
}

P.join();
Q.join();
println(counter);
return;
// Exhibit the execution path that leads to x begin 2, after the program finishes