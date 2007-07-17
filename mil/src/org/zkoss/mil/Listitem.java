/* Listitem.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 15, 2007 10:17:32 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;

/**
 * The Listitem that can be located under Listbox only.
 * 
 * @author henrichen
 */
public class Listitem extends MilComponent {
	private static final long serialVersionUID = 200706151103L;

	private boolean _selected;
	private String _label;
	private Object _value;
	private String _src;
	/** The image. If not null, _src is generated automatically. */
	private org.zkoss.image.Image _image;
	/** Count the version of {@link #_image}. */
	private int _imgver;
	
	public Listitem() {
	}
	public Listitem(String label) {
		setLabel(label);
	}
	public Listitem(String label, Object value) {
		setLabel(label);
		setValue(value);
	}

	/** Returns the list box that it belongs to.
	 */
	public Listbox getListbox() {
		return (Listbox)getParent();
	}
	
	/** 
	 * Get the label of this Item.
	 * 
	 * @return the label of this Item.
	 */
	public String getLabel() {
		return _label;
	}
	
	/**
	 * Set the new label of this Item.
	 * @param label the new label of this Item.
	 */
	public void setLabel(String label) {
		if (label != null && label.length() == 0) {
			label = null;
		}
		if (!Objects.equals(_label, label)) {
			_label = label;
			smartUpdate("lb", encodeString(label));
		}
	}

	/** Sets whether it is selected.
	 */
	public void setSelected(boolean selected) {
		if (_selected != selected) {
			final Listbox listbox = (Listbox)getParent();
			if (listbox != null) {
				//Note: we don't update it here but let its parent does the job
				listbox.toggleItemSelection(this);
			} else {
				_selected = selected;
			}
		}
	}

	public boolean isSelected() {
		return _selected;
	}
	
	/*package*/void setSelectedDirectly(boolean b) {
		_selected = b;
	}
	
	public void setValue(Object value) {
		_value = value;
	}
	
	public Object getValue() {
		return _value;
	}

	/** Returns the image source of this Listitem, or null if no image.
	 */
	public String getSrc() {
		return _src;
	}
	/** Sets theimage soruce of this Listitem.
	 * @param src the image source
	 */
	public void setSrc(String src) {
		setImage(src);
	}
	
	/** Returns the image source of this Listitem, or null if no image.
	 */
	public String getImage() {
		return _src;
	}
	/** Sets the image source of this Listitem.
	 * @param image the image source
	 */
	public void setImage(String image) {
		if (image != null && image.length() == 0)
			image = null;
		
		if (!Objects.equals(_src, image)) {
			_src = image;
			if (_image == null) {
				smartUpdate("im", getEncodedSrc());
			}
			//_src is meaningful only if _image is null
		}
	}

	/** Returns the encoded src ({@link #getSrc}).
	 */
	private String getEncodedSrc() {
		final Desktop dt = getDesktop(); //it might not belong to any desktop
		return _image != null ? getContentSrc(): //already encoded
			dt != null ? dt.getExecution().encodeURL(
				_src != null ? _src: "~./img/spacer.gif"): "";
	}
	
	/** Sets the content directly.
	 * Default: null.
	 *
	 * @param image the image to display. If not null, it has higher
	 * priority than {@link #getSrc}.
	 */
	public void setImageContent(org.zkoss.image.Image image) {
		if (image != _image) {
			_image = image;
			if (_image != null) ++_imgver; //enforce browser to reload image
			smartUpdate("im", getEncodedSrc());
		}
	}

	/** Returns the content set by {@link #setImageContent}.
	 * <p>Note: it won't fetch what is set thru by {@link #setSrc}.
	 * It simply returns what is passed to {@link #setImageContent}.
	 */
	public org.zkoss.image.Image getImageContent() {
		return _image;
	}

	/** Returns the encoded URL for the current image content.
	 * Don't call this method unless _image is not null;
	 *
	 * <p>Used only for component template, not for application developers.
	 */
	private String getContentSrc() {
		final Desktop desktop = getDesktop();
		if (desktop == null) return ""; //no avail at client

		final StringBuffer sb = new StringBuffer(64).append('/');
		Strings.encode(sb, _imgver);
		final String name = _image.getName();
		final String format = _image.getFormat();
		if (name != null || format != null) {
			sb.append('/');
			boolean bExtRequired = true;
			if (name != null && name.length() != 0) {
				sb.append(name);
				bExtRequired = name.lastIndexOf('.') < 0;
			} else {
				sb.append(getId());
			}
			if (bExtRequired && format != null)
				sb.append('.').append(format);
		}
		return desktop.getDynamicMediaURI(this, sb.toString()); //already encoded
	}
	
	//--Component--//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof org.zkoss.mil.Listbox)) {
			throw new UiException("Unsupported parent for Listitem: "+this+", parent: "+parent);
		}
		super.setParent(parent);
	}

	/** Not childable. */
	public boolean isChildable() {
		return false;
	}

	public String getInnerAttrs() {
		final StringBuffer sb = new StringBuffer(64);
		if (getLabel() != null) {
			HTMLs.appendAttribute(sb, "lb",  getLabel());
		}
		if (getImage() != null) {
			HTMLs.appendAttribute(sb, "im",  getImage());
		}
		return sb.toString();
	}
}
