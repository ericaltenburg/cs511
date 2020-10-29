// Implement a semaphore using a monitor
monitor Semaphore {
	// Data fields
	private int permits;
	private Condition has_permits;

	// init_permits should be positive
	Sempahore (int int_permits) {
		// TODO
		this.permits = permits;
	}

	void acquire() {
		// TODO
		while(permits < 1) {
			has_permits.wait();
		}
		permits--;
		// run thread
	}

	void release() {
		// TODO
		permits++;
		has_permits.signal();
	}
}