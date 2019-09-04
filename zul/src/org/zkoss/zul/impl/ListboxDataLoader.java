/* ListboxDataLoader.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 23, 2009 2:53:30 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.zkoss.lang.Objects;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.Cropper;
import org.zkoss.zk.ui.sys.ShadowElementsCtrl;
import org.zkoss.zk.ui.util.ForEachStatus;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.Attributes;
import org.zkoss.zul.Frozen;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfoot;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.ListgroupRendererExt;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.ListitemRendererExt;
import org.zkoss.zul.Paging;
import org.zkoss.zul.event.GroupsDataEvent;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.GroupingInfo;
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.impl.GroupsListModel.GroupDataInfo;

/**
 * Generic {@link Listbox} data loader.
 * @author henrichen
 * @since 5.0.0
 */
public class ListboxDataLoader implements DataLoader, Cropper { //no need to serialize since Listbox assumes it
	private Listbox _listbox;

	//--DataLoader--//
	public void init(Component owner, int offset, int limit) {
		_listbox = (Listbox) owner;
	}

	public void reset() {
		//do nothing
	}

	public final Component getOwner() {
		return _listbox;
	}

	public int getOffset() {
		return 0;
	}

	public int getLimit() {
		return _listbox.getRows() > 0 ? _listbox.getRows() + 5 : 50;
	}

	public int getTotalSize() {
		final ListModel model = _listbox.getModel();
		return model != null ? model.getSize() : _listbox.getVisibleItemCount();
	}

	private int INVALIDATE_THRESHOLD = -1;

	/**
	 * updates the status of the changed group.
	 * @param event
	 * @since 8.0.4
	 */
	public void doGroupsDataChange(GroupsDataEvent event) {
		if (event.getType() == GroupsDataEvent.GROUPS_OPENED) {
			Listbox listbox = (Listbox) getOwner();
			GroupsListModel groupsListModel = ((GroupsListModel) listbox.getModel());
			int offset = groupsListModel.getGroupOffset(event.getGroupIndex());
			((Listgroup) listbox.getItems().get(offset)).setOpen(groupsListModel.getDataInfo(offset).isOpen());
		}
	}

	public void doListDataChange(ListDataEvent event) {
		if (INVALIDATE_THRESHOLD == -1) {
			INVALIDATE_THRESHOLD = Utils.getIntAttribute(this.getOwner(), "org.zkoss.zul.invalidateThreshold", 10,
					true);
		}
		//when this is called _model is never null
		final ListModel _model = _listbox.getModel();
		final int newsz = _model.getSize(), oldsz = _listbox.getItemCount();
		int min = event.getIndex0(), max = event.getIndex1(), cnt;

		switch (event.getType()) {
		case ListDataEvent.INTERVAL_ADDED:
			cnt = newsz - oldsz;
			if (cnt < 0)
				throw new UiException("Adding causes a smaller list?");
			if (cnt == 0) //no change, nothing to do here
				return;
			if ((oldsz <= 0 || cnt > INVALIDATE_THRESHOLD) && !inPagingMold())
				_listbox.invalidatePartial("rows");
			//Bug 3147518: avoid memory leak
			//Also better performance (outer better than remove a lot)
			if (min < 0)
				if (max < 0)
					min = 0;
				else
					min = max - cnt + 1;
			if (min > oldsz)
				min = oldsz;

			ListitemRenderer renderer = null;
			final Component next = min < oldsz ? _listbox.getItemAtIndex(min) : null;
			while (--cnt >= 0) {
				if (renderer == null)
					renderer = (ListitemRenderer) getRealRenderer();
				_listbox.insertBefore(newUnloadedItem(renderer, min++), next);
			}
			break;

		case ListDataEvent.INTERVAL_REMOVED:
			cnt = oldsz - newsz;
			if (cnt < 0)
				throw new UiException("Removal causes a larger list?");
			if (cnt == 0) //no change, nothing to do here
				return;
			if (min >= 0)
				max = min + cnt - 1;
			else if (max < 0)
				max = cnt - 1; //0 ~ cnt - 1			
			if (max > oldsz - 1)
				max = oldsz - 1;

			if ((newsz <= 0 || cnt > INVALIDATE_THRESHOLD) && !inPagingMold()) {
				_listbox.shallUpdateScrollPos(true);
				_listbox.invalidatePartial("rows");
			}
			//Bug 3147518: avoid memory leak
			//Also better performance (outer better than remove a lot)

			//detach from end (due to groupfoot issue)
			Component comp = _listbox.getItemAtIndex(max);
			while (--cnt >= 0) {
				Component p = comp.getPreviousSibling();
				comp.detach();
				comp = p;
			}
			break;

		default: //CONTENTS_CHANGED
			syncModel(min, max < 0 ? -1 : (max - min + 1));
			//TonyQ: B50-ZK-897 , listfoot disappear after clicking run button , 
			// 		   		sync logic with GridDataLoader 
		}
	}

