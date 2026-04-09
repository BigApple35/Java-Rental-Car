import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Member extends Customer {
    private LocalDate memberSince;

    public Member(String customerId, String firstName, String lastName, String email, String phoneNumber, LocalDate memberSince) {
        super(customerId, firstName, lastName, email, phoneNumber);
        this.memberSince = memberSince;
    }

    @Override
    public String getFullName() {
        if (getLastName() != null && !getLastName().trim().isEmpty()) {
            return getFirstName() + " " + getLastName();
        }
        return getFirstName();
    }

    public long getMembershipDuration() {
        return ChronoUnit.DAYS.between(memberSince, LocalDate.now());
    }

    public LocalDate getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(LocalDate memberSince) {
        this.memberSince = memberSince;
    }
}
