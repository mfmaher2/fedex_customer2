package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import datastax.com.DAOs.*;

@Mapper
public interface CustomerMapper {

    @DaoFactory
    ContactDao contactDao(@DaoKeyspace String keyspace);

    @DaoFactory
    PaymentInfoDao paymentInfoDao(@DaoKeyspace String keyspace);

    @DaoFactory
    AccountDao accountDao(@DaoKeyspace String keyspace);

    @DaoFactory
    AssocAccountDao assocAccountDao(@DaoKeyspace String keyspace);

    @DaoFactory
    NationalAccountDao nationalAccountDao(@DaoKeyspace String keyspace);

    @DaoFactory
    ApplyDiscountDao applyDiscountDao(@DaoKeyspace String keyspace);

    @DaoFactory
    IndexCollectionDao indexCollectionDao(@DaoKeyspace String keyspace);

    @DaoFactory
    CommentDao commentDao(@DaoKeyspace String keyspace);

    @DaoFactory
    AuditHistoryDao auditHistoryDao(@DaoKeyspace String keyspace);

//    @DaoFactory
//    AccountContactDao accountContactDao(@DaoKeyspace String keyspace); //todo - test with enabled, two instances of DAO with different mappers
}
