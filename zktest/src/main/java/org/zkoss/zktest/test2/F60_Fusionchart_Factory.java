package org.zkoss.zktest.test2;

import java.util.Date;

import org.zkoss.zkmax.zul.fusionchart.GanttTableRenderer;
import org.zkoss.zkmax.zul.fusionchart.config.CategoriesPropertiesMap;
import org.zkoss.zkmax.zul.fusionchart.config.CategoryChartConfig;
import org.zkoss.zkmax.zul.fusionchart.config.GanttChartConfig;
import org.zkoss.zkmax.zul.fusionchart.config.GanttChartSeriesPropertiesMap;
import org.zkoss.zkmax.zul.fusionchart.config.GanttTableConfig;
import org.zkoss.zkmax.zul.fusionchart.config.PieChartConfig;
import org.zkoss.zkmax.zul.fusionchart.config.SeriesPropertiesMap;
import org.zkoss.zkmax.zul.fusionchart.config.XYChartConfig;
import org.zkoss.zkmax.zul.fusionchart.config.GanttChartCategoriesPropertiesList.GanttChartCategoriesProperties;
import org.zkoss.zkmax.zul.fusionchart.config.GanttTableConfig.GanttRowPropertiesList;
import org.zkoss.zkmax.zul.fusionchart.config.GanttTableConfig.GanttTableColumnPropertiesMap;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.GanttModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.SimplePieModel;
import org.zkoss.zul.SimpleXYModel;
import org.zkoss.zul.XYModel;
import org.zkoss.zul.GanttModel.GanttTask;

public class F60_Fusionchart_Factory {

	public static PieModel createPieModel() {
		PieModel piemodel = new SimplePieModel();
		piemodel.setValue("C/C++", new Double(12.5));
		piemodel.setValue("Java", new Double(50.2));
		piemodel.setValue("VB", new Double(20.5));
		piemodel.setValue("PHP", new Double(15.5));
		return piemodel;
	}

	public static XYModel createXYModel() {
		XYModel xymodel = new SimpleXYModel();
		xymodel.addValue("2001", new Integer(10), new Integer(1459));
		xymodel.addValue("2001", new Integer(20), new Integer(1423));
		xymodel.addValue("2001", new Integer(30), new Integer(1382));
		xymodel.addValue("2001", new Integer(40), new Integer(1356));
		xymodel.addValue("2001", new Integer(50), new Integer(1310));
		xymodel.addValue("2001", new Integer(60), new Integer(1282));
		xymodel.addValue("2001", new Integer(70), new Integer(1247));
		xymodel.addValue("2001", new Integer(80), new Integer(1182));

		xymodel.addValue("2002", new Integer(10), new Integer(1188));
		xymodel.addValue("2002", new Integer(20), new Integer(1189));
		xymodel.addValue("2002", new Integer(30), new Integer(1177));
		xymodel.addValue("2002", new Integer(40), new Integer(1175));
		xymodel.addValue("2002", new Integer(50), new Integer(1210));
		xymodel.addValue("2002", new Integer(60), new Integer(1280));
		xymodel.addValue("2002", new Integer(70), new Integer(1390));
		xymodel.addValue("2002", new Integer(80), new Integer(1524));
		return xymodel;
	}

	public static GanttModel createGanttModel() {
		GanttModel ganttmodel = new GanttModel();

		ganttmodel.addValue(
				"Scheduled",
				new GanttTask("Write Proposal", date(2008, 4, 1), date(2008, 4,
						5), 0.0));
		ganttmodel.addValue("Scheduled", new GanttTask("Requirements Analysis",
				date(2008, 4, 10), date(2008, 5, 5), 0.0));
		ganttmodel.addValue(
				"Scheduled",
				new GanttTask("Design Phase", date(2008, 5, 6), date(2008, 5,
						30), 0.0));
		ganttmodel.addValue("Scheduled", new GanttTask("Alpha Implementation",
				date(2008, 6, 3), date(2008, 7, 31), 0.0));

		ganttmodel.addValue(
				"Actual",
				new GanttTask("Write Proposal", date(2008, 4, 1), date(2008, 4,
						3), 0.0));
		ganttmodel.addValue("Actual", new GanttTask("Requirements Analysis",
				date(2008, 4, 10), date(2008, 5, 15), 0.0));
		ganttmodel.addValue(
				"Actual",
				new GanttTask("Design Phase", date(2008, 5, 15), date(2008, 6,
						17), 0.0));
		ganttmodel.addValue("Actual", new GanttTask("Alpha Implementation",
				date(2008, 7, 1), date(2008, 9, 12), 0.0));

		return ganttmodel;
	}

