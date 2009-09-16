/* B30_1526742.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2009/9/16 ¤U¤È3:40:42 , Created by sam
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;

import java.util.List;

import org.junit.Test;

import com.thoughtworks.selenium.Selenium;

/**
 * @author sam
 *
 */
public class B30_1526742Test extends ZKTestCase{

	private String _target="B30-1526742.zul";
	private List<Selenium> _browsers;
	private String _url;
	
	public B30_1526742Test(){
		super();
		_browsers = getBrowsers(_target);
		_url = getUrl(_target);
	}
	
	@Test(expected=AssertionError.class)
	public void test1(){
		String compParent = getCompId(2);
		String compId1 = getCompId(3);
		String compId2 = getCompId(5);
		
		for(Selenium browser : _browsers){
			try{
				browser.start();
				try{
					browser.open(_url);
					Thread.sleep(1000);
					
					int totalWidth = getCompPos(browser, compParent).getWidth();
					
					int comp1Right = getCompPos(browser, compId1).getRight();
					int comp2Left = getCompPos(browser, compId2).getLeft();
					int diff = comp2Left - comp1Right;
					
					//comp1 and comp2 has about 20% space
					assertTrue( (totalWidth*0.2 - diff) < 50 );
					
					browser.close();
						
				}catch(InterruptedException e){ }
			}finally{
				browser.stop();
			}
		}
	}
}
