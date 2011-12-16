/* F60SelectorComposer.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Dec 14, 2011 3:19:48 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.select;

import java.util.List;

import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

/**
 *
 * @author simonpai
 */
@VariableResolver(Z60SelectorComposer.Z60VariableResolver.class)
public class Z60SelectorComposer extends SelectorComposer<Component> {
	
	@Wire("row > label:nth-child(2)")
	private List<Label> rowlbs;
	
	@Wire("grid")
	private Grid grid;
	
	@WireVariable("0")
	private String var;
	
	@Listen("onClick = button#btn")
	public void go() {
		for (Label lb : rowlbs)
			lb.setValue(var);
	}
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		grid.setRowRenderer(new RowRenderer<String>() {
			public void render(Row row, String data) throws Exception {
				row.appendChild(new Label(data));
				row.appendChild(new Label());
			}
		});
		
	}
	
	public static class Z60VariableResolver implements org.zkoss.xel.VariableResolver {
		
		public Object resolveVariable(String name) throws XelException {
			return name + name;
		}
		
	}
	
}
