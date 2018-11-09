package university_db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;


@Table(name = "teachers")
public class Teacher extends Person{

    private String email;
    private Double salaryPerHour;
    private Set<Course> toughCourses;

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "salary_per_hour")
    public Double getSalaryPerHour() {
        return salaryPerHour;
    }

    public void setSalaryPerHour(Double salaryPerHour) {
        this.salaryPerHour = salaryPerHour;
    }

    @OneToMany(mappedBy = "teacher")
    public Set<Course> getToughCourses() {
        return toughCourses;
    }

    public void setToughCourses(Set<Course> toughCourses) {
        this.toughCourses = toughCourses;
    }
}
