import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.List;

public class Bakery implements Runnable {
    private static final int TOTAL_CUSTOMERS = 200;
    private static final int ALLOWED_CUSTOMERS = 50;
    private static final int FULL_BREAD = 20;
    private Map<BreadType, Integer> availableBread;
    private ExecutorService executor;
    private float sales = 0;

    // TODO
    private Semaphore[] shelf = new Semaphore[] {new Semaphore(1), new Semaphore(1), new Semaphore(1)}; // index 0: takeRye, index 1: takeSourDough, index 2: takeWonder 
    private Semaphore saleProcess = new Semaphore(4);
    private Semaphore evaluateCustomer = new Semaphore(1);

    public void setSales(float s){ 
        this.sales = s;
    }
    /**
     * Remove a loaf from the available breads and restock if necessary
     */
    public void takeBread(BreadType bread) {
        int breadLeft = availableBread.get(bread);
        if (breadLeft > 0) {
            availableBread.put(bread, breadLeft - 1);
        } else {
            System.out.println("No " + bread.toString() + " bread left! Restocking...");
            System.out.println(breadLeft);
            // restock by preventing access to the bread stand for some time
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            availableBread.put(bread, FULL_BREAD - 1);
        }
    }

    /**
     * Add to the total sales
     */
    public void addSales(float value) {
        try{ 
            saleProcess.acquire();
        } catch(InterruptedException ie){ 
            System.out.println(ie);
        }
        sales += value;
        saleProcess.release();
    }


    /**
     * Run all customers in a fixed thread pool
     */
    public void run() {
        availableBread = new ConcurrentHashMap<BreadType, Integer>();
        availableBread.put(BreadType.RYE, FULL_BREAD);
        availableBread.put(BreadType.SOURDOUGH, FULL_BREAD);
        availableBread.put(BreadType.WONDER, FULL_BREAD);


        ArrayList<Customer> customerList = new ArrayList<Customer>();
        executor = Executors.newFixedThreadPool(50);
        // TODO
        for(int i = 0; i < 200; i++){
            try{ 
                evaluateCustomer.acquire();
            } catch(InterruptedException ie){ 
                System.out.println(ie);
            }
            Customer c = new Customer(this);
            List<BreadType> cart = c.getShoppingCart();

            for(BreadType b : cart){ 
                int n = 0;
                if(b == BreadType.RYE){ 
                    n = 0;
                } 
                if(b == BreadType.SOURDOUGH){ 
                    n = 1;
                }
                if(b == BreadType.WONDER){ 
                    n = 2;
                }
                try{ 
                    shelf[n].acquire();
                } catch (InterruptedException ie){ 
                    System.out.println(ie);
                }
                takeBread(b);
                shelf[n].release();
            }
            executor.execute(c);
            evaluateCustomer.release();
        }
       executor.shutdown();
    }
}