import java.time.LocalDateTime;

public class RentalTransaction {
    private String transactionId;
    private Customer customer;
    private Vehicle vehicle;
    private Driver driver;
    private LocalDateTime startDate;
    private LocalDateTime rentalDate;
    private int durationHours;
    private double baseCost;
    private double finalCost;
    private boolean isConfirmed;
    private PromoBundle appliedBundle;

    public RentalTransaction(String transactionId, Customer customer, Vehicle vehicle, LocalDateTime startDate, int durationHours) {
        this.transactionId = transactionId;
        this.customer = customer;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.rentalDate = LocalDateTime.now();
        this.durationHours = durationHours;
        this.baseCost = vehicle.calculateRentalCost(durationHours);
        this.finalCost = this.baseCost;
        this.isConfirmed = false;
    }

    public void applyBundle(PromoBundle bundle, Vehicle vehicle) {
        this.appliedBundle = bundle;
        bundle.applyConfiguration(this, vehicle);
        this.baseCost = vehicle.calculateRentalCost(this.durationHours);
        this.finalCost = bundle.calculateDiscountedPrice(vehicle);
        if (bundle.isIncludesDriver() && this.driver == null) {
            assignDriver(new Driver("DBUNDLE", "Bundle Driver", "BND001"));
        }
    }

    public void assignDriver(Driver driver) {
        this.driver = driver;
        this.finalCost += Driver.getFixedFee();
    }

    public LocalDateTime getEndDate() {
        return startDate.plusHours(durationHours);
    }

    public boolean overlapsWith(LocalDateTime checkStart, int checkHours) {
        LocalDateTime checkEnd = checkStart.plusHours(checkHours);
        LocalDateTime thisEnd = this.getEndDate();
        return startDate.isBefore(checkEnd) && checkStart.isBefore(thisEnd);
    }

    public void confirmOrder() {
        if (!isConfirmed && vehicle.isAvailable()) {
            vehicle.toggleAvailability();
            this.isConfirmed = true;
        }
    }

    public void displayReceipt() {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.println("\n================ RECEIPT ================");
        System.out.println("Transaction ID : " + transactionId);
        System.out.println("Customer       : " + customer.getFullName());
        System.out.println("Vehicle        : " + vehicle.getBrand() + " " + vehicle.getModel());
        System.out.println("Booking created: " + rentalDate.format(formatter));
        System.out.println("Rental period  : " + startDate.format(formatter) + " to " + getEndDate().format(formatter));
        System.out.println("Duration       : " + durationHours + " hours");
        if (driver != null) {
            System.out.println("Driver         : " + driver.getName() + " (Fee: " + String.format("%.2f", Driver.getFixedFee()) + ")");
        }
        if (appliedBundle != null) {
            System.out.println("Bundle Applied : " + appliedBundle.getBundleName() + " (" + String.format("%.0f%%", appliedBundle.getDiscountRate() * 100) + " off)");
        }
        System.out.println("Base Cost      : " + String.format("%.2f", baseCost));
        System.out.println("-----------------------------------------");
        System.out.println("FINAL TOTAL    : " + String.format("Rp %,.2f", finalCost));
        System.out.println("=========================================\n");
    }

    public void extendRental(int additionalHours) {
        this.durationHours += additionalHours;
        if (appliedBundle == null) {
            this.finalCost += vehicle.calculateRentalCost(additionalHours);
        }
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public Driver getDriver() { return driver; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getRentalDate() { return rentalDate; }
    public void setRentalDate(LocalDateTime rentalDate) { this.rentalDate = rentalDate; }

    public int getDurationHours() { return durationHours; }
    public void setDurationHours(int durationHours) { this.durationHours = durationHours; }

    public double getBaseCost() { return baseCost; }
    public void setBaseCost(double baseCost) { this.baseCost = baseCost; }

    public double getFinalCost() { return finalCost; }
    public void setFinalCost(double finalCost) { this.finalCost = finalCost; }

    public boolean isConfirmed() { return isConfirmed; }
    public void setConfirmed(boolean confirmed) { this.isConfirmed = confirmed; }

    public PromoBundle getAppliedBundle() { return appliedBundle; }
    public void setAppliedBundle(PromoBundle appliedBundle) { this.appliedBundle = appliedBundle; }
}
