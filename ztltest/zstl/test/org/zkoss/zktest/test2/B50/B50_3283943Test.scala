/* B50_3283943Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 10:35:09 CST 2011 , Created by benbai
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
 * A test class for bug 3283943
 * @author benbai
 *
 */
@Tags(tags = "B50-3283943.zul,A,E,Grid,Listbox")
class B50_3283943Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk xmlns:w="client">
				<html><![CDATA[
					<ol>
						<li>Click on Groups and Listgroups, if the UUIDs shown below are different, it is a bug.</li>
					</ol>
				]]></html>
				<grid>
					<rows>
						<group id="group1" onClick="lb1s.value = self.group.uuid">
							<attribute w:name="onClick"><![CDATA[
								var g = this.getGroup(),
									id = g ? g.uuid : 'null';
								this.$f('lb1c').setValue(id);
							]]></attribute>
							Group 1 (click to show getGroup() result)
						</group>
						<row>Row 1-1</row>
						<row>Row 1-2</row>
						<group id="group2" onClick="lb1s.value = self.group.uuid">
							<attribute w:name="onClick"><![CDATA[
								var g = this.getGroup(),
									id = g ? g.uuid : 'null';
								this.$f('lb1c').setValue(id);
							]]></attribute>
							Group 2 (click to show getGroup() result)
						</group>
						<row>Row 2-1</row>
						<row>Row 2-2</row>
						<group id="group3" onClick="lb1s.value = self.group.uuid">
							<attribute w:name="onClick"><![CDATA[
								var g = this.getGroup(),
									id = g ? g.uuid : 'null';
								this.$f('lb1c').setValue(id);
							]]></attribute>
							Group 3 (click to show getGroup() result)
						</group>
						<row>Row 3-1</row>
						<row>Row 3-2</row>
					</rows>
				</grid>
				<div>
					getGroup uuid (server): <label id="lb1s" />
				</div>
				<div>
					getGroup uuid (client): <label id="lb1c" />
				</div>
				<separator />
				<listbox onSelect="lb2s.value = self.selectedItem.listgroup.uuid">
					<attribute w:name="onSelect"><![CDATA[
						var g = this.getSelectedItem().getListgroup(),
							id = g ? g.uuid : 'null';
						this.$f('lb2c').setValue(id);
					]]></attribute>
					<listgroup><listcell>Listgroup 1</listcell></listgroup>
					<listitem id="li1"><listcell>Listitem 1-1</listcell></listitem>
					<listitem><listcell>Listitem 1-2</listcell></listitem>
					<listgroup><listcell>Listgroup 2</listcell></listgroup>
					<listitem id="li2"><listcell>Listitem 2-1</listcell></listitem>
					<listitem><listcell>Listitem 2-2</listcell></listitem>
					<listgroup><listcell>Listgroup 3</listcell></listgroup>
					<listitem id="li3"><listcell>Listitem 3-1</listcell></listitem>
					<listitem><listcell>Listitem 3-2</listcell></listitem>
				</listbox>
				<div>
					getListgroup uuid (server): <label id="lb2s" />
				</div>
				<div>
					getListgroup uuid (client): <label id="lb2c" />
				</div>
			</zk>

    }

    def executor = () => {
    	var (group1, group2, group3,
    	    li1, li2, li3,
    	    lb1s, lb1c, lb2s, lb2c) = 
    	  (engine.$f("group1"), engine.$f("group2"), engine.$f("group3"),
    	      engine.$f("li1"), engine.$f("li2"), engine.$f("li3"),
    	      engine.$f("lb1s"), engine.$f("lb1c"), engine.$f("lb2s"), engine.$f("lb2c"));

		waitResponse();

		checkGridUuid(group1, lb1s, lb1c);
		checkGridUuid(group2, lb1s, lb1c);
		checkGridUuid(group3, lb1s, lb1c);
		checkGridUuid(li1, lb2s, lb2c);
		checkGridUuid(li2, lb2s, lb2c);
		checkGridUuid(li3, lb2s, lb2c);
    }
    def checkGridUuid(wgt: Widget, lbs: Widget, lbc: Widget) {
    	click(wgt.$n());
		waitResponse();
		verifyTrue(lbs.$n().get("innerHTML").equals(lbc.$n().get("innerHTML")));
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
    /** create widget example
		var tree: Widget = engine.$f("tree");
		var listbox: Widget = engine.$f("listbox");
		waitResponse();
	*/
   /** trigger mouse event example
    Scripts.triggerMouseEventAt(getWebDriver(), inner1, "click", "5,5");
    */
   /** detect whether exception exists example
   		verifyFalse(jq(".z-window-highlighted").exists());
   		verifyFalse(jq(".z-window-modal").exists())
	*/
	/** detect browser
		if (ZK.is("ie6_") || ZK.is("ie7_"))
	*/
  }
}