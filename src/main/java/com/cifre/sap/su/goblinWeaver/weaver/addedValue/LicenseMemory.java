package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.utils.ConstantProperties;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class LicenseMemory {
    private static HashMap<String, LicenseExpression> currentMemory = null;
    private static String emptyItemId = "0";

    public static void appendToExpression(String key, LicenseExpression expression){
	if (key.equals(emptyItemId)) {
	    return;
	}
	LicenseExpression currentExpression = currentMemory.get(key).deepCopy();
	if (!currentExpression.altNames.isEmpty() && currentExpression.altNames != null) {
	    currentExpression.altNames.addAll(expression.altNames);
	}
	if (!currentExpression.urls.isEmpty() && currentExpression.urls != null) {
	    currentExpression.urls.addAll(expression.urls);
	}
	if ((currentExpression.licenseText == null || currentExpression.licenseText.trim().isEmpty()) &&
	    expression.licenseText != null && !expression.licenseText.trim().isEmpty()){
	    currentExpression.licenseText = expression.licenseText;
	}
	currentMemory.put(key, currentExpression);
    }

    public static String addNewExpression(LicenseExpression expression){
	String newKey = UUID.randomUUID().toString();
	currentMemory.put(newKey, expression);
	System.out.println("Key: " + newKey + "\nValue: " + currentMemory.get(newKey).toString());
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
	    if (key.equals(emptyItemId)) {
		continue;
	    }
	    LicenseExpression gotExp = currentMemory.get(key).deepCopy();
	    if (gotExp.fuzzyMatches(expression)){
		return key;
	    }
	}
	return null;
    }

    public static void initMemory(){
	System.out.println("Reading current license data...");
	readMemoryFromFile(ConstantProperties.licenseMemoryPath);
	if (currentMemory == null){
	    System.out.println("Couldn't read data. Starting fresh from seed file...");
	    readMemoryFromFile(ConstantProperties.licenseSeedPath);
	    if (currentMemory == null) {
		System.out.println("Warning: Couldn't read from seed file. Starting with a blank set.");
		currentMemory = new HashMap<String, LicenseExpression>();
		addNewExpression(emptyItemId, LicenseExpression.Empty());
	    }
	    return;
	}
	System.out.println("Done. Read " + String.valueOf(currentMemory.size()) + " elements.");
    }

    public static void writeMemory(){
	System.out.println("Saving current license data (" + String.valueOf(currentMemory.size()) + " elements)...");
	if (currentMemory != null){
	    writeMemoryToFile(ConstantProperties.licenseMemoryPath);
	}
    }

    private static void readMemoryFromFile(String path){
	Gson gs = new Gson();
	try{
	    File licenseMemoryFile = new File(path);
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

    private static void writeMemoryToFile(String path){
	Gson gs = new GsonBuilder().
	    enableComplexMapKeySerialization()
	    .setPrettyPrinting()
	    .create();
	try{
	    File licenseMemoryFile = new File(path);
	    if (!licenseMemoryFile.exists()){
		licenseMemoryFile.createNewFile();
	    }
	    FileWriter fw = new FileWriter(licenseMemoryFile);
	    gs.toJson(currentMemory, fw);
	    fw.close();
	    return;
	} catch (IOException e){
	    e.printStackTrace();
	}
    }
}
