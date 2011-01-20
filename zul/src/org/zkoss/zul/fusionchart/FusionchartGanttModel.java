/* FusionchartGanttModel.java

	Purpose:
		
	Description:
		
	History:
		Jan 13, 2011 5:48:47 PM, Created by jimmyshiau

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.fusionchart;

import java.awt.Font;
import java.util.Date;

import org.zkoss.lang.Objects;
import org.zkoss.zul.*;
import org.zkoss.zul.fusionchart.FusionchartCategoryModel.FusionchartSeries;

/**
 * A Category data model implementation of {@link CategoryModel}. A Category
 * model is an N series of (category, value) data objects.
 * 
 * @author jimmyshiau
 * @see CategoryModel
 * @see Fusionchart
 */
public class FusionchartGanttModel extends AbstractFusionchartGanttModel {

	private static final long serialVersionUID = 20110104121601L;

	public void addValue(Comparable series, GanttTask task) {
		super.addValue(series, task);
		if (series instanceof FusionchartSeries) {
			FusionchartSeries fseries = (FusionchartSeries) series;
			fseries.setCategory(task);
			fseries.setOwner(this);
		}
	}

	public void removeValue(Comparable series, GanttTask task) {
		super.removeValue(series, task);
		if (series instanceof FusionchartSeries) {
			FusionchartSeries fseries = (FusionchartSeries) series;
			fseries.setCategory(null);
			fseries.setOwner((AbstractFusionchartGanttModel)null);
		}
	}
	
	protected void fireEvent(int type, Comparable series, Object data) {
		super.fireEvent(type, series, data);
	}

	/**
	 * A Task in an operation series; a helper class used in
	 * {@link FusionchartGanttModel}.
	 * 
	 * @author jimmyshiau
	 * @see FusionchartGanttModel
	 */
	public static class FusionchartGanttTask extends GanttTask {
		private static final long serialVersionUID = 20110103102914L;

		private String _hoverText;
		private String _link;
		private boolean _animation = false;
		private boolean _showName = true;
		private boolean _showStartDate = true;
		private boolean _showEndDate = true;
		private Integer _taskDatePadding;
		private String _color;
		private int _alpha = 255;
		private Font _font;
		private Border _border;

		public FusionchartGanttTask(String description, Date start, Date end) {
			super(description, start, end, 0);
		}

		public FusionchartGanttTask(String description, Date start, Date end,
				String color) {
			this(description, start, end);
			this._color = color;
		}
		
		public FusionchartGanttTask(String description, Date start, Date end,
				boolean animation) {
			this(description, start, end);
			this._animation = animation;
		}
		
		public FusionchartGanttTask(String description, Date start, Date end,
				String color, boolean animation) {
			this(description, start, end, animation);
			this._color = color;
		}

		public FusionchartGanttTask(String description, Date start, Date end,
				String color, boolean animation,
				String hoverText, String link,
				boolean showName, boolean showStartDate,
				boolean showEndDate, Integer taskDatePadding,
				int alpha, Font font, Border border) {
			this(description, start, end, color, animation);
			this._hoverText = hoverText;
			this._link = link;
			this._showName = showName;
			this._showStartDate = showStartDate;
			this._showEndDate = showEndDate;
			this._taskDatePadding = taskDatePadding;
			this._alpha = alpha;
			this._font = font;
			this._border = border;
		}

		/**
		 * Returns the hover text of this task bar.
		 * 
		 * @return String
		 */
		public String getHoverText() {
			return _hoverText;
		}

		/**
		 * Sets the hover text of this task bar.
		 * 
		 * @param hoverText
		 */
		public void setHoverText(String hoverText) {
			if (!Objects.equals(hoverText, _hoverText)) {
				this._hoverText = hoverText;
				fireChartChange();
			}
		}

		/**
		 * Return the hyper link of this task bar.
		 * 
		 * @return String
		 */
		public String getLink() {
			return _link;
		}

		/**
		 * Sets the hyper link of this task bar.
		 * 
		 * @param link
		 */
		public void setLink(String link) {
			if (!Objects.equals(link, _link)) {
				this._link = link;
				fireChartChange();
			}
		}

		/**
		 * Returns whether this particular task bar would animate or not.
		 * <p>
		 * Default: false.
		 * 
		 * @return boolean
		 */
		public boolean isAnimation() {
			return _animation;
		}

