//  This software code is made available "AS IS" without warranties of any
//  kind.  You may copy, display, modify and redistribute the software
//  code either by itself or as incorporated into your code; provided that
//  you do not remove any proprietary notices.  Your use of this software
//  code is at your own risk and you waive any claim against Amazon
//  Digital Services, Inc. or its affiliates with respect to your use of
//  this software code. (c) 2006 Amazon Digital Services, Inc. or its
//  affiliates.
package com.amazon.mturk.requester;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SignatureException;
import java.io.IOException;

/**
 * An interface into the mturk requester system. It is initially configured with
 * authentication and connection parameters and exposes methods to access and
 * create requester data.
 */
public class AWSMechanicalTurkRequester {

    private String awsAccessKeyId;
    private String awsSecretAccessKey;
    private static String SERVICE_NAME = "AWSMechanicalTurkRequester";
    //private static String SERVICE_VERSION = "2006-10-31";
    private static String SERVICE_VERSION = "2012-03-25";
    //private static String SERVICE_VERSION = "2012-03-25";  //latest version from http://docs.aws.amazon.com/AWSMechTurk/latest/AWSMturkAPI/ApiReference_WsdlLocationArticle.html#service-api-versions
    //private static String SERVER = "https://mechanicalturk.amazonaws.com/onca/xml";
    private static String SERVER = "https://mechanicalturk.sandbox.amazonaws.com/?Service=AWSMechanicalTurkRequester";
    //private static String SERVER = "https://mechanicalturk.sandbox.amazonaws.com/";

    /**
     * Create a new interface to interact with Mturk with the given credential
     * and connection parameters
     *
     * @param awsAccessKeyId The your user key into AWS
     * @param awsSecretAccessKey The secret string used to generate signatures
     * for authentication.
     */
    public AWSMechanicalTurkRequester(String awsAccessKeyId, String awsSecretAccessKey) {
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretAccessKey = awsSecretAccessKey;
    }

    /**
     * Creates a new HIT using HittypeID
     *
     * @param hitTypeID
     * @param question
     * @param lifetimeInSeconds
     */
    public RESTResponse createHIT(String hitTypeID, String question, String lifetimeInSeconds)
            throws MalformedURLException, IOException, SignatureException {
        /*
         &HITTypeId=T100CN9P324W00EXAMPLE
         &Question=[URL-encoded question data]
         &LifetimeInSeconds=604800
         */

        StringBuffer sb = new StringBuffer();
        sb.append("&HITTypeId=").append(Utils.urlencode(hitTypeID));
        sb.append("&Question=").append(Utils.urlencode(question));
        sb.append("&LifetimeInSeconds=").append(Utils.urlencode(lifetimeInSeconds));

        return new RESTResponse(makeRequest("CreateHIT", sb.toString()));
    }

    /**
     * Creates a new HIT without HITTypeID
     *
     * @param title
     * @param description
     * @param keywords
     * @param rewardAmountInUSD
     * @param maxAssignments
     * @param assignmentDurationInSeconds
     * @param autoApprovalDelayInSeconds
     * @param question
     * @param lifetimeInSeconds
     */
    public RESTResponse createHIT(String title,
            String description,
            String keywords,
            String rewardAmountInUSD,
            long maxAssignments,
            int assignmentDurationInSeconds,
            int autoApprovalDelayInSeconds,
            String question,
            String lifetimeInSeconds)
            throws MalformedURLException, IOException, SignatureException {
        /*
         &Title=Location%20and%20Photograph%20Identification
         &Description=Select%20the%20image%20that%20best%20represents...
         &Reward.1.Amount=5
         &Reward.1.CurrencyCode=USD
         &Question=[URL-encoded question data]
         &AssignmentDurationInSeconds=30
         &LifetimeInSeconds=604800
         &Keywords=location,%20photograph,%20image,%20identification,%20opinion
         */

        StringBuffer sb = new StringBuffer();

        sb.append("&Title=").append(Utils.urlencode(title));
        sb.append("&Description=").append(Utils.urlencode(description));
        sb.append("&Reward.1.Amount=").append(Utils.urlencode(rewardAmountInUSD));
        sb.append("&Reward.1.CurrencyCode=").append("USD");
        sb.append("&AssignmentDurationInSeconds=").append(String.valueOf(assignmentDurationInSeconds));
        sb.append("&MaxAssignments=").append(String.valueOf(maxAssignments));
        sb.append("&Keywords=").append(Utils.urlencode(keywords));
        sb.append("&AutoApprovalDelayInSeconds=").append(String.valueOf(autoApprovalDelayInSeconds));
        sb.append("&Question=").append(Utils.urlencode(question));
        sb.append("&LifetimeInSeconds=").append(Utils.urlencode(lifetimeInSeconds));

        return new RESTResponse(makeRequest("CreateHIT", sb.toString()));
    }

