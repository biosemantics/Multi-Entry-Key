package edu.ucdavis.cs.cfgproject.client;

import java.io.*;
import java.util.*;

import edu.ucdavis.cs.cfgproject.shared.TaxonManager;
import edu.ucdavis.cs.cfgproject.shared.Taxon;
import au.com.bytecode.opencsv.*;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CfgProject implements EntryPoint {
	
	private HorizontalPanel mainPanel = new HorizontalPanel(); 
	private VerticalPanel westPanel = new VerticalPanel();
	private VerticalPanel centerPanel = new VerticalPanel();
	private VerticalPanel eastPanel = new VerticalPanel();
	private KeyGenerationServiceAsync KeyGenerationSvc = GWT.create(KeyGenerationService.class);
	CellTable<String> taxaTable = new CellTable<String>();
	CellTable<HashMap<String, String>> characterTable = new CellTable<HashMap<String, String>>();
	CellTable<HashMap<String, String>> stateTable = new CellTable<HashMap<String, String>>();
	private List<Taxon> taxaPool = new LinkedList<Taxon>();
	private List<String> charactersPool = new LinkedList<String>();
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
				
		/*
		 * Start here refresh the remaining taxa
		 */
		updateTaxa(taxaPool, charactersPool);
	
		
		// add stuff to west panel
		westPanel.add(new Label("You selected taxa: "));
		taxaTable.setPageSize(200);
		westPanel.add(taxaTable);
		
		// add stuff to center panel
		centerPanel.add(new Label("The avaiable characters and IGs: "));
		characterTable.setPageSize(200);
		centerPanel.add(characterTable);
		
	
		// add stuff to east panel;
		eastPanel.add(new Label("The states and the taxa: "));
		stateTable.setPageSize(200);
		eastPanel.add(stateTable);
		
		// add three big panels to main panel
		mainPanel.add(westPanel);
		mainPanel.add(centerPanel);
		mainPanel.add(eastPanel);
		
		// associate the main panel with the HTML host page.  
		RootPanel.get("cfgproject").add(mainPanel);
		
		
	}


	public void updateTaxa(List<Taxon> taxa, final List<String> characters) {
		
		/*
		 * RPC service that retrieve taxa names
		 */
		// initialize the service proxy
		if (KeyGenerationSvc == null) {
			KeyGenerationSvc = GWT.create(KeyGenerationService.class);
		}
//		KeyGenerationSvc = GWT.create(KeyGenerationService.class);
		
		// set up the callback object
		AsyncCallback<List<Taxon>> updateTaxaCallback = new AsyncCallback<List<Taxon>>() {
			public void onFailure(Throwable caught) {
			}	
			public void onSuccess(List<Taxon> remainingTaxa) {
				// set all remaining taxa
				setTaxaTable(remainingTaxa, characters, taxaTable);
										
			}
		};
		// Make the call to read matrix service
		KeyGenerationSvc.retrieveTaxa(taxa, characters, updateTaxaCallback);
		
	}
	
	public void updateCharactersAndIg (List<Taxon> taxa, final List<String> characters) {
		
		final List<Taxon> remainingTaxa = taxa;
		AsyncCallback<HashMap<String, String>> updateCharactersCallback = new AsyncCallback<HashMap<String, String>>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(HashMap<String, String> result) {
				setCharacterAngIgTable(remainingTaxa, characters, result, characterTable);
				
			}
		};
		KeyGenerationSvc.retrieveCharactersAndIg(remainingTaxa,characters,updateCharactersCallback);
	}
	
	public void updateStates (List<Taxon> taxa, final List<String> remainingCharacters, String character) {
		final List<Taxon> remainingTaxa = taxa;
		AsyncCallback<HashMap<String, List<Taxon>>> updateStatesCallback = new AsyncCallback<HashMap<String, List<Taxon>>>() {

			@Override
			public void onFailure(Throwable caught) {				
			}

			@Override
			public void onSuccess(HashMap<String, List<Taxon>> result) {
				setStatesTable(remainingTaxa, remainingCharacters, result, stateTable);
			}
			
		};
		KeyGenerationSvc.retreieveStatesAndSpecies(remainingTaxa, remainingCharacters, character, updateStatesCallback);
		
	}
	
	
	

	/*
	 * set all remaining taxa
	 */
	public void setTaxaTable(List<Taxon> taxa, List<String> characters, CellTable<String> taxaTable) {
		
		// clear the table first
		while (taxaTable.getColumnCount() !=0 ) {
			taxaTable.removeColumn(0);
		}
		
		List<String> dataToShow = new LinkedList<String>();
		
		for(Taxon taxon : taxa) {
			dataToShow.add(taxon.getName());
		}
		
		// Add a text column to show the name.
		TextColumn<String> nameColumn = new TextColumn<String>() {

			@Override
			public String getValue(String object) {
				return object;
			}
			
		};
		
		// Make the taxa name column sortable.
	    nameColumn.setSortable(true);
	    
		taxaTable.addColumn(nameColumn,"All remaining taxa");
		
		ListDataProvider<String> dataProvider = new ListDataProvider<String>();
		dataProvider.addDataDisplay(taxaTable);
		List<String> list = dataProvider.getList();
		for (String taxaName : dataToShow) {
			list.add(taxaName);
		}
		
		// Add a ColumnSortEvent.ListHandler to connect sorting to the java.util.List.
	    ListHandler<String> columnSortHandler = new ListHandler<String>(list);
	    columnSortHandler.setComparator(nameColumn, new Comparator<String>() {
	    	public int compare(String o1, String o2) {
	    		if (o1 == o2) {
	    			return 0;
	    		}

	    		// Compare the name columns.
	    		if (o1 != null) {
	    			return (o2 != null) ? o1.toString().compareTo(o2.toString()) : 1;
	    		}
	            return -1;
	          }
	        });
	    taxaTable.addColumnSortHandler(columnSortHandler);
	    // We know that the data is sorted alphabetically by default.
	    taxaTable.getColumnSortList().push(nameColumn);
	    
		
	    // issue another RPC to get characters and information gain
		updateCharactersAndIg(taxa, characters);
	}	
	
	/*
	 * set all remaining states
	 */
	public void setStatesTable(final List<Taxon> remainingTaxa, final List<String> remainingCharacters, final HashMap<String, List<Taxon>> result, CellTable<HashMap<String, String>> stateTable) {
		
		// clear the table first
		while (stateTable.getColumnCount() !=0 ) {
			stateTable.removeColumn(0);
		}
				
		List<HashMap<String, String>> dataToShow = new LinkedList<HashMap<String, String>>();
		
		for (Map.Entry<String, List<Taxon>> entry : result.entrySet()) {
			String key = entry.getKey();
			List<Taxon> value = entry.getValue();
			String speciesNames = "";
			for (Taxon taxon : value) {
				speciesNames = speciesNames + taxon.getName() + ";\n";
			}
			HashMap<String, String> tmp = new HashMap<String, String>();
			tmp.put(key, speciesNames);
			dataToShow.add(tmp);
		}
		
		
		TextColumn<HashMap<String, String>> statesColumn = new TextColumn<HashMap<String, String>>() {

			@Override
			public String getValue(HashMap<String, String> object) {
				String statesName = "";
				for (String key : object.keySet()) {
					statesName = key;
				}
				return statesName;
			}
			
		};
		
		TextColumn<HashMap<String, String>> speciesColumn = new TextColumn<HashMap<String, String>>() {

			@Override
			public String getValue(HashMap<String, String> object) {
				String speciesNames = "";
				for (String key : object.values()) {
					speciesNames = key;
				}
				return speciesNames;
			}
			
		};
		
		stateTable.addColumn(statesColumn,"Show you all states");
		stateTable.addColumn(speciesColumn,"Show you all species");
		
		ListDataProvider<HashMap<String, String>> dataProvider = new ListDataProvider<HashMap<String, String>>();
		dataProvider.addDataDisplay(stateTable);
		List<HashMap<String, String>> list = dataProvider.getList();
		for (HashMap<String, String> stateSpPair : dataToShow) {
			list.add(stateSpPair);
		}
		
		// add a selection model to handle user selection
		final SingleSelectionModel<HashMap<String, String>> selectionModel = new SingleSelectionModel<HashMap<String, String>>();
		stateTable.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				HashMap<String, String> selected = selectionModel.getSelectedObject();
				if (selected != null) {
					
					String stateName = "";
					for (String key : selected.keySet()) {
						stateName = key;
					}
					
					// issue another RPC to get the updated taxa pool
					for (Map.Entry<String, List<Taxon>> entry : result.entrySet()) {
						String key = entry.getKey();
						List<Taxon> newTaxa = entry.getValue();
						if (key.equals(stateName)) {
							updateTaxa(newTaxa, remainingCharacters);
							break;
						}

					}
					
					
				}
			}
		});
		
		
	}
	
	public void setCharacterAngIgTable(final List<Taxon> remainingTaxa, final List<String> characters, HashMap<String, String> result, CellTable<HashMap<String, String>> characterTable) {
		
		// clear the table first
		while (characterTable.getColumnCount() !=0 ) {
			characterTable.removeColumn(0);
		}
		
		//use the "result" to update characters
		final List<String> originCharacters = new LinkedList<String>();
		final List<String> newCharacters = new LinkedList<String>();	

		List<HashMap<String, String>> dataToShow = new LinkedList<HashMap<String, String>>();
		
		for (Map.Entry<String, String> entry : result.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			//use the "result" to update characters
			originCharacters.add(key);
			HashMap<String, String> tmp = new HashMap<String, String>();
			tmp.put(key, value);
			dataToShow.add(tmp);
		}
		
		
		TextColumn<HashMap<String, String>> charactersColumn = new TextColumn<HashMap<String, String>>() {

			@Override
			public String getValue(HashMap<String, String> object) {
				String charName = "";
				for (String key : object.keySet()) {
					charName = key;
				}
				return charName;
			}
			
		};
		
		TextColumn<HashMap<String, String>> igsColumn = new TextColumn<HashMap<String, String>>() {

			@Override
			public String getValue(HashMap<String, String> object) {
				String ig = "";
				for (String key : object.values()) {
					ig = key;
				}
				return ig;
			}
			
		};
		
		// Make the column sortable.
		charactersColumn.setSortable(true);
		igsColumn.setSortable(true);

		characterTable.addColumn(charactersColumn,"Show you all characters");
		characterTable.addColumn(igsColumn,"Show you corresponding IG");
		
		ListDataProvider<HashMap<String, String>> dataProvider = new ListDataProvider<HashMap<String, String>>();
		dataProvider.addDataDisplay(characterTable);
		List<HashMap<String, String>> list = dataProvider.getList();
		for (HashMap<String, String> charIgPair : dataToShow) {
			list.add(charIgPair);
		}
		
		
		// Add a ColumnSortEvent.ListHandler to connect sorting to the java.util.List.
	    ListHandler<HashMap<String, String>> columnSortHandler = new ListHandler<HashMap<String, String>>(list);
	    columnSortHandler.setComparator(charactersColumn, new Comparator<HashMap<String, String>>() {
	    	public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
	    		List<String> o1List = new LinkedList<String>();
	    		List<String> o2List = new LinkedList<String>();
	    		for (String key : o1.keySet()) {
	    			o1List.add(key);
	    		}
	    		String str1 = o1List.get(0);	
	    		for (String key : o2.keySet()) {
	    			o2List.add(key);
	    		}
	    		String str2 = o2List.get(0);
	    		
	    		if (str1 == str2) {
	    			return 0;
	    		}

	    		// Compare the name columns.
	    		if (str1 != null) {
	    			return (str2 != null) ? str1.toString().compareTo(str2.toString()) : 1;
	    		}
	            return -1;
	          }
	        });
	    characterTable.addColumnSortHandler(columnSortHandler);
	    // We know that the data is sorted alphabetically by default.
	    characterTable.getColumnSortList().push(charactersColumn);
	    
	    
	 // Add a ColumnSortEvent.ListHandler to connect sorting to the java.util.List.
	    ListHandler<HashMap<String, String>> columnSortHandler2 = new ListHandler<HashMap<String, String>>(list);
	    columnSortHandler2.setComparator(igsColumn, new Comparator<HashMap<String, String>>() {
	    	public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
	    		List<String> o1List = new LinkedList<String>();
	    		List<String> o2List = new LinkedList<String>();
	    		for (String value : o1.values()) {
	    			o1List.add(value);
	    		}
	    		String str1 = o1List.get(0);	
	    		for (String value : o2.values()) {
	    			o2List.add(value);
	    		}
	    		String str2 = o2List.get(0);
	    		
	    		if (str1 == str2) {
	    			return 0;
	    		}

	    		// Compare the name columns.
	    		if (str1 != null) {
	    			return (str2 != null) ? str1.toString().compareTo(str2.toString()) : 1;
	    		}
	            return -1;
	          }
	        });
	    characterTable.addColumnSortHandler(columnSortHandler2);
	    // We know that the data is sorted alphabetically by default.
	    characterTable.getColumnSortList().push(igsColumn);
		
