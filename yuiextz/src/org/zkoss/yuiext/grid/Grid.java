/* Grid.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Jun 22, 2007 12:00:07 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.yuiext.grid;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Objects;
import org.zkoss.util.logging.Log;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.RenderOnDemand;
import org.zkoss.zk.ui.ext.client.Selectable;
import org.zkoss.zk.ui.ext.render.ChildChangedAware;
import org.zkoss.yuiext.ColumnMovedCommand;
import org.zkoss.yuiext.grid.Columns;
import org.zkoss.yuiext.grid.Row;
import org.zkoss.yuiext.grid.Rows;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.RendererCtrl;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.impl.XulElement;

/**
 * This class represents the primary interface of an implemented
 * <code>Ext JS</code> component based grid control. This component support
 * two mold <i>editable grid</i> and <i>un-editable grid</i>. To use
 * <i>editable grid</i> is unsupported the feature of <i>trackMouseOver</i>
 * and <i>lockable column</i>.
 * 
 * <p>
 * Events:<br/> onColumnMoved, onSelect.
 * 
 * @author jumperchen
 * @see ListModel
 * @see RowRenderer
 * @see RowRendererExt
 * 
 */
public class Grid extends XulElement {
	private static final Log log = Log.lookup(Grid.class);

	private transient Rows _rows;

	private transient Columns _cols;

	private boolean _trackMouseOver = true;

	private boolean _multiple = true;

	private boolean _editable;

	private boolean _columnmove;

	private boolean _columnhide;

	private boolean _dragdrop;

	private boolean _columnlock;

	private String _type = "tableGrid";

	private ListModel _model;

	private RowRenderer _renderer;

	private transient ListDataListener _dataListener;

	/** A list of selected items. */
	private transient Set _selRows;

	/** A readonly copy of {@link #_selRows}. */
	private transient Set _roSelRows;

	/** disable smartUpdate; usually caused by the client. */
	private boolean _noSmartUpdate;

	/** the # of rows to preload. */
	private int _preloadsz = 7;

	private int _jsel = -1;
	
	// control variable
	private boolean _smartVerify;

	private transient EventListener _smartVerifyListener;

	public Grid() {
		init();
	}

	private void init() {
		_selRows = new LinkedHashSet(5);
		_roSelRows = Collections.unmodifiableSet(_selRows);
	}

	/**
	 * Returns whether this grid enable lockable column.
	 */
	public boolean isColumnlock() {
		return _columnlock;
	}

	/**
	 * Sets whether this grid enable lockable column.
	 */
	public void setColumnlock(boolean columnlock) {
		if (columnlock && isEditable())
			throw new UiException("Appliable only to the uneditable type: "
					+ this);
		if (_columnlock != columnlock) {
			_columnlock = columnlock;
			invalidate();
		}
	}

	/**
	 * Returns whether this grid enable drag and drop reorder of columns.
	 * <p>
	 * Default: false.
	 */
	public boolean isColumnmove() {
		return _columnmove;
	}

	/**
	 * Sets whether this grid enable drag and drop reorder of columns.
	 */
	public void setColumnmove(boolean columnmove) {
		if (columnmove && isDragdrop())
			throw new UiException("Appliable only to the undrag-drop type: "
					+ this);
		if (_columnmove != columnmove) {
			_columnmove = columnmove;
			invalidate();
			smartVerify();
		}
	}

	/**
	 * Returns whether this grid enable hiding of columns with the header
	 * context menu.
	 */
	public boolean isColumnhide() {
		return _columnhide;
	}

	/**
	 * Sets whether this grid enable hiding of columns with the header context
	 * menu.
	 * <p>
	 * Default: false.
	 */
	public void setColumnhide(boolean columnhide) {
		if (_columnhide != columnhide) {
			_columnhide = columnhide;
			invalidate();
		}
	}

	/**
	 * Returns whether this grid enable drag and drop of rows.
	 */
	public boolean isDragdrop() {
		return _dragdrop;
	}

