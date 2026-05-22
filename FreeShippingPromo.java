import java.time.LocalDate;

public class FreeShippingPromo extends Promotion {

    public FreeShippingPromo(String promoCode, LocalDate startDate, LocalDate endDate, double percentageDiscount, double maxDiscount, double minPurchase) {
        super(promoCode, startDate, endDate, percentageDiscount, maxDiscount, minPurchase);
    }

    @Override
    public String getPromoType() {
        return "FREESHIP";
    }
}
