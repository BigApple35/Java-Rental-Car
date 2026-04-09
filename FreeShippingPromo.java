import java.time.LocalDate;

public class FreeShippingPromo extends Promotion {

    public FreeShippingPromo(String promoCode, LocalDate startDate, LocalDate endDate) {
        super(promoCode, startDate, endDate);
    }

    @Override
    public double calculateDiscount(Order x) {
        return 0;
    }

    @Override
    public double calculateCashback(Order x) {
        return 0;
    }

    @Override
    public double calculateShippingDiscount(Order x) {
        if (isShippingFeeEligible(x)) {
            double discount = x.getShippingFee();
            if (discount > x.getSubTotal() + x.getShippingFee()) {
                throw new IllegalStateException("Discount exceeds total cost");
            }
            return discount;
        }
        return 0;
    }

    @Override
    public int compareTo(Promotion other) {
        if (other instanceof FreeShippingPromo) {
            return 0;
        }
        return 1;
    }
}
