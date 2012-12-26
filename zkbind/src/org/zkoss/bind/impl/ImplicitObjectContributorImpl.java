package org.zkoss.bind.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.Binder;
import org.zkoss.bind.impl.ImplicitObjectContributor;
import org.zkoss.bind.sys.CommandBinding;
import org.zkoss.zk.ui.event.Event;
/**
 * contribute implicit object of zkbind EL
 * @author dennis
 * @since 6.5.1
 */
public class ImplicitObjectContributorImpl implements ImplicitObjectContributor,Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Map<String, Object> contirbuteCommandObject(Binder binder, CommandBinding binding, Event event) {
		HashMap<String,Object> implicit = new HashMap<String,Object>();
		implicit.put("event", event);
		return implicit;
	}

}
