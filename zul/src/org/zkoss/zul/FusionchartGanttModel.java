package org.zkoss.zul;

import java.util.Date;

import org.zkoss.lang.Objects;
import org.zkoss.zul.event.ChartDataEvent;

public class FusionchartGanttModel extends GanttModel {

	private static final long serialVersionUID = 20110104121601L;

	public void addValue(Comparable series, GanttTask task) {
		super.addValue(series, task);
		if (task instanceof FusionchartGanttTask) {
			FusionchartGanttTask ftask = (FusionchartGanttTask) task;
			ftask.setSeries(series);
			ftask.setOwner(this);
			if (series instanceof FusionchartGanttProcess) {
				FusionchartGanttProcess process = (FusionchartGanttProcess) series;
				ftask.setProcessId(process.getId());
				process.setSeries(series);
				process.setOwner(this);
			}
		}
	}

	public void removeValue(Comparable series, GanttTask task) {
		super.removeValue(series, task);
		if (task instanceof FusionchartGanttTask) {
			FusionchartGanttTask ftask = (FusionchartGanttTask) task;
			ftask.setSeries(null);
			ftask.setOwner(null);
			if (series instanceof FusionchartGanttProcess) {
				FusionchartGanttProcess process = (FusionchartGanttProcess) series;
				process.setSeries(null);
				process.setOwner(null);
			}
		}
	}

	/**
	 * A Task in an operation series; a helper class used in
	 * {@FusionchartGanttModel}.
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
		private int _taskDatePadding;
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
				int taskDatePadding, String color, int alpha, Font font,
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
		 * @return String
		 */
		public String getHoverText() {
			return _hoverText;
		}

		/**
		 * Sets the hover text of this task bar.
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
		 * @return String
		 */
		public String getLink() {
			return _link;
		}

		/**
		 * Sets  the hyper link of this task bar.
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
		 * <p>Default: false.
		 * @return boolean
		 */
		public boolean isAnimation() {
			return _animation;
		}

		/**
		 * Sets whether this particular task bar would animate or not.
		 * <p>Default: false.
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
		 * <p>Default: true.
		 * @return boolean
		 */
		public boolean isShowName() {
			return _showName;
		}

