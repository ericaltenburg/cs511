/* 
Author: Hamzah Nizami 
Description: CS511 HW2 
Pledge: I pledge my honor that I have abided by the Stevens Honor System.
*/
// import java.util.Arrays;
// import java.util.List;
// import java.util.Random;
  
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class Customer implements Runnable {
    private Bakery bakery;
    private Random rnd;
    private List<BreadType> shoppingCart;
    private int shopTime;
    private int checkoutTime;

    /**
     * Initialize a customer object and randomize its shopping cart
     */
    public Customer(Bakery bakery) {
        // TODO
        this.rnd = new Random();
        this.shoppingCart = new ArrayList<BreadType>();
        this.shopTime = (int)(Math.random() * 120 + (50));
        this.checkoutTime = (int)(Math.random() * 60 + (50));
        this.bakery = bakery;
    }

    public List<BreadType> getShoppingCart(){ 
        return this.shoppingCart;
    }

    /**
     * Run tasks for the customer
     */
    public void run() {
        // TODO
        try {
			fillShoppingCart();
			for (BreadType b : shoppingCart) {
				// int n;
	   //          if(b == BreadType.RYE){ 
	   //              n = 0;
	   //          } else if(b == BreadType.SOURDOUGH){ 
	   //              n = 1;
	   //          } else { 
	   //              n = 2;
	   //          }
	            // bakery.shelf[n].acquire();
	            bakery.shelf.get(b).acquire();
	            bakery.takeBread(b);
	            Thread.sleep(shopTime);
	            // bakery.shelf[n].release();
	            bakery.shelf.get(b).release();
			}

            bakery.saleProcess.acquire();
            bakery.registerProcess.acquire();
            bakery.addSales(this.getItemsValue());
            Thread.sleep(checkoutTime);
            bakery.saleProcess.release();
            bakery.registerProcess.release();

            System.out.println(this.toString());
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