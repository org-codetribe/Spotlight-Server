package com.yappyapps.spotlight.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.yappyapps.spotlight.service.impl.HeaderRequestInterceptor; 


@Service
public class RestConnector {
	
	
//	@Value("${wowza.apiKey}")
	private static String apiKey = "Hagme0Qb1lfim12PIgq2ibUhrg5MQ2v5VE6r0x3x4NAr5qvj6cp0jA9QHyf43247";

	/**
	 * apiKey
	 */
//	@Value("${wowza.accessKey}")
	private static String accessKey = "RBi9e2NHPxNWszwvx2o83dtAMvFQQA4z8zUNa4T4qHHX6TN6btWj4tBbrXGD3306";
	
	/**
	 * apiKey
	 */
//	@Value("${wowza.uri}")
	private static String uri = "https://api-sandbox.cloud.wowza.com/api/v1.2/";
	
	
	public  String executeGet(String path) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(getCommonInterceptorHeaders());
		System.out.println(uri + path);
		String result = restTemplate.getForObject(uri + path, String.class);
	    return result;
		
	}
	
	public  String executePost(String path, JSONObject requestBody) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(getCommonInterceptorHeaders());
		System.out.println(uri + path);
		System.out.println(requestBody.toString());
		String result = restTemplate.postForObject(uri + path, requestBody.toString(), String.class);
	    return result;
		
	}
	
	public  String executePatch(String path, JSONObject requestBody) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(getCommonInterceptorHeaders());
		HttpHeaders headers = new HttpHeaders();
		System.out.println(uri + path);
		System.out.println(requestBody.toString());
		ResponseEntity<String> result = restTemplate.exchange(uri + path, HttpMethod.PUT, new HttpEntity(requestBody.toString(), headers), String.class);
	    return result.getBody();
		
	}
	
	public  String executeDelete(String path) {

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(getCommonInterceptorHeaders());
		restTemplate.delete(uri + path);
	    return "SUCCESS";
		
	}
	
	private static HttpHeaders getCommonHeaders() {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    headers.add("wsc-api-key", apiKey);
	    headers.add("wsc-access-key", accessKey);
	    return headers;
	}
	
	private static List<ClientHttpRequestInterceptor> getCommonInterceptorHeaders() {
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new HeaderRequestInterceptor("Accept", MediaType.APPLICATION_JSON_VALUE));
		interceptors.add(new HeaderRequestInterceptor("Content-Type", MediaType.APPLICATION_JSON_VALUE));
		interceptors.add(new HeaderRequestInterceptor("wsc-api-key", apiKey));
		interceptors.add(new HeaderRequestInterceptor("wsc-access-key", accessKey));
	    return interceptors;
	}

	
	
}