		/**
		 * Sets whether this particular task bar would animate or not.
		 * <p>
		 * Default: false.
		 * 
		 * @param animation
		 */
		public void setAnimation(boolean animation) {
			if (animation != _animation) {
				this._animation = animation;
				fireChartChange();
			}
		}

		/**
		 * Returns whether to show the name of this tasks over the task bar.
		 * <p>
		 * Default: true.
		 * 
		 * @return boolean
		 */
		public boolean isShowName() {
			return _showName;
		}

		/**
		 * Sets whether to show the name of this tasks over the task bar.
		 * <p>
		 * Default: true.
		 * 
		 * @param showName
		 */
		public void setShowName(boolean showName) {
			if (showName != _showName) {
				this._showName = showName;
				fireChartChange();
			}
		}

		/**
		 * Returns whether to show the start date of this task on the left of
		 * task bar.
		 * <p>
		 * Default: true.
		 * 
		 * @return boolean
		 */
		public boolean isShowStartDate() {
			return _showStartDate;
		}

		/**
		 * Sets whether to show the start date of this task on the left of task
		 * bar.
		 * <p>
		 * Default: true.
		 * 
		 * @param showStartDate
		 */
		public void setShowStartDate(boolean showStartDate) {
			if (showStartDate != _showStartDate) {
				this._showStartDate = showStartDate;
				fireChartChange();
			}
		}

		/**
		 * Returns whether to show the end date of this task on the right side
		 * of the task bar.
		 * <p>
		 * Default: true.
		 * 
		 * @return boolean
		 */
		public boolean isShowEndDate() {
			return _showEndDate;
		}

		/**
		 * Returns whether to show the end date of this task on the right side
		 * of the task bar.
		 * <p>
		 * Default: true.
		 * 
		 * @param showEndDate
		 */
		public void setShowEndDate(boolean showEndDate) {
			if (showEndDate != _showEndDate) {
				this._showEndDate = showEndDate;
				fireChartChange();
			}
		}

		/**
		 * Returns the distance between task bar and date textbox.
		 * 
		 * @return int
		 */
		public Integer getTaskDatePadding() {
			return _taskDatePadding;
		}

		/**
		 * Sets the distance between task bar and date textbox, if null
		 * FusionCharts automatically calculates the best possible value.
		 * 
		 * @param taskDatePadding
		 */
		public void setTaskDatePadding(Integer taskDatePadding) {
			if (taskDatePadding != _taskDatePadding) {
				this._taskDatePadding = taskDatePadding;
				fireChartChange();
			}
		}

		/**
		 * Returns the background color for the task bar.
		 * 
		 * @return String
		 */
		public String getColor() {
			return _color;
		}

		/**
		 * Sets the background color for the task bar. If you need to show a
		 * gradiented background, just specify the list of colors here using a
		 * comma.
		 * 
		 * @param color
		 */
		public void setColor(String color) {
			if (!Objects.equals(color, _color)) {
				this._color = color;
				fireChartChange();
			}
		}

		/**
		 * Returns the transparency of the task bar.
		 * <p>
		 * Default: 255.
		 * 
		 * @return int
		 */
		public int getAlpha() {
			return _alpha;
		}

		/**
		 * Sets the transparency of the task bar.
		 * <p>
		 * Default: 255.
		 * 
		 * @param alpha
		 */
		public void setAlpha(int alpha) {
			if (alpha != _alpha) {
				this._alpha = alpha;
				fireChartChange();
			}
		}

		/**
		 * Returns the font in which text will be rendered.
		 * 
		 * @return Font
		 */
		public Font getFont() {
			return _font;
		}

		/**
		 * Sets the font in which text will be rendered.
		 * 
		 * @param font
		 */
		public void setFont(Font font) {
			if (!Objects.equals(font, _font)) {
				this._font = font;
				fireChartChange();
			}
		}

		/**
		 * Returns the border would appear around the task bar.
		 * 
		 * @return Border
		 */
		public Border getBorder() {
			return _border;
		}

		/**
		 * Sets the border would appear around the task bar.
		 * 
		 * @param border
		 */
		public void setBorder(Border border) {
			if (!Objects.equals(border, _border)) {
				this._border = border;
				fireChartChange();
			}
		}

	}

