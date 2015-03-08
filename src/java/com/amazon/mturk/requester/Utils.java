package com.amazon.mturk.requester;

import org.apache.commons.codec.binary.*;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
 
import javax.crypto.*;
import javax.crypto.spec.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    /**
     * @param serviceName The service name
     * @param operation   The Operation name
     * @param timestamp   Calendar timestamp.
     * Use AWSDateFormatter.getCurrentTimeStampAsCalendar()
     * @param key         The signing key
     * @return The base64-encoded RFC 2104-compliant HMAC signature
     * @throws java.security.SignatureException
     *
     */
    public static String generateSignature
            (String serviceName,
             String operation,
             String timestamp,
             String key)
            throws java.security.SignatureException {
        return generateSignature
                (serviceName + operation + timestamp, key);
    }
    /**
     * Computes RFC 2104-compliant HMAC signature.
     *
     * @param data The data to be signed
     * @param key  The signing key
     * @return The base64-encoded RFC 2104-compliant HMAC signature
     * @throws java.security.SignatureException
     *
     */
    public static String generateSignature(String data, String key)
            throws java.security.SignatureException {

        String signature;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),
                                                         HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            signature = new String(org.apache.commons.codec.binary.Base64.encodeBase64(rawHmac));
        }

        catch (Exception e) {
            throw new SignatureException("Failed to generate Signature : "
                                         + e.getMessage());
        }
        return signature;
    }
    

    private static String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static String TIME_ZONE = "UTC";
  //   private static String TIME_ZONE = "EDT";

    /**
     * Gets current time in the calendar format
     * @return current timestamp
     * @throws ParseException
     */
    public static Calendar getCurrentTimeStampAsCalendar()
            throws ParseException {

        SimpleDateFormat df = new SimpleDateFormat(TIMESTAMP_FORMAT);
        Calendar ts = new GregorianCalendar();
        df.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        ts.setTime(df.parse(convertDateToString(Calendar.getInstance())));
        return ts;
    }

    /**
     * Gets current time in the timestamp format
     * @return String of current time in the timestamp format
     */
    public static String getCurrentTimeStampAsString() {
        return convertDateToString(Calendar.getInstance());
    }

    /**
     * Converts Calendar timestamp to String
     * @param time
     * @return Calndar time stamp
     */
    public static String convertDateToString(Calendar time) {
        SimpleDateFormat df = new SimpleDateFormat(TIMESTAMP_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        return df.format(time.getTime());
    }

    /**
     * Converts String timestamp to Calendar
     * @param timestamp formatted string timestamp
     * @return Calndar time stamp
     * @throws java.text.ParseException
     */
    public static Calendar convertStringToCalendar(String timestamp)
            throws ParseException {

        SimpleDateFormat df = new SimpleDateFormat(TIMESTAMP_FORMAT);
        Calendar ts = new GregorianCalendar();
        df.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        ts.setTime(df.parse(timestamp));
        return ts;
    }
    
    public static String urlencode(String unencoded) {
        try {
            return URLEncoder.encode(unencoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // should never happen
            throw new RuntimeException("Could not url encode to UTF-8", e);
        }
    }

    public static XMLReader createXMLReader() {
        try {
            return XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            // oops, lets try doing this (needed in 1.4)
            System.setProperty("org.xml.sax.driver", "org.apache.crimson.parser.XMLReaderImpl");
        }
        try {
            // try once more
            return XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            throw new RuntimeException("Couldn't initialize a sax driver for the XMLReader");
        }
    }
}