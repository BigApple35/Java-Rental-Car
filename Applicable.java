public interface Applicable {
    default boolean isCustomerEligible(Customer x) {
        if (x instanceof Member) {
            return ((Member) x).getMembershipDuration() > 30;
        }
        return false;
    }

    default boolean isMinimumPriceEligible(Order x) {
        return x.getSubTotal() >= 100000;
    }

    default boolean isShippingFeeEligible(Order x) {
        return x.getShippingFee() > 0;
    }

    double calculateDiscount(Order x);
    double calculateCashback(Order x);
    double calculateShippingDiscount(Order x);
}
