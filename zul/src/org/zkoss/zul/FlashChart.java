/* Flashchart.java

	Purpose:

	Description:

	History:
		Nov 26, 2009 11:58:42 AM , Created by joy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.zkoss.json.JSONObject;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.event.ChartDataEvent;
import org.zkoss.zul.event.ChartDataListener;
/**
 * The generic flash chart component. Developers set proper chart type, data model,
 * and the src attribute to draw proper chart. The model and type must match to each other;
 * or the result is unpredictable.
 *
 * <table>
 *   <tr><th>type</th><th>model</th></tr>
 *   <tr><td>pie</td><td>{@link PieModel}</td></tr>
 *   <tr><td>bar</td><td>{@link CategoryModel}</td></tr>
 *   <tr><td>line</td><td>{@link CategoryModel}</td></tr>
 *   <tr><td>column</td><td>{@link CategoryModel}</td></tr>
 * </table>
 *
 * <p>Default {@link #getWidth}: 400px
 * <p>Default {@link #getHeight}: 200px
 *
 * @author Joy Lo
 * @date Created at Nov 20, 2009 4:37:26 PM
 * @since 5.0.0
 */
public class Flashchart extends Flash implements org.zkoss.zul.api.Flashchart {

	private static final long serialVersionUID = 20091126115842L;
	/**
	 * Declares attributes.
	 */
	private String _type = "pie";
	private String _styles;
	private ChartModel _model;
	private ChartDataListener _dataListener;
	/**
	 * Sets default values.
	 */
	public Flashchart() {
		setWidth("400px");
		setHeight("200px");
	}
	private class MyChartDataListener implements ChartDataListener, Serializable {
		private static final long serialVersionUID = 20091125153002L;
		public void onChange(ChartDataEvent event) {
			smartDrawChart();
		}
	}
	/**
	 * Refresh data when the chart data changed.
	 */
	protected void smartDrawChart() {
		smartUpdate("jsonModel", getJSONResponse(transferToJSONObject(getModel())));
		invalidate();
	}
	/**
	 * RenderProperties method will bind the attributes with FlashChart.js.
	 */
	protected void renderProperties(ContentRenderer renderer) throws IOException {
		super.renderProperties(renderer);
		render(renderer, "type", _type);
		render(renderer, "jsonModel", getJSONResponse(transferToJSONObject(getModel())));
//		render(renderer, "jsonStyle", getJSONResponse(transferToJSONObject(getStyles())));
	}
	/**
	 * Sets the type of chart.
	 * <p>Default: "pie"
	 * <p>Types: pie, line, bar, column
	 */
	public void setType(String type) {
		if (!Objects.equals(_type, type)) {
			_type = type;
			smartUpdate("type", _type);
		}
	}
	/**
	 * Returns the type of chart
	 */
	public String getType() {
		return _type;
	}
	/**
	 * Sets the model of chart.
	 * <p>Only implement models which matched the allowed types
	 * @param model
	 * @see #setType(String)
	 */
	public void setModel(ChartModel model) {
		if(_model != model){
			if (_model != null)
				_model.removeChartDataListener(_dataListener);

			_model = model;

			if (_dataListener == null) {
				_dataListener = new MyChartDataListener();
				_model.addChartDataListener(_dataListener);
			}
			
		}
		smartDrawChart();		//Refresh the data
	}
	/**
	 * Returns the model of chart.
	 */
	public ChartModel getModel() {
		return _model;
	}		
	/**
	 * Sets the style of chart
	 * @param styles
	 */
	public void setStyles(String styles) {
		if (!Objects.equals(_styles, styles)) {
			_styles = styles;
			smartUpdate("styles", _styles);
		}		
	}
	/**
	 * Returns a string which prepares to use in javascript as a chart style
	 */
	public String getStyles() {
		return _styles;
	}
	
	private List transferToJSONObject(String styles){
		LinkedList list = new LinkedList();
		//TODO:
		return null;		
	}
	
	private List transferToJSONObject(ChartModel model){
		LinkedList list = new LinkedList();

		if(model != null && _type != null){
			if("pie".equals(_type)) {	//Draw PieChart
				PieModel tempModel = (PieModel)model;
				for(int i = 0; i < tempModel.getCategories().size(); i++){
					Comparable category = tempModel.getCategory(i);
					JSONObject json = new JSONObject();
					json.put("categoryField", category);
					json.put("dataField", tempModel.getValue(category));
					list.add(json);
				}
			} else if("bar".equals(_type) || "line".equals(_type) || "column".equals(_type)) {
				CategoryModel tempModel = (CategoryModel)model;
				for(int i = 0; i < tempModel.getCategories().size(); i++){
					Comparable category = tempModel.getCategory(i);
					Comparable series = tempModel.getSeries(i);
					JSONObject json = new JSONObject();
					if("line".equals(_type) || "column".equals(_type)){		//Draw LineChart & ColumnChart
						json.put("horizontalField", series);
						json.put("verticalField", tempModel.getValue(series, category));
					} else {		//Draw BarChart
						json.put("horizontalField", tempModel.getValue(series, category));
						json.put("verticalField", series);
					}
					list.add(json);
				}
			}
		};
		return list;
	}
	private String getJSONResponse(List list) {
	    final StringBuffer sb = new StringBuffer().append('[');
	    for (Iterator it = list.iterator(); it.hasNext();) {
            sb.append(it.next()).append(',');
	    }
	    sb.deleteCharAt(sb.length() - 1);
	    sb.append(']');
	    return sb.toString();
	}
}