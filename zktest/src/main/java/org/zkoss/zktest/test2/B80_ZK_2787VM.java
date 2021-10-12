/* B80_ZK_2787VM.java

	Purpose:
		
	Description:
		
	History:
		5:52 PM 1/5/16, Created by jumperchen

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.sql.Time;
import java.util.Date;

import org.zkoss.bind.annotation.ImmutableFields;
import org.zkoss.bind.annotation.Init;

/**
 * @author jumperchen
 */
public class B80_ZK_2787VM {
	private Foo _f;

	@Init
	public void init() {
		_f = new Foo();
		_f.setVal(new Time(new Date().getTime()));
	}
	public void setFoo(Foo f) {
		_f = f;
	}
	@ImmutableFields
	public Foo getFoo() {
		return _f;
	}

	public static class Foo {
		private Time _val;
		public void setVal(Time val) {
			_val = val;
		}
		public Time getVal() {
			return _val;
		}
	}
}
