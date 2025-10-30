package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Rowchildren;
import org.zkoss.zkmax.zul.Rowlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Window;

public class F80_ZK_2708_Composer extends SelectorComposer<Window> {
	
	@Wire("#win")
	private Window _win;
	@Wire("#colspan")
	private Spinner _colspan;
	@Wire("#offset")
	private Spinner _offset;
	@Wire("#add")
	private Button _add;
	
	// available space in the current row
	private int _avail = 0;
	
	// current row
	private Rowlayout _curRow = null;

	@Listen("onClick = #add")
	public void addChild() {
		int colspan = _colspan.getValue();
		int offset  = _offset.getValue();
		
		if (_avail < colspan+offset) {
			_curRow = new Rowlayout();
			_curRow.setParent(_win);
			_avail = 12;
		}
		
		Rowchildren rc = new Rowchildren();
		rc.setColspan(colspan);
		rc.setOffset(offset);
		
		Window win = new Window();
		win.setBorder("normal");
		win.setTitle("colspan=" + colspan);
		win.setHflex("1");
		win.setParent(rc);
		
		rc.setParent(_curRow);
		
		_avail -= colspan+offset;
		
		String js = "jq('.z-window-embedded-cnt').attr('contentEditable', '').css('min-height', '30px')";
		Clients.evalJavaScript(js);
	}
}
