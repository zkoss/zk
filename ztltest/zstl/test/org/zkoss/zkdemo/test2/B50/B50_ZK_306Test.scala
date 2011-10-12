/* B50_ZK_306Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 11, 2011 17:26:14 PM , Created by benbai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2.B50

import org.zkoss.zstl.ZTL4ScalaTestCase
import scala.collection.JavaConversions._
import org.junit.Test;
import org.zkoss.ztl.Element;
import org.zkoss.ztl.JQuery;
import org.zkoss.ztl.Tags;
import org.zkoss.ztl.util.Scripts;
import org.zkoss.ztl.Widget;
import org.zkoss.ztl.ZK;
import org.zkoss.ztl.ZKClientTestCase;
import java.lang._

/**
 * A test class for bug ZK-306
 * @author benbai
 *
 */
@Tags(tags = "B50-ZK-306.zul,A,E,Tree,TreeModel")
class B50_ZK_306Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
				<div>1. Click on a Treeitem in Tree 1 and click on "show selected" Button. You will see the selected item shown after the Button.</div>
				<div>2. Click on another Treeitem in Tree 1 and click on "show selected" Button again. You should only see 1 Treeitem displayed after the Button. Otherwise it is a bug.</div>
				<tree id="tree" width="830px">
					<treecols>
						<treecol hflex="3" label="Path" />
						<treecol hflex="5" label="Description" />
						<treecol hflex="2" label="Size" />
					</treecols>
				</tree>
				<button id="btn" label="show selected">
					<attribute name="onClick"><![CDATA[
						Set s = ((org.zkoss.zul.ext.Selectable) tree.getModel()).getSelection();
						Iterator it = s.iterator();
						StringBuffer sb = new StringBuffer();
						while (it.hasNext())
							sb.append(it.next()).append("\n");
						tb.setValue(sb.toString());
					]]></attribute>
				</button>
				<label id="tb" />
				<div>3. In Tree 2, select 3 Treeitems, click on show selected Button.</div>
				<div>4. Deselect one of them, and click on the Button again. You should see only 2 items printed out.</div>
				<tree id="tree2" width="830px" checkmark="true" multiple="true">
					<treecols>
						<treecol hflex="3" label="Path" />
						<treecol hflex="5" label="Description" />
						<treecol hflex="2" label="Size" />
					</treecols>
				</tree>
				<button id="btn2" label="show selected">
					<attribute name="onClick"><![CDATA[
						Set s = ((org.zkoss.zul.ext.Selectable) tree2.getModel()).getSelection();
						Iterator it = s.iterator();
						StringBuffer sb = new StringBuffer();
						while (it.hasNext())
							sb.append(it.next()).append("\n");
						tb2.setValue(sb.toString());
					]]></attribute>
				</button>
				<label id="tb2" />
				<zscript><![CDATA[
					tree.setItemRenderer(new org.zkoss.zktest.test2.tree.DirectoryTreeitemRenderer());
					tree.setModel(new org.zkoss.zul.DefaultTreeModel(org.zkoss.zktest.test2.tree.PackageData.getRoot()));
					tree2.setItemRenderer(new org.zkoss.zktest.test2.tree.DirectoryTreeitemRenderer());
					tree2.setModel(new org.zkoss.zul.DefaultTreeModel(org.zkoss.zktest.test2.tree.PackageData.getRoot()));
				]]></zscript>
			</zk>
    }

    def executor() = {
    	var btn: Widget = engine.$f("btn");
    	var btn2: Widget = engine.$f("btn2");
    	var tree: Widget = engine.$f("tree");
    	var tree2: Widget = engine.$f("tree2");
    	var tb: Widget = engine.$f("tb");
    	var tb2: Widget = engine.$f("tb2");
    	waitResponse();

    	clickItem(tree, 1);
    	waitResponse();
    	Scripts.triggerMouseEventAt(getWebDriver(), btn, "click", "5,5");
    	waitResponse();
    	checkNumber(tb, 1);
    	clickItem(tree, 3);
    	waitResponse();
    	Scripts.triggerMouseEventAt(getWebDriver(), btn, "click", "5,5");
    	waitResponse();
    	checkNumber(tb, 1);

    	clickItem(tree2, 1);
    	waitResponse();
    	clickItem(tree2, 2);
    	waitResponse();
    	clickItem(tree2, 3);
    	waitResponse();
    	Scripts.triggerMouseEventAt(getWebDriver(), btn2, "click", "5,5");
    	waitResponse();
    	checkNumber(tb2, 3);

    	clickItem(tree2, 2);
    	waitResponse();
    	Scripts.triggerMouseEventAt(getWebDriver(), btn2, "click", "5,5");
    	waitResponse();
    	checkNumber(tb2, 2);
    }
    def clickItem(wgt: Widget, index: java.lang.Integer) {
      click(jq(wgt.$n("body")).find(".z-treerow").get(index));
    }
    def checkNumber(wgt: Widget, cnt: java.lang.Integer) {
      verifyTrue(jq(wgt.$n()).get(0).get("innerHTML").split("@").length == cnt+1);
    }
   // Run syntax 1 
   runZTL(zscript, executor);
   
   // Run syntax 2
   /**
    runZTL(zscript,
        () => {
        var l1: Widget = engine.$f("l1");
        var l2: Widget = engine.$f("l2");
        waitResponse();
        var strClickBefor = getText(l1);
        click(l1);
        waitResponse();
        verifyNotEquals(strClickBefor, getText(l1));
        strClickBefor = getText(l2);
        click(l2);
        waitResponse();
        verifyNotEquals(strClickBefor, getText(l2));
    }
   );
    */
  }
}