package edu.ucdavis.cs.cfgproject.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;

import edu.ucdavis.cs.cfgproject.client.common.Alerter;
import edu.ucdavis.cs.cfgproject.client.event.DeselectStateEvent;
import edu.ucdavis.cs.cfgproject.client.event.SelectStateEvent;
import edu.ucdavis.cs.cfgproject.shared.TaxonMatrixExtractor;
import edu.ucdavis.cs.cfgproject.shared.model.CharacterGain;
import edu.ucdavis.cs.cfgproject.shared.model.CharacterStateValue;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;
import edu.ucdavis.cs.cfgproject.shared.rpc.IKeyGenerationService;
import edu.ucdavis.cs.cfgproject.shared.rpc.IKeyGenerationServiceAsync;

public class KeyView extends BorderLayoutContainer {

	private IKeyGenerationServiceAsync keyGenerationService = GWT.create(IKeyGenerationService.class);
	
	private EventBus eventBus = new SimpleEventBus();
	private CharactersView charactersView = new CharactersView(eventBus);
	private TaxaView taxaView = new TaxaView(eventBus);
	
	private TaxonMatrix taxonMatrix;
	private Set<CharacterStateValue> selectedCharacterStateValues = new HashSet<CharacterStateValue>();
	
	public KeyView() {
		BorderLayoutData westData = new BorderLayoutData(150);
	    westData.setCollapsible(true);
	    westData.setSplit(true);
	    westData.setCollapseMini(true);
	    westData.setMargins(new Margins(0, 5, 0, 5));
	    this.setWestWidget(west, westData);
	    
	    ContentPanel east = new ContentPanel();
	    east.add(taxaView);
	    ContentPanel center = new ContentPanel();
	    center.add(charactersView);
	    
	    BorderLayoutData eastData = new BorderLayoutData(350);
	    eastData.setMargins(new Margins(0, 5, 0, 5));
	    eastData.setCollapsible(true);
	    eastData.setSplit(true);
	    eastData.setMaxSize(Integer.MAX_VALUE);
	    MarginData centerData = new MarginData();
	    this.setCenterWidget(center, centerData);
	    this.setEastWidget(east, eastData);
	    		
		//add(charactersView, new HorizontalLayoutData(0.5, 1.0, new Margins(4)));
		//add(taxaView, new HorizontalLayoutData(0.5, 1.0, new Margins(4)));
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(SelectStateEvent.TYPE, new SelectStateEvent.SelectStateHandler() {
			@Override
			public void onSelect(SelectStateEvent event) {
				Alerter.startLoading();
				selectedCharacterStateValues.add(event.getCharacterStateValue());
				updateTaxa();
			}
		});
		eventBus.addHandler(DeselectStateEvent.TYPE, new DeselectStateEvent.DeselectStateHandler() {
			@Override
			public void onSelect(DeselectStateEvent event) {
				Alerter.startLoading();
				selectedCharacterStateValues.remove(event.getCharacterStateValue());
				updateTaxa();
			}
		});
	}
	
	public void initialize(TaxonMatrix taxonMatrix) {
		Alerter.startLoading();
		this.taxonMatrix = taxonMatrix;
		selectedCharacterStateValues.clear();
		updateTaxa();
	}

	public void updateTaxa() {
		TaxonMatrix remainingTaxonMatrix = TaxonMatrixExtractor.extractTaxonMatrix(taxonMatrix, selectedCharacterStateValues);
		taxaView.setTaxa(remainingTaxonMatrix);
		updateKey(remainingTaxonMatrix);
	}

	private void updateKey(final TaxonMatrix reminaingTaxoMatrix) {
    	keyGenerationService.getCharacterGains(reminaingTaxoMatrix, new AsyncCallback<List<CharacterGain>>() {
			@Override
			public void onFailure(Throwable caught) { 
				Alerter.stopLoading();
			}
			@Override
			public void onSuccess(List<CharacterGain> characterGains) {
				charactersView.setCharacterGains(taxonMatrix, characterGains, selectedCharacterStateValues);
				Alerter.stopLoading();
			}
		});
	}
		
}
