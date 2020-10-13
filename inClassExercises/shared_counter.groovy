monitor Counter { // monitor key word, enforced mutex on all shared resources
	// Data fields
	private c = 0;
	// private Semaphore mutex = new Semaphore(1);

	void inc() { // for java, just prepend synchronized
		// mutex.acquire();
		c++;
		// mutex.release();
	}

	void dec() { // for java, just prepend synchronized
		// mutex.acquire();
		c--;
		// mutex.release();
	}
}