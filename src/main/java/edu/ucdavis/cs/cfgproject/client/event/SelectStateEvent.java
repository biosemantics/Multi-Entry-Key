package edu.ucdavis.cs.cfgproject.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.ucdavis.cs.cfgproject.client.event.SelectStateEvent.SelectStateHandler;
import edu.ucdavis.cs.cfgproject.shared.model.CharacterStateValue;

public class SelectStateEvent extends GwtEvent<SelectStateHandler> {

	public interface SelectStateHandler extends EventHandler {
		void onSelect(SelectStateEvent event);
	}
	
	public static Type<SelectStateHandler> TYPE = new Type<SelectStateHandler>();

	private CharacterStateValue characterStateValue;

	public SelectStateEvent(CharacterStateValue characterStateValue) {
		this.characterStateValue = characterStateValue;
	}

	@Override
	public Type<SelectStateHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectStateHandler handler) {
		handler.onSelect(this);
	}

	public CharacterStateValue getCharacterStateValue() {
		return characterStateValue;
	}
	
}
