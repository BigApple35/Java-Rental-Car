import java.time.LocalDate;
import java.util.List;

public class Order {
    private int orderNumber;
    private Customer customer;
    private List<CartItem> items;
    private LocalDate orderDate;
    private Promotion promo;
    private OrderStatus status;

    public Order(int orderNumber, Customer customer, List<CartItem> items, LocalDate orderDate, Promotion promo) {
        this.orderNumber = orderNumber;
        this.customer = customer;
        this.items = items;
        this.orderDate = orderDate;
        this.promo = promo;
        this.status = OrderStatus.SUCCESSFUL;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public Promotion getPromo() {
        return promo;
    }

    public double getSubtotalPrice() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public double getTotalPrice() {
        double subtotal = getSubtotalPrice();
        if (promo instanceof PercentOffPromo) {
            return subtotal - ((PercentOffPromo) promo).getTotalPriceOff(this);
        }
        return subtotal;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
