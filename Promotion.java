import java.time.LocalDate;

public abstract class Promotion {
    private String promoCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private double percentageDiscount;
    private double maxDiscount;
    private double minPurchase;

    public Promotion(String promoCode, LocalDate startDate, LocalDate endDate, double percentageDiscount, double maxDiscount, double minPurchase) {
        this.promoCode = promoCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.percentageDiscount = percentageDiscount;
        this.maxDiscount = maxDiscount;
        this.minPurchase = minPurchase;
    }

    public abstract String getPromoType();

    public boolean isExpired(LocalDate checkDate) {
        boolean inside = checkDate.isBefore(startDate);

        return inside;
}

    public String getPromoCode() {
        return promoCode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getPercentageDiscount() {
        return percentageDiscount;
    }

    public double getMaxDiscount() {
        return maxDiscount;
    }

    public double getMinPurchase() {
        return minPurchase;
    }
}
