package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface CustomerMapper {

    @DaoFactory
    CustomerContactDao customerContactDao(@DaoKeyspace String keyspace);

    @DaoFactory
    CustomerPaymentInfoDao customerPaymentInfoDao(@DaoKeyspace String keyspace);

    @DaoFactory
    CustomerAccountDao customerAccountDao(@DaoKeyspace String keyspace);

    @DaoFactory
    CustomerAssocAccountDao customerAssocAccountDao(@DaoKeyspace String keyspace);

    @DaoFactory
    CustomerNationalAccountDao customerNationalAccountDao(@DaoKeyspace String keyspace);

    @DaoFactory
    CustomerApplyDiscountDao customerApplyDiscountDao(@DaoKeyspace String keyspace);
}
