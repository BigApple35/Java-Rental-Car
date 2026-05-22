import java.util.List;
import java.util.ArrayList;

public abstract class Customer {
    private String id;
    protected String name;
    private double balance;
    private List<Order> orderHistory;
    private List<CartItem> currentCart;
    private Promotion appliedPromo;

    public Customer(String id, String name, double initialBalance) {
        this.id = id;
        this.name = name;
        this.balance = initialBalance;
        this.orderHistory = new ArrayList<>();
        this.currentCart = new ArrayList<>();
        this.appliedPromo = null;
    }

    public abstract String getName();

    public double getBalance() {
        return balance;
    }

    public void addBalance(double amount) {
        this.balance += amount;
    }

    public void deductBalance(double amount) {
        this.balance -= amount;
    }

    public String getId() {
        return id;
    }

    public List<Order> getOrderHistory() {
        return orderHistory;
    }

    public List<CartItem> getCurrentCart() {
        return currentCart;
    }

    public Promotion getAppliedPromo() {
        return appliedPromo;
    }

    public void setAppliedPromo(Promotion promo) {
        this.appliedPromo = promo;
    }
}
