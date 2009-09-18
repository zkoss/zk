/* Box.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 20 21:51:32     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul;

import java.util.Iterator;
import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ComponentCtrl;

import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.impl.Utils;

/**
 * A box.
 *<p>Default {@link #getZclass}: z-vbox.(since 3.5.0)
 * @author tomyeh
 */
public class Box extends XulElement implements org.zkoss.zul.api.Box {
	private String _spacing;
	private String _align = "start", _pack = "stretch";
	/** Array of width/height for each cell. */
	private String[] _sizes;

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
			throw new WrongValueException("orient cannot be "+orient);

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
	 * @param spacing the spacing (such as "0", "5px", "3pt" or "1em"),
	 * or null to use the default spacing
	 * @see #getSpacing
	 */
	public void setSpacing(String spacing) {
		if (spacing != null && spacing.length() == 0) spacing = null;
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
	 * @param align the alignment in the 'opposite' direction.
	 * Allowed values: start, center, end, stretch.
	 * If empty or null, the browser's default is used
	 * (IE center and FF left, if vertical).
	 * @since 3.0.0
	 */
	public void setAlign(String align) {
		if (align != null && align.length() == 0) align = null;
		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdate("align", _align);
		}
	}
	/** Returns the pack alignment of cells of this box
	 * (start, center, end) plus an indication <i>stretch</i> option.
	 *
	 * <p>Default: stretch, start.
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
	 * If given null or simply "stretch" to this pack attribute then it is the 
	 * same as "stretch,start"</dd> 
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
	 * <i>stretch</i> option. If empty or null, it defaults to "stretch,start".
	 * @since 3.0.0
	 * @see #getPack()
	 */
	public void setPack(String pack) {
		if (pack != null && pack.length() == 0) pack = null;
		if (!Objects.equals(_pack, pack)) {
			_pack = pack;
			smartUpdate("pack", _pack);
		}
	}

	/** Returns the widths/heights, which is a list of numbers separated by comma
	 * to denote the width/height of each cell in a box.
	 * If {@link Hbox} (i.e., {@link #getOrient} is horizontal),
	 * it is a list of widths.
	 * If {@link Vbox} (i.e., {@link #getOrient} is vertical),
	 * it is a list of heights.
	 *
	 * <p>It is the same as {@link #getHeights}.
	 *
	 * <p>Default: empty.
	 * 
	 * @deprecated As of release 5.0.0, use {@link Cell} instead.
	 */
	public String getWidths() {
		return Utils.arrayToString(_sizes);
	}
	/** Returns the heights/widths, which is a list of numbers separated by comma
	 * to denote the height/width of each cell in a box.
	 * If {@link Hbox} (i.e., {@link #getOrient} is horizontal),
	 * it is a list of widths.
	 * If {@link Vbox} (i.e., {@link #getOrient} is vertical),
	 * it is a list of heights.
	 *
	 * <p>It is the same as {@link #getWidths}.
	 *
	 * <p>Default: empty.
	 * @deprecated As of release 5.0.0, use {@link Cell} instead.
	 */
	public String getHeights() {
		return getWidths();
	}
	/** Sets the widths/heights, which is a list of numbers separated
	 * by comma to denote the width/height of each cell in a box.
	 *
	 * <p>It is the same as {@link #setHeights}.
	 *
	 * <p>For example, "10%,20%,30%" means the second cell shall
	 * occupy 10% width, the second cell 20%, the third cell 30%,
	 * and the following cells don't specify any width.
	 *
	 * <p>Note: the splitters are ignored, i.e., they are not cells.
	 *
	 * <p>Another example, ",,30%" means the third cell shall occupy
	 * 30% width, and the rest of cells don't specify any width.
	 * Of course, the real widths depend on the interpretation of
	 * the browser.
	 * @deprecated As of release 5.0.0, use {@link Cell} instead.
	 */
	public void setWidths(String widths) throws WrongValueException {
		final String[] sizes = Utils.stringToArray(widths, null);
		if (!Objects.equals(sizes, _sizes)) {
			_sizes = sizes;
			smartUpdate("_sizes", _sizes);
		}
	}
	/** Sets the widths/heights, which is a list of numbers separated
	 * by comma to denote the width/height of each cell in a box.
	 *
	 * <p>It is the same as {@link #setWidths}.
	 * @deprecated As of release 5.0.0, use {@link Cell} instead.
	 */
	public void setHeights(String heights) throws WrongValueException {
		setWidths(heights);
	}

	//-- super --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "spacing", _spacing);
		render(renderer, "_sizes", _sizes);

		if (!"start".equals(_align)) render(renderer, "align", _align);
		if (!"stretch".equals(_pack)) render(renderer, "pack", _pack);
	}
	public String getZclass() {
		return _zclass == null ? isVertical() ? "z-vbox" : "z-hbox" : _zclass;
	}
}
