public class Car extends Vehicle {
    private int trunkCapacity;
    private String fuelType;
    private int seatCount;

    public Car(String vehicleId, String brand, String model, int year, double basePricePerHour, int trunkCapacity, String fuelType, int seatCount) {
        super(vehicleId, brand, model, year, basePricePerHour);
        this.trunkCapacity = trunkCapacity;
        this.fuelType = fuelType;
        this.seatCount = seatCount;
    }

    @Override
    public double calculateRentalCost(int hours) {
        return (getBasePricePerHour() * hours) + (seatCount > 5 ? hours * 10000 : 0);
    }

    @Override
    public String getVehicleType() {
        return "Car";
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.printf("Trunk Capacity  : %dL%n", trunkCapacity);
        System.out.printf("Fuel            : %s%n", fuelType);
        System.out.printf("Seats           : %d%n%n", seatCount);
    }

    public boolean hasLargeTrunk() {
        return trunkCapacity > 400;
    }

    public int getTrunkCapacity() { return trunkCapacity; }
    public void setTrunkCapacity(int trunkCapacity) { this.trunkCapacity = trunkCapacity; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public int getSeatCount() { return seatCount; }
    public void setSeatCount(int seatCount) { this.seatCount = seatCount; }
}
