package employees;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class EmployeeDao {

    @PersistenceContext(name = "pu")
    private EntityManager em;

    public void save(Employee employee) {
        em.persist(employee);
    }

    public List<Employee> findAll() {
        return em.createQuery("select e from Employee e order by e.name", Employee.class).getResultList();
    }

    public Employee findById(long id) {
        return em.find(Employee.class, id);
    }
}
