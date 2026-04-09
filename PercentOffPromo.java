import java.time.LocalDate;

public class PercentOffPromo extends Promotion {
    private double percentOff;

    public PercentOffPromo(String promoCode, LocalDate startDate, LocalDate endDate, double percentOff) {
        super(promoCode, startDate, endDate);
        this.percentOff = percentOff;
    }

    @Override
    public double calculateDiscount(Order x) {
        if (isMinimumPriceEligible(x)) {
            double discount = x.getSubTotal() * percentOff;
            if (discount > x.getSubTotal() + x.getShippingFee()) {
                throw new IllegalStateException("Discount exceeds total cost");
            }
            return discount;
        }
        return 0;
    }

    @Override
    public double calculateCashback(Order x) {
        return 0;
    }

    @Override
    public double calculateShippingDiscount(Order x) {
        return 0;
    }

    @Override
    public int compareTo(Promotion other) {
        if (other instanceof PercentOffPromo) {
            return Double.compare(this.percentOff, ((PercentOffPromo) other).percentOff);
        }
        return super.compareTo(other);
    }

    public double getPercentOff() {
        return percentOff;
    }

    public void setPercentOff(double percentOff) {
        this.percentOff = percentOff;
    }
}
