package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.utils.ConstantProperties;

import java.io.*;
import java.net.URL;
import java.util.HashSet;

public class LicenseExpression implements Serializable {
    private static final long serialVersionUID = 10L;
    public HashSet<String> altNames;
    public HashSet<URL> urls;
    public String licenseText;

    public LicenseExpression(HashSet<String> altNames, HashSet<URL> urls, String licenseText){
	this.altNames = altNames;
	this.urls = urls;
	this.licenseText = licenseText;
    }
}
