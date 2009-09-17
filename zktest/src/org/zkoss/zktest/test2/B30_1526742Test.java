/* B30_1526742.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2009/9/16 �U��3:40:42 , Created by sam
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.test2;

import java.util.List;

import org.junit.Before;
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
	
	
	@Override
	@Before
	public void setUp(){
		_browsers = getBrowsers(_target);
		_url = getUrl(_target);
	}
	
	@Test(expected=AssertionError.class)
	public void test1(){
		String compParent = uuid(2);
		String compId1 = uuid(3);
		String compId2 = uuid(5);
		
		for(Selenium browser : _browsers){
			try{
				browser.start();
				browser.open(_url);
					
				int totalWidth = browser.getElementWidth(compParent).intValue();
				int comp1Right = browser.getElementPositionLeft(compId1).intValue() + browser.getElementWidth(compId1).intValue();
				int comp2Left = browser.getElementPositionLeft(compId2).intValue();
				int diff = comp2Left - comp1Right;	
					
				//comp1 and comp2 has about 20% space
				assertTrue( (totalWidth*0.2 - diff) < 50 );
					
				browser.close();
			}finally{
				browser.stop();
			}
		}
	}
}
