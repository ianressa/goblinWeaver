package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.utils.ConstantProperties;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class LicenseMemory {
    private static HashMap<String, LicenseExpression> currentMemory = null;
    private static String emptyItemId = "0";

    public static void appendToExpression(String key, LicenseExpression expression){
	LicenseExpression currentExpression = new LicenseExpression(currentMemory.get(key));
	currentExpression.altNames.addAll(expression.altNames);
	currentExpression.urls.addAll(expression.urls);
	if ((currentExpression.licenseText == null || currentExpression.licenseText.trim().isEmpty()) &&
	    expression.licenseText != null && !expression.licenseText.trim().isEmpty()){
	    currentExpression.licenseText = expression.licenseText;
	}
	currentMemory.put(key, currentExpression);
    }

    public static String addNewExpression(LicenseExpression expression){
	String newKey = UUID.randomUUID().toString();
	currentMemory.put(newKey, expression);
	return newKey;
    }

    private static void addNewExpression(String id, LicenseExpression expression){
	currentMemory.put(id, expression);
    }

    public static String findExpressionMatch(LicenseExpression expression){
	if (currentMemory == null || currentMemory.size() == 0) {
	    return null;
	}
	if (expression.isEmpty()) {
	    return emptyItemId;
	}
	for (String key : currentMemory.keySet()){
	    LicenseExpression gotExp = new LicenseExpression(currentMemory.get(key));
	    gotExp.altNames.retainAll(expression.altNames);
	    gotExp.urls.retainAll(expression.urls);
	    if((!gotExp.altNames.isEmpty()) || (!gotExp.urls.isEmpty()) ||
	       (gotExp.licenseText.equals(expression.licenseText))){
		return key;
	    }
	}
	return null;
    }

    public static void initMemory(){
	System.out.println("Reading current license data...");
	readMemoryFromFile();
	if (currentMemory == null){
	    System.out.println("Couldn't read data. Starting fresh...");
	    currentMemory = new HashMap<String, LicenseExpression>();
	    addNewExpression(emptyItemId, LicenseExpression.Empty());
	    return;
	}
	System.out.println("Done. Read " + String.valueOf(currentMemory.size()) + " elements.");
    }

    public static void writeMemory(){
	System.out.println("Saving current license data (" + String.valueOf(currentMemory.size()) + " elements)...");
	if (currentMemory != null){
	    writeMemoryToFile();
	}
    }

    private static void readMemoryFromFile(){
	Gson gs = new Gson();
	try{
	    File licenseMemoryFile = new File(ConstantProperties.licenseMemoryPath);
	    if (!licenseMemoryFile.exists()){
		licenseMemoryFile.createNewFile();
	    }
	    FileInputStream in = new FileInputStream(licenseMemoryFile);
	    String json = IOUtils.toString(in, StandardCharsets.UTF_8);
	    in.close();
	    currentMemory = gs.fromJson(json,
					new TypeToken<HashMap<String, LicenseExpression>>() {}.getType());
	} catch (IOException | JsonParseException e) {
	    e.printStackTrace();
	}
    }

    private static void writeMemoryToFile(){
	Gson gs = new Gson();
	try{
	    File licenseMemoryFile = new File(ConstantProperties.licenseMemoryPath);
	    if (!licenseMemoryFile.exists()){
		licenseMemoryFile.createNewFile();
	    }
	    FileOutputStream out = new FileOutputStream(licenseMemoryFile);
	    ObjectOutputStream oout = new ObjectOutputStream(out);
	    oout.writeObject(gs.toJson(currentMemory));
	    oout.flush();
	    oout.close();
	} catch (IOException e){
	    e.printStackTrace();
	}
    }
}
