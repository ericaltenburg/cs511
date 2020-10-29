monitor PC { 
	// Data fields
	private Object buffer; // size 1;
	private Condition full, empty;

	void produce(Object o) {
		while (is_full()) {
			empty.wait();
		}
		buffer = o;
		full.signal();
	}

	Object consume() {
		while(is_empty()) {
			full.wait();
		}
		empty.signal();
		return buffer;
	}
}