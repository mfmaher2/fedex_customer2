package datastax.com.DAOs;

import com.datastax.oss.driver.api.mapper.annotations.*;
import datastax.com.dataObjects.PaymentInfo;

import java.util.concurrent.CompletableFuture;

@Dao
public interface PaymentInfoDao {

    @Select
    PaymentInfo findByAccountNumber(String acct);

    @Select
    CompletableFuture<PaymentInfo> findByAccountNumberAsync(String acct);

//    @Insert
//    void save(CustomerPaymentInfo payInfo);
//
//    @Update
//    void update(CustomerPaymentInfo payInfo);
//
//    @Delete
//    void delete(CustomerPaymentInfo payInfo);
}
