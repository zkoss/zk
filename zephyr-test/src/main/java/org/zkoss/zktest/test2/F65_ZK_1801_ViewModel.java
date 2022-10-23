package org.zkoss.zktest.test2;

import java.awt.Color;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zkmax.zul.ChartProperties;
import org.zkoss.zkmax.zul.fusionchart.config.CategoriesPropertiesMap;
import org.zkoss.zkmax.zul.fusionchart.config.FunnelChartConfig;
import org.zkoss.zul.SimpleSingleValueCategoryModel;
import org.zkoss.zul.SingleValueCategoryModel;

import static org.zkoss.zktest.test2.F65_ZK_1801_ViewModel.ChartColors.*;

public class F65_ZK_1801_ViewModel {

	private String message;
	private FunnelChartConfig chartConfig;

	private boolean sliced = true;
	private boolean showValues = true;
	private int decimalPrecision = 1;

	@Init
	public void init() {
		chartConfig = new FunnelChartConfig();
		updateChartConfig();
	}

	private void updateChartConfig() {
		ChartProperties chartProperties = chartConfig.getChartProperties();
		chartProperties.addProperty(FunnelChartConfig.CHART_PROPERTY_IS_SLICED,
				sliced ? "1" : "0");
		chartProperties.addProperty(
				FunnelChartConfig.CHART_PROPERTY_SHOW_VALUES, showValues ? "1"
						: "0");
		chartProperties.addProperty(
				FunnelChartConfig.CHART_PROPERTY_DECIMAL_PRECISION, ""
						+ decimalPrecision);
	}

	private void setCategoryColors(Color... colors) {
		CategoriesPropertiesMap categoryPropertiesMap = chartConfig.getCategoryPropertiesMap();
		int categoryIndex = 0;
		for (Color color : colors) {
			categoryPropertiesMap.createCategoryProperties(categoryIndex)
			.addProperty(FunnelChartConfig.CATEGORY_PROPERTY_COLOR,
					toHtmlColor(color));
			categoryIndex++;
		}
	}
	
	@Command("showMessage")
	@NotifyChange("message")
	public void onShowMessage(@BindingParam("data") Map<String, Object> data) {
		this.message = data.get("category") + ", " + data.get("value");
	}

	@Command("configUpdated")
	@NotifyChange("chartConfig")
	public void onConfigUpdated() {
		updateChartConfig();
		System.out.println("config updated");
	}

	@Command("applyCustomColors")
	@NotifyChange("chartConfig")
	public void onApplyCustomColors() {
		
		setCategoryColors(COLOR_1, COLOR_2, COLOR_3, COLOR_4, COLOR_5, COLOR_6,
				COLOR_7, COLOR_8);
	}

	
	public FunnelChartConfig getChartConfig() {
		return chartConfig;
	}

	public SingleValueCategoryModel getModel() {
		SingleValueCategoryModel model = new SimpleSingleValueCategoryModel();
		model.setValue("Step 1", new Double(21.2));
		model.setValue("Step 2", new Double(10.2));
		model.setValue("Step 3", new Double(40.4));
		model.setValue("Step 4", new Double(28.2));
		model.setValue("Step 5", new Double(21.2));
		model.setValue("Step 6", new Double(10.2));
		model.setValue("Step 7", new Double(40.4));
		model.setValue("Step 8", new Double(28.2));
		return model;
	}

	public String getMessage() {
		return message;
	}

	public boolean isSliced() {
		return sliced;
	}

	public void setSliced(boolean sliced) {
		this.sliced = sliced;
	}

	public boolean isShowValues() {
		return showValues;
	}

	public void setShowValues(boolean showValues) {
		this.showValues = showValues;
	}

	public int getDecimalPrecision() {
		return decimalPrecision;
	}

	public void setDecimalPrecision(int decimalPrecision) {
		this.decimalPrecision = decimalPrecision;
	}
	
	public static class ChartColors {
	    //main colors
	    public static Color COLOR_1 = new Color(0x3E454C);
	    public static Color COLOR_2 = new Color(0x2185C5);
	    public static Color COLOR_3 = new Color(0x7ECEFD);
	    public static Color COLOR_4 = new Color(0xFFF6E5);
	    public static Color COLOR_5 = new Color(0xFF7F66);
	    //additional colors
	    public static Color COLOR_6 = new Color(0x98D9FF);
	    public static Color COLOR_7 = new Color(0x4689B1);
	    public static Color COLOR_8 = new Color(0xB17C35);
	    public static Color COLOR_9 = new Color(0xFDC77E);
	     
	    public static String toHtmlColor(Color color) {
	        return "#" + toHexColor(color);
	    }
	 
	    public static String toHexColor(Color color) {
	        return StringUtils.leftPad(Integer.toHexString(color.getRGB() & 0xFFFFFF), 6, '0');
	    }
	}

}
