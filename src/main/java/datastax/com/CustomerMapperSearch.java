package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import datastax.com.DAOs.CAMSearchDao;

@Mapper
public interface CustomerMapperSearch {
    @DaoFactory
    CAMSearchDao camSearchDao(@DaoKeyspace String keyspace);
}
