import java.util.concurrent.Semaphore;

// Declare your semaphores here
Semaphore client = new Semaphore(2); // can have two at once
Semaphore employee = new Semaphore(1); // only one can refill

100.times {
	Thread.start { // Client
		client.acquire();
		print(" grabbing coffee ");
		client.release();
	}
}

5.times {
	Thread.start { // employee
		
		client.acquire();
		client.acquire();
		employee.acquire();
		println(" refill dispenser ");
		employee.release();
		client.release();
		client.release();

	}
}