	/** Creates an new and unloaded listitem. */
	protected final Listitem newUnloadedItem(ListitemRenderer renderer, int index) {
		final ListModel model = _listbox.getModel();
		Listitem item = null;
		if (model instanceof GroupsListModel) {
			final GroupsListModel gmodel = (GroupsListModel) model;
			final GroupingInfo info = gmodel.getDataInfo(index);
			switch (info.getType()) {
			case GroupDataInfo.GROUP:
				item = newListgroup(renderer);
				((Listgroup) item).setOpen(info.isOpen());
				break;
			case GroupDataInfo.GROUPFOOT:
				item = newListgroupfoot(renderer);
				break;
			default:
				item = newListitem(renderer);
			}
		} else {
			item = newListitem(renderer);
		}
		((LoadStatus) item.getExtraCtrl()).setLoaded(false);
		((LoadStatus) item.getExtraCtrl()).setIndex(index);

		newUnloadedCell(renderer, item);
		return item;
	}

	private Listitem newListitem(ListitemRenderer renderer) {
		Listitem item = null;
		if (renderer instanceof ListitemRendererExt)
			item = ((ListitemRendererExt) renderer).newListitem(_listbox);
		if (item == null) {
			item = new Listitem();
			item.applyProperties();
		}
		return item;
	}

	private Listgroup newListgroup(ListitemRenderer renderer) {
		Listgroup group = null;
		if (renderer instanceof ListgroupRendererExt)
			group = ((ListgroupRendererExt) renderer).newListgroup(_listbox);
		if (group == null) {
			group = new Listgroup();
			group.applyProperties();
		}
		return group;
	}

	private Listgroupfoot newListgroupfoot(ListitemRenderer renderer) {
		Listgroupfoot groupfoot = null;
		if (renderer instanceof ListgroupRendererExt)
			groupfoot = ((ListgroupRendererExt) renderer).newListgroupfoot(_listbox);
		if (groupfoot == null) {
			groupfoot = new Listgroupfoot();
			groupfoot.applyProperties();
		}
		return groupfoot;
	}

	private Listcell newUnloadedCell(ListitemRenderer renderer, Listitem item) {
		Listcell cell = null;
		if (renderer instanceof ListitemRendererExt)
			cell = ((ListitemRendererExt) renderer).newListcell(item);

		if (cell == null) {
			cell = new Listcell();
			cell.applyProperties();
		}
		cell.setParent(item);
		return cell;
	}

	public Object getRealRenderer() {
		final ListitemRenderer renderer = _listbox.getItemRenderer();
		return renderer != null ? renderer : _defRend;
	}

