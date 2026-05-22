public interface Applicable {
    default boolean isCustomerEligible(Customer customer) {
        return false;
    }
    
    default boolean isMinimumPriceEligible(Order order) {
        return false;
    }
    
    default double calculateDiscount(Order order) {
        return 0.0;
    }
    
    default double calculateCashback(Order order) {
        return 0.0;
    }
}
