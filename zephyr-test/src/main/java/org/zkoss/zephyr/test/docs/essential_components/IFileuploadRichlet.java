/* IFileuploadRichlet.java

	Purpose:

	Description:

	History:
		Tue Apr 19 18:17:14 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.essential_components;

import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.zpr.IFileupload;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IFileupload} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload">IFileupload</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IFileupload
 */
@RichletMapping("/essential_components/iFileupload")
public class IFileuploadRichlet implements StatelessRichlet {
	@RichletMapping("/fileupload")
	public IFileupload fileupload() {
		return IFileupload.of("upload");
	}
}