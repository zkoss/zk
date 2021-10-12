/* B85_ZK_3988MyComp.java

        Purpose:
                
        Description:
                
        History:
                Fri Jul 13 17:28:07 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;
import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Textbox;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class B85_ZK_3988MyComp extends HtmlMacroComponent {

	private static final long serialVersionUID = 1L;
	@Wire
	Textbox line1;

	public B85_ZK_3988MyComp() {
		super();
		// force the template to be applied, and to wire members automatically
		compose();
	}

	@Override
	public void afterCompose() {
		super.afterCompose();
		line1.setWidth("50px");
		Clients.log("did something on the component");
	}

}
