package com.cifre.sap.su.goblinWeaver.weaver.addedValue;

import com.cifre.sap.su.goblinWeaver.utils.OsvDataSingleton;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public enum LicenseEnum {
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
    public T getValue(){
        return value;
    }

    @Override
    public Map<String, Object> getValueMap(){
        return Collections.singletonMap(getAddedValueEnum().getJsonKey(), value);
    }

    @Override
    public void setValue(String value){
        this.value = this.stringToValue(value);
    }
}
