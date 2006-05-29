/* TreeArrayDbg.java

{{IS_NOTE

	Purpose: Debug version of TreeArray
	Description: 
	History:
	 2001/5/11, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util;

/**
 * Extends TreeArray to provide debug information.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 */
public class TreeArrayDbg extends TreeArray {
	private final void dump(java.io.PrintStream out, RbEntry p, int level) {
		if (p==null)
			return;

		for (int j=0; j<level; ++j)
			out.print('.');

		if (p.parent!=null)
			out.print(p.parent.left==p ? 'L': 'R');
		out.print(p.color==BLACK ? 'b' : 'r');
		out.print(" " + p.leftNum + " " + p.element);

		out.println();
		dump(out, p.left,  level+1);
		dump(out, p.right, level+1);
	}
		
	protected final void dump(java.io.PrintStream out) {
		dump(out, _root, 0);
	}

	private final int count(RbEntry p) {
		if (p==null)
			return 0;
		return count(p.left) + count(p.right) + 1;
	}

	private final boolean check(java.io.PrintStream out, RbEntry p, int level) {
		if (p==null)
			return true;

		boolean ok = true;

		//check child's parent
		if (p.left!=null && p.left.parent!=p) {
			ok = false;
			out.println("err: left's parent incorrect");
		}
		if (p.right!=null && p.right.parent!=p) {
			ok = false;
			out.println("err: right's parent incorrect");
		}

		//check leftNum
		int v = p.left!=null ? count(p.left): 0;
		if (v != p.leftNum) {
			ok = false;
			out.println("err: leftNum, expect=" + v + ", real=" + p.leftNum);
		}

		//check color
		if (p.color==RED && p.parent!=null && p.parent.color==RED) {
			ok = false;
			out.println("err: both parent and this node are red");
		}
		if (!ok)
			out.println("^-- this error node is level=" + level
				+ (p.parent==null ? "@root" : p.parent.left==p ? "@left" : "@right")
				+ " element=" + p.element + " leftNum=" + p.leftNum);

		if (!check(out, p.left, level+1))
			ok = false;
		if (!check(out, p.right, level+1))
			ok = false;
		return ok;
	}
	protected final boolean check(java.io.PrintStream out) {
		boolean ok = check(out, _root, 0);
		if (!ok)
			dump(out);
		return ok;
	}
}
