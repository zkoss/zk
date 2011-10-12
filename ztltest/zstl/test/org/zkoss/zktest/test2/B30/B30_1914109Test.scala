/* B30_1914109Test.scala

	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 10:32:55 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2.B30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 *
 * @author jumperchen
 */
@Tags(tags = "")
class B30_1914109Test extends ZTL4ScalaTestCase {
  def testCase() = {
    val zscript = {
      <zk xmlns:n="http://www.zkoss.org/2005/zk/native">
        <n:ol>
          <n:li>
            Click set a smaller height button, and you should see the listbox
is shorter and has a scroll bar
          </n:li>
        </n:ol>
        <window title="Live Data Demo" border="normal">
          <button label="set a smaller height" onClick='list.height="150px"'/>
          <zscript><![CDATA[
            List items = new org.zkoss.zktest.test2.BigList(100);
    		ListModel strset = new ListModelList(items);
    		ListitemRenderer render = new ListitemRenderer(){
              public void render(Listitem item, Object data) {
                new Listcell("col - " + item.getIndex()).setParent(item);
                new Listcell("col - " + item.getIndex()).setParent(item);
                new Listcell("col - " + item.getIndex()).setParent(item);
                new Listcell("col - " + item.getIndex()).setParent(item);
                new Listcell("col - " + item.getIndex()).setParent(item);
              }
            }
            ;
          ]]></zscript>
          <listbox id="list" width="100%" model="&#36;{strset}" itemRenderer="${render}" mold="paging" pagingPosition="both">
            <listhead sizable="true">
              <listheader label="Col 1" sort="auto"/>
              <listheader label="Col 2" sort="auto"/>
              <listheader label="Col 3" sort="auto"/>
              <listheader label="Col 4" sort="auto"/>
              <listheader label="Col 5" sort="auto"/>
            </listhead>
          </listbox>
        </window>
      </zk>
    }
    runZTL(zscript, () => {
    	val list = engine $f "list"
    	val height = jq(list) outerHeight
    	val scrollHeight = jq(list) scrollHeight()
    	click(jq("@button"))
    	waitResponse
    	verifyTrue(height > jq(list).outerHeight)
    	verifyTrue(200 > jq(list).outerHeight)
    	verifyTrue(scrollHeight > jq(list).scrollHeight)
    })
  }
}
