
import java.util.concurrent.Semaphore;

Semaphore permToY = new Semaphore(0);
Semaphore permToE = new Semaphore(0);
Semaphore permToB = new Semaphore(1);

Thread.start {
	10.times {
		permToB.acquire();
		print("b");
		permToY.release();
	}
}

Thread.start {
	10.times {
		permToY.acquire();
		print("y");
		permToE.release();
	}
}

Thread.start {
	10.times {
		permToE.acquire();
		print("e");
		permToB.release();
	}
}

return;