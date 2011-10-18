/* B50_3189758Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 18 12:22:49 CST 2011 , Created by benbai
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
 * A test class for bug 3189758
 * @author benbai
 *
 */
@Tags(tags = "B50-3189758.zul,A,E,Datebox,Constraint")
class B50_3189758Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

    		<zk>
				<zscript><![CDATA[
					import org.zkoss.util.*;
					import org.zkoss.zul.*;
					import java.util.*;
					import java.text.SimpleDateFormat;
					
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
					String date = df.format(new Date());
					
					java.util.Calendar cal = java.util.Calendar.getInstance();
					cal.add(java.util.Calendar.DATE, -3);
					Date beg = cal.getTime();
					cal.add(java.util.Calendar.DATE, 6);
					Date end = cal.getTime();
					
					String bdate = df.format(beg);
					String edate = df.format(end);
					
					
					
					void test(String constraint, int index, Date d, Date d2) {
						db.constraint = constraint;
						
						Label lb = (Label)((Row)rows.getChildren().get(index)).getChildren().get(1);
						try {
							db.setValue(d);
							lb.setValue("O");
							lb.setSclass("positive");
						} catch (Exception ex) {
							lb.setValue("X");
							lb.setSclass("negative");
						}
						
						lb = lb.getNextSibling();
						try {
							db.setValue(d2);
							lb.setValue("O");
							lb.setSclass("positive");
						} catch (Exception ex) {
							lb.setValue("X");
							lb.setSclass("negative");
						}
					}
					
					void test2() {
						db.constraint = "between "+bdate+" and "+edate;
						
						Label lb2 = (Label)((Row)rows.getChildren().get(3)).getFirstChild();
						Label lb = (Label)lb2.getNextSibling().getFirstChild();
						
						Date d = Dates.beginOfDate(beg, TimeZones.getCurrent());
						try {
							db.setValue(d);
							lb.setValue("O");
							lb.setSclass("positive");
						} catch (Exception ex) {
							lb.setValue("X");
							lb.setSclass("negative");
						}
						
						d = Dates.endOfDate(beg, TimeZones.getCurrent());
						lb = (Label)lb.getNextSibling();
						try {
							db.setValue(d);
							lb.setValue("O");
							lb.setSclass("positive");
						} catch (Exception ex) {
							lb.setValue("X");
							lb.setSclass("negative");
						}
						
						
						d = Dates.beginOfDate(end, TimeZones.getCurrent());
						lb = (Label)lb2.getNextSibling().getNextSibling().getFirstChild();
						try {
							db.setValue(d);
							lb.setValue("O");
							lb.setSclass("positive");
						} catch (Exception ex) {
							lb.setValue("X");
							lb.setSclass("negative");
						}
						
						d = Dates.endOfDate(end, TimeZones.getCurrent());
						lb = (Label)lb.getNextSibling();
						try {
							db.setValue(d);
							lb.setValue("O");
							lb.setSclass("positive");
						} catch (Exception ex) {
							lb.setValue("X");
							lb.setSclass("negative");
						}
					}
				]]></zscript>
				<style><![CDATA[
					.positive{ 
						color:blue;
					} 
					.negative { 
						color:red; 
					}
				]]></style>
				<button id="btn" label="Click me, the result shall the same with answer.">
					<attribute name="onClick"><![CDATA[
						test("no future", 0, Dates.today(),Dates.endOfDate(new Date(), TimeZones.getCurrent()));
						test("no past", 1, Dates.today(),Dates.endOfDate(new Date(), TimeZones.getCurrent()));
						test("no today", 2, Dates.today(),Dates.endOfDate(new Date(), TimeZones.getCurrent()));
						test("before " + date, 4, Dates.today(),Dates.endOfDate(new Date(), TimeZones.getCurrent()));
						test("after " + date, 5, Dates.today(),Dates.endOfDate(new Date(), TimeZones.getCurrent()));
						test2();
					]]></attribute>
				</button>
				<grid id="grid" hflex="min">
					<auxhead>
						<auxheader colspan="3"/>
						<auxheader colspan="2" label="answer"/>
					</auxhead>
					<columns>
						<column label="constraint"  hflex="min"/>
						<column label="begin"  hflex="min"/>
						<column label="end"  hflex="min"/>
						<column label="begin"  hflex="min"/>
						<column label="end"  hflex="min"/>
					</columns>
					<rows id="rows">
						<row>
							<label value="no future"/>
							<label/>
							<label/>
							<label value="O" sclass="positive" />
							<label value="O" sclass="positive" />
						</row>
						<row>
							<label value="no past" />
							<label/>
							<label/>
							<label value="O" sclass="positive" />
							<label value="O" sclass="positive" />
						</row>
						<row>
							<label value="no today" />
							<label/>
							<label/>
							<label value="X" sclass="negative" />
							<label value="X" sclass="negative" />
						</row>
						<row>
							<label value="between ${bdate} and ${edate}" />
							<hlayout>
								<label/>
								<label/>
							</hlayout>
							<hlayout>
								<label/>
								<label/>
							</hlayout>
							<hlayout>
								<label value="O" sclass="positive" />
								<label value="O" sclass="positive" />
							</hlayout>
							<hlayout>
								<label value="O" sclass="positive" />
								<label value="O" sclass="positive" />
							</hlayout>
						</row>
						<row>
							<label value="before ${date}" />
							<label/>
							<label/>
							<label value="O" sclass="positive" />
							<label value="O" sclass="positive" />
						</row>
						<row>
							<label value="after ${date}" />
							<label/>
							<label/>
							<label value="O" sclass="positive" />
							<label value="O" sclass="positive" />
						</row>
					</rows>
				</grid>
				<separator />
				<datebox id="db" constraint="no future"/>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var btn: Widget = engine.$f("btn");
        var grid: Widget = engine.$f("grid");

        click(btn);
        waitResponse();

        var rows: Array[JQuery] = jq(grid.$n("body")).find(".z-rows").find(".z-row").toArray[JQuery];

        for (i <- 0 until rows.length) {
        	var beg1: Element = rows(i).find(".z-row-inner").get(1);
        	var beg2: Element = rows(i).find(".z-row-inner").get(3);
        	var end1: Element = rows(i).find(".z-row-inner").get(2);
        	var end2: Element = rows(i).find(".z-row-inner").get(4);

        	checkEqual(beg1, beg2);
        	checkEqual(end1, end2);
        }
        
        def checkEqual (cel1: Element, cel2: Element) {
        	var lbCnt1: Int = jq(cel1).find(".z-label").length;
        	var lbCnt2: Int = jq(cel2).find(".z-label").length;
        	
        	verifyTrue("the amount of label in both column should be equal",
        	    lbCnt1 == lbCnt2);
        	for (i <- 0 until lbCnt1) {
        		var lb1: Element = jq(cel1).find(".z-label").get(i);
        		var lb2: Element = jq(cel2).find(".z-label").get(i);

        		verifyTrue("the class name of both label should be equal",
        		    lb1.get("className").equals(lb2.get("className")));
        		verifyTrue("the innerHTML of both label should be equal",
        		    lb1.get("innerHTML").equals(lb2.get("innerHTML")));
        	}
        }
    }
   );

  }
}