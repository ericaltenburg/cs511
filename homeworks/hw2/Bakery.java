/** 
 * Author: Eric Altenburg and Sarvani Patel
 * Date: 10/4/20
 * Pledge: I pledge my honor that I have abided by the Stevens Honor System.
 **/

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;

public class Bakery implements Runnable {
    private static final int TOTAL_CUSTOMERS = 200;
    private static final int ALLOWED_CUSTOMERS = 50;
    private static final int FULL_BREAD = 20;
    private Map<BreadType, Integer> availableBread;
    private ExecutorService executor;
    private float sales = 0;
    private DecimalFormat dff = new DecimalFormat("0.##");

    // TODO
    public Semaphore[] shelf = new Semaphore[] {new Semaphore(1), new Semaphore(1), new Semaphore(1)}; // index 0: takeRye, index 1: takeSourDough, index 2: takeWonder 
    public Semaphore saleProcess = new Semaphore(4);
    public Semaphore registerProcess = new Semaphore(1);
    public Semaphore permToPrint = new Semaphore(0); // 

    /**
     * Remove a loaf from the available breads and restock if necessary
     */
    public void takeBread(BreadType bread) {
        int breadLeft = availableBread.get(bread);
        if (breadLeft > 0) {
            availableBread.put(bread, breadLeft - 1);
        } else {
            System.out.println("No " + bread.toString() + " bread left! Restocking...");
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
            registerProcess.acquire();
            sales += value;
        	registerProcess.release();
        } catch(InterruptedException ie){ 
            System.out.println(ie);
        }
        
    }


    /**
     * Run all customers in a fixed thread pool
     */
    public void run() {
        availableBread = new ConcurrentHashMap<BreadType, Integer>();
        availableBread.put(BreadType.RYE, FULL_BREAD);
        availableBread.put(BreadType.SOURDOUGH, FULL_BREAD);
        availableBread.put(BreadType.WONDER, FULL_BREAD);

        executor = Executors.newFixedThreadPool(ALLOWED_CUSTOMERS);
    	

        // TODO
        try {
	        for(int i = 0; i < TOTAL_CUSTOMERS; i++){
        		Customer c = new Customer(this);
            	executor.execute(c);
	        }
        	executor.shutdown();
        	for(int i = 0; i < TOTAL_CUSTOMERS; i++) { // DON'T MIND THIS JANK SOLUTION TO PRINTING
				permToPrint.acquire();
			}
       		System.out.print("TOTAL SALES: $" + dff.format(sales));
        } catch (InterruptedException ie) {
        	System.out.println(ie);
        }
      
    }
}