	/**
	 * Sets whether this grid enable drag and drop of rows.
	 */
	public void setDragdrop(boolean dragdrop) {
		if (dragdrop && isColumnmove())
			throw new UiException("Appliable only to the uncolumnmove type: "
					+ this);
		if (_dragdrop != dragdrop) {
			_dragdrop = dragdrop;
			invalidate();
		}
	}

	/**
	 * Returns whether this grid is editable.
	 */
	public boolean isEditable() {
		return _editable;
	}

	/**
	 * Sets whether this grid is editable.
	 */
	public void setEditable(boolean editable) {
		if (editable && isColumnlock())
			throw new UiException("Appliable only to the unlockable type: "
					+ this);
		if (_editable != editable) {
			_editable = editable;
			if (_editable) {
				_trackMouseOver = false;
				_type = "editorGrid";
			} else {
				_type = "tableGrid";
			}
			invalidate();
		}
	}

	/**
	 * Returns the grid type.
	 * <p>
	 * Default: 'tableGrid'
	 */
	public String getType() {
		return _type;
	}

	/**
	 * Returns whether multiple selection is allowed.
	 */
	public boolean isMultiple() {
		return _multiple;
	}

	/**
	 * Sets whether multiple selection is allowed.
	 */
	public void setMultiple(boolean multiple) {
		if (_multiple != multiple) {
			_multiple = multiple;
			smartUpdate("z.multiple", _multiple);
		}
	}

	/**
	 * Returns whether to highlight rows when the mouse is over
	 */
	public boolean isTrackMouseOver() {
		return _trackMouseOver;
	}

	/**
	 * Sets whether to highlight rows when the mouse is over.
	 * <p>
	 * Default: true.
	 */
	public void setTrackMouseOver(boolean trackMouseOver) {
		if (isEditable() && trackMouseOver)
			throw new UiException("Appliable only to the uneditable type: "
					+ this);
		if (_trackMouseOver != trackMouseOver) {
			_trackMouseOver = trackMouseOver;
			smartUpdate("z.trackMouseOver", String.valueOf(trackMouseOver));
		}
	}

	/**
	 * Returns the rows.
	 */
	public Rows getRows() {
		return _rows;
	}

