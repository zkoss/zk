/* Box.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 20 21:51:32     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul;

import java.util.HashMap;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ObjectPropertyAccess;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * A box.
 *<p>Default {@link #getZclass}: z-vbox.(since 3.5.0)
 * @author tomyeh
 */
public class Box extends XulElement {
	private String _spacing;
	private String _align = "start", _pack = "start";
	/** Array of width/height for each cell. */
	private String[] _sizes;
	private boolean _sizedByContent = true;

	/** Default: vertical ({@link Vbox}).
	 */
	public Box() {
		this("vertical");
	}

	/**
	 * @param orient either "horizontal" or "vertical".
	 */
	public Box(String orient) {
		setOrient(orient);
	}

	/** Constructor a box by assigning an array of children.
	 *
	 * @param children an array of children to be added
	 * @since 2.4.0
	 */
	public Box(Component[] children) {
		this("vertical", children);
	}

	/** Constructor a box by assigning an array of children.
	 *
	 * @param children an array of children to be added
	 * @since 2.4.0
	 */
	public Box(String orient, Component[] children) {
		this(orient);

		if (children != null)
			for (int j = 0; j < children.length; ++j)
				appendChild(children[j]);
	}

	/** Returns whether it is a horizontal box.
	 * @since 3.0.0
	 */
	public boolean isHorizontal() {
		return "horizontal".equals(getOrient());
	}

	/** Returns whether it is a vertical box.
	 * @since 3.0.0
	 */
	public boolean isVertical() {
		return "vertical".equals(getOrient());
	}

	/** Returns the orient (the same as {@link #getMold}).
	 * <p>Default: "vertical".
	 */
	public String getOrient() {
		return getMold();
	}

	/** Sets the orient.
	 * @param orient either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException("orient cannot be " + orient);

		setMold(orient);
	}

	/** Returns the spacing between adjacent children, or null if the default
	 * spacing is used.
	 *
	 * <p>The default spacing depends on the definition of the style class
	 * called "xxx-sp", where xxx is
	 *
	 * <ol>
	 *  <li>{@link #getSclass} if it is not null.</li>
	 *  <li>hbox if {@link #getSclass} is null and it is a horizontal box.</li>
	 *  <li>vbox if {@link #getSclass} is null and it is a vertical box.</li>
	 * </ol>
	 *
	 * <p>Default: null (means to use the default spacing).
	 */
	public String getSpacing() {
		return _spacing;
	}

	/** Sets the spacing between adjacent children.
	 * @param spacing the spacing (such as "0", "5px", "3pt" or "1em").
	 * If null, empty ("") or "auto", the default spacing is used (i.e.,
	 * controlled by CSS alone)
	 * @see #getSpacing
	 */
	public void setSpacing(String spacing) {
		if (spacing != null && spacing.length() == 0)
			spacing = null;
		if (!Objects.equals(_spacing, spacing)) {
			_spacing = spacing;
			smartUpdate("spacing", _spacing);
		}
	}

	/** Returns the alignment of cells of a box in the 'opposite' direction
	 * (<i>null</i>, start, center, end).
	 *
	 * <p>Default: start</p>
	 *
	 * <p>The align attribute specifies how child elements of the box are aligned,
	 * when the size of the box is larger than the total size of the children. For
	 * boxes that have horizontal orientation, it specifies how its children will
	 * be aligned vertically. For boxes that have vertical orientation, it is used
	 * to specify how its children are aligned horizontally. The pack attribute
	 * ({@link #getPack}) is
	 * related to the alignment but is used to specify the position in the
	 * opposite direction.
	 *
	 * <dl>
	 * <dt>start</dt>
	 * <dd>Child elements are aligned starting from the left or top edge of
	 * the box. If the box is larger than the total size of the children, the
	 * extra space is placed on the right or bottom side.</dd>
	 * <dt>center</dt>
	 * <dd>Extra space is split equally along each side of the child
	 * elements, resulting in the children being placed in the center of the box.</dd>
	 * <dt>end</dt>
	 * <dd>Child elements are placed on the right or bottom edge of the box. If
	 * the box is larger than the total size of the children, the extra space is
	 * placed on the left or top side.</dd>
	 * <dt>stretch(since 5.0)</dt>
	 * <dd>Child elements are stretched to fill the box.</dd>
	 * </dl>
	 *
	 * @since 3.0.0
	 */
	public String getAlign() {
		return _align;
	}

