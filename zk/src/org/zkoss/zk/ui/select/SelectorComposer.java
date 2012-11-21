/**
 * 
 */
package org.zkoss.zk.ui.select;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.AbstractComponent;
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
import org.zkoss.zk.ui.event.SerializableEventListener;
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
 *     &#064;WireVariable
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
 * The selector syntax is analogous to CSS selector syntax. For example,
 * <pre><code>
 * &#064;Wire("textbox") // matches all textboxes
 * &#064;Wire("#win") // matches components of id "#win" within the same IdSpace
 * &#064;Wire(".myclass") // matches components of sclass "myclass"
 * &#064;Wire("label[value = 'My Label']") // matches all labels with value "My Label"
 * &#064;Wire(":first-child") // matches all components which are first child
 * &#064;Wire("window textbox") // matches textbox who is a descendant of a window
 * &#064;Wire("window > textbox") // matches textbox who is a child of a window
 * &#064;Wire("window + textbox") // matches textbox who is the next sibling of a window
 * &#064;Wire("window ~ textbox") // matches textbox who is a younger sibling of a window
 * &#064;Wire("window > textbox.myclass:first-child") // conditions can be mixed
 * </code></pre>
 * 
 * @since 6.0.0
 * @author simonpai
 */
public class SelectorComposer<T extends Component> implements Composer<T>, ComposerExt<T>,
		ComponentCloneListener, ComponentActivationListener, java.io.Serializable {
	
	private static final long serialVersionUID = 5022810317492589463L;
	private static final String ON_WIRE_CLONE = "onWireCloneSelectorComposer";
	private static final String COMPOSER_CLONE = "COMPOSER_CLONE";
	
	
	private T _self;
	/** A list of resolvers (never null). A variable resolver is added automatically if
	 * {@link org.zkoss.zk.ui.select.annotation.VariableResolver} was annotated.
	 */
	protected final List<VariableResolver> _resolvers;
	
	//subscription information, for sharing between doBeforeCompose & doAfterCompose
	private Object _subsInfo;
	
	public SelectorComposer() {
		_resolvers = Selectors.newVariableResolvers(getClass(), SelectorComposer.class);
	}
	
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
	ComponentInfo compInfo) {
		Selectors.wireVariables(page, this, _resolvers);
		_subsInfo = getUtilityHandler().subscribeEventQueues(this);
		return compInfo;
	}
	@Override
	public void doBeforeComposeChildren(T comp) throws Exception {
		_self = comp;
		ConventionWires.wireController(comp, this);
	}
	@Override
	public void doAfterCompose(T comp) throws Exception {
		_self = comp; // just in case
		Selectors.wireComponents(comp, this, false);
		Selectors.wireEventListeners(comp, this); // first event listener wiring
		
		if(_subsInfo!=null){
			getUtilityHandler().postSubscriptionHandling(_subsInfo,_self);
			_subsInfo = null;//will not use in further lifetime
		}
		
		// register event to wire variables just before component onCreate
		comp.addEventListener(1000, "onCreate", new BeforeCreateWireListener());
		comp.addEventListener("onCreate", new AfterCreateWireListener());
	}
	
	/** Returns the component which applies to this composer.
	 * @since 6.0.1
	 */
	protected T getSelf() {
		return _self;
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
		return exec != null ? ((ExecutionCtrl)exec).getCurrentPage() : null;
	}
	
	private class BeforeCreateWireListener implements SerializableEventListener<Event> {
		private static final long serialVersionUID = 1L;
		// brought from GenericAutowireComposer
		public void onEvent(Event event) throws Exception {
			// wire components again so some late created object can be wired in (e.g. DataBinder)
			Selectors.wireComponents(event.getTarget(), SelectorComposer.this, true);
			_self.removeEventListener("onCreate", this); // called only once
		}
	}
	
	private class AfterCreateWireListener implements SerializableEventListener<Event> {
		private static final long serialVersionUID = 1L;
		public void onEvent(Event event) throws Exception {
			// second event listener wiring, for components created since doAfterCompose()
			Selectors.wireEventListeners(_self, SelectorComposer.this);
			_self.removeEventListener("onCreate", this); // called only once
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
	@Override
	public Object willClone(Component comp) {
		try {
			//following code refers to GenericAutowireComposer.
			final Execution exec = Executions.getCurrent();
			final int idcode = System.identityHashCode(comp);

			Composer composerClone = (Composer) exec.getAttribute(COMPOSER_CLONE+idcode);
			if (composerClone == null) {
				composerClone = (Composer) Classes.newInstance(getClass(), null);
				exec.setAttribute(COMPOSER_CLONE+idcode, composerClone);
				
				//cannot call doAfterCompose directly because the clone 
				//component might not be attach to Page yet
				Component post = new AbstractComponent();
				EventListener<Event> l = new CloneDoAfterCompose();
				Event evt = new Event(ON_WIRE_CLONE, post, new Object[]{comp,composerClone});
				
				//unlike GenericAutowireComposer, 
				//don't add listener to component when cloning, it cause problem/bugs.
				//use a fake component to handle event;
				post.addEventListener(ON_WIRE_CLONE, l);
				Events.postEvent(evt);
			}
			return composerClone;
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	
	//wire, called once after clone
	private class CloneDoAfterCompose
	implements SerializableEventListener<Event>, java.io.Serializable {
		private static final long serialVersionUID = 1L;
		// brought from GenericAutowireComposer
		@SuppressWarnings("unchecked")
		public void onEvent(Event event) throws Exception {
			//we don't need to remove it actually, since target are not at anywhere after event. 
			event.getTarget().removeEventListener(ON_WIRE_CLONE, this);
			
			final Component clone = (Component)((Object[])event.getData())[0];
			final SelectorComposer<Component> composerClone = 
				(SelectorComposer<Component>) ((Object[])event.getData())[1];; 
			ConventionWires.wireController(clone, composerClone);
			Selectors.wireVariables(clone.getPage(), this, composerClone._resolvers);
			Selectors.wireComponents(clone, this, false);
			Selectors.wireEventListeners(clone, this);
			
			Object subsInfo = getUtilityHandler().subscribeEventQueues(composerClone);
			if(subsInfo!=null){
				getUtilityHandler().postSubscriptionHandling(subsInfo,clone);
			}
		}
	}
	
	@Override
	public void didActivate(Component comp) {
		// rewire Session, Webapp and some other variable back, depending on
		// annotation
		Selectors.rewireComponentsOnActivate(comp, this);
		Selectors.rewireVariablesOnActivate(comp, this, _resolvers);
		Selectors.rewireEventListeners(comp, this);
		
		Object subsInfo = getUtilityHandler().resubscribeEventQueues(this);
		if(subsInfo!=null){
			getUtilityHandler().postSubscriptionHandling(subsInfo,comp);
		}
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
	
	// utility handler //
	/** An interface for SelectorComposer's functionality plug-in.
	 * @author simonpai
	 * @since 6.0.1
	 */
	public interface UtilityHandler {
		
		/** Subscribes annotated methods to the EventQueues.
		 */
		public Object subscribeEventQueues(Object controller);

		/** Re-subscribes annotated methods to the EventQueues, used in clustering
		 * environment.
		 */
		public Object resubscribeEventQueues(Object controller);
		
		/**
		 * Handling the subscription after a target(e.x. component) attached to this controller
		 * @since 6.5.1
		 */
		public void postSubscriptionHandling(Object subsInfo,Object target);
	}
	
	/** Default skeletal implementation of {@link UtilityHandler}.
	 * @author simonpai
	 * @since 6.0.1
	 */
	public static class UtilityHandlerImpl implements UtilityHandler {
		private static final long serialVersionUID = 1L;
		public Object subscribeEventQueues(Object controller) {return null;}
		public Object resubscribeEventQueues(Object controller) {return null;}
		public void postSubscriptionHandling(Object subsInfo,Object target) {}
	}
	
	private static final String UTILITY_HANDLER_KEY = 
		"org.zkoss.zk.ui.select.SelectorComposer.UtilityHandler.class";
	private static UtilityHandler _handler;
	
	protected static UtilityHandler getUtilityHandler() {
		loadUtilityHandler();
		return _handler;
	}
	
	private static final void loadUtilityHandler() {
		if (_handler != null)
			return;
		synchronized(SelectorComposer.class){
			if (_handler != null)
				return;
			String clsName = Library.getProperty(UTILITY_HANDLER_KEY);
			if (clsName != null) {
				try {
					final Object o = Classes.newInstanceByThread(clsName);
					if (!(o instanceof UtilityHandler)) {
						_handler = new UtilityHandlerImpl();
						throw new UiException(o.getClass().getName() + 
								" must implement " + UtilityHandler.class.getName());
					}
					_handler = (UtilityHandler) o;
				} catch (Exception ex) {
					_handler = new UtilityHandlerImpl();
					throw UiException.Aide.wrap(ex, "Unable to construct " + clsName);
				}
			} else {
				_handler = new UtilityHandlerImpl();
			}
		}
	}
}
