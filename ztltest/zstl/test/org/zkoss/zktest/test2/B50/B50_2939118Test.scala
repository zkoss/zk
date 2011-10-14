/* B50_2939118Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 15:43:14 CST 2011 , Created by benbai
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
 * A test class for bug 2939118
 * @author benbai
 *
 */
@Tags(tags = "B50-2939118.zul,A,E,Tree,Model")
class B50_2939118Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
			    <zscript><![CDATA[
			        public class TreeComposer extends org.zkoss.zk.ui.util.GenericForwardComposer{
			    Tree tree;
			
			    public void doAfterCompose(Component component) throws java.lang.Exception {
			        super.doAfterCompose(component);
			
			        tree.setTreeitemRenderer(new TreeDragDropRender());
			
			        initializeTree();
			    }
			
			    public void onDropTreerow() {
			        initializeTree();
			    }
			
			    private void initializeTree() {
			        tree.setModel(new org.zkoss.zktest.test2.tree.BinaryTreeModel(new ArrayList(new org.zkoss.zktest.test2.BigList(10))));
			    }
			
			    class TreeDragDropRender implements TreeitemRenderer {
			        public void render(Treeitem item, Object data) throws Exception {
			            if(data != null) {
			                //Contruct treecells
			                Treecell tc = new Treecell(data.toString());
			                Treerow tr;
			
			                /*
			                 * Since only one treerow is allowed, if treerow is not null,
			                 * append treecells to it. If treerow is null, contruct a new
			                 * treerow and attach it to item.
			                 */
			                if(item.getTreerow() == null) {
			                    tr = new Treerow();
			                    tr.setParent(item);
			                } else {
			                    tr = item.getTreerow();
			                    tr.getChildren().clear();
			                }
			
			                //Attach treecells to treerow
			                tc.setParent(tr);
			
			                item.setOpen(true);
			
			                tr.setDraggable("true");
			                tr.setDroppable("true");
			
			            }
			        }
			    }
			}
			
			    ]]></zscript>
			    <tree id="tree" apply="TreeComposer" />
			    <button id="btn" label="Click me, no error is correct." onClick='tree.setModel(new org.zkoss.zktest.test2.tree.BinaryTreeModel(new ArrayList(new org.zkoss.zktest.test2.BigList(10))));'/>
			</zk>

    }
 
   // Run syntax 2
    runZTL(zscript,
        () => {
        var btn: Widget = engine.$f("btn");

        click(btn);
        waitResponse();
        verifyFalse(jq(".z-error").exists());
    }
   );

  }
}