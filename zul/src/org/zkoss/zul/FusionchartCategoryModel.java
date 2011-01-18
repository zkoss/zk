/* FusionchartCategoryModel.java

	Purpose:
		
	Description:
		
	History:
		Dec 30, 2010 5:48:47 PM, Created by jimmyshiau

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul;

import java.util.*;

import org.zkoss.lang.Objects;
import org.zkoss.zul.AbstractChartModel;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Fusionchart.FusionchartData;
import org.zkoss.zul.event.ChartDataEvent;

/**
 * A Category data model implementation of {@link CategoryModel}. A Category
 * model is an N series of (category, value) data objects.
 * 
 * @author jimmyshiau
 * @see CategoryModel
 * @see Fusionchart
 */
public class FusionchartCategoryModel extends AbstractChartModel implements
		CategoryModel {

	private static final long serialVersionUID = 20101230174245L;
	private Map _seriesMap = new HashMap(13); // (series, usecount)
	private List _seriesList = new ArrayList(13);

	private Map _categoryMap = new HashMap(13); // (category, usecount)
	private List _categoryList = new ArrayList(13);

	private Map _valueMap = new LinkedHashMap(79);

	// -- CategoryModel --//
	public Comparable getSeries(int index) {
		return (Comparable) _seriesList.get(index);
	}

	public Comparable getCategory(int index) {
		return (Comparable) _categoryList.get(index);
	}

	public Collection getSeries() {
		return _seriesList;
	}

	public Collection getCategories() {
		return _categoryList;
	}

	public Collection getKeys() {
		return _valueMap.keySet();
	}

	public Number getValue(Comparable series, Comparable category) {
		FusionchartData fd = getFusionchartData(series, category);
		if (fd != null)
			return fd.getValue();
		return null;
	}

	/**
	 * Get the fusionchartData of the specified series and category.
	 * 
	 * @param series
	 *            the series
	 * @param category
	 *            the category.
	 * @return CategoryData
	 */
	public FusionchartData getFusionchartData(Comparable series,
			Comparable category) {
		List key = new ArrayList(2);
		key.add(series);
		key.add(category);
		return (FusionchartData) _valueMap.get(key);
	}

	private void addFusionchartData(Comparable series, Comparable category,
			FusionchartData data) {
		List key = new ArrayList(2);
		key.add(series);
		key.add(category);

		if (!_valueMap.containsKey(key)) {
			if (!_categoryMap.containsKey(category)) {
				_categoryMap.put(category, new Integer(1));
				_categoryList.add(category);
			} else {
				Integer count = (Integer) _categoryMap.get(category);
				_categoryMap.put(category, new Integer(count.intValue() + 1));
			}

			if (!_seriesMap.containsKey(series)) {
				_seriesMap.put(series, new Integer(1));
				_seriesList.add(series);
			} else {
				Integer count = (Integer) _seriesMap.get(series);
				_seriesMap.put(series, new Integer(count.intValue() + 1));

			}
		} else {
			FusionchartData ovalue = (FusionchartData) _valueMap.get(key);
			if (Objects.equals(ovalue, data)) {
				return;
			}
		}

		_valueMap.put(key, data);
		if (series instanceof FusionchartSeries) {
			FusionchartSeries fseries = (FusionchartSeries) series;
			fseries.setCategory(category);
			fseries.setOwner(this);
		}

		if (category instanceof FusionchartCategory) {
			FusionchartCategory fcategory = (FusionchartCategory) category;
			fcategory.setSeries(series);
			fcategory.setOwner(this);
		}
		// bug 2555730: Unnecessary String cast on 'series' in
		// SimpleCategoryModel
		fireEvent(ChartDataEvent.CHANGED, series, category);
	}

	public void setValue(Comparable series, Comparable category, Number value) {
		addFusionchartData(series, category, new FusionchartData(value));
	}

	/**
	 * add or update the value of a specified series and category.
	 * 
	 * @param series
	 *            the series
	 * @param category
	 *            the category
	 * @param value
	 *            the value
	 * @param color
	 *            the color
	 */
	public void setValue(Comparable series, Comparable category, Number value,
			String color) {
		addFusionchartData(series, category, new FusionchartData(value, color));
	}

	/**
	 * add or update the value of a specified series and category.
	 * 
	 * @param series
	 *            the series
	 * @param category
	 *            the category
	 * @param value
	 *            the value
	 * @param color
	 *            the color
	 * @param link
	 *            the link
	 * @param alpha
	 *            the alpha
	 */
	public void setValue(Comparable series, Comparable category, Number value,
			String color, String link, int alpha) {
		addFusionchartData(series, category, new FusionchartData(value, color,
				link, alpha));
	}

	public void removeValue(Comparable series, Comparable category) {
		List key = new ArrayList(2);
		key.add(series);
		key.add(category);
		if (!_valueMap.containsKey(key)) {
			return;
		}

		_valueMap.remove(key);

		int ccount = ((Integer) _categoryMap.get(category)).intValue();
		if (ccount > 1) {
			_categoryMap.put(category, new Integer(ccount - 1));
		} else {
			_categoryMap.remove(category);
			_categoryList.remove(category);
		}

		int scount = ((Integer) _seriesMap.get(series)).intValue();
		if (scount > 1) {
			_seriesMap.put(series, new Integer(scount - 1));
		} else {
			_seriesMap.remove(series);
			_seriesList.remove(series);
		}

		if (series instanceof FusionchartSeries) {
			FusionchartSeries fseries = (FusionchartSeries) series;
			fseries.setCategory(null);
			fseries.setOwner(null);
		}

		if (category instanceof FusionchartCategory) {
			FusionchartCategory fcategory = (FusionchartCategory) category;
			fcategory.setSeries(null);
			fcategory.setOwner(null);
		}
		// bug 2555730: Unnecessary String cast on 'series' in
		// SimpleCategoryModel
		fireEvent(ChartDataEvent.REMOVED, series, category);
	}

	public void clear() {
		_seriesMap.clear();
		_seriesList.clear();
		_categoryMap.clear();
		_categoryList.clear();
		_valueMap.clear();
		fireEvent(ChartDataEvent.REMOVED, null, null);
	}

	/**
	 * A FusionchartCategory in an operation series; a helper class used in
	 * {@link FusionchartCategoryModel}.
	 * 
	 * @author jimmyshiau
	 * @see FusionchartCategoryModel
	 * 
	 */
	public static class FusionchartCategory implements Comparable,
			java.io.Serializable {

		private static final long serialVersionUID = 20110108004522L;
		private String _name;
		private String _hoverText;
		private boolean _showName = true;

		private AbstractChartModel _owner;
		private Comparable _series;

		public FusionchartCategory(String name, String hoverText) {
			super();
			this._name = name;
			this._hoverText = hoverText;
		}

		public FusionchartCategory(String name) {
			super();
			this._name = name;
		}

		public FusionchartCategory(String name, String hoverText,
				boolean showName) {
			super();
			this._name = name;
			this._hoverText = hoverText;
			this._showName = showName;
		}

		/**
		 * Returns the category name which would be displayed on the x-axis as
		 * the data label.
		 * 
		 * @return String
		 */
		public String getName() {
			return _name;
		}

		/**
		 * Sets the category name which would be displayed on the x-axis as the
		 * data label.
		 * 
		 * @param name
		 *            the category name
		 */
		public void setName(String name) {
			if (!Objects.equals(name, _name)) {
				this._name = name;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		/**
		 * Returns the hover text for the category.
		 * 
		 * @return String
		 */
		public String getHoverText() {
			return _hoverText;
		}

		/**
		 * Sets the hover text for the category.
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
		 * Returns wethter the data category name will be displayed on the
		 * chart.
		 * <p>
		 * Default: true.
		 * @return boolean
		 */
		public boolean isShowName() {
			return _showName;
		}

		/**
		 * Sets wethter the data category name will be displayed on the chart.
		 * <p>
		 * Default: true.
		 * @param showName
		 */
		public void setShowName(boolean showName) {
			if (showName != _showName) {
				this._showName = showName;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		private void setSeries(Comparable series) {
			_series = series;
		}

		private void setOwner(AbstractChartModel owner) {
			_owner = owner;
		}

		public int compareTo(Object obj) {
			if (obj instanceof String)
				return _name.compareTo(obj);
			
			if (!(obj instanceof FusionchartCategory))
				throw new ClassCastException(
						"category shall be a String or a FusionchartCategory");
			return _name.compareTo(((FusionchartCategory) obj).getName());
		}
		
		public String toString() {
			return _name;
		}
	}

	/**
	 * A FusionchartSeries in an operation series; a helper class used in
	 * {@link FusionchartCategoryModel}.
	 * 
	 * @author jimmyshiau
	 * @see FusionchartCategoryModel
	 */
	public static class FusionchartSeries implements Comparable,
			java.io.Serializable {
		private static final long serialVersionUID = 20110104121722L;

		private String _seriesName;
		private String _color;
		private boolean _showValues = false;
		private int _alpha = 255;

		private AbstractChartModel _owner;
		private Object _category;

		public FusionchartSeries(String seriesName) {
			super();
			this._seriesName = seriesName;
		}

		public FusionchartSeries(String seriesName, String color) {
			this(seriesName);
			this._color = color;
		}

		public FusionchartSeries(String seriesName, int alpha) {
			super();
			this._seriesName = seriesName;
			this._alpha = alpha;
		}

		public FusionchartSeries(String seriesName, String color, int alpha) {
			this(seriesName, color);
			this._alpha = alpha;
		}

		public FusionchartSeries(String seriesName, String color, int alpha,
				boolean showValues) {
			this(seriesName, color, alpha);
			_showValues = showValues;
		}

		/**
		 * Returns the name of the series name.
		 * 
		 * @return String
		 */
		public String getSeriesName() {
			return _seriesName;
		}

		/**
		 * Sets the name of the series name.
		 * 
		 * @param seriesName
		 */
		public void setSeriesName(String seriesName) {
			if (!Objects.equals(seriesName, _seriesName)) {
				this._seriesName = seriesName;
				fireChartChange();
			}
		}

		/**
		 * Returns the color using which that particular set of data would be
		 * drawn.
		 * 
		 * @return String
		 */
		public String getColor() {
			return _color;
		}

		/**
		 * Sets the color using which that particular set of data would be
		 * drawn.
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
		 * Returns whether the values will be shown alongside the data sets.
		 * <p>
		 * Default: false.
		 * @return boolean
		 */
		public boolean isShowValues() {
			return _showValues;
		}

		/**
		 * Sets whether the values will be shown alongside the data sets.
		 * <p>
		 * Default: false.
		 * @param showValues
		 */
		public void setShowValues(boolean showValues) {
			if (showValues != _showValues) {
				this._showValues = showValues;
				fireChartChange();
			}
		}

		/**
		 * Returns the alpha (transparency) of the entire dataset.
		 * <p>
		 * Default: 255.
		 * @return int
		 */
		public int getAlpha() {
			return _alpha;
		}

		/**
		 * Sets the alpha (transparency) of the entire dataset.
		 * <p>
		 * Default: 255.
		 * @param alpha
		 */
		public void setAlpha(int alpha) {
			if (alpha != _alpha) {
				this._alpha = alpha;
				fireChartChange();
			}
		}
		
		protected void fireChartChange() {
			if (_owner != null)
				_owner.fireEvent(ChartDataEvent.CHANGED, this, _category);
		}

		/*package*/ void setCategory(Object category) {
			_category = category;
		}

		/*package*/ void setOwner(AbstractChartModel owner) {
			_owner = owner;
		}

		public int compareTo(Object obj) {
			if (obj instanceof String)
				return _seriesName.compareTo(obj);

			if (!(obj instanceof FusionchartSeries))
				throw new ClassCastException(
						"series shall be a String or a FusionchartSeries");

			return _seriesName.compareTo(((FusionchartSeries) obj)
					.getSeriesName());
		}

		public String toString() {
			return _seriesName;
		}
	}
	
	/**
	 * A FusionchartAreaSeries in an operation series; a helper class used in
	 * {@link FusionchartCategoryModel}.
	 * 
	 * @author jimmyshiau
	 * @see FusionchartCategoryModel
	 */
	public static class FusionchartAreaSeries extends FusionchartSeries {
		private static final long serialVersionUID = 20110111115522L;
		
		private boolean _showAreaBorder = false;
		private Integer _areaBorderThickness;
		private String _areaBorderColor;
		
		public FusionchartAreaSeries(String seriesName) {
			super(seriesName);
		}

		public FusionchartAreaSeries(String seriesName, String color) {
			super(seriesName, color);
		}
		
		public FusionchartAreaSeries(String seriesName, int alpha) {
			super(seriesName, alpha);
		}

		public FusionchartAreaSeries(String seriesName, String color, int alpha) {
			super(seriesName, color, alpha);
		}

		public FusionchartAreaSeries(String seriesName, String color, int alpha,
				boolean showValues) {
			super(seriesName, color, alpha, showValues);
		}
		
		public FusionchartAreaSeries(String seriesName, String color,
				int alpha, Integer areaBorderThickness,
				String areaBorderColor) {
			super(seriesName, color, alpha);
			this._areaBorderThickness = areaBorderThickness;
			this._areaBorderColor = areaBorderColor;
		}

		public FusionchartAreaSeries(String seriesName, String color,
				int alpha, boolean showValues, boolean showAreaBorder,
				Integer areaBorderThickness, String areaBorderColor) {
			super(seriesName, color, alpha, showValues);
			this._showAreaBorder = showAreaBorder;
			this._areaBorderThickness = areaBorderThickness;
			this._areaBorderColor = areaBorderColor;
		}

		/**
		 * Returns whether the border over the area would be shown or not.
		 * <p>
		 * Default: true.
		 * @return boolean
		 */
		public boolean isShowAreaBorder() {
			return _showAreaBorder;
		}

		/**
		 * Sets whether the border over the area would be shown or not.
		 * <p>
		 * Default: true.
		 * @param showAreaBorder
		 */
		public void setShowAreaBorder(boolean showAreaBorder) {
			if (showAreaBorder != _showAreaBorder) {
				this._showAreaBorder = showAreaBorder;
				fireChartChange();
			}
		}

		/**
		 * Returns the thickness (in pixels) of the area border.
		 * @return Integer
		 */
		public Integer getAreaBorderThickness() {
			return _areaBorderThickness;
		}

		/**
		 * Sets the thickness (in pixels) of the area border.
		 * @param areaBorderThickness
		 */
		public void setAreaBorderThickness(Integer areaBorderThickness) {
			if (!Objects.equals(areaBorderThickness, _areaBorderThickness)) {
				this._areaBorderThickness = areaBorderThickness;
				fireChartChange();
			}
		}

		/**
		 * Returns the color of the area border.
		 * @return String
		 */
		public String getAreaBorderColor() {
			return _areaBorderColor;
		}

		/**
		 * Sets the color of the area border.
		 * @param areaBorderColor
		 */
		public void setAreaBorderColor(String areaBorderColor) {
			if (!Objects.equals(areaBorderColor, _areaBorderColor)) {
				this._areaBorderColor = areaBorderColor;
				fireChartChange();
			}
		}
	}
	
	/**
	 * A FusionchartLineSeries in an operation series; a helper class used in
	 * {@link FusionchartCategoryModel}.
	 * 
	 * @author jimmyshiau
	 * @see FusionchartCategoryModel
	 */
	public static class FusionchartLineSeries extends FusionchartSeries {
		private static final long serialVersionUID = 20110111121822L;
		
		private AnchorProperty _anchorProperty;
		private Integer _lineThickness;
		
		public FusionchartLineSeries(String seriesName) {
			super(seriesName);
		}

		public FusionchartLineSeries(String seriesName, String color) {
			super(seriesName, color);
		}
		
		public FusionchartLineSeries(String seriesName, int alpha) {
			super(seriesName, alpha);
		}

		public FusionchartLineSeries(String seriesName, String color, int alpha) {
			super(seriesName, color, alpha);
		}

		public FusionchartLineSeries(String seriesName, String color, int alpha,
				boolean showValues) {
			super(seriesName, color, alpha, showValues);
		}

		public FusionchartLineSeries(String seriesName, String color,
				int alpha, Integer lineThickness) {
			super(seriesName, color, alpha);
			this._lineThickness = lineThickness;
		}
		
		public FusionchartLineSeries(String seriesName, String color,
				int alpha, Integer lineThickness,
				AnchorProperty _anchorProperty) {
			this(seriesName, color, alpha, lineThickness);
			this._anchorProperty = _anchorProperty;
		}

		public FusionchartLineSeries(String seriesName, String color,
				int alpha, boolean showValues, Integer lineThickness,
				AnchorProperty anchorProperty) {
			super(seriesName, color, alpha, showValues);
			this._lineThickness = lineThickness;
			this._anchorProperty = anchorProperty;
		}

		/**
		 * Returns the anchor of this particular series only.
		 * @return AnchorProperty
		 */
		public AnchorProperty getAnchorProperty() {
			return _anchorProperty;
		}

		/**
		 * Sets the anchor of this particular series only.
		 * @param anchorProperty
		 */
		public void setAnchorProperty(AnchorProperty anchorProperty) {
			if (!Objects.equals(anchorProperty, _anchorProperty)) {
				this._anchorProperty = anchorProperty;
				fireChartChange();
			}
		}

		/**
		 * Returns the thickness of the line (in pixels).
		 * @return Integer
		 */
		public Integer getLineThickness() {
			return _lineThickness;
		}

		/**
		 * Sets the thickness of the line (in pixels).
		 * @param lineThickness
		 */
		public void setLineThickness(Integer lineThickness) {
			if (!Objects.equals(lineThickness, _lineThickness)) {
				this._lineThickness = lineThickness;
				fireChartChange();
			}
		}
	}
	
	/**
	 * A FusionchartCombinationSeries in an operation series; a helper class
	 * used in {@link FusionchartCategoryModel}.
	 * 
	 * @author jimmyshiau
	 * @see FusionchartCategoryModel
	 */
	public static class FusionchartCombinSeries extends
			FusionchartLineSeries {
		private static final long serialVersionUID = 20110111122522L;
		
		private String _parentYAxis = "P";
		private String _numberPrefix;
		private String _numberSuffix;

		public FusionchartCombinSeries(String seriesName) {
			super(seriesName);
		}

		public FusionchartCombinSeries(String seriesName, String color) {
			super(seriesName, color);
		}
		
		public FusionchartCombinSeries(String seriesName, int alpha) {
			super(seriesName, alpha);
		}
		
		public FusionchartCombinSeries(String seriesName, int alpha,
				String parentYAxis) {
			super(seriesName, alpha);
			this._parentYAxis = parentYAxis;
		}
		
		public FusionchartCombinSeries(String seriesName, int alpha,
				String parentYAxis, String numberPrefix, String numberSuffix) {
			super(seriesName, alpha);
			this._parentYAxis = parentYAxis;
			this._numberPrefix = numberPrefix;
			this._numberSuffix = numberSuffix;
		}

		public FusionchartCombinSeries(String seriesName, String color,
				int alpha) {
			super(seriesName, color, alpha);
		}

		public FusionchartCombinSeries(String seriesName, String color,
				int alpha, boolean showValues) {
			super(seriesName, color, alpha, showValues);
		}

		public FusionchartCombinSeries(String seriesName, String color,
				int alpha, Integer lineThickness) {
			super(seriesName, color, alpha, lineThickness);
		}

		public FusionchartCombinSeries(String seriesName, String color,
				int alpha, boolean showValues, Integer lineThickness,
				AnchorProperty anchorProperty) {
			super(seriesName, color, alpha, showValues, lineThickness,
					anchorProperty);
		}
		
		public FusionchartCombinSeries(String seriesName, String color,
				int alpha, boolean showValues, Integer lineThickness,
				AnchorProperty anchorProperty, String parentYAxis,
				String numberPrefix, String numberSuffix) {
			super(seriesName, color, alpha, showValues, lineThickness,
					anchorProperty);
			this._parentYAxis = parentYAxis;
			this._numberPrefix = numberPrefix;
			this._numberSuffix = numberSuffix;
		}

		/**
		 * Returns the parentYAxis in the combination chart. This attribute can
		 * take a value of P or S, whereas P denoting primary axis and S
		 * denoting secondary axis.
		 * 
		 * @return String
		 */
		public String getParentYAxis() {
			return _parentYAxis;
		}

		/**
		 * Returns the parentYAxis in the combination chart. This attribute can
		 * take a value of P or S, whereas P denoting primary axis and S
		 * denoting secondary axis.
		 * <p>
		 * Allowed values: P, S.
		 * 
		 * @param parentYAxis
		 */
		public void setParentYAxis(String parentYAxis) {
			if (!Objects.equals(parentYAxis, _parentYAxis)) {
				this._parentYAxis = parentYAxis;
				fireChartChange();
			}
		}

		/**
		 * Returns the prefix to all the numbers for this dataset.
		 * 
		 * @return String
		 */
		public String getNumberPrefix() {
			return _numberPrefix;
		}

		/**
		 * Sets the prefix to all the numbers for this dataset.
		 * 
		 * @param numberPrefix
		 */
		public void setNumberPrefix(String numberPrefix) {
			if (!Objects.equals(numberPrefix, _numberPrefix)) {
				this._numberPrefix = numberPrefix;
				fireChartChange();
			}
		}

		/**
		 * Returns the suffix to all the numbers for this dataset.
		 * 
		 * @return String
		 */
		public String getNumberSuffix() {
			return _numberSuffix;
		}

		/**
		 * Sets the suffix to all the numbers for this dataset.
		 * 
		 * @param numberSuffix
		 */
		public void setNumberSuffix(String numberSuffix) {
			if (!Objects.equals(numberSuffix, _numberSuffix)) {
				this._numberSuffix = numberSuffix;
				fireChartChange();
			}
		}
	}
	
	
	/**
	 * A Anchor class for store all of anchor attributes.; a helper class used
	 * in {@link FusionchartCategory}.
	 * 
	 * @author jimmyshiau
	 */
	public static class AnchorProperty implements java.io.Serializable {
		private static final long serialVersionUID = 20110104122522L;

		private boolean _showAnchors = true;
		private Integer _sides;
		private Integer _radius;
		private String _borderColor;
		private Integer _borderThickness;
		private String _bgColor;
		private int _bgAlpha = 255;
		private int _alpha = 255;

		public AnchorProperty(Integer sides, Integer radius) {
			super();
			this._sides = sides;
			this._radius = radius;
		}

		public AnchorProperty(Integer sides, Integer radius,
				String borderColor, String bgColor) {
			super();
			this._sides = sides;
			this._radius = radius;
			this._borderColor = borderColor;
			this._bgColor = bgColor;
		}

		public AnchorProperty(boolean showAnchors, Integer sides, Integer radius,
				String borderColor, Integer borderThickness,
				String bgColor, int bgAlpha, int alpha) {
			super();
			this._showAnchors = showAnchors;
			this._sides = sides;
			this._radius = radius;
			this._borderColor = borderColor;
			this._borderThickness = borderThickness;
			this._bgColor = bgColor;
			this._bgAlpha = bgAlpha;
			this._alpha = alpha;
		}

		/**
		 * Returns whether the anchors would be shown for this dataset or not.
		 * If the anchors are not shown, then the hover caption and link
		 * functions won't work.
		 * <p>
		 * Default: true.
		 * @return boolean
		 */
		public boolean isShowAnchors() {
			return _showAnchors;
		}

		/**
		 * Returns the number of sides the anchors (of this dataset) will have.
		 * For e.g., an anchor with 3 sides would represent a triangle, with 4
		 * it would be a square and so on.
		 * 
		 * @return int
		 */
		public Integer getSides() {
			return _sides;
		}

		/**
		 * Returns the radius (in pixels) of the anchor. Greater the radius,
		 * bigger would be the anchor size.
		 * 
		 * @return int
		 */
		public Integer getRadius() {
			return _radius;
		}

		/**
		 * Returns the border Color of the anchor.
		 * 
		 * @return String
		 */
		public String getBorderColor() {
			return _borderColor;
		}

		/**
		 * Returns the thickness of the anchor border (in pixels).
		 * 
		 * @return int
		 */
		public Integer getBorderThickness() {
			return _borderThickness;
		}

		/**
		 * Returns the background color of the anchor.
		 * 
		 * @return String
		 */
		public String getBgColor() {
			return _bgColor;
		}

		/**
		 * Returns the alpha of the anchor background.
		 * 
		 * @return int
		 */
		public int getBgAlpha() {
			return _bgAlpha;
		}

		/**
		 * Returns the transparency of the entire anchor (including the border).
		 * This attribute is particularly useful, when you do not want the
		 * anchors to be visible on the chart, but you want the hover caption
		 * and link functionality. In that case, you can set anchorAlpha to 0.
		 * 
		 * @return int
		 */
		public int getAlpha() {
			return _alpha;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + _alpha;
			result = prime * result + _bgAlpha;
			result = prime * result
					+ ((_bgColor == null) ? 0 : _bgColor.hashCode());
			result = prime * result
					+ ((_borderColor == null) ? 0 : _borderColor.hashCode());
			result = prime
					* result
					+ ((_borderThickness == null) ? 0 : _borderThickness
							.hashCode());
			result = prime * result
					+ ((_radius == null) ? 0 : _radius.hashCode());
			result = prime * result + (_showAnchors ? 1231 : 1237);
			result = prime * result
					+ ((_sides == null) ? 0 : _sides.hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AnchorProperty other = (AnchorProperty) obj;
			if (_alpha != other._alpha)
				return false;
			if (_bgAlpha != other._bgAlpha)
				return false;
			if (_bgColor == null) {
				if (other._bgColor != null)
					return false;
			} else if (!_bgColor.equals(other._bgColor))
				return false;
			if (_borderColor == null) {
				if (other._borderColor != null)
					return false;
			} else if (!_borderColor.equals(other._borderColor))
				return false;
			if (_borderThickness == null) {
				if (other._borderThickness != null)
					return false;
			} else if (!_borderThickness.equals(other._borderThickness))
				return false;
			if (_radius == null) {
				if (other._radius != null)
					return false;
			} else if (!_radius.equals(other._radius))
				return false;
			if (_showAnchors != other._showAnchors)
				return false;
			if (_sides == null) {
				if (other._sides != null)
					return false;
			} else if (!_sides.equals(other._sides))
				return false;
			return true;
		}

		public String toString() {
			return "Anchor [_showAnchors=" + _showAnchors + ", _sides="
					+ _sides + ", _radius=" + _radius
					+ ", _borderColor=" + _borderColor
					+ ", _borderThickness=" + _borderThickness
					+ ", _bgColor=" + _bgColor
					+ ", _bgAlpha=" + _bgAlpha + ", _alpha="
					+ _alpha + "]";
		}
	}

}
