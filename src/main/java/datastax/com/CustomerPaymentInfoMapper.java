package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface CustomerPaymentInfoMapper {

    @DaoFactory
    CustomerPaymentInfoDao customerPaymentInfoDao(@DaoKeyspace String keyspace);
}
