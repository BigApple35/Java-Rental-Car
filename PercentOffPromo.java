import java.time.LocalDate;

public class PercentOffPromo extends Promotion {

    public PercentOffPromo(String promoCode, LocalDate startDate, LocalDate endDate, double percentageDiscount, double maxDiscount, double minPurchase) {
        super(promoCode, startDate, endDate, percentageDiscount, maxDiscount, minPurchase);
    }

    @Override
    public String getPromoType() {
        return "DISCOUNT";
    }

    public double getTotalPriceOff(Order order) {
        return Math.min(order.getSubtotalPrice() * getPercentageDiscount(), getMaxDiscount());
    }
}