    /**
     * Get Results of a HIT.
     *
     * @param hitID Hit ID
     */
    public RESTResponse getAssignmentsForHIT(String hitID)
            throws MalformedURLException, IOException, SignatureException {
        /*
         &Operation=GetAssignmentsForHIT
         &HITId=123RVWYBAZW00EXAMPLE
         &PageSize=5
         &PageNumber=1
         */

        StringBuffer sb = new StringBuffer();
        sb.append("&HITId=").append(Utils.urlencode(hitID));

        return new RESTResponse(makeRequest("GetAssignmentsForHIT", sb.toString()));
    }

    /**
     * Get HIT given the HIT ID
     *
     * @param hitID Hit ID
     */
    public RESTResponse getHIT(String hitID)
            throws MalformedURLException, IOException, SignatureException {
        /*
         &Operation=GetHIT
         &HITId=123RVWYBAZW00EXAMPLE
         */

        StringBuffer sb = new StringBuffer();
        sb.append("&HITId=").append(Utils.urlencode(hitID));

        return new RESTResponse(makeRequest("GetHIT", sb.toString()));
    }

    /**
     * Gets Reviewable HITs.
     */
    public RESTResponse getReviewableHITs()
            throws MalformedURLException, IOException, SignatureException {
        /*
         &Operation=GetReviewableHITs
         &PageSize=5
         &PageNumber=1
         */

        StringBuffer sb = new StringBuffer();
    	//sb.append("&PageSize=").append(String.valueOf(pageSize));
        //sb.append("&PageNumber=").append(String.valueOf(pageNumber));

        return new RESTResponse(makeRequest("GetReviewableHITs", sb.toString()));
    }

    /**
     * Rejects an assignment after HIT was answered.
     *
     * @param assignmentId
     * @param requesterFeedBack
     */
    public RESTResponse rejectAssignment(String assignmentId, String requesterFeedback)
            throws MalformedURLException, IOException, SignatureException {
        /*
         &Operation=RejectAssignment
         &AssignmentId=123RVWYBAZW00EXAMPLE456RVWYBAZW00EXAMPLE
         */

        StringBuffer sb = new StringBuffer();
        sb.append("&AssignmentId=").append(Utils.urlencode(assignmentId));
        sb.append("&RequesterFeedback=").append(Utils.urlencode(assignmentId));

        return new RESTResponse(makeRequest("RejectAssignment", sb.toString()));
    }

    /**
     * Approves an assignment after HIT was answered. worker Gets Paid
     *
     * @param assignmentId
     * @param requesterFeedBack
     */
    public RESTResponse approveAssignment(String assignmentId, String requesterFeedback)
            throws MalformedURLException, IOException, SignatureException {
        /*
         &Operation=ApproveAssignment
         &AssignmentId=123RVWYBAZW00EXAMPLE456RVWYBAZW00EXAMPLE
         */

        StringBuffer sb = new StringBuffer();
        sb.append("&AssignmentId=").append(Utils.urlencode(assignmentId));
        sb.append("&RequesterFeedback=").append(Utils.urlencode(assignmentId));

        return new RESTResponse(makeRequest("ApproveAssignment", sb.toString()));
    }

