public class Motorcycle extends Vehicle {
    private int engineCC;
    private boolean hasTopBox;
    private String category;

    public Motorcycle(String vehicleId, String brand, String model, int year, double basePricePerHour, int engineCC, boolean hasTopBox, String category) {
        super(vehicleId, brand, model, year, basePricePerHour);
        this.engineCC = engineCC;
        this.hasTopBox = hasTopBox;
        this.category = category;
    }

    @Override
    public double calculateRentalCost(int hours) {
        return getBasePricePerHour() * hours * (engineCC > 250 ? 1.5 : 1.0);
    }

    @Override
    public String getVehicleType() {
        return "Motorcycle";
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.printf("Engine          : %dcc%n", engineCC);
        System.out.printf("Top Box         : %s%n", hasTopBox ? "Yes" : "No");
        System.out.printf("Category        : %s%n%n", category);
    }

    public boolean isSportBike() {
        return "Sport".equalsIgnoreCase(category);
    }

    public int getEngineCC() { return engineCC; }
    public void setEngineCC(int engineCC) { this.engineCC = engineCC; }

    public boolean isHasTopBox() { return hasTopBox; }
    public void setHasTopBox(boolean hasTopBox) { this.hasTopBox = hasTopBox; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
