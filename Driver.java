public class Driver {
    private String driverId;
    private String name;
    private String licenseNumber;
    public static final double FIXED_FEE = 150000;

    public Driver(String driverId, String name, String licenseNumber) {
        this.driverId = driverId;
        this.name = name;
        this.licenseNumber = licenseNumber;
    }

    public static double getFixedFee() {
        return FIXED_FEE;
    }

    public boolean validateLicense() {
        return licenseNumber != null && java.util.regex.Pattern.matches("^[A-Z]{1,2}\\d{4,6}[A-Z]{0,2}$", licenseNumber);
    }

    public void displayInfo() {
        System.out.println("Driver ID: " + driverId + ", Name: " + name + ", License: " + licenseNumber);
    }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
}
