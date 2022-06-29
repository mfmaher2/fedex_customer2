package datastax.com.multiThreadTest;

import com.datastax.oss.driver.api.core.CqlSession;
import datastax.com.CustomerMapper;
import datastax.com.DAOs.AccountDao;
import datastax.com.DAOs.AssocAccountDao;
import datastax.com.DAOs.PaymentInfoDao;
import datastax.com.dataObjects.Account;
import datastax.com.schemaElements.Keyspace;
import datastax.com.schemaElements.KeyspaceConfig;

public class AccountRunnable implements Runnable{

    CqlSession session = null;
    KeyspaceConfig keyspaceConfig = null;
    int threadID = 0;
    int numWriteActions = 0;

    AccountDao daoAccount = null;
    AssocAccountDao daoAssocAccount = null;
    PaymentInfoDao daoPayment = null;

    public AccountRunnable(CqlSession session, KeyspaceConfig keyspaces, CustomerMapper mapper, int threadID, int accountWrites){
        this.threadID = threadID;
        this.numWriteActions = accountWrites;
        this.keyspaceConfig = keyspaces;

        daoAccount = mapper.accountDao(keyspaceConfig.getKeyspaceName(Keyspace.ACCOUNT_KS));

        this.session = session;
    }

    public void run(){
        String acctBase = "acctThreadTest_";
        String opco = "opcoThreadProcessingTest";
        String customerType = "testCustType";
        String accountType = "testAcctType";

        for(int i=0; i<numWriteActions; i++){
            String acctNum = acctBase + String.valueOf(threadID) + "_" + String.valueOf(i);

            Account newAcct = new Account();
            newAcct.setAccountNumber(acctNum);
            newAcct.setOpco(opco);
            newAcct.setProfileCustomerType(customerType);
            newAcct.setProfileAccountType(accountType);

            daoAccount.save(newAcct);
        }
    }
}