	public static Date date(int year, int month, int day) {
		final java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(year, month - 1, day);
		return calendar.getTime();
	}

	public static PieChartConfig createPieChartConfig() {
		PieChartConfig config = new PieChartConfig();
		config.setPieFillAlpha(95);
		config.setAnimation(false);
		config.getChartProperties().addProperty(
				PieChartConfig.PROPERTY_PIE_BORDER_COLOR, "FF0000");

		CategoriesPropertiesMap cConfig = config.getCategoryPropertiesMap();
		cConfig.createCategoryProperties("C/C++").addProperty(
				PieChartConfig.CATEGORY_PROPERTY_COLOR, "AFD8F8");

		cConfig.createCategoryProperties("Java")
				.addProperty(PieChartConfig.CATEGORY_PROPERTY_COLOR, "8BBA00")
				.addProperty(PieChartConfig.CATEGORY_PROPERTY_IS_SLICED, "1");

		cConfig.createCategoryProperties(2).addProperty(
				PieChartConfig.CATEGORY_PROPERTY_COLOR, "000000");
		cConfig.createCategoryProperties(3).addProperty(
				PieChartConfig.CATEGORY_PROPERTY_COLOR, "A66EDD");

		return config;
	}

	public static CategoryChartConfig createCategoryChartConfig() {
		CategoryChartConfig config = new CategoryChartConfig();
		config.setAnimation(false);
		config.getChartProperties().addProperty("rotateNames", "1");

		SeriesPropertiesMap seriesPropertiesMap = config
				.getSeriesPropertiesMap();

		// parentXAxis / parentYAxis /lineThickness / anchorBorderColor takes no
		// effect for bar chart
		// lineThickness work for line chart
		seriesPropertiesMap.createSeriesProperties(0)
				.addProperty("color", "AFD8F8").addProperty("parentYAxis", "P");
		seriesPropertiesMap.createSeriesProperties(1)
				.addProperty("color", "FF8000").addProperty("parentYAxis", "S")
				.addProperty("anchorBorderColor", "FF8000")
				.addProperty("lineThickness", "15");

		return config;
	}

	public static CategoryChartConfig createCategoryChartConfigForLine() {
		CategoryChartConfig config = new CategoryChartConfig();
		config.setAnimation(false);
		config.getChartProperties().addProperty("rotateNames", "1");

		SeriesPropertiesMap seriesPropertiesMap = config
				.getSeriesPropertiesMap();

		// lineThickness work for line chart ,parentYAxis / parentXAxis not.
		seriesPropertiesMap.createSeriesProperties(0)
				.addProperty("color", "AFD8F8").addProperty("parentYAxis", "P");
		seriesPropertiesMap.createSeriesProperties(1)
				.addProperty("color", "FF8000").addProperty("parentYAxis", "S")
				.addProperty("anchorBorderColor", "FF8000")
				.addProperty("lineThickness", "15");

		return config;
	}

	public static CategoryChartConfig createCategoryChartConfigForBarChart() {
		CategoryChartConfig config = new CategoryChartConfig();
		config.setAnimation(false);

		// parentXAxis / parentYAxis /lineThickness / anchorBorderColor
		// /rotateNames takes no effect for bar chart

		SeriesPropertiesMap seriesPropertiesMap = config
				.getSeriesPropertiesMap();
		seriesPropertiesMap.createSeriesProperties(0).addProperty("color",
				"AFD8F8");
		seriesPropertiesMap.createSeriesProperties(1).addProperty("color",
				"FF8000");

		return config;
	}

	public static CategoryModel createCategoryModel() {
		CategoryModel catmodel = new SimpleCategoryModel();
		catmodel.setValue("2001", "Q1", new Integer(20));
		catmodel.setValue("2001", "Q2", new Integer(35));
		catmodel.setValue("2001", "Q3", new Integer(40));
		catmodel.setValue("2001", "Q4", new Integer(55));
		catmodel.setValue("2002", "Q1", new Integer(40));
		catmodel.setValue("2002", "Q2", new Integer(60));
		catmodel.setValue("2002", "Q3", new Integer(70));
		catmodel.setValue("2002", "Q4", new Integer(90));
		return catmodel;
	}

