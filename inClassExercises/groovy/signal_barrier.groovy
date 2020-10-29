monitor SignalBarrier {
	// Data fields
	private boolean door1 = false;
	private boolean door2 = true;
	private Condition room1, room2;
	private int c = 0;

	public pass() {
		while(door1) {room1.wait();}
		c++;
		while(door2) {room2.wait();}
		c--;

		// TODO
		if (c == 0) {
			room1.signalAll();
			door1 = false;
			door2 = true;
		}

	}

	public electricCurrent() {
		room2.signalAll();
		door1 = true;
		door2 = false;
	}
}