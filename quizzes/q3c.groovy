// I pledge my honor that I have abided by the Stevens Honor System. - Eric Altenburg
import java.util.concurrent.Semaphore;

int N = 4;
permissionToGetOn = [new Semaphore(0), new Semaphore(0)];
Semaphore occupancy = new Semaphore(0);
Semaphore permissionToGetOff = new Semaphore(0);

Thread.start { // Elevator
	int floor = 0;
	while (true) {
		// getting on
		N.times {
			permissionToGetOn[floor].release();
		}

		N.times {
			occupancy.acquire();
		}

		// next floor
		floor = floor + 1 % 2;

		// getting off
		N.times {
			permissionToGetOff.release();
		}

		N.times {
			occupancy.acquire();
		}
	}
}

Thread.start { // worker
	Random rnd = new Random();

	int floor = rnd.nextInt(1);

	permissionToGetOn[floor].acquire();
	// getting on
	occupancy.release();

	// in transit

	permissionToGetOff.acquire();
	// getting off
	occupancy.release();
}

