package org.zkoss.zktest.test2;

import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

public class B50_ZK_1022_GridRenderer implements RowRenderer {

	public void render(Row row, Object data) throws Exception {
		new Label( "item1" ).setParent(row);
		new Label( "item2" ).setParent(row);
		new Label( "item3" ).setParent(row);
		new Label( "item4" ).setParent(row);
		new Label( "item5" ).setParent(row);
	}	
	public void render(Row row, Object data,int ind) throws Exception {
		render(row,data);
	}	

}