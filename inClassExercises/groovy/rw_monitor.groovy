// can have multiple readers
monitor RW {	
	// Data fields
	int readers, writers = 0;
	int writersWaiting = 0;
	Condition okToRead, okToWrite;

	startRead() {
		while (writers != 0 || writersWaiting != 0) {
			// readerWaiting++; // leads to deadlock
			okToRead.wait();
			// readerWaiting--; // leads to deadlock
		}
		readers++;
		okToRead.signal(); // chain the rest of the readers waiting to read
	}

	stopRead() {
		readers--;
		if (reader == 0) { // not really necessary
			okToWrite.signal();
		}
	}

	startWrite() {
		// while (readers != 0 || writers != 0 || readerWaiting != 0) { // this doesn't work leads to deadlock, R1, W1, R2, finish R1 and deadlock
		while (readers != 0 || writers != 0) {
			writersWaiting++;
			okToWrite.wait();
			writersWaiting--;
		}
		writers=1;
	}

	stopWrite() {
		writer = 0;
		okToRead.signal(); // can  use signalAll() instead of chaining
		okToWrite.signal();
	}
}