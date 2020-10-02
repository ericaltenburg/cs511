import java.util.concurrent.Semaphore;

Semaphore permToI = new Semaphore(0);
Semaphore permToO = new Semaphore(0);
Semaphore permToOK = new Semaphore(0);

Thread.start {
	print("R");
	permToI.release();
	permToOK.acquire();
	print("OK");
}

Thread.start {
	permToI.acquire();
	print("I");
	permToO.release();
	permToOK.acquire();
	print("OK");
}

Thread.start {
	permToO.acquire();
	print("O");
	permToOK.release();
	permToOK.release();
	print("OK");
}


// R I O OK OK OK
return;