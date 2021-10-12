package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

public class B96_ZK_4845VM {
	private boolean showSection = true;
	private PageElement pageElement;

	@Init
	public void init() {
		pageElement = new PageElement("First");
	}

	@Command
	public void nextPage() {
		pageElement.setName("Second");
		BindUtils.postNotifyChange(null, null, this, "showSection"); //this notify change causes the problem
		BindUtils.postNotifyChange(null, null, this.pageElement, "name");
	}

	public boolean isShowSection() {
		return showSection;
	}

	public PageElement getPageElement() {
		return pageElement;
	}

	public static class PageElement {
		private String name;

		public PageElement(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}

