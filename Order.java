import java.time.LocalDateTime;

public class Order {
    private static int orderCounter = 1;

    private int orderNumber;
    private LocalDateTime orderDate;
    private Vehicle vehicle;
    private int durationHours;
    private int quantity;
    private double subTotal;
    private double shippingFee;
    private double totalDiscount;
    private double totalPrice;
    private Promotion promotion;
    private OrderStatus status;

    public Order(Vehicle vehicle, int durationHours, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        this.orderNumber = orderCounter++;
        this.vehicle = vehicle;
        this.durationHours = durationHours;
        this.quantity = quantity;
        this.subTotal = vehicle.calculateRentalCost(durationHours) * quantity;
        this.shippingFee = 50000;
        this.totalDiscount = 0;
        this.status = OrderStatus.UNPAID;
    }

    public void checkOut() {
        this.orderDate = LocalDateTime.now();
        this.totalPrice = this.subTotal + this.shippingFee - this.totalDiscount;
    }

    public void printDetails() {
        if (orderDate == null) {
            System.out.println("Order not checked out yet.");
            return;
        }
        System.out.println("Order Date: " + orderDate);
        System.out.println("Order Number: " + orderNumber);
        System.out.println("Vehicle: " + vehicle.getBrand() + " " + vehicle.getModel());
        System.out.println("Quantity: " + quantity);
        System.out.println("Duration (Hours): " + durationHours);
        System.out.println("Sub Total: " + subTotal);
        System.out.println("Shipping Fee: " + shippingFee);
        System.out.println("Total Discount: " + totalDiscount);
        System.out.println("Total Price: " + totalPrice);
        System.out.println("Status: " + status);
        if (promotion != null) {
            System.out.println("Promotion: " + promotion.getPromoCode());
        }
    }

    public void applyPromo(Promotion promo) {
        this.promotion = promo;
        this.totalDiscount = promo.calculateDiscount(this) + promo.calculateCashback(this) + promo.calculateShippingDiscount(this);
        if (this.totalDiscount > this.subTotal + this.shippingFee) {
            throw new IllegalStateException("Discount exceeds total cost");
        }
    }

    public void pay() {
        this.status = OrderStatus.SUCCESSFUL;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
