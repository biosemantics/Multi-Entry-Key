package edu.ucdavis.cs.cfgproject.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.ExpandMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.ucdavis.cs.cfgproject.client.event.DeselectStateEvent;
import edu.ucdavis.cs.cfgproject.client.event.SelectStateEvent;
import edu.ucdavis.cs.cfgproject.shared.TaxonMatrixExtractor;
import edu.ucdavis.cs.cfgproject.shared.model.CharacterGain;
import edu.ucdavis.cs.cfgproject.shared.model.CharacterStateValue;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;

public class CharactersView extends VerticalLayoutContainer {
	
	private EventBus eventBus;
	private AccordionLayoutContainer categoriesContainer  = new AccordionLayoutContainer();
	private AccordionLayoutAppearance appearance = GWT.<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);
	private AccordionLayoutContainer usefullContainer;
	private AccordionLayoutContainer selectedContainer;
	private AccordionLayoutContainer uselessContainer;
	private ContentPanel usefullPanel = new ContentPanel(appearance);
	private ContentPanel selectedPanel = new ContentPanel(appearance);
	private ContentPanel uselessPanel = new ContentPanel(appearance);

	
	public CharactersView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		usefullPanel.setHeadingText("<b>Informative Characters for Differentiation (Sorted by character diagnosis values high to low)</b>");
		selectedPanel.setHeadingText("<b>Selected Characters for Differentiation</b>");
		uselessPanel.setHeadingText("<b>Non-informative Characters for Differentiation</b>");
		categoriesContainer.add(usefullPanel);
		categoriesContainer.add(selectedPanel);
		categoriesContainer.add(uselessPanel);
		categoriesContainer.setExpandMode(ExpandMode.SINGLE_FILL);
		add(categoriesContainer, new VerticalLayoutData(1, 1));
		
		Menu menu = createContextMenu();
		usefullPanel.setContextMenu(menu);
		uselessPanel.setContextMenu(menu);
		selectedPanel.setContextMenu(menu);
	}
	
	private void expandAll(AccordionLayoutContainer accordionLayoutContainer) {
		for(int i=0; i<accordionLayoutContainer.getWidgetCount(); i++) 
			((ContentPanel)accordionLayoutContainer.getWidget(i)).expand();
	}
	
	private void collapseAll(AccordionLayoutContainer accordionLayoutContainer) {
		for(int i=0; i<accordionLayoutContainer.getWidgetCount(); i++) 
			((ContentPanel)accordionLayoutContainer.getWidget(i)).collapse();
	}
	
	private Menu createContextMenu() {
		Menu menu = new Menu();
		
		MenuItem insert = new MenuItem();
		insert.setText("Expand All");
		insert.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				ContentPanel contentPanel = (ContentPanel)categoriesContainer.getActiveWidget();
				AccordionLayoutContainer accordionLayoutContainer = (AccordionLayoutContainer)((FlowLayoutContainer)contentPanel.getWidget()).getWidget(0);
				expandAll(accordionLayoutContainer);
			}			
		});

		MenuItem remove = new MenuItem();
		remove.setText("Collapse All");
		remove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				ContentPanel contentPanel = (ContentPanel)categoriesContainer.getActiveWidget();
				AccordionLayoutContainer accordionLayoutContainer = (AccordionLayoutContainer)((FlowLayoutContainer)contentPanel.getWidget()).getWidget(0);
				collapseAll(accordionLayoutContainer);
			}
		});
	      
		menu.add(insert);
		menu.add(remove);
		return menu;
	}

	public void setCharacterGains(final TaxonMatrix taxonMatrix, List<CharacterGain> characterGains, Set<CharacterStateValue> selectedCharacterStateValues) {		
		ContentPanel activePanel = (ContentPanel)categoriesContainer.getActiveWidget();
		
		usefullContainer = new AccordionLayoutContainer();
		usefullContainer.setExpandMode(ExpandMode.MULTI);
		selectedContainer = new AccordionLayoutContainer();
		selectedContainer.setExpandMode(ExpandMode.MULTI);
		uselessContainer = new AccordionLayoutContainer();
		uselessContainer.setExpandMode(ExpandMode.MULTI);
		
		Set<String> selectedCharacters = new HashSet<String>();
		for(CharacterStateValue characterStatevalue : selectedCharacterStateValues) 
			selectedCharacters.add(characterStatevalue.getCharacter());
		
		for(CharacterGain characterGain : characterGains) {
			Set<String> stateValues = TaxonMatrixExtractor.extractCharacterStateValues(taxonMatrix, characterGain.getCharacter());
			ContentPanel widget = createEntry(characterGain, stateValues, selectedCharacterStateValues);
			
			if(selectedCharacters.contains(characterGain.getCharacter()))
				selectedContainer.add(widget);
			else {
				if(characterGain.getInformationGain() == 0.0)
					uselessContainer.add(widget);
				else 			
					usefullContainer.add(widget);
			}
		}
		
		FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
		flowLayoutContainer.setScrollMode(ScrollMode.AUTO);
		flowLayoutContainer.add(usefullContainer);
		usefullPanel.setWidget(flowLayoutContainer);
		flowLayoutContainer = new FlowLayoutContainer();
		flowLayoutContainer.setScrollMode(ScrollMode.AUTO);
		flowLayoutContainer.add(selectedContainer);
		selectedPanel.setWidget(flowLayoutContainer);
		flowLayoutContainer = new FlowLayoutContainer();
		flowLayoutContainer.setScrollMode(ScrollMode.AUTO);
		flowLayoutContainer.add(uselessContainer);
		uselessPanel.setWidget(flowLayoutContainer);
		if(activePanel == null)
			activePanel = usefullPanel;		
		categoriesContainer.setActiveWidget(activePanel);

		//expandAll((AccordionLayoutContainer)((FlowLayoutContainer)activePanel.getWidget()).getWidget(0));
		categoriesContainer.forceLayout();
	}
	
	public boolean isActiveUsefull() {
		return categoriesContainer.getActiveWidget() != null && categoriesContainer.getActiveWidget().equals(usefullPanel);
	}
	
	public boolean isActiveUseless() {
		return categoriesContainer.getActiveWidget() != null && categoriesContainer.getActiveWidget().equals(uselessPanel);
	}
	
	public boolean isActiveSelected() {
		return categoriesContainer.getActiveWidget() != null && categoriesContainer.getActiveWidget().equals(selectedPanel);
	}

	private ContentPanel createEntry(final CharacterGain characterGain, Set<String> stateValues, Set<CharacterStateValue> selectedCharacterStateValues) {
		List<String> sortedStateValues = new ArrayList<String>(stateValues);
		Collections.sort(sortedStateValues);
		ContentPanel result = new ContentPanel(appearance);
		String character = characterGain.getCharacter().trim();
		result.setHeadingText(character);
	    VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
	    for (final String stateValue : sortedStateValues) {
	    	String showValue = stateValue.trim();
			if(showValue.isEmpty())
				showValue = "unknown";
			CheckBox checkBox = new CheckBox(showValue);
			final CharacterStateValue characterStateValue = new CharacterStateValue(characterGain.getCharacter(), stateValue);
			checkBox.setValue(selectedCharacterStateValues.contains(characterStateValue));
			checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if(event.getValue()) 
						eventBus.fireEvent(new SelectStateEvent(characterStateValue));
					else
						eventBus.fireEvent(new DeselectStateEvent(characterStateValue));
				}
		    });
			verticalLayoutContainer.add(checkBox);
		}
	    result.add(verticalLayoutContainer);
	    return result;
	}
}
