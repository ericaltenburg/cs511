import java.util.concurrent.Semaphore;

float static int N = 10;
Semaphore[]  permToLeave = [new Semaphore(0), new Semaphore(0)];
Semaphore permToGetOn = new Semaphore(0);
Semaphore permToGetOff = new Semaphore(0);

Thread.start { // ferry 
	int coast = 0;
	while (true) {
		N.times {
			permToGetOn[coast].release();	
		}
		
		N.times {
			permToLeave.acquire();
		}
		
		// in transit to other coast
		coast = 1 - coast;

		N.times {
			permToGetOff.release();
		}

		N.times {
			permToLeave.acquire();			
		}


	}
}

100.times {
	Thread.start { // passengers
		Random rnd = new Random();
		int coast = rnd.nextInt(2); // 0 or 1


		permToGetOn[coast].acquire();
		permToLeave.release();
		// passenger is on ferry and in transit
		permToGetOff.acquire();
		permToLeave.release();
	}
}
