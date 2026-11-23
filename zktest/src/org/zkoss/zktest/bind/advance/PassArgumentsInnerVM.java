/* PassArgumentsInnerVM.java

		Purpose:
		
		Description:
		
		History:
				Mon May 10 15:31:23 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

public class PassArgumentsInnerVM {
	private String typeFromOuter;
	private String abcFromOuter;

	@Init
	public void init(@ExecutionArgParam("type") String type, @ExecutionArgParam String abc) {
		typeFromOuter = type;
		abcFromOuter = abc;
	}

	public String getTypeFromOuter() {
		return typeFromOuter;
	}

	public String getAbcFromOuter() {
		return abcFromOuter;
	}
}
