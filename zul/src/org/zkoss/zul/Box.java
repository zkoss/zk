/* Box.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 20 21:51:32     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;
import java.io.IOException;

import org.zkoss.lang.JVMs;
import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.ext.render.Floating;

import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.impl.Utils;

/**
 * A box.
 *
 * @author tomyeh
 */
public class Box extends XulElement {
	private String _spacing;
	private String _align = "start", _pack = "start";
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
			invalidate();
		}
	}

	/** Returns the vertical alignment of the adjacent cells of a box
	 * (top, middle or bottom).
	 * <p>Default: null (i.e., use the browser default, usually middle).
	 * @deprecated As of release 3.0.0, since it is not compliant to XUL.
	 * Use {@link #getAlign} and {@link #getPack} instead.
	 */
	public String getValign() {
		return toValign(isVertical() ? getPack(): getAlign());
	}
	/** Sets the vertical alignment of the adjacent cells of a box.
	 *
	 * @param valign the vertical alignment: top, middle and bottom.
	 * @deprecated As of release 3.0.0, since it is not compliant to XUL.
	 * Use {@link #setAlign} and {@link #setPack} instead.
	 */
	public void setValign(String valign) {
		valign = valign == null ? null:
			"top".equals(valign) ? "start": 
			"middle".equals(valign) ? "center":
			"bottom".equals(valign) ? "end": valign;

		if (isVertical()) setPack(valign);
		else setAlign(valign);
	}
	private static String toValign(String v) {
		return v == null ? null: "start".equals(v) ? "top": 
			"center".equals(v) ? "middle":
			"end".equals(v) ? "bottom": v;
	}
	private static String toHalign(String v) {
		return v == null ? null: "start".equals(v) ? "left": 
			"end".equals(v) ? "right": v;
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
	 * to specify how its children are algined horizontally. The pack attribute
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
	 * </dl>
	 *
	 * @since 3.0.0
	 */
	public String getAlign() {
		return _align;
	}
	/** Sets the alignment of cells of this box in the 'opposite' direction
	 * (<i>null</i>, start, center, end).
	 *
	 * @param align the alignment in the 'opposite' direction.
	 * Allowed values: start, center, end.
	 * If empty or null, the browser's default is used
	 * (IE center and FF left, if vertical).
	 * @since 3.0.0
	 */
	public void setAlign(String align) {
		if (!Objects.equals(_align, align)) {
			_align = align;
			if (isVertical()) invalidate();
			else smartUpdate("valign", toValign(align));
		}
	}
	/** Returns the alignment of cells of this box
	 * (<i>null</i>, start, center, end).
	 *
	 * <p>Default: null.
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
	 * extra space is placed on the right or bottom side.</dd>
	 * <dt>center</dt>
	 * <dd>Extra space is split equally along each side of the child
	 * elements, resulting in the children being placed in the center of the box.</dd>
	 * <dt>end</dt>
	 * <dd>Child elements are placed on the right or bottom edge of the box. If
	 * the box is larger than the total size of the children, the extra space is
	 * placed on the left or top side.</dd>
	 * </dl>
	 *
	 * @since 3.0.0
	 */
	public String getPack() {
		return _pack;
	}
	/** Sets the alignment of cells of this box
	 * (<i>null</i>, start, center, end).
	 *
	 * @param pack the alignment. Allowed values: start, center, end.
	 * If empty or null, the browser's default is used.
	 * @since 3.0.0
	 */
	public void setPack(String pack) {
		if (!Objects.equals(_pack, pack)) {
			_pack = pack;
			invalidate(); //generated to all cells
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
	 */
	public void setWidths(String widths) throws WrongValueException {
		final String[] sizes = Utils.stringToArray(widths, null);
		if (!Objects.equals(sizes, _sizes)) {
			_sizes = sizes;
			invalidate();
		}
	}
	/** Sets the widths/heights, which is a list of numbers separated
	 * by comma to denote the width/height of each cell in a box.
	 *
	 * <p>It is the same as {@link #setWidths}.
	 */
	public void setHeights(String heights) throws WrongValueException {
		setWidths(heights);
	}

	/** Returns the outer attributes used to wrap the children (never null).
	 * It is used only for the vertical layout.
	 */
	public String getChildOuterAttrs(Component child) {
		final StringBuffer sb = new StringBuffer(64)
			.append(" z.coexist=\"true\"");
			//coexist: the visibility of exterior is the same as child.

		final boolean vert = isVertical();
		if (child instanceof Splitter) {
			sb.append(" class=\"")
				.append(((Splitter)child).getRealSclass())
				.append('"');
			if (!child.isVisible())
				sb.append(" style=\"display:none\"");
			return sb.toString();
		}
		
		//Note: style is handled in getChildInnerAttrs if horizontal layout
		//so we don't need to handle valign and visible if vertical
		if (vert) {
			HTMLs.appendAttribute(sb, "valign", toValign(_pack));
			if (!child.isVisible()) {
				final Object xc = ((ComponentCtrl)child).getExtraCtrl();
				if (!(xc instanceof Floating) || !((Floating)xc).isFloating())
					sb.append(" style=\"display:none\"");
			}
		}
		return sb.toString();
	}
	/** Returns the inner attributes used to wrap the children (never null).
	 * Used only by component development to generate HTML tags.
	 */
	public String getChildInnerAttrs(Component child) {
		if (child instanceof Splitter)
			return "";

		final boolean vert = isVertical();
		final StringBuffer sb = new StringBuffer(64);

		final String align = toHalign(vert ? _align: _pack);
		if (align != null && align.length() > 0) {
			HTMLs.appendAttribute(sb, "align", align);
		}

		String size = null;
		if (_sizes != null) {
			int j = 0;
			for (Iterator it = getChildren().iterator(); it.hasNext();) {
				final Object o = it.next();
				if (child == o) {
					size = _sizes[j];
					break;
				} else if (!(o instanceof Splitter)) {
					if (++j >= _sizes.length)
						break; //not found
				}
			}
		}

		final Object xc = ((ComponentCtrl)child).getExtraCtrl();
		final boolean floating =
			(xc instanceof Floating) && ((Floating)xc).isFloating();
		final boolean visible = vert || floating || child.isVisible();
			//if vert, visible is handled by getChildOutAttrs

		if (size != null || floating || !visible) {
			sb.append(" style=\"");
			if (!visible)
				sb.append("display:none;");

			if (floating || size != null)
				sb.append(vert ? "height": "width")
					.append(':').append(floating ? "0": size);

			sb.append('"');
		}
		return sb.toString();
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(80).append(super.getOuterAttrs());
		for (Iterator it = getChildren().iterator(); it.hasNext();)
			if (it.next() instanceof Splitter) {
				HTMLs.appendAttribute(sb, "z.hasSplt", true);
				break;
			}
		if ("vertical".equals(getOrient())) 
			HTMLs.appendAttribute(sb, "z.vert", "true");
		return sb.toString();
	}
	/** Returns the attributes used by the 'cave' element (never null).
	 * Used only by component development to generate HTML tags.
	 * @since 3.0.0
	 */
	public String getCaveAttrs() {
		if (isVertical())
			return "";

		final String valign = toValign(_align);
		return valign != null ? " valign=\"" + valign + '"': null;
	}

	//-- Component --//
	public boolean insertBefore(Component newChild, Component refChild) {
		//Bug 1828702: onChildAdded not called if only moved
		if (super.insertBefore(newChild, refChild)) {
			invalidate();
			return true;
		}
		return false;
	}
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		invalidate();
	}
	public void onDrawNewChild(Component child, StringBuffer out)
	throws IOException {
		throw new InternalError(); //impossible since we always invalidate
	}
}
