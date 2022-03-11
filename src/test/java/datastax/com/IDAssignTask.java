package datastax.com;

import lombok.SneakyThrows;

import java.util.*;

public class IDAssignTask implements Runnable {

//    protected int startIndex=0;
    protected IDAssignment assignmentHandler;
    protected String domain;
    protected int blockCount=10;
    protected int maxBlockSize=10;
    protected Map<String, Set<String>> mapAssignedIDs;

    public IDAssignTask(/*int start,*/ IDAssignment assigner, String domain, int numBlocks, int maxBlockCount, Map<String, Set<String>> mapIDs){
//        this.startIndex = start;
        this.assignmentHandler = assigner;
        this.domain = domain;
        this.blockCount = numBlocks;
        this.maxBlockSize = maxBlockCount;
        this.mapAssignedIDs = mapIDs;
    }

    @SneakyThrows
    @Override
    public void run(){
        Random random= new Random();
        Set<String> ids = new HashSet<>();
        String threadName = Thread.currentThread().getName() ;

        for(int i = 0; i< blockCount; i++){
//            ids.add(String.valueOf(i));
            System.out.println("AssignThread: name-" + threadName + ",   iteration #" + i);

            List<String> assignedIDs =  assignmentHandler.assignAvailableIds(threadName, domain, 1);
            assignedIDs.forEach(assigned -> ids.add(assigned));

            Thread.sleep(random.nextInt(100));
        }

        mapAssignedIDs.put(threadName, ids);
    }
}
