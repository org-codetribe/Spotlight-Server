package com.yappyapps.spotlight.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.access_manager.PNAccessManagerGrantResult;

@Component
@Service
public class PubNubService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PubNubService.class);
	
	@Value("${pubnub.publish.key}")
    public static String PUBLISH_KEY = "pub-c-01390525-4ad7-4648-8701-e3af25f81293";
	@Value("${pubnub.subscribe.key}")
	public static String SUBSCRIBE_KEY = "sub-c-b7c3c602-2dcb-11e9-962b-4ae3de4ea26b";
	@Value("${pubnub.secret.key}")
    private static String SECRET_KEY = "sec-c-MDNlYzEwY2UtYmE5ZS00OTRkLThlOWYtMzJjODc1YmI2NjJm";
    private PubNub pubnub = null;
    
    public PubNubService() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(SUBSCRIBE_KEY);
        pnConfiguration.setPublishKey(PUBLISH_KEY);
        pnConfiguration.setSecretKey(SECRET_KEY);
        pnConfiguration.setSecure(true);
        pubnub = new PubNub(pnConfiguration);
    }

    
    public void grantPermissions(List<String> authList, String channel, boolean read, boolean write, boolean manage) {
    	try {
	    	pubnub.grant()
	    	.channels(Arrays.asList(channel))
	    	.authKeys(authList)
	        .write(write) // allow those keys to write (false by default)
	        .manage(manage) // allow those keys to manage channel groups (false by default)
	        .read(read) // allow keys to read the subscribe feed (false by default)
	        .ttl(0)
	        .async(new PNCallback<PNAccessManagerGrantResult>() {
	            @Override
	            public void onResponse(PNAccessManagerGrantResult result, PNStatus status) {
	            	LOGGER.info( "grantPermissions :::: " + result.toString());
	            	LOGGER.info( "grantPermissions :::: " + status.toString());
	            }
	        });
    	} catch (Exception e) {
    		LOGGER.error("Exception in grantPermissions :::: " + e.getMessage());
    	}
    }
    
    public void grantPermissions(List<String> authList, String channel, String access, boolean accessFlag) {
    	try {
    		
    		if(access.equalsIgnoreCase("READ")) {
		    	pubnub.grant()
		    	.channels(Arrays.asList(channel))
		    	.authKeys(authList)
		        .read(accessFlag) // allow keys to read the subscribe feed (false by default)
		        .ttl(0)
		        .async(new PNCallback<PNAccessManagerGrantResult>() {
		            @Override
		            public void onResponse(PNAccessManagerGrantResult result, PNStatus status) {
		            	LOGGER.info( "read grantPermissions :::: " + result.toString());
		            	LOGGER.info( "read grantPermissions :::: " + status.toString());
		            }
		        });
	    	} else if(access.equalsIgnoreCase("WRITE")) {
		    	pubnub.grant()
		    	.channels(Arrays.asList(channel))
		    	.authKeys(authList)
		        .write(accessFlag) // allow those keys to write (false by default)
		        .ttl(0)
		        .async(new PNCallback<PNAccessManagerGrantResult>() {
		            @Override
		            public void onResponse(PNAccessManagerGrantResult result, PNStatus status) {
		            	LOGGER.info( "write grantPermissions :::: " + result.toString());
		            	LOGGER.info( "write grantPermissions :::: " + status.toString());
		            }
		        });
	    	}  else if(access.equalsIgnoreCase("MANAGE")) {
		    	pubnub.grant()
		    	.channels(Arrays.asList(channel))
		    	.authKeys(authList)
		        .manage(accessFlag) // allow those keys to manage (false by default)
		        .ttl(0)
		        .async(new PNCallback<PNAccessManagerGrantResult>() {
		            @Override
		            public void onResponse(PNAccessManagerGrantResult result, PNStatus status) {
		            	LOGGER.info( "manage grantPermissions :::: " + result.toString());
		            	LOGGER.info( "manage grantPermissions :::: " + status.toString());
		            }
		        });
	    	} 
    	} catch (Exception e) {
    		LOGGER.error("Exception in grantPermissions :::: " + e.getMessage());
    	}
    }
    
    public static void main(String[] args) {
		
    	PubNubService pns = new PubNubService();
    	List<String> authList = new ArrayList<>();
    	authList.add("01390525-4ad7-4648-8701-sushubdh");
    	authList.add("01390525-4ad7-4648-8701-naveen");
    	pns.pubnub.grant()
    	.channels(Arrays.asList("Eevent11"))
    	.authKeys(authList)
        .write(false) // allow those keys to write (false by default)
        .manage(true) // allow those keys to manage channel groups (false by default)
        .read(true) // allow keys to read the subscribe feed (false by default)
        .ttl(0)
        .async(new PNCallback<PNAccessManagerGrantResult>() {
            @Override
            public void onResponse(PNAccessManagerGrantResult result, PNStatus status) {
                System.out.println(result.toString());
                System.out.println(status.toString());
            }
        });
    	
	}
    
}
