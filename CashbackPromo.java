import java.time.LocalDate;

public class CashbackPromo extends Promotion {

    public CashbackPromo(String promoCode, LocalDate startDate, LocalDate endDate, double percentageDiscount, double maxDiscount, double minPurchase) {
        super(promoCode, startDate, endDate, percentageDiscount, maxDiscount, minPurchase);
    }

    @Override
    public String getPromoType() {
        return "CASHBACK";
    }

    public double getTotalCashback(Order order) {
        return Math.min(order.getSubtotalPrice() * getPercentageDiscount(), getMaxDiscount());
    }
}
