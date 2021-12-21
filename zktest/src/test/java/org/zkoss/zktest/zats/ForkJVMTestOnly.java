/* ForkJVM.java

	Purpose:
		
	Description:
		
	History:
		7:18 PM 2021/11/20, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats;

/**
 * Used for isolating JVM for test in ZATS tests (@Category).
 * It means the flag of <t>reuseForks=false</t> for maven surefire.
 * @author jumperchen
 */
public interface ForkJVMTestOnly {
}
