/* MyWindow.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 26, 2007 10:25:44 AM 2007, Created by Ian Tsai
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jspdemo;

import java.io.PrintStream;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zul.Window;

/**
 * A test Window used in zkjsp-demo
 * @author ian
 */
public class MyWindow extends Window implements AfterCompose{
	
	/**
	 * 
	 *test if onCreat event will be triggerd.
	 */
	public void onCreate()
	{
		System.out.println("MyWindow OnCreate Event happend.");
	}

	public void afterCompose() {
		System.out.println("MyWindow::afterCompose(): start print Annotation...");
		recursivePrint(this, System.out, 0);
	}
	
	/**
	 * 
	 * @param comp
	 * @param out
	 */
	private void recursivePrint(Component comp, PrintStream out, int level)
	{
		StringBuffer sb = new StringBuffer();
		for(int i=level;i>0;i--)out.print(" |");
		dump(sb,comp);
		out.println(sb);
		for(Iterator itor = comp.getChildren().iterator();itor.hasNext();)
			recursivePrint((Component) itor.next(),out,level+1);
	}
	/**
	 * 
	 * @param sb
	 * @param comp
	 */
	private static void dump(StringBuffer sb, Component comp) 
	{
		String srClss=comp.getClass().toString();
		srClss = srClss.substring(srClss.lastIndexOf(".")+1);
		ComponentCtrl compCtrl = (ComponentCtrl)comp;
		
		sb.append("->"+srClss+"-"+comp.getId()+"=")
			.append(compCtrl.getAnnotations().size()+" : ")
			.append(compCtrl.getAnnotatedProperties().size());
		String prop;
		for (Iterator it = compCtrl.getAnnotations().iterator(); it.hasNext();) {
			Annotation annot = (Annotation) it.next();
			sb.append(" self").append(annot);
		}
		for (Iterator it = compCtrl.getAnnotatedProperties().iterator(); it.hasNext();) {
			prop = (String) it.next();
			sb.append(" ").append(prop)
			.append(compCtrl.getAnnotations(prop));
		}
	}
	
}
