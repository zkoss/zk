/* Serializations.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Jan 6, 2012 12:08:32 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.ListModelSet;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

/**
 *
 * @author simonpai
 */
public class Serializations {
	
	/**
	 * Clone by serialization.
	 */
	public static Object clone(Object obj) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream boa = new ByteArrayOutputStream();
		new ObjectOutputStream(boa).writeObject(obj);
		byte[] bs = boa.toByteArray();
		return new ObjectInputStream(new ByteArrayInputStream(bs)).readObject();
	}
	
	public static <E> ListModel<E> toCloneableListModelList(Collection<? extends E> c) {
		return new CloneModelList<E>(c);
	}
	
	private static class CloneModelList<E> extends ListModelList<E> implements ComponentCloneListener, Cloneable {
		private CloneModelList(Collection<? extends E> c) {
			super(c);
		}

		@Override
		public Object willClone(Component comp) {
			return clone();
		}
	}

	public static <E> ListModel<E> toCloneableSimpleListModel(E[] c) {
		return new CloneSimpleListModel<E>(c);
	}
	
	private static class CloneSimpleListModel<E> extends SimpleListModel<E> implements ComponentCloneListener, Cloneable {
		private CloneSimpleListModel(E[] c) {
			super(c);
		}

		@Override
		public Object willClone(Component comp) {
			return clone();
		}
	}
	
	public static <E> ListModel<E> toCloneableListModelSet(Collection<? extends E> c) {
		return new CloneModelSet<E>(c);
	}
	
	private static class CloneModelSet<E> extends ListModelSet<E> implements ComponentCloneListener, Cloneable {
		private CloneModelSet(Collection<? extends E> e) {
			super(e);
		}

		@Override
		public Object willClone(Component comp) {
			return clone();
		}
	}
	
	public static <E> ListModel<E> toCloneableListModelAraay(E[] c) {
		return new CloneModelArray<E>(c);
	}
	
	private static class CloneModelArray<E> extends ListModelArray<E> implements ComponentCloneListener, Cloneable {
		private CloneModelArray(E[] e) {
			super(e);
		}

		@Override
		public Object willClone(Component comp) {
			return clone();
		}
	}

	public static <K,V> ListModel<Map.Entry<K, V>> toCloneableListModelMap(Map<K, V> map) {
		return new CloneModelMap<K, V>(map);
	}
	
	private static class CloneModelMap<K,V> extends ListModelMap<K,V> implements ComponentCloneListener, Cloneable {
		private CloneModelMap(Map<K, V> map) {
			super(map);
		}

		@Override
		public Object willClone(Component comp) {
			return clone();
		}
	}

	
	@SuppressWarnings("unchecked")
	public static <E> TreeModel<E> toCloneableTreeModel(TreeNode<E> root) {
		return (TreeModel<E>) new CloneTreeModel<E>(root);
	}
	
	private static class CloneTreeModel<E> extends DefaultTreeModel<E> implements ComponentCloneListener, Cloneable {
		private CloneTreeModel(TreeNode<E> e) {
			super(e);
		}

		@Override
		public Object willClone(Component comp) {
			return clone();
		}
	}

}
