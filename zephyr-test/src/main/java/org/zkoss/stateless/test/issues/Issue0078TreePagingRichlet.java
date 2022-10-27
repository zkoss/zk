/* Issue0078TreePagingRichlet.java

	Purpose:
		
	Description:
		
	History:
		3:54 PM 2021/12/20, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.issues;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.ITree;
import org.zkoss.stateless.sul.ITreechildren;
import org.zkoss.stateless.sul.ITreeitem;

/**
 * @author jumperchen
 */
@RichletMapping("/issue0078")
public class Issue0078TreePagingRichlet implements StatelessRichlet {
	@RichletMapping("")
	public IComponent pagingMold() {
		return ITree.of(IntStream.range(1, 51).mapToObj(i ->
				ITreeitem.of(String.valueOf(i)).withTreechildren(ITreechildren.of(
						IntStream.range(1, 11).mapToObj(j -> ITreeitem.of(String.valueOf(j))).collect(
								Collectors.toList())
				))).collect(
				Collectors.toList())).withMold("paging");
	}
}