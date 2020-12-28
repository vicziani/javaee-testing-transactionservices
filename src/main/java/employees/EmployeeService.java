package employees;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class EmployeeService {

    @Inject
    private EmployeeDao employeeDao;

    @Transactional
    public Employee create(String name) {
        var employee = new Employee(name);
        employeeDao.save(employee);
        return employee;
    }

    @Transactional
    public Employee update(long id, String name) {
        var employee = employeeDao.findById(id);
        employee.setName(name);
        return employee;
    }

    public List<Employee> findAll() {
        return employeeDao.findAll();
    }
}
