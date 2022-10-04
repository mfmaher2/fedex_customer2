package datastax.com.multiThreadTest;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.BatchableStatement;
import datastax.com.CustomerMapper;
import datastax.com.DAOs.AccountDao;
import datastax.com.DAOs.AssocAccountDao;
import datastax.com.DAOs.PaymentInfoDao;
import datastax.com.dataObjects.Account;
import datastax.com.dataObjects.AssocAccount;
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
        daoAssocAccount = mapper.assocAccountDao(keyspaceConfig.getKeyspaceName(Keyspace.ASSOC_ACCOUNT_KS));
        daoPayment = mapper.paymentInfoDao(keyspaceConfig.getKeyspaceName(Keyspace.PAYMENT_INFO_KS));

        this.session = session;
    }

    public void run(){
        String acctBase = "acctThreadTest_";
        String opco = "opcoThreadProcessingTest";
        String customerType = "testCustType";
        String accountType = "testAcctType";
        String assocAcctNum = "assocNum";

        for(int i=0; i<numWriteActions; i++){
            BatchStatement batch = BatchStatement.builder(BatchType.LOGGED).build();

            String acctNum = acctBase + String.valueOf(threadID) + "_" + String.valueOf(i);

            Account newAcct = new Account();
            newAcct.setAccountNumber(acctNum);
            newAcct.setOpco(opco);
            newAcct.setProfileCustomerType(customerType);
            newAcct.setProfileAccountType(accountType);
            batch = batch.add(daoAccount.batchSave(newAcct));

            AssocAccount assocAccount = new AssocAccount();
            assocAccount.setAccountNumber(acctNum);
            assocAccount.setOpco(opco);
            assocAccount.setAssociatedAccountNumber(assocAcctNum);


            session.execute(batch);


        }
    }
}
