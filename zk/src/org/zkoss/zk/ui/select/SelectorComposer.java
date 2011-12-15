/**
 * 
 */
package org.zkoss.zk.ui.select;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.lang.Classes;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.ComponentActivationListener;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;
import org.zkoss.zk.ui.util.ConventionWires;

/**
 * <p>A composer analogous to GenericForwardComposer. Instead of wiring 
 * variables and adding event listeners by naming convention, this composer 
 * do the work by annotation and selectors. </p>
 * 
 * For example:
 * <pre><code>
 * MyComposer.java
 * 
 * public class MyComposer extends SelectorComposer {
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
 * @since 6.0.0
 * @author simonpai
 */
public class SelectorComposer<T extends Component> implements Composer<T>, ComposerExt<T>,
		ComponentCloneListener, ComponentActivationListener, java.io.Serializable {
	
	private static final long serialVersionUID = 5022810317492589463L;
	private static final String COMPOSER_CLONE = "COMPOSER_CLONE";
	private static final String 
		ON_CLONE_DO_AFTER_COMPOSE = "onCLONE_DO_AFTER_COMPOSE";
	
	private Component _self;
	protected final List<VariableResolver> resolvers = new ArrayList<VariableResolver>();
	
	public SelectorComposer() {
		resolvers.clear();
		Class<?> cls = this.getClass();
		while (cls != SelectorComposer.class) {
			org.zkoss.zk.ui.select.annotation.VariableResolver anno = 
				cls.getAnnotation(org.zkoss.zk.ui.select.annotation.VariableResolver.class);
			if (anno != null)
				for (Class<? extends VariableResolver> rc : anno.value()) {
					try {
						resolvers.add(rc.getConstructor().newInstance());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			cls = cls.getSuperclass();
		}
	}
	
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
	ComponentInfo compInfo) {
		Selectors.wireVariables(page, this, resolvers);
		return compInfo;
	}
	
	@Override
	public void doAfterCompose(T comp) throws Exception {
		_self = comp;
		Selectors.wireComponents(comp, this, false);
		Selectors.wireEventListeners(comp, this);
		
		//register event to wire variables just before component onCreate
		comp.addEventListener(1000, "onCreate", new BeforeCreateWireListener());
	}

	/** Returns the current page.
	 */
	protected Page getPage() {
		if (_self != null) {
			final Page page = _self.getPage();
			if (page != null)
				return page;
		}
		final Execution exec = Executions.getCurrent();
		return exec != null ? ((ExecutionCtrl)exec).getCurrentPage(): null;
	}
	
	private class BeforeCreateWireListener implements EventListener<Event> {
		// brought from GenericAutowireComposer
		public void onEvent(Event event) throws Exception {
			//wire components again so some late created object can be wired in (e.g. DataBinder)
			Selectors.wireComponents(event.getTarget(), SelectorComposer.this, true);
			//called only once
			_self.removeEventListener("onCreate", this);
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
			final SelectorComposer<Component> composerClone = 
				(SelectorComposer<Component>) event.getData(); 
			ConventionWires.wireController(clone, composerClone);
			composerClone.doAfterCompose(clone);
			clone.removeEventListener(ON_CLONE_DO_AFTER_COMPOSE, this);
		}
	}
	
	@Override
	public void doBeforeComposeChildren(Component comp) throws Exception {
		// no super
		ConventionWires.wireController(comp, this);
	}
	
	@Override
	public void didActivate(Component comp) {
		// rewire Session, Webapp and some other variable back, depending on
		// annotation
		Selectors.rewireComponentsOnActivate(comp, this);
		Selectors.rewireVariablesOnActivate(comp, this, resolvers);
	}
	
	@Override
	public void willPassivate(Component comp) { // do nothing
	}
	
	@Override
	public boolean doCatch(Throwable ex) throws Exception { //do nothing
		return false;
	}
	
	@Override
	public void doFinally() throws Exception { //do nothing
	}
	
}
