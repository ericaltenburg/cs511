
// 1 = south, 0 = north
// Declare appropriate semaphores:
Semaphore permToLoad = new Semaphore(0);
Semaphore doneLoading = new Semaphore(0);

tracks = [new Semaphore(1), new Semaphore(1)];

Thread.start { // pasenger
	Random rnd = new Random();
	int dir = rnd.nextInt(1);
	tracks[dir].acquire();
	// load unload passengers
	tracks[dir].release();
}

Thread.start { // freight
	Random rnd = new Random();
	int dir = rnd.nextInt(1);

	tracks[0].acquire();
	tracks[1].acquire();
	permToLoad.release();
	// waiting for loading to complete

	doneLoading.acquire();
	tracks[0].release();
	tracks[1].release();
}

Thread.start { // loading machine
	permToLoad.acquire();
	// loading
	doneLoading.release();
}