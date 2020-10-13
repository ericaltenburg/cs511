// Eric Altenburg completed alone
// I pledge my honor that I have abided by the Stevens Honor System. - Eric Altenburg

monitor Pizzeria {
	// Data fields
	private int small_pizzas = 0;
	private int large_pizzas = 0;
	private	Condition okToBuyLargePizza;
	private Condition okToBuySmallPizza;


	purchaseLargePizza() {
		while(large_pizzas == 0 && small_pizzas < 2) {
			okToBuyLargePizza.wait();
		}

		if (large_pizzas != 0) { // to be fair to the small pizzas
			large_pizzas--;
		} else if (large_pizzas == 0) {
			small_pizzas -= 2;
		}
	}

	purchaseSmallPizza() {
		while (small_pizzas == 0) {
			okToBuySmallPizza.wait();
		}
		small_pizzas--;
	}

	bakeSmallPizza() {
		small_pizzas++;
		okToBuySmallPizza.signal(); 
		if (small_pizzas > 1) { 
			okToBuyLargePizza.signal();
		}
	}

	bakeLargePizza() {
		large_pizzas++;
		okToBuyLargePizza.signal();
	}
}

// the baking operation should notblock you can always amke new pizzas