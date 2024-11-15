package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.utils.LicenseProceeding;

import java.util.*;

public class License extends AbstractAddedValue<LicenseData.LicenseEnum>{

    public License(String nodeId){
        super(nodeId);
    }

    @Override
    public AddedValueEnum getAddedValueEnum(){
	return AddedValueEnum.LICENSE;
    }

    @Override
    public String getNodeId(){
        return nodeId;
    }

    @Override
    public LicenseData.LicenseEnum getValue(){
        return value;
    }

    @Override
    public Map<String, Object> getValueMap(){
        return Collections.singletonMap(getAddedValueEnum().getJsonKey(), value);
    }

    @Override
    public LicenseData.LicenseEnum stringToValue(String string){
	return LicenseData.LicenseEnum.valueOf(string);
    }

    @Override
    public void computeValue(){
	this.value = getLicenseFromId(nodeId);
    }

    @Override
    public String valueToString(LicenseData.LicenseEnum value){
	return value.name();
    }

    @Override
    public void setValue(String value){
        this.value = this.stringToValue(value);
    }

    protected static LicenseData.LicenseEnum getLicenseFromId(String nodeId) {
	return LicenseProceeding.getLicenseFromId(nodeId);
    }
}
