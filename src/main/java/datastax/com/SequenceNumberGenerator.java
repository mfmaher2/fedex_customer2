package datastax.com;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.session.Session;

public class SequenceNumberGenerator {

    private CqlSession session;
    private int blockSize;
    private int repeatCount;

    SequenceNumberGenerator(CqlSession session) {
        this.session = session;
    };

    SequenceNumberGenerator(CqlSession session, int blockSize, int repeatCount) {

        this.session = session;
    };

}
