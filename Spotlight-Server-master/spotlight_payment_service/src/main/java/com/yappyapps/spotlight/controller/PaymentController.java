package com.yappyapps.spotlight.controller;

import com.yappyapps.spotlight.domain.Viewer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.google.gson.Gson;
import com.yappyapps.spotlight.domain.Coupon;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.service.IPaymentService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.MeteringService;
import com.yappyapps.spotlight.util.Payment;
import com.yappyapps.spotlight.util.Utils;

/**
 * The PaymentController class is the controller which will expose all the
 * required REST interfaces to perform CRUD on Payment.
 *
 * <h1>@RestController</h1> will enable it to expose the REST APIs.
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */

@RestController
@RequestMapping(value = "1.0/payment")

@CrossOrigin(value = "*")
public class PaymentController {
    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    /**
     * Controller Name.
     */
    private static final String controller = "Payment";

    /**
     * IPaymentService dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IPaymentService paymentService;

    /**
     * MeteringService dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private MeteringService meteringService;

    /**
     * Gson dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private Gson gson;

    /**
     * Utils dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private Utils utils;


    @RequestMapping(value = "/token", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getAccessToken(@RequestParam(value = "viewerId", required = false) String viewerId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getAccessToken";
        LOGGER.info("PaymentController :: " + operation + " :: viewerId :: " + viewerId);
        long startTime = System.currentTimeMillis();
        String result = null;
        try {
            result = paymentService.getAccessToken(viewerId);
        } catch (InvalidParameterException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (BusinessException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
        } finally {
            meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
        }
        return result;
    }


    @RequestMapping(value = "/status/{transactionId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    String getTransactionStatus(@PathVariable("transactionId") String transactionId)
            throws InvalidParameterException, ResourceNotFoundException, BusinessException {
        String operation = "getTransactionStatus";
        LOGGER.info("PaymentController :: " + operation + " :: transactionId :: " + transactionId);
        long startTime = System.currentTimeMillis();
        String result = null;
        try {
            result = paymentService.getTransactionStatus(transactionId);
        } catch (InvalidParameterException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (BusinessException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
        } finally {
            meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
        }
        return result;
    }

    /**
     *
     */
    @RequestMapping(value = "/transaction", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public Result<Transaction> paymentTransaction(@RequestBody String requestBody,
                                                  @RequestHeader("Content-Type") String contentType) throws InvalidParameterException, ResourceNotFoundException, BusinessException, Exception {

        String operation = "paymentTransaction";
        LOGGER.info("PaymentController :: " + operation + " :: requestBody :: " + requestBody + " :: contentType :: " + contentType);
        long startTime = System.currentTimeMillis();
        Result<Transaction> result = null;
        utils.isBodyJSONObject(requestBody);
        Payment payment = gson.fromJson(requestBody, Payment.class);
        utils.isEmptyOrNull(payment.getChargeAmount(), "Charge Amount");
        utils.isBigDecimalGreaterThanZero(payment.getChargeAmount(), "Charge Amount");
        utils.isEmptyOrNull(payment.getEvent(), "Event");
        utils.isEmptyOrNull(payment.getViewer(), "Viewer");
        utils.isEmptyOrNull(payment.getNonce(), "Nonce");
        utils.isEmptyOrNull(payment.getEvent().getId(), "Event Id");
        utils.isEmptyOrNull(payment.getViewer().getId(), "Viewer Id");
        utils.isIntegerGreaterThanZero(payment.getEvent().getId(), "Event Id");
        utils.isIntegerGreaterThanZero(payment.getViewer().getId(), "Viewer Id");
        try {
            result = paymentService.paymentTransaction(payment);
        } catch (InvalidParameterException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (BusinessException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
        }
        return result;

    }


    /**
     *
     */
    @RequestMapping(value = "/coupon/transaction", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public String couponTransaction(@RequestBody String requestBody,
                                    @RequestHeader("Content-Type") String contentType) throws InvalidParameterException, ResourceNotFoundException, BusinessException {

        String operation = "couponTransaction";
        LOGGER.info("PaymentController :: " + operation + " :: requestBody :: " + requestBody + " :: contentType :: " + contentType);
        long startTime = System.currentTimeMillis();
        String result = null;
        utils.isBodyJSONObject(requestBody);
        Payment payment = gson.fromJson(requestBody, Payment.class);
        utils.isEmptyOrNull(payment.getCouponCode(), "Coupon Code");
        utils.isEmptyOrNull(payment.getEvent(), "Event");
        utils.isEmptyOrNull(payment.getViewer(), "Viewer");
        utils.isEmptyOrNull(payment.getEvent().getId(), "Event Id");
        utils.isEmptyOrNull(payment.getViewer().getId(), "Viewer Id");
        utils.isIntegerGreaterThanZero(payment.getEvent().getId(), "Event Id");
        utils.isIntegerGreaterThanZero(payment.getViewer().getId(), "Viewer Id");
        try {
            result = paymentService.couponTransaction(payment);
        } catch (InvalidParameterException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (BusinessException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new BusinessException(IConstants.INTERNAL_SERVER_ERROR);
        } finally {
            meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
        }
        return result;

    }

    /**
     *
     */
    @RequestMapping(value = "/purchaseCoupon", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public Result<Transaction> purchaseCoupon(@RequestBody String requestBody,
                                              @RequestHeader("Content-Type") String contentType) throws InvalidParameterException, ResourceNotFoundException, BusinessException, Exception {

        String operation = "purchaseCoupon";
        LOGGER.info("PaymentController :: " + operation + " :: requestBody :: " + requestBody + " :: contentType :: " + contentType);
        long startTime = System.currentTimeMillis();
        Result<Transaction> result = null;
        utils.isBodyJSONObject(requestBody);
        Payment payment = gson.fromJson(requestBody, Payment.class);
        utils.isEmptyOrNull(payment.getChargeAmount(), "Charge Amount");
        utils.isBigDecimalGreaterThanZero(payment.getChargeAmount(), "Charge Amount");
        utils.isEmptyOrNull(payment.getEvent(), "Event");
        utils.isEmptyOrNull(payment.getBroadcasterInfo(), "BroadcasterInfo");
        utils.isEmptyOrNull(payment.getNonce(), "Nonce");
        utils.isEmptyOrNull(payment.getEvent().getId(), "Event Id");
        utils.isEmptyOrNull(payment.getBroadcasterInfo().getId(), "BroadcasterInfo Id");
        utils.isIntegerGreaterThanZero(payment.getEvent().getId(), "Event Id");
        utils.isIntegerGreaterThanZero(payment.getBroadcasterInfo().getId(), "BroadcasterInfo Id");

        Coupon coupon = payment.getCoupon();
        coupon.setEvent(payment.getEvent());
        utils.isEmptyOrNull(coupon.getType(), "Coupon Type");
        utils.isCouponTypeValid(coupon.getType());
        utils.isStatusValid(coupon.getStatus());

        JSONObject reqJson = new JSONObject(requestBody).getJSONObject("coupon");

        if (coupon.getType().equalsIgnoreCase("Multi")) {
            utils.isEmptyOrNull(coupon.getRedemptionLimit(), "Redemption Limit");
        } else {
            if (!reqJson.has("count"))
                throw new InvalidParameterException(
                        "The Request parameters are invalid. The parameter 'count' cannot be null or empty.");
            utils.isEmptyOrNull(reqJson.get("count"), "Coupon Count");
            utils.isIntegerGreaterThanZero(reqJson.get("count"), "Coupon Count");
            Integer count = Integer.parseInt(reqJson.get("count").toString());
            coupon.setCount(count);
        }

        try {
            result = paymentService.purchaseCoupon(payment);
        } catch (InvalidParameterException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (BusinessException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
        }
        return result;

    }

    @RequestMapping(value = "/add-money-wallet", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public Result<Transaction> addWalletBucksThroughPaymentGateway(@RequestBody String requestBody,
                                                                   @RequestHeader("Content-Type") String contentType, @RequestParam("viewerId") Integer viewerId) throws InvalidParameterException, ResourceNotFoundException, BusinessException, Exception {

        String operation = "addWalletBucksThroughPaymentGateway";
        LOGGER.info("PaymentController :: " + operation + " :: requestBody :: " + requestBody + " :: contentType :: " + contentType);
        long startTime = System.currentTimeMillis();
        Result<Transaction> result = null;
        utils.isBodyJSONObject(requestBody);
        Payment payment = gson.fromJson(requestBody, Payment.class);
        utils.isEmptyOrNull(payment.getChargeAmount(), "Charge Amount");
        utils.isBigDecimalGreaterThanZero(payment.getChargeAmount(), "Charge Amount");
        utils.isEmptyOrNull(payment.getNonce(), "Nonce");
        utils.isEmptyOrNull(viewerId, "Viewer Id");
        utils.isIntegerGreaterThanZero(viewerId, "Viewer Id");
        try {
            Viewer viewer = new Viewer();
            viewer.setId(viewerId);
            result = paymentService.addWalletPaymentTransaction(payment);
        } catch (InvalidParameterException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (ResourceNotFoundException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (BusinessException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            meteringService.record(controller, operation, (System.currentTimeMillis() - startTime), 0);
        }
        return result;

    }


}
