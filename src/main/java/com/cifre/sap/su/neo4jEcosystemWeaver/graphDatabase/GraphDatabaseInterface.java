package com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase;

import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes.NodeType;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue.AddedValue;
import com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue.AddedValueEnum;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GraphDatabaseInterface {
    QueryDictionary getQueryDictionary();
    InternGraph executeQuery(String query);
    Map<String,Map<AddedValueEnum,String>> getNodeAddedValues(Set<String> nodeIds, List<AddedValueEnum> addedValues, NodeType nodeType);
    void addAddedValues(List<AddedValue<?>> computedAddedValues);
    void putOneAddedValueOnGraph(String nodeId, AddedValueEnum addedValueType, String value);
    void removeAddedValuesOnGraph(Set<AddedValueEnum> addedValuesType);
}
