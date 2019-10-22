/** NestedApplyVM.java.

	Purpose:
		
	Description:
		
	History:
		2:07:15 PM Nov 24, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.vm;

import java.util.Arrays;
import java.util.List;

/**
 * @author jumperchen
 *
 */
public class NestedApplyVM {
	private List<String> templates = Arrays.asList("first", "second", "third");
	public void setTemplates(List<String> templates) {}
	public List<String> getTemplates() {
		return templates;
	}
}
