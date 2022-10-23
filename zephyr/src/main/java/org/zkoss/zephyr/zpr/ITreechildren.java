/* ITreechildren.java

	Purpose:

	Description:

	History:
		2:49 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zephyr.ui.util.IComponentChecker;
import org.zkoss.zul.Treechildren;

/**
 * Immutable {@link Treechildren} component
 * @author jumperchen
 * @see Treechildren
 */
@ZephyrStyle
public interface ITreechildren extends IXulElement<ITreechildren>,
		IChildable<ITreechildren, ITreeitem>, ITreeComposite<ITreechildren>, ITreeitemComposite<ITreechildren> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITreechildren DEFAULT = new ITreechildren.Builder().build();

	/**
	 * Internal use
	 *
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Treechildren> getZKType() {
		return Treechildren.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.sel.Treechildren"}</p>
	 */
	default String getWidgetClass() {
		return "zul.sel.Treechildren";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void check() {
		IComponentChecker.checkWidth(getWidth());
		IComponentChecker.checkHflex(getHflex());
	}

	/** Returns the number of child {@link ITreeitem}
	 * including all descendants. The same as {@link #getItems}.size().
	 * <p><b>Note:</b> the performance is no good.
	 */
	@Value.Lazy
	default int getItemCount() {
		int sz = 0;
		for (Iterator it = getChildren().iterator(); it.hasNext(); ++sz) {
			final ITreeitem item = (ITreeitem) it.next();
			final ITreechildren tchs = item.getTreechildren();
			if (tchs != null)
				sz += tchs.getItemCount();
		}
		return sz;
	}

	/** Returns a readonly list of all descending {@link ITreeitem}
	 * (children's children and so on).
	 *
	 * <p><b>Note:</b> the performance of the size method of returned collection
	 * is no good.
	 */
	@Value.Lazy
	default Collection<ITreeitem> getItems() {
		return new AbstractCollection<ITreeitem>() {
			public int size() {
				return getItemCount();
			}

			public boolean isEmpty() {
				return getChildren().isEmpty();
			}

			public Iterator<ITreeitem> iterator() {
				return new Iterator<ITreeitem>() {
					private final Iterator<ITreeitem> _it = getChildren().iterator();
					private Iterator<ITreeitem> _sub;

					public boolean hasNext() {
						return (_sub != null && _sub.hasNext()) || _it.hasNext();
					}

					public ITreeitem next() {
						if (_sub != null && _sub.hasNext())
							return _sub.next();

						final ITreeitem item = (ITreeitem) _it.next();
						final ITreechildren tc = item.getTreechildren();
						_sub = tc != null ? tc.getItems().iterator() : null;
						return item;
					}

					public void remove() {
						throw new UnsupportedOperationException("readonly");
					}
				};
			}
		};
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default ITreechildren assignChildIndex() {
		List<ITreeitem> children = this.getChildren();
		if (children != null && !children.isEmpty()) {
			List<ITreeitem> newChildren = new ArrayList<>(children);
			boolean hasChanged = false;
			for (int i = 0, j = newChildren.size(); i < j; i++) {
				ITreeitem iTreeitem = newChildren.get(i);
				// Only unset index will be updated here.
				// With the model case, the index will be set from the model controller,
				// so we don't change it here. Because the index might not start from 0,
				// such as Paging mold with ROD case.
				if (iTreeitem.getIndex() < 0) {
					hasChanged = true;
					newChildren.set(i, iTreeitem.withIndex(i));
				}
			}
			if (hasChanged) {
				return new Builder().from(this).setChildren(newChildren).build();
			}
		}
		return this;
	}

	/**
	 * Returns the instance with the given tree items which belong to this component.
	 * @param children The tree items of the component.
	 */
	static ITreechildren of(Iterable<? extends ITreeitem> children) {
		return new ITreechildren.Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given tree items which belong to this component.
	 * @param children The tree items of the component.
	 */
	static ITreechildren of(ITreeitem... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Builds instances of type {@link ITreechildren ITreechildren}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableITreechildren.Builder {}

	/**
	 * Builds an updater of type {@link ITreechildren} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends ITreechildrenUpdater {}
}
