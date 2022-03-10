package datastax.com;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.delete.Delete;


import java.time.Duration;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public class IDAssignment {
    private CqlSession session;
    private String applicationID;

    private final int RETRY_LIMIT = 3; //todo make input parameter

    private String keyspace;
    private String availableTableName;
    private String assignmentTableName;

    private PreparedStatement addAvailableIdStmt;
    private PreparedStatement addAssignmentIdStmt;
    private PreparedStatement getAvailbleIdsStmt;
    private PreparedStatement assignIdStmt;
    private PreparedStatement removeAvailableIdStmt;

    private final String UNASSIGNED = "unassigned";
    private final String DOMAIN_PARAM = "domParam";
    private final String ID_PARAM = "idParam";
    private final String ASSIGN_PARAM = "assignParam";
    private final String SIZE_LIMIT_PARAM = "sizeParam";

    IDAssignment(CqlSession session, String keyspace, String availbleTable, String assignTable, String appID){
        this.session = session;
        this.keyspace = keyspace;
        this.availableTableName = availbleTable;
        this.assignmentTableName = assignTable;
        this.applicationID = appID;

        //create prepared statemens using supplied parameters
        initializeStatements();
    }

    private void initializeStatements(){

        SimpleStatement addAvailableStmt =  insertInto(keyspace, availableTableName)
                .value("domain", bindMarker(DOMAIN_PARAM))
                .value("identifier", bindMarker(ID_PARAM))
                .build();
        addAvailableIdStmt = session.prepare(addAvailableStmt);

        SimpleStatement addAssignStmt =  insertInto(keyspace, assignmentTableName)
                .value("domain", bindMarker(DOMAIN_PARAM))
                .value("identifier", bindMarker(ID_PARAM))
                .value("assigned_by", literal(UNASSIGNED))
                .build();
        addAssignmentIdStmt = session.prepare(addAssignStmt);

        SimpleStatement getAvailableStmt = selectFrom(keyspace, availableTableName)
                .column("identifier")
                .whereColumn("domain").isEqualTo(bindMarker(DOMAIN_PARAM))
                .limit(bindMarker(SIZE_LIMIT_PARAM))
                .build();
        getAvailbleIdsStmt = session.prepare(getAvailableStmt);

        SimpleStatement lwtUpdateAssign =  update(keyspace, assignmentTableName)
                .setColumn("assigned_by", bindMarker(ASSIGN_PARAM)) //todo use appID member varialbe for assignment?
                .whereColumn("domain").isEqualTo(bindMarker(DOMAIN_PARAM))
                .whereColumn("identifier").isEqualTo(bindMarker(ID_PARAM))
                .ifColumn("assigned_by").isEqualTo(literal(UNASSIGNED))
                .build();
//        String lwtUpdateAssignmentCQL =
//                "UPDATE " + keyspace + "." + assignmentTableName + " SET assigned_by = ? WHERE domain = ? AND identifier = ? if assigned_by = " + UNASSIGNED;
        assignIdStmt = session.prepare(lwtUpdateAssign);

        SimpleStatement deleteAvailable =  deleteFrom(keyspace, availableTableName)
                .whereColumn("domain").isEqualTo(bindMarker(DOMAIN_PARAM))
                .whereColumn("identifier").isEqualTo(bindMarker(ID_PARAM))
                .build();
        removeAvailableIdStmt = session.prepare(deleteAvailable);


//        String lwtUpdateCurNumberCQL = "UPDATE " + keyspace + "." + tableName + " SET current_num = ? WHERE domain = ? AND sequence_name = ? if current_num = ?";
//
//        getCurNumberStmt = session.prepare(getCurNumberCQL);
//        lwdUpdateCurNumberStmt = session.prepare(lwtUpdateCurNumberCQL);
    }

    //method to set customized configuration parameters when executing LWT
    private ResultSet executeLwtStatement(BoundStatement statement){
        return session.execute(statement
                .setSerialConsistencyLevel(ConsistencyLevel.LOCAL_SERIAL)
                .setTimeout(Duration.ofSeconds(15L))
        );
    }

    public boolean addAvailableId(String domain, String id){
        BatchStatement batch = BatchStatement.builder(BatchType.LOGGED).build();

        BoundStatement assignBndStmt =  addAssignmentIdStmt.bind()
                .setString(DOMAIN_PARAM, domain)
                .setString(ID_PARAM, id);
//        session.execute(assignBndStmt);
        batch = batch.add(assignBndStmt);

        BoundStatement availableBndStmt = addAvailableIdStmt.bind()
                .setString(DOMAIN_PARAM, domain)
                .setString(ID_PARAM, id);
        batch = batch.add(availableBndStmt);
//        session.execute(availableBndStmt);

        session.executeAsync(batch);

        return true; //todo use batch, verify applied
    }

}
