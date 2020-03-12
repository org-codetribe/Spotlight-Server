package com.yappyapps.spotlight.domain.helper;

import java.sql.Timestamp;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The ViewerHelper class is the utility class to build and validate
 * ViewerHelper
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class ViewerHelper {
    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewerHelper.class);

    /*
     * PasswordEncoder Bean
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * This method is used to create the Viewer Entity by copying properties from
     * requested Bean
     *
     * @param viewerReqObj : Viewer
     * @return Viewer: viewerEntity
     */
    public Viewer populateViewer(Viewer viewerReqObj) {
        Viewer viewerEntity = new Viewer();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        viewerEntity.setFname(viewerReqObj.getFname() != null ? viewerReqObj.getFname() : null);
        viewerEntity.setLname(viewerReqObj.getLname() != null ? viewerReqObj.getLname() : null);
        viewerEntity.setPhone(viewerReqObj.getPhone() != null ? viewerReqObj.getPhone() : null);
        viewerEntity.setEmail(viewerReqObj.getEmail() != null ? viewerReqObj.getEmail() : null);
        viewerEntity.setFacebookGmailId(viewerReqObj.getFacebookGmailId()!=null ? viewerReqObj.getFacebookGmailId() : null);
        viewerEntity.setPassword(
                viewerReqObj.getPassword() != null ? passwordEncoder.encode(viewerReqObj.getPassword()) : null);
        viewerEntity.setCreatedOn(viewerReqObj.getCreatedOn() != null ? viewerReqObj.getCreatedOn() : currentTime);
        viewerEntity.setUpdatedOn(viewerReqObj.getUpdatedOn() != null ? viewerReqObj.getUpdatedOn() : currentTime);
        viewerEntity.setUniqueName(Utils.uniqueIdGenerator(viewerReqObj.getEmail()));
        viewerEntity.setStatus(viewerReqObj.getStatus() != null ? viewerReqObj.getStatus() : IConstants.DEFAULT_STATUS);
        viewerEntity.setChatName(viewerReqObj.getChatName() != null ? viewerReqObj.getChatName() : null);
        viewerEntity.setUsername(viewerReqObj.getUsername() != null ? viewerReqObj.getUsername() : null);
        if (viewerReqObj.getUsername() == null || viewerReqObj.getUsername().trim().equals("")) {
            viewerEntity.setUsername(viewerReqObj.getEmail());
        }

        return viewerEntity;
    }

    /**
     * This method is used to copy the Viewer properties from requested Bean to
     * Entity Bean
     *
     * @param viewerReqObj : Viewer
     * @param viewerEntity : Viewer
     * @return Viewer: viewerEntity
     */
    public Viewer populateViewer(Viewer viewerReqObj, Viewer viewerEntity) {
        Timestamp updatedTime = new Timestamp(System.currentTimeMillis());
        viewerEntity.setFname(viewerReqObj.getFname() != null ? viewerReqObj.getFname() : viewerEntity.getFname());
        viewerEntity.setLname(viewerReqObj.getLname() != null ? viewerReqObj.getLname() : viewerEntity.getLname());
        viewerEntity.setPhone(viewerReqObj.getPhone() != null ? viewerReqObj.getPhone() : viewerEntity.getPhone());
        viewerEntity.setEmail(viewerReqObj.getEmail() != null ? viewerReqObj.getEmail() : viewerEntity.getAlternativeEmail());
        viewerEntity.setAlternativeEmail(viewerReqObj.getAlternativeEmail() != null ? viewerReqObj.getAlternativeEmail() : viewerEntity.getEmail());
        viewerEntity.setProfilePicture(viewerReqObj.getProfilePicture() != null ? viewerReqObj.getProfilePicture() : viewerEntity.getProfilePicture());
        viewerEntity.setPassword(viewerReqObj.getPassword() != null ? passwordEncoder.encode(viewerReqObj.getPassword())
                : viewerEntity.getPassword());
        viewerEntity.setUpdatedOn(updatedTime);
        viewerEntity.setStatus(viewerReqObj.getStatus() != null ? viewerReqObj.getStatus() : viewerEntity.getStatus());
        viewerEntity.setChatName(viewerReqObj.getChatName() != null ? viewerReqObj.getChatName() : viewerEntity.getChatName());

        LOGGER.debug("Viewer Entity populated from Requested Viewer Object ");
        return viewerEntity;
    }

    /**
     * This method is used to build the response object.
     *
     * @param viewer: Viewer
     * @return JSONObject: viewerObj
     * @throws JSONException JSONException
     */
    public JSONObject buildResponseObject(Viewer viewer) throws JSONException {
        JSONObject viewerObj = new JSONObject();
        viewerObj.put("id", viewer.getId());
        viewerObj.put("fname", viewer.getFname());
        viewerObj.put("lname", viewer.getLname());
        viewerObj.put("createdOn", viewer.getCreatedOn());
        viewerObj.put("email", viewer.getEmail());
        viewerObj.put("alternativeEmail", viewer.getAlternativeEmail());
        viewerObj.put("phone", viewer.getPhone());
        viewerObj.put("status", viewer.getStatus());
        viewerObj.put("uniqueName", viewer.getUniqueName());
        viewerObj.put("updatedOn", viewer.getUpdatedOn());
        viewerObj.put("username", viewer.getUsername());
        viewerObj.put("profilePicture", viewer.getProfilePicture());
        viewerObj.put("chatName", viewer.getChatName());

        LOGGER.debug("Viewer Response Object built for Viewer Object id :::: " + viewer.getId());
        return viewerObj;

    }

    /**
     * This method is used to build the response object.
     *
     * @param viewerrList : List&lt;Viewer&gt;
     * @return JSONArray: viewerArr
     * @throws JSONException JSONException
     */
    public JSONArray buildResponseObject(List<Viewer> viewerrList) throws JSONException {
        JSONArray viewerArr = new JSONArray();
        for (Viewer viewer : viewerrList) {
            JSONObject viewerObj = buildResponseObject(viewer);
            viewerArr.put(viewerObj);
        }
        LOGGER.debug("Viewer Response Array built with size :::: " + viewerArr.length());
        return viewerArr;
    }

}
