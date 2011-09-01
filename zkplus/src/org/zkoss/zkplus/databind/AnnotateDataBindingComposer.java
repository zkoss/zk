package org.zkoss.zkplus.databind;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;

/**
 * 
 * It's another way to replace the &lt;?init class="org.zkoss.zkplus.databind.AnnotateDataBinderInit"?&gt; in zul
 * And initiate annotate data binding by apply this composer.
 * A component can apply many composers, remember to apply this composer at the last.
 * For example:
 * &lt;window apply="xxx.MyComposer,org.zkoss.zkplus.databind.AnnotateDataBindingComposer"&gt;
 *
 */
public class AnnotateDataBindingComposer implements Composer, java.io.Serializable {
	private static final long serialVersionUID = -1572346127326530607L;
	protected AnnotateDataBinder _binder;
	
	public void doAfterCompose(Component comp) throws Exception {
		_binder = new AnnotateDataBinder(comp);
		comp.setAttribute("binder",_binder);
		_binder.loadAll();
	}
}
