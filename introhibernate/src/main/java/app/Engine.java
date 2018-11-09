package app;

import app.entities.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Scanner;

public class Engine implements Runnable {
    private final EntityManager entityManager;

    public Engine(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void run() {
        // this.containsEmployee();
        //  this.findEmployeeByFirstName();
        // this.employeesMaximumSalaries();
        // this.addingNewAddressAndUpdatingEmployee();
        // this.employeesFromDepartment();
        // this.employeesWithSalaryOver();
        //  this.getEmployeeWithProjects();
        //  this.addressWithEmployeeCount();
         this.findLatestProjects();
       // this.removeObjects();
       // this.increaseSalaries();
      //  this.removeTowns();
    }

    /*
    ** Problem 11. Remove Towns
     */
    private void removeTowns(){
        Scanner scanner = new Scanner(System.in);
        String nameOfTheTown = scanner.nextLine();

        try {
            Town town = entityManager.createQuery("SELECT t FROM Town as t WHERE t.name =:name", Town.class)
                    .setParameter("name", nameOfTheTown)
                    .getSingleResult();
            List<Address> addresses = entityManager
                    .createQuery("SELECT add FROM Address as add WHERE add.town = :town", Address.class)
                    .setParameter("town", town)
                    .getResultList();
            entityManager.getTransaction().begin();
            entityManager.createQuery("UPDATE Employee as emp SET emp.address = null WHERE emp.address in :addresses")
                    .setParameter("addresses", addresses)
                    .executeUpdate();
            for (Address add : addresses) {
                entityManager.remove(add);
            }
            entityManager.remove(town);
            entityManager.getTransaction().commit();
            System.out.printf("%d addresses in %s deleted%n", addresses.size(), nameOfTheTown);
        } catch (NoResultException e) {
            System.out.println("No such town");
        }

    }

    /*
    ** Problem 10. Increase Salaries
     */
    private void increaseSalaries(){
        this.entityManager.getTransaction().begin();

        String query ="SELECT * FROM  employees e WHERE e.department_id = 1 OR e.department_id = 2 OR e.department_id = 4 OR e.department_id = 11";
        List<Employee> employees = this.entityManager
                .createNativeQuery(query, Employee.class)
                .getResultList();
        for (Employee employee : employees) {
            employee.setSalary(employee.getSalary().multiply((new BigDecimal(1.12))));
            entityManager.persist(employee);
        }

        for (Employee employee : employees) {
            System.out.printf("%s %s ($%s)%n", employee.getFirstName(), employee.getLastName(), employee.getSalary());
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }


    /*
     ** Problem 2. Remove Objects
     */
    private void removeObjects() {
        this.entityManager.getTransaction().begin();

        String query = "SELECT * FROM towns t WHERE LENGTH(t.name) > 5";
        List<Town> towns = this.entityManager
                .createNativeQuery(query, Town.class)
                .getResultList();
        for (Town town : towns) {
            this.entityManager.remove(town);
        }
        for (Town town : towns) {
            town.setName(town.getName().toLowerCase());
            entityManager.persist(town);
        }
        this.entityManager.getTransaction().commit();
        this.entityManager.close();
    }


    /*
     ** Problem 9. Find Latest 10 Projects
     */
    private void findLatestProjects() {
        this.entityManager.getTransaction().begin();

        String query = "SELECT p FROM Project AS p WHERE p.endDate IS NOT NULL ORDER BY p.startDate DESC";

        List<Project> projects = this.entityManager.createQuery(query, Project.class)
                .setMaxResults(10)
                .getResultList();

        for (Project project : projects) {
            System.out.println(String
                    .format("Project name: %s%n      Project Description: %s%n      Project Start Date: %s%n      Project End Date: %s",
                            project.getName(),
                            project.getDescription(),
                            project.getStartDate(),
                            project.getEndDate()));
        }
        this.entityManager.close();

//        String query = "SELECT p FROM Project as p ORDER BY p.name ASC";
//        List<Project> projects = this.entityManager
//                .createQuery(query, Project.class)
//                .setMaxResults(10)
//                .getResultList();
//
//        for (Project project : projects) {
//            System.out.println(String
//                    .format("Project name: %s%n      Project Description: %s%n      Project Start Date: %s%n      Project End Date: %s",
//                            project.getName(),
//                            project.getDescription(),
//                            project.getStartDate(),
//                            project.getEndDate()));
//        }
//        this.entityManager.close();

    }


    /*
     ** Problem 7. Addresses with Employee Count
     */
    private void addressWithEmployeeCount() {

        this.entityManager.getTransaction().begin();

        String query = "SELECT *, COUNT(e.employee_id) FROM addresses a JOIN employees e on a.address_id = e.address_id JOIN towns t on a.town_id = t.town_id GROUP BY a.address_id ORDER BY  COUNT(e.employee_id) DESC, t.town_id ASC LIMIT 10";
        List<Employee> employees = this.entityManager
                .createNativeQuery(query, Employee.class)
                .getResultList();
        for (Employee employee : employees) {
            System.out.println(employee.getAddress().getText() + ", " + employee.getAddress().getTown().getName() + " - " + employee.getAddress().getEmployees().size());
        }
    }

    /*
     ** Problem 8. Get Employee With Project
     */
    private void getEmployeeWithProjects() {
        Scanner scanner = new Scanner(System.in);
        int id = Integer.parseInt(scanner.nextLine());

        this.entityManager.getTransaction().begin();

        String query = "SELECT * FROM employees e JOIN employees_projects ON e.employee_id = employees_projects.employee_id JOIN projects p on employees_projects.project_id = p.project_id WHERE e.employee_id = " + id + " ORDER BY p.name ASC";
        String que = "SELECT * FROM projects JOIN employees_projects e on projects.project_id = e.project_id WHERE e.employee_id = " + id + " ORDER BY name ASC";

        List<Employee> employees = this.entityManager
                .createNativeQuery(query, Employee.class)
                .getResultList();
        for (Employee employee : employees) {
            System.out.println(employee.getFirstName() + " " + employee.getLastName() + " - " + employee.getJobTitle());
            System.out.println(employee.getProjects());
            break;
        }

        List<Project> project = this.entityManager
                .createNativeQuery(que, Project.class)
                .getResultList();
        for (Project project1 : project) {
            System.out.println(project1.getName());
        }

    }

    /*
     ** Problem 4. Employees With Salary Over 50 000
     */
    private void employeesWithSalaryOver() {

        this.entityManager.getTransaction().begin();

        String query = "SELECT * FROM employees e WHERE e.salary > 50000";
        List<Employee> employees = this.entityManager
                .createNativeQuery(query, Employee.class)
                .getResultList();
        for (Employee employee : employees) {
            System.out.println(employee.getFirstName());
        }

    }


    /*
     ** Problem 5. Employees from Department
     */
    private void employeesFromDepartment() {
        this.entityManager.getTransaction().begin();
        String query = "SELECT * FROM employees e JOIN departments d on e.department_id = d.department_id WHERE d.department_id = 6 ORDER BY e.salary ASC, e.employee_id ASC";

        List<Employee> employees = this.entityManager
                .createNativeQuery(query, Employee.class)
                .getResultList();
        for (Employee employee : employees) {
            System.out.println(String.format("%s %s from %s - $%.2f", employee.getFirstName(),
                    employee.getLastName(), employee.getDepartment().getName(), employee.getSalary()));
        }
    }

    /*
     ** Problem 13. Employees Maximum Salaries
     */

    private void employeesMaximumSalaries() {

        this.entityManager.getTransaction().begin();

        String query = "SELECT d.name, max(emp.salary) FROM Employee as emp JOIN emp.department as d GROUP BY d.name HAVING max(emp.salary) < 30000 OR max(emp.salary) > 70000 ORDER BY d.id";
        this.entityManager.createQuery(query, Object[].class)
                .getResultList()
                .forEach(x -> System.out.printf("%s - %s%n", x[0], x[1]));
        this.entityManager.getTransaction().commit();
        this.entityManager.close();
    }

    /*
     ** Problem 6. Adding a New Address and Updating Employee
     */
    private void addingNewAddressAndUpdatingEmployee() {
        Scanner scanner = new Scanner(System.in);
        String lastName = scanner.nextLine();

        this.entityManager.getTransaction().begin();

        Address address = new Address();
        address.setText("Vitoshka 15");

        Town town = this.entityManager.createQuery("FROM Town WHERE name ='Sofia'", Town.class)
                .getSingleResult();
        address.setTown(town);
        this.entityManager.persist(address);

        Employee employee = this.entityManager
                .createQuery("FROM Employee WHERE last_name = :name", Employee.class)
                .setParameter("name", lastName)
                .getSingleResult();

        this.entityManager.detach(employee.getAddress());
        employee.setAddress(address);
        this.entityManager.merge(employee);

        this.entityManager.getTransaction().commit();
    }



    /*
     ** Problem 12. Find Employees by First Name
     */

    private void findEmployeeByFirstName() {
        Scanner scanner = new Scanner(System.in);
        String startWith = scanner.nextLine() + "%";

        this.entityManager.getTransaction().begin();

        String query = String.format("SELECT * FROM employees e WHERE lower(e.first_name) LIKE lower('%s')", startWith);
        List<Employee> employees = this.entityManager
                .createNativeQuery(query, Employee.class)
                .getResultList();
        for (Employee employee : employees) {
            System.out.println(employee.getFirstName() + " " + employee.getLastName() + " - " + employee.getJobTitle() + " - " + "($" + employee.getSalary() + ")");
        }
        this.entityManager.getTransaction().commit();
    }

    /*
     ** Problem 3. Contains Employee
     */
    private void containsEmployee() {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();

        this.entityManager.getTransaction().begin();

        try {
            Employee employee = this.entityManager
                    .createQuery("FROM Employee WHERE concat(first_name, ' ', last_name) = :name", Employee.class)
                    .setParameter("name", name)
                    .getSingleResult();

            System.out.println("Yes");
        } catch (NoResultException nre) {
            System.out.println("No");

        }
        this.entityManager.getTransaction().commit();
    }
}
