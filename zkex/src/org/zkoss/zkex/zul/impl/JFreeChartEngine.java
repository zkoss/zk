/* JFreeChartEngine.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 1 10:30:48     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkex.zul.impl;

import org.zkoss.zul.*;
import org.zkoss.zul.impl.ChartEngine;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.image.AImage;
import org.zkoss.util.TimeZones;
import org.zkoss.lang.Strings;
import org.zkoss.lang.Objects;

import org.jfree.chart.*;
import org.jfree.chart.encoders.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.entity.*;
import org.jfree.data.general.*;
import org.jfree.data.category.*;
import org.jfree.data.xy.*;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.util.TableOrder;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.TimeZone;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Transparency;
import java.util.Iterator;


/**
 * A chart engine implemented with JFreeChart.
 *
 * <p>This is the JFreeChart base chart engine implementation. All chart would 
 * support drilldown by providing Area hot spots. Each Area would callback to 
 * {@link org.zkoss.zul.event.ChartAreaListener} class that application developers
 * can do processing on each area.</p>
 *
 * <p>Note that useful {@link org.zkoss.zul.ChartModel} information is put in 
 * Area's custom attribute Map so you can retrieve them by calling Area's 
 * getAttribute(key) method and use them in drilldown function. Following is 
 * the table of keys for different ChartModel.</p>
 *
 * <table>
 *   <tr><th>model</th><th>key</th></tr>
 *   <tr><td>{@link PieModel}</td><td>entity</td></tr>
 *   <tr><td></td><td>category</td></tr>
 *   <tr><td></td><td>value</td></tr>
 *	
 *   <tr><td>{@link CategoryModel}</td><td>entity</td></tr>
 *   <tr><td></td><td>series</td></tr>
 *   <tr><td></td><td>category</td></tr>
 *   <tr><td></td><td>value</td></tr>
 *
 *   <tr><td>{@link XYModel}</td><td>entity</td></tr>
 *   <tr><td></td><td>series</td></tr>
 *   <tr><td></td><td>x</td></tr>
 *   <tr><td></td><td>y</td></tr>
 *
 *   <tr><td>{@link HiLoModel}</td><td>entity</td></tr>
 *   <tr><td></td><td>series</td></tr>
 *   <tr><td></td><td>date</td></tr>
 *   <tr><td></td><td>open</td></tr>
 *   <tr><td></td><td>high</td></tr>
 *   <tr><td></td><td>low</td></tr>
 *   <tr><td></td><td>close</td></tr>
 *   <tr><td></td><td>volumn</td></tr>
 * </table>
 * <p>Following is the explanation for each key:</p>
 * <ul>
 * <li>entity: entity is used to distinguish the differnt entities on a
 * chart. It might be TITLE, CATEGORY, DATA, LEGEND. The most important 
 * entity might be DATA entity where the real chart data point is located. 
 * For example, the DATA Area is each slice in a Pie chart.</li>
 * <li>category: category name of the associated data.</li>
 * <li>value: value of the associated data.</li>
 * <li>series: series name of the associated data.</li>
 * <li>x: x value of the XYModel</li>
 * <li>y: y value of the XYModel</li>
 * <li>date, open, high, low, close, volumn: data of the HiLoModel</li>
 * </ul>
 *
 * <p>See also <a href="http://www.jfree.org/jfreechart">jFreeChart</a>.
 * @author henrichen
 * @since 3.0.0
 */
public class JFreeChartEngine implements ChartEngine, java.io.Serializable {
	//as long as the series name is not set
	private static final String DEFAULT_HI_LO_SERIES = "High Low Data";
	
	//caching chartImpl if type and 3d are the same 
	private transient boolean _threeD; 
	private transient String _type; 
	private transient ChartImpl _chartImpl; //chart implementaion
	
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

		else if (Chart.CANDLESTICK.equals(chart.getType()))			
			_chartImpl = new Candlestick();

		else if (Chart.HIGHLOW.equals(chart.getType()))			
			_chartImpl = new Highlow();

