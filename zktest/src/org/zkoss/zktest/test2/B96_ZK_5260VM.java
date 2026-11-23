package org.zkoss.zktest.test2;

import org.zkoss.zul.ListModelList;

public class B96_ZK_5260VM {
	final private ListModelList<String> model = new ListModelList<>();

	public B96_ZK_5260VM() {
		model.add("1@email.io");
		model.add("2@email.io");
		model.add("3@email.io");
		// Escape the string manually because
		// "`ItemRenderer::render` renders the data to the corresponding HTML fragment, and returns the HTML `fragment.model."
		// See https://www.zkoss.org/javadoc/latest/zk/org/zkoss/zul/ItemRenderer.html
		// Also see ZK-2691.
		model.add("4@email.io &lt;aerror");
		model.add("5@email.io");
		model.add("6@email.io");
		model.add("7@email.io");
	}

	public ListModelList<String> getModel() {
		return model;
	}
}