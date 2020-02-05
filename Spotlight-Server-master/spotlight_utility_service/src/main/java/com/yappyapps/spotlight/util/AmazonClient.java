package com.yappyapps.spotlight.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
* The AmazonClient class is used to connect to AWS S3
* 
* <h1>@Service</h1> denotes that it is a service class
* * 
* @author  Naveen Goswami
* @version 1.0
* @since   2018-07-14 
*/
@Service
public class AmazonClient {
	/**
	* Logger for the class.
	*/		
	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonClient.class);

	/**
	* AmazonS3 object
	*/		
	private AmazonS3 s3client;

	/**
	* AWS endpointUrl to connect
	* <h1>@Value</h1> will enable the value read from properties file. 
	*/		
    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;

	/**
	* AWS S3 bucketname
	* <h1>@Value</h1> will enable the value read from properties file. 
	*/		
    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    
	/**
	* AWS accesskey for authentication
	* <h1>@Value</h1> will enable the value read from properties file. 
	*/		
    @Value("${amazonProperties.accessKey}")
    private String accessKey;

	/**
	* AWS secretKey for authentication
	* <h1>@Value</h1> will enable the value read from properties file. 
	*/		
    @Value("${amazonProperties.secretKey}")
    private String secretKey;

	/**
	* AWS region to connect
	* <h1>@Value</h1> will enable the value read from properties file. 
	*/		
    @Value("${amazonProperties.region}")
    private String region;

	/**
	* This private method is used to initialize AWS S3 client
	* <h1>@PostConstruct</h1> will enable the method to execute after dependency injection is done. 
	*/		
    @PostConstruct
    private void initializeAmazon() {
       AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
       this.s3client = AmazonS3ClientBuilder.standard()
               .withCredentials(new AWSStaticCredentialsProvider(credentials))
               .withRegion(region)
               .build();
    }
    
    
	/**
	 * This method is used to upload the file to AWS S3.
	 * 
	 * @param multipartFile:
	 *            MultipartFile
	 *            
	 * @return String: fileUrl
	 */
   public String uploadFile(MultipartFile multipartFile) {

        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = generateFileName(multipartFile);
            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
            uploadFileTos3bucket(fileName, file);
            LOGGER.info("File uploaded :::: " + fileUrl);
            file.delete();
        } catch (Exception e) {
           e.printStackTrace();
        }
        return fileUrl;
    }
    
	/**
	 * This private method is used to convert MultipartFile object to File.
	 * 
	 * @param multipartFile:
	 *            MultipartFile
	 *            
	 * @return File: convertedFile
	 * 
	 * @throws IOException IOException
	 */
	private File convertMultiPartToFile(MultipartFile multipartFile) throws IOException {
	    File convertedFile = new File(multipartFile.getOriginalFilename());
	    FileOutputStream fos = new FileOutputStream(convertedFile);
	    fos.write(multipartFile.getBytes());
	    fos.close();
	    return convertedFile;
	}
	
	/**
	 * This private method is used to generate the filename from MultipartFile object.
	 * 
	 * @param multipartFile:
	 *            MultipartFile
	 *            
	 * @return String: fileName
	 */
	private String generateFileName(MultipartFile multipartFile) {
	    return new Date().getTime() + "-" + multipartFile.getOriginalFilename().replace(" ", "_");
	}
	
	/**
	 * This private method is used to upload file to AWS S3 bucket.
	 * 
	 * @param fileName:
	 *            String
	 * @param file:
	 *            File
	 *            
	 */
	private void uploadFileTos3bucket(String fileName, File file) {
	    s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
	            .withCannedAcl(CannedAccessControlList.PublicRead));
	}
	
	
	/**
	 * This private method is used to delete file from AWS S3 bucket.
	 * 
	 * @param fileUrl:
	 *            String
	 * @return fileName:
	 *            String
	 *            
	 */
	public String deleteFileFromS3Bucket(String fileUrl) {
	    String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
	    String bucketUrl = fileUrl.substring(0, fileUrl.lastIndexOf("/"));
	    String bucketName = bucketUrl.substring(bucketUrl.lastIndexOf("/") + 1);
	    s3client.deleteObject(new DeleteObjectRequest(bucketName , fileName));
	    LOGGER.info("File Deleted :::: " + fileUrl);
	    return fileName;
	}
}