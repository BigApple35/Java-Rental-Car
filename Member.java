import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Member extends Customer {
    private LocalDate registrationDate;

    public Member(String id, String name, LocalDate registrationDate, double initialBalance) {
        super(id, name, initialBalance);
        this.registrationDate = registrationDate;
    }

    @Override
    public String getName() {
        return name;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public long getMembershipDays() {
        return ChronoUnit.DAYS.between(registrationDate, LocalDate.now());
    }
}
