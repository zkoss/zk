/* NavigationModel.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 15 16:46:37 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ListModel;

/**
 * A model that provides navigation and state checking operations - {@link #next()},
 * {@link #back()}, {@link #isFirst()} and {@link #isLast()}.
 *
 * <h3>History state support</h3>
 * Implement the {@link HistoryStateGenerator} interface and register it by
 * {@link #setHistoryStateGenerator(HistoryStateGenerator)} to enable this feature.
 * It would push a new history state when navigation changed by default.
 * <p>
 * Even the generator is set, you can still control whether to push a new history state
 * by providing the {@code pushHistory} argument when calling
 * {@link #navigateTo(Object, boolean)} or {@link #navigateToByIndex(int, boolean)}.
 * Normally we set it {@code false} on history pop state to avoid adding a duplicated state.
 *
 * @author rudyhuang
 * @since 8.6.0
 */
public class NavigationModel<T> {
	private static final String CURRENT = "current";
	private List<T> _items = new ArrayList<T>();
	private int _selectedIndex = 0;
	private HistoryStateGenerator<T, ?> _HistoryStateGenerator = null;

	/**
	 * Constructor.
	 */
	public NavigationModel() {
	}

	/**
	 * Constructor.
	 * It makes a copy of the specified array (i.e., not live).
	 *
	 * @param items the array
	 */
	public NavigationModel(T[] items) {
		this(items != null ? Arrays.asList(items) : Collections.<T>emptyList());
	}

	/**
	 * Constructor.
	 * It makes a copy of the specified collection (i.e., not live).
	 */
	public NavigationModel(Collection<? extends T> c) {
		if (c != null && !c.isEmpty()) {
			_items.addAll(c);
		}
	}

	/**
	 * Constructor.
	 * It copies the items from the model (i.e., not live).
	 *
	 * @param model the model
	 */
	public NavigationModel(ListModel<? extends T> model) {
		if (model != null && model.getSize() > 0) {
			for (int i = 0; i < model.getSize(); i++) {
				_items.add(model.getElementAt(i));
			}
		}
	}

	/**
	 * Sets the history state generator.
	 * Once set this, a new history state will be pushed when navigating items.
	 * <p>
	 * Default: null (no history state)
	 *
	 * @param generator A {@link HistoryStateGenerator} object. Set null to disable history state support.
	 */
	public void setHistoryStateGenerator(HistoryStateGenerator<T, ?> generator) {
		this._HistoryStateGenerator = generator;
	}

	/**
	 * Navigates to the specific item.
	 * Pushes a history state by default if a {@link HistoryStateGenerator} object is provided.
	 *
	 * @param item The item that navigates to
	 * @throws UiException if the item is not in the model
	 * @see #navigateTo(Object, boolean)
	 */
	public void navigateTo(T item) {
		navigateToByIndex(getIndex(item), true);
	}

	/**
	 * Navigates to the specific item.
	 *
	 * @param item The item that navigates to
	 * @param pushHistory if history state will be pushed. Ideal when navigating at history pop state.
	 * @throws UiException if the item is not in the model
	 */
	public void navigateTo(T item, boolean pushHistory) {
		navigateToByIndex(getIndex(item), pushHistory);
	}

	private int getIndex(T item) {
		int index = _items.indexOf(item);
		if (index == -1)
			throw new UiException("item not found: " + item);

		return index;
	}

	/**
	 * Navigates to the specific item by index.
	 * Pushes a history state by default if a {@link HistoryStateGenerator} object is provided.
	 *
	 * @param index The index that navigates to
	 * @throws UiException if the index is out of bound
	 * @see #navigateToByIndex(int, boolean)
	 */
	public void navigateToByIndex(int index) {
		navigateToByIndex(index, true);
	}

	/**
	 * Navigates to the specific item by index.
	 *
	 * @param index The index that navigates to
	 * @param pushHistory if history state will be pushed. Ideal when navigating at history pop state.
	 * @throws UiException if the index is out of bound
	 */
	public void navigateToByIndex(int index, boolean pushHistory) {
		if (index < 0 || index >= _items.size())
			throw new UiException("index out of bound: " + index);

		if (index != _selectedIndex) {
			_selectedIndex = index;
			BindUtils.postNotifyChange(null, null, this, CURRENT);
			if (pushHistory) pushHistoryState();
		}
	}

	private void pushHistoryState() {
		if (_HistoryStateGenerator != null) {
			T current = getCurrent();
			Object state = _HistoryStateGenerator.getState(current, _selectedIndex);
			String title = _HistoryStateGenerator.getTitle(current, _selectedIndex);
			String url = _HistoryStateGenerator.getUrl(current, _selectedIndex);
			Executions.getCurrent().getDesktop().pushHistoryState(state, title, url);
		}
	}

	/**
	 * Gets the current item. Could be {@code null} if the model is empty.
	 *
	 * @return item
	 */
	public T getCurrent() {
		return _items.isEmpty() ? null : _items.get(_selectedIndex);
	}

	/**
	 * Gets the position of item.
	 *
	 * @param item the item to be checked
	 * @return the value {@code 0} if item is the current item;
	 *         a value less than {@code 0} if item is before the current; and
	 *         a value greater than {@code 0} if item is after the current
	 * @throws UiException if the item is not in the model
	 */
	public int getPosition(T item) {
		int index = getIndex(item);
		return (index < _selectedIndex) ? -1 : ((index == _selectedIndex) ? 0 : 1);
	}

	/**
	 * Steps back to the previous item.
	 */
	public void back() {
		if (!_items.isEmpty()) {
			int index = getCurrentIndex();
			if (index > 0) navigateToByIndex(index - 1);
		}
	}

	/**
	 * Steps forward to the next item.
	 */
	public void next() {
		if (!_items.isEmpty()) {
			int index = getCurrentIndex();
			if (index < _items.size() - 1) navigateToByIndex(index + 1);
		}
	}

	/**
	 * Returns if it is the first item currently.
	 *
	 * @return true if the first
	 */
	@DependsOn(CURRENT)
	public boolean isFirst() {
		return getCurrentIndex() == 0;
	}

	/**
	 * Returns if it is the last item currently.
	 *
	 * @return true if the last
	 */
	@DependsOn(CURRENT)
	public boolean isLast() {
		return _items.isEmpty() || getCurrentIndex() == _items.size() - 1;
	}

	/**
	 * Gets the inner {@code List} object. Intended for application developers to use.
	 *
	 * @return {@code List} object
	 */
	protected List<T> getInnerItems() {
		return _items;
	}

	/**
	 * Gets the items in the model.
	 *
	 * @return Unmodifiable {@code List} object
	 */
	public List<T> getItems() {
		return Collections.unmodifiableList(getInnerItems());
	}

	protected int getCurrentIndex() {
		return _selectedIndex;
	}

	/**
	 * The History state generator interface.
	 *
	 * @param <T> Item Type
	 * @param <E> History state Type
	 * @since 8.6.0
	 */
	public interface HistoryStateGenerator<T, E> {
		/**
		 * Generates the history state object.
		 *
		 * @param item the item
		 * @param index the index of this item
		 * @return The history state object
		 */
		E getState(T item, int index);

		/**
		 * Generates the title of history state.
		 *
		 * @param item the item
		 * @param index the index of this item
		 * @return the title of history state
		 */
		String getTitle(T item, int index);

		/**
		 * Generates the url of history state.
		 *
		 * @param item the item
		 * @param index the index of this item
		 * @return the url of history state
		 */
		String getUrl(T item, int index);
	}
}
