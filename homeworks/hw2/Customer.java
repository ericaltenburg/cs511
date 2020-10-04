/** 
 * Author: Eric Altenburg and Sarvani Patel
 * Date: 10/4/20
 * Pledge: I pledge my honor that I have abided by the Stevens Honor System.
 **/
  
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.text.DecimalFormat;

public class Customer implements Runnable {
    private Bakery bakery;
    private Random rnd;
    private List<BreadType> shoppingCart;
    private int shopTime;
    private int checkoutTime;
    private DecimalFormat df;

    /**
     * Initialize a customer object and randomize its shopping cart
     */
    public Customer(Bakery bakery) {
        // TODO
        this.rnd = new Random();
        this.shoppingCart = new ArrayList<BreadType>();
        this.shopTime = (int)(Math.random() * (100 - 50 + 1) + 50);
        this.checkoutTime = (int)(Math.random() * (100 - 50 + 1) + 50);
        this.bakery = bakery;
        this.df = new DecimalFormat("#.00");
        fillShoppingCart();
    }

    /**
     * Run tasks for the customer
     */
    public void run() {
        // TODO
        try {
        	System.out.println("Customer: " + hashCode() + " decided to say, \"lets get this bread\"");
			for (BreadType b : shoppingCart) {
				int n;
	            if(b == BreadType.RYE){ 
	                n = 0;
	            } else if(b == BreadType.SOURDOUGH){ 
	                n = 1;
	            } else { 
	                n = 2;
	            }
	            bakery.shelf[n].acquire();
	            bakery.takeBread(b);
	            System.out.println("Customer " + hashCode() + ": taking " + b);
	            Thread.sleep(shopTime);
	            bakery.shelf[n].release();
			}
			
			

            bakery.saleProcess.acquire();
            System.out.println("Customer " + hashCode() + ": amount to buy $" + df.format(getItemsValue()));
            bakery.addSales(getItemsValue());
            Thread.sleep(checkoutTime);
            bakery.saleProcess.release();

            System.out.println(toString());
            bakery.permToPrint.release();
        } catch (InterruptedException ie) {
        	System.out.println(ie);
        }
    }

    /**
     * Return a string representation of the customer
     */
    public String toString() {
        return "Customer " + hashCode() + ": shoppingCart=" + Arrays.toString(shoppingCart.toArray()) + ", shopTime=" + shopTime + ", checkoutTime=" + checkoutTime;
    }

    /**
     * Add a bread item to the customer's shopping cart
     */
    private boolean addItem(BreadType bread) {
        // do not allow more than 3 items, chooseItems() does not call more than 3 times
        if (shoppingCart.size() >= 3) {
            return false;
        }
        shoppingCart.add(bread);
        return true;
    }

    /**
     * Fill the customer's shopping cart with 1 to 3 random breads
     */
    private void fillShoppingCart() {
        int itemCnt = 1 + rnd.nextInt(3);
        while (itemCnt > 0) {
            addItem(BreadType.values()[rnd.nextInt(BreadType.values().length)]);
            itemCnt--;
        }
    }

    /**
     * Calculate the total value of the items in the customer's shopping cart
     */
    private float getItemsValue() {
        float value = 0;
        for (BreadType bread : shoppingCart) {
            value += bread.getPrice();
        }
        return value;
    }
}