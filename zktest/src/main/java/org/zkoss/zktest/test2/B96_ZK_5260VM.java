package org.zkoss.zktest.test2;

import org.zkoss.zul.ListModelList;

public class B96_ZK_5260VM {
	final private ListModelList<String> model = new ListModelList<>();

	public B96_ZK_5260VM() {
		model.add("1@email.io");
		model.add("2@email.io");
		model.add("3@email.io");
		model.add("4@email.io <aerror");
		model.add("5@email.io");
		model.add("<a onmouseover=\"alert('xss attack 1')\" href='https://www.zkoss.org/'>6@email.io</a>");
		model.add("<b onmouseover=f()>7@email.io</b><script>function f() { alert('xss attack 2'); }</script>");
	}

	public ListModelList<String> getModel() {
		return model;
	}
}