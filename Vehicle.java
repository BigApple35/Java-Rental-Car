import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Vehicle {
    private String vehicleId;
    private String brand;
    private String model;
    private int year;
    private double basePricePerHour;
    private boolean isAvailable;
    private List<RentalTransaction> activeBookings;

    public Vehicle(String vehicleId, String brand, String model, int year, double basePricePerHour) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.basePricePerHour = basePricePerHour;
        this.isAvailable = true;
        this.activeBookings = new ArrayList<>();
    }

    public abstract double calculateRentalCost(int hours);

    public abstract String getVehicleType();

    public void displayInfo() {
        System.out.printf("[%s] %s %s (%d)%n", vehicleId, brand, model, year);
        System.out.printf("Price/hr        : Rp %,.2f%n", basePricePerHour);
        System.out.printf("Available       : %s%n", isAvailable ? "Yes" : "No");
    }

    public void toggleAvailability() {
        this.isAvailable = !this.isAvailable;
    }

    public boolean isAvailable(LocalDateTime start, int hours) {
        if (!this.isAvailable) return false;
        for (RentalTransaction tx : activeBookings) {
            if (tx.overlapsWith(start, hours)) {
                return false;
            }
        }
        return true;
    }

    public void addBooking(RentalTransaction tx) {
        activeBookings.add(tx);
    }
    
    public List<RentalTransaction> getActiveBookings() {
        return activeBookings;
    }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public double getBasePricePerHour() { return basePricePerHour; }
    public void setBasePricePerHour(double basePricePerHour) { this.basePricePerHour = basePricePerHour; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}
