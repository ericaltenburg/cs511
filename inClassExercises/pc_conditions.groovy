class PC {
	// Data fields
	private Object buffer;
	private Condition full;
	private Condition empty;

	synchronized void produce (Object o) {
		while (is_full_buffer()) { // if this is an if instead, then if T2 writes to buffer, and T1 awakes, it will overwrite buffer
			empty.wait();
		}

		buffer = o;
		full.signal();
	}

	synchronized Object consume() {
		while  (is_empty_buffer()) {
			fill.wait();
		}
		// consuming
		empty.signal();
		return buffer;
	}
}