	private static final ListitemRenderer _defRend = new ListitemRenderer() {
		public void render(final Listitem item, final Object data, final int index) {
			final Listbox listbox = (Listbox) item.getParent();
			Template tm = listbox.getTemplate("model");
			GroupingInfo info = null;
			if (item instanceof Listgroup) {
				final Template tm2 = listbox.getTemplate("model:group");
				if (tm2 != null)
					tm = tm2;
				if (listbox.getModel() instanceof GroupsListModel) {
					final GroupsListModel gmodel = (GroupsListModel) listbox.getModel();
					info = gmodel.getDataInfo(index);
				}
			} else if (item instanceof Listgroupfoot) {
				final Template tm2 = listbox.getTemplate("model:groupfoot");
				if (tm2 != null)
					tm = tm2;
			}
			if (tm == null) {
				item.setLabel(Objects.toString(data));
				item.setValue(data);
			} else {
				final GroupingInfo groupingInfo = info;
				final Component[] items = ShadowElementsCtrl
						.filterOutShadows(tm.create(listbox, item, new VariableResolver() {
					public Object resolveVariable(String name) {
						if ("each".equals(name)) {
							return data;
						} else if ("forEachStatus".equals(name)) {
							return new ForEachStatus() {

								public ForEachStatus getPrevious() {
									return null;
								}

								public Object getEach() {
									return getCurrent();
								}

								public int getIndex() {
									return index;
								}

								public Integer getBegin() {
									return 0;
								}

								public Integer getEnd() {
									return listbox.getModel().getSize();
								}

								public Object getCurrent() {
									return data;
								}

								public boolean isFirst() {
									return getCount() == 1;
								}

								public boolean isLast() {
									return getIndex() + 1 == getEnd();
								}

								public Integer getStep() {
									return null;
								}

								public int getCount() {
									return getIndex() + 1;
								}
							};
						} else if ("groupingInfo".equals(name)) {
							return groupingInfo;
						} else {
							return null;
						}
					}
				}, null));
				if (items.length != 1)
					throw new UiException("The model template must have exactly one item, not " + items.length);

				final Listitem nli = (Listitem) items[0];

				//sync open state
				if (nli instanceof Listgroup && item instanceof Listgroup) {
					((Listgroup) nli).setOpen(((Listgroup) item).isOpen());
				}

				if (nli.getValue() == null) //template might set it
					nli.setValue(data);
				item.setAttribute(Attributes.MODEL_RENDERAS, nli);
				//indicate a new item is created to replace the existent one
				item.detach();
			}
		}
	};

	public void syncModel(int offset, int limit) {
		_listbox.setAttribute(Listbox.SYNCING_MODEL, Boolean.TRUE);
		try {
			syncModel0(offset, limit);
		} finally {
			//Bug ZK-2789: do not use setAttribute when actually trying to removeAttribute
			_listbox.removeAttribute(Listbox.SYNCING_MODEL);
		}
	}

	private void syncModel0(int offset, int limit) {
		int min = offset;
		int max = offset + limit - 1;

		final ListModel _model = _listbox.getModel();
		final int newsz = _model.getSize();
		final int oldsz = _listbox.getItemCount();
		final Paginal _pgi = _listbox.getPaginal();
		final boolean inPaging = inPagingMold();
		final boolean shallInvalidated = //Bug 3147518: avoid memory leak
		(min < 0 || min == 0) && (max < 0 || max >= newsz || max >= oldsz);

		int newcnt = newsz - oldsz;
		int atg = _pgi != null ? _listbox.getActivePage() : 0;
		ListitemRenderer renderer = null;
		Component next = null;
		if (oldsz > 0) {
			if (min < 0)
				min = 0;
			else if (min > oldsz - 1)
				min = oldsz - 1;
			if (max < 0)
				max = oldsz - 1;
			else if (max > oldsz - 1)
				max = oldsz - 1;
			if (min > max) {
				int t = min;
				min = max;
				max = t;
			}

			int cnt = max - min + 1; //# of affected
			if (_model instanceof GroupsListModel) {
				//detach all from end to front since groupfoot
				//must be detached before group
				newcnt += cnt; //add affected later
				if ((shallInvalidated || newcnt > INVALIDATE_THRESHOLD) && !inPaging)
					_listbox.invalidatePartial("rows");
				//Bug 3147518: avoid memory leak
				//Also better performance (outer better than remove a lot)

				Component comp = _listbox.getItemAtIndex(max);
				next = comp.getNextSibling();
				while (--cnt >= 0) {
					Component p = comp.getPreviousSibling();
					comp.detach();
					comp = p;
				}
			} else { //ListModel
				int addcnt = 0;
				Component item = _listbox.getItemAtIndex(min);
				while (--cnt >= 0) {
					next = item.getNextSibling();

					if (cnt < -newcnt) { //if shrink, -newcnt > 0
						item.detach(); //remove extra
					} else if (((Listitem) item).isLoaded()) {
						if (renderer == null)
							renderer = (ListitemRenderer) getRealRenderer();

						// ZK-2450: cache selected Index and item, added them back after detach item
						if (_pgi != null && ((Listitem) item).isSelected()) {
							int index = ((Listitem) item).getIndex();
							item.detach(); // always detach
							Listitem newItem = newUnloadedItem(renderer, min);
							_listbox.insertBefore(newItem, next);
							_listbox.addItemToSelection(newItem);
						} else {
							item.detach(); //always detach
							_listbox.insertBefore(newUnloadedItem(renderer, min), next);
						}
						++addcnt;
					}
					++min;
					item = next; //B2100338.,next item could be Paging, don't use Listitem directly
				}

				if ((shallInvalidated || addcnt > INVALIDATE_THRESHOLD || addcnt + newcnt > INVALIDATE_THRESHOLD)
						&& !inPagingMold())
					_listbox.invalidatePartial("rows");
				//Bug 3147518: avoid memory leak
				//Also better performance (outer better than remove a lot)
			}
		} else {
			min = 0;
		}

		for (; --newcnt >= 0; ++min) {
			if (renderer == null)
				renderer = (ListitemRenderer) getRealRenderer();
			_listbox.insertBefore(newUnloadedItem(renderer, min), next);
		}
		if (_pgi != null) {
			if (atg >= _pgi.getPageCount())
				atg = _pgi.getPageCount() - 1;
			_pgi.setActivePage(atg);
			if (_pgi.getTotalSize() != newsz)
				_pgi.setTotalSize(newsz); //Bug ZK-1601: reset total size since model size may changed.
		}
	}

