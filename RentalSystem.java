import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class RentalSystem {
    private ArrayList<Vehicle> vehicleList;
    private ArrayList<PromoBundle> availableBundles;
    private ArrayList<RentalTransaction> transactions;
    private ArrayList<Customer> customerList;
    private ArrayList<Promotion> availablePromotions;
    private Scanner scanner;

    public RentalSystem() {
        vehicleList = new ArrayList<>();
        availableBundles = new ArrayList<>();
        transactions = new ArrayList<>();
        customerList = new ArrayList<>();
        availablePromotions = new ArrayList<>();
        scanner = new Scanner(System.in);
        initializeVehicles();
        initializeBundles();
        initializePromotions();
    }

    public void initializeVehicles() {
        vehicleList.add(new Car("C001", "Toyota", "Avanza", 2022, 50000, 500, "Petrol", 7));
        vehicleList.add(new Car("C002", "Honda", "Brio", 2023, 40000, 300, "Petrol", 5));
        vehicleList.add(new Motorcycle("M001", "Yamaha", "R25", 2021, 20000, 250, false, "Sport"));
        vehicleList.add(new Motorcycle("M002", "Honda", "Vario", 2022, 10000, 150, true, "Standard"));
    }

    public void initializeBundles() {
        availableBundles.add(new PromoBundle("BND1", "Weekend Car", 48, false, 0.1, Car.class));
        availableBundles.add(new PromoBundle("BND2", "Touring Moto", 24, false, 0.15, Motorcycle.class));
        availableBundles.add(new PromoBundle("BND3", "VIP Car Driver", 12, true, 0.05, Car.class));
    }

    public void initializePromotions() {
        availablePromotions.add(new PercentOffPromo("DISC10", LocalDate.now(), LocalDate.now().plusDays(30), 0.10));
        availablePromotions.add(new CashbackPromo("CASH50", LocalDate.now(), LocalDate.now().plusDays(30), 50000));
        availablePromotions.add(new FreeShippingPromo("FREESHIP", LocalDate.now(), LocalDate.now().plusDays(30)));
    }

    public void registerCustomer() {
        System.out.print("Register as (1) Guest or (2) Member: ");
        String typeChoice = scanner.nextLine();

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();

        String customerId = "CUST-" + (customerList.size() + 1);
        Customer customer;

        if (typeChoice.equals("2")) {
            customer = new Member(customerId, firstName, lastName, email, phone, LocalDate.now());
        } else {
            customer = new Guest(customerId, firstName, lastName, email, phone);
        }
        
        customerList.add(customer);
        System.out.println("Customer registered: " + customer.getFullName() + " (" + customerId + ")");
    }

    public Customer findCustomerById(String customerId) {
        for (Customer c : customerList) {
            if (c.getCustomerId().equalsIgnoreCase(customerId)) {
                return c;
            }
        }
        return null;
    }

    public void displayCustomers() {
        if (customerList.isEmpty()) {
            System.out.println("No customers registered.");
            return;
        }
        System.out.println("ID\tName\tType\tMember Since");
        for (Customer c : customerList) {
            String type = (c instanceof Member) ? "Member" : "Guest";
            if (c instanceof Member) {
                Member m = (Member) c;
                System.out.println(c.getCustomerId() + "\t" + c.getFullName() + "\t" + type + "\t" + m.getMemberSince() + ", " + m.getMembershipDuration() + " days");
            } else {
                System.out.println(c.getCustomerId() + "\t" + c.getFullName() + "\t" + type + "\t-");
            }
        }
    }

    public void processOrderFlow() {
        displayCustomers();
        System.out.print("Enter customer ID: ");
        String custId = scanner.nextLine();
        Customer customer = findCustomerById(custId);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.println("\n--- Available Vehicles ---");
        for (Vehicle v : vehicleList) {
            if (v.isAvailable()) {
                v.displayInfo();
            }
        }
        System.out.print("Enter vehicle ID: ");
        String vId = scanner.nextLine();
        Vehicle vehicle = null;
        for (Vehicle v : vehicleList) {
            if (v.getVehicleId().equalsIgnoreCase(vId) && v.isAvailable()) {
                vehicle = v;
                break;
            }
        }

        if (vehicle == null) {
            System.out.println("Invalid or unavailable vehicle.");
            return;
        }

        System.out.print("Enter duration (hours): ");
        int duration;
        try {
            duration = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid duration.");
            return;
        }

        Order order;
        try {
            order = customer.makeOrder(vehicle, duration);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Order created. Checking out...");
        order.checkOut();

        System.out.print("Apply a promo? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            PercentOffPromo p1 = new PercentOffPromo("PROMO10", LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), 0.10);
            CashbackPromo p2 = new CashbackPromo("CASH50K", LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), 50000);
            FreeShippingPromo p3 = new FreeShippingPromo("FREESHIP", LocalDate.now().minusDays(1), LocalDate.now().plusDays(30));

            System.out.println("[1] PROMO10 - 10% off");
            System.out.println("[2] CASH50K - Cashback 50k");
            System.out.println("[3] FREESHIP - Free shipping");
            System.out.print("Choose promo (1-3): ");
            int pChoice = -1;
            try {
                pChoice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
            }

            Promotion selectedPromo = null;
            if (pChoice == 1) selectedPromo = p1;
            else if (pChoice == 2) selectedPromo = p2;
            else if (pChoice == 3) selectedPromo = p3;

            if (selectedPromo != null) {
                if (!selectedPromo.isCustomerEligible(customer) || !selectedPromo.isMinimumPriceEligible(order)) {
                    System.out.println("Customer not eligible for this promo.");
                } else {
                    try {
                        order.applyPromo(selectedPromo);
                    } catch (IllegalStateException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }

        order.pay();
        order.printDetails();
    }

    public void displayAvailableBundles() {
        System.out.println("\n--- Available Promo Bundles ---");
        for (PromoBundle bundle : availableBundles) {
            System.out.printf("[%s] %s%n", bundle.getBundleCode(), bundle.getBundleName());
            System.out.printf("Duration        : %d HOURS%n", bundle.getFixedDurationHours());
            System.out.printf("Includes Driver : %s%n", bundle.isIncludesDriver() ? "Yes" : "No");
            System.out.printf("Discount        : %.0f%%%n%n", bundle.getDiscountRate() * 100);
        }
        System.out.println("-------------------------------");
    }

    public void displayCompatibleVehicles(PromoBundle bundle) {
        Class<? extends Vehicle> type = bundle.getCompatibleVehicleType();
        for (Vehicle v : vehicleList) {
            if (type.isInstance(v) && v.isAvailable()) {
                v.displayInfo();
            }
        }
    }

    public void processRentalWithBundle() {
        displayAvailableBundles();
        System.out.print("Enter bundle code: ");
        String code = scanner.nextLine();
        PromoBundle selectedBundle = null;
        for (PromoBundle b : availableBundles) {
            if (b.getBundleCode().equalsIgnoreCase(code)) {
                selectedBundle = b;
                break;
            }
        }

        if (selectedBundle == null) {
            System.out.println("Invalid bundle code.");
            return;
        }

        displayCompatibleVehicles(selectedBundle);
        System.out.print("Enter vehicle ID: ");
        String vId = scanner.nextLine();
        Vehicle selectedVehicle = null;
        for (Vehicle v : vehicleList) {
            if (v.getVehicleId().equalsIgnoreCase(vId) && v.isAvailable() && selectedBundle.getCompatibleVehicleType().isInstance(v)) {
                selectedVehicle = v;
                break;
            }
        }

        if (selectedVehicle == null) {
            System.out.println("Invalid or unavailable vehicle.");
            return;
        }

        Customer c = createCustomerInput();
        if (c == null) return;

        RentalTransaction tx = new RentalTransaction(UUID.randomUUID().toString(), c, selectedVehicle, LocalDateTime.now(), selectedBundle.getFixedDurationHours());
        tx.applyBundle(selectedBundle, selectedVehicle);
        
        tx.confirmOrder();
        transactions.add(tx);
        System.out.println("Rental confirmed.");
        tx.displayReceipt();
    }

    public void processRegularRental() {
        System.out.println("\n--- Available Vehicles ---");
        for (Vehicle v : vehicleList) {
            if (v.isAvailable()) {
                v.displayInfo();
            }
        }
        System.out.println("--------------------------");
        System.out.print("Enter vehicle ID: ");
        String vId = scanner.nextLine();
        Vehicle selectedVehicle = null;
        for (Vehicle v : vehicleList) {
            if (v.getVehicleId().equalsIgnoreCase(vId) && v.isAvailable()) {
                selectedVehicle = v;
                break;
            }
        }

        if (selectedVehicle == null) {
            System.out.println("Invalid or unavailable vehicle.");
            return;
        }

        System.out.print("Enter duration (hours): ");
        int duration = 0;
        try {
            duration = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid duration.");
            return;
        }

        Customer c = createCustomerInput();
        if (c == null) return;

        RentalTransaction tx = new RentalTransaction(UUID.randomUUID().toString(), c, selectedVehicle, LocalDateTime.now(), duration);
        
        System.out.print("Include driver? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            Driver d = new Driver(UUID.randomUUID().toString(), "Available Driver", "D987654V");
            tx.assignDriver(d);
        }

        tx.confirmOrder();
        transactions.add(tx);
        System.out.println("Rental confirmed.");
        tx.displayReceipt();
    }

    private Customer createCustomerInput() {
        System.out.println("1. Guest");
        System.out.println("2. Member");
        System.out.print("Choose customer type: ");
        String typeChoice = scanner.nextLine();

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        String customerId = UUID.randomUUID().toString();
        Customer c;

        if (typeChoice.equals("2")) {
            System.out.print("Enter join date (yyyy-MM-dd): ");
            String dateStr = scanner.nextLine();
            LocalDate joinDate;
            try {
                joinDate = LocalDate.parse(dateStr);
            } catch (Exception e) {
                System.out.println("Invalid date format.");
                return null;
            }
            c = new Member(customerId, firstName, lastName, email, phone, joinDate);
        } else {
            c = new Guest(customerId, firstName, lastName, email, phone);
        }
        
        customerList.add(c);
        return c;
    }

    public void showAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions.");
            return;
        }
        System.out.println("\n=====================================================================================================================");
        System.out.printf("%-36s | %-16s | %-20s | %-16s | %-6s | %s%n",
                "Transaction ID", "Customer", "Vehicle", "Start Date", "Hours", "Total Cost");
        System.out.println("---------------------------------------------------------------------------------------------------------------------");
        for (RentalTransaction tx : transactions) {
            String vehicleName = tx.getVehicle().getBrand() + " " + tx.getVehicle().getModel();
            String startDateStr = tx.getStartDate().toString().replace("T", " ");
            if (startDateStr.length() > 16) startDateStr = startDateStr.substring(0, 16);
            System.out.printf("%-36s | %-16s | %-20s | %-16s | %-6d | Rp %,.2f%n",
                    tx.getTransactionId(), tx.getCustomer().getFullName(), vehicleName, startDateStr, tx.getDurationHours(), tx.getFinalCost());
        }
        System.out.println("=====================================================================================================================\n");
    }

    public static void main(String[] args) {
        RentalSystem system = new RentalSystem();
        while (true) {
            System.out.println("\n1. Bundle Rental");
            System.out.println("2. Regular Rental");
            System.out.println("3. Show Transactions");
            System.out.println("4. Register Customer");
            System.out.println("5. Order Flow");
            System.out.println("6. View Customers");
            System.out.println("7. Show Transactions");
            System.out.println("8. Exit");
            System.out.print("Choice: ");
            String choice = system.scanner.nextLine();
            
            switch (choice) {
                case "1": system.processRentalWithBundle(); break;
                case "2": system.processRegularRental(); break;
                case "3": system.showAllTransactions(); break;
                case "4": system.registerCustomer(); break;
                case "5": system.processOrderFlow(); break;
                case "6": system.displayCustomers(); break;
                case "7": system.showAllTransactions(); break;
                case "8": return;
                default: System.out.println("Invalid choice");
            }
        }
    }
}
