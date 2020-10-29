monitor Barrier {
	// data fields
	final int N = 3;
	int c = 0;
	Condition okToPass;

	pass() {
		while (c != N) {
			c++;
			okToPass.wait();
		}
		okToPass.signalAll();

		// if (c < N) { // This also works apparently, I wonder if mine does too
		// 	c++;
		// 	while (c < N) {
		// 		okToPass.wait();
		// 	}
		// 	okToPass.signal();
		// }
	}
}