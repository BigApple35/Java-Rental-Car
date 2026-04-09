import java.time.LocalDate;

public abstract class Promotion implements Applicable, Comparable<Promotion> {
    private String promoCode;
    private LocalDate startDate;
    private LocalDate endDate;

    public Promotion(String promoCode, LocalDate startDate, LocalDate endDate) {
        this.promoCode = promoCode;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public int compareTo(Promotion other) {
        return 0;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
