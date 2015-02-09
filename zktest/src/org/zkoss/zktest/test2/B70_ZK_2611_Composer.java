package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;

public class B70_ZK_2611_Composer extends SelectorComposer<Component> {

	@Wire
	Tree tree;
	private DefaultTreeModel<String> model;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		@SuppressWarnings("unchecked")
		DefaultTreeNode<String> root = new DefaultTreeNode<String>("root", Arrays.asList(
				new DefaultTreeNode<String>("Node 1", Arrays.asList(
						new DefaultTreeNode<String>("1.1"),
						new DefaultTreeNode<String>("1.2", Arrays.asList(
								new DefaultTreeNode<String>("1.2.1"),
								new DefaultTreeNode<String>("1.2.2")
						))
				)),
				new DefaultTreeNode<String>("Node 2", Arrays.asList(
						new DefaultTreeNode<String>("2.1", Arrays.asList(
								new DefaultTreeNode<String>("2.1.1"),
								new DefaultTreeNode<String>("2.1.2")
						)),
						new DefaultTreeNode<String>("2.2")
				)),
				new DefaultTreeNode<String>("Node 3", Arrays.asList(
						new DefaultTreeNode<String>("3.1"),
						new DefaultTreeNode<String>("3.2")
						)),
				new DefaultTreeNode<String>("Node 4", Arrays.asList(
						new DefaultTreeNode<String>("4.1"),
						new DefaultTreeNode<String>("4.2")
						)),
				new DefaultTreeNode<String>("Node 5", Arrays.asList(
						new DefaultTreeNode<String>("5.1"),
						new DefaultTreeNode<String>("5.2", Arrays.asList(
								new DefaultTreeNode<String>("5.2.1", Arrays.asList(
										new DefaultTreeNode<String>("5.2.1.1"),
										new DefaultTreeNode<String>("5.2.1.2")
								)),
								new DefaultTreeNode<String>("5.2.2")
						))
				))
			)
		);
		model = new DefaultTreeModel<String>(root);
		model.setMultiple(true);
		tree.setModel(model);
	}
	
	@Listen("onClick=#removeButton")
	public void removeNode() {
		String openPathsBefore = logPaths(model.getOpenPaths());
		String openObjectsBefore = model.getOpenObjects().toString();
		System.out.println("Open paths before: " + openPathsBefore);
		System.out.println("Open objects before: " + openObjectsBefore);
		
		model.getRoot().getChildren().remove(0);
		
		String openPathsAfter = logPaths(model.getOpenPaths());
		String openObjectsAfter = model.getOpenObjects().toString();
		
		System.out.println("Open paths after: " + openPathsAfter);
		System.out.println("Open objects after: " + openObjectsAfter);
	}
	
	@Listen("onClick=#addButton")
	public void addNode() {
		String openPathsBefore = logPaths(model.getOpenPaths());
		String openObjectsBefore = model.getOpenObjects().toString();
		System.out.println("Open paths before: " + openPathsBefore);
		System.out.println("Open objects before: " + openObjectsBefore);
		
		model.getRoot().getChildren().add(1, new DefaultTreeNode<String>("1.5"));
		
		String openPathsAfter = logPaths(model.getOpenPaths());
		String openObjectsAfter = model.getOpenObjects().toString();
		
		System.out.println("Open paths after: " + openPathsAfter);
		System.out.println("Open objects after: " + openObjectsAfter);
	}
	
	public String logPaths(int[][] paths) {
		List<String> pathTexts = new ArrayList<String>();
		for(int i = 0; i < paths.length; i++) {
			pathTexts.add(Arrays.toString(paths[i]));
		}
		return pathTexts.toString();
	}
}