	public static XYChartConfig createXYChartConfig() {
		XYChartConfig config = new XYChartConfig();
		SeriesPropertiesMap sConfig = config.getSeriesPropertiesMap();

		sConfig.createSeriesProperties(0).addProperty("color", "0099FF")
				.addProperty("alpha", "100").addProperty("anchorAlpha", "0")
				.addProperty("lineThickness", "2");

		sConfig.createSeriesProperties(1).addProperty("color", "FF8000")
				.addProperty("alpha", "80").addProperty("showAnchors", "0");

		return config;
	}

	public static CategoryChartConfig createCombiCategoryChartConfig() {
		CategoryChartConfig config = new CategoryChartConfig();
		config.setAnimation(false);
		config.setCanvasBgColor("F6DFD9");
		config.getChartProperties().addProperty("canvasBaseColor", "FE6E54")
				.addProperty("numberPrefix", "$");

		SeriesPropertiesMap sPropertiesMap = config.getSeriesPropertiesMap();

		sPropertiesMap.createSeriesProperties(0).addProperty("color", "9ACCF6")
				.addProperty("alpha", "90");
		sPropertiesMap.createSeriesProperties(1).addProperty("color", "82CF27")
				.addProperty("alpha", "90").addProperty("parentYAxis", "S");

		return config;
	}

	public static GanttChartConfig createGanttChartConfig() {
		GanttChartConfig config = new GanttChartConfig();
		config.getChartProperties().addProperty("canvasBorderColor", "024455")
				.addProperty("canvasBorderThickness", "0");

		GanttChartCategoriesProperties cProps = config.getCategoriesConfig()
				.createCategoriesProperties();
		cProps.addProperty("bgColor", "4567aa");

		cProps.createCategoryProperties("Months", date(2008, 4, 1),
				date(2008, 9, 30)).addProperty("align", "center")
				.addProperty("font", "Verdana")
				.addProperty("fontColor", "ffffff").addProperty("isBold", "1")
				.addProperty("fontSize", "16");

		config.getHeaderConfig().addProperty("bgColor", "ffffff")
				.addProperty("fontColor", "1288dd")
				.addProperty("fontSize", "10");

		config.getProcessConfig().addProperty("bgColor", "4567aa")
				.addProperty("fontColor", "ffffff")
				.addProperty("fontSize", "10")
				.addProperty("headerVAlign", "right")
				.addProperty("headerbgColor", "4567aa")
				.addProperty("headerFontColor", "ffffff")
				.addProperty("headerFontSize", "16").addProperty("width", "80")
				.addProperty("align", "left");

		config.getTasksProperties().addProperty("width", "10");

		GanttChartSeriesPropertiesMap sConfig = config.getSeriesConfig();
		sConfig.createSeriesProperties("Scheduled").addProperty("color",
				"4567aa");
		sConfig.createSeriesProperties("Actual").addProperty("color", "cccccc");

		defineGanttTable(config.getTableConfig());

		return config;
	}

	public static void defineGanttTable(GanttTableConfig config) {
		config.getChartProperties().addProperty("nameAlign", "left")
				.addProperty("fontColor", "000000")
				.addProperty("fontSize", "10")
				.addProperty("headerBgColor", "00ffff")
				.addProperty("headerFontColor", "4567aa")
				.addProperty("headerFontSize", "11")
				.addProperty("vAlign", "right").addProperty("align", "left");

		GanttTableColumnPropertiesMap cConfig = config.getColumnConfig();
		cConfig.createColumnProperties(0).addProperty("headerText", "Dur")
				.addProperty("align", "center")
				.addProperty("headerfontcolor", "ffffff")
				.addProperty("headerbgColor", "4567aa")
				.addProperty("bgColor", "eeeeee");

		cConfig.createColumnProperties(1).addProperty("headerText", "Cost")
				.addProperty("align", "right")
				.addProperty("headerfontcolor", "ffffff")
				.addProperty("headerbgColor", "4567aa")
				.addProperty("bgColor", "4567aa").addProperty("bgAlpha", "25");
	}

	public static ListModel createGanttTableModel() {
		ListModelList model = new ListModelList();
		model.add(new String[] { "150", "$400" });
		model.add(new String[] { "340", "$890" });
		model.add(new String[] { "60", "$1234" });
		model.add(new String[] { "20", "$230" });
		return model;
	}

	public static GanttTableRenderer createGanttTableRenderer() {
		return new GanttTableRenderer() {
			public void render(GanttRowPropertiesList row, Object data)
					throws Exception {
				String[] s = (String[]) data;
				row.createLabel(s[0]);
				row.createLabel(s[1]);
			}
		};
	}

}
