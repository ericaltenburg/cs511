import java.util.concurrent.Semaphore;

Semaphore s = new Semaphore(0);
Semaphore y = new Semaphore(0);

Thread.start {
	s.acquire();
	print("A");
	print("C");
	y.release();
}

Thread.start {
	print("R");
	s.release();
	y.acquire();
	print("E");
	print("S");
}

return;