package edu.ucdavis.cs.cfgproject.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;
import edu.ucdavis.cs.cfgproject.shared.rpc.IKeyGenerationService;
import edu.ucdavis.cs.cfgproject.shared.rpc.IKeyGenerationServiceAsync;

public class Key implements EntryPoint {
	
	@Override
	public void onModuleLoad() {		  
		final KeyView view = new KeyView();

		// simulate etc site 
		DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);
		dock.addNorth(new HTML("header"), 2);
		HTML footer = new HTML("footer");
		dock.addSouth(footer, 2);
		dock.add(view.asWidget());
		RootLayoutPanel.get().add(dock);

		IKeyGenerationServiceAsync keyGenerationService = GWT.create(IKeyGenerationService.class);
		// TaxonMatrix taxonMatrix = createSampleMatrix();
		String input = "C:/gitEtc/cfgproject/src/main/resources/mapQueryNew.csv";
		keyGenerationService.getTaxonMatrix(input, new AsyncCallback<TaxonMatrix>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(final TaxonMatrix result) {
				view.initialize(result);
			}
		});
	}
}