	/**
	 * Returns the columns.
	 */
	public Columns getColumns() {
		return _cols;
	}

	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super
				.getOuterAttrs());
		appendAsapAttr(sb, "onColumnMoved");
		appendAsapAttr(sb, Events.ON_SELECT);
		HTMLs.appendAttribute(sb, "z.multiple", String.valueOf(_multiple));
		HTMLs.appendAttribute(sb, "z.trackMouseOver", String
				.valueOf(_trackMouseOver));
		HTMLs.appendAttribute(sb, "z.selId", getSelectedId());
		HTMLs.appendAttribute(sb, "z.gridType", getType());
		HTMLs.appendAttribute(sb, "z.columnmove", isColumnmove());
		HTMLs.appendAttribute(sb, "z.columnhide", isColumnhide());
		HTMLs.appendAttribute(sb, "z.dragdrop", isDragdrop());
		HTMLs.appendAttribute(sb, "z.columnlock", isColumnlock());
		if (_model != null)
			HTMLs.appendAttribute(sb, "z.model", true);
		return sb.toString();
	}

	/**
	 * Returns the specified cell, or null if not available.
	 * 
	 * @param row
	 *            which row to fetch (starting at 0).
	 * @param col
	 *            which column to fetch (starting at 0).
	 */
	public Component getCell(int row, int col) {
		final Rows rows = getRows();
		if (rows == null)
			return null;

		List children = rows.getChildren();
		if (children.size() <= row)
			return null;

		children = ((Row) children.get(row)).getChildren();
		return children.size() <= col ? null : (Component) children.get(col);
	}

	// -- Component --//
	public boolean insertBefore(Component newChild, Component refChild) {
		if (newChild instanceof Rows) {
			if (_rows != null && _rows != newChild)
				throw new UiException("Only one rows child is allowed: " + this
						+ "\nNote: rows is created automatically if live data");
			_rows = (Rows) newChild;
		} else if (newChild instanceof Columns) {
			if (_cols != null && _cols != newChild)
				throw new UiException("Only one columns child is allowed: "
						+ this);
			_cols = (Columns) newChild;
		} else {
			throw new UiException("Unsupported child for grid: " + newChild);
		}

		if (super.insertBefore(newChild, refChild)) {
			invalidate();
			return true;
		}
		return false;
	}

	public boolean removeChild(Component child) {
		if (!super.removeChild(child))
			return false;

		if (_rows == child)
			_rows = null;
		else if (_cols == child)
			_cols = null;
		invalidate();
		return true;
	}

	/**
	 * Returns the index of the selected row (-1 if no one is selected).
	 */
	public int getSelectedIndex() {
		return _jsel;
	}

	/**
	 * If the specified row is selected, it is deselected. If it is not
	 * selected, it is selected. Other rows in the grid that are selected are
	 * not affected, and retain their selected state.
	 */
	public void toggleItemSelection(Row row) {
		if (row.isSelected())
			removeItemFromSelection(row);
		else addItemToSelection(row);
	}

	/**
	 * Deselects all of the currently selected rows and selects the row with the
	 * given index.
	 */
	public void setSelectedIndex(int jsel) {
		if (jsel < -1)
			jsel = -1;
		if (jsel < 0) { // unselct all
			clearSelection();
		} else if (jsel != _jsel || (_multiple && _selRows.size() > 1)) {
			for (Iterator it = _selRows.iterator(); it.hasNext();) {
				final Row row = (Row) it.next();
				row.setSelectedDirectly(false);
			}
			_selRows.clear();

			_jsel = jsel;
			final Row row = (Row) getRows().getChildren().get(_jsel);
			row.setSelectedDirectly(true);
			_selRows.add(row);
			smartUpdate("select", row.getUuid());
		}
	}

	/**
	 * Selects the given row, without deselecting any other rows that are
	 * already selected..
	 */
	public void addItemToSelection(Row row) {
		if (row.getRows().getParent() != this)
			throw new UiException("Not a child: " + row);

		if (!row.isSelected()) {
			if (!_multiple) {
				selectItem(row);
			} else {
				final int index = getRows().getChildren().indexOf(row);
				if (index < _jsel || _jsel < 0) {
					_jsel = index;
				}
				row.setSelectedDirectly(true);
				_selRows.add(row);
				smartUpdateSelection();
			}
		}

	}

	/**
	 * Note: we have to update all selection at once, since addItemToSelection
	 * and removeItemFromSelection might be called interchangeably.
	 */
	private void smartUpdateSelection() {
		final StringBuffer sb = new StringBuffer(80);
		for (Iterator it = _selRows.iterator(); it.hasNext();) {
			if (sb.length() > 0)
				sb.append(',');
			sb.append(((Row) it.next()).getUuid());
		}
		smartUpdate("chgSel", sb.toString());
	}

	/**
	 * Fix the selected index, _jsel, assuming there are no selected one before
	 * (and excludes) j-the row.
	 */
	private void fixSelectedIndex(int j) {
		if (!_selRows.isEmpty()) {
			for (Iterator it = getRows().getChildren().listIterator(j); it
					.hasNext(); ++j) {
				final Row row = (Row) it.next();
				if (row.isSelected()) {
					_jsel = j;
					return;
				}
			}
		}
		_jsel = -1;
	}

	/**
	 * Deselects the given row without deselecting other rows.
	 */
	public void removeItemFromSelection(Row row) {
		if (row.getRows().getParent() != this)
			throw new UiException("Not a child: " + row);

		if (row.isSelected()) {
			if (!_multiple) {
				clearSelection();
			} else {
				final int oldSel = _jsel;
				row.setSelectedDirectly(false);
				_selRows.remove(row);
				fixSelectedIndex(0);
				smartUpdateSelection();
				if (oldSel != _jsel)
					smartUpdate("z.selId", getSelectedId());

			}
		}
	}

	/**
	 * Returns the ID of the selected row (it is stored as the z.selId attribute
	 * of the grid).
	 */
	private String getSelectedId() {
		final Row sel = getSelectedItem();
		return sel != null ? sel.getUuid() : "zk_n_a";
	}

	/**
	 * Deselects all of the currently selected rows and selects the given row.
	 * <p>
	 * It is the same as {@link #setSelectedItem}.
	 * 
	 * @param row
	 *            the row to select. If null, all rows are deselected.
	 */
	public void selectItem(Row row) {
		if (row == null) {
			setSelectedIndex(-1);
		} else {
			if (row.getRows().getParent() != this)
				throw new UiException("Not a child: " + row);
			if (_multiple || !row.isSelected())
				setSelectedIndex(getRows().getChildren().indexOf(row));
		}
	}

	/**
	 * Clears the selection.
	 */
	public void clearSelection() {
		if (!_selRows.isEmpty()) {
			for (Iterator it = _selRows.iterator(); it.hasNext();) {
				final Row row = (Row) it.next();
				row.setSelectedDirectly(false);
			}
			_selRows.clear();
			_jsel = -1;
			smartUpdate("select", "");
		}
	}

	/**
	 * Selects all rows.
	 */
	public void selectAll() {
		if (!_multiple)
			throw new UiException("Appliable only to the multiple seltype: "
					+ this);

		for (Iterator it = getRows().getChildren().iterator(); it.hasNext();) {
			final Row row = (Row) it.next();
			_selRows.add(row);
			row.setSelectedDirectly(true);
		}
		_jsel = getRows().getChildren().isEmpty() ? -1 : 0;
		smartUpdate("selectAll", "true");
	}

	/**
	 * Returns the selected row.
	 */
	public Row getSelectedItem() {
		return _jsel >= 0 ? _jsel > 0 && _selRows.size() == 1 ? (Row) _selRows
				.iterator().next() : (Row) getRows().getChildren().get(_jsel)
				: null;
	}

	/**
	 * Deselects all of the currently selected rows and selects the given row.
	 * <p>
	 * It is the same as {@link #selectItem}.
	 */
	public void setSelectedItem(Row row) {
		selectItem(row);
	}

	/**
	 * Returns all selected rows.
	 */
	public Set getSelectedItems() {
		return _roSelRows;
	}

	/**
	 * Returns the number of rows being selected.
	 */
	public int getSelectedCount() {
		return _selRows.size();
	}

	// -- ListModel dependent codes --//
	/**
	 * Returns the list model associated with this grid, or null if this grid is
	 * not associated with any list data model.
	 */
	public ListModel getModel() {
		return _model;
	}

	/**
	 * Sets the list model associated with this grid. If a non-null model is
	 * assigned, no matter whether it is the same as the previous, it will
	 * always cause re-render.
	 * 
	 * @param model
	 *            the list model to associate, or null to dis-associate any
	 *            previous model.
	 * @exception UiException
	 *                if failed to initialize with the model
	 */
	public void setModel(ListModel model) {
		if (model != null) {
			if (_model != model) {
				if (_model != null) {
					_model.removeListDataListener(_dataListener);
				} else {
					smartUpdate("z.model", "true");
				}

				initDataListener();
				_model = model;
				_model.addListDataListener(_dataListener);
			}

			// Always syncModel because it is easier for user to enfore reload
			syncModel(-1, -1); // create rows if necessary
			Events.postEvent("onInitRender", this, null);
			// Since user might setModel and setRender separately or repeatedly,
			// we don't handle it right now until the event processing phase
			// such that we won't render the same set of data twice
			// --
			// For better performance, we shall load the first few row now
			// (to save a roundtrip)
			smartVerify();
		} else if (_model != null) {
			_model.removeListDataListener(_dataListener);
			_model = null;
			if (_rows != null)
				_rows.getChildren().clear();
			smartUpdate("z.model", null);
			smartVerify();
		}
	}

	private void initDataListener() {
		if (_dataListener == null)
			_dataListener = new ListDataListener() {
				public void onChange(ListDataEvent event) {
					onListDataChange(event);
				}
			};
	}

	/**
	 * Returns the renderer to render each row, or null if the default renderer
	 * is used.
	 */
	public RowRenderer getRowRenderer() {
		return _renderer;
	}

	/**
	 * Sets the renderer which is used to render each row if {@link #getModel}
	 * is not null.
	 * 
	 * <p>
	 * Note: changing a render will not cause the grid to re-render. If you want
	 * it to re-render, you could assign the same model again (i.e.,
	 * setModel(getModel())), or fire an {@link ListDataEvent} event.
	 * 
	 * @param renderer
	 *            the renderer, or null to use the default.
	 * @exception UiException
	 *                if failed to initialize with the model
	 */
	public void setRowRenderer(RowRenderer renderer) {
		_renderer = renderer;
	}

	/**
	 * Sets the renderer by use of a class name. It creates an instance
	 * automatically.
	 */
	public void setRowRenderer(String clsnm) throws ClassNotFoundException,
			NoSuchMethodException, InstantiationException,
			java.lang.reflect.InvocationTargetException {
		if (clsnm != null)
			setRowRenderer((RowRenderer) Classes.newInstanceByThread(clsnm));
	}

	/**
	 * Returns the number of rows to preload when receiving the rendering
	 * request from the client.
	 * 
	 * <p>
	 * Default: 7.
	 * 
	 * <p>
	 * It is used only if live data ({@link #setModel} and not paging ({@link #getPaging}.
	 * 
	 */
	public int getPreloadSize() {
		return _preloadsz;
	}

	/**
	 * Sets the number of rows to preload when receiving the rendering request
	 * from the client.
	 * <p>
	 * It is used only if live data ({@link #setModel} and not paging ({@link #getPaging}.
	 * 
	 * @param sz
	 *            the number of rows to preload. If zero, no preload at all.
	 * @exception UiException
	 *                if sz is negative
	 */
	public void setPreloadSize(int sz) {
		if (sz < 0)
			throw new UiException("nonnegative is required: " + sz);
		_preloadsz = sz;
	}

	/**
	 * Synchronizes the grid to be consistent with the specified model.
	 * 
	 * @param min
	 *            the lower index that a range of invalidated rows
	 * @param max
	 *            the higher index that a range of invalidated rows
	 */
	private void syncModel(int min, int max) {
		RowRenderer renderer = null;
		final int newsz = _model.getSize();
		final int oldsz = _rows != null ? _rows.getChildren().size() : 0;
		if (oldsz > 0) {
			if (newsz > 0 && min < oldsz) {
				if (max < 0 || max >= oldsz)
					max = oldsz - 1;
				if (max >= newsz)
					max = newsz - 1;
				if (min < 0)
					min = 0;

				for (Iterator it = _rows.getChildren().listIterator(min); min <= max
						&& it.hasNext(); ++min) {
					final Row row = (Row) it.next();
					if (row.isLoaded()) {
						if (renderer == null)
							renderer = getRealRenderer();
						unloadRow(renderer, row);
					}
				}
			}

			// detach and remove
			if (oldsz > newsz) {
				for (Iterator it = _rows.getChildren().listIterator(newsz); it
						.hasNext();) {
					it.next();
					it.remove();
				}
			}
		}

		// auto create but it means <grid model="xx"><rows/>... will fail
		if (_rows == null)
			new Rows().setParent(this);

		for (int j = oldsz; j < newsz; ++j) {
			if (renderer == null)
				renderer = getRealRenderer();
			newUnloadedRow(renderer).setParent(_rows);
		}
	}

	/** Creates an new and unloaded row. */
	private final Row newUnloadedRow(RowRenderer renderer) {
		Row row = null;
		if (renderer instanceof RowRendererExt)
			row = ((RowRendererExt) renderer).newRow(this);

		if (row == null) {
			row = new Row();
			row.applyProperties();
		}
		row.setLoaded(false);

		newUnloadedCell(renderer, row);
		return row;
	}

	private Component newUnloadedCell(RowRenderer renderer, Row row) {
		Component cell = null;
		if (renderer instanceof RowRendererExt)
			cell = ((RowRendererExt) renderer).newCell(row);

		if (cell == null) {
			cell = newRenderLabel(null);
			cell.applyProperties();
		}
		cell.setParent(row);
		return cell;
	}

	/**
	 * Returns the label for the cell generated by the default renderer.
	 */
	private static Label newRenderLabel(String value) {
		final Label label = new Label(
				value != null && value.length() > 0 ? value : " ");
		label.setPre(true); // to make sure &nbsp; is generated, and then
		// occupies some space
		return label;
	}

	/** Clears a row as if it is not loaded. */
	private final void unloadRow(RowRenderer renderer, Row row) {
		if (!(renderer instanceof RowRendererExt)
				|| (((RowRendererExt) renderer).getControls() & RowRendererExt.DETACH_ON_UNLOAD) == 0) { // re-use
			// (default)
			final List cells = row.getChildren();
			boolean bNewCell = cells.isEmpty();
			if (!bNewCell) {
				// detach and remove all but the first cell
				for (Iterator it = cells.listIterator(1); it.hasNext();) {
					it.next();
					it.remove();
				}

				final Component cell = (Component) cells.get(0);
				bNewCell = !(cell instanceof Label);
				if (bNewCell) {
					cell.detach();
				} else {
					((Label) cell).setValue("");
				}
			}

			if (bNewCell)
				newUnloadedCell(renderer, row);
			row.setLoaded(false);
		} else { // detach
			_rows.insertBefore(newUnloadedRow(renderer), row);
			row.detach();
		}
	}

	/**
	 * Handles a private event, onInitRender. It is used only for
	 * implementation, and you rarely need to invoke it explicitly.
	 */
	public void onInitRender() {
		final Renderer renderer = new Renderer();
		try {
			final int pgsz = 20;
			// we don't know # of visible rows, so a 'smart' guess
			// It is OK since client will send back request if not enough
			int j = 0;
			for (Iterator it = _rows.getChildren().iterator(); j < pgsz
					&& it.hasNext(); ++j)
				renderer.render((Row) it.next());
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	/**
	 * Handles when the list model's content changed.
	 */
	private void onListDataChange(ListDataEvent event) {
		// when this is called _model is never null
		final int newsz = _model.getSize(), oldsz = _rows.getChildren().size();
		int min = event.getIndex0(), max = event.getIndex1();
		if (min < 0)
			min = 0;

		boolean done = false;
		switch (event.getType()) {
		case ListDataEvent.INTERVAL_ADDED:
			if (max < 0)
				max = newsz - 1;
			if ((max - min + 1) != (newsz - oldsz)) {
				log
						.warning("Conflict event: number of added rows not matched: "
								+ event);
				break; // handle it as CONTENTS_CHANGED
			}

			RowRenderer renderer = null;
			final Row before = min < oldsz ? (Row) _rows.getChildren().get(min)
					: null;
			for (int j = min; j <= max; ++j) {
				if (renderer == null)
					renderer = getRealRenderer();
				_rows.insertBefore(newUnloadedRow(renderer), before);
			}

			done = true;
			break;

		case ListDataEvent.INTERVAL_REMOVED:
			if (max < 0)
				max = oldsz - 1;
			int cnt = max - min + 1;
			if (cnt != (oldsz - newsz)) {
				log
						.warning("Conflict event: number of removed rows not matched: "
								+ event);
				break; // handle it as CONTENTS_CHANGED
			}

			// detach and remove
			for (Iterator it = _rows.getChildren().listIterator(min); --cnt >= 0
					&& it.hasNext();) {
				it.next();
				it.remove();
			}

			done = true;
			break;
		}

		if (!done) // CONTENTS_CHANGED
			syncModel(min, max);

		initAtClient();
		// client have to send back for what have to reload
	}

	private static final RowRenderer getDefaultRowRenderer() {
		return _defRend;
	}

	private static final RowRenderer _defRend = new RowRenderer() {
		public void render(Row row, Object data) {
			final Label label = newRenderLabel(Objects.toString(data));
			label.applyProperties();
			label.setParent(row);
			row.setValue(data);
		}
	};

	/**
	 * Returns the renderer used to render rows.
	 */
	private RowRenderer getRealRenderer() {
		return _renderer != null ? _renderer : getDefaultRowRenderer();
	}

	/** Used to render row if _model is specified. */
	private class Renderer implements java.io.Serializable {
		private final RowRenderer _renderer;

		private boolean _rendered, _ctrled;

		private Renderer() {
			_renderer = getRealRenderer();
		}

		private void render(Row row) throws Throwable {
			if (row.isLoaded())
				return; // nothing to do

			if (!_rendered && (_renderer instanceof RendererCtrl)) {
				((RendererCtrl) _renderer).doTry();
				_ctrled = true;
			}

			final Component cell = (Component) row.getChildren().get(0);
			if (!(_renderer instanceof RowRendererExt)
					|| (((RowRendererExt) _renderer).getControls() & RowRendererExt.DETACH_ON_RENDER) != 0) { // detach
				// (default)
				cell.detach();
			}

			try {
				_renderer.render(row, _model.getElementAt(row.getIndex()));
			} catch (Throwable ex) {
				try {
					final Label label = newRenderLabel(Exceptions
							.getMessage(ex));
					label.applyProperties();
					label.setParent(row);
				} catch (Throwable t) {
					log.error(t);
				}
				row.setLoaded(true);
				throw ex;
			} finally {
				if (row.getChildren().isEmpty())
					cell.setParent(row);
			}

			row.setLoaded(true);
			_rendered = true;
		}

		private void doCatch(Throwable ex) {
			if (_ctrled) {
				try {
					((RendererCtrl) _renderer).doCatch(ex);
				} catch (Throwable t) {
					throw UiException.Aide.wrap(t);
				}
			} else {
				throw UiException.Aide.wrap(ex);
			}
		}

		private void doFinally() {
			if (_rendered)
				initAtClient();
			// reason: after rendering, the column width might change
			// Also: Mozilla remembers scrollTop when user's pressing
			// RELOAD, it makes init more desirable.
			if (_ctrled)
				((RendererCtrl) _renderer).doFinally();
		}
	}

	/**
	 * Renders the specified {@link Row} if not loaded yet, with
	 * {@link #getRowRenderer}.
	 * 
	 * <p>
	 * It does nothing if {@link #getModel} returns null. In other words, it is
	 * meaningful only if live data model is used.
	 */
	public void renderRow(Row row) {
		if (_model == null)
			return;

		final Renderer renderer = new Renderer();
		try {
			renderer.render(row);
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	/**
	 * Renders all {@link Row} if not loaded yet, with {@link #getRowRenderer}.
	 */
	public void renderAll() {
		if (_model == null)
			return;

		final Renderer renderer = new Renderer();
		try {
			for (Iterator it = _rows.getChildren().iterator(); it.hasNext();)
				renderer.render((Row) it.next());
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	/**
	 * Renders a set of specified rows. It is the same as {@link #renderItems}.
	 */
	public void renderRows(Set rows) {
		renderItems(rows);
	}

	public void renderItems(Set rows) {
		if (_model == null) { // just in case that app dev might change it
			if (log.debugable())
				log.debug("No model no render");
			return;
		}

		if (rows.isEmpty())
			return; // nothing to do

		final Renderer renderer = new Renderer();
		try {
			for (Iterator it = rows.iterator(); it.hasNext();)
				renderer.render((Row) it.next());
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
	}

	// -- Component --//
	public void smartUpdate(String attr, String value) {
		if (!_noSmartUpdate)
			super.smartUpdate(attr, value);
	}

	// Cloneable//
	public Object clone() {
		final Grid clone = (Grid) super.clone();

		int cnt = 0;
		if (clone._rows != null)
			++cnt;
		if (clone._cols != null)
			++cnt;
		if (cnt > 0)
			clone.afterUnmarshal(cnt);

		return clone;
	}

	/**
	 * @param cnt #
	 *            of children that need special handling (used for
	 *            optimization). -1 means process all of them
	 */
	private void afterUnmarshal(int cnt) {
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Rows) {
				_rows = (Rows) child;
				if (--cnt == 0)
					break;
			} else if (child instanceof Columns) {
				_cols = (Columns) child;
				if (--cnt == 0)
					break;
			}
		}
	}
	protected void smartVerify() {
		if (_smartVerify)
			return; // already mark smart
		_smartVerify = true;
		if (_smartVerifyListener == null) {
			_smartVerifyListener = new EventListener() {
				public void onEvent(Event event) {
					if (isColumnmove() && getModel() != null) {
						throw new UiException(
								"When the columnmove is true, the model must be null!");
					}
					_smartVerify = false;
				}
			};
			addEventListener("onSmartVerify", _smartVerifyListener);
		}
		Events.postEvent("onSmartVerify", this, null);
	}

	// -- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}

	/**
	 * A utility class to implement {@link #getExtraCtrl}. It is used only by
	 * component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements
			ChildChangedAware, Selectable, RenderOnDemand {
		// ChildChangedAware//
		public boolean isChildChangedAware() {
			return true;
		}

		// -- Selectable --//
		public void selectItemsByClient(Set selItems) {
			_noSmartUpdate = true;
			try {
				if (!_multiple || ((selItems == null || selItems.size() <= 1))) {
					final Row row = selItems != null && selItems.size() > 0 ? (Row) selItems
							.iterator().next()
							: null;
					selectItem(row);
				} else {
					int j = 0;
					for (Iterator it = getRows().getChildren().iterator(); it
							.hasNext(); ++j) {
						final Row row = (Row) it.next();
						if (selItems.contains(row)) {
							addItemToSelection(row);
						} else {
							removeItemFromSelection(row);
						}
					}
				}
			} finally {
				_noSmartUpdate = false;
			}
		}

		// RenderOnDemand//
		public void renderItems(Set items) {
			int cnt = items.size();
			if (cnt == 0)
				return; // nothing to do
			cnt = 20 - cnt;
			if (cnt > 0 && _preloadsz > 0) { // Feature 1740072: pre-load
				if (cnt > _preloadsz)
					cnt = _preloadsz;

				// 1. locate the first item found in items
				final List toload = new LinkedList();
				Iterator it = getRows().getChildren().iterator();
				while (it.hasNext()) {
					final Row row = (Row) it.next();
					if (items.contains(row)) // found
						break;
					if (!row.isLoaded())
						toload.add(0, row); // reverse order
				}

				// 2. add unload items before the found one
				if (!toload.isEmpty()) {
					int bfcnt = cnt / 3;
					for (Iterator e = toload.iterator(); bfcnt > 0
							&& e.hasNext(); --bfcnt, --cnt) {
						items.add(e.next());
					}
				}

				// 3. add unloaded after the found one
				while (cnt > 0 && it.hasNext()) {
					final Row row = (Row) it.next();
					if (!row.isLoaded() && items.add(row))
						--cnt;
				}
			}

			Grid.this.renderItems(items);
		}
	}

	public void redraw(Writer out) throws IOException {
		if (getColumns() == null)
			throw new UiException(
					"The Columns component is necessay for grid according to the specification of Ext JS!");
		super.redraw(out);
	}

	/**
	 * Re-initialize the grid at the client (actually, re-calculate the column
	 * width at the client).
	 */
	/* package */void initAtClient() {
		smartUpdate("z.init", true);
	}

	static {
		new ColumnMovedCommand("onColumnMoved", 0);
	}
}
