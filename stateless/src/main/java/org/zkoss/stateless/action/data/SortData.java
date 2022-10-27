/* SortData.java

	Purpose:

	Description:

	History:
		2:24 PM 2021/12/14, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import static org.zkoss.stateless.action.ActionTarget.SELF;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.zpr.IHeaderElement;
import org.zkoss.zk.au.AuRequests;

/**
 * Represents sort data by sorting activity
 *
 * @author jumperchen
 */
public class SortData implements ActionData {

	@ActionVariable(targetId = SELF, field = "childIndex")
	private int childIndex;

	@ActionVariable(targetId = SELF, field = "sortDirection")
	private String sortDirection;

	private final boolean ascending;

	@JsonCreator
	protected SortData(Map data) {
		ascending = AuRequests.getBoolean(data, data.get("ascending") == null ? "" : "ascending");
		sortDirection = (String) data.get("sortDirection");
		childIndex = AuRequests.getInt(data, "childIndex", 0);
	}

	/**
	 * Returns true if the sorting request is ascending.
	 */
	public boolean isAscending() {
		return ascending;
	}

	/**
	 * Returns the sort direction.
	 */
	public String getSortDirection() {
		return sortDirection;
	}

	/**
	 * Returns the reference index.
	 * This method is used for {@link IHeaderElement} to sort a header to
	 * get its index from its parent.
	 *
	 * @return
	 */
	public int getReferenceIndex() {
		return childIndex;
	}
}
