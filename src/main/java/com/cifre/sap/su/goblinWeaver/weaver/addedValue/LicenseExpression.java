package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.io.*;
import java.net.URL;
import java.util.HashSet;
import org.apache.commons.lang3.StringUtils;

public class LicenseExpression implements Serializable {
    private static final long serialVersionUID = 10L;
    private static final int FUZZY_THRESHOLD = 95;
    private HashSet<String> altNames;
    private HashSet<URL> urls;
    private String licenseText;

    public LicenseExpression(HashSet<String> altNames, HashSet<URL> urls, String licenseText){
	this.altNames = new HashSet<String>();
	this.urls = new HashSet<URL>();
	this.licenseText = "";
	if (altNames != null){
	    for (String name : altNames) {
		addName(name);
	    }
	}
	if (urls != null){
	    for (URL url : urls) {
		addURL(url);
	    }
	}
	setLicenseText(licenseText);
    }

    public LicenseExpression(String name, URL url, String licenseText){
	this.altNames = new HashSet<String>();
	this.urls = new HashSet<URL>();
	this.licenseText = "";
	if (name != null){ addName(name); }
	if (url != null){ addURL(url); }
	setLicenseText(licenseText);
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
	for (String namea : this.altNames) {
	    if (namea == null) {
		continue;
	    }
	    for (String nameb : expb.altNames) {
		if (nameb != null && FuzzySearch.weightedRatio(namea, nameb) >= FUZZY_THRESHOLD) {
		    return true;
		}
	    }
	}
	for (URL urla : this.urls) {
	    if (urla == null) {
		continue;
	    }
	    for (URL urlb : expb.urls) {
		if (urlb != null && urla.equals(urlb)) {
		    return true;
		}
	    }
	}
	if (this.licenseText != null && !this.licenseText.isEmpty() && expb.licenseText != null && !expb.licenseText.isEmpty()) {
	    if (FuzzySearch.weightedRatio(this.licenseText, expb.licenseText) >= FUZZY_THRESHOLD) {
		return true;
	    }
	}
	return false;
    }

    public static LicenseExpression Empty(){
	return new LicenseExpression(new HashSet<String>(), new HashSet<URL>(), new String());
    }

    private String normalizeText(String licenseText) {
	return StringUtils.trim(licenseText.replaceAll("\\s+", " "));
    }

    public boolean addName(String name) {
	if (name != null) {
	    return this.altNames.add(name);
	}
	return false;
    }

    public HashSet<String> getNames() {
	return altNames;
    }

    public boolean addURL(URL url) {
	if (url != null) {
	    return this.urls.add(url);
	}
	return false;
    }

    public HashSet<URL> getURLs() {
	return urls;
    }

    public boolean setLicenseText(String licenseText) {
	if (licenseText != null) {
	    this.licenseText = normalizeText(licenseText);
	    return true;
	}
	return false;
    }

    public String getLicenseText() {
	return licenseText;
    }

    @Override
    public String toString(){
	return this.altNames.toString() + "\n" + this.urls.toString() + "\n" + this.licenseText.toString();
    }
}