	protected boolean inPagingMold() {
		return "paging".equals(_listbox.getMold());
	}

	protected boolean inSelectMold() {
		return "select".equals(_listbox.getMold());
	}

	public void updateModelInfo() {
		// do nothing
	}

	public void setLoadAll(boolean b) {
		// do nothing
	}

	//--Cropper--//
	public boolean isCropper() {
		return _listbox != null && inPagingMold() && _listbox.getPageSize() <= getTotalSize();
		//Single page is considered as not a cropper.
		//isCropper is called after a component is removed, so
		//we have to test >= rather than >
	}

	/** Retrieves the children available at client.
	 * <p>It can not be overridden. Rather, override {@link #getAvailableAtClient(boolean)} instead.
	 */
	public final Set<? extends Component> getAvailableAtClient() {
		return getAvailableAtClient(false);
	}

	/** Retrieves the children available at client with more control.
	 * <p>Derived class shall override this method rather than {@link #getAvailableAtClient()}.
	 * @param itemOnly whether to return only {@link Listitem} and derives.
	 * @since 5.0.10
	 */
	protected Set<? extends Component> getAvailableAtClient(boolean itemOnly) {
		if (!isCropper())
			return null;

		final Paginal pgi = _listbox.getPaginal();
		int pgsz = pgi.getPageSize();
		int ofs = pgi.getActivePage() * pgsz;
		return getAvailableAtClient(ofs, pgsz, itemOnly);
	}

	/** Retrieves the children available at the client within the given range.
	 * @param itemOnly whether to return only {@link Listitem} and derives.
	 * @since 5.0.10
	 */
	protected Set<? extends Component> getAvailableAtClient(int offset, int limit, boolean itemOnly) {
		if (!isCropper())
			return null;

		final Set<Component> avail = new LinkedHashSet<Component>(32);
		if (!itemOnly) {
			avail.addAll(_listbox.getHeads());
			final Listfoot listfoot = _listbox.getListfoot();
			if (listfoot != null)
				avail.add(listfoot);
			final Paging paging = _listbox.getPagingChild();
			if (paging != null)
				avail.add(paging);
			final Frozen frozen = _listbox.getFrozen();
			if (frozen != null)
				avail.add(frozen);
		}

		int pgsz = limit;
		int ofs = offset;
		if (_listbox.getItemCount() > 0) {
			Component item = _listbox.getItems().get(0);
			while (item != null) {
				if (pgsz == 0)
					break;
				if (item.isVisible() && item instanceof Listitem) {
					if (--ofs < 0) {
						--pgsz;
						avail.add(item);
					}
				}
				if (item instanceof Listgroup) {
					final Listgroup g = (Listgroup) item;
					if (!g.isOpen()) {
						for (int j = 0, len = g.getItemCount(); j < len; j++)
							item = item.getNextSibling();
					}
				}
				if (item != null)
					item = item.getNextSibling();
			}
		}
		return avail;
	}

	public Component getCropOwner() {
		return _listbox;
	}
}
