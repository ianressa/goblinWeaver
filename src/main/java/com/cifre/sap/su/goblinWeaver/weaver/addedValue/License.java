package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.utils.LicenseProceeding;

import java.util.*;

public class License extends AbstractAddedValue<String>{

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
    public String getValue(){
        return value;
    }

    @Override
    public Map<String, Object> getValueMap(){
        return Collections.singletonMap(getAddedValueEnum().getJsonKey(), value);
    }

    @Override
    public String stringToValue(String string){
	return string;
    }

    @Override
    public void computeValue(){
	this.value = getLicenseFromId(nodeId);
    }

    @Override
    public String valueToString(String value){
	return value;
    }

    @Override
    public void setValue(String value){
        this.value = this.stringToValue(value);
    }

    protected static String getLicenseFromId(String nodeId) {
	return LicenseProceeding.getLicenseFromId(nodeId);
    }
}
