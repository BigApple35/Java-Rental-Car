public class Car extends Vehicle {
    private String customType;

    public Car(String id, String name, String plateNumber, double pricePerDay, String customType) {
        super(id, name, plateNumber, pricePerDay);
        this.customType = customType;
    }

    @Override
    public String getVehicleType() {
        return "MOBIL";
    }

    public String getCustomType() {
        return customType;
    }

    public double calculateRentalCost(int days) {
        return getPricePerDay() * days;
    }
}
