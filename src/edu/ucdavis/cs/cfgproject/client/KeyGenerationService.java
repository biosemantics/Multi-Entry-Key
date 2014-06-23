package edu.ucdavis.cs.cfgproject.client;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.ucdavis.cs.cfgproject.shared.Taxon;

//get the pool of the taxa, if the pool is empty, read the input CSV file
@RemoteServiceRelativePath("keyGeneration")
public interface KeyGenerationService extends RemoteService{
	
	List<Taxon> retrieveTaxa(List<Taxon> taxa, List<String> characters);
	
	HashMap<String, String> retrieveCharactersAndIg(List<Taxon> taxa, List<String> characters);
	
	HashMap<String, List<Taxon>> retreieveStatesAndSpecies(List<Taxon> taxa, List<String> characters, String character);
}
