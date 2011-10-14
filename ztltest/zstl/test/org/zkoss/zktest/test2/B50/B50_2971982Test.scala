/* B50_2971982Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 14:59:49 CST 2011 , Created by benbai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.B50

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
 * A test class for bug 2971982
 * @author benbai
 *
 */
@Tags(tags = "B50-2971982.zul,A,E,Treefooter,Listfooter")
class B50_2971982Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
				Check "Second footer" is in third column
				<hbox>
					<listbox id="listbox" width="300px">
						<listhead>
							<listheader label="First" />
							<listheader label="Second" />
							<listheader label="Third" />
						</listhead>
						<listitem>
							<listcell label="test1" />
							<listcell label="test2" />
							<listcell label="test3" />
						</listitem>
						<listfoot>
							<listfooter span="2" label="2 span footer" />
							<listfooter label="Second footer" />
						</listfoot>
					</listbox>
					<separator />
					<tree id="tree" width="300px">
						<treecols>
							<treecol label="First" />
							<treecol label="Second" />
							<treecol label="Third" />
						</treecols>
						<treechildren>
							<treeitem>
								<treerow>
									<treecell label="test1" />
									<treecell label="test2" />
									<treecell label="test3" />
								</treerow>
							</treeitem>
						</treechildren>
						<treefoot>
							<treefooter span="2" label="2 span footer" />
							<treefooter label="Second footer" />
						</treefoot>
					</tree>
				</hbox>
			</zk>

    }
 
   // Run syntax 2
    runZTL(zscript,
        () => {
        var listbox: Widget = engine.$f("listbox");
        var tree: Widget = engine.$f("tree");

        var w1: Int = jq(jq(listbox.$n("body")).find(".z-listcell").get(0)).width() + 
        				jq(jq(listbox.$n("body")).find(".z-listcell").get(1)).width();
        var w2: Int = jq(jq(tree.$n("body")).find(".z-treecell").get(0)).width() + 
        				jq(jq(tree.$n("body")).find(".z-treecell").get(1)).width();
        var offsetLeft1 = Integer.parseInt(jq(listbox.$n("foot")).find(".z-listfooter").get(1).get("offsetLeft"));
        var offsetLeft2 = Integer.parseInt(jq(tree.$n("foot")).find(".z-treefooter").get(1).get("offsetLeft"));

        verifyTrue("offsetLeft of second footer in left listbox ("+offsetLeft1+
            ") should larger then the width of first two rows ("+w1+")",
            offsetLeft1 > w1);
        verifyTrue("offsetLeft of second footer in right tree ("+offsetLeft2+
            ") should larger then the width of first two rows ("+w2+")",
            offsetLeft2 > w2);


    }
   );

  }
}