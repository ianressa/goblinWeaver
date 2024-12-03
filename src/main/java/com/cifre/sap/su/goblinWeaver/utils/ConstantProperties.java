package com.cifre.sap.su.goblinWeaver.utils;

import java.io.File;

public class ConstantProperties {

    // Paths
    public static final String dataFolderPath = "goblinWeaver_data";
    public static final String licenseMemoryPath = dataFolderPath + "/licenseMemory.json";
    public static final String licenseSeedPath = dataFolderPath + "/licenseSeed.json";
    public static final String osvDataFolderPath = dataFolderPath + "/osvData";
    public static final String databaseStatusFile = dataFolderPath+"/databaseStatus.txt";

    public static void initDataFolder() {
	File f = new File(dataFolderPath);
	f.mkdirs();
    }
}
