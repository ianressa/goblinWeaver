package com.cifre.sap.su.goblinWeaver.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;

public class MvnParser {
    private static final String REPOSITORY_URL = "https://mvnrepository.com";
    private static final String LICENSE_CLASS = "b lic";

    public static String getLicenseFromMvn(String groupId, String artifactId, String version){
	Document doc = null;
	try{
	    doc = Jsoup.connect(REPOSITORY_URL + "/artifact/" + groupId + "/" + artifactId + "/" + version).get();
	} catch (IOException e){
	    e.printStackTrace();
	    return "nodoc";
	}
	if (doc != null){
	    Elements licenses = doc.getElementsByClass(LICENSE_CLASS);
	    return licenses.first().toString();
	}
	return "nolicense";
    }
}
