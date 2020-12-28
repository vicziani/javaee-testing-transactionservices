package employees;

import com.arjuna.ats.jta.cdi.TransactionExtension;
import com.arjuna.ats.jta.utils.JNDIManager;
import org.hibernate.cfg.Environment;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.jnp.server.NamingBeanImpl;
import org.junit.jupiter.api.*;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoWeld
public class EmployeeServiceIT {

    @Inject
    EmployeeService employeeService;

    static NamingBeanImpl NAMING_BEAN;

    @Inject
    BeanManager beanManager;

    @BeforeAll
    static void before() throws Throwable {
        NAMING_BEAN = new NamingBeanImpl();
        NAMING_BEAN.start();

        JNDIManager.bindJTAImplementation();
        TransactionalConnectionProvider.bindDataSource();
    }

    @AfterAll
    static void after() {
        NAMING_BEAN.stop();
    }

    @WeldSetup
    public WeldInitiator weldInitializator = WeldInitiator.from(WeldInitiator.createWeld()
            .addBeanClasses(EmployeeDao.class, EmployeeService.class, CDITransactionServices.class)
//            .addServices(new CDITransactionServices())
            .addExtension(new TransactionExtension())
            )
            .setPersistenceContextFactory(this::createEntityManager)
            .build();

    @Test
    void testCreate() {
        employeeService.create("John Doe");
        employeeService.create("Jack Doe");
        var employees = employeeService.findAll();
        assertThat(employees).extracting(Employee::getName)
                .containsExactly("Jack Doe", "John Doe");
    }

    @Test
    void testUpdate() {
        var created = employeeService.create("John Doe");
        employeeService.update(created.getId(), "Jack Doe");
        var employees = employeeService.findAll();
        assertThat(employees).extracting(Employee::getName)
                .containsExactly("Jack Doe");
    }


    private Object createEntityManager(InjectionPoint ip) {
        Map<String, Object> props = new HashMap<>();
        props.put("javax.persistence.bean.manager", beanManager);
//        props.put(Environment.CONNECTION_PROVIDER, TransactionalConnectionProvider.class);

        var factory = Persistence.createEntityManagerFactory("pu", props);
        return factory.createEntityManager();
    }
}
