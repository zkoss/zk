/**
 * 
 */
package org.zkoss.zk.ui.select;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.zkoss.lang.Classes;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.ComponentActivationListener;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zk.ui.util.AbstractComposer;
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
public class SelectorComposer<T extends Component> extends AbstractComposer<T>
implements ComponentCloneListener, ComponentActivationListener, java.io.Serializable {
	
	private static final long serialVersionUID = 5022810317492589463L;
	private static final String ON_WIRE_CLONE = "onWireCloneSelectorComposer";
	
	private T _self;
	/** A list of resolvers (never null). A variable resolver is added automatically if
	 * {@link org.zkoss.zk.ui.select.annotation.VariableResolver} was annotated.
	 */
	protected final List<VariableResolver> _resolvers;
	
	public SelectorComposer() {
		_resolvers = Selectors.newVariableResolvers(getClass(), SelectorComposer.class);
	}
	
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
	ComponentInfo compInfo) {
		Selectors.wireVariables(page, this, _resolvers);
		return super.doBeforeCompose(page, parent, compInfo);
	}
	@Override
	public void doBeforeComposeChildren(T comp) throws Exception {
		super.doBeforeComposeChildren(_self = comp);
	}
	@Override
	public void doAfterCompose(T comp) throws Exception {
		super.doAfterCompose(comp);

		_self = comp; //just in case
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
		return super.getPage();
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
	 */
	protected void alert(String m) {
		// brought from GenericAutowireComposer
		try {
			if (_alert == null) {
				final Class<?> mboxcls = 
					Classes.forNameByThread("org.zkoss.zul.Messagebox");
				_alert = mboxcls.getMethod("show", new Class[] {String.class});
			}
			_alert.invoke(null, m);
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
	 */
	public Object willClone(Component comp) {
		try {
			Composer<?> composerClone = getClass().newInstance();
				
			//cannot wire directly because the clone 
			//component might not be attach to Page yet
			comp.addEventListener(ON_WIRE_CLONE, new CloneDoAfterCompose());
			Events.postEvent(new Event(ON_WIRE_CLONE, comp, composerClone));
			return composerClone;
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	
	//wire, called once after clone
	private static class CloneDoAfterCompose
	implements EventListener<Event>, java.io.Serializable {
		// brought from GenericAutowireComposer
		@SuppressWarnings("unchecked")
		public void onEvent(Event event) throws Exception {
			final Component clone = (Component) event.getTarget();
			final SelectorComposer<Component> composerClone = 
				(SelectorComposer<Component>) event.getData(); 
			ConventionWires.wireController(clone, composerClone);
			Selectors.wireVariables(clone.getPage(), this, composerClone._resolvers);
			Selectors.wireComponents(clone, this, false);
			Selectors.wireEventListeners(clone, this);
			clone.removeEventListener(ON_WIRE_CLONE, this);
		}
	}
	
	@Override
	public void didActivate(Component comp) {
		// rewire Session, Webapp and some other variable back, depending on
		// annotation
		Selectors.rewireComponentsOnActivate(comp, this);
		Selectors.rewireVariablesOnActivate(comp, this, _resolvers);
	}
	
	@Override
	public void willPassivate(Component comp) { // do nothing
	}
}
