import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RentalSystem {
    private List<Customer> customers;
    private List<Vehicle> vehicles;
    private List<Promotion> promotions;
    private List<Order> allOrders;
    private List<String> outputs;

    public RentalSystem() {
        customers = new ArrayList<>();
        vehicles = new ArrayList<>();
        promotions = new ArrayList<>();
        allOrders = new ArrayList<>();
        outputs = new ArrayList<>();
    }

    public static void main(String[] args) throws IOException {
        RentalSystem system = new RentalSystem();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) break;
            system.processCommand(line.trim(), reader);
        }
        for (String output : system.outputs) {
            System.out.println(output);
        }
    }

    private void processCommand(String line, BufferedReader reader) throws IOException {
        if (line.trim().isEmpty()) return;
        String[] parts = line.split(" ");
        String cmd = parts[0];

        if (cmd.equals("CREATE")) {
            String subCmd = parts[1];
            if (subCmd.equals("MEMBER")) {
                String params = line.substring("CREATE MEMBER".length()).trim();
                if (params.isEmpty()) params = reader.readLine();
                handleCreateMember(params);
            } else if (subCmd.equals("GUEST")) {
                String params = line.substring("CREATE GUEST".length()).trim();
                if (params.isEmpty()) params = reader.readLine();
                handleCreateGuest(params);
            } else if (subCmd.equals("MENU")) {
                String type = parts[2];
                String prefix = "CREATE MENU " + type;
                String params = line.substring(prefix.length()).trim();
                if (params.isEmpty()) params = reader.readLine();
                handleCreateMenu(type, params);
            } else if (subCmd.equals("PROMO")) {
                String type = parts[2];
                String prefix = "CREATE PROMO " + type;
                String params = line.substring(prefix.length()).trim();
                if (params.isEmpty()) params = reader.readLine();
                handleCreatePromo(type, params);
            }
        } else if (cmd.equals("ADD_TO_CART")) {
            handleAddToCart(parts);
        } else if (cmd.equals("REMOVE_FROM_CART")) {
            handleRemoveFromCart(parts);
        } else if (cmd.equals("APPLY_PROMO")) {
            handleApplyPromo(parts);
        } else if (cmd.equals("TOPUP")) {
            handleTopup(parts);
        } else if (cmd.equals("CHECK_OUT")) {
            handleCheckOut(parts);
        } else if (cmd.equals("PRINT_HISTORY")) {
            handlePrintHistory(parts);
        } else if (cmd.equals("PRINT")) {
            handlePrint(parts);
        }
    }

    private void handleCreateMember(String params) {
        String[] parts = params.split("\\|");
        String id = parts[0];
        String name = parts[1];
        LocalDate date = LocalDate.parse(parts[2], DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        double balance = Double.parseDouble(parts[3]);
        if (findCustomer(id) != null) {
            outputs.add("CREATE MEMBER FAILED: " + id + " IS EXISTS");
        } else {
            customers.add(new Member(id, name, date, balance));
            outputs.add("CREATE MEMBER SUCCESS: " + id + " " + name);
        }
    }

    private void handleCreateGuest(String params) {
        String[] parts = params.split("\\|");
        String id = parts[0];
        double balance = Double.parseDouble(parts[1]);
        if (findCustomer(id) != null) {
            outputs.add("CREATE GUEST FAILED: " + id + " IS EXISTS");
        } else {
            customers.add(new Guest(id, balance));
            outputs.add("CREATE GUEST SUCCESS: " + id);
        }
    }

    private void handleCreateMenu(String type, String params) {
        String[] parts = params.split("\\|");
        String id = parts[0];
        String name = parts[1];
        String plate = parts[2];
        double price = Double.parseDouble(parts[3]);
        
        if (findVehicle(id) != null) {
            outputs.add("CREATE MENU FAILED: " + id + " IS EXISTS");
            return;
        }
        boolean plateExists = vehicles.stream().anyMatch(v -> v.getPlateNumber().equals(plate));
        if (plateExists) {
            outputs.add("CREATE MENU FAILED: " + plate + " IS EXISTS");
            return;
        }

        if (type.equals("MOTOR")) {
            vehicles.add(new Motorcycle(id, name, plate, price));
        } else if (type.equals("MOBIL")) {
            String customType = parts[4];
            vehicles.add(new Car(id, name, plate, price, customType));
        }
        outputs.add("CREATE MENU SUCCESS: " + id + " " + name + " " + plate);
    }

    private void handleCreatePromo(String type, String params) {
        String[] parts = params.split("\\|");
        String code = parts[0];
        LocalDate start = LocalDate.parse(parts[1], DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        LocalDate end = LocalDate.parse(parts[2], DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        double pct = Double.parseDouble(parts[3].replace("%", "")) / 100.0;
        double maxDisc = Double.parseDouble(parts[4]);
        double minPurch = Double.parseDouble(parts[5]);

        if (findPromo(code) != null) {
            outputs.add("CREATE PROMO " + type + " FAILED: " + code + " IS EXISTS");
            return;
        }

        if (type.equals("CASHBACK")) {
            promotions.add(new CashbackPromo(code, start, end, pct, maxDisc, minPurch));
        } else if (type.equals("DISCOUNT")) {
            promotions.add(new PercentOffPromo(code, start, end, pct, maxDisc, minPurch));
        }
        outputs.add("CREATE PROMO " + type + " SUCCESS: " + code);
    }

    private void handleAddToCart(String[] parts) {
        String customerId = parts[1];
        String vehicleId = parts[2];
        int qty = Integer.parseInt(parts[3]);
        LocalDate startDate = LocalDate.parse(parts[4], DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        Customer customer = findCustomer(customerId);
        Vehicle vehicle = findVehicle(vehicleId);
        
        if (customer == null || vehicle == null) {
            outputs.add("ADD_TO_CART FAILED: NON EXISTENT CUSTOMER OR MENU");
            return;
        }

        CartItem existingItem = customer.getCurrentCart().stream()
                .filter(item -> item.getVehicle().getId().equals(vehicleId))
                .findFirst().orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + qty);
            int newQty = existingItem.getQuantity();
            String dayStr = newQty == 1 ? "day" : "days";
            outputs.add("ADD_TO_CART SUCCESS: " + newQty + " " + dayStr + " " + vehicle.getName() + " " + vehicle.getPlateNumber() + " (UPDATED)");
        } else {
            CartItem cartItem = new CartItem(vehicle, qty, startDate);
            customer.getCurrentCart().add(cartItem);
            String dayStr = qty == 1 ? "day" : "days";
            outputs.add("ADD_TO_CART SUCCESS: " + qty + " " + dayStr + " " + vehicle.getName() + " " + vehicle.getPlateNumber() + " (NEW)");
        }
    }

    private void handleRemoveFromCart(String[] parts) {
        String customerId = parts[1];
        String vehicleId = parts[2];
        int qty = Integer.parseInt(parts[3]);
        
        Customer customer = findCustomer(customerId);
        Vehicle vehicle = findVehicle(vehicleId);
        
        if (customer == null || vehicle == null) {
            outputs.add("REMOVE_FROM_CART FAILED: NON EXISTENT CUSTOMER OR MENU");
            return;
        }

        CartItem item = customer.getCurrentCart().stream()
                .filter(ci -> ci.getVehicle().getId().equals(vehicleId))
                .findFirst().orElse(null);

        if (item == null) {
            outputs.add("REMOVE_FROM_CART FAILED: NON EXISTENT CUSTOMER OR MENU");
            return;
        }

        int remaining = item.getQuantity() - qty;
        if (remaining >= 1) {
            item.setQuantity(remaining);
            outputs.add("REMOVE_FROM_CART SUCCESS: " + vehicle.getName() + " QUANTITY IS DECREMENTED");
        } else {
            customer.getCurrentCart().remove(item);
            outputs.add("REMOVE_FROM_CART: " + vehicle.getName() + " IS REMOVED");
        }
    }

    private void handleApplyPromo(String[] parts) {
        String customerId = parts[1];
        String promoCode = parts[2];
        
        Customer customer = findCustomer(customerId);
        Promotion promo = findPromo(promoCode);
        
        if (promo == null || customer == null) {
            outputs.add("APPLY_PROMO FAILED: " + promoCode);
            return;
        }

        if (promo.isExpired(LocalDate.now())) {
            outputs.add("APPLY_PROMO FAILED: " + promoCode + " is EXPIRED");
            return;
        }
        
        if (!(customer instanceof Member)) {
            outputs.add("APPLY_PROMO FAILED: " + promoCode);
            return;
        }
        
        Member member = (Member) customer;
        if (member.getMembershipDays() <= 30) {
            outputs.add("APPLY_PROMO FAILED: " + promoCode);
            return;
        }
        
        double cartSubtotal = customer.getCurrentCart().stream().mapToDouble(CartItem::getSubtotal).sum();
        if (cartSubtotal < promo.getMinPurchase()) {
            outputs.add("APPLY_PROMO FAILED: " + promoCode);
            return;
        }
        
        customer.setAppliedPromo(promo);
        outputs.add("APPLY_PROMO SUCCESS: " + promoCode);
    }

    private void handleTopup(String[] parts) {
        String customerId = parts[1];
        double amount = Double.parseDouble(parts[2]);
        
        Customer customer = findCustomer(customerId);
        if (customer == null) {
            outputs.add("TOPUP FAILED: NON EXISTENT CUSTOMER");
            return;
        }
        
        double oldBalance = customer.getBalance();
        customer.addBalance(amount);
        double newBalance = customer.getBalance();
        outputs.add("TOPUP SUCCESS: " + customer.getName() + " " + formatNumber(oldBalance) + " => " + formatNumber(newBalance));
    }

    private void handleCheckOut(String[] parts) {
        String customerId = parts[1];
        Customer customer = findCustomer(customerId);
        if (customer == null) {
            outputs.add("CHECK_OUT FAILED: NON EXISTENT CUSTOMER");
            return;
        }
        
        List<CartItem> cart = customer.getCurrentCart();
        if (cart.isEmpty()) {
            outputs.add("CHECK_OUT FAILED: " + customerId + " " + customer.getName() + " INSUFFICIENT BALANCE");
            return;
        }

        Promotion promo = customer.getAppliedPromo();
        double subtotal = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        double totalPrice = subtotal;
        
        if (promo instanceof PercentOffPromo) {
            totalPrice = subtotal - Math.min(subtotal * promo.getPercentageDiscount(), promo.getMaxDiscount());
        }
        
        if (customer.getBalance() < totalPrice) {
            outputs.add("CHECK_OUT FAILED: " + customerId + " " + customer.getName() + " INSUFFICIENT BALANCE");
            return;
        }
        
        customer.deductBalance(totalPrice);
        if (promo instanceof CashbackPromo) {
            double cashback = Math.min(subtotal * promo.getPercentageDiscount(), promo.getMaxDiscount());
            customer.addBalance(cashback);
        }
        
        int orderNumber = customer.getOrderHistory().size() + 1;
        List<CartItem> itemsCopy = new ArrayList<>(cart);
        Order order = new Order(orderNumber, customer, itemsCopy, LocalDate.now(), promo);
        
        customer.getOrderHistory().add(order);
        allOrders.add(order);
        customer.getCurrentCart().clear();
        customer.setAppliedPromo(null);
        outputs.add("CHECK_OUT SUCCESS: " + customerId + " " + customer.getName());
    }

    private void handlePrint(String[] parts) {
        String customerId = parts[1];
        Customer customer = findCustomer(customerId);
        if (customer == null) {
            outputs.add("PRINT FAILED: NON EXISTENT CUSTOMER");
            return;
        }
        
        boolean hasCart = !customer.getCurrentCart().isEmpty();
        boolean hasOrders = !customer.getOrderHistory().isEmpty();
        
        if (hasCart) {
            printCartView(customer);
        } else if (hasOrders) {
            printOrderView(customer, customer.getOrderHistory().get(customer.getOrderHistory().size() - 1));
        } else {
            printCartView(customer);
        }
    }

    private void handlePrintHistory(String[] parts) {
        String customerId = parts[1];
        Customer customer = findCustomer(customerId);
        if (customer == null) {
            outputs.add("PRINT FAILED: NON EXISTENT CUSTOMER");
            return;
        }
        printHistoryView(customer);
    }

    private void printCartView(Customer customer) {
        outputs.add("Kode Pemesan: " + customer.getId());
        outputs.add("Nama: " + customer.getName());
        
        List<CartItem> items = new ArrayList<>(customer.getCurrentCart());
        items.sort(Comparator.comparing(CartItem::getStartDate).thenComparingDouble(CartItem::getSubtotal));
        
        outputs.add(String.format("%3s | %-25s | %3s | %8s ", "No", "Menu", "Dur.", "Subtotal"));
        outputs.add("==================================================");
        
        int i = 1;
        for (CartItem item : items) {
            String display = item.getVehicle().getName() + " " + item.getVehicle().getPlateNumber();
            if (display.length() > 25) {
                display = display.substring(0, 25);
            }
            String subtotalStr = formatNumber(item.getSubtotal());
            outputs.add(String.format("%3d | %-25s | %3d | %8s ", i, display, item.getQuantity(), subtotalStr));
            String startStr = item.getStartDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String endStr = item.getEndDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            outputs.add(String.format("%5s %s", " ", startStr + " - " + endStr));
            i++;
        }
        
        outputs.add("==================================================");
        
        double subtotal = items.stream().mapToDouble(CartItem::getSubtotal).sum();
        outputs.add(String.format("%-32s: %14s", "Sub Total", formatNumber(subtotal)));
        
        Promotion promo = customer.getAppliedPromo();
        if (promo instanceof PercentOffPromo) {
            double discount = Math.min(subtotal * promo.getPercentageDiscount(), promo.getMaxDiscount());
            outputs.add(String.format("%-27s: %9s", "PROMO: " + promo.getPromoCode(), formatNumber(-discount)));
        }
        
        outputs.add("==================================================");
        
        double total = subtotal;
        if (promo instanceof PercentOffPromo) {
            total = subtotal - Math.min(subtotal * promo.getPercentageDiscount(), promo.getMaxDiscount());
        }
        outputs.add(String.format("%-32s: %14s", "Total", formatNumber(total)));
        
        if (promo instanceof CashbackPromo) {
            double cashback = Math.min(subtotal * promo.getPercentageDiscount(), promo.getMaxDiscount());
            outputs.add(String.format("%-27s: %9s", "PROMO: " + promo.getPromoCode(), formatNumber(cashback)));
        }
        
        outputs.add(String.format("%-32s: %14s", "Saldo", formatNumber(customer.getBalance())));
        outputs.add("");
    }

    private void printOrderView(Customer customer, Order order) {
        outputs.add("Kode Pemesan: " + customer.getId());
        outputs.add("Nama: " + customer.getName());
        outputs.add("Nomor Pesanan: " + order.getOrderNumber());
        outputs.add("Tanggal Pesanan: " + formatIndonesianDate(order.getOrderDate()));
        
        List<CartItem> items = new ArrayList<>(order.getItems());
        items.sort(Comparator.comparing(CartItem::getStartDate).thenComparingDouble(CartItem::getSubtotal));
        
        outputs.add(String.format("%3s | %-25s | %3s | %8s ", "No", "Menu", "Dur.", "Subtotal"));
        outputs.add("==================================================");
        
        int i = 1;
        for (CartItem item : items) {
            String display = item.getVehicle().getName() + " " + item.getVehicle().getPlateNumber();
            if (display.length() > 25) {
                display = display.substring(0, 25);
            }
            String subtotalStr = formatNumber(item.getSubtotal());
            outputs.add(String.format("%3d | %-25s | %3d | %8s ", i, display, item.getQuantity(), subtotalStr));
            String startStr = item.getStartDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String endStr = item.getEndDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            outputs.add(String.format("%5s %s", " ", startStr + " - " + endStr));
            i++;
        }
        
        outputs.add("==================================================");
        
        double subtotal = items.stream().mapToDouble(CartItem::getSubtotal).sum();
        outputs.add(String.format("%-32s: %14s", "Sub Total", formatNumber(subtotal)));
        
        Promotion promo = order.getPromo();
        if (promo instanceof PercentOffPromo) {
            double discount = Math.min(subtotal * promo.getPercentageDiscount(), promo.getMaxDiscount());
            outputs.add(String.format("%-27s: %9s", "PROMO: " + promo.getPromoCode(), formatNumber(-discount)));
        }
        
        outputs.add("==================================================");
        
        double total = subtotal;
        if (promo instanceof PercentOffPromo) {
            total = subtotal - Math.min(subtotal * promo.getPercentageDiscount(), promo.getMaxDiscount());
        }
        outputs.add(String.format("%-32s: %14s", "Total", formatNumber(total)));
        
        if (promo instanceof CashbackPromo) {
            double cashback = Math.min(subtotal * promo.getPercentageDiscount(), promo.getMaxDiscount());
            outputs.add(String.format("%-27s: %9s", "PROMO: " + promo.getPromoCode(), formatNumber(cashback)));
        }
        
        outputs.add(String.format("%-32s: %14s", "Saldo", formatNumber(customer.getBalance())));
        outputs.add("");
    }

    private String formatIndonesianDate(LocalDate date) {
        String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        return date.getDayOfMonth() + " " + months[date.getMonthValue() - 1] + " " + date.getYear();
    }

    private void printHistoryView(Customer customer) {
        outputs.add("Kode Pemesan: " + customer.getId());
        outputs.add("Nama: " + customer.getName());
        outputs.add("Saldo: " + formatNumber(customer.getBalance()));
        outputs.add(String.format("%4s| %10s | %5s | %5s | %8s | %-8s", "No", "No. Pesanan", "Motor", "Mobil", "Subtotal", "PROMO"));
        outputs.add("=======================================================");
        
        int i = 1;
        for (Order order : customer.getOrderHistory()) {
            long motorCount = order.getItems().stream().filter(item -> item.getVehicle() instanceof Motorcycle).count();
            long mobilCount = order.getItems().stream().filter(item -> item.getVehicle() instanceof Car).count();
            int totalPriceInt = (int) order.getTotalPrice();
            String promoCode = order.getPromo() != null ? order.getPromo().getPromoCode() : "";
            outputs.add(String.format("%4d| %11d | %5d | %5d | %8d | %-8s", i, order.getOrderNumber(), motorCount, mobilCount, totalPriceInt, promoCode));
            i++;
        }
        
        outputs.add("=======================================================");
    }

    private String formatNumber(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("###,###.##", symbols);
        return formatter.format(amount);
    }

    private Customer findCustomer(String id) {
        return customers.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    private Vehicle findVehicle(String id) {
        return vehicles.stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
    }

    private Promotion findPromo(String code) {
        return promotions.stream().filter(p -> p.getPromoCode().equals(code)).findFirst().orElse(null);
    }
}
