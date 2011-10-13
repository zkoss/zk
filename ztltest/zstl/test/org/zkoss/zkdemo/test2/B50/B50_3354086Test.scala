/* B50_3354086Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 12:21:28 CST 2011 , Created by benbai
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
 * A test class for bug 3354086
 * @author benbai
 *
 */
@Tags(tags = "B50-3354086.zul,A,E,Listbox,Databind")
class B50_3354086Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
    		<?init class="org.zkoss.zkplus.databind.AnnotateDataBinderInit"?>
			<zscript><![CDATA[ 
			     //prepare the example persons List
			     	import org.zkoss.zktest.test.Person;
			     	import org.zkoss.zul.event.ListDataListener;	
			     
					int count = 5;
					List persons = new ArrayList();
					for(int j= 0; j < count; ++j) {
						Person personx = new Person("Tom"+j,"Hanks"+j);
						persons.add(personx);
					}
					
						
					class SimpleListModel implements ListModel{
						private List _list;
						SimpleListModel(List list){
							_list = list;
						}
						public Object getElementAt(int index){
							return _list.get(index);
						}
						public int getSize(){
							return _list.size();
						}
						public void addListDataListener(ListDataListener l){}
						public void removeListDataListener(ListDataListener l){} 			
					}
				
					SimpleListModel personLM = new SimpleListModel(persons);
			     ]]>
			 	</zscript>
				<separator/>
				Should see a selectable listbox, no exception thrown
				<separator/>
				<listbox id="lb" mold="select" model="@{personLM}"/>
			</zk>

    }

    def executor = ()=>{
    	var lb: Widget = engine.$f("lb");
    	waitResponse();
    	verifyFalse(jq(".z-window-highlighted").exists());
   		verifyFalse(jq(".z-window-modal").exists())
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
   /** trigger mouse event example
    Scripts.triggerMouseEventAt(getWebDriver(), inner1, "click", "5,5");
    */
   /** detect whether exception exists example
   		verifyFalse(jq(".z-window-highlighted").exists());
   		verifyFalse(jq(".z-window-modal").exists())
	*/

  }
}