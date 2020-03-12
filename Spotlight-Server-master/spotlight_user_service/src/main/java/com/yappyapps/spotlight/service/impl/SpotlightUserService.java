package com.yappyapps.spotlight.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.yappyapps.spotlight.domain.Role;
import com.yappyapps.spotlight.repository.IRoleRepository;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yappyapps.spotlight.domain.AuditLog;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.helper.SpotlightUserHelper;
import com.yappyapps.spotlight.exception.AlreadyExistException;
import com.yappyapps.spotlight.exception.BusinessException;
import com.yappyapps.spotlight.exception.InvalidParameterException;
import com.yappyapps.spotlight.exception.ResourceNotFoundException;
import com.yappyapps.spotlight.repository.IAuditLogRepository;
import com.yappyapps.spotlight.repository.ISpotlightUserRepository;
import com.yappyapps.spotlight.repository.ISpotlightUserSessionRepository;
import com.yappyapps.spotlight.service.IEmailNotificationService;
import com.yappyapps.spotlight.service.ISpotlightUserService;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The SpotlightUserService class is the implementation of ISpotlightUserService
 *
 * <h1>@Service</h1> denotes that it is a service class *
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Service
public class SpotlightUserService implements ISpotlightUserService {
    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotlightUserService.class);

    /**
     * ISpotlightUserRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private ISpotlightUserRepository spotlightUserRepository;

    /**
     * IAuditLogRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IAuditLogRepository auditLogRepository;

    @Autowired
    private IRoleRepository roleRepository;

    /**
     * ISpotlightUserSessionRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private ISpotlightUserSessionRepository spotlightUserSessionRepository;

    /**
     * SpotlightUserHelper dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private SpotlightUserHelper spotlightUserHelper;

    /**
     * IEmailNotificationService dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IEmailNotificationService emailNotificationService;

    /**
     * PasswordEncoder dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Utils dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private Utils utils;

    /**
     * This method is used to create the SpotlightUser.
     *
     * @param spotlightUserReqObj: SpotlightUser
     * @return String: Response
     * @throws AlreadyExistException AlreadyExistException
     * @throws BusinessException     BusinessException
     * @throws Exception             Exception
     */
    @Override
    public String createSpotlightUser(SpotlightUser spotlightUserReqObj)
            throws AlreadyExistException, BusinessException, Exception {
        String result = null;

        boolean generatePasswordFlag = false;

        String generatedPassword = Utils.generateRandomPassword(10);
        if (spotlightUserReqObj.getPassword() == null
                || spotlightUserReqObj.getPassword().trim().equalsIgnoreCase("")) {
            generatePasswordFlag = true;
        }
        if (generatePasswordFlag) {
            spotlightUserReqObj.setPassword(generatedPassword);
            LOGGER.info("generatedPassword    ::::: " + generatedPassword);
        }

        //List<Role> roles = spotlightUserReqObj.getUserRoles();
        //LOGGER.info("User Roles     ::::: " + roles);
        // List<Role> roleRequestList = new ArrayList<>();
        /*if(roles !=null && roles.size() > 0) {
			for (Role role : roles) {
                Optional<Role> byId = roleRepository.findById(role.getId());
                if(byId.isPresent())
                    roleRequestList.add(byId.get());

			}
		}else{
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        if(roleRequestList.size() == 0)
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        else
            spotlightUserReqObj.setUserRoles(roleRequestList);*/

        if (spotlightUserRepository.findByEmail(spotlightUserReqObj.getEmail()) != null) {
            throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
        }
        try {
            spotlightUserReqObj = spotlightUserHelper.populateSpotlightUser(spotlightUserReqObj);
            spotlightUserReqObj = spotlightUserRepository.save(spotlightUserReqObj);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (generatePasswordFlag) {
            try {
                spotlightUserReqObj.setPassword(generatedPassword);
                emailNotificationService.sendMimeMessage(spotlightUserReqObj, IConstants.EMAIL_SUBJECT_ACCOUNT_CREDENTIALS);
                LOGGER.info("email sent for createSpotlightUser ::::: ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.USER, spotlightUserHelper.buildResponseObject(spotlightUserReqObj));
        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all SpotlightUser.
     *
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getAllSpotlightUsers() throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;

        List<SpotlightUser> spotlightUserList = null;
        try {
            spotlightUserList = (List<SpotlightUser>) spotlightUserRepository.findAll();
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (spotlightUserList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }
        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.USERS, spotlightUserHelper.buildResponseObject(spotlightUserList));

        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all SPotlightUsers with paging and orderBy.
     *
     * @param limit:     Integer
     * @param offset:    Integer
     * @param direction: String
     * @param orderBy:   String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getAllSpotlightUsers(Integer limit, Integer offset, String direction, String orderBy)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<SpotlightUser> spotlightUserList = new ArrayList<SpotlightUser>();
        int pageNum = offset / limit;
        try {
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<SpotlightUser> spotlightUsers = spotlightUserRepository.findAll(pageableRequest);
            totalCount = spotlightUsers.getTotalElements();
            List<SpotlightUser> spotlightUserEntities = spotlightUsers.getContent();
            for (SpotlightUser spotlightUserEntity : spotlightUserEntities) {
                SpotlightUser spotlightUserDto = new SpotlightUser();
                BeanUtils.copyProperties(spotlightUserEntity, spotlightUserDto);
                spotlightUserList.add(spotlightUserDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (spotlightUserList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }
        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.USERS, spotlightUserHelper.buildResponseObject(spotlightUserList));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, spotlightUserList.size());

        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to get SpotlightUser by spotlightUserId.
     *
     * @param spotlightUserId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getSpotlightUser(Integer spotlightUserId)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        Optional<SpotlightUser> spotlightUser = null;

        try {
            spotlightUser = spotlightUserRepository.findById(spotlightUserId);
            if (!spotlightUser.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.USER, spotlightUserHelper.buildResponseObject(spotlightUser.get()));
        result = utils.constructSucessJSON(jObj);
        return result;
    }

    /**
     * This method is used to get all the Spotlight User by userType.
     *
     * @param userType: String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getSpotlightUsersByType(String userType)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        List<SpotlightUser> spotlightUserList = null;

        try {
            spotlightUserList = spotlightUserRepository.findAllByUserType(userType);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (spotlightUserList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.USERS, spotlightUserHelper.buildResponseObject(spotlightUserList));
        result = utils.constructSucessJSON(jObj);

        return result;

    }

    /**
     * This method is used to get all the Spotlight User by userType with paging and
     * orderBy.
     *
     * @param userType:  String
     * @param limit:     Integer
     * @param offset:    Integer
     * @param direction: String
     * @param orderBy:   String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getSpotlightUsersByType(String userType, Integer limit, Integer offset, String direction,
                                          String orderBy) throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        long totalCount = 0;

        List<SpotlightUser> spotlightUserList = new ArrayList<SpotlightUser>();
        int pageNum = offset / limit;
        try {
            Direction directionObj = (direction != null ? Direction.valueOf(direction) : Direction.valueOf("ASC"));
            orderBy = (orderBy != null ? orderBy : "id");
            Pageable pageableRequest = PageRequest.of(pageNum, limit, directionObj, orderBy);
            Page<SpotlightUser> spotlightUsers = spotlightUserRepository.findAllByUserType(userType, pageableRequest);
            totalCount = spotlightUsers.getTotalElements();
            List<SpotlightUser> spotlightUserEntities = spotlightUsers.getContent();
            for (SpotlightUser spotlightUserEntity : spotlightUserEntities) {
                SpotlightUser spotlightUserDto = new SpotlightUser();
                BeanUtils.copyProperties(spotlightUserEntity, spotlightUserDto);
                spotlightUserList.add(spotlightUserDto);
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (spotlightUserList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }
        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.USERS, spotlightUserHelper.buildResponseObject(spotlightUserList));
        jObj.put(IConstants.TOTAL_RECORDS, totalCount);
        jObj.put(IConstants.CURRENT_PAGE, pageNum);
        jObj.put(IConstants.CURRENT_PAGE_RECORDS, spotlightUserList.size());

        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to get all the Spotlight User by role.
     *
     * @param roleName: String
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String getSpotlightUsersByRole(String roleName)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        List<SpotlightUser> spotlightUserList = null;

        try {
            spotlightUserList = spotlightUserRepository.findAllByRoles(roleName);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        if (spotlightUserList.size() <= 0) {
            throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.USERS, spotlightUserHelper.buildResponseObject(spotlightUserList));
        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to change the password of Spotlight User.
     *
     * @param spotlightUser: SpotlightUser
     * @return String: Response
     * @throws BusinessException BusinessException
     * @throws Exception         Exception
     */
    @Override
    public String changeSpotlightUserPassword(SpotlightUser spotlightUser) throws BusinessException, Exception {
        String result = null;

        SpotlightUser spotlightUserEntity = null;
        try {
            spotlightUserEntity = spotlightUserRepository.findByUsername(spotlightUser.getUsername());
            spotlightUserEntity.setPassword(passwordEncoder.encode(spotlightUser.getPassword()));
            spotlightUserRepository.save(spotlightUserEntity);
            spotlightUserSessionRepository.deleteBySpotlightUser(spotlightUserEntity);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        try {
            emailNotificationService.sendMimeMessage(
                    "You’ve got a new password! <BR> <BR> You’ve successfully updated your password!",
                    spotlightUserEntity.getEmail(), spotlightUserEntity.getEmail(),
                    IConstants.EMAIL_SUBJECT_PASSWORD_CHANGE);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error while sending email ::::: " + e.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.USER, spotlightUserHelper.buildResponseObject(spotlightUserEntity));
        result = utils.constructSucessJSON(jObj);
        return result;

    }

    /**
     * This method is used to verify the old password of Spotlight User.
     *
     * @param spotlightUser: SpotlightUser
     * @param oldPassword:   String
     * @return String: Response
     * @throws BusinessException BusinessException
     * @throws Exception         Exception
     */
    public boolean verifyOldPassword(SpotlightUser spotlightUser, String oldPassword)
            throws BusinessException, Exception {
        boolean result = false;
        SpotlightUser spotlightUserEntity = null;
        try {
            spotlightUserEntity = spotlightUserRepository.findByUsername(spotlightUser.getUsername());
            String persistedPassword = spotlightUserEntity.getPassword();
            if (passwordEncoder.matches(oldPassword, persistedPassword)) {
                return true;
            }

        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        return result;

    }

    /**
     * This method is used to update the SpotlightUser.
     *
     * @param spotlightUserReqObj: SpotlightUser
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws AlreadyExistException     AlreadyExistException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String updateSpotlightUser(SpotlightUser spotlightUserReqObj)
            throws ResourceNotFoundException, AlreadyExistException, BusinessException, Exception {
        String result = null;

        Optional<SpotlightUser> spotlightUserEntity = null;
        try {
            spotlightUserEntity = spotlightUserRepository.findById(spotlightUserReqObj.getId());
            if (!spotlightUserEntity.isPresent()) {
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }

            // if(spotlightUser.getPhone() != null) {
            // SpotlightUser userEntity =
            // spotlightUserRepository.findByPhone(spotlightUser.getPhone());
            // if(( userEntity != null) &&
            // userEntity.getPhone().equals(spotlightUser.getPhone())) {
            // throw new AlreadyExistException("User with same Phone already exists.");
            // }
            // }

            SpotlightUser spotlightUser = spotlightUserHelper.populateSpotlightUser(spotlightUserReqObj, spotlightUserEntity.get());
            spotlightUserRepository.save(spotlightUser);
            JSONObject jObj = new JSONObject();
            jObj.put(IConstants.USER, spotlightUserHelper.buildResponseObject(spotlightUser));
            result = utils.constructSucessJSON(jObj);
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }


        return result;
    }

    /**
     * This method is used to update the SpotlightUser roles.
     *
     * @param spotlightUserReqObj: SpotlightUser
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws AlreadyExistException     AlreadyExistException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String updateSpotlightUserRole(SpotlightUser spotlightUserReqObj)
            throws ResourceNotFoundException, AlreadyExistException, BusinessException, Exception {
        String result = null;

        Optional<SpotlightUser> spotlightUserEntity = null;
        try {
            spotlightUserEntity = spotlightUserRepository.findById(spotlightUserReqObj.getId());
            if (!spotlightUserEntity.isPresent()) {
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);
            }
//			if (spotlightUserReqObj.getPhone() != null) {
//				if ((spotlightUserRepository.findByPhone(spotlightUserReqObj.getPhone()) != null)) {
//					throw new AlreadyExistException(IConstants.ALREADY_EXIST_MESSAGE);
//				}
//			}

            spotlightUserHelper.populateSpotLightUserAccessRole(spotlightUserReqObj.getRoles(),
                    spotlightUserEntity.get());
            spotlightUserRepository.save(spotlightUserEntity.get());
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }

        JSONObject jObj = new JSONObject();
        jObj.put(IConstants.USER, spotlightUserHelper.buildResponseObject(spotlightUserEntity.get()));
        result = utils.constructSucessJSON(jObj);

        return result;
    }

    /**
     * This method is used to delete the SpotlightUser by id.
     *
     * @param spotlightUserId: Integer
     * @return String: Response
     * @throws ResourceNotFoundException ResourceNotFoundException
     * @throws InvalidParameterException InvalidParameterException
     * @throws BusinessException         BusinessException
     * @throws Exception                 Exception
     */
    @Override
    public String deleteSpotlightUser(Integer spotlightUserId)
            throws ResourceNotFoundException, BusinessException, Exception {
        String result = null;
        Optional<SpotlightUser> spotlightUser = null;
        try {
            spotlightUser = spotlightUserRepository.findById(spotlightUserId);
            if (!spotlightUser.isPresent())
                throw new ResourceNotFoundException(IConstants.RESOURCE_NOT_FOUND_MESSAGE);

            List<AuditLog> auditLogList = auditLogRepository.findBySpotlightUser(spotlightUser.get());

            if (!auditLogList.isEmpty()) {
                throw new InvalidParameterException("We cannot delete the User as there is associated data with the User. Please deactivate it.");
            }


            spotlightUserRepository.delete(spotlightUser.get());
        } catch (ConstraintViolationException | DataIntegrityViolationException sqlException) {
            throw new Exception(sqlException.getMessage());
        } catch (HibernateException | JpaSystemException sqlException) {
            throw new Exception(sqlException.getMessage());
        }
        JSONObject jObj = new JSONObject();

        result = utils.constructSucessJSON(jObj);
        return result;
    }

}
