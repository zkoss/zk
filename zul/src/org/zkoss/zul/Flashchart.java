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
 *   <tr><td>stackbar</td><td>{@link CategoryModel}</td></tr>
 *   <tr><td>stackcolumn</td><td>{@link XYModel}</td></tr>
 * </table>
 *
 * <p>Default {@link #getWidth}: 400px
 * <p>Default {@link #getHeight}: 200px
 *
 * @author Joy Lo
 * @since 5.0.0
 */
public class Flashchart extends Flash implements org.zkoss.zul.api.Flashchart {

	private static final long serialVersionUID = 20091126115842L;
	/**
	 * Declares attributes.
	 */
	private String _type = "pie";
	private String _chartStyle;
	private ChartModel _model;
	private ChartDataListener _dataListener;
	private LinkedList _seriesList;
	private String _xAxis;
	private String _yAxis;
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
			refresh();
		}
	}	
	private final void refresh(){
		smartUpdate("refresh", getJSONResponse(transferToJSONObject(getModel())));
	}
	/**
	 * RenderProperties method will bind the attributes with FlashChart.js.
	 */
	protected void renderProperties(ContentRenderer renderer) throws IOException {
		super.renderProperties(renderer);
		render(renderer, "type", _type.split(":")[0]);
		if(_chartStyle != null)
			render(renderer, "chartStyle", _chartStyle);
		render(renderer, "jsonModel", getJSONResponse(transferToJSONObject(getModel())));
		if(_type.startsWith("stackbar") || _type.startsWith("stackcolumn"))
			render(renderer, "jsonSeries", getJSONResponse(_seriesList));
	}
	/**
	 * Sets the type of chart.
	 * <p>Default: "pie"
	 * <p>Allowed Types: pie, line, bar, column, stackbar, stackcolumn
	 */
	public void setType(String type) {
		if (!Objects.equals(_type, type)) {
			_type = type;
			smartUpdate("type", _type.split(":")[0]);
		}
	}
	/**
	 * Returns the type of chart
	 */
	public String getType() {
		return _type;
	}
	/**
	 * Sets the model of chart. The chart will be redrawed if setting an different model.
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
			invalidate();		//Always redraw
		}		
	}
	/**
	 * Returns the model of chart.
	 */
	public ChartModel getModel() {
		return _model;
	}
	/**
	 * Sets X-Axis name of chart
	 * <p>Only used for StackColumnChart
	 */
	public void setXAxis(String xAxis) {
		_xAxis = xAxis;
	}
	/**
	 * Returns the name of X-Axis
	 */
	public String getXAxis() {
		return _xAxis;
	}
	/**
	 * Sets Y-Axis name of chart
	 * <p>Only used for StackColumnChart
	 */
	public void setYAxis(String yAxis) {
		_yAxis = yAxis;
	}
	/**
	 * Returns the name of Y-Axis
	 */
	public String getYAxis() {
		return _yAxis;
	}
	/**
	 * Sets the style of swf
	 * <p>Default format: "Category-Attribute=Value", ex."legend-display=right"
	 */
	public void setChartStyle(String chartStyle) {
		if (!Objects.equals(_chartStyle, chartStyle)) {
			_chartStyle = chartStyle;
			smartUpdate("chartStyle", _chartStyle);
		}
	}
	/**
	 * Returns the swf style
	 */
	public String getChartStyle() {
		return _chartStyle;
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
						json.put("horizontalField", category);
						json.put("verticalField", tempModel.getValue(series, category));
					} else {		//Draw BarChart
						json.put("horizontalField", tempModel.getValue(series, category));
						json.put("verticalField", category);
					}
					list.add(json);
				}
			} else if(_type.startsWith("stackbar")){		//Draw StackedBarChart
				_seriesList = new LinkedList();
				CategoryModel tempModel = (CategoryModel)model;
				for(int i = 0; i < tempModel.getCategories().size(); i++){
					Comparable series = tempModel.getSeries(i);	
					JSONObject json = new JSONObject();			
					json.put("xField", series);
					json.put("displayName", series);
					_seriesList.add(json);
				}
				for(int i = 0; i < tempModel.getCategories().size(); i++){
					Comparable category = tempModel.getCategory(i);					
					JSONObject jData = new JSONObject();
					jData.put("verticalField", category);
					for(int j = 0; j < _seriesList.size(); j++){
						Comparable series = tempModel.getSeries(j);	
						JSONObject temp = (JSONObject) _seriesList.get(j);						
						jData.put(temp.get("xField"), tempModel.getValue(series, category));
					}
					list.add(jData);					
				}
			} else if(_type.startsWith("stackcolumn")){		//Draw StackedColumnChart 
				_seriesList = new LinkedList();
				XYModel tempModel = (XYModel)model;
				for(int i = 0; i < tempModel.getSeries().size(); i++){
					Comparable series = tempModel.getSeries(i);					
					JSONObject jData = new JSONObject();
					jData.put("horizontalField", series);
					jData.put(_xAxis, tempModel.getX(series, 0));
					jData.put(_yAxis, tempModel.getY(series, 0));
					list.add(jData);
				}
				for(int j = 0; j < 2; j++){
					JSONObject json = new JSONObject();
					String tempType = _type.split(":")[1];
					if(j == 0){
						json.put("type", tempType.split(",")[0]);
						json.put("displayName", _yAxis);
						json.put("yField", _yAxis);
					} else {
						json.put("type", tempType.split(",")[1]);
						json.put("displayName", _xAxis);
						json.put("yField", _xAxis);
					}
					_seriesList.add(json);
				}
			}
		};
		return list;
	}
		
	private String getJSONResponse(List list) {
	    final StringBuffer sb = new StringBuffer().append('[');
	    for (Iterator it = list.iterator(); it.hasNext();) {
	    	String s = String.valueOf(it.next());
            sb.append(s).append(',');
	    }
	    sb.deleteCharAt(sb.length() - 1);
	    sb.append(']');
	    return sb.toString().replace("\\", "");
	}
}