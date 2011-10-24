/* B50_ZK_428Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 19 12:12:09 CST 2011 , Created by benbai
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
 * A test class for bug ZK-428
 * @author benbai
 *
 */
@Tags(tags = "B50-ZK-428.zul,A,E,Hlayout,Vlayout")
class B50_ZK_428Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
				<zscript><![CDATA[
					import java.util.ArrayList;
					ArrayList productList = new ArrayList();
					ListModelList listModel = new ListModelList();
					int i = 0;
					while (i < 5) {
						String p = new String("name" + i++);
						productList.add(p);
					}
					listModel.addAll(productList);
					RowRenderer render = new RowRenderer() {
						public void render(Row row, Object data) throws Exception {
							String p = (String) data;
							
							Hlayout l = new Hlayout();
							l.appendChild(new Label("Label"));
							l.setParent(row);
							
							Div div = new Div();
							div.setHflex("1");
							div.setParent(row);
						}
					};
					void setProdListModel(int amount) {
						int i = 0;
						productList.clear();
						listModel.clear();
						while (i < amount) {
							String p = new String("name" + i++);
							productList.add(p);
						}
						listModel.addAll(productList);
					}
				]]></zscript>
				<window title="new page title" border="normal">
					<div>Click on the button. If it takes more than 20 seconds to run on the client side, it is a bug.</div>
					ListModelList Size:
					<intbox id="modelSize" value="50" />
					<button label="change" id="button">
						<attribute name="onClick">
							setProdListModel(modelSize.getValue());
							grdProductProductions.setModel(listModel);
						</attribute>
					</button>
					<grid width="100%" id="grdProductProductions"
						model="${listModel}" rowRenderer="${render}">
						<columns sizable="true">
							<column label="total" align="left" />
							<column label="obver" align="center" />
						</columns>
					</grid>
				</window>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var button: Widget = engine.$f("button");
        var cnt: Int = 5;

        var t1: Long = System.currentTimeMillis();
        click(button);
        while (cnt != 50) {
            cnt = jq(".z-row").length();
        }
        var t2: Long = System.currentTimeMillis();
        verifyTrue("it should not take more than 20 seconds to run on the client side for change the size of ListModelList",
            (t2-t1) < 20000);
    }
   );

 }
}