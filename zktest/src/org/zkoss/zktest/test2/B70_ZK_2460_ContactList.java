package org.zkoss.zktest.test2;

public class B70_ZK_2460_ContactList {
	public final static String Category = "Category";
	public final static String B70_ZK_2460_Contact = "B70-ZK-2460-Contact";
	
	private B70_ZK_2460_ContactTreeNode root;
	public B70_ZK_2460_ContactList() {
		root = new B70_ZK_2460_ContactTreeNode(null,
			new B70_ZK_2460_ContactTreeNode[] {
				new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("Friend"),new B70_ZK_2460_ContactTreeNode[] {
					new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("High School"), new B70_ZK_2460_ContactTreeNode[] {
						new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("Fernando Terrell", "B70-ZK-2460-Contact.png")),
						new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("Stanley Larson", "B70-ZK-2460-Contact.png"))
					},true),
					new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("University"), new B70_ZK_2460_ContactTreeNode[] {
						new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("Camryn Breanna", "B70-ZK-2460-Contact.png")),
						new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("Juliana Isabela","B70-ZK-2460-Contact-gu.png")),
						new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("Holden Craig", "B70-ZK-2460-Contact-g.png"))
					}),
					new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("Emma Jones", "B70-ZK-2460-Contact-i.png")),
					new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("Eric Franklin",  "B70-ZK-2460-Contact.png")),
					new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("Alfred Wong", "B70-ZK-2460-Contact.png")),
					new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("Miguel Soto",  "B70-ZK-2460-Contact.png"))
				},true),
				new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("Work"),new B70_ZK_2460_ContactTreeNode[] {
					new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("Andrew Willis",  "B70-ZK-2460-Contact.png")),
					new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("Russell Thomas",  "B70-ZK-2460-Contact-jq.png")),
					new B70_ZK_2460_ContactTreeNode(new B70_ZK_2460_Contact("Donovan Marcus",  "B70-ZK-2460-Contact.png"))
				})
			},true
		);
	}
	public B70_ZK_2460_ContactTreeNode getRoot() {
		return root;
	}
}
