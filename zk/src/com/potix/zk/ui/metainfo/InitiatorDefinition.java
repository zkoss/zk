/* InitiatorDefinition.java

{{IS_NOTE
	$Id: InitiatorDefinition.java,v 1.2 2006/05/02 09:54:39 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Mar 31 14:24:37     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.metainfo;

import java.util.List;

import com.potix.lang.Classes;
import com.potix.zk.ui.util.Initiator;

/**
 * A definition of the initiator.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/05/02 09:54:39 $
 */
public class InitiatorDefinition {
	/** The class of the initiator. If null, use {@link #className}. */
	public final Class klass;
	/** The class name of the initiator. If null, use {@link #klass}. */
	public final String className;
	/** The arguments, never null (might with zero length). */
	public final String[] arguments;

	public InitiatorDefinition(Class cls, String[] args) {
		if (!Initiator.class.isAssignableFrom(cls))
			throw new IllegalArgumentException(Initiator.class+" must be implemented: "+cls);
		this.klass = cls;
		this.className = null;
		this.arguments = args != null ? args: new String[0];
	}
	public InitiatorDefinition(Class cls, List args) {
		this(cls, args != null ?
			(String[])args.toArray(new String[args.size()]): null);
	}
	/**
	 * @param clsnm the class name; it could be an EL expression.
	 */
	public InitiatorDefinition(String clsnm, String[] args)
	throws ClassNotFoundException {
		if (clsnm == null) throw new IllegalArgumentException(clsnm);
		if (clsnm.indexOf("${") < 0) {
			try {
				this.klass = Classes.forNameByThread(clsnm);
			} catch (ClassNotFoundException ex) {
				throw new ClassNotFoundException("Not found: "+clsnm, ex);
			}
			this.className = null;
		} else {
			this.klass = null;
			this.className = clsnm;
		}
		this.arguments = args != null ? args: new String[0];
	}
	/**
	 * @param clsnm the class name; it could be an EL expression.
	 */
	public InitiatorDefinition(String clsnm, List args)
	throws ClassNotFoundException {
		this(clsnm, args != null ?
			(String[])args.toArray(new String[args.size()]): null);
	}
}
