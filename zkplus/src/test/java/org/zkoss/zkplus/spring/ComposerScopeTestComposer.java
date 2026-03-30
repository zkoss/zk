package org.zkoss.zkplus.spring;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Label;

public class ComposerScopeTestComposer extends SelectorComposer<Component> {

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		// Find the first Label child and set its value to this Composer's identity hash
		comp.getChildren().stream()
				.filter(c -> c instanceof Label)
				.map(c -> (Label) c)
				.findFirst()
				.ifPresent(label -> label.setValue(Integer.toHexString(System.identityHashCode(this))));
	}
}
