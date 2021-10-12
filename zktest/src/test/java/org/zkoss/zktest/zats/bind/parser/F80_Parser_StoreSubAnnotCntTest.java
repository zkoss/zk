package org.zkoss.zktest.zats.bind.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class F80_Parser_StoreSubAnnotCntTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent rt = desktop.query("#root");
		ComponentAgent w1 = desktop.query("#root #w1");
		ComponentAgent l1 = desktop.query("#root #w1").getChild(0);
		ComponentAgent dabtn = desktop.query("#root #w1").getChild(1);
		ComponentAgent w2 = desktop.query("#root #w1 #w2");
		ComponentAgent l2 = desktop.query("#root #w1 #w2").getChild(0);
		ComponentAgent testb1 = desktop.query("#root #w1 #w2 #test1");
		ComponentAgent w3 = desktop.query("#root #w3");
		ComponentAgent l3 = desktop.query("#root #w3").getChild(0);
		ComponentAgent testb2 =  desktop.query("#root #w3 #test2");
		ComponentAgent testb3 =  desktop.query("#root #w3 #test3");
		ComponentAgent c =  desktop.query("#root #children_binding");
		
		int rt_scnt = rt.as(Window.class).getSubBindingAnnotationCount();
		int w1_scnt = w1.as(Window.class).getSubBindingAnnotationCount();
		int l1_scnt = l1.as(Label.class).getSubBindingAnnotationCount();
		int dabtn_scnt = dabtn.as(Button.class).getSubBindingAnnotationCount();
		int w2_scnt = w2.as(Window.class).getSubBindingAnnotationCount();
		int l2_scnt = l2.as(Label.class).getSubBindingAnnotationCount();
		int testb1_scnt = testb1.as(Button.class).getSubBindingAnnotationCount();
		int w3_scnt = w3.as(Window.class).getSubBindingAnnotationCount();
		int l3_scnt = l3.as(Label.class).getSubBindingAnnotationCount();
		int testb2_scnt = testb2.as(Button.class).getSubBindingAnnotationCount();
		int testb3_scnt = testb3.as(Button.class).getSubBindingAnnotationCount();
		int c_scnt = c.as(Window.class).getSubBindingAnnotationCount();
		
		//labels
		assertEquals(l1_scnt, 1);
		assertEquals(l2_scnt, 1);
		assertEquals(l3_scnt, 1);

		//button
		assertEquals(testb1_scnt, 1);
		assertEquals(dabtn_scnt, 1);
		assertEquals(testb2_scnt, 1);
		assertEquals(testb3_scnt, 1);
		
		//windows
		assertEquals(w2_scnt, l2_scnt + testb1_scnt+ 1);
		assertEquals(w1_scnt, l1_scnt + w2_scnt + dabtn_scnt + 1);
		assertEquals(w3_scnt, l3_scnt + testb2_scnt + testb3_scnt + 1);
		assertEquals(rt_scnt, w1_scnt + w3_scnt + c_scnt);
		
		//children binding
		assertEquals(c_scnt, 5);
		
		//Event - detach then attach (Composer)
		dabtn.click();
		w2 = desktop.query("#root #w1 #w2");
		rt_scnt = rt.as(Window.class).getSubBindingAnnotationCount();
		w1_scnt = w1.as(Window.class).getSubBindingAnnotationCount();
		w2_scnt = w2.as(Window.class).getSubBindingAnnotationCount();
		l2_scnt = l2.as(Label.class).getSubBindingAnnotationCount();
		assertEquals(l1_scnt, 1);
		assertEquals(l2_scnt, 1);
		assertEquals(testb1_scnt, 1);
		//windows
		assertEquals(w2_scnt, l2_scnt + testb1_scnt+ 1);
		assertEquals(w1_scnt, l1_scnt + dabtn_scnt + w2_scnt + 1);
		assertEquals(rt_scnt, w1_scnt + w3_scnt + c_scnt);
		
		//Event - add label
		ComponentAgent add = desktop.query("#root #add");
		add.click();
		ComponentAgent l5 = desktop.query("#root #w3 #l5");
		int l5_scnt = l5.as(Label.class).getSubBindingAnnotationCount();
		assertEquals(l5_scnt, 1);
		rt_scnt = rt.as(Window.class).getSubBindingAnnotationCount();
		w3_scnt = w3.as(Window.class).getSubBindingAnnotationCount();
		assertEquals(w3_scnt, l3_scnt + testb2_scnt + testb3_scnt  + l5_scnt + 1);
		assertEquals(rt_scnt, w1_scnt + w3_scnt + c_scnt);
				
		//Event - add bind annotation
		ComponentAgent add_b = desktop.query("#root #add_bind");
		add_b.click();
		ComponentAgent l4 = desktop.query("#root #w3").getLastChild();
		int l4_scnt = l4.as(Label.class).getSubBindingAnnotationCount();
		assertEquals(l4_scnt, 1);
		w3_scnt = w3.as(Window.class).getSubBindingAnnotationCount();
		assertEquals(w3_scnt, l3_scnt + testb2_scnt + testb3_scnt  + l5_scnt + l4_scnt + 1);
		rt_scnt = rt.as(Window.class).getSubBindingAnnotationCount();
		assertEquals(rt_scnt, w1_scnt + w3_scnt + c_scnt);
		
		//Event - remove binding one
		ComponentAgent remove_binding_one = desktop.query("#root #remove_binding_one");
		remove_binding_one.click();
		testb2_scnt = testb2.as(Button.class).getSubBindingAnnotationCount();
		assertEquals(testb2_scnt, 1);
		w3_scnt = w3.as(Window.class).getSubBindingAnnotationCount();
		assertEquals(w3_scnt, l3_scnt + testb2_scnt + testb3_scnt  + l5_scnt + l4_scnt + 1);
		rt_scnt = rt.as(Window.class).getSubBindingAnnotationCount();
		assertEquals(rt_scnt,  w1_scnt + w3_scnt + c_scnt);
		
		//Event - remove binding all
		ComponentAgent remove_binding_all = desktop.query("#root #remove_binding_all");
		remove_binding_all.click();
		testb3_scnt = testb3.as(Button.class).getSubBindingAnnotationCount();
		assertEquals(testb3_scnt, 0);
		w3_scnt = w3.as(Window.class).getSubBindingAnnotationCount();
		assertEquals(w3_scnt, l3_scnt + testb2_scnt + testb3_scnt  + l5_scnt + l4_scnt + 1);
		rt_scnt = rt.as(Window.class).getSubBindingAnnotationCount();
		assertEquals(rt_scnt,  w1_scnt + w3_scnt + c_scnt);
		
		//Event - more bind annotation
		add_b.click();
		l4_scnt = l4.as(Label.class).getSubBindingAnnotationCount();
		assertEquals(l4_scnt, 1);
		rt_scnt = rt.as(Window.class).getSubBindingAnnotationCount();
		assertEquals(rt_scnt, w1_scnt + w3_scnt + c_scnt);
		
		//Event - add and bind first
		ComponentAgent add_bf = desktop.query("#root #add_bindfirst");
		add_bf.click();
		ComponentAgent w_bf = desktop.query("#root").getLastChild();
		ComponentAgent l_bf = w_bf.getFirstChild();
		int l_bf_scnt = l_bf.as(Label.class).getSubBindingAnnotationCount();
		assertEquals(l_bf_scnt, 1);
		
		int w_bf_scnt = w_bf.as(Window.class).getSubBindingAnnotationCount();
		assertEquals(w_bf_scnt, l_bf_scnt + 1);
		
		rt_scnt = rt.as(Window.class).getSubBindingAnnotationCount();
		assertEquals(rt_scnt, w1_scnt + w3_scnt + c_scnt + w_bf_scnt);
		
		//Event - add and bind last
		ComponentAgent add_bl = desktop.query("#root #add_bindlast");
		add_bl.click();
		ComponentAgent w_bl = desktop.query("#root").getLastChild();
		ComponentAgent l_bl = w_bf.getFirstChild();
		int l_bl_scnt = l_bl.as(Label.class).getSubBindingAnnotationCount();
		assertEquals(l_bl_scnt, 1);
		int w_bl_scnt = w_bl.as(Window.class).getSubBindingAnnotationCount();
		assertEquals(w_bl_scnt, l_bl_scnt + 1);
		rt_scnt = rt.as(Window.class).getSubBindingAnnotationCount();
		assertEquals(rt_scnt, w1_scnt + w3_scnt + c_scnt + w_bf_scnt + w_bl_scnt);
		
		//Event - move w2 to root as last child
		ComponentAgent move_append = desktop.query("#root #move_append");
		move_append.click();
		w1_scnt = w1.as(Window.class).getSubBindingAnnotationCount();
		w2 = desktop.query("#root #w2");
		w2_scnt = w2.as(Window.class).getSubBindingAnnotationCount();
		rt_scnt = rt.as(Window.class).getSubBindingAnnotationCount();
	   
		assertEquals(w1_scnt, l1_scnt + dabtn_scnt + 1);
		assertEquals(w2_scnt, l2_scnt + testb1_scnt + 1);
		assertEquals(rt_scnt,  w1_scnt + w2_scnt + w3_scnt + c_scnt + w_bf_scnt + w_bl_scnt);
		
		//Event - move back
		ComponentAgent move_setParent = desktop.query("#root #move_setParent");
		move_setParent.click();
		w1_scnt = w1.as(Window.class).getSubBindingAnnotationCount();
		w2 = desktop.query("#root #w1 #w2");
		w2_scnt = w2.as(Window.class).getSubBindingAnnotationCount();
		rt_scnt = rt.as(Window.class).getSubBindingAnnotationCount();
		assertEquals(w2_scnt, l2_scnt + testb1_scnt + 1);
		assertEquals(w1_scnt, l1_scnt + dabtn_scnt + w2_scnt + 1);
		assertEquals(rt_scnt,  w1_scnt + w3_scnt + c_scnt + w_bf_scnt + w_bl_scnt);
		
		//Event - remove w3
		ComponentAgent remove = desktop.query("#root #remove");
		remove.click();
		rt_scnt = rt.as(Window.class).getSubBindingAnnotationCount();
		assertEquals(rt_scnt,  w1_scnt + c_scnt + w_bf_scnt + w_bl_scnt);
	}
}
