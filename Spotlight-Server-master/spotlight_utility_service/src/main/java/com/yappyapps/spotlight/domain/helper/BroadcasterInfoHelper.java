package com.yappyapps.spotlight.domain.helper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yappyapps.spotlight.domain.BroadcasterInfo;
import com.yappyapps.spotlight.domain.Favorite;
import com.yappyapps.spotlight.domain.Genre;
import com.yappyapps.spotlight.domain.SpotlightCommission;
import com.yappyapps.spotlight.domain.SpotlightUser;
import com.yappyapps.spotlight.domain.Viewer;
import com.yappyapps.spotlight.repository.IFavoriteRepository;
import com.yappyapps.spotlight.repository.IGenreRepository;
import com.yappyapps.spotlight.repository.ISpotlightCommissionRepository;
import com.yappyapps.spotlight.util.IConstants;
import com.yappyapps.spotlight.util.Utils;

/**
 * The BroadcasterInfoHelper class is the utility class to build and validate
 * BroadcasterInfo
 *
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Component
public class BroadcasterInfoHelper {
    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BroadcasterInfoHelper.class);

    /*
     * IGenreRepository Bean
     */
    @Autowired
    private IGenreRepository genreRepository;

    /*
     * SpotlightUserHelper Bean
     */
    @Autowired
    private SpotlightUserHelper spotlightUserHelper;

    /*
     * GenreHelper Bean
     */
    @Autowired
    private GenreHelper genreHelper;

    /*
     * ISpotlightCommissionRepository Bean
     */
    @Autowired
    private ISpotlightCommissionRepository spotlightCommissionRepository;

    /**
     * IFavoriteRepository dependency will be automatically injected.
     * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
     */
    @Autowired
    private IFavoriteRepository favoriteRepository;

    /**
     * This method is used to create the BroadcasterInfo Entity by copying
     * properties from requested Bean
     *
     * @param broadcasterInfoReqObj : BroadcasterInfo
     * @return BroadcasterInfo: broadcasterInfoEntity
     */
    public BroadcasterInfo populateBroadcasterInfo(BroadcasterInfo broadcasterInfoReqObj) {
        BroadcasterInfo broadcasterInfoEntity = new BroadcasterInfo();
        SpotlightUser spotlightUserEntity = spotlightUserHelper
                .populateSpotlightUser(broadcasterInfoReqObj.getSpotlightUser());
        spotlightUserEntity.setUserType("BROADCASTER");
        broadcasterInfoEntity.setSpotlightUser(spotlightUserEntity);

        Set<Genre> genreSet = new HashSet<Genre>();
        for (Genre genre : broadcasterInfoReqObj.getGenre()) {
            Optional<Genre> genreEntity = genreRepository.findById(genre.getId());
            if (genreEntity.isPresent())
                genreSet.add(genreEntity.get());
        }
        broadcasterInfoEntity.setGenre(genreSet);

        broadcasterInfoEntity
                .setBannerUrl(broadcasterInfoReqObj.getBannerUrl() != null ? broadcasterInfoReqObj.getBannerUrl()
                        : broadcasterInfoEntity.getBannerUrl());
        broadcasterInfoEntity
                .setBiography(broadcasterInfoReqObj.getBiography() != null ? broadcasterInfoReqObj.getBiography()
                        : broadcasterInfoEntity.getBiography());
        broadcasterInfoEntity
                .setDisplayName(broadcasterInfoReqObj.getDisplayName() != null ? broadcasterInfoReqObj.getDisplayName()
                        : broadcasterInfoEntity.getDisplayName());
        broadcasterInfoEntity
                .setIsTrending(broadcasterInfoReqObj.getIsTrending() != null ? broadcasterInfoReqObj.getIsTrending()
                        : broadcasterInfoEntity.getIsTrending());
        broadcasterInfoEntity
                .setShortDesc(broadcasterInfoReqObj.getShortDesc() != null ? broadcasterInfoReqObj.getShortDesc()
                        : broadcasterInfoEntity.getShortDesc());
        broadcasterInfoEntity.setUniqueName(Utils.generateRandomString(64));
        broadcasterInfoEntity.setStatus(broadcasterInfoReqObj.getStatus() != null ? broadcasterInfoReqObj.getStatus()
                : IConstants.DEFAULT_STATUS);
        LOGGER.debug("BroadcasterInfo populated from Requested BroadcasterInfo Object ");
        return broadcasterInfoEntity;
    }

    /**
     * This method is used to copy the BroadcasterInfo properties from requested
     * Bean to Entity Bean
     *
     * @param broadcasterInfoReqObj : BroadcasterInfo
     * @param broadcasterInfoEntity : BroadcasterInfo
     * @return BroadcasterInfo: broadcasterInfoEntity
     */
    public BroadcasterInfo populateBroadcasterInfo(BroadcasterInfo broadcasterInfoReqObj,
                                                   BroadcasterInfo broadcasterInfoEntity) {
        String currentPassword = "";
        if (broadcasterInfoReqObj.getSpotlightUser() == null) {
            currentPassword = broadcasterInfoEntity.getSpotlightUser().getPassword();
            broadcasterInfoReqObj.setSpotlightUser(broadcasterInfoEntity.getSpotlightUser());
        }

        SpotlightUser spotlightUserEntity = spotlightUserHelper.populateSpotlightUser(
                broadcasterInfoReqObj.getSpotlightUser(), broadcasterInfoEntity.getSpotlightUser());
        spotlightUserEntity.setUserType("BROADCASTER");
        spotlightUserEntity.setStatus(broadcasterInfoReqObj.getStatus() != null ? broadcasterInfoReqObj.getStatus()
                : broadcasterInfoEntity.getStatus());
        if (!currentPassword.equals(""))
            spotlightUserEntity.setPassword(currentPassword);
        broadcasterInfoEntity.setSpotlightUser(spotlightUserEntity);

        if (broadcasterInfoReqObj.getGenre().size() > 0) {
            Set<Genre> genreSet = new HashSet<Genre>();
            for (Genre genre : broadcasterInfoReqObj.getGenre()) {
                Optional<Genre> genreEntity = genreRepository.findById(genre.getId());
                genreSet.add(genreEntity.get());
            }
            broadcasterInfoEntity.setGenre(genreSet);
        }
        broadcasterInfoEntity
                .setBannerUrl(broadcasterInfoReqObj.getBannerUrl() != null ? broadcasterInfoReqObj.getBannerUrl()
                        : broadcasterInfoEntity.getBannerUrl());
        broadcasterInfoEntity
                .setBiography(broadcasterInfoReqObj.getBiography() != null ? broadcasterInfoReqObj.getBiography()
                        : broadcasterInfoEntity.getBiography());
        broadcasterInfoEntity
                .setDisplayName(broadcasterInfoReqObj.getDisplayName() != null ? broadcasterInfoReqObj.getDisplayName()
                        : broadcasterInfoEntity.getDisplayName());
        broadcasterInfoEntity
                .setIsTrending(broadcasterInfoReqObj.getIsTrending() != null ? broadcasterInfoReqObj.getIsTrending()
                        : broadcasterInfoEntity.getIsTrending());
        broadcasterInfoEntity
                .setShortDesc(broadcasterInfoReqObj.getShortDesc() != null ? broadcasterInfoReqObj.getShortDesc()
                        : broadcasterInfoEntity.getShortDesc());
        broadcasterInfoEntity.setStatus(broadcasterInfoReqObj.getStatus() != null ? broadcasterInfoReqObj.getStatus()
                : broadcasterInfoEntity.getStatus());
        LOGGER.debug("BroadcasterInfo Entity populated from Requested BroadcasterInfo Object ");
        return broadcasterInfoEntity;
    }

    /**
     * This method is used to build the response object.
     *
     * @param broadcasterInfo: BroadcasterInfo
     * @param viewer:          Viewer
     * @param deepGenreFlag:   boolean
     * @return JSONObject: broadcasterInfoObj
     */
    public JSONObject buildResponseObject(BroadcasterInfo broadcasterInfo, Viewer viewer, boolean deepGenreFlag) throws JSONException {
        JSONObject broadcasterInfoObj = new JSONObject();
        broadcasterInfoObj.put("id", broadcasterInfo.getId());

        broadcasterInfoObj.put("bannerUrl", broadcasterInfo.getBannerUrl());


        broadcasterInfoObj.put("biography", broadcasterInfo.getBiography());
        broadcasterInfoObj.put("displayName", broadcasterInfo.getDisplayName());
        broadcasterInfoObj.put("isTrending", broadcasterInfo.getIsTrending());
        broadcasterInfoObj.put("shortDesc", broadcasterInfo.getShortDesc());
        broadcasterInfoObj.put("status", broadcasterInfo.getStatus());
        broadcasterInfoObj.put("uniqueName", broadcasterInfo.getUniqueName());
        SpotlightCommission spotlightCommissionEntity = spotlightCommissionRepository
                .findByBroadcasterInfoAndEvent(broadcasterInfo, null);
        if (spotlightCommissionEntity != null)
            broadcasterInfoObj.put("commission", spotlightCommissionEntity.getPercentage());

        broadcasterInfoObj.put("spotlightUser",
                spotlightUserHelper.buildResponseObject(broadcasterInfo.getSpotlightUser()));
        if (deepGenreFlag) {
            JSONArray genreArr = new JSONArray();
            for (Genre genre : broadcasterInfo.getGenre()) {
                genreArr.put(genreHelper.buildResponseObject(genre));
            }
            broadcasterInfoObj.put(IConstants.GENRES, genreArr);
        }

        if (viewer != null && viewer.getId() != null) {
            Favorite favoriteEntity = favoriteRepository.findByBroadcasterInfoAndEventAndViewer(broadcasterInfo, null, viewer);
            if (favoriteEntity != null) {
                if (favoriteEntity.getBroadcasterInfo().getId() == broadcasterInfo.getId()) {
                    broadcasterInfoObj.put("isFavorite", true);
                } else {
                    broadcasterInfoObj.put("isFavorite", false);
                }
            }
        }

        LOGGER.debug("BroadcasterInfo Response Object built for BroadcasterInfo Object id :::: " + broadcasterInfo.getId() + " with deepGenreFlag :::: " + deepGenreFlag);
        return broadcasterInfoObj;
    }

    /**
     * This method is used to build the response object.
     *
     * @param broadcasterInfoList : List&lt;BroadcasterInfo&gt;
     * @param viewer              : Viewer
     * @return JSONArray: broadcasterInfoArr
     */
    public JSONArray buildResponseObject(List<BroadcasterInfo> broadcasterInfoList, Viewer viewer) throws JSONException {
        JSONArray broadcasterInfoArr = new JSONArray();
        for (BroadcasterInfo broadcasterInfo : broadcasterInfoList) {
            JSONObject broadcasterInfoObj = buildResponseObject(broadcasterInfo, viewer, false);
            broadcasterInfoArr.put(broadcasterInfoObj);
        }
        LOGGER.debug("BroadcasterInfo Response Array built with size :::: " + broadcasterInfoArr.length());
        return broadcasterInfoArr;
    }

    /**
     * This method is used to build the response object.
     *
     * @param broadcasterInfoList : Set&lt;BroadcasterInfo&gt;
     * @param viewer              : Viewer
     * @return JSONArray: broadcasterInfoArr
     */
    public JSONArray buildResponseObject(Set<BroadcasterInfo> broadcasterInfoList, Viewer viewer) throws JSONException {
        JSONArray broadcasterInfoArr = new JSONArray();
        for (BroadcasterInfo broadcasterInfo : broadcasterInfoList) {
            JSONObject broadcasterInfoObj = buildResponseObject(broadcasterInfo, viewer, false);
            broadcasterInfoArr.put(broadcasterInfoObj);
        }
        LOGGER.debug("BroadcasterInfo Response Array built with size :::: " + broadcasterInfoArr.length());
        return broadcasterInfoArr;
    }
}
