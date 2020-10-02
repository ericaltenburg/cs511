import java.util.concurrent.Semaphore;
import java.util.*;

// One prod one con, size n
final int N = 10;
Object [] buffer = new Object[N];
int start, end;


// Setting: One producer and one consumer
// Object buffer; // Buffer of size 1
Semaphore permToProduce = new Semaphore(N);
Semaphore permToConsume = new Semaphore(0);
Semaphore mutex = new Semaphore(1);
Random rnd = new Random();
Object produce() {
	return rnd.nextInt(100);
}

Object consume(Object o) {
	buffer = -1;
}

100.times { // 100 prod now
	Thread.start { // Producer
		while(true) {
			permToProduce.acquire();
			mutex.acquire();
			buffer[start] = produce(); // Print P
			start = (start + 1) %N;
			mutex.release();
			permToConsume.release();
		}
	}
}

100.times { // 100 con now
	Thread.start { // Consumer	
		while(true) {
			permToConsume.acquire();
			mutex.acquire();
			consume(buffer[end]); // Print C
			end = (end+1)%N;
			mutex.release();
			permToProduce.release();
		}
	}
}
