import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    // ==========================================
    // VEHICLE DATA SERIALIZATION
    // ==========================================
    public static List<Vehicle> loadVehicles(String filePath) {
        List<Vehicle> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|");
                String type = parts[0];
                String id = parts[1];
                String name = parts[2];
                String plateNumber = parts[3];
                double pricePerDay = Double.parseDouble(parts[4]);
                boolean isAvailable = Boolean.parseBoolean(parts[5]);

                Vehicle vehicle;
                if (type.equals("MOTOR")) {
                    vehicle = new Motorcycle(id, name, plateNumber, pricePerDay);
                } else {
                    String customType = parts[6];
                    vehicle = new Car(id, name, plateNumber, pricePerDay, customType);
                }
                vehicle.setAvailable(isAvailable);
                list.add(vehicle);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading vehicles: " + e.getMessage());
        }
        return list;
    }

    public static void saveVehicles(String filePath, List<Vehicle> vehicles) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Vehicle v : vehicles) {
                if (v instanceof Motorcycle) {
                    writer.write(String.format("MOTOR|%s|%s|%s|%.2f|%b\n",
                            v.getId(), v.getName(), v.getPlateNumber(), v.getPricePerDay(), v.isAvailable()));
                } else if (v instanceof Car) {
                    Car c = (Car) v;
                    writer.write(String.format("MOBIL|%s|%s|%s|%.2f|%b|%s\n",
                            c.getId(), c.getName(), c.getPlateNumber(), c.getPricePerDay(), c.isAvailable(), c.getCustomType()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving vehicles: " + e.getMessage());
        }
    }

    // ==========================================
    // PROMOTIONS DATA SERIALIZATION
    // ==========================================
    public static List<Promotion> loadPromotions(String filePath) {
        List<Promotion> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|");
                String type = parts[0];
                String code = parts[1];
                LocalDate startDate = LocalDate.parse(parts[2], DATE_FORMATTER);
                LocalDate endDate = LocalDate.parse(parts[3], DATE_FORMATTER);
                double percentage = Double.parseDouble(parts[4]);
                double maxDiscount = Double.parseDouble(parts[5]);
                double minPurchase = Double.parseDouble(parts[6]);

                if (type.equals("DISCOUNT")) {
                    list.add(new PercentOffPromo(code, startDate, endDate, percentage, maxDiscount, minPurchase));
                } else if (type.equals("CASHBACK")) {
                    list.add(new CashbackPromo(code, startDate, endDate, percentage, maxDiscount, minPurchase));
                } else if (type.equals("FREESHIP")) {
                    list.add(new FreeShippingPromo(code, startDate, endDate, percentage, maxDiscount, minPurchase));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading promotions: " + e.getMessage());
        }
        return list;
    }

    public static void savePromotions(String filePath, List<Promotion> promotions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Promotion p : promotions) {
                writer.write(String.format("%s|%s|%s|%s|%.4f|%.2f|%.2f\n",
                        p.getPromoType(), p.getPromoCode(),
                        p.getStartDate().format(DATE_FORMATTER),
                        p.getEndDate().format(DATE_FORMATTER),
                        p.getPercentageDiscount(), p.getMaxDiscount(), p.getMinPurchase()));
            }
        } catch (IOException e) {
            System.err.println("Error saving promotions: " + e.getMessage());
        }
    }

    // ==========================================
    // CUSTOMER DATA SERIALIZATION
    // ==========================================
    public static List<Customer> loadCustomers(String filePath, List<Promotion> promotions) {
        List<Customer> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|");
                String type = parts[0];
                String id = parts[1];
                if (type.equals("GUEST")) {
                    double balance = Double.parseDouble(parts[2]);
                    list.add(new Guest(id, balance));
                } else if (type.equals("MEMBER")) {
                    String name = parts[2];
                    LocalDate regDate = LocalDate.parse(parts[3], DATE_FORMATTER);
                    double balance = Double.parseDouble(parts[4]);
                    String promoCode = parts.length > 5 ? parts[5] : "";

                    Member m = new Member(id, name, regDate, balance);
                    if (!promoCode.isEmpty() && !"null".equalsIgnoreCase(promoCode)) {
                        Promotion promo = promotions.stream().filter(p -> p.getPromoCode().equals(promoCode)).findFirst().orElse(null);
                        m.setAppliedPromo(promo);
                    }
                    list.add(m);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
        return list;
    }

    public static void saveCustomers(String filePath, List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Customer c : customers) {
                if (c instanceof Guest) {
                    writer.write(String.format("GUEST|%s|%.2f\n", c.getId(), c.getBalance()));
                } else if (c instanceof Member) {
                    Member m = (Member) c;
                    String promoCode = m.getAppliedPromo() != null ? m.getAppliedPromo().getPromoCode() : "null";
                    writer.write(String.format("MEMBER|%s|%s|%s|%.2f|%s\n",
                            m.getId(), m.getName(), m.getRegistrationDate().format(DATE_FORMATTER), m.getBalance(), promoCode));
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving customers: " + e.getMessage());
        }
    }

    // ==========================================
    // ORDERS DATA SERIALIZATION
    // ==========================================
    public static List<Order> loadOrders(String filePath, List<Customer> customers, List<Vehicle> vehicles, List<Promotion> promotions) {
        List<Order> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 5) continue;
                int orderNumber = Integer.parseInt(parts[0]);
                String customerId = parts[1];
                LocalDate orderDate = LocalDate.parse(parts[2], DATE_FORMATTER);
                String promoCode = parts[3];
                String itemsStr = parts.length > 5 ? parts[5] : "";

                Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);
                if (customer == null) continue;

                Promotion promo = null;
                if (!promoCode.isEmpty() && !"null".equalsIgnoreCase(promoCode)) {
                    promo = promotions.stream().filter(p -> p.getPromoCode().equals(promoCode)).findFirst().orElse(null);
                }

                List<CartItem> items = new ArrayList<>();
                if (!itemsStr.isEmpty() && !"none".equalsIgnoreCase(itemsStr)) {
                    String[] itemTokens = itemsStr.split(";");
                    for (String token : itemTokens) {
                        String[] itemParts = token.split(":");
                        if (itemParts.length < 3) continue;
                        String vehicleId = itemParts[0];
                        int quantity = Integer.parseInt(itemParts[1]);
                        LocalDate startDate = LocalDate.parse(itemParts[2], DATE_FORMATTER);

                        Vehicle vehicle = vehicles.stream().filter(v -> v.getId().equals(vehicleId)).findFirst().orElse(null);
                        if (vehicle != null) {
                            items.add(new CartItem(vehicle, quantity, startDate));
                        }
                    }
                }

                Order order = new Order(orderNumber, customer, items, orderDate, promo);
                list.add(order);

                // Re-associate this order directly to the customer's history
                customer.getOrderHistory().add(order);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading orders: " + e.getMessage());
        }
        return list;
    }

    public static void saveOrders(String filePath, List<Order> orders) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Order o : orders) {
                StringBuilder itemsBuilder = new StringBuilder();
                List<CartItem> items = o.getItems();
                for (int i = 0; i < items.size(); i++) {
                    CartItem item = items.get(i);
                    itemsBuilder.append(item.getVehicle().getId())
                            .append(":")
                            .append(item.getQuantity())
                            .append(":")
                            .append(item.getStartDate().format(DATE_FORMATTER));
                    if (i < items.size() - 1) {
                        itemsBuilder.append(";");
                    }
                }
                String itemsStr = itemsBuilder.length() > 0 ? itemsBuilder.toString() : "none";
                String promoCode = o.getPromo() != null ? o.getPromo().getPromoCode() : "null";
                writer.write(String.format("%d|%s|%s|%s|%s|%s\n",
                        o.getOrderNumber(),
                        o.getCustomer().getId(),
                        o.getOrderDate().format(DATE_FORMATTER),
                        promoCode,
                        o.getStatus().name(),
                        itemsStr));
            }
        } catch (IOException e) {
            System.err.println("Error saving orders: " + e.getMessage());
        }
    }
}
