package datastax.com;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;

public class IDAssignmentSingleTbl {
    private CqlSession session;
    private final int RETRY_LIMIT = 3; //todo make input parameter

    private String keyspace;
    private String assignmentTableName;

    private PreparedStatement addAssignmentIdStmt;
    private PreparedStatement getAvailbleIdsStmt;
    private PreparedStatement assignIdStmt;
    private PreparedStatement checkAssignmentStmt;

    private final String UNASSIGNED = "unassigned";
    private final String DOMAIN_PARAM = "domParam";
    private final String ID_PARAM = "idParam";
    private final String ASSIGN_PARAM = "assignParam";
    private final String SIZE_LIMIT_PARAM = "sizeParam";

    IDAssignmentSingleTbl(CqlSession session, String keyspace, String assignTable){
        this.session = session;
        this.keyspace = keyspace;
        this.assignmentTableName = assignTable;

        //create prepared statemens using supplied parameters
        initializeStatements();
    }

    private void initializeStatements(){
        SimpleStatement addAssignStmt =  insertInto(keyspace, assignmentTableName)
                .value("domain", bindMarker(DOMAIN_PARAM))
                .value("identifier", bindMarker(ID_PARAM))
                .value("assigned_by", literal(UNASSIGNED))
                .build();
        addAssignmentIdStmt = session.prepare(addAssignStmt);

        SimpleStatement getAvailableStmt = selectFrom(keyspace, assignmentTableName)
                .column("identifier")
                .whereColumn("domain").isEqualTo(bindMarker(DOMAIN_PARAM))
                .whereColumn("assigned_by").isEqualTo(literal(UNASSIGNED))
                .limit(bindMarker(SIZE_LIMIT_PARAM))
                .allowFiltering()
                .build();
        getAvailbleIdsStmt = session.prepare(getAvailableStmt);

        ///example if use bucket
//        Random random = new Random();
//        SimpleStatement getAvailableBucketedStmt = selectFrom(keyspace, assignmentTableName)
//                .column("identifier")
//                .whereColumn("domain").isEqualTo(bindMarker(DOMAIN_PARAM))
//                .whereColumn("bucket").isEqualTo(literal(random.nextInt(1000)))
//                .whereColumn("assigned_by").isEqualTo(literal(UNASSIGNED))
//                .limit(bindMarker(SIZE_LIMIT_PARAM))
//                .allowFiltering()
//                .build();

        SimpleStatement lwtUpdateAssign =  update(keyspace, assignmentTableName)
                .setColumn("assigned_by", bindMarker(ASSIGN_PARAM)) //todo use appID member varialbe for assignment?
                .whereColumn("domain").isEqualTo(bindMarker(DOMAIN_PARAM))
                .whereColumn("identifier").isEqualTo(bindMarker(ID_PARAM))
                .ifColumn("assigned_by").isEqualTo(literal(UNASSIGNED))
                .build();
        assignIdStmt = session.prepare(lwtUpdateAssign);

        SimpleStatement getAssigned = selectFrom(keyspace, assignmentTableName)
                .column("assigned_by")
                .whereColumn("domain").isEqualTo(bindMarker(DOMAIN_PARAM))
                .whereColumn("identifier").isEqualTo(bindMarker(ID_PARAM))
                .build();
        checkAssignmentStmt = session.prepare(getAssigned);
    }

    //method to set customized configuration parameters when executing LWT
    private ResultSet executeLwtStatement(BoundStatement statement){
        return session.execute(statement
                .setSerialConsistencyLevel(ConsistencyLevel.LOCAL_SERIAL)
                .setTimeout(Duration.ofSeconds(15L))
        );
    }

    public boolean addAvailableId(String domain, String id){
        BoundStatement assignBndStmt =  addAssignmentIdStmt.bind()
                .setString(DOMAIN_PARAM, domain)
                .setString(ID_PARAM, id);
        session.executeAsync(assignBndStmt);
        return true; //todo use batch, verify applied
    }

    public List<String> assignAvailableIds(String applicationID, String domain, int numIdsRequired){
        List<String> assignedIds = new ArrayList<>();

        List<String> availableIds = getAvailableIds(domain, numIdsRequired);
        Iterator<String> availableIter = availableIds.iterator();

        String candidateId = "";
        int retryCount;
        boolean curIdAssigned;
        for(int curRequiredID=0; curRequiredID<numIdsRequired; curRequiredID++){

            retryCount=0;
            curIdAssigned = false;

            while(!curIdAssigned && (retryCount<RETRY_LIMIT)) {
                if (availableIter.hasNext()) {
                    candidateId = availableIter.next();
                }
                else{
                    //todo - more complicate/robust logic may be needed (retry limit, checking newly retrieved ID set etc.)
                    List<String> additionaAvailableIds = getAvailableIds(domain, numIdsRequired);
                    availableIter = additionaAvailableIds.iterator();
                    candidateId = availableIter.next();
                }

                //attempt assignment using conditional statement
                BoundStatement assignBndStmt = assignIdStmt.bind()
                        .setString(ASSIGN_PARAM, applicationID)
                        .setString(DOMAIN_PARAM, domain)
                        .setString(ID_PARAM, candidateId);

                if (executeLwtStatement(assignBndStmt).wasApplied()) {
                    //verify appID row value is set to local value
                    BoundStatement verifyBndStmt = checkAssignmentStmt.bind()
                            .setString(DOMAIN_PARAM, domain)
                            .setString(ID_PARAM, candidateId);
                    Row rowVerify = session.execute(verifyBndStmt).one();

                    if (rowVerify.getString("assigned_by").equals(applicationID)) {
                        assignedIds.add(candidateId);
                        curIdAssigned = true;
                    } else {
                        //assignment not successful
                        //another process assigned ID, need to retry with next available
                        retryCount++;
                    }
                }
                else{
                    //assignment not successful
                    //another process assigned ID, need to retry with next available
                    retryCount++;
                }
            }

            //todo check retry count, output error if retries exceeded
            if(retryCount >= RETRY_LIMIT){
                System.out.println("ERROR - not all requested IDs assigned");
            }
        }

        return assignedIds;
    }

    private List<String> getAvailableIds(String domain, int numIDs){
        //todo - use a set to avoid duplicates?
        //todo - add bucket as clustering key (~random int) to prevent wide partitions
        BoundStatement availIDsBndStmt = getAvailbleIdsStmt.bind()
                .setString(DOMAIN_PARAM, domain)
                .setInt(SIZE_LIMIT_PARAM, ((numIDs > 0) ? numIDs: 1) * 3);  //todo ?? - retrieve numIDs x (factor) to get more than needed, possibly avoid additional retrievals if some already assigned ??

        return session.execute(availIDsBndStmt).all().stream()
                .map(row->row.getString("identifier"))
                .collect(Collectors.toList());
    }
}
