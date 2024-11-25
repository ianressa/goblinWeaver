package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.utils.ConstantProperties;

import java.io.*;
import java.util.HashMap;

public class LicenseMemory {
    public static HashMap<String, LicenseExpression> currentMemory = null;

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
