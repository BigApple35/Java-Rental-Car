public class Guest extends Customer {

    public Guest(String customerId, String firstName, String lastName, String email, String phoneNumber) {
        super(customerId, firstName, lastName, email, phoneNumber);
    }

    @Override
    public String getFullName() {
        if (getLastName() != null && !getLastName().trim().isEmpty()) {
            return getFirstName() + " " + getLastName();
        }
        return getFirstName();
    }
}
