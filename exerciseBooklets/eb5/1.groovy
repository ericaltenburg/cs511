import java.util.concurrent.Semaphore;

Semaphore permToJet = new Semaphore(0)



Thread.start { // Jets
	14.times {
	permToJet.acquire();
	permToJet.acquire();
	print("Jets ");
}
}

Thread.start { // Patriots
	10.times {


	print("Patriots ");
	permToJet.release();
}
}