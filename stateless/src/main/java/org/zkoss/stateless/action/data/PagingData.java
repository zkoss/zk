/* PagingData.java

	Purpose:

	Description:

	History:
		5:22 PM 2021/11/1, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import static org.zkoss.stateless.action.ActionTarget.SELF;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.sul.IPaging;
import org.zkoss.zk.au.AuRequests;

/**
 * Used to notify that a new page is selected by the user
 * (such as {@link IPaging}).
 * It is used for paging long content.
 *
 * @author jumperchen
 */
public class PagingData implements ActionData {
	@ActionVariable(targetId = SELF, field = "pageCount") private int pageCount;

	@ActionVariable(targetId = SELF, field = "pageSize") private int pageSize;

	@ActionVariable(targetId = SELF, field = "totalSize") private int totalSize;

	@ActionVariable(targetId = SELF, field = "pageIncrement") private int pageIncrement;

	private int activePage;

	@JsonCreator
	private PagingData(Map data) {
		int pgi = AuRequests.getInt(data, "", 0);
		pageCount = AuRequests.getInt(data, "pageCount", 1);
		pageIncrement = AuRequests.getInt(data, "pageIncrement", 10);
		totalSize = AuRequests.getInt(data, "totalSize", 0);
		pageSize = AuRequests.getInt(data, "pageSize", 20);
		if (pgi < 0) {
			activePage = 0;
		} else {
			if (pgi >= pageCount) {
				activePage = pageCount - 1;
				if (pgi < 0) {
					activePage = 0;
				}
			} else {
				activePage = pgi;
			}
		}
	}

	/**
	 * Returns the active page (starting from 0).
	 *
	 * <p>To get the index of the first visible item, use<br/>
	 * <code>{@link #getActivePage} * {@link IPaging#getPageSize}</code>.
	 */
	public int getActivePage() {
		return activePage;
	}

	/**
	 * Returns the number of pages.
	 * Note: there is at least one page even no item at all.
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * Returns the number of items per page.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Returns the total number of items.
	 *
	 * @return
	 */
	public int getTotalSize() {
		return totalSize;
	}

	/**
	 * Returns the number of page anchors shall appear at the client.
	 */
	public int getPageIncrement() {
		return pageIncrement;
	}

	/**
	 * @hidden for Javadoc
	 */
	public String toString() {
		return "PagingData{" + "pageCount=" + pageCount + ", pageSize="
				+ pageSize + ", totalSize=" + totalSize + ", pageIncrement="
				+ pageIncrement + ", activePage=" + activePage + '}';
	}
}
