import java.util.concurrent.Semaphore;
// stations to be modeled with semaphores
Semaphore station0 = new Semaphore(1);
Semaphore permTo0 = new Semaphore(0);
Semaphore permTo1 = new Semaphore(0);
Semaphore permTo2 = new Semaphore(0);

2.times {
	int car_id = it; // store value of iteration variable into local variable

	Thread.start { // car
		// move through all 3 stations
		println("Car " + car_id + " is waiting to get washed");
		
		station0.acquire();
		permToStartWorking0.release();

		println("Car " + car_id + " is getting blasted");
		permToMoveToStation1.acquire();
		
		station1.acquire();
		station0.release();
		
		permToStartWorking1.release();
	
		println("Car " + car_id + " is getting rinsed");
		permToMoveToStation2.acquire();
		
		station2.acquire();
		station1.release();
		
		permToStartWorking2.release();


		println("Car " + car_id + " is getting dried");
		permToLeave.acquire();
		station2.release();

		println("Car " + car_id + " has been washed")
	}
}

Thread.start { // M0
	permTo0.acquire();
	print(" station0 ");
	permTo1.release();
}

Thread.start { // M1
	
	permTo1.acquire();
	print(" station1 ");
	permTo2.release();
}

Thread.start { // M2
	permTo2.acquire();
	print(" station2 ");
	permToLeave.release();
}

