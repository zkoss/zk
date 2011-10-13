/* B50_ZK_421Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 11, 2011 12:38:14 PM , Created by benbai
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
import org.zkoss.ztl.Widget;
import org.zkoss.ztl.ZK;
import org.zkoss.ztl.ZKClientTestCase;
import java.lang._

/**
 * A test class for bug ZK-421
 * @author benbai
 *
 */
@Tags(tags = "B50-ZK-421.zul,A,E,Listbox,Tree,Paging")
class B50_ZK_421Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
    		<zk>
    			<div>1. Select an item, go to page 2.</div>
    			<div>2. Select another item and go back to page 1. In the first Listbox you 
    				should see no item selected in the first page. For the second Listbox, the item 
    				shall remain selected unless the Library properties "checkmarkDeselectsOther" is true.</div>
    				Multiple:
    			<listbox id="lb1" mold="paging" multiple="true" onCreate="self.setPageSize(5)">
    				<listitem label="${each}" forEach="1,2,3,4,5,6,7,8,9,10" />
    			</listbox>
    			Multiple, Checkmark:
    			<listbox id="lb2" mold="paging" multiple="true" checkmark="true" onCreate="self.setPageSize(5)">
    				<listitem label="${each}" forEach="1,2,3,4,5,6,7,8,9,10" />
    			</listbox>
    		</zk>
    }

    def executor() = {
    	var lb1: Widget = engine.$f("lb1");
    	var lb2: Widget = engine.$f("lb2");
    	waitResponse();
    	testSelect(lb1, false);
    	testSelect(lb2, true);
    }

    def testSelect(lb: Widget, keep: java.lang.Boolean) = ()=>{
    	click(jq(lb.$n("rows")).find(".z-listitem").get(2));
    	waitResponse();
    	click(jq(lb.$n("pgib")).find(".z-paging-next").get(0));
    	waitResponse();
    	click(jq(lb.$n("rows")).find(".z-listitem").get(2));
    	waitResponse();
    	verifyTrue(jq(jq(lb.$n("rows")).find(".z-listitem").get(2)).hasClass("z-listitem-seld"));
    	click(jq(lb.$n("pgib")).find(".z-paging-prev").get(0));
    	waitResponse();
    	if (keep)
    		verifyTrue(jq(jq(lb.$n("rows")).find(".z-listitem").get(2)).hasClass("z-listitem-seld"));
    	else
    		verifyFalse(jq(jq(lb.$n("rows")).find(".z-listitem").get(2)).hasClass("z-listitem-seld"));
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
