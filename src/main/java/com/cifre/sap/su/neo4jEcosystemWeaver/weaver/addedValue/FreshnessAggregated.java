package com.cifre.sap.su.neo4jEcosystemWeaver.weaver.addedValue;

import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseInterface;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.InternGraph;
import com.cifre.sap.su.neo4jEcosystemWeaver.graphEntities.nodes.NodeObject;

import java.util.*;

public class FreshnessAggregated extends Freshness{

    public FreshnessAggregated(String gav) {
        super(gav);
    }

    @Override
    public AddedValueEnum getAddedValueEnum() {
        return AddedValueEnum.FRESHNESS_AGGREGATED;
    }

    public String getNodeId(){
        return gav;
    }

    @Override
    public void computeValue() {
        super.value = fillAggregatedFreshness(super.gav);
    }

    private Map<String, String> fillAggregatedFreshness(String gav){
        GraphDatabaseInterface gdb = GraphDatabaseSingleton.getInstance();
        // Check if value exist
        Map<String,Map<AddedValueEnum,String>> alreadyCalculatedAddedValue = gdb.getNodeAddedValues(Set.of(gav), List.of(getAddedValueEnum()), getAddedValueEnum().getTargetNodeType());
        // Value exist
        if(alreadyCalculatedAddedValue.containsKey(gav) && alreadyCalculatedAddedValue.get(gav).containsKey(getAddedValueEnum())){
            return this.stringToValue(alreadyCalculatedAddedValue.get(gav).get(getAddedValueEnum()));
        }
        else{
            // Compute release freshness
            int totalNumberMissedRelease = 0;
            long totalOutdatedTimeInMs = 0;
            Map<String, String> currentFreshnessValue = new HashMap<>(getFreshnessMapFromGav(gav));
            totalNumberMissedRelease += Integer.parseInt(currentFreshnessValue.get("numberMissedRelease"));
            totalOutdatedTimeInMs += Long.parseLong(currentFreshnessValue.get("outdatedTimeInMs"));

            InternGraph graph = gdb.executeQuery(gdb.getQueryDictionary().getReleaseDirectCompileDependencies(gav));
            for(NodeObject dep : graph.getGraphNodes()){
                Map<String, String> freshnessToAdd = fillAggregatedFreshness(dep.getId());
                totalNumberMissedRelease += Integer.parseInt(freshnessToAdd.get("numberMissedRelease"));
                totalOutdatedTimeInMs += Long.parseLong(freshnessToAdd.get("outdatedTimeInMs"));
            }
            Map<String, String> aggregatedFreshnessMap = new HashMap<>();
            aggregatedFreshnessMap.put("numberMissedRelease", Integer.toString(totalNumberMissedRelease));
            aggregatedFreshnessMap.put("outdatedTimeInMs", Long.toString(totalOutdatedTimeInMs));
            //Add calculated value on graph and return
            gdb.putOneAddedValueOnGraph(gav, getAddedValueEnum(), valueToString(aggregatedFreshnessMap));
            return aggregatedFreshnessMap;
        }
    }
}
