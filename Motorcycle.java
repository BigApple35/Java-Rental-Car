public class Motorcycle extends Vehicle {

    public Motorcycle(String id, String name, String plateNumber, double pricePerDay) {
        super(id, name, plateNumber, pricePerDay);
    }

    @Override
    public String getVehicleType() {
        return "MOTOR";
    }

    public double calculateRentalCost(int days) {
        return getPricePerDay() * days;
    }
}
