/* Piechart.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 1 15:38:08     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;

import com.potix.zul.html.event.ChartDataEvent;
import com.potix.zul.html.event.ChartDataListener;
import com.potix.image.AImage;

import org.jfree.chart.*;
import org.jfree.chart.encoders.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.entity.*;
import org.jfree.data.general.*;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.awt.Paint;
import java.util.Map.Entry;


/**
 * A pie chart class written with JFreeChart engine.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Piechart extends Chart {
	private ChartDataListener _dataListener;
	
	public Piechart() {
		setPreRenderer(new PiechartAreaRenderer());
		
	}

	/**
	 * Native data interface.
	 */
	public void setDataset(PieDataset dataset) {
		setInnerDataset(dataset);
	}
	
	/**
	 * Native data interface.
	 */
	public PieDataset getDataset() {
		return (PieDataset) getInnerDataset();
	}
	
	/**
	 * 
	 */
	public void setModel(ChartModel model) {
		if (!(model instanceof PiechartModel) && model != null) {
			throw new ClassCastException(model.getClass().getName());
		}
		final ChartModel omodel = getModel();
		if (omodel != model) {
			initDataListener();
			if (omodel != null) {
				omodel.removeChartDataListener(_dataListener);
			}
			if (model != null) {
				model.addChartDataListener(_dataListener);
				
				if (getDataset() == null) { //dataset is not populated yet
					DefaultPieDataset pieDataset = new DefaultPieDataset();
					final int size = model.getSize(0);
					for(int j=0; j < size; ++j) {
						final Object[] data = (Object[]) model.getData(0, j);
						pieDataset.setValue((String) data[0], (Number) data[1]);
					}
					setDataset(pieDataset);
				}
			}
		}
		super.setModel(model);
	}

	private void initDataListener() {
		if (_dataListener == null) {
			_dataListener = new ChartDataListener() {
				public void onChange(ChartDataEvent event) {
					final DefaultPieDataset pieDataset = (DefaultPieDataset) getDataset();
					if (pieDataset != null) {
						int type = event.getType();
						Object[] data = (Object[])event.getData();
						if (type == ChartDataEvent.REMOVED) {
							pieDataset.remove((String)data[0]);
						} else {
							pieDataset.setValue((String)data[0], (Number) data[1]);
						}
					}
					smartDrawChart();
				}
			};
		}
	}

	
	protected JFreeChart createChart() {
		return ChartFactory.createPieChart(getTitle(), getDataset(), isShowLegend(), isShowTooltiptext(), true);
	}
	
	public class PiechartAreaRenderer implements AreaRenderer {
		public void render(Area area, Object data) throws Exception {
			if (data instanceof PieSectionEntity) {
				area.setAttribute("entity", "PIE");
				decodeURL(area, ((ChartEntity)data).getURLText());
			} else {
				area.setAttribute("entity", "TITLE");
				if (isShowTooltiptext()) {
					area.setTooltiptext(getTitle());
				}
			}
		}
	}	
}
