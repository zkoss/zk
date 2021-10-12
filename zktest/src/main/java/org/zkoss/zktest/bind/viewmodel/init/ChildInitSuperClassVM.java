/* ChildInitSuperClassVM.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 11:20:31 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.viewmodel.init;

import org.zkoss.bind.annotation.Destroy;
import org.zkoss.bind.annotation.Init;

/**
 * @author rudyhuang
 */
@Init(superclass = true)
@Destroy(superclass = true)
public class ChildInitSuperClassVM extends InitVM {

}
