/* Rating.java

	Purpose:

	Description:

	History:
		Thu Jul 12 10:24:21 CST 2018, Created by wenninghsu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul;

import java.util.Map;

import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.Disable;
import org.zkoss.zk.ui.ext.Readonly;

/**
 * A rating component provides a icon based rating input.
 * The default icon is Unicode \u2605 star icon.
 * Icons could be set to unicode icons by specifying Unicode.
 * (for example: \u260E)
 * Or even Font Awesome icons with the prefix z-icon.
 * (for example: z-icon-home)
 *
 * The selectedIndex decides the rating input of this component.
 * <p>Default {@link #getZclass}: z-rating.
 *
 * @author wenninghsu
 * @since 8.6.0
 */
public class Rating extends HtmlBasedComponent implements Disable, Readonly {

	static {
		addClientEvent(Rating.class, Events.ON_CHANGE, CE_IMPORTANT | CE_REPEAT_IGNORE);
	}

	private String _orient = "horizontal";

	private int _rating = 0;

	private boolean _cancelable = true;

	private int _max = 5;

	private boolean _disabled = false;

	private boolean _readonly = false;

	private String _iconSclass = "z-icon-star";

	/**
	 * Returns the iconSclass name of this rating.
	 * @return the iconSclass name
	 */
	public String getIconSclass() {
		return _iconSclass;
	}

	/**
	 * Sets the iconSclass name of this rating.
	 * @param iconSclass String
	 */
	public void setIconSclass(String iconSclass) {
		if (!Objects.equals(_iconSclass, iconSclass)) {
			_iconSclass = iconSclass;
			smartUpdate("iconSclass", _iconSclass);
		}
	}

	/**
	 * Returns the orientation of this rating component.
	 * Default: horizontal if not specified.
	 * @return vertical or horizontal.
	 */
	public String getOrient() {
		return _orient;
	}

	/**
	 * Sets the orientation of this rating component.
	 * Default: horizontal if not specified.
	 * @param orient vertical or horizontal.
	 */
	public void setOrient(String orient) {
		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			invalidate();
		}
	}

	/**
	 * Returns the rating.
	 * @return the rating input
	 */
	public int getRating() {
		return _rating;
	}

	/**
	 * Sets the rating.
	 * @param rating
	 */
	public void setRating(int rating) {
		if (isInitialized() && (rating > _max || rating < 0)) {
			throw new UiException("Out of bound: " + rating + " while max is " + _max);
		}
		if (_rating != rating) {
			_rating = rating;
			smartUpdate("rating", _rating);
		}
	}

	/**
	 * Returns whether this rating component is cancelable.
	 * If true, the rating could be cancelled by clicking the rated rating again.
	 * Default: true
	 * @return true if enabled
	 */
	public boolean isCancelable() {
		return _cancelable;
	}

	/**
	 * Sets whether to enable the cancel feature.
	 * If true, the rating could be cancel by clicking the rated rating again.
	 * Default: true
	 * @param cancelable
	 */
	public void setCancelable(boolean cancelable) {
		if (_cancelable != cancelable) {
			_cancelable = cancelable;
			smartUpdate("_cancelable", _cancelable);
		}
	}

	/**
	 * Returns the max rating of this rating component.
	 * Default: 5
	 * @return max rate
	 */
	public int getMax() {
		return _max;
	}

	/**
	 * Sets the max rate of this rating component.
	 * Default: 5
	 * @param max
	 */
	public void setMax(int max) {
		if (max <= 0)
			throw new UiException("max should be > 0");
		if (isInitialized() && max < _rating) {
			throw new UiException("max should be larger than rating");
		}
		if (_max != max) {
			_max = max;
			invalidate();
		}
	}

	/**
	 * Returns if this rating component is disabled.
	 * @return boolean
	 */
	public boolean isDisabled() {
		return _disabled;
	}

	/**
	 * Sets whether this component is disabled, means the rating is not changeable.
 	 * @param disabled
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", _disabled);
		}
	}

	/**
	 * Returns if this rating component is readonly.
	 * @return boolean
	 */
	public boolean isReadonly() {
		return _readonly;
	}

	/**
	 * Sets whether this component is readonly.
	 * @param readonly
	 */
	public void setReadonly(boolean readonly) {
		if (_readonly != readonly) {
			_readonly = readonly;
			smartUpdate("readonly", _readonly);
		}
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		super.renderProperties(renderer);

		if (_max <= 0)
			throw new UiException("max should be > 0");
		if (_rating > _max) {
			throw new UiException("Out of bound: " + _rating + " while max is" + _max);
		}
		if (_rating != 0) {
			render(renderer, "rating", _rating);
		}
		if (_max != 5) {
			render(renderer, "_max", _max);
		}
		if (_disabled) {
			renderer.render("disabled", true);
		}
		if (_readonly) {
			renderer.render("readonly", true);
		}
		if (!_cancelable) {
			renderer.render("_cancelable", false);
		}
		if (!"horizontal".equals(_orient)) {
			render(renderer, "_orient", "vertical");
		}
		if (!"z-icon-star".equals(_iconSclass)) {
			render(renderer, "iconSclass", _iconSclass);
		}

	}

	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		final Map data = request.getData();

		if (cmd.equals(Events.ON_CHANGE)) {
			disableClientUpdate(true);
			try {
				setRating((Integer) data.get("rating"));
			} finally {
				disableClientUpdate(false);
				Events.postEvent(Event.getEvent(request));
			}
		} else
			super.service(request, everError);
	}

}
