/* FusionchartPieModel.java

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.lang.Objects;
import org.zkoss.zul.AbstractChartModel;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.Fusionchart.FusionchartData;
import org.zkoss.zul.event.ChartDataEvent;

/**
 * A Pie chart data model implementation of {@link PieModel}. Piechart model is
 * an one series of (Category, value) data objects.
 * 
 * @author jimmyshiau
 * @see PieModel
 * @see Fusionchart
 */
public class FusionchartPieModel extends AbstractChartModel implements PieModel {

	private static final long serialVersionUID = 20101230150456L;
	private List _categoryList = new ArrayList(13);
	private Map _categoryMap = new HashMap(13);

	// -- PieModel --//
	public Comparable getCategory(int index) {
		return (Comparable) _categoryList.get(index);
	}

	public Collection getCategories() {
		return _categoryList;
	}

	public Number getValue(Comparable category) {
		FusionchartPieData pd = (FusionchartPieData) _categoryMap.get(category);
		if (pd != null)
			return pd.getValue();
		return null;
	}

	/**
	 * Get the pieData of the specified series and category.
	 * 
	 * @param category
	 *            the category
	 * @return PieData
	 */
	public FusionchartPieData getPieData(Comparable category) {
		return (FusionchartPieData) _categoryMap.get(category);
	}

	private void addPieData(Comparable category, FusionchartPieData pieData) {
		if (!_categoryMap.containsKey(category)) {
			_categoryList.add(category);
		} else {
			FusionchartPieData ovalue = (FusionchartPieData) _categoryMap
					.get(category);
			if (Objects.equals(ovalue, pieData)) {
				return;
			}
		}
		_categoryMap.put(category, pieData);
		fireEvent(ChartDataEvent.CHANGED, null, category);
	}

	public void setValue(Comparable category, Number value) {
		addPieData(category, new FusionchartPieData(value));
	}

	/**
	 * add or update the value of a specified series and category.
	 * 
	 * @param category
	 *            the category
	 * @param value
	 *            the value
	 * @param color
	 *            the color
	 */
	public void setValue(Comparable category, Number value, String color) {
		addPieData(category, new FusionchartPieData(value, color));
	}

	/**
	 * add or update the value of a specified series and category.
	 * 
	 * @param category
	 *            the category
	 * @param value
	 *            the value
	 * @param color
	 *            the color
	 * @param hoverText
	 *            the hoverText
	 * @param alpha
	 *            the alpha
	 * @param link
	 *            the link
	 * @param sliced
	 *            the sliced
	 */
	public void setValue(Comparable category, Number value, String color,
			String hoverText, int alpha, String link, boolean sliced) {
		addPieData(category, new FusionchartPieData(value, color, hoverText,
				alpha, link, sliced));
	}

	public void removeValue(Comparable category) {
		_categoryMap.remove(category);
		_categoryList.remove(category);
		fireEvent(ChartDataEvent.REMOVED, null, category);
	}

	public void clear() {
		_categoryMap.clear();
		_categoryList.clear();
		fireEvent(ChartDataEvent.REMOVED, null, null);
	}

	/**
	 * A PieData in an operation series; a helper class used in
	 * {@link FusionchartPieModel}.
	 * 
	 * @author jimmyshiau
	 * @see FusionchartPieModel
	 */
	/* package */ static class FusionchartPieData extends FusionchartData {

		private static final long serialVersionUID = 20110104121300L;
		private String hoverText;
		private boolean sliced = false;

		public FusionchartPieData(Number value) {
			super(value);
		}

		public FusionchartPieData(Number value, String color) {
			super(value, color);
		}

		public FusionchartPieData(Number value, String color, String link,
				int alpha, String hoverText) {
			super(value, color, link, alpha);
			this.hoverText = hoverText;
		}

		public FusionchartPieData(Number value, String color, String hoverText,
				int alpha, String link, boolean sliced) {
			this(value, color, link, alpha, hoverText);
			this.sliced = sliced;
		}

		/**
		 * Returns the hover text for the set of data according to which the
		 * chart would be built for the concerned set of data.
		 * 
		 * @return String
		 */
		public String getHoverText() {
			return hoverText;
		}

		/**
		 * Returns whether the pie appears as a part of the total circle or is
		 * sliced out as an individual item (highlighted).
		 * <p>
		 * Default: false.
		 * 
		 * @return boolean
		 */
		public boolean isSliced() {
			return sliced;
		}

		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result
					+ ((hoverText == null) ? 0 : hoverText.hashCode());
			result = prime * result + (sliced ? 1231 : 1237);
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			FusionchartPieData other = (FusionchartPieData) obj;
			if (hoverText == null) {
				if (other.hoverText != null)
					return false;
			} else if (!hoverText.equals(other.hoverText))
				return false;
			if (sliced != other.sliced)
				return false;
			return true;
		}

		public String toString() {
			return "FusionchartPieData [value=" + getValue() + ", color="
					+ getColor() + ", link=" + getLink() + ", hoverText="
					+ hoverText + ", alpha=" + getAlpha() + ", sliced="
					+ sliced + "]";

		}
	}
}