	/** Sets the alignment of cells of this box in the 'opposite' direction
	 * (<i>start</i>, center, end, stretch).
	 *
	 * <p>Refer to {@link #getAlign} for more information
	 * 
	 * @param align the alignment in the 'opposite' direction.
	 * Allowed values: start, center, end, stretch.
	 * If empty or null, the browser's default is used
	 * (IE center and FF left, if vertical).
	 * @since 3.0.0
	 */
	public void setAlign(String align) {
		if (align != null && align.length() == 0)
			align = null;
		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdate("align", _align);
		}
	}

	/** Returns the pack alignment of cells of this box
	 * (start, center, end) plus an indication <i>stretch</i> option.
	 *
	 * <p>Default: start.
	 *
	 * <p>The pack attribute specifies where child elements of the box are placed
	 * when the box is larger that the size of the children. For boxes with
	 * horizontal orientation, it is used to indicate the position of children
	 * horizontally. For boxes with vertical orientation, it is used to indicate
	 * the position of children vertically. The align attribute 
	 * ({@link #getAlign})is used to specify
	 * the position in the opposite direction.
	 * 
	 * <dl>
	 * <dt>start</dt>
	 * <dd>Child elements are aligned starting from the left or top edge of
	 * the box. If the box is larger than the total size of the children, the
	 * Extra space is placed on the right or bottom side.</dd>
	 * <dt>center</dt>
	 * <dd>Extra space is split equally along each side of the child
	 * elements, resulting in the children being placed in the center of the box.</dd>
	 * <dt>end</dt>
	 * <dd>Child elements are placed on the right or bottom edge of the box. If
	 * the box is larger than the total size of the children, the extra space is
	 * placed on the left or top side.</dd>
	 * <dt>stretch(since 5.0)</dt>
	 * <dd>This is an extra option in addition to the (start, center, end) options.
	 * When add this extra option in the pack attribute, the Extra space is placed
	 * proportionally and evenly along each child elements. If you specify 
	 * "stretch,start", then the Extra proportionally and evenly allocated space 
	 * for each child is placed on the right or bottom side of the child. 
	 * If you specify "stretch,center", then the Extra proportionally and evenly 
	 * allocated space for each child is split equally along each side of the
	 * child. If you specify "stretch,end", then the Extra proportionally and 
	 * evenly allocated space for each child is placed on the left or top side of 
	 * the child. Note that if there are {@link Splitter} child inside this Box, 
	 * then this Box behaves as if the pack attribute has been set the "stretch"
	 * option; no matter you really specify "stretch" in pack attribute or not. 
	 * If you give null to the pack attribute, it is the same as "start". If simply 
	 * give "stretch" to this pack attribute then it is the same as "stretch,start"</dd> 
	 * </dl>
	 *
	 * @since 3.0.0
	 */
	public String getPack() {
		return _pack;
	}

	/** Sets the alignment of cells of this box
	 * (start, center, end) plus an <i>stretch</i> option.
	 *
	 * @param pack the alignment. Allowed values: (start, center, end) plus an 
	 * <i>stretch</i> option. If empty or null, it defaults to "start".
	 * @since 3.0.0
	 * @see #getPack()
	 */
	public void setPack(String pack) {
		if (pack != null && pack.length() == 0)
			pack = null;
		if (!Objects.equals(_pack, pack)) {
			_pack = pack;
			smartUpdate("pack", _pack);
		}
	}

	/**
	 * Sets whether sizing the cell's size by its content.
	 * <p>Default: true. It means the cell's size is depended on its content.
	 * 
	 * <p> With {@link Splitter}, you can specify the sizedByContent to be false
	 * for resizing smoothly, if it contains a grid or other sophisticated
	 * components.
	 * @param byContent 
	 * @since 5.0.4
	 */
	public void setSizedByContent(boolean byContent) {
		if (_sizedByContent != byContent) {
			_sizedByContent = byContent;
			smartUpdate("sizedByContent", byContent);
		}
	}

	/**
	 * Returns whether sizing the cell's size by its content.
	 * <p>Default: true.
	 * @since 5.0.4
	 * @see #setSizedByContent
	 */
	public boolean isSizedByContent() {
		return _sizedByContent;
	}

	@Override
	public boolean evalCSSFlex() {
		return false;
	}

	//-- super --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "spacing", _spacing);
		render(renderer, "_sizes", _sizes);

		if (!"start".equals(_align))
			render(renderer, "align", _align);
		if (!"start".equals(_pack))
			render(renderer, "pack", _pack);
		if (!isSizedByContent())
			renderer.render("sizedByContent", false);
	}

	public String getZclass() {
		return _zclass == null ? isVertical() ? "z-vbox" : "z-hbox" : _zclass;
	}

	private static HashMap<String, PropertyAccess> _properties = new HashMap<String, PropertyAccess>(1);

	static {
		_properties.put("_sizes", new ObjectPropertyAccess() {
			public void setValue(Component cmp, Object sizes) {
				if (sizes instanceof String[]) {
					((Box) cmp)._sizes = (String[]) sizes;
				} else if (sizes instanceof String) {
					((Box) cmp)._sizes = Utils.stringToArray((String) sizes, null);
				} else if (sizes == null) {
					((Box) cmp)._sizes = null;
				}
			}

			public Object getValue(Component cmp) {
				return ((Box) cmp)._sizes;
			}
		});
	}

	public PropertyAccess getPropertyAccess(String prop) {
		PropertyAccess pa = _properties.get(prop);
		if (pa != null)
			return pa;
		return super.getPropertyAccess(prop);
	}
}
