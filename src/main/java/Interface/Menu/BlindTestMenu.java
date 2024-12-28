package Interface.Menu;

import Entities.Account;
import Interface.Operation;
import jakarta.persistence.EntityManagerFactory;

public class BlindTestMenu implements Operation {
    private Operation nextOperation;
    private Account account;
    public BlindTestMenu(Account account) {
        this.account = account;
    }

    private void setNextOperation(Operation nextOperation) {
        this.nextOperation = nextOperation;
    }

    public Operation getNextOperation() {
        return nextOperation;
    }

    public void run(EntityManagerFactory emf) {

    }
}
