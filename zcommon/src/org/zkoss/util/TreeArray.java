/* TreeArray.java


	Purpose: Red-black tree based array implementation of List interface.
	Description:
	History:
	 2001/5/9, Tom M. Yeh: Created.


Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util;

import java.util.*;

/**
 * Red-black tree based array implementation of List interface.
 * Unlike LinkedList, the random access by index is as fast as log(n).
 * Unlike ArrayList, the insertion is as fast as log(n). It is
 * a great compromise between randown and sequential access.
 *
 * <p>In additions, it extends the features by also implementing
 * ListX.
 *
 * <p>The deriving class might override newEntry if it also
 * extends RbEntry; override insert(RbEntry, RbEntry) for adding element;
 * override delete(RbEntry) for removing element; clear() for
 * clearing the whole list.
 *
 * <p>Also, RbEntry.setElement might be overrided if the deriving class
 * wants to do something when the set method is called.
 *
 * <p>The iterator method is designed such that next() will proceed correctly
 * even if getElement() throws an exception.
 *
 * <p>The original algorithm is invented by Henri Chen.
 *
 * @author tomyeh
 * @see ListX
 */
public class TreeArray extends AbstractList
implements ListX, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 20060622L;

    protected static final boolean RED   = false;
    protected static final boolean BLACK = true;

	/**
	 * Caller shall use AbstractList.Entry instead of RbEntry for
	 * better portability.
	 */
	protected static class RbEntry implements Entry {
		protected Object	element; //use setElement and getElement
		protected int   	leftNum; //# of elements on its left sub-tree
		protected RbEntry	left;
		protected RbEntry	right;
		protected RbEntry	parent;
		protected boolean	color; //a black node
		protected boolean	orphan = false; //not belonging to any list

		protected RbEntry(Object element) {
			this.element = element;
		}

		/**
		 * Override it if you want to something when an element is retrieved.
		 * All other parts that get element must invoke this method
		 */
		public Object getElement() {
			return this.element;
		}

		/**
		 * Override it if you want to do something when an element
		 * is set. All other parts that set element will invoke this method.
		 */
		public void setElement(Object element) {
			this.element = element;
		}

		public final boolean isOrphan() {
			return orphan;
		}

		protected final RbEntry nextEntry() {
			if (right != null)
				return right.leftMost();
			return firstRightAncestor();
		}
		public final Entry next() {
			return nextEntry();
		}
		protected final RbEntry previousEntry() {
			if (left!=null)
				return left.rightMost();
			return firtLeftAncestor();
		}
		public final Entry previous() {
			return previousEntry();
		}

		//-- utilities --/
		/**
		 * Gets the leftmost leaf of the specified subtree.
		 * It is the entry with lowest index in the subtree.
		 */
		protected final RbEntry leftMost() {
			for (RbEntry p=this;; p=p.left)
				if (p.left==null)
					return p;
		}
		/**
		 * Gets the rihtmost leaf of the specified subtree.
		 * It is the entry with highest index in the subtree.
		 */
		protected final RbEntry rightMost() {
			for (RbEntry p=this;; p=p.right)
				if (p.right==null)
					return p;
		}
		/**
		 * Gets the first ancestor at the left of the specified entry.
		 * "At the left" we mean the returned ancesor's right is the entry
		 * or its ancestor. It is also the first parent with lower index.
		 */
		protected final RbEntry firtLeftAncestor() {
			for (RbEntry p=this; p.parent!=null; p=p.parent)
				if (p.parent.right == p)
					return p.parent;
			return null;
		}
		/**
		 * Gets the first parent at the right of the specified entry.
		 * "At the right" we mean the returned ancesor's right is the entry
		 * or its ancestor. It is also the first parent with higer index.
		 */
		protected final RbEntry firstRightAncestor() {
			for (RbEntry p=this; p.parent != null; p = p.parent)
				if (p.parent.left == p)
					return p.parent;
			return null;
		}

		//It doesn't maintain the tree but reset itself as an orphan.
		private final void setOrphan() {
			orphan = true;
			left = right = parent = null;
		}

		/**
		 * Called by TreeArray.clear to do clear recursively.
		 * Since it always be invoked to clear the whole tree,
		 * it doesn't have to maintain leftNum or other tree info.
		 * <p>However, this.element is kept.
		 */
		protected void clear() {
			if (left != null) left.clear();
			if (right != null) right.clear();
			setOrphan();
		}
	}//class RbEntry

	protected transient RbEntry _root = null;
	protected transient int _size = 0;
	protected transient int _hashCode = 0;

	public TreeArray() {
	}

	/** Constructor with a collection.
	 * @param c the collection to add; null to ignore
	 */
	public TreeArray(Collection c) {
		this();
		if (c != null)
			addAll(c);
	}

	//-- its own extension --//
	/**
	 * Adds an element by its natural ordering.
	 * This array must be sorted into ascending order according to
	 * the natural ordering. To sort, either sort
	 * or add all elements by order.
	 * <p>All elements are assumed to implement Comparable.
	 */
	public final void addByOrder(Object element) {
		int j = search(element);
		if (j < 0)
			j = -j-1;
		add(j, element);
	}
	/**
	 * Adds an element by the specified comparator.
	 * This array must be sorted into ascending order according to
	 * the specified comparator. To sort, either sort
	 * or add all elements by order.
	 */
	public final void addByOrder(Object element, Comparator c) {
		int j = search(element, c);
		if (j < 0)
			j = -j-1;
		add(j, element);
	}

	/**
	 * Removes an element by its natural ordering.
	 * This array must be sorted into ascending order according to
	 * the natural ordering. To sort, either sort
	 * or add all elements by order.
	 * <p>All elements are assumed to implement Comparable.
	 */
	public final boolean removeByOrder(Object element) {
		int j = search(element);
		if (j >= 0) {
			remove(j);
			return true;
		}
		return false;
	}
	/**
	 * Removes an element by the specified comparator.
	 * This array must be sorted into ascending order according to
	 * the specified comparator. To sort, either sort
	 * or add all elements by order.
	 */
	public final boolean removeByOrder(Object element, Comparator c) {
		int j = search(element, c);
		if (j >= 0) {
			remove(j);
			return true;
		}
		return false;
	}

	/**
	 * Adds all elements by their natural ordering.
	 * This array must be sorted into ascending order according to
	 * the natural ordering. To sort, either sort
	 * or add all elements by order.
	 * <p>All elements are assumed to implement Comparable.
	 */
	public final void addAllByOrder(Collection cn) {
		for (Iterator it = cn.iterator(); it.hasNext();)
			addByOrder(it.next());
	}
	/**
	 * Adds all elements by the specified comparator.
	 * This array must be sorted into ascending order according to
	 * the specified comparator. To sort, either sort
	 * or add all elements by order.
	 */
	public final void addAllByOrder(Collection cn, Comparator c) {
		for (Iterator it = cn.iterator(); it.hasNext();)
			addByOrder(it.next(), c);
	}

	/**
	 * Searches an element by its natural ordering.
	 * This array must be sorted into ascending order according to
	 * the natural ordering. To sort, either sort
	 * or add all elements by order, {@link #addByOrder(Object)}.
	 *
	 * <p>All elements are assumed to implement Comparable.
	 * Note: the element argument of this method is passed as the argument 
	 * of the compareTo method. Thus, it is OK to pass any kind of object,
	 * as long as the elements stored in this array is able to detect it.
	 *
	 * <p>For example, you might use a String to search the element,
	 * and your element's compareTo shall be implemented as follows.
	 *
	 * <pre><code>public int compareTo(Object o) {
	 *  return o instanceof String ?
	 *		_name.compareTo((String)o):
	 *		_name.compareTo(((YourClass)o).getName());
	 *}
	 */
	public final int search(Object element) {
		return Collections.binarySearch(this, element);
	}
	/**
	 * Searches an element by the specified comparator.
	 * This array must be sorted into ascending order according to
	 * the specified comparator. To sort, either sort
	 * or add all elements by order, {@link #addByOrder(Object, Comparator)}.
	 *
	 * <p>All elements are assumed to implement Comparable.
	 * Note: the element argument of this method is passed as the argument 
	 * of the compareTo method. Thus, it is OK to pass any kind of object,
	 * as long as the elements stored in this array is able to detect it.
	 */
	public final int search(Object element, Comparator c) {
		return Collections.binarySearch(this, element, c);
	}
	/**
	 * Gets an element by its natural ordering.
	 * It is a shortcut of get(search(element)).
	 *
	 * @see #search(Object)
	 * @return null if not found
	 */
	public final Object getByOrder(Object element) {
		int j = search(element);
		return j >= 0 ? get(j): null;
	}
	/**
	 * Gets an element by its natural ordering.
	 * It is a shortcut of get(search(element, c)).
	 *
	 * @see #search(Object, Comparator)
	 * @return null if not found
	 */
	public final Object getByOrder(Object element, Comparator c) {
		int j = search(element, c);
		return j >= 0 ? get(j): null;
	}
	/**
	 * Sorts all elements ascendingly by the natural ordering.
	 * <p>All elements are assumed to implement Comparable.
	 */
	public final void sort() {
		Collections.sort(this);
	}
	/**
	 * Sorts all elements ascendingly by the specified comparator.
	 */
	public final void sort(Comparator c) {
		Collections.sort(this, c);
	}

	//-- overriding AbstractList --//
	public final int size() {
		return _size;
	}

    public final Object get(int index) {
    	return getRbEntry(index).getElement();
    }

	public Object set(int index, Object element) {
		RbEntry p = getRbEntry(index);
		Object old = p.getElement();
		p.setElement(element);
		return old;
	}

	public void add(int index, Object element) {
		addEntry(index, element);
	}

	public Object remove(int index) {
		RbEntry p = getRbEntry(index);
		delete(p);
		return p.getElement();
	}

	public final Iterator iterator() {
		return listIterator();
	}

	public final ListIterator listIterator(int index) {
		return new Iter(index);
	}

	/**
	 * Clears the whole list. Overrides it if the derived class has
	 * other data to clear. Note it doesn't call removeEx.
	 *
	 * <p>Note clear actually invokes RbEntry.clear to do the real
	 * cleanup. Deriving classes might override RbEntry.clear.
	 */
	public void clear() {
		if (_root != null) {
			_root.clear();
			modCount++;
			_size = 0;
			_root = null;
		}
	}

	//-- overriding ListX --//
	protected final RbEntry getRbEntry(int index) {
		checkRange(index);

		int baseIndex = 0;
		RbEntry p = _root;
		do {
			int thisIndex = baseIndex + p.leftNum;
			if (index == thisIndex) {
				return p;
			} else if (index < thisIndex) {
				p = p.left;
			} else {
				baseIndex = thisIndex + 1;
				p = p.right;
			}
		} while (p != null); //it might be modified by someone else
		throw new ConcurrentModificationException();
	}

	public final Entry getEntry(int index) {
		return getRbEntry(index);
	}

	protected final int indexOfEntry(RbEntry p) {
		if (p.orphan)
			return -1;

		int v = p.leftNum;
		RbEntry lowParent = p.firtLeftAncestor();
		if (lowParent != null)
			 v += indexOfEntry(lowParent) + 1;
		return v;
	}	

	public final int indexOfEntry(Entry p) {
		return indexOfEntry((RbEntry)p);
	}

	public int hashCode() {
		if (_hashCode == 0)
			_hashCode = super.hashCode();
		return _hashCode;
	}

	//-- Extra features --//
	public final ListIterator entryIterator(int index) {
		return new EntryIter(index);
	}
	public final ListIterator entryIterator() {
		return new EntryIter(0);
	}

	/**
	 * Creates an instance of RbEntry. Override it if necessary
	 */
	protected RbEntry newEntry(Object element) {
		return new RbEntry(element);
	}

	public final Entry addEntry(Entry insertBefore, Object element) {
		return insert(checkNotOrphan(insertBefore), newEntry(element));
	}

	public final Entry addEntry(int index, Object element) {
		return insert(index, newEntry(element));
	}

	public final Entry addEntry(Object element) {
		return addEntry(_size, element);
	}

	public final void removeEntry(Entry p) {
		delete(checkNotOrphan(p));
	}

	public final Entry removeEntry(int index) {
		return delete(index);
	}

	//-- utilities --/
	/** Returns the first node. */
	protected final RbEntry first() {
		return _root==null ? null: _root.leftMost();
	}

	private static final boolean colorOf(RbEntry p) {
		return (p == null ? BLACK : p.color);
	}

	private static final RbEntry  parentOf(RbEntry p) {
		return (p == null ? null: p.parent);
	}

	private static final void setColor(RbEntry p, boolean c) {
		if (p != null)  p.color = c;
	}

	private static final RbEntry leftOf(RbEntry p) {
		return (p == null)? null: p.left;
	}

	private static final RbEntry rightOf(RbEntry p) {
		return (p == null)? null: p.right;
	}

	protected final RbEntry insert(int index, RbEntry p) {
		checkRangePlus(index); //index==_size is ok
		return insert(index<_size ? getRbEntry(index): null, p);
	}

	private static final void changeAncestorLeftNum(RbEntry p, int diff) {
		for (; p.parent!=null; p=p.parent)
			if (p.parent.left == p)
				p.parent.leftNum += diff;
	}

	/**
	 * All add methods are done thru this method. Override it if necessary.
	 *
	 * <p>Note: p is inserted <b>before</b> insertBefore.
	 */
	protected RbEntry insert(RbEntry insertBefore, final RbEntry p) {
		assert !p.orphan;
		if (_root == null) {
			_root = p;
		} else {
			if (insertBefore != null && insertBefore.left == null) {
				insertBefore.left = p;
			} else {
				insertBefore = insertBefore==null ? _root: insertBefore.left;
				insertBefore = insertBefore.rightMost();
				insertBefore.right = p;
			}
			p.parent = insertBefore;

			changeAncestorLeftNum(p, 1);
		}

		fixAfterInsert(p); //fix up for red-black rule
		incSize();
		return p;
	}

	private final void rotateLeft(RbEntry x) {
		RbEntry y = x.right;
		x.right = y.left;
		if (y.left != null)
			y.left.parent = x;
		y.parent = x.parent;
		if (x.parent == null)
			_root = y;
		else if (x.parent.left == x)
			x.parent.left = y;
		else
			x.parent.right = y;
		y.left = x;
		x.parent = y;

		y.leftNum += x.leftNum+1;
	}

	private final void rotateRight(RbEntry y) {
		RbEntry x = y.left;
		y.left = x.right;
		if (x.right != null)
			x.right.parent = y;
		x.parent = y.parent;
		if (y.parent == null)
			_root = x;
		else if (y.parent.right == y)
			y.parent.right = x;
		else
			y.parent.left = x;
		x.right = y;
		y.parent = x;

		y.leftNum -= x.leftNum+1;
	}

	private final void fixAfterInsert(RbEntry x) {
		x.color = RED;
		while (x!=null && x!=_root && x.parent.color==RED) {
			if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
				RbEntry y = rightOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == rightOf(parentOf(x))) {
						x = parentOf(x);
						rotateLeft(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					if (parentOf(parentOf(x)) != null)
						rotateRight(parentOf(parentOf(x)));
				}
			} else {
				RbEntry y = leftOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == leftOf(parentOf(x))) {
						x = parentOf(x);
						rotateRight(x);
					}
					setColor(parentOf(x),  BLACK);
					setColor(parentOf(parentOf(x)), RED);
					if (parentOf(parentOf(x)) != null)
						rotateLeft(parentOf(parentOf(x)));
				}
			}
		}//while
		_root.color = BLACK;
	}

	protected RbEntry delete(int index) {
		RbEntry p = getRbEntry(index);
		delete(p);
		return p;
	}

	/**
	 * All remove methods are done thru this method. Override it if necessary.
	 */
	protected void delete(RbEntry p) {
		assert !p.orphan;
		if (p.left!=null && p.right!=null)
			swapPosition(p.nextEntry(), p);
				//Make sure at least one of left or right is null by
				//swapping with next, whose left is null (right.leftMost)

		changeAncestorLeftNum(p, -1);
		decSize();

		RbEntry replacement = (p.left != null ? p.left : p.right);
		if (replacement != null) {
			replacement.parent = p.parent;
			if (p.parent == null) 
				_root = replacement;
			else if (p == p.parent.left)
				p.parent.left  = replacement;
			else
				p.parent.right = replacement;

			if (p.color == BLACK)
				fixAfterDelete(replacement);
		} else { // No children
			if (p.parent == null) // size==1
				_root = null;
			else if (p == p.parent.left)
					p.parent.left = null;
			else if (p == p.parent.right)
					p.parent.right = null;
		}
		p.setOrphan();
	}

	/**
	 * Swap the linkages of two nodes in a tree. We cannot only swap the
	 * element field because the binding of entry and element have to remain.
	 */
	private final void swapPosition(RbEntry x, RbEntry y) {
		// Save initial values.
		RbEntry px = x.parent, lx = x.left, rx = x.right;
		RbEntry py = y.parent, ly = y.left, ry = y.right;
		boolean xWasLeftChild = px != null && x == px.left;
		boolean yWasLeftChild = py != null && y == py.left;

		// Swap, handling special cases of one being the other's parent.
		if (x == py) {  // x was y's parent
			x.parent = y;
			if (yWasLeftChild) {
				y.left = x;
				y.right = rx;
			} else {
				y.right = x;
				y.left = lx;
			}
		} else {
			x.parent = py;
			if (py != null) {
				if (yWasLeftChild)
					py.left = x;
				else
					py.right = x;
			}
			y.left = lx;
			y.right = rx;
		}

		if (y == px) { // y was x's parent
			y.parent = x;
			if (xWasLeftChild) {
				x.left = y;
				x.right = ry;
			} else {
				x.right = y;
				x.left = ly;
			}
		} else {
			y.parent = px;
			if (px != null) {
				if (xWasLeftChild)
					px.left = y;
				else
					px.right = y;
			}
			x.left = ly;
			x.right = ry;
		}

		// Fix children's parent pointers
		if (x.left != null)
			x.left.parent = x;
		if (x.right != null)
			x.right.parent = x;
		if (y.left != null)
			y.left.parent = y;
		if (y.right != null)
			y.right.parent = y;

		// Swap colors
		boolean c = x.color;
		x.color = y.color;
		y.color = c;

		// Swap leftNum
		int v = x.leftNum;
		x.leftNum = y.leftNum;
		y.leftNum = v;

		// Check if root changed
		if (_root == x)
			_root = y;
		else if (_root == y)
			_root = x;
	}

	private void fixAfterDelete(RbEntry x) {
		while (x != _root && colorOf(x) == BLACK) {
			if (x == leftOf(parentOf(x))) {
				RbEntry sib = rightOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateLeft(parentOf(x));
					sib = rightOf(parentOf(x));
				}

				if (colorOf(leftOf(sib))  == BLACK &&
					colorOf(rightOf(sib)) == BLACK) {
					setColor(sib,  RED);
					x = parentOf(x);
				} else {
					if (colorOf(rightOf(sib)) == BLACK) {
						setColor(leftOf(sib), BLACK);
						setColor(sib, RED);
						rotateRight(sib);
						sib = rightOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(rightOf(sib), BLACK);
					rotateLeft(parentOf(x));
					x = _root;
				}
			} else { // symmetric
				RbEntry sib = leftOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateRight(parentOf(x));
					sib = leftOf(parentOf(x));
				}

				if (colorOf(rightOf(sib)) == BLACK &&
					colorOf(leftOf(sib)) == BLACK) {
					setColor(sib,  RED);
					x = parentOf(x);
				} else {
					if (colorOf(leftOf(sib)) == BLACK) {
						setColor(rightOf(sib), BLACK);
						setColor(sib, RED);
						rotateLeft(sib);
						sib = leftOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(leftOf(sib), BLACK);
					rotateRight(parentOf(x));
					x = _root;
				}
			}
		}

		setColor(x, BLACK);
	}

	protected final void incSize() {modCount++; _size++; }
	protected final void decSize() {modCount++; _size--; }

	protected final void checkRange(int index) {
		if (index >= _size || index < 0)
			indexOutOfBounds(index);
	}

	protected final void checkRangePlus(int index) {
		if (index > _size || index < 0)
			indexOutOfBounds(index);
	}

	protected final void indexOutOfBounds(int index) {
		throw new IndexOutOfBoundsException(
				"Index: " + index + ", Size: " + _size);
	}

	/**
	 * Converts and checks whether it is not orphan
	 */
	protected final RbEntry checkNotOrphan(Entry entry) {
		RbEntry p = (RbEntry)entry;
		if (p.orphan)
			throw new IllegalStateException();
		return p;
	}

	private class EntryIter implements ListIterator {
		private RbEntry _cursor;
		private RbEntry _lastRet = null;
		private int _expectedModCount;

		private EntryIter(int index) {
			checkRangePlus(index); //index==_size is ok

			_cursor = index<_size ? getRbEntry(index): null;
			_expectedModCount = modCount;
		}
		
		public final boolean hasNext() {
			checkComodification();
			return _cursor != null;
		}
		public Object next() {
			checkComodification();

			if (_cursor == null)
				throw new NoSuchElementException();

			_lastRet = _cursor;
			Object obj = _cursor;
			_cursor = _cursor.nextEntry();
			return obj;
		}

		public final boolean hasPrevious() {
			checkComodification();
			if (_cursor == null)
				return _size > 0;
			else
				return _cursor.previousEntry() != null;
		}
		public Object previous() {
			checkComodification();

			if (_cursor == null) {
				if (_size == 0)
					throw new NoSuchElementException();
				_cursor = getRbEntry(_size-1);
			} else {
				RbEntry prev = _cursor.previousEntry();
				if (prev == null)
					throw new NoSuchElementException();
				_cursor = prev;
			}
			_lastRet = _cursor;
			return _cursor;
		}

		public final int nextIndex() {
			return _cursor==null ? _size: indexOfEntry(_cursor);
		}
		public final int previousIndex() {
			return _cursor==null ? _size-1: indexOfEntry(_cursor)-1;
		}

		public final void remove() {
			if (_lastRet == null)
				throw new IllegalStateException();

			checkComodification();

			if (_lastRet == _cursor)
				_cursor = _cursor.nextEntry();

			delete(_lastRet);
			_expectedModCount = modCount;
			_lastRet = null;
		}

		public final void set(Object obj) {
			if (_lastRet == null)
				throw new IllegalStateException();

			checkComodification();

			_lastRet.setElement(obj);
			//no need to update _expectdModCount here
		}
		public final Object get() {
			return _lastRet.getElement();
		}

		public final void add(Object obj) {
			checkComodification();

			insert(_cursor, newEntry(obj));
			_expectedModCount = modCount;
		}

		private final void checkComodification() {
			if (modCount != _expectedModCount)
				throw new ConcurrentModificationException();
		}
	}//EntryIter

	private class Iter extends EntryIter {
		private Iter(int index) {
			super(index);
		}
		public final Object next() {
			return ((RbEntry)super.next()).getElement();
		}
		public final Object previous() {
			return ((RbEntry)super.previous()).getElement();
		}
	}//Iter

	//-- cloneable --//
	public Object clone() {
		TreeArray clone;
		try {
			clone = (TreeArray)super.clone();
		}catch(CloneNotSupportedException e) {
			throw new InternalError();
		}

		//Put clone into "virgin" state
		clone._size = 0;
		clone._root = null;
		clone.modCount = 0;

		// Initialize clone with our elements
		for (RbEntry p = first(); p != null; p = p.nextEntry())
		    clone.add(p.getElement());

		return clone;
	}

	//-- Serializable --//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

        s.writeInt(_size);

		for (RbEntry p = first(); p != null; p = p.nextEntry())
			s.writeObject(p.getElement());
	}

	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		int size = s.readInt();

		for (int i=0; i<size; i++)
			add(s.readObject());
	}
}
