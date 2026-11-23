/* F86_ZK_3970VM.java

        Purpose:
                
        Description:
                
        History:
                Tue Aug 28 10:06:05 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.Scope;
import org.zkoss.bind.annotation.ScopeParam;

public class F86_ZK_4036VM {
	private String _sysConfig;
	private String _value;
	private String _valueAuto;
	private String _name;
	
	@Init
	public void init(
		@ScopeParam (scopes = Scope.AUTO, value = "config") String sysConfig,
		@ScopeParam (scopes = Scope.AUTO, value = "user") String name,
		@ScopeParam (scopes = Scope.AUTO, value = "exec") String valueAuto,
		@ScopeParam (scopes = Scope.EXECUTION, value = "exec") String value) {
		_sysConfig = sysConfig;
		_name = name;
		_value = value;
		_valueAuto = valueAuto;
	}
	
	public String getSysConfig() {
		return _sysConfig;
	}
	
	public String getValue() {
		return _value;
	}
	
	public String getValueAuto() {
		return _valueAuto;
	}
	
	public String getName() {
		return _name;
	}
}
