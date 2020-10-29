import java.util.concurrent.Semaphore;

// Writers can write so long as there are no other writers nor readers
// Readers can read so long as there are no other writers,
// but there may be other readers

Semaphore resource =  new Semaphore(1);
Semaphore mutexR = new Semaphore(1);
Semaphore access = new Semaphore(1, true); // true makes it a strong semaphore so it uses queue
int r = 0;

Thread.start { // Writer
	access.acquire();
	resource.acquire();
	access.release();
	// write to resource
	resource.release();
}

Thread.start {
	access.acquire();
	mutexR.acquire();
	r++;
	if (r == 1) {
		resource.acquire();	
	}
	mutexR.release();
	access.release();
	// read from resource
	mutexR.acquire();
	r--;
	if (r == 0) {
		resource.release();	
	}
	mutexR.release();
}