monitor DryCleaners {
	private dirtyItemCount = 0;
	private cleanItemCount = 0;
	private Condition okToDryClean;
	private Condition okToPickUp;

	void dropOffOverall() { // called by an employee
		dirtyItemCount++;
		okToDryClean.signal();
	}

	void dryCleanItem() { // Called by a dry cleaning machine. Should block if there is nothing to dryclean
		while (dirtyItemCount == 0) {
			okToDryClean.wait();
		}
		// Cleans the overall
		dirtyItemCount--;
		cleanItemCount++;
		okToPickUp.signal();
	}

	void pickUpOverall () { // Called by an employee. Should block if there are no clean overalls.
		while(cleanItemCount == 0) {
			okToPickUp.wait();
		}
		cleanItemCount--;
	}
}