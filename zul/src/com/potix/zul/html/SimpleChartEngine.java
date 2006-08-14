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
import org.jfree.data.xy.*;
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
					
		else if (Chart.POLAR.equals(chart.getType()))
			_chartImpl = new Polar();

		else if (Chart.SCATTER.equals(chart.getType()))
			_chartImpl = new Scatter();

		else if (Chart.TIME_SERIES.equals(chart.getType()))
			_chartImpl = new TimeSeries();

		else if (Chart.STEP_AREA.equals(chart.getType()))
			_chartImpl = new StepArea();

		else if (Chart.STEP.equals(chart.getType()))
			_chartImpl = new Step();
			
		else if (Chart.HISTOGRAM.equals(chart.getType()))			
			_chartImpl = new Histogram();

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
	 * transfer a PieModel into JFreeChart PieDataset.
	 */
	private PieDataset PieModelToPieDataset(PieModel model) {
		if (model.getNativeModel() == null) {
			DefaultPieDataset dataset = new DefaultPieDataset();
			for (final Iterator it = model.getCategories().iterator(); it.hasNext();) {
				Comparable category = (Comparable) it.next();
				Number value = model.getValue(category);
				dataset.setValue(category, value);
			}
			return dataset;
		} else {
			return (PieDataset) model.getNativeModel();
		}
	}

	/**
	 * transfer a CategoryModel into JFreeChart PieDataset.
	 */
	private PieDataset CategoryModelToPieDataset(CategoryModel model) {
		if (model.getNativeModel() == null) {
			DefaultPieDataset dataset = new DefaultPieDataset();
			for (final Iterator it = model.getSeries().iterator(); it.hasNext();) {
				final Comparable series = (Comparable) it.next();
				for(final Iterator itc = model.getCategories(series).iterator(); itc.hasNext();) {
					Comparable category = (Comparable) itc.next();
					Number value = model.getValue(series, category);
					dataset.setValue(category, value);
				}
				break; //first series only
			}
			return dataset;
		} else {
			return new CategoryToPieDataset((CategoryDataset) model.getNativeModel(), TableOrder.BY_ROW, 0);
		}
	}

	/**
	 * transfer a CategoryModel into JFreeChart CategoryDataset.
	 */
	private CategoryDataset CategoryModelToCategoryDataset(CategoryModel model) {
		if (model.getNativeModel() == null) {
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			for (final Iterator it = model.getSeries().iterator(); it.hasNext();) {
				final Comparable series = (Comparable) it.next();
				for(final Iterator itc = model.getCategories(series).iterator(); itc.hasNext();) {
					Comparable category = (Comparable) itc.next();
					Number value = model.getValue(series, category);
					dataset.setValue(value, series, category);
				}
			}
			return dataset;
		} else {
			return (CategoryDataset) model.getNativeModel();
		}
	}

	/**
	 * transfer a XYModel into JFreeChart XYSeriesCollection.
	 */
	private XYDataset XYModelToXYDataset(XYModel model) {
		if (model.getNativeModel() == null) {
			XYSeriesCollection dataset = new XYSeriesCollection();
			for (final Iterator it = model.getSeries().iterator(); it.hasNext();) {
				final Comparable series = (Comparable) it.next();
				XYSeries xyser = new XYSeries(series);
				final int size = model.getDataCount(series);
				for(int j = 0; j < size; ++j) {
					xyser.add(model.getX(series, j), model.getY(series, j), false);
				}
				dataset.addSeries(xyser);
			}
			return dataset;
		} else {
			return (XYDataset) model.getNativeModel();
		}
	}

	/**
	 * transfer a XYModel into JFreeChart DefaultTableXYDataset.
	 */
	private TableXYDataset XYModelToTableXYDataset(XYModel model) {
		if (model.getNativeModel() == null) {
			DefaultTableXYDataset dataset = new DefaultTableXYDataset();
			for (final Iterator it = model.getSeries().iterator(); it.hasNext();) {
				final Comparable series = (Comparable) it.next();
				XYSeries xyser = new XYSeries(series, false, false);
				final int size = model.getDataCount(series);
				for(int j = 0; j < size; ++j) {
					xyser.add(model.getX(series, j), model.getY(series, j), false);
				}
				dataset.addSeries(xyser);
			}
			return dataset;
		} else {
			return (TableXYDataset) model.getNativeModel();
		}
	}

	/**
	 * decode PieSectionEntity into key-value pair of Area's componentScope.
	 * @param area the Area where the final attribute is set
	 * @param info the PieSectionEntity to be decoded.
	 */
	private void decodePieInfo(Area area, PieSectionEntity info) {
		if (info == null) {
			return;
		}
		
		PieDataset dataset = info.getDataset();
		Comparable category = info.getSectionKey();
		area.setAttribute("category", category);
		area.setAttribute("value", dataset.getValue(category));
	}
	
	/**
	 * decode CategoryItemEntity into key-value pair of Area's componentScope.
	 * @param area the Area where the final attribute is set
	 * @param info the CategoryItemEntity to be decoded.
	 */
	private void decodeCategoryInfo(Area area, CategoryItemEntity info) {
		if (info == null) {
			return;
		}
		
		CategoryDataset dataset = info.getDataset();
		int si = info.getSeries();
		Comparable category = (Comparable) info.getCategory();
		Comparable series = (Comparable) dataset.getRowKey(si);
		
		area.setAttribute("series", series);
		area.setAttribute("category", category);
		area.setAttribute("value", dataset.getValue(series, category));
	}

	/**
	 * decode XYItemEntity into key-value pair of Area's componentScope.
	 * @param area the Area where the final attribute is set
	 * @param info the XYItemEntity to be decoded.
	 */
	private void decodeXYInfo(Area area, XYItemEntity info) {
		if (info == null) {
			return;
		}
		
		XYDataset dataset = info.getDataset();
		int si = info.getSeriesIndex();
		int ii = info.getItem();
		
		area.setAttribute("series", dataset.getSeriesKey(si));
		area.setAttribute("x", dataset.getX(si, ii));
		area.setAttribute("y", dataset.getY(si, ii));
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
				decodePieInfo(area, (PieSectionEntity)info);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		
		protected PieDataset getDataset(ChartModel model) {
			if (model instanceof CategoryModel) {
				return CategoryModelToPieDataset((CategoryModel)model);
			} else if (model instanceof PieModel) {
				return PieModelToPieDataset((PieModel) model);
			} else {
				throw new UiException("model must be a com.potix.zul.html.PieModel or a com.potix.zul.html.CategoryModel");
			}
		}			
		
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			return ChartFactory.createPieChart(
				chart.getTitle(), 
				getDataset(model), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}
	
	/** pie3d chart */
	private class Pie3d extends Pie {
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			return ChartFactory.createPieChart3D(
				chart.getTitle(), 
				getDataset(model), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}		
		
	/** ring chart */
	private class Ring extends Pie {
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			return ChartFactory.createRingChart(
				chart.getTitle(), 
				getDataset(model), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}		

	/** bar chart */
	private class Bar extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "BAR");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "BAR");
				decodeXYInfo(area, (XYItemEntity) info);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (model instanceof CategoryModel) {
				return ChartFactory.createBarChart(
					chart.getTitle(),
					chart.getXAxis(),
					chart.getYAxis(),
					CategoryModelToCategoryDataset((CategoryModel)model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else if (model instanceof XYModel) {
				return ChartFactory.createXYBarChart(
					chart.getTitle(),
					chart.getXAxis(),
					false,
					chart.getYAxis(),
					(IntervalXYDataset) XYModelToXYDataset((XYModel)model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else {
				throw new UiException("model must be a com.potix.zul.html.CategoryModel or a com.potix.zul.html.XYModel");
			}
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
				CategoryModelToCategoryDataset((CategoryModel)model),
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
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "AREA");
				decodeXYInfo(area, (XYItemEntity) info);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (model instanceof CategoryModel) {
				return ChartFactory.createAreaChart(
					chart.getTitle(),
					chart.getXAxis(),
					chart.getYAxis(),
					CategoryModelToCategoryDataset((CategoryModel)model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else if (model instanceof XYModel) {
				return ChartFactory.createXYAreaChart(
					chart.getTitle(),
					chart.getXAxis(),
					chart.getYAxis(),
					XYModelToXYDataset((XYModel)model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else {
				throw new UiException("model must be a com.potix.zul.html.CategoryModel or a com.potix.zul.html.XYModel");
			}
				
		}
	}

	/** line chart */
	private class Line extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "POINT");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "POINT");
				decodeXYInfo(area, (XYItemEntity) info);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (model instanceof CategoryModel) {
				return ChartFactory.createLineChart(
					chart.getTitle(),
					chart.getXAxis(),
					chart.getYAxis(),
					CategoryModelToCategoryDataset((CategoryModel) model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else if (model instanceof XYModel) {
				return ChartFactory.createXYLineChart(
					chart.getTitle(),
					chart.getXAxis(),
					chart.getYAxis(),
					XYModelToXYDataset((XYModel) model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else {
				throw new UiException("model must be a com.potix.zul.html.CategoryModel or a com.potix.zul.html.XYModel");
			}
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
				CategoryModelToCategoryDataset((CategoryModel) model),
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
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
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
				CategoryModelToCategoryDataset((CategoryModel)model),
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
				CategoryModelToCategoryDataset((CategoryModel)model),
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
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "POINT");
				decodeXYInfo(area, (XYItemEntity) info);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (model instanceof CategoryModel) {
				return ChartFactory.createStackedAreaChart(
					chart.getTitle(),
					chart.getXAxis(),
					chart.getYAxis(),
					CategoryModelToCategoryDataset((CategoryModel)model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else if (model instanceof XYModel) {
				return ChartFactory.createStackedXYAreaChart(
					chart.getTitle(),
					chart.getXAxis(),
					chart.getYAxis(),
					XYModelToTableXYDataset((XYModel) model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else {
				throw new UiException("model must be a com.potix.zul.html.CategoryModel or a com.potix.zul.html.XYModel");
			}
		}
	}

	/** waterfall chart */
	//note: cuttin area corrdinate is not correct.
	private class Waterfall extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "BAR");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
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
				CategoryModelToCategoryDataset((CategoryModel)model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** polar chart */
	private class Polar extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			area.setAttribute("entity", "TITLE");
			if (chart.isShowTooltiptext()) {
				area.setTooltiptext(chart.getTitle());
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a com.potix.zul.html.XYModel");
			}
			return ChartFactory.createPolarChart(
				chart.getTitle(),
				XYModelToXYDataset((XYModel)model),
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** scatter chart */
	private class Scatter extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "POINT");
				decodeXYInfo(area, (XYItemEntity)info);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a com.potix.zul.html.XYModel");
			}
			return ChartFactory.createScatterPlot(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				XYModelToXYDataset((XYModel)model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** timeseries chart */
	private class TimeSeries extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "POINT");
				decodeXYInfo(area, (XYItemEntity) info);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a com.potix.zul.html.XYModel");
			}
			return ChartFactory.createTimeSeriesChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				XYModelToXYDataset((XYModel)model),
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** steparea chart */
	private class StepArea extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "POINT");
				decodeXYInfo(area, (XYItemEntity)info);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a com.potix.zul.html.XYModel");
			}
			return ChartFactory.createXYStepAreaChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				XYModelToXYDataset((XYModel)model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** step chart */
	private class Step extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "POINT");
				decodeXYInfo(area, (XYItemEntity)info);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a com.potix.zul.html.XYModel");
			}
			return ChartFactory.createXYStepChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				XYModelToXYDataset((XYModel)model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}
	
	/** histogram */
	private class Histogram extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "BAR");
				decodeXYInfo(area, (XYItemEntity)info);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a com.potix.zul.html.XYModel");
			}
			return ChartFactory.createHistogram(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				(IntervalXYDataset)XYModelToXYDataset((XYModel)model),
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
