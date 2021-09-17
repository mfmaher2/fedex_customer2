package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface CustomerMapper {

    @DaoFactory
    ContactDao customerContactDao(@DaoKeyspace String keyspace);

    @DaoFactory
    PaymentInfoDao customerPaymentInfoDao(@DaoKeyspace String keyspace);

    @DaoFactory
    AccountDao customerAccountDao(@DaoKeyspace String keyspace);

    @DaoFactory
    AssocAccountDao customerAssocAccountDao(@DaoKeyspace String keyspace);

    @DaoFactory
    NationalAccountDao customerNationalAccountDao(@DaoKeyspace String keyspace);

    @DaoFactory
    ApplyDiscountDao customerApplyDiscountDao(@DaoKeyspace String keyspace);
}
