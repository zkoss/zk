package org.zkoss.zktest.bind.issue;

import java.io.Serializable;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

public class F00986CloseWindow {

	boolean detached;
	
	
	public boolean isDetached() {
		return detached;
	}


	public void setDetached(boolean detached) {
		this.detached = detached;
	}


	@Command @NotifyChange("detached")
	public void detach(){
		detached = true;
	}
	
	static public class DetachConverter implements Converter,Serializable {
		
		private static final long serialVersionUID = 1463169907348730644L;

		@Override
		public Object coerceToUi(Object val, Component component, BindContext ctx) {
			Boolean b = (Boolean)Classes.coerce(Boolean.class, val);
			if(b!=null && b.booleanValue()){
				Event evt = new Event("onPostDetach",component);
				component.addEventListener(evt.getName(), _listener);
				Events.postEvent(evt);
			}
			return IGNORED_VALUE;
		}
		static private PostDetachListener _listener = new PostDetachListener();
		static class PostDetachListener implements EventListener<Event>,Serializable{
			private static final long serialVersionUID = 1L;
			@Override
			public void onEvent(Event event) throws Exception {
				Component comp = event.getTarget();
				comp.removeEventListener(event.getName(), this);
				comp.detach();
			}
		}

		@Override
		public Object coerceToBean(Object val, Component component, BindContext ctx) {
			return val;
		}

	}
	
}