package edu.ucdavis.cs.cfgproject.client;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.ui.CheckBox;

import edu.ucdavis.cs.cfgproject.shared.Taxon;

//get the pool of the taxa, if the pool is empty, read the input CSV file
@RemoteServiceRelativePath("keyGeneration")
public interface KeyGenerationService extends RemoteService{
	
	List<Taxon> retrieveCSV(List<Taxon> taxa);
	
	List<Taxon> retrieveTaxa(List<Taxon> taxa);
	
	HashMap<String, String> retrieveCharactersAndIg(List<Taxon> taxa);
	
	HashMap<String, List<Taxon>> retrieveStatesAndSpecies(List<Taxon> taxa, String character);
	
	List<Taxon> retrieveTaxaByCheckBoxes(List<Taxon> allTaxa, HashMap<String, HashMap<String, CheckBox>> characterStateCheckBoxMap);
}
