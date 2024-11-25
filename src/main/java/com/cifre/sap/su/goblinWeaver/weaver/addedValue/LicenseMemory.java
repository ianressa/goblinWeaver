package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.utils.ConstantProperties;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class LicenseMemory {
    private static HashMap<String, LicenseExpression> currentMemory = null;

    public static void appendToExpression(String key, LicenseExpression expression){
	LicenseExpression currentExpression = currentMemory.get(key);
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

    public static String findExpressionMatch(LicenseExpression expression){
	for (String key : currentMemory.keySet()){
	    LicenseExpression gotExp = currentMemory.get(key);
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
	try{
	    FileInputStream in = new FileInputStream(ConstantProperties.licenseMemoryPath);
	    ObjectInputStream oin = new ObjectInputStream(in);
	    // Fixme, bad way to do this
	    HashMap<String, LicenseExpression> obj = (HashMap<String, LicenseExpression>) oin.readObject();
	    oin.close();
	} catch (IOException e){
	    e.printStackTrace();
	} catch (ClassNotFoundException e){
	    e.printStackTrace();
	}
    }

    private static void writeMemoryToFile(){
	try{
	    FileOutputStream out = new FileOutputStream(ConstantProperties.licenseMemoryPath);
	    ObjectOutputStream oout = new ObjectOutputStream(out);
	    oout.writeObject(currentMemory);
	    oout.flush();
	    oout.close();
	} catch (IOException e){
	    e.printStackTrace();
	}
    }
}
