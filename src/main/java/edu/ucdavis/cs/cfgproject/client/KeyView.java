package edu.ucdavis.cs.cfgproject.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;

import edu.ucdavis.cs.cfgproject.client.event.SelectStateEvent;
import edu.ucdavis.cs.cfgproject.shared.TaxonMatrixExtractor;
import edu.ucdavis.cs.cfgproject.shared.model.CharacterGain;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;
import edu.ucdavis.cs.cfgproject.shared.rpc.IKeyGenerationService;
import edu.ucdavis.cs.cfgproject.shared.rpc.IKeyGenerationServiceAsync;

public class KeyView extends HorizontalLayoutContainer {

	private IKeyGenerationServiceAsync keyGenerationService = GWT.create(IKeyGenerationService.class);
	
	private EventBus eventBus = new SimpleEventBus();
	private CharactersView charactersView = new CharactersView(eventBus);
	private TaxaView taxaView = new TaxaView(eventBus);
	
	public KeyView() {
		add(charactersView, new HorizontalLayoutData(0.5, 1.0, new Margins(4)));
		add(taxaView, new HorizontalLayoutData(0.5, 1.0, new Margins(4)));
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(SelectStateEvent.TYPE, new SelectStateEvent.SelectStateHandler() {
			@Override
			public void onSelect(SelectStateEvent event) {
				TaxonMatrix taxonMatrix = 
						TaxonMatrixExtractor.extractTaxonMatrix(taxaView.getTaxonMatrix(), event.getCharacter(), event.getState());
				setTaxa(taxonMatrix);
			}
		});
	}

	public void setTaxa(TaxonMatrix taxonMatrix) {
		taxaView.setTaxa(taxonMatrix);
		updateKey();
	}

	private void updateKey() {
		final TaxonMatrix taxoMatrix = taxaView.getTaxonMatrix();
    	keyGenerationService.getCharacterGains(taxoMatrix, new AsyncCallback<List<CharacterGain>>() {
			@Override
			public void onFailure(Throwable caught) { 
			}
			@Override
			public void onSuccess(List<CharacterGain> characterGains) {
				charactersView.setCharacterGains(taxoMatrix, characterGains);
			}
		});
	}
		
}
