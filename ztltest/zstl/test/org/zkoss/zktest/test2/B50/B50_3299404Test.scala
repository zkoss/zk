/* B50_3299404Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 18:30:44 CST 2011 , Created by benbai
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
 * A test class for bug 3299404
 * @author benbai
 *
 */
@Tags(tags = "B50-3299404.zul,A,E,Grid,Model,IE8")
class B50_3299404Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
			<window width="600px">
				<zscript><![CDATA[
					import org.zkoss.zk.ui.event.EventListener;
					import org.zkoss.zul.*;
					
					
					class MyRowrenderer implements RowRenderer {
					    public void render(Row row, Object data) throws Exception {
					        for (int i=0; i<5; i++) {
					            Cell c = new Cell();
					            c.setParent(row);
					
					            Label l = new Label("label" + i);
					            l.setParent(c);
					        }
					    }
					}
					
					RowRenderer renderer = new MyRowrenderer();
				]]></zscript>
				<button id="btn" label="IE only, click me, it shall not error happen.">
					<attribute name="onClick"><![CDATA[
						Columns columns = grid.getColumns();
						columns.setParent(null);
						
						columns = new Columns();
						columns.setParent(grid);
						columns.setSizable(true);
						
						for (int i=0; i<5; i++) {
						    Column c = new Column("column" + i);
						    c.setParent(columns);
						}
						
						ListModelList model = new ListModelList();
				        model.add("Null");
				        model.add("Null");
				        grid.setModel(model);
				        
					]]></attribute>
				</button>
			    <grid id="grid" sizedByContent="true" span="true" rowRenderer="${renderer}">
			        <columns sizable="true">
			            <column width="30px"></column>
			            <column label="22"></column>
			            <column label="0123456789"></column>
			            <column label="44444"></column>
			        </columns>
			    </grid>
			</window>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var btn: Widget = engine.$f("btn");
		waitResponse();

		click(btn);
		waitResponse();
		verifyFalse(jq(".z-error").exists());
    }
   );
  }
}