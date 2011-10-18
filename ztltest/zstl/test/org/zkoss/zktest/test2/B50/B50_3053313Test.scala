/* B50_3053313Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 18 14:18:15 CST 2011 , Created by benbai
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
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

/**
 * A test class for bug 3053313
 * @author benbai
 *
 */
@Tags(tags = "B50-3053313.zul,A,E,Calendar,Datebox,Constraint")
class B50_3053313Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
			<hbox>No Past:
			<datebox id="dtbx1" constraint="no past"/>
			Today can be selected
			</hbox>
			<hbox>No Today:
			<datebox id="dtbx2" constraint="no today"/>
			Today can't be selected
			</hbox>
			<hbox>No After:
			<datebox  id="dtbx3" constraint="no future"/>
			Today can be selected
			</hbox>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var dtbx1: Widget = engine.$f("dtbx1");
        var dtbx2: Widget = engine.$f("dtbx2");
        var dtbx3: Widget = engine.$f("dtbx3");
        var cal: Calendar = Calendar.getInstance();
        var today: Int = cal.get(Calendar.DAY_OF_MONTH);

        var dt1: Int = 0;

        click(dtbx3.$n("btn"));
        var calRows: Array[JQuery] = jq(dtbx3.$n("pp")).find(".z-calendar-caldayrow").toArray[JQuery];

        var l: List[JQuery]  = new ArrayList();
        for (i <- 0 until calRows.length) {
        	l.addAll(calRows(i).find("td").toList);
        }
        var foundToday: Boolean = false;
        for (j <- 0 until l.size()) {
            var td = l.get(j).get(0);
            var clsnm: String = td.get("className")
            dt1 = Integer.parseInt(td.get("innerHTML"));
            if (!foundToday) {
                verifyFalse("for third datebox, today and all pass day should be selectable",
                    clsnm.contains("z-calendar-disd"));
                foundToday = (dt1 == today) && (!clsnm.contains("z-outside"));
            } else {
                verifyTrue("for third datebox, all future day should be unselectable",
                    clsnm.contains("z-calendar-disd"));
            }
        }

        click(dtbx2.$n("btn"));
        dt1 = Integer.parseInt(jq(dtbx2.$n("pp"))
        		.find(".z-calendar-disd").get(0).get("innerHTML"));
        verifyTrue("for second datebox, the only unselectable date should be today",
            (dt1 == today) && (jq(dtbx2.$n("pp")).find(".z-calendar-disd").length() == 1));

        click(dtbx1.$n("btn"));
        dt1 = Integer.parseInt(jq(dtbx1.$n("pp"))
        		.find(".z-calendar-disd").last().get(0).get("innerHTML"));
        verifyTrue("for first datebox, the last unselectable date should be yesterday",
            (today - dt1 == 1) || ( (today == 1) && (31 - dt1 <= 3) ));
    }
   );

  }
}