		else 
			throw new UiException("Unsupported chart type yet: "+chart.getType());

		_threeD = chart.isThreeD();
		_type = chart.getType();
		return _chartImpl;
	}
	
	/**
	* Developers with special needs can override this method to apply own rendering properties on the created JFreeChart. The
	* original implementation of this method simply return false to tell the engine to apply its default rendering properties. Return true
	* tells the engine NOT to apply its default rendering properties.
	*
	* @param jfchart the created JFreeChart
	* @param chart the ZK chart component associated with this ChartEngine.
	* @return true to tell this engine NOT to apply its default rendering properties; false to tell this engine TO apply its default rendering properties.
	* @since 3.0.1
	*/
	protected boolean prepareJFreeChart(JFreeChart jfchart, Chart chart) {
		return false; //request original engine to also do the setting.
	}

	//-- ChartEngine --//
	public byte[] drawChart(Object data) {
		Chart chart = (Chart) data;
		ChartImpl impl = getChartImpl(chart);

		JFreeChart jfchart = impl.createChart(chart);

		//feature#1814621 
		//call prepareJFreeChart so developer can override it and do their own parameter settings
		if (!prepareJFreeChart(jfchart, chart)) {
			Plot plot = (Plot) jfchart.getPlot();
			float alpha = (float)(((float)chart.getFgAlpha()) / 255);
			plot.setForegroundAlpha(alpha);
			
			alpha = (float)(((float)chart.getBgAlpha()) / 255);
			plot.setBackgroundAlpha(alpha);

			int[] bgRGB = chart.getBgRGB();
			if (bgRGB != null) {
				plot.setBackgroundPaint(new Color(bgRGB[0], bgRGB[1], bgRGB[2], chart.getBgAlpha()));
			}

	        int[] paneRGB = chart.getPaneRGB();
	        if (paneRGB != null) {
	            jfchart.setBackgroundPaint(new Color(paneRGB[0], paneRGB[1], paneRGB[2], chart.getPaneAlpha()));
	        }
		}
		
		//callbacks for each area
		ChartRenderingInfo jfinfo = new ChartRenderingInfo();
		BufferedImage bi = jfchart.createBufferedImage(chart.getIntWidth(), chart.getIntHeight(), Transparency.TRANSLUCENT, jfinfo);
		
		//remove old areas 	
		chart.getChildren().clear();
		
		int j = 0;
		String preUrl = null;
		for(Iterator it=jfinfo.getEntityCollection().iterator();it.hasNext();) {
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
		    area.setId("area_"+chart.getId()+'_'+(j++));
		    if (chart.isShowTooltiptext() && ce.getToolTipText() != null) {
		    	area.setTooltiptext(ce.getToolTipText());
		    }
		    area.setAttribute("url", ce.getURLText());
		    impl.render(chart, area, ce);
			if (chart.getAreaListener() != null) {
				try {
					chart.getAreaListener().onRender(area, ce);
				} catch (Exception ex) {
					throw UiException.Aide.wrap(ex);
				}
			}
		}
		
		//clean up the "LEGEND_SEQ"
		//used for workaround LegendItemEntity.getSeries() always return 0
		//used for workaround TickLabelEntity no information
	    chart.removeAttribute("LEGEND_SEQ"); 
	    chart.removeAttribute("TICK_SEQ"); 

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
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (final Iterator it = model.getCategories().iterator(); it.hasNext();) {
			Comparable category = (Comparable) it.next();
			Number value = model.getValue(category);
			dataset.setValue(category, value);
		}
		return dataset;
	}

	/**
	 * transfer a CategoryModel into JFreeChart PieDataset.
	 */
	private PieDataset CategoryModelToPieDataset(CategoryModel model) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		Comparable defaultSeries = null;
		int max = 0;
		for (final Iterator it = model.getKeys().iterator(); it.hasNext();) {
			final List key = (List) it.next();
			Comparable series = (Comparable) key.get(0);
			if (defaultSeries == null) {
				defaultSeries = series;
				max = model.getCategories().size();
			}
			if (!Objects.equals(defaultSeries, series)) {
				continue;
			}
			Comparable category = (Comparable) key.get(1);
			Number value = (Number) model.getValue(series, category);
			dataset.setValue(category, value);

			if (--max == 0) break; //no more in this series
		}
		return dataset;
	}

	/**
	 * transfer a CategoryModel into JFreeChart CategoryDataset.
	 */
	private CategoryDataset CategoryModelToCategoryDataset(CategoryModel model) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (final Iterator it = model.getKeys().iterator(); it.hasNext();) {
			final List key = (List) it.next();
			Comparable series = (Comparable) key.get(0);
			Comparable category = (Comparable) key.get(1);
			Number value = (Number) model.getValue(series, category);
			dataset.setValue(value, series, category);
		}
		return dataset;
	}

	/**
	 * transfer a XYModel into JFreeChart XYSeriesCollection.
	 */
	private XYDataset XYModelToXYDataset(XYModel model) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		for (final Iterator it = model.getSeries().iterator(); it.hasNext();) {
			final Comparable series = (Comparable) it.next();
			XYSeries xyser = new XYSeries(series, model.isAutoSort());
			final int size = model.getDataCount(series);
			for(int j = 0; j < size; ++j) {
				xyser.add(model.getX(series, j), model.getY(series, j), false);
			}
			dataset.addSeries(xyser);
		}
		return dataset;
	}

	/**
	 * transfer a XYModel into JFreeChart DefaultTableXYDataset.
	 */
	private TableXYDataset XYModelToTableXYDataset(XYModel model) {
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
	}

	/**
	 * transfer a XYModel into JFreeChart TimeSeriesCollection.
	 */
	private XYDataset XYModelToTimeDataset(XYModel model, Chart chart) {
		TimeZone tz = chart.getTimeZone();
		if (tz == null) tz = TimeZones.getCurrent();
		String p = chart.getPeriod();
		if (p == null) p = Chart.MILLISECOND;
		Class pclass = (Class) _periodMap.get(p);
		if (pclass == null) {
			throw new UiException("Unsupported period for Time Series chart: "+p);
		}
		TimeSeriesCollection dataset = new TimeSeriesCollection(tz);
		
		for (final Iterator it = model.getSeries().iterator(); it.hasNext();) {
			final Comparable series = (Comparable) it.next();
			final org.jfree.data.time.TimeSeries tser = 
				new org.jfree.data.time.TimeSeries(series, pclass);
			final int size = model.getDataCount(series);
			for(int j = 0; j < size; ++j) {
				final RegularTimePeriod period = RegularTimePeriod.createInstance(
					pclass, new Date(model.getX(series, j).longValue()), tz);
				tser.addOrUpdate(period, model.getY(series, j));
			}
			dataset.addSeries(tser);
		}
		return dataset;
	}

	private static Map _periodMap = new HashMap(10);	
	static {
		_periodMap.put(Chart.MILLISECOND, org.jfree.data.time.Millisecond.class);
		_periodMap.put(Chart.SECOND, org.jfree.data.time.Second.class);
		_periodMap.put(Chart.MINUTE, org.jfree.data.time.Minute.class);
		_periodMap.put(Chart.HOUR, org.jfree.data.time.Hour.class);
		_periodMap.put(Chart.DAY, org.jfree.data.time.Day.class);
		_periodMap.put(Chart.WEEK, org.jfree.data.time.Week.class);
		_periodMap.put(Chart.MONTH, org.jfree.data.time.Month.class);
		_periodMap.put(Chart.QUARTER, org.jfree.data.time.Quarter.class);
		_periodMap.put(Chart.YEAR, org.jfree.data.time.Year.class);
	}

	/**
	 * transfer a HiLoModel into JFreeChart DefaultOHLCDataset
	 */
	private OHLCDataset HiLoModelToOHLCDataset(HiLoModel model) {
		final int size = model.getDataCount();
		final OHLCDataItem[] items = new OHLCDataItem[size];
		
		for(int j = 0; j < size; ++j) {
			Date date = model.getDate(j);
			Number open = model.getOpen(j);
			Number high = model.getHigh(j);
			Number low = model.getLow(j);
			Number close = model.getClose(j);
			Number volume = model.getVolume(j);
			
			OHLCDataItem item = new OHLCDataItem(date, 
				doubleValue(open), doubleValue(high), 
				doubleValue(low), doubleValue(close), 
				doubleValue(volume));
			items[j] = item;
		}
		
		Comparable series = model.getSeries();
		if (series == null) {
			series = DEFAULT_HI_LO_SERIES;
		}
		return new DefaultOHLCDataset(series, items);
	}
	
	private double doubleValue(Number n) {
		return n == null ? 0.0 : n.doubleValue();
	}

	/**
	 * decode LegendItemEntity into key-value pair of Area's componentScope.
	 * @param area the Area where the final attribute is set
	 * @param info the LegendItemEntity to be decoded.
	 */
	private void decodeLegendInfo(Area area, LegendItemEntity info, Chart chart) {
		if (info == null) {
			return;
		}
		final ChartModel model = chart.getModel(); 
		final int seq = ((Integer)chart.getAttribute("LEGEND_SEQ")).intValue();
		
		if (model instanceof PieModel) {
			Comparable category = ((PieModel)model).getCategory(seq);
			area.setAttribute("category", category);
			area.setAttribute("value", ((PieModel)model).getValue(category));
		    if (chart.isShowTooltiptext() && info.getToolTipText() == null) {
		    	area.setTooltiptext(category.toString());
		    }
		} else if (model instanceof CategoryModel) {
			Comparable series = ((CategoryModel)model).getSeries(seq);
			area.setAttribute("series", series);
		    if (chart.isShowTooltiptext() && info.getToolTipText() == null) {
		    	area.setTooltiptext(series.toString());
		    }
		} else if (model instanceof XYModel) {
			Comparable series = ((XYModel)model).getSeries(seq);
			area.setAttribute("series", series);
		    if (chart.isShowTooltiptext() && info.getToolTipText() == null) {
		    	area.setTooltiptext(series.toString());
		    }
		} else if (model instanceof HiLoModel) {
			Comparable series = ((HiLoModel)model).getSeries();
			if (series == null) {
				series = DEFAULT_HI_LO_SERIES;
			}
			area.setAttribute("series", series);
		    if (chart.isShowTooltiptext() && info.getToolTipText() == null) {
		    	area.setTooltiptext(series.toString());
		    }
		}
	}

	/**
	 * decode TickLabelEntity into key-value pair of Area's componentScope.
	 * @param area the Area where the final attribute is set
	 * @param info the TickLabelEntity to be decoded.
	 */
	private void decodeTickLabelInfo(Area area, TickLabelEntity info, Chart chart) {
		if (info == null) {
			return;
		}
		final ChartModel model = chart.getModel(); 
		final int seq = ((Integer)chart.getAttribute("TICK_SEQ")).intValue();
		
		if (model instanceof CategoryModel) {
			Comparable category = ((CategoryModel)model).getCategory(seq);
			area.setAttribute("category", category);
		    if (chart.isShowTooltiptext() && info.getToolTipText() == null) {
		    	area.setTooltiptext(category.toString());
		    }
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
		Comparable category = info.getColumnKey();
		Comparable series = info.getRowKey();
		
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
		
		if (dataset instanceof OHLCDataset) {
			OHLCDataset ds = (OHLCDataset) dataset;
			area.setAttribute("date", new Date(ds.getX(si, ii).longValue()));
			area.setAttribute("open", ds.getOpen(si, ii));
			area.setAttribute("high", ds.getHigh(si, ii));
			area.setAttribute("low", ds.getLow(si, ii));
			area.setAttribute("close", ds.getClose(si, ii));
			area.setAttribute("volume", ds.getVolume(si, ii));
		} else {
			area.setAttribute("x", dataset.getX(si, ii));
			area.setAttribute("y", dataset.getY(si, ii));
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
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof PieSectionEntity) {
				area.setAttribute("entity", "DATA");
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
				throw new UiException("model must be a org.zkoss.zul.PieModel or a org.zkoss.zul.CategoryModel");
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
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity) info);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer)chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("TICK_SEQ", seq);
				decodeTickLabelInfo(area, (TickLabelEntity) info, chart);
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
				throw new UiException("model must be a org.zkoss.zul.CategoryModel or a org.zkoss.zul.XYModel");
			}
		}
	}

	/** bar3d chart */
	private class Bar3d extends Bar {
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a org.zkoss.zul.CategoryModel");
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
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity) info);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer)chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("TICK_SEQ", seq);
				decodeTickLabelInfo(area, (TickLabelEntity) info, chart);
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
				throw new UiException("model must be a org.zkoss.zul.CategoryModel or a org.zkoss.zul.XYModel");
			}
				
		}
	}

	/** line chart */
	private class Line extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity) info);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer)chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("TICK_SEQ", seq);
				decodeTickLabelInfo(area, (TickLabelEntity) info, chart);
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
				throw new UiException("model must be a org.zkoss.zul.CategoryModel or a org.zkoss.zul.XYModel");
			}
		}
	}

	/** line3d chart */
	private class Line3d extends Line {
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a org.zkoss.zul.CategoryModel");
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
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer)chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("TICK_SEQ", seq);
				decodeTickLabelInfo(area, (TickLabelEntity) info, chart);
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
				throw new UiException("model must be a org.zkoss.zul.CategoryModel");
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
				throw new UiException("model must be a org.zkoss.zul.CategoryModel");
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
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity) info);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer)chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("TICK_SEQ", seq);
				decodeTickLabelInfo(area, (TickLabelEntity) info, chart);
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
				throw new UiException("model must be a org.zkoss.zul.CategoryModel or a org.zkoss.zul.XYModel");
			}
		}
	}

	/** waterfall chart */
	//note: cuttin area corrdinate is not correct.
	private class Waterfall extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer)chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("TICK_SEQ", seq);
				decodeTickLabelInfo(area, (TickLabelEntity) info, chart);
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
				throw new UiException("model must be a org.zkoss.zul.CategoryModel");
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
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
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
				throw new UiException("model must be a org.zkoss.zul.XYModel");
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
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
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
				throw new UiException("model must be a org.zkoss.zul.XYModel");
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
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
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
				throw new UiException("model must be a org.zkoss.zul.XYModel");
			}
			return ChartFactory.createTimeSeriesChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				XYModelToTimeDataset((XYModel)model, chart),
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** steparea chart */
	private class StepArea extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
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
				throw new UiException("model must be a org.zkoss.zul.XYModel");
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
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
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
				throw new UiException("model must be a org.zkoss.zul.XYModel");
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
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
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
				throw new UiException("model must be a org.zkoss.zul.XYModel");
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

	/** candlestick */
	private class Candlestick extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
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
			if (!(model instanceof HiLoModel)) {
				throw new UiException("model must be a org.zkoss.zul.HiLoModel");
			}
			return ChartFactory.createCandlestickChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				HiLoModelToOHLCDataset((HiLoModel)model),
				chart.isShowLegend());
		}
	}

	/** highlow */
	private class Highlow extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
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
			if (!(model instanceof HiLoModel)) {
				throw new UiException("model must be a org.zkoss.zul.HiLoModel");
			}
			return ChartFactory.createHighLowChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				HiLoModelToOHLCDataset((HiLoModel)model),
				chart.isShowLegend());
		}
	}

	private PlotOrientation getOrientation(String orient) {
		return "horizontal".equals(orient) ?
			PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL;
	}
}