    /**
     * Registers an HITType
     *
     * @param title
     * @param description
     * @param keywords
     * @param rewardAmountInUSD
     * @param assignmentDurationInSeconds
     * @param autoApprovalDelayInSeconds
     */
    public RESTResponse registerHITType(String title,
            String description,
            String keywords,
            String rewardAmountInUSD,
            int assignmentDurationInSeconds,
            int autoApprovalDelayInSeconds)
            throws MalformedURLException, IOException, SignatureException {
        /*&Title=Location%20and%20Photograph%20Identification
         &Description=Select%20the%20image%20that%20best%20represents...
         &Reward.1.Amount=5
         &Reward.1.CurrencyCode=USD
         &AssignmentDurationInSeconds=30
         &Keywords=location,%20photograph,%20image,%20identification,%20opinion
         &QualificationRequirement.1.QualificationTypeId=789RVWYBAZW00EXAMPLE
         &QualificationRequirement.1.Comparator=GreaterThan
         &QualificationRequirement.1.IntegerValue=18
         &QualificationRequirement.2.QualificationTypeId=237HSIANVCI00EXAMPLE
         &QualificationRequirement.2.Comparator=EqualTo
         &QualificationRequirement.2.IntegerValue=1
 
         */
        StringBuffer sb = new StringBuffer();

        sb.append("&Title=").append(Utils.urlencode(title));
        sb.append("&Description=").append(Utils.urlencode(description));
        sb.append("&Reward.1.Amount=").append(Utils.urlencode(rewardAmountInUSD));
        sb.append("&Reward.1.CurrencyCode=").append("USD");
        sb.append("&AssignmentDurationInSeconds=").append(String.valueOf(assignmentDurationInSeconds));
        sb.append("&Keywords=").append(Utils.urlencode(keywords));
        sb.append("&AutoApprovalDelayInSeconds=").append(String.valueOf(autoApprovalDelayInSeconds));

        return new RESTResponse(makeRequest("RegisterHITType", sb.toString()));

    }

    /**
     * Make a new HttpURLConnection without passing an S3Object parameter.
     */
    private HttpURLConnection makeRequest(String operation, String operationParamsString)
            throws MalformedURLException, IOException, SignatureException {
        String urlString = makeInitialURL(operation);
        urlString += operationParamsString;

        URL url = makeURL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        return connection;
    }

    /**
     * Create a new URL object for a given resource.
     *
     * @param resource The resource name (bucketName + "/" + key).
     */
    private URL makeURL(String resource) throws MalformedURLException {
        return new URL(resource);
    }

    public String makeInitialURL(String operation)
            throws MalformedURLException, IOException, SignatureException {
        StringBuffer url = new StringBuffer();

        String timeStamp = Utils.getCurrentTimeStampAsString();
        String signature = Utils.generateSignature(SERVICE_NAME, operation, timeStamp, awsSecretAccessKey);
        url.append(SERVER);
        url.append("?Service=").append(SERVICE_NAME);
        url.append("&AWSAccessKeyId=").append(awsAccessKeyId);
        url.append("&Version=").append(SERVICE_VERSION);
        url.append("&Operation=").append(operation);
        url.append("&Signature=").append(signature);
        url.append("&Timestamp=").append(timeStamp);

        return url.toString();

        /*http://mechanicalturk.amazonaws.com/onca/xml?Service=AWSMechanicalTurkRequester
         &AWSAccessKeyId=[the Requester's Access Key ID]
         &Version=2006-06-20
         &Operation=CreateHIT
         &Signature=[signature for this request]
         &Timestamp=[your system's local time]
         */
    }
}