	/**
	 * A FusionchartAreaSeries in an operation series; a helper class used in
	 * {@link FusionchartCategoryModel}.
	 * 
	 * @author jimmyshiau
	 * @see FusionchartCategoryModel
	 */
	public static class FusionchartGanttSeries extends FusionchartSeries {

		private static final long serialVersionUID = 20110120122422L;
		
		private boolean _animation = false;
		private Font _font;
		private Border _border;
		
		public FusionchartGanttSeries(String seriesName) {
			super(seriesName);
		}

		public FusionchartGanttSeries(String seriesName, String color) {
			super(seriesName, color);
		}

		public FusionchartGanttSeries(String seriesName, int alpha) {
			super(seriesName, alpha);
		}

		public FusionchartGanttSeries(String seriesName, String color, int alpha) {
			super(seriesName, color, alpha);
		}

		public FusionchartGanttSeries(String seriesName, boolean _animation) {
			super(seriesName);
			this._animation = _animation;
		}

		public FusionchartGanttSeries(String seriesName, boolean _animation,
				Font _font, Border _border) {
			super(seriesName);
			this._animation = _animation;
			this._font = _font;
			this._border = _border;
		}
		
		/**
		 * Returns whether this particular task bar would animate or not.
		 * <p>
		 * Default: false.
		 * 
		 * @return boolean
		 */
		public boolean isAnimation() {
			return _animation;
		}

		/**
		 * Sets whether this particular task bar would animate or not.
		 * <p>
		 * Default: false.
		 * 
		 * @param animation
		 */
		public void setAnimation(boolean animation) {
			if (animation != _animation) {
				this._animation = animation;
				fireChartChange();
			}
		}
		
		/**
		 * Returns the font in which text will be rendered.
		 * 
		 * @return Font
		 */
		public Font getFont() {
			return _font;
		}

		/**
		 * Sets the font in which text will be rendered.
		 * 
		 * @param font
		 */
		public void setFont(Font font) {
			if (!Objects.equals(font, _font)) {
				this._font = font;
				fireChartChange();
			}
		}

		/**
		 * Returns the border would appear around the task bar.
		 * 
		 * @return Border
		 */
		public Border getBorder() {
			return _border;
		}

		/**
		 * Sets the border would appear around the task bar.
		 * 
		 * @param border
		 */
		public void setBorder(Border border) {
			if (!Objects.equals(border, _border)) {
				this._border = border;
				fireChartChange();
			}
		}
	}
	
	/**
	 * A Border class for store all of border attributes.
	 * 
	 * @author jimmyshiau
	 */
	/**
	 * @author Jimmy
	 * 
	 *         2011
	 */
	public static class Border implements java.io.Serializable {
		private static final long serialVersionUID = 20110104121704L;
		private boolean showBorder = true;
		private String color;
		private int thickness;
		private int alpha;

		public Border(String color, int thickness, int alpha) {
			super();
			this.color = color;
			this.thickness = thickness;
			this.alpha = alpha;
		}

		public Border(boolean showBorder, String color,
				int thickness, int alpha) {
			super();
			this.showBorder = showBorder;
			this.color = color;
			this.thickness = thickness;
			this.alpha = alpha;
		}

		/**
		 * Returns whether a border would appear around the task bar.
		 * 
		 * @return boolean
		 */
		public boolean isShowBorder() {
			return showBorder;
		}

		/**
		 * Returns the color of the task bar border.
		 * 
		 * @return String
		 */
		public String getColor() {
			return color;
		}

		/**
		 * Returns the thickness of the task bar border.
		 * 
		 * @return int
		 */
		public int getThickness() {
			return thickness;
		}

		/**
		 * Returns the alpha of the task bar border.
		 * 
		 * @return int
		 */
		public int getAlpha() {
			return alpha;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + alpha;
			result = prime * result
					+ ((color == null) ? 0 : color.hashCode());
			result = prime * result + thickness;
			result = prime * result + (showBorder ? 1231 : 1237);
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Border other = (Border) obj;
			if (alpha != other.alpha)
				return false;
			if (color == null) {
				if (other.color != null)
					return false;
			} else if (!color.equals(other.color))
				return false;
			if (thickness != other.thickness)
				return false;
			if (showBorder != other.showBorder)
				return false;
			return true;
		}

		public String toString() {
			return "Border [showBorder=" + showBorder + ", borderColor="
					+ color + ", borderThickness=" + thickness
					+ ", borderAlpha=" + alpha + "]";
		}
	}

}
