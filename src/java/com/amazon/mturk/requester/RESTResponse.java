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
import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.parsers.*;
import org.apache.xpath.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import java.util.ArrayList;

/**
 * A Response object returned from AWSAuthConnection.get(). Exposes the
 * attribute object, which represents the retrieved object.
 */
public class RESTResponse extends Response {
	public Document doc;

	public Document getDocument() {
		return doc;
	}

	/**
	 * Pulls a representation of an S3Object out of the HttpURLConnection
	 * response.
	 */
	public RESTResponse(HttpURLConnection connection) throws IOException {
		super(connection);
		if (connection.getResponseCode() < 400) {

			try {

				/***************************************************************
				 * How to use turn an XML file into a document object in Java
				 **************************************************************/

				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory
						.newDocumentBuilder();
				// Parse the XML file and build the Document object in RAM
				doc = docBuilder.parse(connection.getInputStream());

				// Normalize text representation.
				// Collapses adjacent text nodes into one node.
				doc.getDocumentElement().normalize();
				/** ************************************************************* */

			} catch (Exception e) {
				e.printStackTrace();
			} catch (Throwable t) {
				t.printStackTrace();
				String msg = "Some other exception while getting XML";
				System.out.println(msg);
			}
		}
	}

	/**
	 * Read the input stream and dump it all into a big byte array
	 */
	static byte[] slurpInputStream(InputStream stream) throws IOException {
		final int chunkSize = 2048;
		byte[] buf = new byte[chunkSize];
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(chunkSize);
		int count;

		while ((count = stream.read(buf)) != -1)
			byteStream.write(buf, 0, count);

		return byteStream.toByteArray();
	}

	public void printXMLResponse() throws TransformerException {

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty("indent", "yes");
		transformer.transform(new DOMSource(doc), new StreamResult(System.out));
	}

	public String getXPathValue(String xpathString) {

		String str = "";
		try {

			// Catches the first node that meets the criteria of xpath string
			str = XPathAPI.eval(doc, xpathString).toString();

		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return str;
	}

	public ArrayList getXPathValues(String xpathString) {

		ArrayList strarray = new ArrayList();
		try {

			// Catches all the nodes that meets the criteria of xpath string
			NodeList nl = XPathAPI.selectNodeList(doc, xpathString);

			for (int i = 0; i < nl.getLength(); i++) {
				Node n = (Node) nl.item(i);
				strarray.add((String) n.getTextContent());
				// Serialize the found nodes to System.out
				// serializer.transform(new DOMSource(n),new StreamResult(System.out));
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return strarray;
	}

}
