import java.util.concurrent.Semaphore;

Semaphore permToCD = new Semaphore(0);
Thread.start { // P
	println("A");
	println("B");
	permToCD.release(); // 2. P must release to give Q a permission for scheduling
}

Thread.start { //Q
	permToCD.acquire(); // 1. Q doesn't have permissions yet, release to give it permissions
	println("C");
	println("D");
}

return;