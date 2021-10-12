package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Command;

public class B01908DatabindingOnPaging {

	int pageSize = 10;
	int totalSize = 100;
	int activePage = 3;

	@Command("onPaging")
	public void onPaging() {
		System.out.println("onPaging");
	}

	public int getPageSize() {
		System.out.println("getPageSize");
		return pageSize;
	}

	public int getTotalSize() {
		System.out.println("getTotalSize");
		return totalSize;
	}

	public int getActivePage() {
		System.out.println("getActivePage");
		return activePage;
	}

	public void setActivePage(int activePage) {
		System.out.println("setActivePage");
		this.activePage = activePage;
	}
}