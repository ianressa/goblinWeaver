package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.utils.ConstantProperties;

import java.io.File;
import java.util.EnumMap;

public class LicenseData {
    public static enum LicenseEnum {
	agpl_v3,
	apache_v2,
	bsd_2,
	bsd_3,
	cddl_v1,
	epl_only_v1,
	epl_only_v2,
	epl_v1,
	epl_v2,
	eupl_v1_1,
	fdl_v1_3,
	gpl_v1,
	gpl_v2,
	gpl_v3,
	lgpl_v2_1,
	lgpl_v3,
	mit;
    }

    public static EnumMap<LicenseEnum, File> LicenseFileMap = new EnumMap<>(LicenseEnum.class){{
	    put(LicenseEnum.agpl_v3, new File(ConstantProperties.licenseFilePath + File.separator + "agpl-3.0.txt"));
	    put(LicenseEnum.apache_v2, new File(ConstantProperties.licenseFilePath + File.separator + "apache-2.0.txt"));
	    put(LicenseEnum.bsd_2, new File(ConstantProperties.licenseFilePath + File.separator + "bsd-2.txt"));
	    put(LicenseEnum.bsd_3, new File(ConstantProperties.licenseFilePath + File.separator + "bsd-3.txt"));
	    put(LicenseEnum.cddl_v1, new File(ConstantProperties.licenseFilePath + File.separator + "cddl-1.0.txt"));
	    put(LicenseEnum.epl_only_v1, new File(ConstantProperties.licenseFilePath + File.separator + "epl-1.0.txt"));
	    put(LicenseEnum.epl_only_v2, new File(ConstantProperties.licenseFilePath + File.separator + "epl-2.0.txt"));
	    put(LicenseEnum.gpl_v1, new File(ConstantProperties.licenseFilePath + File.separator + "gpl-1.txt"));
	    put(LicenseEnum.gpl_v2, new File(ConstantProperties.licenseFilePath + File.separator + "gpl-2.0.txt"));
	    put(LicenseEnum.gpl_v3, new File(ConstantProperties.licenseFilePath + File.separator + "gpl-3.0.txt"));
	    put(LicenseEnum.lgpl_v2_1, new File(ConstantProperties.licenseFilePath + File.separator + "lgpl-2.1.txt"));
	    put(LicenseEnum.mit, new File(ConstantProperties.licenseFilePath + File.separator + "mit.txt"));
	}};
}
