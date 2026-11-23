/* macro.zs

{{IS_NOTE
	Purpose:
		zscript
	Description:
		
	History:
		Wed May 24 14:20:03     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
import org.zkoss.zk.ui.*;
import org.zkoss.zul.*;

public class Username extends HtmlMacroComponent {
	void addRow(String label) {
		Row row = new Row();
		row.setParent(getFellow("mc_grid").getRows());
		new Label(label).setParent(row);
		new Textbox().setParent(row);
	}
};
