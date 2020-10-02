
/*
Quiz 2C - 25 Sep 2020

You may not add print statements nor additional semaphores.
Add ONLY acquire and release operations to the following code so that the output is:

aabcaabcaabc....
Eric Altenburg
I pledge my honor that I have abided by the Stevens Honor System
*/

import java.util.concurrent.Semaphore;
Semaphore a = new Semaphore(2);
Semaphore b = new Semaphore(0);
Semaphore c = new Semaphore(0);



Thread.start { // P
    while(true) {
	    a.acquire();
		print("a");
		b.release();
    }
}

Thread.start { // Q 
    while(true) {
	    b.acquire();
	    b.acquire();
		print("b");
		c.release();
    }
}


Thread.start { // R
    while(true) {
	    c.acquire();
		print("c");
		a.release();
		a.release();
    }
}
