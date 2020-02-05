package com.yappyapps.spotlight.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.yappyapps.spotlight.domain.PaymentTransaction;

/**
 * The IPaymentTransactionRepository interface provides the CRUD operations on PaymentTransaction domain
 * 
 * <h1>@Repository</h1> will enable it to include all the CRUD operations.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Repository
public interface IPaymentTransactionRepository extends CrudRepository<PaymentTransaction, Integer> {



}
