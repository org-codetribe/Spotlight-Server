package com.yappyapps.spotlight.service;

import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.util.Payment;

/**
 * The IPaymentService interface declares all the operations to act upon Payment
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
public interface IPaymentService {

    /**
     * This method is used to get all Payment.
     *
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    public String getAccessToken(String viewerId) throws ResourceNotFoundException, BusinessException, Exception;

    public String getTransactionStatus(String transactionId) throws ResourceNotFoundException, BusinessException, Exception;

    public Result<Transaction> paymentTransaction(Payment payment) throws ResourceNotFoundException, BusinessException, Exception;

    public Result<Transaction> addWalletPaymentTransaction(Payment payment) throws ResourceNotFoundException, BusinessException, Exception;


    public String couponTransaction(Payment payment) throws ResourceNotFoundException, BusinessException, Exception;

    public Result<Transaction> purchaseCoupon(Payment payment) throws ResourceNotFoundException, BusinessException, Exception;

}