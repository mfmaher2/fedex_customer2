package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import datastax.com.DAOs.AccountContactDao;

@Mapper
public interface CustomerMapperEdge {
    @DaoFactory
    AccountContactDao accountContactDao(@DaoKeyspace String keyspace);
}
