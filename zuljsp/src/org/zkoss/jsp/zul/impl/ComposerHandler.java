package org.zkoss.jsp.zul.impl;

import java.util.Collection;

import javax.servlet.jsp.JspException;

import org.zkoss.lang.Classes;
import org.zkoss.util.CollectionsX;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;

/**
 * 
 * @author Ian Tsai
 *
 */
public class ComposerHandler{
	
	private Composer composer;
	private ComposerExt composerExt;
	/**
	 * 
	 * @param apply
	 */
	/* package*/ ComposerHandler(Object apply){
		composer =  parseAppliedComposer( apply);
		if(composer!=null)
			composerExt = composer instanceof ComposerExt ? 
				(ComposerExt)composer: null;
	}
	
	/**
	 * 
	 * @return
	 */
	private static Composer parseAppliedComposer(Object o)
	{
		if(null==o)return null;
		try {
			if (o instanceof String) {
				final String s = (String)o;
				if (s.indexOf(',') >= 0)
					o = CollectionsX.parse(null, s, ',');
			}

			if (o instanceof Collection) {
				final Collection c = (Collection)o;
				int sz = c.size();
				switch (sz) {
				case 0: return null;
				case 1: o = c.iterator().next(); break;
				default: o = c.toArray(new Object[sz]); break;
				}
			}

			if (o instanceof Object[]) {
				final Object[] cs = (Object[])o;
				switch (cs.length) {
				case 0: return null;
				case 1: o = cs[0]; break;
				default: return new MultiComposer(cs);
				}
			}

			if (o instanceof String)
				o = Classes.newInstanceByThread(((String)o).trim());
			else if (o instanceof Class)
				o = ((Class)o).newInstance();

			if (o instanceof Composer)
				return (Composer)o;
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
		return null;
	}
	/**
	 * 
	 * @param comp
	 * @throws JspException
	 */
	public void doAfterCompose(Component comp) throws JspException {
		try {
			if(composer!=null)composer.doAfterCompose(comp);
		} catch (Exception e) {
			throw new JspException(e);
		}
	}
	/**
	 * 
	 * @param comp
	 * @throws Exception
	 */
	public void doBeforeComposeChildren(Component comp) throws Exception {
		if(composerExt!=null)
			composerExt.doBeforeComposeChildren(comp);		
	}
	/**
	 * 
	 * @param e
	 * @return
	 * @throws JspException
	 */
	public boolean doCatch(Throwable e) throws JspException {
		try {
			if(composerExt!=null)
				composerExt.doCatch(e);
		} catch (Exception e1) {
			StackTraceElement[] oriArr = e.getStackTrace();
			StackTraceElement[] erArr = e1.getStackTrace();
			StackTraceElement[] newErrArr = new StackTraceElement[oriArr.length+erArr.length];
			System.arraycopy(newErrArr, 0, oriArr, 0, oriArr.length);
			System.arraycopy(newErrArr, oriArr.length, erArr, 0, erArr.length);
			e.setStackTrace(newErrArr);
		}
		return false;
	}
	/**
	 * 
	 * @throws JspException
	 */
	public void doFinally() throws JspException {
		if(composerExt!=null){
			try {
				composerExt.doFinally();
			} catch (Exception e) {
				throw new JspException(e);
			}	
		}
	}
//	public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {
//	return null;
//}
	
}//end of class...
