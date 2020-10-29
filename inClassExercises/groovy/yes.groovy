Semaphore gasUp = new Semaphore(6, true);
Semaphore truckRefill = new Semaphore(2);
Semaphore mutex = new Semaphore(1);
int truckAtStation = 0;

Thread.start { // Vehicle
	gasUp.acquire();
	// gas up
	gasUp.release();
}

Thread.start { // Truck 
	truckRefill.acquire();
	mutex.acquire();
	if (truckAtStation == 0) {
		6.times {
			gasUp.acquire();
		}
	}
	
	
	truckAtStation ++;
	mutex.release();

	// refill

	mutex.acquire();
	truckAtStation--;
	


	if (truckAtStation == 0) {
		6.times {
			gasUp.release();
		}
	}
	mutex.release();
	truckRefill.release();
}