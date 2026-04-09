public abstract class Customer {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Order currentOrder;

    public Customer(String customerId, String firstName, String lastName, String email, String phoneNumber) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public abstract String getFullName();

    public Order makeOrder(Vehicle v, int durationHours) {
        if (this.currentOrder != null && this.currentOrder.getStatus() == OrderStatus.UNPAID) {
            throw new IllegalStateException("Current order is already set and not yet paid/canceled");
        }
        this.currentOrder = new Order(v, durationHours, 1);
        return this.currentOrder;
    }

    public void confirmPay(int orderNumber) {
        if (this.currentOrder != null && this.currentOrder.getOrderNumber() == orderNumber) {
            this.currentOrder.pay();
            this.currentOrder = null;
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }
}
