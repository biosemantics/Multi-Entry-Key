package edu.ucdavis.cs.cfgproject.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import edu.ucdavis.cs.cfgproject.client.event.SelectStateEvent.SelectStateHandler;

public class SelectStateEvent extends GwtEvent<SelectStateHandler> {

	public interface SelectStateHandler extends EventHandler {
		void onSelect(SelectStateEvent event);
	}
	
	public static Type<SelectStateHandler> TYPE = new Type<SelectStateHandler>();
	
	private String character;
	private String state;

	public SelectStateEvent(String character, String state) {
		this.character = character;
		this.state = state;
	}

	@Override
	public Type<SelectStateHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectStateHandler handler) {
		handler.onSelect(this);
	}

	public String getCharacter() {
		return character;
	}
	
	public String getState() {
		return state;
	}
}
