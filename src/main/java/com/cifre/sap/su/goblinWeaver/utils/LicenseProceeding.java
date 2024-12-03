package com.cifre.sap.su.goblinWeaver.utils;

import com.cifre.sap.su.goblinWeaver.weaver.addedValue.LicenseExpression;
import com.cifre.sap.su.goblinWeaver.weaver.addedValue.LicenseMemory;
import org.apache.commons.io.FileUtils;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import java.io.*;
import java.nio.file.*;
import java.lang.StringBuilder;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


public class LicenseProceeding {
    private static final String REPOSITORY_URL = "https://repo1.maven.org/maven2";

    private static Logger logger = Logger.getLogger("licenseLog");
    private static SimpleFormatter formatter = new SimpleFormatter();
    private static FileHandler handler;

    public static void InitLicenseLogging(){
	try {
	    handler = new FileHandler("/tmp/goblinWeaver_licenseLog.log");
	    logger.addHandler(handler);
	    handler.setFormatter(formatter);
	} catch (SecurityException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
    }


    public static String getLicenseFromId(String nodeId) {
	String[] splitNodeId = nodeId.split(":");
	if (splitNodeId.length != 3){
	    return null;
	}
	String groupId = splitNodeId[0];
	String artifactId = splitNodeId[1];
	String version = splitNodeId[2];
	try {
	    URL pomURL = new URL(new String(REPOSITORY_URL + "/" + groupId.replace(".", "/") +
					    "/" + artifactId + "/" + version + "/" +
					    artifactId + "-" + version + ".pom"));
	    URL jarURL = new URL(new String(REPOSITORY_URL + "/" + groupId.replace(".", "/") +
					    "/" + artifactId + "/" + version + "/" +
					    artifactId + "-" + version + ".jar"));

	    NameURLTuple<String,URL> nameURL = getInfoFromPom(pomURL);
	    String licenseText = downloadLicenseFromJar(jarURL);

	    LicenseExpression inferredExpression = new LicenseExpression(nameURL.name, nameURL.url, licenseText);
	    logger.info("Created expression with: \n" +
			inferredExpression.getNames().toString() + ",\n" +
			inferredExpression.getURLs().toString() + ",\n" +
			(inferredExpression.isEmpty() ? "Empty" : "Not Empty"));
	    String expressionMatchKey = LicenseMemory.findExpressionMatch(inferredExpression);
	    if (expressionMatchKey == null){
		String newKey = LicenseMemory.addNewExpression(inferredExpression);
		logger.info("Created new key " + newKey);
		return newKey;
	    }
	    logger.info("Matched existing expression with key " + expressionMatchKey);
	    LicenseMemory.appendToExpression(expressionMatchKey, inferredExpression);
	    return expressionMatchKey;
	} catch(MalformedURLException e){
	    e.printStackTrace();
	}
	return null;
    }

    private static String downloadLicenseFromJar(URL url){
	System.out.println("Trying JAR URL at " + url.toString());
	try(InputStream in = url.openStream();
	    JarInputStream jarIn = new JarInputStream(in)){
	    JarEntry je;
	    while ((je = jarIn.getNextJarEntry()) != null){
		if (je.getName().endsWith("LICENSE") || je.getName().endsWith("LICENSE.txt")) {
		    System.out.println("Found LICENSE in archive at " + je.getName());
		    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		    for (int n = jarIn.read(); n != -1; n = jarIn.read()) {
			outStream.write(n);
		    }
		    outStream.close();
		    jarIn.closeEntry();
		    return outStream.toString();
		}
	    }
	    System.out.println("No error, but could not find LICENSE in jar");
	    jarIn.closeEntry();
	    return null;
	} catch (IOException e){
	    e.printStackTrace();
	    return null;
	}
    }

    private static NameURLTuple getInfoFromPom(URL url){
	System.out.println("Trying POM URL at " + url.toString());
	try(InputStream in = url.openStream();){
	    try{
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser parser = parserFactory.newSAXParser();
		POMLicenseHandler licenseHandler = new POMLicenseHandler();
		parser.parse(in, licenseHandler);

		// Log license name for survey
		String logText = new String();
		logText = new String("\t" +
				     ((licenseHandler.licenseName != null) ? licenseHandler.licenseName : "No_License_Name") +
				     "\t" +
				     ((licenseHandler.licenseURL != null) ? "(" + licenseHandler.licenseURL + ")" : "(No_License_URL)"));
		logger.info(logText);

		return new NameURLTuple(licenseHandler.licenseName, licenseHandler.licenseURL);
	    } catch (SAXException e){
		e.printStackTrace();
		return null;
	    } catch (ParserConfigurationException e){
		e.printStackTrace();
		return null;
	    }
	} catch (IOException e){
	    e.printStackTrace();
	    return null;
	}
    }

    private static class POMLicenseHandler extends DefaultHandler{
	public URL licenseURL = null;
	public String licenseName = null;
	private boolean readingLicenseElement = false;
	private StringBuilder stringBuilder = null;

	@Override
	public void characters(char ch[], int start, int length){
	    if (stringBuilder != null){
		stringBuilder.append(ch, start, length);
	    }
	}

	@Override
	public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException{
	    if (qName == "license" && (licenseURL == null && licenseName == null)){
		readingLicenseElement = true;
	    }
	    if (qName == "url" && licenseURL == null && readingLicenseElement){
		stringBuilder = new StringBuilder();
	    }
	    if (qName == "name" && licenseName == null && readingLicenseElement){
		stringBuilder = new StringBuilder();
	    }
	}

	@Override
	public void endElement(String uri, String lName, String qName) throws SAXException{
	    if (qName == "license"){
		readingLicenseElement = false;
	    }
	    if (qName == "url" && readingLicenseElement){
		try{
		    licenseURL = new URL(stringBuilder.toString());
		} catch (MalformedURLException e){
		    e.printStackTrace();
		    licenseURL = null;
		}
		stringBuilder = null;
	    }
	    if (qName == "name" && readingLicenseElement){
		licenseName = stringBuilder.toString();
		stringBuilder = null;
	    }
	}
    }

    private static class NameURLTuple<String, URL>{
	public final String name;
	public final URL url;
	public NameURLTuple(String name, URL url){
	    this.name = name;
	    this.url = url;
	}
    }
}
