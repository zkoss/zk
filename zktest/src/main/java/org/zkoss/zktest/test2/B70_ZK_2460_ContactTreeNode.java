package org.zkoss.zktest.test2;

import org.zkoss.zul.DefaultTreeNode;

public class B70_ZK_2460_ContactTreeNode extends DefaultTreeNode<B70_ZK_2460_Contact> {
	private static final long serialVersionUID = -7012663776755277499L;
	
	private boolean open = false;

	public B70_ZK_2460_ContactTreeNode(B70_ZK_2460_Contact data, DefaultTreeNode<B70_ZK_2460_Contact>[] children) {
		super(data, children);
	}

	public B70_ZK_2460_ContactTreeNode(B70_ZK_2460_Contact data, DefaultTreeNode<B70_ZK_2460_Contact>[] children, boolean open) {
		super(data, children);
		setOpen(open);
	}

	public B70_ZK_2460_ContactTreeNode(B70_ZK_2460_Contact data) {
		super(data);

	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

}
