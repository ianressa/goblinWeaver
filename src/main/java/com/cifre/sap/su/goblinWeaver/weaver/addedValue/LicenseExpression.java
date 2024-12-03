package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.io.*;
import java.net.URL;
import java.util.HashSet;
import org.apache.commons.lang3.StringUtils;

public class LicenseExpression implements Serializable {
    private static final long serialVersionUID = 10L;
    private static final int FUZZY_THRESHOLD = 95;
    public HashSet<String> altNames;
    public HashSet<URL> urls;
    public String licenseText;

    public LicenseExpression(HashSet<String> altNames, HashSet<URL> urls, String licenseText){
	if (altNames != null){
	    this.altNames = altNames;
	} else{
	    this.altNames = new HashSet<String>();
	}
	if (urls != null){
	    this.urls = urls;
	} else{
	    this.urls = new HashSet<URL>();
	}
	if (licenseText == null){
	    this.licenseText = "";
	} else{
	    this.licenseText = normalizeText(licenseText);
	}
    }

    public LicenseExpression(String name, URL url, String licenseText){
	this.altNames = new HashSet<String>();
	this.urls = new HashSet<URL>();
	if (name != null){ this.altNames.add(name); }
	if (url != null){ this.urls.add(url); }
	if (licenseText == null){
	    this.licenseText = "";
	} else{
	    this.licenseText = normalizeText(licenseText);
	}
    }

    public LicenseExpression deepCopy(){
	try {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(this);
	    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	    ObjectInputStream ois = new ObjectInputStream(bais);
	    return (LicenseExpression) ois.readObject();
	} catch (IOException | ClassNotFoundException e){
	    return null;
	}
    }

    public boolean isEmpty(){
	return
	    (this.altNames == null || this.altNames.isEmpty()) &&
	    (this.urls == null || this.urls.isEmpty()) &&
	    (this.licenseText == null || this.licenseText.isEmpty());
    }

    public boolean fuzzyMatches(LicenseExpression expb){
	for (String namea : this.altNames){
	    for (String nameb : expb.altNames){
		if (FuzzySearch.weightedRatio(namea, nameb) >= FUZZY_THRESHOLD){
		    return true;
		}
	    }
	}
	for (URL urla : this.urls){
	    for (URL urlb : expb.urls){
		if (urla.toString().equals(urlb.toString())){
		    return true;
		}
	    }
	}
	if (FuzzySearch.weightedRatio(this.licenseText, expb.licenseText) >= FUZZY_THRESHOLD){
	    return true;
	}
	return false;
    }

    public static LicenseExpression Empty(){
	return new LicenseExpression(new HashSet<String>(), new HashSet<URL>(), new String());
    }

    private String normalizeText(String licenseText) {
	return StringUtils.trim(licenseText.replaceAll("\\s+", " "));
    }


    @Override
    public String toString(){
	return this.altNames.toString() + "\n" + this.urls.toString() + "\n" + this.licenseText.toString();
    }
}
