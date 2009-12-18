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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.Cropper;
import org.zkoss.zul.Frozen;
import org.zkoss.zul.Group;
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
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.Paginal;
import org.zkoss.zul.ext.Selectable;
import org.zkoss.zul.impl.GroupsListModel.GroupDataInfo;

/**
 * Generic {@link Listbox} data loader.
 * @author henrichen
 * @since 5.0.0
 */
public class ListboxDataLoader implements DataLoader, Cropper {
	private transient Listbox _listbox;

	//--DataLoader--//
	public void init(Component owner, int offset, int limit) {
		_listbox = (Listbox) owner;
	}
	
	final public Component getOwner() {
		return _listbox;
	}

	public int getOffset() {
		return 0;
	}
	
	public int getLimit() {
		return _listbox.getRows() > 0 ? _listbox.getRows() + 5 : 20;
	}

	public int getTotalSize() {
		final ListModel model = _listbox.getModel();
		return model != null ? model.getSize() : _listbox.getVisibleItemCount();
	}
	
	public void doListDataChange(ListDataEvent event) {
		//when this is called _model is never null
		final ListModel _model = _listbox.getModel();
		final int newsz = _model.getSize(), oldsz = _listbox.getItemCount();
		int min = event.getIndex0(), max = event.getIndex1(), cnt;

		switch (event.getType()) {
		case ListDataEvent.INTERVAL_ADDED:
			cnt = newsz - oldsz;
			if (cnt <= 0)
				throw new UiException("Adding causes a smaller list?");
			if (cnt > 50 && !inPagingMold())
				_listbox.invalidate(); //performance is better
			if (min < 0)
				if (max < 0) min = 0;
				else min = max - cnt + 1;
			if (min > oldsz) min = oldsz;

			ListitemRenderer renderer = null;
			final Component next =
				min < oldsz ? _listbox.getItemAtIndex(min): null;
			while (--cnt >= 0) {
				if (renderer == null)
					renderer = (ListitemRenderer) getRealRenderer();
				_listbox.insertBefore(newUnloadedItem(renderer, min++), next);
			}
			break;

		case ListDataEvent.INTERVAL_REMOVED:
			cnt = oldsz - newsz;
			if (cnt <= 0)
				throw new UiException("Removal causes a larger list?");
			if (min >= 0) max = min + cnt - 1;
			else if (max < 0) max = cnt - 1; //0 ~ cnt - 1			
			if (max > oldsz - 1) max = oldsz - 1;

			//detach from end (due to groopfoot issue)
			Component comp = _listbox.getItemAtIndex(max);
			while (--cnt >= 0) {
				Component p = comp.getPreviousSibling();
				comp.detach();
				comp = p;
			}
			break;

		default: //CONTENTS_CHANGED
			syncModel(min, max);
		}
	}
	
	/** Creates an new and unloaded listitem. */
	protected final Listitem newUnloadedItem(ListitemRenderer renderer, int index) {
		final ListModel model = _listbox.getModel();
		Listitem item = null;
		if (model instanceof GroupsListModel) {
			final GroupsListModel gmodel = (GroupsListModel) model;
			final GroupDataInfo info = gmodel.getDataInfo(index);
			switch(info.type){
			case GroupDataInfo.GROUP:
				item = newListgroup(renderer);
				((Listgroup)item).setOpen(!info.close);
				break;
			case GroupDataInfo.GROUPFOOT:
				item = newListgroupfoot(renderer);
				break;
			default:
				item = newListitem(renderer);
			}		
		}else{
			item = newListitem(renderer);
		}
		((LoadStatus)item.getExtraCtrl()).setLoaded(false);
		((LoadStatus)item.getExtraCtrl()).setIndex(index);

		newUnloadedCell(renderer, item);
		return item;
	}
	private Listitem newListitem(ListitemRenderer renderer) {
		Listitem item = null;
		if (renderer instanceof ListitemRendererExt)
			item = ((ListitemRendererExt)renderer).newListitem(_listbox);
		if (item == null) {
			item = new Listitem();
			item.applyProperties();
		}
		return item;
	}
	private Listgroup newListgroup(ListitemRenderer renderer) {
		Listgroup group = null;
		if (renderer instanceof ListgroupRendererExt)
			group = ((ListgroupRendererExt)renderer).newListgroup(_listbox);
		if (group == null) {
			group = new Listgroup();
			group.applyProperties();
		}
		return group;
	}
	private Listgroupfoot newListgroupfoot(ListitemRenderer renderer) {
		Listgroupfoot groupfoot = null;
		if (renderer instanceof ListgroupRendererExt)
			groupfoot = ((ListgroupRendererExt)renderer).newListgroupfoot(_listbox);
		if (groupfoot == null) {
			groupfoot = new Listgroupfoot();
			groupfoot.applyProperties();
		}
		return groupfoot;
	}
	private Listcell newUnloadedCell(ListitemRenderer renderer, Listitem item) {
		Listcell cell = null;
		if (renderer instanceof ListitemRendererExt)
			cell = ((ListitemRendererExt)renderer).newListcell(item);

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
		public void render(Listitem item, Object data) {
			item.setLabel(Objects.toString(data));
			item.setValue(data);
		}
	};
	
