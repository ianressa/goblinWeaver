package com.cifre.sap.su.goblinWeaver.utils;

import com.cifre.sap.su.goblinWeaver.weaver.addedValue.LicenseData;
import org.apache.commons.io.IOUtils;

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
	    
	    if (downloadLicenseData(groupId, artifactId, version)){
		File licenseFile = extractLicenseFile(groupId, artifactId, version);
		if(licenseFile != null){
		    return inferLicenseFromFile(licenseFile);
		}
	    }
	    return null;
	}

	private static File extractLicenseFile(String groupId, String artifactId, String version) {
	File groupDir = new File(DATA_PATH + File.separator + groupId);
	File artifactDir = new File(groupDir + File.separator + artifactId);
	File versionDir = new File(artifactDir + File.separator + version);
	File archive = new File(versionDir + File.separator + artifactId + "-" + version + ".jar");
	File outfile = new File(versionDir + File.separator + "LICENSE");

	if (!archive.exists()){
	    return null;
	}

	try(FileInputStream in = new FileInputStream(archive);
	    JarInputStream jarIn = new JarInputStream(in)) {
	    JarEntry je;
	    
	    while ((je = jarIn.getNextJarEntry()) != null) {
		if (je.getName().equals("/META-INF/LICENSE")) {
		    if (outfile.exists()) {
			outfile.delete();
		    }
		    FileOutputStream outStream = new FileOutputStream(outfile);
		    for (int n = jarIn.read(); n != -1; n = jarIn.read()) {
			outStream.write(n);
		    }
		    outStream.close();
		    return outfile;
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    return null;
	}
        return null;
    }

    private static LicenseData.LicenseEnum inferLicenseFromFile(File licenseFile) {
	try (FileInputStream licenseIn = new FileInputStream(licenseFile)) {
	    for (EnumMap.Entry<LicenseData.LicenseEnum, File> entry : LicenseData.LicenseFileMap.entrySet()) {
		try(FileInputStream licenseMapIn = new FileInputStream(entry.getValue())){
		    if (IOUtils.contentEquals(licenseIn, licenseMapIn)) {
			licenseIn.close();
			licenseMapIn.close();
			return entry.getKey();
		    }
		    licenseMapIn.close();
		} catch (IOException e) {
		    e.printStackTrace();
		    return null;
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    return null;
	}
	return null;
    }

    

    private static boolean downloadLicenseData(String groupId, String artifactId, String version){
	System.out.println("Downloading license data for " + groupId + ":" + artifactId + ":" + version);
	String[] TldDomain = groupId.split(".");
	File rootDir = new File(ROOT_PATH);
	File dataDir = new File(DATA_PATH);
	File groupDir = new File(DATA_PATH + File.separator + groupId);
	File artifactDir = new File(groupDir + File.separator + artifactId);
	File versionDir = new File(artifactDir + File.separator + version);
	
	if (rootDir.exists()){
	    rootDir.delete();
	}
	versionDir.mkdirs();
	
	try {
	    URL url = new URL(new String(REPOSITORY_URL + "/" + TldDomain[0] + "/" + TldDomain[1] + "/" +
					 "/" + artifactId + "/" + version + "/" +
					 artifactId + "-" + version + ".jar"));
	    try(InputStream in = url.openStream();
		JarInputStream jarIn = new JarInputStream(in)){
		
		JarEntry entry;
		byte[] buffer = new byte[1024];
		
		while ((entry = jarIn.getNextJarEntry()) != null){
		    String filePath = versionDir + entry.getName();
		    if (!entry.isDirectory()){
			try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
			    int read;
			    while((read = jarIn.read(buffer)) != -1){
				bos.write(buffer, 0, read);
			    }
			}
		    } else{
			File subDir = new File(filePath);
			subDir.mkdir();
		    }
		    jarIn.closeEntry();
		}
	    } catch(IOException e){
		e.printStackTrace();
		return false;
	    }
	} catch (MalformedURLException e){
	    e.printStackTrace();
	    return false;
	}
	return true;
    }
}