//		// Add a ColumnSortEvent.ListHandler to connect sorting to the java.util.List.
//	    ListHandler<String> columnSortHandler = new ListHandler<String>(list);
//	    columnSortHandler.setComparator(nameColumn, new Comparator<String>() {
//	    	public int compare(String o1, String o2) {
//	    		if (o1 == o2) {
//	    			return 0;
//	    		}
//
//	    		// Compare the name columns.
//	    		if (o1 != null) {
//	    			return (o2 != null) ? o1.toString().compareTo(o2.toString()) : 1;
//	    		}
//	            return -1;
//	          }
//	        });
//	    taxaTable.addColumnSortHandler(columnSortHandler);
//	    // We know that the data is sorted alphabetically by default.
//	    taxaTable.getColumnSortList().push(nameColumn);
	    
		
		
		// add a selection model to handle user selection
		final SingleSelectionModel<HashMap<String, String>> selectionModel = new SingleSelectionModel<HashMap<String, String>>();
		characterTable.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				HashMap<String, String> selected = selectionModel.getSelectedObject();
				if (selected != null) {
					
					if (remainingTaxa.size() == 1) {
						Window.alert("You have reached the only taxon");
						return;
					}
					
					String charName = "";
					for (String key : selected.keySet()) {
						charName = key;
					}
					
					
					// after selected one character, update the pool of the characters (originCharacters)
					for(String character : originCharacters) {
						if (character.equals(charName) == false) {
							newCharacters.add(character);

						}
					}
					
					// issue another RPC to get the selected character and states:species
					updateStates(remainingTaxa, newCharacters, charName);
					
					
				}
			}
		});
		
				
	}	
	
	
}
