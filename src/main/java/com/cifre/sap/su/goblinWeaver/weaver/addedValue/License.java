package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.utils.OsvDataSingleton;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class License extends AbstractAddedValue<LicenseEnum>{

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
    public LicenseEnum getValue(){
        return value;
    }

    @Override
    public Map<String, Object> getValueMap(){
        return Collections.singletonMap(getAddedValueEnum().getJsonKey(), value);
    }

    @Override
    public LicenseEnum stringToValue(String string){
	return LicenseEnum.valueOf(string);
    }

    @Override
    public void computeValue(){
	this.value = LicenseEnum.agpl_v3;
    }

    @Override
    public String valueToString(LicenseEnum value){
	return value.name();
    }

    @Override
    public void setValue(String value){
        this.value = this.stringToValue(value);
    }
}
