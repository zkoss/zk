/* SimpleChartEngine.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 1 10:30:48     2006, Created by henrichen@potix.com
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
import com.potix.image.AImage;
import com.potix.lang.Strings;
import com.potix.lang.Objects;

import org.jfree.chart.*;
import org.jfree.chart.encoders.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.entity.*;
import org.jfree.data.general.*;
import org.jfree.data.category.*;
import org.jfree.util.TableOrder;

import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.awt.Paint;
import java.awt.Color;
import java.util.Iterator;


/**
 * A Facade Chart engine implemented with JFreeChart.
 *
 * <p>This is the JFreeChart base chart engine implementation. All chart would 
 * support drilldown by providing Area hot spots. Each Area would callback to 
 * {@link com.potix.zul.html.AreaRenderer} class that application developers
 * can do processing on each area.</p>
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 */
public class SimpleChartEngine implements ChartEngine {
	//caching chartImpl if type and 3d are the same
	private boolean _threeD;
	private String _type; 
	private ChartImpl _chartImpl; //chart implementaion
	
	/**
	 * create specific type of chart drawing engine. This implementation use 
	 * JFreeChart engine.
	 */
	private ChartImpl getChartImpl(Chart chart) {
		if (Objects.equals(chart.getType(), _type) && _threeD == chart.isThreeD()) {
			return _chartImpl;
		}

		if (Chart.PIE.equals(chart.getType())) 
			_chartImpl = chart.isThreeD() ? new Pie3d() : new Pie();

		else if (Chart.RING.equals(chart.getType()))
			_chartImpl = new Ring();
					
		else if (Chart.BAR.equals(chart.getType()))
			_chartImpl = chart.isThreeD() ? new Bar3d() : new Bar();
		
		else if (Chart.LINE.equals(chart.getType()))
			_chartImpl = chart.isThreeD() ? new Line3d() : new Line();
			
		else if (Chart.AREA.equals(chart.getType()))
			_chartImpl = new AreaImpl();
					
		else if (Chart.STACKED_BAR.equals(chart.getType()))
			_chartImpl = chart.isThreeD() ? new StackedBar3d() : new StackedBar();

		else if (Chart.STACKED_AREA.equals(chart.getType()))
			_chartImpl = new StackedArea();
					
		else if (Chart.WATERFALL.equals(chart.getType()))
			_chartImpl = new Waterfall();
					
		else 
			throw new UiException("Unsupported chart type yet: "+chart.getType());

		_threeD = chart.isThreeD();
		_type = chart.getType();
		return _chartImpl;
	}

