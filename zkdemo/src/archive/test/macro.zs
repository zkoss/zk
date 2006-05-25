/* macro.zs

{{IS_NOTE
	$Id: macro.zs,v 1.1 2006/05/25 07:09:37 tomyeh Exp $
	Purpose:
		zscript
	Description:
		
	History:
		Wed May 24 14:20:03     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
import com.potix.zk.ui.*;
import com.potix.zul.html.*;

public class Username extends HtmlMacroComponent {
	void addRow(String label) {
		Row row = new Row();
		row.setParent(getFellow("mc_grid").getRows());
		new Label(label).setParent(row);
		new Textbox().setParent(row);
	}
};
