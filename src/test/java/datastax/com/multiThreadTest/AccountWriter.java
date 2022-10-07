package datastax.com.multiThreadTest;

import com.datastax.oss.driver.api.core.CqlSession;
import datastax.com.CustomerMapper;
import datastax.com.schemaElements.KeyspaceConfig;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AccountWriter {

    CqlSession session = null;
    KeyspaceConfig keyspaceConfig = null;
    CustomerMapper customerMapper = null;
    int threadAmount = 0;

    public AccountWriter(CqlSession session, KeyspaceConfig keyspaces, CustomerMapper mapper, int threadCount){
        this.session = session;
        this.keyspaceConfig = keyspaces;
        this.customerMapper = mapper;
        this.threadAmount = threadCount;
    }

    public void runAccountWrite(){
        System.out.println("BEGIN ACCOUNT WRITER JOBS");

        int writesPerThread = 100;
        ArrayList<CompletableFuture<Void>> batchCompletableFutures = new ArrayList<CompletableFuture<Void>>();
        for(int value=0; value<threadAmount; value++){
            try {
                Runnable worker = new AccountRunnable(session,keyspaceConfig, customerMapper, value, writesPerThread);
                batchCompletableFutures.add(CompletableFuture.runAsync(worker));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        CompletableFuture<Void> batchCompletableFutureArr[] = new CompletableFuture[batchCompletableFutures.size()];
        CompletableFuture<Void> batchAllCDSFutures = CompletableFuture
                .allOf(batchCompletableFutures.toArray(batchCompletableFutureArr));

        try {
            batchAllCDSFutures.get();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("ACCOUNT WRITER JOBS FINISHED");

    }
}
