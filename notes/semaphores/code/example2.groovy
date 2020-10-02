import java.util.concurrent.Semaphore;

Semaphore P = new Semaphore(-1);

int c = 0;

Thread.start { // P
	// one.acquire();
	c++; // atomic
	P.release();
}

Thread.start { // P
	// one.acquire();
	c++; // atomic
	P.release();
}

// Print c only after P and Q are done this way it is always 2
P.acquire(); // Block, then wait till two releases so that it can be ready
// P.acquire(); use this if you set semaphore to 0.
// Run 20, wait till another release, Run 21, wait till another release
// Issue: Run 20, it is blocked, run 10, the release code will see that the process has 20 process
// so I will move it to ready, now it will be able to run 23 without needing the other release on 16
println(c);
return ;