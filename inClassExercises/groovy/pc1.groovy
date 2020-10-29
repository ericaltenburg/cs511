// Implement a semaphore using a monitor

monitor Semaphore {
	// Data fields
	private int permits;
	private Condition availablePermits;
	// TODO

	Semaphore(int init_permits) {
		permits = init_permits;
	}

	void acquire() {
		while (permits == 0) {
			availablePermits.wait(); // always put waits inside while loops
		}
		permits--;
	}

	void release() {
		permits++;
		availablePermits.signal(); // if you signal on condition that isn't waiting, then it does nothing
	}
}