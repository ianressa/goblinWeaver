package com.cifre.sap.su.goblinWeaver;

import com.cifre.sap.su.goblinWeaver.graphDatabase.GraphDatabaseSingleton;
import com.cifre.sap.su.goblinWeaver.utils.GraphUpdatedChecker;
import com.cifre.sap.su.goblinWeaver.utils.OsvProceeding;
import com.cifre.sap.su.goblinWeaver.utils.LicenseProceeding;
import com.cifre.sap.su.goblinWeaver.weaver.addedValue.LicenseMemory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Neo4jEcosystemWeaverApplication {

	public static void main(String[] args) {
		LicenseProceeding.InitLicenseLogging();
		LicenseMemory.initMemory();
		Runtime.getRuntime().addShutdownHook(
						     new Thread("shutdown_hook"){
							 @Override
							 public void run() {
							     LicenseMemory.writeMemory();
							 }
						     }
						     );
		/*
		GraphDatabaseSingleton.getInstance(); // Init database connection
		GraphUpdatedChecker.deleteAddedValuesIfUpdated(); // Check if database was updated
		OsvProceeding.initOsvData(args); // Download CVE dataset
		*/
		SpringApplication.run(Neo4jEcosystemWeaverApplication.class, args); // Run API
	}

}
