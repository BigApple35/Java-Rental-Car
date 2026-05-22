public class PromoBundle {
    private String bundleCode;
    private String bundleName;
    private int fixedDurationHours;
    private boolean includesDriver;
    private double discountRate;
    private Class<? extends Vehicle> vehicleType;

    public PromoBundle(String bundleCode, String bundleName, int fixedDurationHours, boolean includesDriver, double discountRate, Class<? extends Vehicle> vehicleType) {
        this.bundleCode = bundleCode;
        this.bundleName = bundleName;
        this.fixedDurationHours = fixedDurationHours;
        this.includesDriver = includesDriver;
        this.discountRate = discountRate;
        this.vehicleType = vehicleType;
    }

    public double calculateDiscountedPrice(Vehicle vehicle) {
        return vehicle.getPricePerDay() * fixedDurationHours * (1.0 - discountRate);
    }

    public void applyConfiguration(RentalTransaction tx, Vehicle vehicle) {
        tx.setDurationHours(fixedDurationHours);
    }

    public Class<? extends Vehicle> getCompatibleVehicleType() {
        return vehicleType;
    }

    public String getBundleCode() { return bundleCode; }
    public void setBundleCode(String bundleCode) { this.bundleCode = bundleCode; }
    public String getBundleName() { return bundleName; }
    public void setBundleName(String bundleName) { this.bundleName = bundleName; }
    public int getFixedDurationHours() { return fixedDurationHours; }
    public void setFixedDurationHours(int fixedDurationHours) { this.fixedDurationHours = fixedDurationHours; }
    public boolean isIncludesDriver() { return includesDriver; }
    public void setIncludesDriver(boolean includesDriver) { this.includesDriver = includesDriver; }
    public double getDiscountRate() { return discountRate; }
    public void setDiscountRate(double discountRate) { this.discountRate = discountRate; }
    public void setVehicleType(Class<? extends Vehicle> vehicleType) { this.vehicleType = vehicleType; }
}
