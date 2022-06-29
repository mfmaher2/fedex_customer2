//package datastax.com;
//
//import java.util.ArrayList;
//import java.util.concurrent.CompletableFuture;
//import java.util.stream.IntStream;
//
//public class CodeSamples {
//    int threadAmount = 4;
//
//    ArrayList<CompletableFuture<Void>> batchCompletableFutures = new ArrayList<CompletableFuture<Void>>();
//        IntStream .range(0, threadAmount).forEach(value -> {
//        Service hessianService = ctx.getBean(Service.class);
//        String fullFileName = fileName + ".0" + value;
//
//        if(!new File(fullFileName).exists()){
//            logger.error("File " + fullFileName + " does not exist in current path (" + System.getProperty("user.dir") + ")");
//            System.exit(1);
//        }
//
//        System.out.println("FOUND FILE : " + fullFileName);
//
//        try {
//
//            Runnable worker = new DataStaxAccount(fullFileName, blockSize, futuresBlockSize, value, hessianService, session,jAXBMarshaller.extractUnmarshaller(),jAXBMarshaller.extractMarshaller());
//            batchCompletableFutures.add(CompletableFuture.runAsync(worker));
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//
//    });
//
//    @SuppressWarnings("unchecked")
//    CompletableFuture<Void> batchCompletableFutureArr[] = new CompletableFuture[batchCompletableFutures.size()];
//    CompletableFuture<Void> batchAllCDSFutures = CompletableFuture
//            .allOf(batchCompletableFutures.toArray(batchCompletableFutureArr));
//
//        try {
//
//        batchAllCDSFutures.get();
//
//    } catch (InterruptedException e) {
//        // TODO Auto-generated catch block
//        e.printStackTrace();
//
//    } catch (ExecutionException e) {
//        // TODO Auto-generated catch block
//        e.printStackTrace();
//    }
//
//        System.out.println("JOBS HAVE FINISHED");
//}
//
//
//public DataStaxAccount(String filename, int blockSize, int futuresBlockSize, int threadNumber,
//        Service hessianService, CqlSession session, Unmarshaller unmarshaller,
//        Marshaller marshaller) {
//        this.blockSize = blockSize;
//        this.threadNumber = threadNumber;
//        this.filename = filename;
//        this.hessianService = hessianService;
//        this.futuresBlockSize = futuresBlockSize;
//        this.unmarshaller = unmarshaller;
//        this.marshaller = marshaller;
//
//        customerAccountDao = new CustomerMapperBuilder(session).build()
//                                    .customerAccountDao(Keyspaces.ACCOUNT_KS.keyspaceName(datastax_app_level, datastax_app_name));
//        customerPaymentInfoDao = new CustomerMapperBuilder(session).build()
//        .customerPaymentInfoDao(Keyspaces.PAYMENT_INFO_KS.keyspaceName(datastax_app_level, datastax_app_name));
//        customerAssocAccountDao = new CustomerMapperBuilder(session).build().customerAssocAccountDao(
//        Keyspaces.ASSOC_ACCOUNT_KS.keyspaceName(datastax_app_level, datastax_app_name));
//
//        //this.customerAccountDao = new CustomerMapperBuilder(session).build().customerAccountDao(Keyspaces.ACCOUNT_KS.keyspaceName());
//        //this.customerPaymentInfoDao = new CustomerMapperBuilder(session).build().customerPaymentInfoDao(Keyspaces.PAYMENT_INFO_KS.keyspaceName());
//        //this.customerAssocAccountDao = new CustomerMapperBuilder(session).build().customerAssocAccountDao(Keyspaces.ASSOC_ACCOUNT_KS.keyspaceName());
//
//
//        this.session = session;
//
//        }