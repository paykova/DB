package bill_db;

import javax.persistence.*;
import java.util.Set;


@Table(name = "users")
public class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<BasicBillingDetail> billingDetail;

    public User() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @OneToMany(mappedBy = "owner")
    public Set<BasicBillingDetail> getBillingDetail() {
        return billingDetail;
    }

    public void setBillingDetail(Set<BasicBillingDetail> billingDetail) {
        this.billingDetail = billingDetail;
    }
}
