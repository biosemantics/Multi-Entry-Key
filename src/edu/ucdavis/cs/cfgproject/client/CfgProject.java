package edu.ucdavis.cs.cfgproject.client;

import java.io.*;
import java.util.*;

import edu.ucdavis.cs.cfgproject.server.TaxonManager;
import edu.ucdavis.cs.cfgproject.shared.State;
import edu.ucdavis.cs.cfgproject.shared.StatesToSpeciesCreator;
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
import com.google.gwt.user.client.ui.CheckBox;
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
	private VerticalPanel eastPanel = new VerticalPanel();
	private KeyGenerationServiceAsync KeyGenerationSvc = GWT.create(KeyGenerationService.class);
	CellTable<String> taxaTable = new CellTable<String>();
	CellTable<HashMap<String, String>> characterTable = new CellTable<HashMap<String, String>>();
	CellTable<HashMap<String, String>> stateTable = new CellTable<HashMap<String, String>>();
	private VerticalPanel igCharStateCbPanel = new VerticalPanel();
	private List<Taxon> taxaPool = new LinkedList<Taxon>();
//	private List<String> charactersPool = new LinkedList<String>();
	private Label taxaCntLabel = new Label("");
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
				
//		// test checkboxes
//		// Make a new check box, and select it by default.
//	      CheckBox checkBox1 = new CheckBox("Check Me123!");
//	      CheckBox checkBox2 = new CheckBox("Check Me456!");
//	      
//
//	      // set check box as selected
//	      checkBox1.setValue(true);
//
//	      //disable a checkbox
//	      checkBox2.setEnabled(false);
//
//	      checkBox1.addClickHandler(new ClickHandler() {
//	    	  @Override
//	    	  public void onClick(ClickEvent event) {
//	    		  CheckBox checkBox = (CheckBox)event.getSource();
////	    		  Window.alert("CheckBox is " + (checkBox.getValue() ? "" : "not") + " checked");
//	    		  Window.alert(""+checkBox.getText());
//	         }
//	      });
//
//
//	      // Add checkboxes to the root panel.
//	      VerticalPanel panel = new VerticalPanel();
//	      panel.setSpacing(10);            
//	      panel.add(checkBox1);
//	      panel.add(checkBox2);
		
		
		
		/*
		 * Start here to read taxa csv file
		 */
		readTaxa(taxaPool);	
		
		// add stuff to west panel
//		westPanel.add(new Label("Input files: "));
		westPanel.add(new Label("Characters: "));
//		characterTable.setPageSize(200);
//		westPanel.add(characterTable);
//		westPanel.add(new Label("States: "));
//		stateTable.setPageSize(200);
//		westPanel.add(stateTable);
		westPanel.add(igCharStateCbPanel);
				
		// add stuff to east panel;
//		eastPanel.add(new Label("Task names: "));
//		eastPanel.add(new Label("Remaining taxa: "));
		eastPanel.add(taxaCntLabel);
		taxaTable.setPageSize(200);
		eastPanel.add(taxaTable);
