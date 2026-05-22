public class Guest extends Customer {

    public Guest(String id, double initialBalance) {
        super(id, "GUEST", initialBalance);
    }

    @Override
    public String getName() {
        return "GUEST";
    }
}
