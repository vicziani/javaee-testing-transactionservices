package employees;

import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.transaction.spi.TransactionServices;

import javax.transaction.*;

@Slf4j
public class CDITransactionServices implements TransactionServices {

    @Override
    public void registerSynchronization(Synchronization synchronization) {
        try {
            com.arjuna.ats.jta.TransactionManager.transactionManager()
                    .getTransaction().registerSynchronization(synchronization);
        } catch (SystemException | IllegalStateException | RollbackException e) {
            throw new IllegalStateException("Cannot register synchronization observer " + synchronization
                    + " to the available transaction", e);
        }
    }

    @Override
    public boolean isTransactionActive() {
        try {
            int status = com.arjuna.ats.jta.TransactionManager.transactionManager().getStatus();
            switch(status) {
                case Status.STATUS_ACTIVE:
                case Status.STATUS_COMMITTING:
                case Status.STATUS_MARKED_ROLLBACK:
                case Status.STATUS_PREPARED:
                case Status.STATUS_PREPARING:
                case Status.STATUS_ROLLING_BACK:
                    return true;
                default:
                    return false;
            }
        } catch (SystemException se) {
            log.error("Cannot obtain the status of the transaction", se);
            return false;
        }
    }

    @Override
    public UserTransaction getUserTransaction() {
        return com.arjuna.ats.jta.UserTransaction.userTransaction();
    }

    @Override
    public void cleanup() {

    }
}
