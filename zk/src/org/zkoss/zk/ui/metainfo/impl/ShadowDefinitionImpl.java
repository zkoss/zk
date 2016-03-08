/** ShadowDefinition.java.

	Purpose:
		
	Description:
		
	History:
		6:31:27 PM Oct 23, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.metainfo.impl;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinition;

/**
 * A shadow element definition.
 * @author jumperchen
 * @since 8.0.0
 */
public class ShadowDefinitionImpl extends ComponentDefinitionImpl {
	private final String _templateURI;

	public ShadowDefinitionImpl(LanguageDefinition langdef, PageDefinition pgdef, String name,
			Class<? extends Component> cls) {
		super(langdef, pgdef, name, cls);
		_templateURI = null;
	}

	public ShadowDefinitionImpl(LanguageDefinition langdef, PageDefinition pgdef, String name, String clsnm) {
		super(langdef, pgdef, name, clsnm);
		_templateURI = null;
	}

	ShadowDefinitionImpl(LanguageDefinition langdef, PageDefinition pgdef, String name, Class<? extends Component> cls,
			String templateURI) {
		super(langdef, pgdef, name, cls);
		_templateURI = templateURI;
	}

	public String getTemplateURI() {
		return _templateURI;
	}

	public boolean isShadowElement() {
		// TODO Auto-generated method stub
		return true;
	}

}
