package com.cifre.sap.su.goblinWeaver.utils;

import com.cifre.sap.su.goblinWeaver.weaver.addedValue.LicenseData;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


public class LicenseProceeding {
    private static final String ROOT_PATH = ConstantProperties.licenseDataFolderPath;
    private static final String DATA_PATH = ROOT_PATH + File.separator + "maven";
    private static final String REPOSITORY_URL = "https://repo1.maven.org/maven2";

	public static LicenseData.LicenseEnum getLicenseFromId(String nodeId) {
	    String[] splitNodeId = nodeId.split(":");
	    if (splitNodeId.length != 3){
		return null;
	    }
	    String groupId = splitNodeId[0];
	    String artifactId = splitNodeId[1];
	    String version = splitNodeId[2];

	    
	    File licenseFile = downloadLicenseData(groupId, artifactId, version);
	    if(licenseFile != null){
		return inferLicenseFromFile(licenseFile);
	    }
	    return null;
	}

    private static LicenseData.LicenseEnum inferLicenseFromFile(File licenseFile) {
	try {
	    for (EnumMap.Entry<LicenseData.LicenseEnum, File> entry : LicenseData.LicenseFileMap.entrySet()){
		System.out.println("Checking if " + licenseFile.getName() + " is equivalent to " + entry.getValue().getName());
		if (FileUtils.contentEquals(licenseFile, entry.getValue())) {
		    return entry.getKey();
		}
	    }
	} catch (IOException e){
	    e.printStackTrace();
	    return null;
	}
	    System.out.println("No error, but no existing license matched " + licenseFile.getName());
	    return null;
    }

    private static File downloadLicenseData(String groupId, String artifactId, String version){
	System.out.println("Downloading license data for " + groupId + ":" + artifactId + ":" + version);
	File rootDir = new File(ROOT_PATH);
	File groupDir = new File(DATA_PATH + File.separator + groupId);
	File artifactDir = new File(groupDir + File.separator + artifactId);
	File versionDir = new File(artifactDir + File.separator + version);
	File outfile = new File(versionDir + File.separator + "LICENSE");
	
	if (rootDir.exists()){
	    rootDir.delete();
	}
	versionDir.mkdirs();
	
	try {
	    URL url = new URL(new String(REPOSITORY_URL + "/" + groupId.replace(".", "/") +
					 "/" + artifactId + "/" + version + "/" +
					 artifactId + "-" + version + ".jar"));
	    System.out.println("Trying JAR URL at " + url.toString());
	    try(InputStream in = url.openStream();
		JarInputStream jarIn = new JarInputStream(in)){
		JarEntry je;
		while ((je = jarIn.getNextJarEntry()) != null){
		    if (je.getName().endsWith("LICENSE") || je.getName().endsWith("LICENSE.txt")) {
			System.out.println("Found LICENSE in archive at " + je.getName());
			if(outfile.exists()) {
			    outfile.delete();
			}
			FileOutputStream outStream = new FileOutputStream(outfile);
			for (int n = jarIn.read(); n != -1; n = jarIn.read()) {
			    outStream.write(n);
			}
			outStream.close();
			jarIn.closeEntry();
			return outfile;
		    }
		}
		System.out.println("No error, but could not find LICENSE in jar");
		jarIn.closeEntry();
		return null;
	    } catch(IOException e){
		e.printStackTrace();
		return null;
	    }
	} catch (MalformedURLException e){
	    e.printStackTrace();
	    return null;
	}
    }
}
