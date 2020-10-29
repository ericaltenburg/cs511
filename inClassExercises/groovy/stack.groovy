monitor Stacks {
	Condition okToPop;
	Condition okToPush;
	Stack s;

	push(Object o) {
		while (s.isFull()) {
			okToPush().wait();
		}
		s.push(o);
		okToPop.signal();
	}

	Object pop() {
		while (s.isEmpty()) {
			okToPop.wait();
		}
		okToPush.signal();
		return s.pop();
	}
}