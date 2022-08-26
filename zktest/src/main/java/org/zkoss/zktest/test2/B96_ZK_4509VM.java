/* B96_ZK_4509VM.java

		Purpose:

		Description:

		History:
				Thu Oct 28 10:25:04 CST 2021, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;

/**
 * test for error below
 * cannot be cast to org.zkoss.zul.Comboitem
 * at org.zkoss.zul.Combobox.service(Combobox.java:990)
 *
 */

public final class B96_ZK_4509VM {


	private final List<String> category1List = new ArrayList<>();
	private final List<String> category2List = new ArrayList<>();
	private int category1ListSize = 10;
	private int category2ListSize = 20;
	private String category = null;

	@AfterCompose
	protected void afterCompose() throws Exception {
		initCategoryLists();
	}

	@Command
	public void categoryChanged() {
		BindUtils.postNotifyChange(null, null, this, "categoryList");
	}

	@Command
	public void regenerateCategoryLists() {
		category1List.clear();
		category2List.clear();
		initCategoryLists();
		BindUtils.postNotifyChange(null, null, this, "categoryList");
	}

	private void initCategoryLists() {
		for (int i = 1; i <= category1ListSize; i++) {
			category1List.add("Category 1 list element " + i);
		}
		for (int i = 1; i <= category2ListSize; i++) {
			category2List.add("Category 2 list element " + i);
		}
	}

	public List<String> getCategoryList() {
		if ("Category 1".equals(category)) {
			return category1List;
		}
		if ("Category 2".equals(category)) {
			return category2List;
		}
		return new ArrayList<>();
	}

	public int getCategory1ListSize() {
		return category1ListSize;
	}

	public void setCategory1ListSize(int category1ListSize) {
		this.category1ListSize = category1ListSize;
	}

	public int getCategory2ListSize() {
		return category2ListSize;
	}

	public void setCategory2ListSize(int category2ListSize) {
		this.category2ListSize = category2ListSize;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}