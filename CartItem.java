import java.time.LocalDate;

public class CartItem {
    private Vehicle vehicle;
    private int quantity;
    private LocalDate startDate;

    public CartItem(Vehicle vehicle, int quantity, LocalDate startDate) {
        this.vehicle = vehicle;
        this.quantity = quantity;
        this.startDate = startDate;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return startDate.plusDays(quantity - 1);
    }

    public double getSubtotal() {
        return vehicle.getPricePerDay() * quantity;
    }
}