	//-- ChartEngine --//
	public byte[] drawChart(Object data) {
		Chart chart = (Chart) data;
		ChartImpl impl = getChartImpl(chart);

		JFreeChart jfchart = impl.createChart(chart);
		
		Plot plot = (Plot) jfchart.getPlot();
		float alpha = (float)(((float)chart.getFgAlpha()) / 255);
		plot.setForegroundAlpha(alpha);
		
		alpha = (float)(((float)chart.getBgAlpha()) / 255);
		plot.setBackgroundAlpha(alpha);
		
		int[] bgRGB = chart.getBgRGB();
		if (bgRGB != null) {
			plot.setBackgroundPaint(new Color(bgRGB[0], bgRGB[1], bgRGB[2], chart.getBgAlpha()));
		}

		//callbacks for each area
		ChartRenderingInfo jfinfo = new ChartRenderingInfo();
		BufferedImage bi = jfchart.createBufferedImage(chart.getIntWidth(), chart.getIntHeight(), BufferedImage.TRANSLUCENT, jfinfo);
		
		//remove old areas 	
		chart.getChildren().clear();
		
		int j = 0;
		String preUrl = null;
		for(Iterator it=jfinfo.getEntityCollection().getEntities().iterator();it.hasNext();) {
			ChartEntity ce = ( ChartEntity ) it.next();
			final String url = ce.getURLText();

			//workaround JFreeChart's bug (skip replicate areas)
			if (url != null) { 
				if (preUrl == null) {
					preUrl = url;
				} else if (url.equals(preUrl)) { //start replicate, skip
					break;
				}
			}
			
			Area area = new Area();
			area.setParent(chart);
		 	area.setCoords(ce.getShapeCoords());
		 	area.setShape(ce.getShapeType());
		    area.setId(chart.getId()+'_'+(j++));
		    if (chart.isShowTooltiptext() && ce.getToolTipText() != null) {
		    	area.setTooltiptext(ce.getToolTipText());
		    }
		    area.setAttribute("url", ce.getURLText());
		    impl.render(chart, area, ce);
			if (chart.getAreaRenderer() != null) {
				try {
					chart.getAreaRenderer().render(area, ce);
				} catch (Exception ex) {
					throw UiException.Aide.wrap(ex);
				}
			}
		}

		try {		
			//encode into png image format byte array
			return EncoderUtil.encode(bi, ImageFormat.PNG, true);
		} catch(java.io.IOException ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	
	//-- utilities --//
	/**
	 * decode url requests into key-value pair of Area's componentScope.
	 * @param area the Area where the final attribute is set
	 * @param url the url to be decoded.
	 */
	private void decodeURL(Area area, String url) {
		if (url == null) {
			return;
		}
		int j = url.indexOf("?");
		if (j < 0) {
			return;
		}
		url = url.substring(j+1);
		
		do {
			j = url.indexOf("&amp;");
			if (j < 0) {
				storePair(area, url);
				break;
			} else {
				String pair = url.substring(0, j);
				storePair(area, pair);
				if ((j+5) >= url.length()) {
					break; //no more
				}
				url = url.substring(j+5);
			}
		} while(true);
	}
	
	private void storePair(Area area, String url) {
		int j = url.indexOf("=");
		if (j > 0) {
			area.setAttribute(url.substring(0, j), url.substring(j+1));
		}
	}
	
	//-- Chart specific implementation --//
	/** base chart */
	abstract private class ChartImpl {
		abstract void render(Chart chart, Area area, ChartEntity info);
		abstract JFreeChart createChart(Chart chart);
	}
	
	/** pie chart */
	private class Pie extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof PieSectionEntity) {
				area.setAttribute("entity", "PIE");
				decodeURL(area, info.getURLText());
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			PieDataset dataset = null;
			if (model instanceof CategoryModel) {
				dataset = new CategoryToPieDataset((CategoryDataset) model.getNativeModel(), TableOrder.BY_ROW, 0);
			} else if (model instanceof PieModel) {
				dataset = (PieDataset) model.getNativeModel();
			} else {
				throw new UiException("model must be a com.potix.zul.html.PieModel");
			}
			return ChartFactory.createPieChart(
				chart.getTitle(), 
				(PieDataset) dataset, 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}
	
	/** pie3d chart */
	private class Pie3d extends Pie {
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			PieDataset dataset = null;
			if (model instanceof CategoryModel) {
				dataset = new CategoryToPieDataset((CategoryDataset) model.getNativeModel(), TableOrder.BY_ROW, 0);
			} else if (model instanceof PieModel) {
				dataset = (PieDataset) model.getNativeModel();
			} else {
				throw new UiException("model must be a com.potix.zul.html.PieModel");
			}
			return ChartFactory.createPieChart3D(
				chart.getTitle(), 
				(PieDataset) dataset, 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}		
		
	/** ring chart */
	private class Ring extends Pie {
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			PieDataset dataset = null;
			if (model instanceof CategoryModel) {
				dataset = new CategoryToPieDataset((CategoryDataset) model.getNativeModel(), TableOrder.BY_ROW, 0);
			} else if (model instanceof PieModel) {
				dataset = (PieDataset) model.getNativeModel();
			} else {
				throw new UiException("model must be a com.potix.zul.html.PieModel");
			}
			return ChartFactory.createRingChart(
				chart.getTitle(), 
				(PieDataset) dataset, 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}		

	/** bar chart */
	private class Bar extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "BAR");
				decodeURL(area, info.getURLText());
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				decodeURL(area, info.getURLText());
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a com.potix.zul.html.CategoryModel");
			}
			return ChartFactory.createBarChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				(CategoryDataset) model.getNativeModel(),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** bar3d chart */
	private class Bar3d extends Bar {
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a com.potix.zul.html.CategoryModel");
			}
			return ChartFactory.createBarChart3D(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				(CategoryDataset) model.getNativeModel(), 
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}
	
	/** area chart */
	private class AreaImpl extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "AREA");
				decodeURL(area, info.getURLText());
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				decodeURL(area, info.getURLText());
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a com.potix.zul.html.CategoryModel");
			}
			return ChartFactory.createAreaChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				(CategoryDataset) model.getNativeModel(),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** line chart */
	private class Line extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "POINT");
				decodeURL(area, info.getURLText());
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				decodeURL(area, info.getURLText());
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a com.potix.zul.html.CategoryModel");
			}
			return ChartFactory.createLineChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				(CategoryDataset) model.getNativeModel(),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** line3d chart */
	private class Line3d extends Line {
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a com.potix.zul.html.CategoryModel");
			}
			return ChartFactory.createLineChart3D(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				(CategoryDataset) model.getNativeModel(), 
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** stackedbar chart */
	private class StackedBar extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "BAR");
				decodeURL(area, info.getURLText());
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				decodeURL(area, info.getURLText());
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a com.potix.zul.html.CategoryModel");
			}
			return ChartFactory.createStackedBarChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				(CategoryDataset) model.getNativeModel(),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** stackedbar3d chart */
	private class StackedBar3d extends StackedBar {
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a com.potix.zul.html.CategoryModel");
			}
			return ChartFactory.createStackedBarChart3D(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				(CategoryDataset) model.getNativeModel(), 
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** stackedarea chart */
	//note: cutting area coordinate is not correct.
	private class StackedArea extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "POINT");
				decodeURL(area, info.getURLText());
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				decodeURL(area, info.getURLText());
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a com.potix.zul.html.CategoryModel");
			}
			return ChartFactory.createStackedAreaChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				(CategoryDataset) model.getNativeModel(),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** waterfall chart */
	//note: cuttin area corrdinate is not correct.
	private class Waterfall extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "BAR");
				decodeURL(area, info.getURLText());
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				decodeURL(area, info.getURLText());
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a com.potix.zul.html.CategoryModel");
			}
			return ChartFactory.createWaterfallChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				(CategoryDataset) model.getNativeModel(),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	private PlotOrientation getOrientation(String orient) {
		return "horizontal".equals(orient) ?
			PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL;
	}
}
