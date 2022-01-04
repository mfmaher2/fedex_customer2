package datastax.com;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.Statement;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

import org.junit.Test;

public class JSONTests {
    // A codec to convert JSON payloads into User instances;
    private static final TypeCodec<User> USER_CODEC = new JacksonJsonCodec<>(User.class);

//    public static void main(String[] args) {
    @Test
    public void sampleTest(){
        try (CqlSession session = CqlSession.builder().addTypeCodecs(USER_CODEC).build()) {
            createSchema(session);
            insertJsonColumn(session);
            selectJsonColumn(session);
        }
    }

    private static void createSchema(CqlSession session) {
        session.execute(
                "CREATE KEYSPACE IF NOT EXISTS examples "
                        + "WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
        session.execute(
                "CREATE TABLE IF NOT EXISTS examples.json_jackson_column("
                        + "id int PRIMARY KEY, json text)");
    }

    // Mapping a User instance to a table column
    private static void insertJsonColumn(CqlSession session) {

        User alice = new User("alice", 30);
        User bob = new User("bob", 35);

        // Build and execute a simple statement

        Statement stmt =
                insertInto("examples", "json_jackson_column")
                        .value("id", literal(1))
                        // the User object will be converted into a String and persisted into the VARCHAR column
                        // "json"
                        .value("json", literal(alice, session.getContext().getCodecRegistry()))
                        .build();
        session.execute(stmt);

        // The JSON object can be a bound value if the statement is prepared
        // (subsequent calls to the prepare() method will return cached statement)
        PreparedStatement pst =
                session.prepare(
                        insertInto("examples", "json_jackson_column")
                                .value("id", bindMarker("id"))
                                .value("json", bindMarker("json"))
                                .build());
        session.execute(pst.bind().setInt("id", 2).set("json", bob, User.class));
    }

    // Retrieving User instances from a table column
    private static void selectJsonColumn(CqlSession session) {

        Statement stmt =
                selectFrom("examples", "json_jackson_column")
                        .all()
                        .whereColumn("id")
                        .in(literal(1), literal(2))
                        .build();

        ResultSet rows = session.execute(stmt);

        for (Row row : rows) {
            int id = row.getInt("id");
            // retrieve the JSON payload and convert it to a User instance
            User user = row.get("json", User.class);
            // it is also possible to retrieve the raw JSON payload
            String json = row.getString("json");
            System.out.printf(
                    "Retrieved row:%n id           %d%n user         %s%n user (raw)   %s%n%n",
                    id, user, json);
        }
    }

//    @SuppressWarnings("unused")
    public static class User {

        private final String name;

        private final int age;

        @JsonCreator
        public User(@JsonProperty("name") String name, @JsonProperty("age") int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return String.format("%s (%s)", name, age);
        }
    }
}
