import java.util.concurrent.Semaphore;

Semaphore s = new Semaphore(0);

Thread.start {
    s.acquire();
    println("a");
    println("b");
}

Thread.start {
    println("c");
    println("d");
    s.release();
}

return;