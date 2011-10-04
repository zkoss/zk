/**
 * 
 */
package org.zkoss.zk.ui.select;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.GenericComposer;

/**
 * <p>A composer analogous to GenericForwardComposer. Instead of wiring 
 * variables and adding event listeners by naming convention, this composer 
 * do the work by annotation and selectors. </p>
 * 
 * For example:
 * <pre><code>
 * MyComposer.java
 * 
 * public class MyComposer extends GenericAnnotatedComposer {
 *     
 *     &#064;Wire("#win")
 *     private Window myWin;
 *     
 *     // implicit variables
 *     &#064;Wire
 *     private Desktop desktop;
 *     
 *     &#064;Listen("onClick = button#btn")
 *     public void onSubmit(Event event){
 *         // do something ...
 *     }
 *     
 * }
 * </code></pre>
 * 
 * @author simonpai
 */
public class GenericAnnotatedComposer<T extends Component> extends GenericComposer<T>
	implements ComponentCloneListener {
	
	private static final long serialVersionUID = 5022810317492589463L;
	private static final String COMPOSER_CLONE = "COMPOSER_CLONE";
	private static final String 
		ON_CLONE_DO_AFTER_COMPOSE = "onCLONE_DO_AFTER_COMPOSE";
	
	private Component _self;
	private final boolean _ignoreZScript;
	private final boolean _ignoreXel;
	
	protected GenericAnnotatedComposer(){
		this(true, true);
	}
	
	protected GenericAnnotatedComposer(boolean ignoreZScript, boolean ignoreXel){
		_ignoreZScript = ignoreZScript;
		_ignoreXel = ignoreXel;
	}
	
	@Override
	public void doAfterCompose(T comp) throws Exception {
		super.doAfterCompose(comp);
		_self = comp;
		autowire(comp);
		// adding event listeners is only done here
		Selectors.wireEventListeners(comp, this); 
		
		//register event to wire variables just before component onCreate
		comp.addEventListener("onCreate", new BeforeCreateWireListener());
	}
	
	/**
	 * Redo variable auto-wiring.
	 */
	protected final void rewire(){
		autowire(_self);
	}
	
	private class BeforeCreateWireListener implements EventListener<Event> {
		// brought from GenericAutowireComposer
		public void onEvent(Event event) throws Exception {
			//wire variables again so some late created object can be wired in(e.g. DataBinder)
			autowire(event.getTarget());
			//called only once
			event.getTarget().removeEventListener("onCreate", this);
		}
	}
	
	// alert //
	private static Method _alert;
	
	/** Shortcut to call Messagebox.show(String).
	 * @since 3.0.7 
	 */
	protected void alert(String m) {
		// brought from GenericAutowireComposer
		try {
			if (_alert == null) {
				final Class<?> mboxcls = 
					Classes.forNameByThread("org.zkoss.zul.Messagebox");
				_alert = mboxcls.getMethod("show", new Class[] {String.class});
			}
			_alert.invoke(null, new Object[] {m});
		} catch (InvocationTargetException e) {
			throw UiException.Aide.wrap(e);
		} catch (Exception e) {
			//ignore
		}
	}
	
	/** Internal use only. Call-back method of CloneComposerListener. You shall 
	 * not call this method directly. Clone this Composer when its applied 
	 * component is cloned.
	 * @param comp the clone of the applied component
	 * @return A clone of this Composer. 
	 * @since 3.5.2
	 */
	public Object willClone(Component comp) {
		// brought from GenericAutowireComposer
		try {
			final Execution exec = Executions.getCurrent();
			final int idcode = System.identityHashCode(comp);
			Composer<?> composerClone = 
				(Composer<?>) exec.getAttribute(COMPOSER_CLONE + idcode);
			if (composerClone == null) {
				composerClone = (Composer<?>) Classes.newInstance(getClass(), null);
				exec.setAttribute(COMPOSER_CLONE + idcode, composerClone);
				
				//cannot call doAfterCompose directly because the clone 
				//component might not be attach to Page yet
				comp.addEventListener(ON_CLONE_DO_AFTER_COMPOSE, 
						new CloneDoAfterCompose());
				Events.postEvent(new Event(ON_CLONE_DO_AFTER_COMPOSE, comp, 
						composerClone));
			}
			return composerClone;
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	
	//doAfterCompose, called once after clone
	private static class CloneDoAfterCompose implements EventListener<Event> {
		// brought from GenericAutowireComposer
		@SuppressWarnings("unchecked")
		public void onEvent(Event event) throws Exception {
			final Component clone = (Component) event.getTarget();
			final GenericAnnotatedComposer composerClone = 
				(GenericAnnotatedComposer) event.getData(); 
			Selectors.wireController(clone, composerClone);
			composerClone.doAfterCompose(clone);
			clone.removeEventListener(ON_CLONE_DO_AFTER_COMPOSE, this);
		}
	}
	
	// helper //
	private void autowire(Component comp){
		IdSpace spaceOwner = comp.getSpaceOwner();
		if(spaceOwner instanceof Page)
			Selectors.wireVariables(
					(Page) spaceOwner, this, _ignoreZScript, _ignoreXel);
		else
			Selectors.wireVariables(
					(Component) spaceOwner, this, _ignoreZScript, _ignoreXel);
	}
	
	@Override
	public void doBeforeComposeChildren(Component comp) throws Exception {
		// no super
		Selectors.wireController(comp, this);
	}
	
}