//		eastPanel.add(panel);
				
		// add three big panels to main panel
		mainPanel.add(westPanel);
		mainPanel.add(eastPanel);
		
		// associate the main panel with the HTML host page.  
		RootPanel.get("cfgproject").add(mainPanel);
		
	}


	public void readTaxa(List<Taxon> taxa) {
		/*
		 * RPC service that retrieve taxa names
		 */
		// initialize the service proxy
		if (KeyGenerationSvc == null) {
			KeyGenerationSvc = GWT.create(KeyGenerationService.class);
		}
		
		// set up the callback object
		AsyncCallback<List<Taxon>> readTaxaCallback = new AsyncCallback<List<Taxon>>() {
			public void onFailure(Throwable caught) {
			}	
			public void onSuccess(List<Taxon> allTaxa) {
				// set all taxa
				updateTaxa(allTaxa, allTaxa);								
			}
		};
		// Make the call to read matrix service
		KeyGenerationSvc.retrieveCSV(taxa, readTaxaCallback);
		
	}


	public void updateTaxa(final List<Taxon> allTaxa, List<Taxon> taxa) {
		
		/*
		 * RPC service that retrieve taxa names
		 */
		// initialize the service proxy
		if (KeyGenerationSvc == null) {
			KeyGenerationSvc = GWT.create(KeyGenerationService.class);
		}
		
		final HashMap<String, HashMap<String, CheckBox>> characterStateCheckBoxMap = new HashMap<String, HashMap<String, CheckBox>>(); 
		
		// set up the callback object
		AsyncCallback<List<Taxon>> updateTaxaCallback = new AsyncCallback<List<Taxon>>() {
			public void onFailure(Throwable caught) {
			}	
			public void onSuccess(List<Taxon> remainingTaxa) {				
				// set all remaining taxa
				setTaxaTable(allTaxa, remainingTaxa, characterStateCheckBoxMap, taxaTable);
				
			}
		};
		// Make the call to get retrieve taxa
		KeyGenerationSvc.retrieveTaxa(taxa, updateTaxaCallback);
		
	}
	
	public void showNumOfTaxa(List<Taxon> remainingTaxa, Label taxaCntLabel) {
		int taxaCnt = 0;
		taxaCnt = remainingTaxa.size();
		taxaCntLabel.setText("The number of remaining taxa is: " + taxaCnt);
		
	}


	public void updateCharactersAndIgAndStates (final List<Taxon> allTaxa, List<Taxon> taxa) {
		
		final List<Taxon> remainingTaxa = taxa;
		
		// fancy data structure HashMap<Character, HashMap<State, CheckBox>>
		final HashMap<String, HashMap<String, CheckBox>> characterStateCheckBoxMap = new HashMap<String, HashMap<String, CheckBox>>();
				
		// get the list of all characters
		Taxon firstTaxon = new Taxon();
		firstTaxon = allTaxa.get(0);
		List<String> charactersList = new ArrayList<>(firstTaxon.getAllCharacters());		
		
		// initialize fancy data structure
		for (String character : charactersList) {
			
			// for each character, find all states of it
			ArrayList<String> allStates = new ArrayList<>();
			for (Taxon taxon : allTaxa) {
				State statesOfTaxon = taxon.getAllStatesByCharacter(character);
				String[] stateValues = statesOfTaxon.getValues();
				for (String stateValue : stateValues) {
					allStates.add(stateValue);
				}
			}
			HashSet<String> tmpHs = new HashSet<>();
			tmpHs.addAll(allStates);
			allStates.clear();
			allStates.addAll(tmpHs);			
			
			// for each state of this character
			for (String stateValue : allStates) {
				// create a checkbox
				CheckBox cb = new CheckBox(stateValue);
				
				// update the characterStateCheckBoxMap
				if (characterStateCheckBoxMap.get(character) != null) {
					characterStateCheckBoxMap.get(character).put(stateValue, cb);
				} else {
					HashMap<String, CheckBox> tmpInner = new HashMap<String, CheckBox>();
					tmpInner.put(stateValue, cb);
					characterStateCheckBoxMap.put(character, tmpInner);
				}	
			}
		}
		
		updateNewIg(allTaxa, remainingTaxa, characterStateCheckBoxMap);
		
		
//		// issue RPC to get character-ig pair
//		AsyncCallback<HashMap<String, String>> updateCharactersCallback = new AsyncCallback<HashMap<String, String>>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//			}
//
//			@Override
//			public void onSuccess(HashMap<String, String> result) {
//				setCharacterAngIgCheckboxTable(allTaxa, remainingTaxa, result, characterStateCheckBoxMap, igCharStateCbPanel);
//				
//			}
//		};
//		KeyGenerationSvc.retrieveCharactersAndIg(remainingTaxa,updateCharactersCallback);
	}



	public void updateNewIg(final List<Taxon> allTaxa, final List<Taxon> remainingTaxa, final HashMap<String, HashMap<String, CheckBox>> characterStateCheckBoxMap) {
		// issue RPC to get character-ig pair
		AsyncCallback<HashMap<String, String>> updateCharactersCallback = new AsyncCallback<HashMap<String, String>>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(HashMap<String, String> result) {
				setCharacterAngIgCheckboxTable(allTaxa, remainingTaxa, result, characterStateCheckBoxMap, igCharStateCbPanel);
				
			}
		};
		KeyGenerationSvc.retrieveCharactersAndIg(remainingTaxa,updateCharactersCallback);
		
		
	}


	public void setCharacterAngIgCheckboxTable(final List<Taxon> allTaxa, List<Taxon> remainingTaxa, HashMap<String, String> result, final HashMap<String, HashMap<String, CheckBox>> characterStateCheckBoxMap, VerticalPanel igCharStateCbPanel) {
		
		// reset the igCharStateCbPanel
		if (igCharStateCbPanel.getWidgetCount() != 0){
			igCharStateCbPanel.clear();
		}
		
		// iterate through the fancy data structure
		for (Map.Entry<String, HashMap<String, CheckBox>> entry : characterStateCheckBoxMap.entrySet()) {
			String character = entry.getKey();							//key
			HashMap<String, CheckBox> stateCbMap = entry.getValue();	//value
			String ig = result.get(character);
			Label igLabel = new Label(ig);
			Label characterLabel = new Label(character);
			
			// for each entry create a HorizontalPanel
			HorizontalPanel entryPanel = new HorizontalPanel();
			entryPanel.setSpacing(10);
			entryPanel.add(igLabel);
			entryPanel.add(characterLabel);
			
			for (Map.Entry<String, CheckBox> innerEntry : stateCbMap.entrySet()) {
//				String state = innerEntry.getKey();
				CheckBox cb = innerEntry.getValue();
				
				// add click handler for checkbox
				cb.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						// iterate all checkboxes to get updated taxa
						updateTaxaByCheckBoxes(allTaxa, characterStateCheckBoxMap);
						
					}
					
				});
				
				entryPanel.add(cb);
			}
			
			igCharStateCbPanel.add(entryPanel);
			
		}
		
		
		
		
	}


	public void updateTaxaByCheckBoxes(final List<Taxon> allTaxa, HashMap<String, HashMap<String, CheckBox>> characterStateCheckBoxMap) {
		

//		// issue a RPC to update taxa Table
//		AsyncCallback<List<Taxon>> updateTaxaByCheckBoxesCallback = new AsyncCallback<List<Taxon>>() {
//			public void onFailure(Throwable caught) {
//				System.out.println("" + caught.getMessage());
//			}	
//			public void onSuccess(List<Taxon> remainingTaxa) {
//				// set all remaining taxa
//				setTaxaTable(allTaxa, remainingTaxa, taxaTable);
//				
//										
//			}
//		};
//		// Make the call to get retrieve taxa
//		KeyGenerationSvc.retrieveTaxaByCheckBoxes(allTaxa, characterStateCheckBoxMap, updateTaxaByCheckBoxesCallback);
		
		
		StatesToSpeciesCreator statesToSpeciesCreator = new StatesToSpeciesCreator(allTaxa);
		
		List<Taxon> deletedTaxa = new LinkedList<Taxon>();
		List<Taxon> remainingTaxa = new LinkedList<Taxon>();
		
		
		// iterate through the fancy data structure
		for (Map.Entry<String, HashMap<String, CheckBox>> entry : characterStateCheckBoxMap.entrySet()) {
			String character = entry.getKey();							//key
			HashMap<String, CheckBox> stateCbMap = entry.getValue();	//value
			
			// for each character, create HashMap statesToSpecies
			HashMap<String, List<Taxon>> statesToSpecies = new HashMap<String, List<Taxon>>();
			statesToSpecies = statesToSpeciesCreator.createStatesToSpecies(character);
			List<Taxon> taxonList = new LinkedList<Taxon>();
			
			// By this character go through every state
			for (Map.Entry<String, CheckBox> innerEntry : stateCbMap.entrySet()) {
				String state = innerEntry.getKey();						//inner key
				CheckBox cb = innerEntry.getValue();					//inner value
				
				//if cb is checked, find all species that are in this state
				if (cb.getValue()) {
					List<Taxon> tmpTaxa = statesToSpecies.get(state);
					
					for (Taxon taxon : tmpTaxa) {
						taxonList.add(taxon);
					}
				}
				
			}			
			
			HashSet<Taxon> tmpHs = new HashSet<Taxon>();
			tmpHs.addAll(taxonList);
			taxonList.clear();
			taxonList.addAll(tmpHs);
			
						
			// if the taxon is not in taxonList, add it to deletedTaxa
			if (taxonList.size() != 0) {
				for (Taxon taxon : allTaxa) {
					if (taxonList.contains(taxon) == false) {
						deletedTaxa.add(taxon);
					}
				}
			}
			
			
		}
				
		HashSet<Taxon> tmpHs = new HashSet<Taxon>();
		tmpHs.addAll(deletedTaxa);
		deletedTaxa.clear();
		deletedTaxa.addAll(tmpHs);
		
		
		// update remainingTaxa
		for (Taxon taxon : allTaxa) {
			if (deletedTaxa.contains(taxon) == false) {
				remainingTaxa.add(taxon);
			}
		}
		
		
		setTaxaTable(allTaxa, remainingTaxa, characterStateCheckBoxMap, taxaTable);
		
	
		
	}


	public void updateStates (List<Taxon> taxa, String character) {
		final List<Taxon> remainingTaxa = taxa;
		AsyncCallback<HashMap<String, List<Taxon>>> updateStatesCallback = new AsyncCallback<HashMap<String, List<Taxon>>>() {

			@Override
			public void onFailure(Throwable caught) {				
			}

			@Override
			public void onSuccess(HashMap<String, List<Taxon>> result) {
				setStatesTable(remainingTaxa, result, stateTable);
			}
			
		};
		KeyGenerationSvc.retrieveStatesAndSpecies(remainingTaxa, character, updateStatesCallback);
		
	}
	
	
	

	/*
	 * set all remaining taxa
	 */
	public void setTaxaTable(List<Taxon> allTaxa, List<Taxon> taxa, HashMap<String, HashMap<String, CheckBox>> characterStateCheckBoxMap, CellTable<String> taxaTable) {
		
		// show number of remaining taxa above taxaTable
		showNumOfTaxa(taxa, taxaCntLabel);
		
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
	    
		
	    // issue another RPC to get characters and states and information gain
	    if (characterStateCheckBoxMap.isEmpty()) {
	    	updateCharactersAndIgAndStates(allTaxa, taxa);
	    } else {
	    	updateNewIg(allTaxa, taxa, characterStateCheckBoxMap);
	    }
	    
		
	}	
	
	/*
	 * set all remaining states
	 */
	public void setStatesTable(final List<Taxon> remainingTaxa, final HashMap<String, List<Taxon>> result, CellTable<HashMap<String, String>> stateTable) {
		
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
							updateTaxa(newTaxa, newTaxa);
							break;
						}

					}
					
					
				}
			}
		});
		
		
	}
	
	public void setCharacterAngIgTable(final List<Taxon> remainingTaxa, HashMap<String, String> result, CellTable<HashMap<String, String>> characterTable) {
		
		// clear the table first
		while (characterTable.getColumnCount() !=0 ) {
			characterTable.removeColumn(0);
		}
		
		//use the "result" to update characters
//		final List<String> originCharacters = new LinkedList<String>();
//		final List<String> newCharacters = new LinkedList<String>();	

		List<HashMap<String, String>> dataToShow = new LinkedList<HashMap<String, String>>();
		
		for (Map.Entry<String, String> entry : result.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			//use the "result" to update characters
//			originCharacters.add(key);
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
//					for(String character : originCharacters) {
//						if (character.equals(charName) == false) {
//							newCharacters.add(character);
//
//						}
//					}
					
					// issue another RPC to get the selected character and states:species
					updateStates(remainingTaxa, charName);
					
					
				}
			}
		});
		
				
	}	
	
	
}
