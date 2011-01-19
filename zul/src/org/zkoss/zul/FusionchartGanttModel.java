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
package org.zkoss.zul;

import java.awt.Font;
import java.util.Date;

import org.zkoss.lang.Objects;
import org.zkoss.zul.FusionchartCategoryModel.FusionchartSeries;
import org.zkoss.zul.event.ChartDataEvent;

/**
 * A Category data model implementation of {@link CategoryModel}. A Category
 * model is an N series of (category, value) data objects.
 * 
 * @author jimmyshiau
 * @see CategoryModel
 * @see Fusionchart
 */
public class FusionchartGanttModel extends GanttModel {

	private static final long serialVersionUID = 20110104121601L;

	public void addValue(Comparable series, GanttTask task) {
		super.addValue(series, task);
		if (task instanceof FusionchartGanttTask) {
			FusionchartGanttTask ftask = (FusionchartGanttTask) task;
			ftask.setSeries(series);
			ftask.setOwner(this);
			if (series instanceof FusionchartSeries) {
				FusionchartSeries fseries = (FusionchartSeries) series;
				fseries.setCategory(task);
				fseries.setOwner(this);
			}
		}
	}

	public void removeValue(Comparable series, GanttTask task) {
		super.removeValue(series, task);
		if (task instanceof FusionchartGanttTask) {
			FusionchartGanttTask ftask = (FusionchartGanttTask) task;
			ftask.setSeries(null);
			ftask.setOwner(null);
			if (series instanceof FusionchartSeries) {
				FusionchartSeries fseries = (FusionchartSeries) series;
				fseries.setCategory(null);
				fseries.setOwner(null);
			}
		}
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

		private static int count = 0;
		private int _id = count++;
		private int _processId;
		private String _hoverText;
		private String _link;
		private boolean _animation = false;
		private boolean _showName = true;
		private boolean _showStartDate = true;
		private boolean _showEndDate = true;
		private int _height;
		private int _topPadding;
		private Integer _taskDatePadding;
		private String _color;
		private int _alpha = 255;
		private Font _font;
		private Border _border;

		private GanttModel _owner;
		private Comparable _series;

		public FusionchartGanttTask(String description, Date start, Date end,
				double percent) {
			super(description, start, end, percent);
		}

		public FusionchartGanttTask(String description, Date start, Date end,
				double percent, String color) {
			this(description, start, end, percent);
			this._color = color;
		}

		public FusionchartGanttTask(String description, Date start, Date end,
				double percent, String color, int height, int topPadding) {
			this(description, start, end, percent);
			this._color = color;
			this._height = height;
			this._topPadding = topPadding;
		}

		public FusionchartGanttTask(String description, Date start, Date end,
				double percent, String color, int height, int topPadding,
				boolean animation) {
			this(description, start, end, percent);
			this._color = color;
			this._height = height;
			this._topPadding = topPadding;
			this._animation = animation;
		}

		public FusionchartGanttTask(String description, Date start, Date end,
				double percent, String hoverText, String link,
				boolean animation, boolean showName, boolean showStartDate,
				boolean showEndDate, int height, int topPadding,
				Integer taskDatePadding, String color, int alpha, Font font,
				Border border) {
			super(description, start, end, percent);
			this._hoverText = hoverText;
			this._link = link;
			this._animation = animation;
			this._showName = showName;
			this._showStartDate = showStartDate;
			this._showEndDate = showEndDate;
			this._height = height;
			this._topPadding = topPadding;
			this._taskDatePadding = taskDatePadding;
			this._color = color;
			this._alpha = alpha;
			this._font = font;
			this._border = border;
		}

		public int getProcessId() {
			return _processId;
		}

		private void setProcessId(int processId) {
			this._processId = processId;
			if (processId != _processId) {
				this._processId = processId;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
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
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
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
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
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
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
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
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
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
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
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
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		/**
		 * Returns the height for the task bar.
		 * 
		 * @return int
		 */
		public int getHeight() {
			return _height;
		}

		/**
		 * Sets the height for the task bar, if null FusionCharts automatically
		 * calculates the best possible value.
		 * 
		 * @param height
		 */
		public void setHeight(int height) {
			if (height != _height) {
				this._height = height;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		/**
		 * Returns the top padding for the task bar.
		 * 
		 * @return int
		 */
		public int getTopPadding() {
			return _topPadding;
		}

		/**
		 * Sets the top padding for the task bar, if null FusionCharts
		 * automatically calculates the best possible value.
		 * 
		 * @param topPadding
		 */
		public void setTopPadding(int topPadding) {
			if (topPadding != _topPadding) {
				this._topPadding = topPadding;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
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
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
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
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
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
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
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
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
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
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		/**
		 * Returns the id of the task.
		 * 
		 * @return int
		 */
		public int getId() {
			return _id;
		}

		public void addSubtask(GanttTask task) {
			super.addSubtask(task);
			if (task instanceof FusionchartGanttTask) {
				FusionchartGanttTask ftask = (FusionchartGanttTask) task;
				ftask.setSeries(_series);
				ftask.setOwner(_owner);
				if (_series instanceof FusionchartSeries) {
					FusionchartSeries fseries = (FusionchartSeries) _series;
					fseries.setCategory(_series);
					fseries.setOwner(_owner);
				}
			}
		}

		public void removeSubtask(GanttTask task) {
			super.removeSubtask(task);
			if (task instanceof FusionchartGanttTask) {
				FusionchartGanttTask ftask = (FusionchartGanttTask) task;
				ftask.setSeries(null);
				ftask.setOwner(null);
				if (_series instanceof FusionchartSeries) {
					FusionchartSeries fseries = (FusionchartSeries) _series;
					fseries.setCategory(null);
					fseries.setOwner(null);
				}
			}
		}

		private void setSeries(Comparable series) {
			_series = series;
		}

		private void setOwner(GanttModel owner) {
			_owner = owner;
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
		private String borderColor;
		private int borderThickness;
		private int borderAlpha;

		public Border(String borderColor, int borderThickness, int borderAlpha) {
			super();
			this.borderColor = borderColor;
			this.borderThickness = borderThickness;
			this.borderAlpha = borderAlpha;
		}

		public Border(boolean showBorder, String borderColor,
				int borderThickness, int borderAlpha) {
			super();
			this.showBorder = showBorder;
			this.borderColor = borderColor;
			this.borderThickness = borderThickness;
			this.borderAlpha = borderAlpha;
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
		public String getBorderColor() {
			return borderColor;
		}

		/**
		 * Returns the thickness of the task bar border.
		 * 
		 * @return int
		 */
		public int getBorderThickness() {
			return borderThickness;
		}

		/**
		 * Returns the alpha of the task bar border.
		 * 
		 * @return int
		 */
		public int getBorderAlpha() {
			return borderAlpha;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + borderAlpha;
			result = prime * result
					+ ((borderColor == null) ? 0 : borderColor.hashCode());
			result = prime * result + borderThickness;
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
			if (borderAlpha != other.borderAlpha)
				return false;
			if (borderColor == null) {
				if (other.borderColor != null)
					return false;
			} else if (!borderColor.equals(other.borderColor))
				return false;
			if (borderThickness != other.borderThickness)
				return false;
			if (showBorder != other.showBorder)
				return false;
			return true;
		}

		public String toString() {
			return "Border [showBorder=" + showBorder + ", borderColor="
					+ borderColor + ", borderThickness=" + borderThickness
					+ ", borderAlpha=" + borderAlpha + "]";
		}
	}

}
