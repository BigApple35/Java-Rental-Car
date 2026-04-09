import java.time.LocalDate;

public class CashbackPromo extends Promotion {
    private double cashbackAmount;

    public CashbackPromo(String promoCode, LocalDate startDate, LocalDate endDate, double cashbackAmount) {
        super(promoCode, startDate, endDate);
        this.cashbackAmount = cashbackAmount;
    }

    @Override
    public double calculateDiscount(Order x) {
        return 0;
    }

    @Override
    public double calculateCashback(Order x) {
        if (isMinimumPriceEligible(x)) {
            if (cashbackAmount > x.getSubTotal() + x.getShippingFee()) {
                throw new IllegalStateException("Cashback exceeds total cost");
            }
            return cashbackAmount;
        }
        return 0;
    }

    @Override
    public double calculateShippingDiscount(Order x) {
        return 0;
    }

    @Override
    public int compareTo(Promotion other) {
        if (other instanceof CashbackPromo) {
            return Double.compare(this.cashbackAmount, ((CashbackPromo) other).cashbackAmount);
        }
        return super.compareTo(other);
    }

    public double getCashbackAmount() {
        return cashbackAmount;
    }

    public void setCashbackAmount(double cashbackAmount) {
        this.cashbackAmount = cashbackAmount;
    }
}
