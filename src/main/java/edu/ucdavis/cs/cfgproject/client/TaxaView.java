package edu.ucdavis.cs.cfgproject.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import edu.ucdavis.cs.cfgproject.shared.model.Taxon;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonProperties;

public class TaxaView extends VerticalLayoutContainer {

	private EventBus eventBus;
	private TaxonProperties taxonProperites = GWT.create(TaxonProperties.class);
	private ListStore<Taxon> taxaStore;
	private Grid<Taxon> taxaGrid;
	private ColumnConfig<Taxon, String> nameColumn;
	
	public TaxaView(EventBus eventBus) {
		this.eventBus = eventBus;

		setScrollMode(ScrollMode.AUTO);
		add(createTaxaGrid());
	}
	
	private Grid<Taxon> createTaxaGrid() {
		taxaStore = new ListStore<Taxon>(taxonProperites.key());
		taxaStore.addSortInfo(new StoreSortInfo<Taxon>(new IdentityValueProvider<Taxon>(), SortDir.ASC));
		nameColumn = new ColumnConfig<Taxon, String>(taxonProperites.name(), 
				50, SafeHtmlUtils.fromTrustedString("<b>Remaining Taxa</b>"));
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>();
		columns.add(nameColumn);
	    ColumnModel<Taxon> cm = new ColumnModel<Taxon>(columns);
		taxaGrid = new Grid<Taxon>(taxaStore, cm);
		taxaGrid.getView().setAutoExpandColumn(nameColumn);
		taxaGrid.getView().setStripeRows(true);
		taxaGrid.getView().setColumnLines(true);
		taxaGrid.getView().setSortingEnabled(true);
	    return taxaGrid;
	}

	public void setTaxa(final TaxonMatrix taxonMatrix) {
		nameColumn.setHeader("Taxa  (Remaining;" + taxonMatrix.size() + ")");
		taxaGrid.getView().getHeader().refresh();
		taxaStore.clear();
		taxaStore.addAll(taxonMatrix.getTaxa());
	}	

}
