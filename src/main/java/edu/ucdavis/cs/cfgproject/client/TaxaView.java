package edu.ucdavis.cs.cfgproject.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

import edu.ucdavis.cs.cfgproject.shared.model.Taxon;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;
import edu.ucdavis.cs.cfgproject.shared.rpc.IKeyGenerationService;
import edu.ucdavis.cs.cfgproject.shared.rpc.IKeyGenerationServiceAsync;

public class TaxaView extends VerticalLayoutContainer {

	private EventBus eventBus;
	private CellTable<String> taxaTable;
	private Label taxaLabel = new Label();
	private TaxonMatrix currentTaxonMatrix;
	
	public TaxaView(EventBus eventBus) {
		this.eventBus = eventBus;
		this.taxaTable = createTaxaTable();
		setScrollMode(ScrollMode.AUTOY);
		add(taxaLabel);
		add(taxaTable);
	}
	
	private CellTable<String> createTaxaTable() {
		CellTable<String> result = new CellTable<String>();
		TextColumn<String> nameColumn = new TextColumn<String>() {
			@Override
			public String getValue(String object) {
				return object;
			}
		};
	    nameColumn.setSortable(true);
	    result.addColumn(nameColumn,"All remaining taxa");
	    result.getColumnSortList().push(nameColumn);
	    result.setPageSize(200);
		return result;
	}

	public void setTaxa(final TaxonMatrix taxonMatrix) {
		currentTaxonMatrix = taxonMatrix;
		taxaLabel.setText("Remaining taxa: " + taxonMatrix.size());
		List<String> rowData = new LinkedList<String>();
		for(Taxon taxon : taxonMatrix.getTaxa())
			rowData.add(taxon.getName());
		taxaTable.setRowData(rowData);
	}	
	
	public TaxonMatrix getTaxonMatrix() {
		return currentTaxonMatrix;
	}
}
