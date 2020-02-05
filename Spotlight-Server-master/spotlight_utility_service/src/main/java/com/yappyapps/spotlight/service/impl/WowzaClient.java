package com.yappyapps.spotlight.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.yappyapps.spotlight.domain.LiveStreamConfig;

@Service
public class WowzaClient {

	/**
	* Logger for the class.
	*/	
	private static final Logger LOGGER = LoggerFactory.getLogger(WowzaClient.class);
	
	/**
	 * apiKey
	 */
	private String userName;

	/**
	 * accessKey
	 */
	private String password;
	
	/**
	 * uri
	 */
	private String uri;
	
	/**
	 * uri
	 */
	private String connectionType;
	
	public WowzaClient() {
	}
	
	public WowzaClient(LiveStreamConfig liveStreamConfig) {
		this.connectionType = liveStreamConfig.getConnectionType();
		this.userName = liveStreamConfig.getUsername();
		this.password = liveStreamConfig.getPassword();
		this.uri = liveStreamConfig.getHost();
	}
	
////	@Autowired
////	private RestTemplate restTemplate;	
//
//	public static void main(String args[]) {
//////		System.out.println("1111");
////		final String uri = "https://api-sandbox.cloud.wowza.com/api/v1.2/live_streams";
////		  String apiKey = "Hagme0Qb1lfim12PIgq2ibUhrg5MQ2v5VE6r0x3x4NAr5qvj6cp0jA9QHyf43247";
////		  String accessKey = "RBi9e2NHPxNWszwvx2o83dtAMvFQQA4z8zUNa4T4qHHX6TN6btWj4tBbrXGD3306";
//////		  System.out.println("222222222");
////		RestTemplate restTemplate = new RestTemplate();
////		System.out.println("333333");
//////		restT
//////		String result = restTemplate.getForObject( uri, String.class);
//////		
//////		System.out.println(result);
////		
////	    HttpHeaders headers = new HttpHeaders();
////	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
////	    headers.add("wsc-api-key", apiKey);
////	    headers.add("wsc-access-key", accessKey);
////	    HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
////	     
////	    ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
////		String result =  executeGet("live_streams/b7hhpvnb") ;
////		JSONObject jobj = new JSONObject("{\"live_stream\": {  \"aspect_ratio_height\": 720,  \"aspect_ratio_width\": 1280,  \"billing_mode\": \"pay_as_you_go\",  \"broadcast_location\": \"us_west_california\",  \"closed_caption_type\": \"none\",  \"delivery_method\": \"push\",  \"encoder\": \"wowza_gocoder\",  \"hosted_page\": false,  \"hosted_page_sharing_icons\": true,  \"name\": \"TestLiveStream\",  \"player_countdown\": true,  \"player_responsive\": true,  \"player_type\": \"original_html5\",  \"player_width\": 0,  \"recording\": false,  \"target_delivery_protocol\": \"hls-https\",  \"transcoder_type\": \"transcoded\",  \"use_stream_source\": false}}");
////		
////		String result =  executePost("live_streams", jobj);
////	    System.out.println(new JSONObject(result).get("live_stream"));
//		
//		String result = executeGet("applications/live");
//		System.out.println(result);
//	}
//	
	public String executeGet(String path) {
		RestTemplate restTemplate = restTemplate();
		restTemplate.setInterceptors(getCommonInterceptorHeaders());
		System.out.println("https://api.cloud.wowza.com/api/v1.3/" + path);
		String result = restTemplate.getForObject("https://api.cloud.wowza.com/api/v1.3/" + path, String.class);
	    return result;
		
	}
	
	public String executePost(String path, JSONObject requestBody) {
		RestTemplate restTemplate = restTemplate();
		restTemplate.setInterceptors(getCommonInterceptorHeaders());
		System.out.println("https://api.cloud.wowza.com/api/v1.3/" + path);
		System.out.println(requestBody.toString());
		String result = restTemplate.postForObject("https://api.cloud.wowza.com/api/v1.3/" + path, requestBody.toString(), String.class);
	    return result;
		
	}
	
	public String executePatch(String path, JSONObject requestBody) {
		RestTemplate restTemplate = restTemplate();
		restTemplate.setInterceptors(getCommonInterceptorHeaders());
		HttpHeaders headers = new HttpHeaders();
		System.out.println("https://api.cloud.wowza.com/api/v1.3/" + path);
		System.out.println(requestBody.toString());
		ResponseEntity<String> result = restTemplate.exchange("https://api.cloud.wowza.com/api/v1.3/" + path, HttpMethod.PUT, new HttpEntity(requestBody.toString(), headers), String.class);
	    return result.getBody();
		
	}

	public String executeDelete(String path) {
		RestTemplate restTemplate = restTemplate();
		restTemplate.setInterceptors(getCommonInterceptorHeaders());
		System.out.println(uri + path);
		restTemplate.delete(uri + path);
	    return "SUCCESS";
		
	}

//	private static HttpHeaders getCommonHeaders() {
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//	    headers.add("wsc-api-key", apiKey);
//	    headers.add("wsc-access-key", accessKey);
//	    return headers;
//	}
	
	private List<ClientHttpRequestInterceptor> getCommonInterceptorHeaders() {
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new HeaderRequestInterceptor("Accept", MediaType.APPLICATION_JSON_VALUE));
		interceptors.add(new HeaderRequestInterceptor("Content-Type", MediaType.APPLICATION_JSON_VALUE));
		if(connectionType.equalsIgnoreCase("Cloud")) {
			interceptors.add(new HeaderRequestInterceptor("wsc-api-key", userName));
			interceptors.add(new HeaderRequestInterceptor("wsc-access-key", password));
		}
	    return interceptors;
	}
	
	
	   public RestTemplate restTemplate() {
		   if(connectionType.equalsIgnoreCase("Cloud"))
			   return new RestTemplate();
	        
		   CloseableHttpClient client = HttpClientBuilder.create().
	          setDefaultCredentialsProvider(provider()).useSystemProperties().build();
	        HttpComponentsClientHttpRequestFactory requestFactory = 
	          new HttpComponentsClientHttpRequestFactory(client);
	 
	        return new RestTemplate(requestFactory);
	    }
	     
	    private CredentialsProvider provider() {
	        CredentialsProvider provider = new BasicCredentialsProvider();
	        UsernamePasswordCredentials credentials = 
	          new UsernamePasswordCredentials(userName, password);
	        provider.setCredentials(AuthScope.ANY, credentials);
	        return provider;
	    }}
