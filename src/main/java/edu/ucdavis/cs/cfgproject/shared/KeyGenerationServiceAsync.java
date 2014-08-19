package edu.ucdavis.cs.cfgproject.shared;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;


public interface KeyGenerationServiceAsync{
	void retrieveCSV(List<Taxon> taxa, AsyncCallback<List<Taxon>> readTaxaCallback);
	
	void retrieveTaxa(List<Taxon> taxa, AsyncCallback<List<Taxon>> updateTaxaCallback);

	void retrieveCharactersAndIg(List<Taxon> taxa, AsyncCallback<HashMap<String, String>> updateCharactersCallback);
	
	void retrieveStatesAndSpecies(List<Taxon> taxa, String character, AsyncCallback<HashMap<String, List<Taxon>>> updateStatesCallback);
	
	void retrieveTaxaByCheckBoxes(List<Taxon> allTaxa, HashMap<String, HashMap<String, CheckBox>> characterStateCheckBoxMap, AsyncCallback<List<Taxon>> updateTaxaByCheckBoxesCallback);
}
