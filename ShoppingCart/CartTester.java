import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CartTester {
    
    /**
     * Test: When created, the cart has 0 items
     */
    @Test
    public void testNewCartIsEmpty() {
        // Crea un nuovo carrello
        ShoppingCart cart = new ShoppingCart();
        
        // Verifica che abbia 0 elementi
        assertEquals(0, cart.getItemCount(), "A newly created cart should have 0 items.");
    }

    /**
     * Test: When the carti gets emptied, it should have 0 items
     */
    @Test
    public void testEmptyCart() {
        ShoppingCart cart = new ShoppingCart();
        
        cart.empty(); 
        assertEquals(0, cart.getItemCount(), "Emptying the cart should result in 0 items.");
    }

    /**
     * Test: When an item is added, the number of items should incremenet by 1.
     */
    @Test
    public void testAddItem() {
        ShoppingCart cart = new ShoppingCart();
        Product prod = new Product("Panino", 5);
        int itemCount = cart.getItemCount();
        cart.addItem(prod);
        assertEquals(itemCount+1, cart.getItemCount(), "The number of items should be increased by 1.");
        
    }
    /**
     * Test: When a new product is addes the balance should be the sum
     * of the previous balance plus the new item's price.
     */
    @Test
    public void testBalance() {
        ShoppingCart cart = new ShoppingCart();
        Product prod1 = new Product("Panino", 5);
        Product prod2 = new Product("Pizza", 6);

        cart.addItem(prod1);
        double balance = cart.getBalance();
        cart.addItem(prod2);
        assertEquals(balance+prod2.getPrice(),cart.getBalance(), "The balance should increase and match.");
    }

    /**
     * Test: When an item is removed the number of iterm should decrease
     */

    @Test
    public void testRemovingItem() throws ProductNotFoundException {
        ShoppingCart cart = new ShoppingCart();
        Product prod = new Product("Panino", 5);
        cart.addItem(prod); 
        int itemCount = cart.getItemCount();
        cart.removeItem(prod);
        assertEquals(itemCount-1, cart.getItemCount(), "The number of items should be decresed by 1.");
    }

    /** 
     * Test: When a product not in the cart is removed a ProductNotFound exception must be thrown.
     */

    @Test
    public void testRemovingNotFound() {
        ShoppingCart cart = new ShoppingCart();
        Product prod = new Product("Panino", 5);
        try {
		    cart.removeItem(prod);
		    fail("Expected ProductNotFoundException to be thrown");
	    } catch (ProductNotFoundException e) {
            // Test passed - exception was thrown as expected
        }
    }

}