	public void syncModel(int offset, int limit) {
		_listbox.setAttribute(Listbox.SYNCING_MODEL, Boolean.TRUE);
		try {
			syncModel0(offset, limit);
		} finally {
			_listbox.setAttribute(Listbox.SYNCING_MODEL, null);
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

		int newcnt = newsz - oldsz;
		int atg = _pgi != null ? _listbox.getActivePage(): 0;
		ListitemRenderer renderer = null;
		Component next = null;		
		if (oldsz > 0) {
			if (min < 0) min = 0;
			else if (min > oldsz - 1) min = oldsz - 1;
			if (max < 0) max = oldsz - 1;
			else if (max > oldsz - 1) max = oldsz - 1;
			if (min > max) {
				int t = min; min = max; max = t;
			}

			int cnt = max - min + 1; //# of affected
			if (_model instanceof GroupsListModel) {
			//detach all from end to front since groupfoot
			//must be detached before group
				newcnt += cnt; //add affected later
				if (newcnt > 50 && !inPaging)
					_listbox.invalidate(); //performance is better

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
					} else if (((Listitem)item).isLoaded()) {
						if (renderer == null)
							renderer = (ListitemRenderer) getRealRenderer();
						item.detach(); //always detach
						_listbox.insertBefore(newUnloadedItem(renderer, min++), next);
						++addcnt;
					}

					item = next;//B2100338.,next item could be Paging, don't use Listitem directly
				}

				if ((addcnt > 50 || addcnt + newcnt > 50) && !inPagingMold())
					_listbox.invalidate(); //performance is better
			}
		} else {
			min = 0;
		}

		for (; --newcnt >= 0; ++min) {
			if (renderer == null)
				renderer = (ListitemRenderer)getRealRenderer();
			_listbox.insertBefore(newUnloadedItem(renderer, min), next);
		}
		if (_pgi != null) {
			if (atg >= _pgi.getPageCount())
				atg = _pgi.getPageCount() - 1;
			_pgi.setActivePage(atg);
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
		return _listbox != null &&
				inPagingMold()
				&& _listbox.getPageSize() <= getTotalSize();
				//Single page is considered as not a cropper.
				//isCropper is called after a component is removed, so
				//we have to test >= rather than >
	}
	
	public Set getAvailableAtClient() {
		if (!isCropper())
			return null;
		
		final Paginal pgi = _listbox.getPaginal();
		int pgsz = pgi.getPageSize();
		int ofs = pgi.getActivePage() * pgsz;
		return getAvailableAtClient(ofs, pgsz);
	}
	
	protected Set getAvailableAtClient(int offset, int limit) {
		if (!isCropper())
			return null;

		final Set avail = new LinkedHashSet(32);
		avail.addAll(_listbox.getHeads());
		final Listfoot listfoot = _listbox.getListfoot();
		if (listfoot != null) avail.add(listfoot);
		final Paging paging = _listbox.getPagingChild();
		if (paging != null) avail.add(paging);
		final Frozen frozen = _listbox.getFrozen();
		if (frozen != null) avail.add(frozen);

		int pgsz = limit;
		int ofs = offset;
		
		if (_listbox.getItemCount() > 0) {
			Component item = (Component) _listbox.getItems().get(0);
			while(item != null) {
				if (pgsz == 0) break;
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
							item = (Listitem) item.getNextSibling();
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