		/**
		 * Sets whether to show the name of this tasks over the task bar.
		 * <p>Default: true.
		 * @return boolean
		 */
		public void setShowName(boolean showName) {
			if (showName != _showName) {
				this._showName = showName;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		/**
		 * Returns whether to show the start date of this task on the left of task bar.
		 * <p>Default: true.
		 * @return boolean
		 */
		public boolean isShowStartDate() {
			return _showStartDate;
		}

		/**
		 * Sets whether to show the start date of this task on the left of task bar.
		 * <p>Default: true.
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
		 * Returns whether to show the end date of this task on the right side of the task bar.
		 * <p>Default: true.
		 * @return boolean
		 */
		public boolean isShowEndDate() {
			return _showEndDate;
		}

		/**
		 * Returns whether to show the end date of this task on the right side of the task bar.
		 * <p>Default: true.
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
		 * @return int
		 */
		public int getHeight() {
			return _height;
		}

		/**
		 * Sets the height for the task bar, if null
		 * FusionCharts automatically calculates the best possible value.
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
		 * @return int
		 */
		public int getTopPadding() {
			return _topPadding;
		}

		/**
		 * Sets the top padding for the task bar, if null
		 * FusionCharts automatically calculates the best possible value.
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
		 * @return int
		 */
		public int getTaskDatePadding() {
			return _taskDatePadding;
		}

		/**
		 * Sets the distance between task bar and date textbox, if null
		 * FusionCharts automatically calculates the best possible value.
		 * @param taskDatePadding
		 */
		public void setTaskDatePadding(int taskDatePadding) {
			if (taskDatePadding != _taskDatePadding) {
				this._taskDatePadding = taskDatePadding;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		/**
		 * Returns the background color for the task bar.
		 * @return String
		 */
		public String getColor() {
			return _color;
		}

		/**
		 * Sets  the background color for the task bar.
		 * If you need to show a gradiented background, 
		 * just specify the list of colors here using a comma.
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
		 * @return int
		 */
		public int getAlpha() {
			return _alpha;
		}

		/**
		 * Sets the transparency of the task bar.
		 * <p>
		 * Default: 255.
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
		 * @return Font
		 */
		public Font getFont() {
			return _font;
		}

		/**
		 * Sets the font in which text will be rendered.
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
		 * @return Border
		 */
		public Border getBorder() {
			return _border;
		}

		/**
		 * Sets  the border would appear around the task bar.
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
		 * @return
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
				if (_series instanceof FusionchartGanttProcess) {
					FusionchartGanttProcess process = (FusionchartGanttProcess) _series;
					ftask.setProcessId(process.getId());
					process.setSeries(_series);
					process.setOwner(_owner);
				}
			}
		}

		public void removeSubtask(GanttTask task) {
			super.removeSubtask(task);
			if (task instanceof FusionchartGanttTask) {
				FusionchartGanttTask ftask = (FusionchartGanttTask) task;
				ftask.setSeries(null);
				ftask.setOwner(null);
				if (_series instanceof FusionchartGanttProcess) {
					FusionchartGanttProcess process = (FusionchartGanttProcess) _series;
					process.setSeries(null);
					process.setOwner(null);
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
	 * A Process in an operation series; a helper class used in
	 * {@link FusionchartGanttModel}.
	 * 
	 * @author jimmyshiau
	 * @see FusionchartGanttModel
	 */
	public static class FusionchartGanttProcess implements Comparable, java.io.Serializable {
		private static final long serialVersionUID = 20110104121702L;
		private static int count = 0;
		private int _id = count++;
		private String _name;
		private String _link;
		private Font _font;
		private boolean _bold = false;
		private boolean _underLine = false;
		private int _verticalPadding;
		private String _align;
		private String _vAlign;
		
		private GanttModel _owner;
		private Comparable _series;

		public FusionchartGanttProcess(String name) {
			super();
			this._name = name;
		}

		public FusionchartGanttProcess(String name, Font font, boolean bold,
				boolean underLine) {
			super();
			this._name = name;
			this._font = font;
			this._bold = bold;
			this._underLine = underLine;
		}

		public FusionchartGanttProcess(String name, String link, Font font,
				boolean bold, boolean underLine, int verticalPadding,
				String align, String vAlign) {
			super();
			this._name = name;
			this._link = link;
			this._font = font;
			this._bold = bold;
			this._underLine = underLine;
			this._verticalPadding = verticalPadding;
			this._align = align;
			this._vAlign = vAlign;
		}

		private int getId() {
			return _id;
		}

		/**
		 * Returns the name of the process, which will be displayed on the chart.
		 * @return String
		 */
		public String getName() {
			return _name;
		}

		/**
		 * Sets the name of the process, which will be displayed on the chart.
		 * @param name the name of the process
		 */
		public void setName(String name) {
			if (!Objects.equals(name, _name)) {
				this._name = name;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		/**
		 * Returns the link of the process, it will be a hyperlink to the process name.
		 * @return String
		 */
		public String getLink() {
			return _link;
		}

		/**
		 * Sets the link of the process, it will be a hyperlink to the process name.
		 * @param link the link of the process
		 */
		public void setLink(String link) {
			if (!Objects.equals(link, _link)) {
				this._link = link;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		/**
		 * Returns the font in which text will be rendered.
		 * @return Font
		 */
		public Font getFont() {
			return _font;
		}

		/**
		 * Sets the font in which text will be rendered.
		 * @param font the font in which text
		 */
		public void setFont(Font font) {
			if (!Objects.equals(font, _font)) {
				this._font = font;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		/**
		 * Returns whether the text will be shown as bold or not.
		 * <p>Default: false.
		 * @return boolean
		 */
		public boolean isBold() {
			return _bold;
		}

		/**
		 * Sets whether the text will be shown as bold or not.
		 * <p>Default: false.
		 * @param bold
		 */
		public void setBold(boolean bold) {
			if (this._bold != bold) {
				this._bold = bold;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		/**
		 * Returns whether the text will be shown as underline.
		 * <p>Default: false.
		 * @return boolean
		 */
		public boolean isUnderLine() {
			return _underLine;
		}

		/**
		 * Sets whether the text will be shown as underline.
		 * <p>Default: false.
		 * @param underLine
		 */
		public void setUnderLine(boolean underLine) {
			if (this._underLine != underLine) {
				this._underLine = underLine;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		/**
		 * Returns the top margin.
		 * @return int
		 */
		public int getVerticalPadding() {
			return _verticalPadding;
		}

		/**
		 * Sets the top margin.
		 * @param verticalPadding
		 */
		public void setVerticalPadding(int verticalPadding) {
			if (this._verticalPadding != verticalPadding) {
				this._verticalPadding = verticalPadding;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		/**
		 * Returns the horizontal alignment of text.
		 * @return String
		 */
		public String getAlign() {
			return _align;
		}

		/**
		 * Sets the horizontal alignment of text.
		 * <p>Allowed values: left, center, right. 
		 * @param align
		 */
		public void setAlign(String align) {
			if (!Objects.equals(align, _align)) {
				this._align = align;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		/**
		 * Returns the vertical alignment of text.
		 * @return String
		 */
		public String getvAlign() {
			return _vAlign;
		}

		/**
		 * Sets  the vertical alignment of text.
		 * <p>Allowed values: left, center, right.
		 * @param vAlign
		 */
		public void setvAlign(String vAlign) {
			if (!Objects.equals(vAlign, _vAlign)) {
				this._vAlign = vAlign;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		private void setSeries(Comparable series) {
			_series = series;
		}

		private void setOwner(GanttModel owner) {
			_owner = owner;
		}
		
		public int compareTo(Object obj) {
			boolean isString = obj instanceof String;
			if (!(obj instanceof FusionchartGanttProcess || isString))
				throw new ClassCastException(
						"series shall be a String or a FusionchartGanttProcess");
			if (isString)
				return _name.compareTo(obj);

			FusionchartGanttProcess process = (FusionchartGanttProcess) obj;
			int res = new Integer(_id).compareTo(new Integer(process._id));
			if (res != 0)
				return res;

			return _name.compareTo(process.getName());
		}
	}

	/**
	 * A Font class for store all of font attributes.
	 * @author jimmyshiau
	 */
	public static class Font implements java.io.Serializable {
		private static final long serialVersionUID = 20110104121703L;
		private String font;
		private int fontSize;
		private String fontColor;

		public Font(String font, int fontSize, String fontColor) {
			super();
			this.font = font;
			this.fontSize = fontSize;
			this.fontColor = fontColor;
		}

		/**
		 * Returns the font of the text.
		 * 
		 * @return String
		 */
		public String getFont() {
			return font;
		}

		/**
		 * Returns the font size of the text.
		 * 
		 * @return int
		 */
		public int getFontSize() {
			return fontSize;
		}

		/**
		 * Returns the font color of the text.
		 * 
		 * @return String
		 */
		public String getFontColor() {
			return fontColor;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((font == null) ? 0 : font.hashCode());
			result = prime * result
					+ ((fontColor == null) ? 0 : fontColor.hashCode());
			result = prime * result + fontSize;
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Font other = (Font) obj;
			if (font == null) {
				if (other.font != null)
					return false;
			} else if (!font.equals(other.font))
				return false;
			if (fontColor == null) {
				if (other.fontColor != null)
					return false;
			} else if (!fontColor.equals(other.fontColor))
				return false;
			if (fontSize != other.fontSize)
				return false;
			return true;
		}

		public String toString() {
			return "Font [font=" + font + ", fontSize=" + fontSize
					+ ", fontColor=" + fontColor + "]";
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
	 * 2011
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
