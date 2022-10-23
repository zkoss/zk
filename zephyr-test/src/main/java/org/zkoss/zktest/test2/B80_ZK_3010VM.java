/* B80_ZK_3010VM.java

	Purpose:
		
	Description:
		
	History:
		Wed Dec 16 17:48:53 CST 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.*;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jameschu
 */
public class B80_ZK_3010VM {
	private ListModelList<B80_ZK_3010Tag> model;

	@Init
	public void init() {
		model = new ListModelList<B80_ZK_3010Tag>();
		model.add(new B80_ZK_3010Tag("tag 1"));
		model.add(new B80_ZK_3010Tag("tag 2"));
		model.add(new B80_ZK_3010Tag("tag 3"));
		model.add(new B80_ZK_3010Tag("banana"));
		model.add(new B80_ZK_3010Tag("lemon"));
		model.add(new B80_ZK_3010Tag("orange"));
		model.add(new B80_ZK_3010Tag("car"));
		model.add(new B80_ZK_3010Tag("bike"));
		model.add(new B80_ZK_3010Tag("train"));
	}

	@Command("newTag")
	public void newTag(@BindingParam("tag") String tag) {
		String tagName = tag == null ? "" : tag.trim();
		System.out.println("# Adding new tag=" + tag);

		if (!tagName.isEmpty()) {
			B80_ZK_3010Tag newTag = new B80_ZK_3010Tag(tagName);
			model.add(newTag);
			model.addToSelection(newTag);
		}
	}

	public ListModel<B80_ZK_3010Tag> getModel() {
		return ListModels.toListSubModel(model);
	}

	@Command("hitButton")
	public void hitButton() {
		System.out.println("Tags in model ");
		for (Object obj : model) {
			System.out.println(" Tag " + obj.toString());
		}
		System.out.println("Selected tags in model");
		for (Object obj : model.getSelection()) {
			System.out.println(" Tag " + obj.toString());
		}

	}
}
