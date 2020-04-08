package com.yappyapps.spotlight.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.yappyapps.spotlight.domain.*;
import com.yappyapps.spotlight.repository.*;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.CreditCard;
import com.braintreegateway.Customer;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.Transaction.Status;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationError;
import com.yappyapps.spotlight.domain.helper.CouponHelper;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.IPaymentService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Payment;
import com.yappyapps.spotlight.util.Utils;

/**
 * The PaymentService class is the implementation of IPaymentService
 *
 * <h1>@Service</h1> denotes that it is a service class *
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Service
public class PaymentService implements IPaymentService {
    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    /**
     * Utils dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private Utils utils;

    /**
     * This is dependency injected from the BraintreeConfig class
     */
    @Autowired
    private BraintreeGateway gateway;

    /**
     * ICouponRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private ICouponRepository couponRepository;

    /**
     * ICouponConsumptionRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private ICouponConsumptionRepository couponConsumptionRepository;

    /**
     * IViewerEventRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IViewerEventRepository viewerEventRepository;

    /**
     * IEventRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IEventRepository eventRepository;

    /**
     * IPaymentTransactionRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IPaymentTransactionRepository paymentTransactionRepository;

    /**
     * ICouponPaymentTransactionRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private ICouponPaymentTransactionRepository couponPaymentTransactionRepository;

	@Autowired
	private IWalletRepository walletRepository;


	/**
     * IViewerRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IViewerRepository viewerRepository;

    /**
     * IBroadcasterInfoRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IBroadcasterInfoRepository broadcasterInfoRepository;

    /**
     * CouponHelper dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private CouponHelper couponHelper;

    /**
     * Successful status codes from credit card processing
     */
    private Status[] TRANSACTION_SUCCESS_STATUSES = new Status[]{
            Transaction.Status.AUTHORIZED,
            Transaction.Status.AUTHORIZING,
            Transaction.Status.SETTLED,
            Transaction.Status.SETTLEMENT_CONFIRMED,
            Transaction.Status.SETTLEMENT_PENDING,
            Transaction.Status.SETTLING,
            Transaction.Status.SUBMITTED_FOR_SETTLEMENT
    };

    public String getAccessToken(String viewerId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = "";

        ClientTokenRequest clientTokenRequest = new ClientTokenRequest();
//				  .customerId(viewerId);
        String clientToken = gateway.clientToken().generate(clientTokenRequest);

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.BRAINTREE_TOKEN, clientToken);
//		result = utils.constructSucessJSON(jObj);
        result = jObj.toString();

        return result;
    }


    public String getTransactionStatus(String transactionId) throws ResourceNotFoundException, BusinessException, Exception {
        String result = "";

        Transaction transaction;
        CreditCard creditCard;
        Customer customer;

        try {
            //find the transaction by its ID
            transaction = gateway.transaction().find(transactionId);

            //grab credit card info
            creditCard = transaction.getCreditCard();

            //grab the customer info
            customer = transaction.getCustomer();
        } catch (Exception e) {
            throw new BusinessException("We could not get the status of transaction. Please try again later");
        }

        JSONObject jObj = new JSONObject();
        jObj.put("isSuccess", Arrays.asList(TRANSACTION_SUCCESS_STATUSES).contains(transaction.getStatus()));
        jObj.put("transaction", transaction);
        jObj.put("creditCard", creditCard);
        jObj.put("customer", customer);
        result = utils.constructSucessJSON(jObj);

        return result;
    }

    @Transactional
    public Result<Transaction> paymentTransaction(Payment payment) throws ResourceNotFoundException, BusinessException, Exception {
        String result = "";

//        BigDecimal decimalAmount = new BigDecimal(0);
//        try {
//            //get the decimal version of the text entered
//            decimalAmount = new BigDecimal(payment.getChargeAmount());
//        } catch (NumberFormatException e) {
//        	LOGGER.error("NumberFormatException :::: " + e.getMessage());
//        	throw new InvalidParameterException("Amount should be BigDecimal.");
//        }

        Optional<Event> eventEntity = eventRepository.findById(payment.getEvent().getId());
        if (!eventEntity.isPresent())
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

        Long soldSeats = viewerEventRepository.countByEvent(eventEntity.get());

        long remainingSeats = eventEntity.get().getTotalSeats() - soldSeats;

        if (remainingSeats <= 0)
            throw new InvalidParameterException("Tickets for the Event is already Sold.");


        Optional<Viewer> viewerEntity = viewerRepository.findById(payment.getViewer().getId());
        if (!viewerEntity.isPresent())
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

        Boolean eventPurchased = viewerEventRepository.existsByEventAndViewer(eventEntity.get(), viewerEntity.get());

        if (eventPurchased) {
            throw new AlreadyExistException("Viewer has already purchased the specified Event.");
        }


        //submit the request for processing
        TransactionRequest request = new TransactionRequest()
                .amount(payment.getChargeAmount())
                .paymentMethodNonce(payment.getNonce())
                .options()
                .submitForSettlement(true)
                .done();

        //get the response
        Result<Transaction> transactionResult = gateway.transaction().sale(request);


        ////TODO save event for viewer

        Transaction transaction = null;
        if (transactionResult.isSuccess()) {
            transaction = transactionResult.getTarget();
        } else if (transactionResult.getTransaction() != null) {
            transaction = transactionResult.getTransaction();
        } else {
            //if the transaction failed, return to the payment page and display all errors
            String errorString = "";
            for (ValidationError error : transactionResult.getErrors().getAllDeepValidationErrors()) {
                errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";
            }
            throw new Exception(errorString);
        }

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        ViewerEvent viewerEvent = new ViewerEvent();
        viewerEvent.setEvent(eventEntity.get());
        viewerEvent.setPurchaseDatetime(currentTime);
        viewerEvent.setPurchaseMethod("Payment");
        viewerEvent.setStatus(IConstants.DEFAULT_STATUS);
        viewerEvent.setViewer(viewerEntity.get());

        String viewerChatAuthKey = utils.generateRandomString(32);

        List<String> authList = new ArrayList<>();
        authList.add(viewerChatAuthKey);
        try {
            PubNubService pns = new PubNubService();
            pns.grantPermissions(authList, eventEntity.get().getUniqueName(), true, true, false);

        } catch (Exception e) {
            LOGGER.error("ERROR in grantpermissions :::: " + e.getMessage());
        }


        viewerEvent.setViewerChatAuthKey(viewerChatAuthKey);

        try {

            viewerEvent = viewerEventRepository.save(viewerEvent);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setAmount(eventEntity.get().getActualPrice());
        paymentTransaction.setEvent(eventEntity.get());
        paymentTransaction.setPaymentDatetime(currentTime);
        paymentTransaction.setPaymentMethod("BRAINTREE");
        paymentTransaction.setPaymentType("Credit");
        paymentTransaction.setStatus(IConstants.DEFAULT_STATUS);
        paymentTransaction.setTransactionId(transaction.getId());
        paymentTransaction.setViewer(viewerEntity.get());

        try {

            paymentTransaction = paymentTransactionRepository.save(paymentTransaction);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		JSONObject jObj = new JSONObject();
//		jObj.put(IConstants.TRANSACTION, transactionResult.toString());
//		result = utils.constructSucessJSON(jObj);


        return transactionResult;
    }

    @Transactional
    public String couponTransaction(Payment payment) throws ResourceNotFoundException, BusinessException, Exception {
        String result = "";

        Coupon coupon = couponRepository.findByNumber(payment.getCouponCode());

        if (coupon == null)
            throw new ResourceNotFoundException("Coupon " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);

        if (coupon.getEvent().getId().intValue() != payment.getEvent().getId().intValue()) {
            throw new ResourceNotFoundException("Coupon doesn't belong to Event.");
        }

        Optional<Event> eventEntity = eventRepository.findById(payment.getEvent().getId());
        if (!eventEntity.isPresent())
            throw new ResourceNotFoundException("Event " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);

        Long soldSeats = viewerEventRepository.countByEvent(eventEntity.get());

        long remainingSeats = eventEntity.get().getTotalSeats() - soldSeats;

        if (remainingSeats <= 0)
            throw new InvalidParameterException("Tickets for the Event is already Sold.");

        Optional<Viewer> viewerEntity = viewerRepository.findById(payment.getViewer().getId());
        if (!viewerEntity.isPresent())
            throw new ResourceNotFoundException("Viewer " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);

//		CouponConsumption couponConsumptionEntity = couponConsumptionRepository.findByCoupon(coupon);

        Long couponConsumptionCount = couponConsumptionRepository.countByCoupon(coupon);

        if (couponConsumptionCount != null && couponConsumptionCount > 0 && coupon.getType().equalsIgnoreCase("Single"))
            throw new AlreadyExistException("Coupon has already been redeemed");

        if (couponConsumptionCount != null && couponConsumptionCount > 0 && coupon.getType().equalsIgnoreCase("Multi") && (Integer.parseInt(couponConsumptionCount + "") >= coupon.getRedemptionLimit()))
            throw new AlreadyExistException("Coupon Redemption Limit has already been reached");

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        CouponConsumption couponConsumption = new CouponConsumption();
        couponConsumption.setCoupon(coupon);
        couponConsumption.setCreatedOn(currentTime);
        couponConsumption.setEvent(eventEntity.get());
        couponConsumption.setViewer(viewerEntity.get());

        try {

            couponConsumption = couponConsumptionRepository.save(couponConsumption);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        ViewerEvent viewerEvent = new ViewerEvent();
        viewerEvent.setEvent(eventEntity.get());
        viewerEvent.setPurchaseDatetime(currentTime);
        viewerEvent.setPurchaseMethod("COUPON");
        viewerEvent.setStatus(IConstants.DEFAULT_STATUS);
        viewerEvent.setViewer(viewerEntity.get());
        String viewerChatAuthKey = utils.generateRandomString(32);

        List<String> authList = new ArrayList<>();
        authList.add(viewerChatAuthKey);
        try {
            PubNubService pns = new PubNubService();
            pns.grantPermissions(authList, eventEntity.get().getUniqueName(), true, true, false);

        } catch (Exception e) {
            LOGGER.error("ERROR in grantpermissions :::: " + e.getMessage());
        }


        viewerEvent.setViewerChatAuthKey(viewerChatAuthKey);
        try {

            viewerEvent = viewerEventRepository.save(viewerEvent);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setAmount(eventEntity.get().getActualPrice());
        paymentTransaction.setEvent(eventEntity.get());
        paymentTransaction.setPaymentDatetime(currentTime);
        paymentTransaction.setPaymentMethod("COUPON");
        paymentTransaction.setPaymentType("Credit");
        paymentTransaction.setStatus(IConstants.DEFAULT_STATUS);
        paymentTransaction.setTransactionId(utils.generateRandomString(64));
        paymentTransaction.setViewer(viewerEntity.get());

        try {

            paymentTransaction = paymentTransactionRepository.save(paymentTransaction);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
//		jObj.put(IConstants.TRANSACTION, payment.g);
        result = utils.constructSucessJSON(jObj);


        return result;
    }


    @Transactional
    public Result<Transaction> purchaseCoupon(Payment payment) throws ResourceNotFoundException, BusinessException, Exception {
        String result = "";

        Optional<Event> eventEntity = eventRepository.findById(payment.getEvent().getId());
        if (!eventEntity.isPresent())
            throw new ResourceNotFoundException("Event " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);

        Optional<BroadcasterInfo> broadcasterInfoEntity = broadcasterInfoRepository.findById(payment.getBroadcasterInfo().getId());
        if (!broadcasterInfoEntity.isPresent())
            throw new ResourceNotFoundException("Broadcaster " + IConstants.RESOURCE_NOT_FOUND_MESSAGE);


        //submit the request for processing
        TransactionRequest request = new TransactionRequest()
                .amount(payment.getChargeAmount())
                .paymentMethodNonce(payment.getNonce())
                .options()
                .submitForSettlement(true)
                .done();

        //get the response
        Result<Transaction> transactionResult = gateway.transaction().sale(request);


        ////TODO save event for viewer

        Transaction transaction = null;
        if (transactionResult.isSuccess()) {
            transaction = transactionResult.getTarget();
        } else if (transactionResult.getTransaction() != null) {
            transaction = transactionResult.getTransaction();
        } else {
            //if the transaction failed, return to the payment page and display all errors
            String errorString = "";
            for (ValidationError error : transactionResult.getErrors().getAllDeepValidationErrors()) {
                errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";
            }
            throw new Exception(errorString);
        }

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Coupon couponEntity = null;
        Coupon couponReqObj = payment.getCoupon();

        if (couponReqObj.getCount() != null) {
            couponReqObj.setRedemptionLimit(1);
        } else {
            couponReqObj.setCount(1);
        }
        for (int i = 0; i < couponReqObj.getCount(); i++) {
            couponEntity = couponHelper.populateCoupon(couponReqObj);
            couponEntity = couponRepository.save(couponEntity);
        }

//        ViewerEvent viewerEvent = new ViewerEvent();
//		viewerEvent.setEvent(eventEntity.get());
//		viewerEvent.setPurchaseDatetime(currentTime);
//		viewerEvent.setPurchaseMethod("Payment");
//		viewerEvent.setStatus(IConstants.DEFAULT_STATUS);
//		viewerEvent.setViewer(broadcasterInfoEntity.get());
//		
//		String viewerChatAuthKey = utils.generateRandomString(32);
//		
//		List<String> authList = new ArrayList<>();
//		authList.add(viewerChatAuthKey);
//		try {
//			PubNubService pns = new PubNubService();
//			pns.grantPermissions(authList, eventEntity.get().getUniqueName(), true, true, false);
//
//		} catch (Exception e) {
//			LOGGER.error("ERROR in grantpermissions :::: " + e.getMessage());
//		}
//		
//
//		viewerEvent.setViewerChatAuthKey(viewerChatAuthKey);
//
//		try {
//			
//			viewerEvent = viewerEventRepository.save(viewerEvent);
//		} catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
//			throw new Exception(sqlException.getMessage());
//		} catch (HibernateException | JpaSystemException sqlException) {
//			throw new Exception(sqlException.getMessage());
//		}

        CouponPaymentTransaction couponPaymentTransaction = new CouponPaymentTransaction();
        couponPaymentTransaction.setAmount(eventEntity.get().getActualPrice());
        couponPaymentTransaction.setEvent(eventEntity.get());
        couponPaymentTransaction.setPaymentDatetime(currentTime);
        couponPaymentTransaction.setPaymentMethod("BRAINTREE");
        couponPaymentTransaction.setPaymentType("Credit");
        couponPaymentTransaction.setStatus(IConstants.DEFAULT_STATUS);
        couponPaymentTransaction.setTransactionId(transaction.getId());
        couponPaymentTransaction.setBroadcasterInfo(broadcasterInfoEntity.get());

        try {

            couponPaymentTransaction = couponPaymentTransactionRepository.save(couponPaymentTransaction);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }


        return transactionResult;
    }


    @Transactional
    public Result<Transaction> addWalletPaymentTransaction(Payment payment) throws ResourceNotFoundException, BusinessException, Exception {
        String result = "";
        Optional<Viewer> viewerEntity = viewerRepository.findById(payment.getViewer().getId());
        if (!viewerEntity.isPresent())
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

        //submit the request for processing
        TransactionRequest request = new TransactionRequest()
                .amount(payment.getChargeAmount())
                .paymentMethodNonce(payment.getNonce())
                .options()
                .submitForSettlement(true)
                .done();

        //get the response
        Result<Transaction> transactionResult = gateway.transaction().sale(request);


        ////TODO save event for viewer

        Transaction transaction = null;
        if (transactionResult.isSuccess()) {
            transaction = transactionResult.getTarget();
			Wallet walletEntity = walletRepository.findByViewerId(viewerEntity.get().getId());
			Double totalAmount = null;
			if (walletEntity != null) {

				if(walletEntity.getAmount() == null){
					walletEntity.setAmount(0.00);
				}
				if (walletEntity.getAmount() != null) {
					totalAmount = (walletEntity.getAmount() + transaction.getAmount().doubleValue());
					walletEntity.setAmount(totalAmount);
					walletRepository.save(walletEntity);

				} else {
					throw new ResourceNotFoundException("wallet amount can not be null or empty !");
				}
			} else {
				throw new ResourceNotFoundException("Wallet not exist yet !");
			}


        } else if (transactionResult.getTransaction() != null) {
            transaction = transactionResult.getTransaction();
        } else {
            //if the transaction failed, return to the payment page and display all errors
            String errorString = "";
            for (ValidationError error : transactionResult.getErrors().getAllDeepValidationErrors()) {
                errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";
            }
            throw new Exception(errorString);
        }

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setAmount(transaction.getAmount().floatValue());
        //paymentTransaction.setEvent(eventEntity.get());
        paymentTransaction.setPaymentDatetime(currentTime);
        paymentTransaction.setPaymentMethod("BRAINTREE");
        paymentTransaction.setPaymentType("Credit");
        paymentTransaction.setStatus(IConstants.DEFAULT_STATUS);
        paymentTransaction.setTransactionId(transaction.getId());
        paymentTransaction.setViewer(viewerEntity.get());

        try {

            paymentTransaction = paymentTransactionRepository.save(paymentTransaction);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

//		JSONObject jObj = new JSONObject();
//		jObj.put(IConstants.TRANSACTION, transactionResult.toString());
//		result = utils.constructSucessJSON(jObj);


        return transactionResult;
    }


}
