public abstract class Vehicle {
    private String id;
    private String name;
    private String plateNumber;
    private double pricePerDay;
    private boolean isAvailable;

    public Vehicle(String id, String name, String plateNumber, double pricePerDay) {
        this.id = id;
        this.name = name;
        this.plateNumber = plateNumber;
        this.pricePerDay = pricePerDay;
        this.isAvailable = true;
    }

    public abstract String getVehicleType();

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }
}
