/**
 * 
 */
package org.zkoss.zul;

import java.util.ArrayList;

import org.zkoss.util.Pair;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.impl.XulElement;

/**
 * @author Jeff
 * 
 */
public class RowsTreeitemRenderer implements TreeitemRenderer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.zul.TreeitemRenderer#render(org.zkoss.zul.Treeitem,
	 *      java.lang.Object)
	 */
	public void render(Treeitem item, Object data) throws Exception {

		String xValue = data.toString();
		if (data instanceof ArrayList) {
			xValue = "Branch:";
		}
		String yValue = "";

		if (data instanceof Pair) {
			Pair p = (Pair) data;
			xValue = p.x.toString();
			yValue = p.y.toString();
		}

		Treecell tc = new Treecell(xValue);
		Treecell tc2 = new Treecell(yValue);
		Treerow tr = null;
		System.out.println((item.getTreerow()!=null));
		if(item.getTreerow()!=null){
			tr = item.getTreerow();
			tc.setParent(tr);
			tc2.setParent(tr);
			tr.setParent(item);
		}	
		else{
			tr = new Treerow();
			tc.setParent(tr);
			tc2.setParent(tr);
			tr.setParent(item);
		}
	}

}
