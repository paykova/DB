package bill_db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Table(name = "bank_accounts")
public class BankAccount extends BasicBillingDetail{

    private String bankName;
    private String swiftCode;

    public String getBankName() {
        return bankName;
    }

    @Column(name = "bank")
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Column(name = "swift_code")
    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }
}
