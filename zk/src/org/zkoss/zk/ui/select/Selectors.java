/**
 * 
 */
package org.zkoss.zk.ui.select;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.zkoss.lang.Strings;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.select.impl.ComponentIterator;
import org.zkoss.zk.ui.select.impl.Reflections;
import org.zkoss.zk.ui.select.impl.Reflections.FieldRunner;
import org.zkoss.zk.ui.select.impl.Reflections.MethodRunner;

/**
 * A collection of selector related utilities. 
 * @since 6.0.0
 * @author simonpai
 */
public class Selectors {
	
	/**
	 * Returns an Iterable that iterates through all Components matched by the
	 * selector. 
	 * @param page the reference page for selector
	 * @param selector the selector string
	 * @return an Iterable of Component
	 */
	public static Iterable<Component> iterable(final Page page, 
			final String selector) {
		return new Iterable<Component>() {
			public Iterator<Component> iterator() {
				return new ComponentIterator(page, selector);
			}
		};
	}
	
	/**
	 * Returns an Iterable that iterates through all Components matched by the
	 * selector. 
	 * @param root the reference component for selector
	 * @param selector the selector string
	 * @return an Iterable of Component
	 */
	public static Iterable<Component> iterable(final Component root, 
			final String selector){
		return new Iterable<Component>() {
			public Iterator<Component> iterator() {
				return new ComponentIterator(root, selector);
			}
		};
	}
	
	/**
	 * Returns a list of Components that match the selector.
	 * @param page the reference page for selector
	 * @param selector the selector string
	 * @return a List of Component
	 */
	public static List<Component> find(Page page, String selector) {
		return toList(iterable(page, selector));
	}
	
	/**
	 * Returns a list of Components that match the selector.
	 * @param root the reference component for selector
	 * @param selector the selector string
	 * @return a List of Component
	 */
	public static List<Component> find(Component root, String selector) {
		return toList(iterable(root, selector));
	}
	
	/**
	 * Returns the ith component that matches the selector
	 * @param page the reference page for selector
	 * @param selector the selector string
	 * @param index 1-based index (1 means the first component found)
	 * @return Component, null if not found
	 */
	public static Component find(Page page, String selector, int index) {
		return getIthItem(new ComponentIterator(page, selector), index);
	}
	
	/**
	 * Returns the ith component that matches the selector
	 * @param root root the reference component for selector
	 * @param selector selector the selector string
	 * @param index 1-based index (1 means the first component found)
	 * @return Component, null if not found
	 */
	public static Component find(Component root, String selector, int index) {
		return getIthItem(new ComponentIterator(root, selector), index);
	}
	
	/**
	 * Wire variables to controller, including XEL variables, implicit variables.
	 * @param component the reference component
	 * @param controller the controller object to be injected with variables
	 */
	public static void wireVariables(Component component, Object controller, 
			List<VariableResolver> extraResolvers) {
		new Wirer(controller, false).wireVariables(
				new ComponentFunctor(component), extraResolvers);
	}
	
	/**
	 * Wire variables to controller, including XEL variables, implicit variables.
	 * @param page the reference page
	 * @param controller the controller object to be injected with variables
	 */
	public static void wireVariables(Page page, Object controller, 
			List<VariableResolver> extraResolvers) {
		new Wirer(controller, false).wireVariables(
				new PageFunctor(page), extraResolvers);
	}
	
	/**
	 * Wire components to controller.
	 * @param component the reference component for selector
	 * @param controller the controller object to be injected with variables
	 * @param ignoreNonNull ignore wiring when the value of the field is a 
	 * Component (non-null) or a non-empty Collection.
	 */
	public static void wireComponents(Component component, Object controller, 
			boolean ignoreNonNull) {
		new Wirer(controller, false).wireComponents(
				new ComponentFunctor(component), ignoreNonNull);
	}
	
	/**
	 * Wire components to controller.
	 * @param page the reference page for selector
	 * @param controller the controller object to be injected with variables
	 * @param ignoreNonNull ignore wiring when the value of the field is a 
	 * Component (non-null) or a non-empty Collection.
	 */
	public static void wireComponents(Page page, Object controller, boolean ignoreNonNull) {
		new Wirer(controller, false).wireComponents(new PageFunctor(page), ignoreNonNull);
	}
	
	/*package*/ static void rewireVariablesOnActivate(Component component, 
			Object controller, List<VariableResolver> extraResolvers) {
		// called when activated
		new Wirer(controller, true).wireVariables(
				new ComponentFunctor(component), extraResolvers);
	}
	
	/*package*/ static void rewireComponentsOnActivate(Component component, 
			Object controller) {
		// called when activated
		new Wirer(controller, true).wireComponents(
				new ComponentFunctor(component), false);
	}
	
	/**
	 * Add event listeners to components based on the controller.
	 * @param component the reference component for selector 
	 * @param controller the controller of event listening methods
	 */
	public static void wireEventListeners(final Component component, 
			final Object controller) {
		Reflections.forMethods(controller.getClass(), Listen.class, 
				new MethodRunner<Listen>() {
			public void onMethod(Class<?> clazz, Method method, Listen anno) {
				// check method signature
				if((method.getModifiers() & Modifier.STATIC) != 0) 
					throw new UiException("Cannot add forward to static method: " + 
							method.getName());
				// method should have 0 or 1 parameter
				if(method.getParameterTypes().length > 1) 
					throw new UiException("Event handler method should have " + 
							"at most one parameter: " + method.getName());
				
				for(String[] strs : splitListenAnnotationValues(anno.value())){
					String name = strs[0];
					if(name == null) name = "onClick";
					Iterable<Component> iter = iterable(component, strs[1]);
					// no forwarding, just add to event listener
					for (Component c : iter)
						c.addEventListener(name, new ComposerEventListener(method, controller));
				}
			}
		});
	}

	/** Creates a list of instances of {@link VariableResolver} based
	 * on the annotation of the given class.
	 * If none of annotataion is found, an empty list is returned.
	 */
	public static List<VariableResolver> newVariableResolvers(Class<?> cls) {
		final List<VariableResolver> resolvers = new ArrayList<VariableResolver>();
		while (cls != SelectorComposer.class) {
			final org.zkoss.zk.ui.select.annotation.VariableResolver anno = 
				cls.getAnnotation(org.zkoss.zk.ui.select.annotation.VariableResolver.class);
			if (anno != null)
				for (Class<? extends VariableResolver> rc : anno.value()) {
					try {
						resolvers.add(rc.getConstructor().newInstance());
					} catch (Exception e) {
						throw UiException.Aide.wrap(e);
					}
				}
			cls = cls.getSuperclass();
		}
		return resolvers;
	}

	// helper //
	private static String[][] splitListenAnnotationValues(String str) {
		List<String[]> result = new ArrayList<String[]>();
		int len = str.length();
		boolean inSqBracket = false;
		boolean inQuote = false;
		boolean escaped = false;
		String evtName = null;
		int i = 0;
		
		for (int j = 0; j < len; j++) {
			char c = str.charAt(j);
			
			if(!escaped)
				switch(c){
				case '[': inSqBracket = true; break;
				case ']': inSqBracket = false; break;
				case '"':
				case '\'':
					inQuote = !inQuote;
					break;
				case '=':
					if(inSqBracket || inQuote) break;
					if(evtName != null)
						throw new UiException("Illegal value of @Listen: " + str);
					evtName = str.substring(i, j).trim();
					// check event name: onX
					if(evtName.length() < 3 || !evtName.startsWith("on") || 
							!Character.isUpperCase(evtName.charAt(2)))
						throw new UiException("Illegal value of @Listen: " + str);
					i = j + 1;
					break;
				case ';':
					if(inQuote) break;
					String target = str.substring(i, j).trim();
					// check selector string: nonempty
					if(target.length() == 0)
						throw new UiException("Illegal value of @Listen: " + str);
					result.add(new String[]{evtName, target});
					i = j + 1;
					evtName = null;
					break;
				default:
					// do nothing
				}
			
			escaped = !escaped && c == '\\'; 
		}
		
		// flush last chunk if any
		if (i < len) {
			String last = str.substring(i).trim();
			if (last.length() > 0)
				result.add(new String[]{evtName, last});
		}
		return result.toArray(new String[0][0]);
	}
	
	private static <T> List<T> toList(Iterable<T> iterable) {
		List<T> result = new ArrayList<T>();
		for (T t : iterable)
			result.add(t);
		return result;
	}
	
	// helper: auto wire //
	private static class Wirer {
		
		private final Object _controller;
		private final boolean _rewire;
		
		private Wirer(Object controller, final boolean rewire) {
			_controller = controller;
			_rewire = rewire;
		}
		
		private void wireComponents(final PsdoCompFunctor functor, 
				final boolean ignoreNonNull) {
			final Class<?> ctrlClass = _controller.getClass();
			// wire to fields
			Reflections.forFields(ctrlClass, Wire.class, new FieldRunner<Wire>() {
				public void onField(Class<?> clazz, Field field, Wire anno) {
					if ((field.getModifiers() & Modifier.STATIC) != 0)
						throw new UiException("Cannot wire variable to " + 
								"static field: " + field.getName());
					
					if (_rewire && !anno.rewireOnActivate())
						return; // skipped, not rewired
					
					if (ignoreNonNull) {
						// if not null && not collection, skip
						Object value = Reflections.getFieldValue(_controller, field);
						if (value != null && (!(value instanceof Collection<?>) ||
								!((Collection<?>) value).isEmpty()))
							return;
					}
					
					String selector = anno.value();
					boolean optional = anno.optional();
					if (selector.length() > 0) {
						injectComponent(field, functor.iterable(selector), optional);
						return;
					}
					
					// no selector value, wire implicit object by naming convention
					Component value = 
						getComponentByName(functor, field.getName(), field.getType());
					if (value != null) {
						Reflections.setFieldValue(_controller, field, value);
						return;
					}
					if (optional) return;
					
					// no matched Object or Component
					String name = field.getName();
					if (name.contains("$")) throw new UiException(
							ctrlClass + " does not support " + 
							"syntax with '$'. Please use selector as alternative.");
					throw new UiException("Cannot wire component to field: " + name);
				}
			});
			// wire by methods
			Reflections.forMethods(ctrlClass, Wire.class, new MethodRunner<Wire>() {
				public void onMethod(Class<?> clazz, Method method, Wire anno) {
					// check method signature
					String name = method.getName();
					if ((method.getModifiers() & Modifier.STATIC) != 0) 
						throw new UiException(
								"Cannot wire component by static method: " + name);
					Class<?>[] paramTypes = method.getParameterTypes();
					if (paramTypes.length != 1) 
						throw new UiException("Setter method should have only" + 
								" one parameter: " + name);
					
					if (_rewire && !anno.rewireOnActivate())
						return; // skipped, not rewired
					
					String selector = anno.value();
					// check selector string: nonempty
					if (selector.length() == 0)
						throw new UiException("Selector is empty on method: " + 
								method.getName());
					
					injectComponent(method, functor.iterable(selector), 
							anno.optional());
				}
			});
		}
		
		private void wireVariables(final PsdoCompFunctor functor, 
				final List<VariableResolver> resolvers) {
			Class<?> ctrlClass = _controller.getClass();
			// wire to fields
			Reflections.forFields(ctrlClass, WireVariable.class, 
					new FieldRunner<WireVariable>(){
				public void onField(Class<?> clazz, Field field, WireVariable anno) {
					if ((field.getModifiers() & Modifier.STATIC) != 0)
						throw new UiException("Cannot wire variable to " + 
								"static field: " + field.getName());
					
					Type tp = field.getGenericType();
					if (_rewire && tp instanceof Class<?>) {
						Class<?> cls = (Class<?>) tp;
						if (!anno.rewireOnActivate() && 
								!Session.class.isAssignableFrom(cls) && 
								!WebApp.class.isAssignableFrom(cls))
							return; // skipped, not rewired
					}
					
					String vname = anno.value();
					String name = Strings.isEmpty(vname) ? field.getName() : vname;
					
					// no selector value, wire implicit object by naming convention
					Object value = 
						getObjectByName(functor, name, field.getType(), resolvers);
					if (value != null)
						Reflections.setFieldValue(_controller, field, value);
					else if (!anno.optional())
						// no matched Object or Component
						throw new UiException("Cannot wire variable of name " + 
								name + " to field: " + field.getName());
					
				}
			});
			// wire by methods
			Reflections.forMethods(ctrlClass, WireVariable.class, 
					new MethodRunner<WireVariable>(){
				public void onMethod(Class<?> clazz, Method method, WireVariable anno) {
					// check method signature
					String mname = method.getName();
					if ((method.getModifiers() & Modifier.STATIC) != 0) 
						throw new UiException("Cannot wire variable by static" + 
								" method: " + mname);
					Class<?>[] paramTypes = method.getParameterTypes();
					if (paramTypes.length != 1) 
						throw new UiException("Setter method should have only" + 
								" one parameter: " + mname);
					
					if (_rewire) {
						Class<?> cls = paramTypes[0];
						if (!anno.rewireOnActivate() && 
								!Session.class.isAssignableFrom(cls) && 
								!WebApp.class.isAssignableFrom(cls))
							return; // skipped, not rewired
					}
					
					String vname = anno.value();
					// check selector string: nonempty
					if (vname.length() == 0)
						throw new UiException("Variable name is empty on method: " + 
								method.getName());
					
					injectComponent(method, functor.iterable(vname), anno.optional());
				}
			});
		}
		
		private void injectComponent(Method method, Iterable<Component> iter, 
				boolean optional) {
			injectComponent(new MethodFunctor(method), iter, optional);
		}
		
		private void injectComponent(Field field, Iterable<Component> iter, 
				boolean optional) {
			injectComponent(new FieldFunctor(field), iter, optional);
		}
		
		@SuppressWarnings("unchecked")
		private void injectComponent(InjectionFunctor injector, 
				Iterable<Component> comps, boolean optional) {
			Class<?> type = injector.getType();
			boolean isField = injector instanceof FieldFunctor;
			// Array
			if (type.isArray()) {
				injector.inject(_controller, 
						generateArray(type.getComponentType(), comps));
				return;
			}
			// Collection
			if (Collection.class.isAssignableFrom(type)) {
				
				Collection collection = null;
				if (isField) {
					Field field = ((FieldFunctor) injector).getField();
					try {
						collection = (Collection) field.get(_controller);
					} catch (Exception e) {
						throw new IllegalStateException("Field " + field + 
							" not accessible or not declared by" + _controller);
					}
				}
				
				// try to give an instance if null 
				if(collection == null) {
					collection = getCollectionInstanceIfPossible(type);
					if (collection == null)
						throw new UiException("Cannot initiate collection for "+
								(isField? "field" : "method") + ": " + 
								injector.getName() + " on " + _controller);
					if (isField)
						injector.inject(_controller, collection);
				}
				
				// try add to collection
				collection.clear();
				for (Component c : comps)
					if (Reflections.isAppendableToCollection(
							injector.getGenericType(), c))
						collection.add(c);
				if (!isField) 
					injector.inject(_controller, collection);
				return;
			} 
			// set to field once or invoke method once
			for (Component c : comps) {
				if(!type.isInstance(c)) 
					continue;
				injector.inject(_controller, c);
				return;
			}
			if (!optional)
				// failed to inject, throw exception
				throw new UiException("Failed to inject to field " + 
						injector.getName() + " in controller " + _controller);
			injector.inject(_controller, null); // no match, inject null
		}
		
		private Component getComponentByName(PsdoCompFunctor functor, 
				String name, Class<?> type) {
			Component result = functor.getFellowIfAny(name);
			return isValidValue(result, type) ? result : null;
		}
		
		private Object getObjectByName(PsdoCompFunctor functor, String name, 
				Class<?> type, List<VariableResolver> resolvers) {
			
			Object result = functor.getXelVariable(name);
			if (isValidValue(result, type)) 
				return result;
			
			if (resolvers != null)
				for (VariableResolver resv : resolvers) {
					result = resv.resolveVariable(name);
					if (isValidValue(result, type))
						return result;
				}
			
			result = functor.getImplicit(name);
			return isValidValue(result, type)? result : null;
		}
		
		private interface InjectionFunctor {
			public void inject(Object obj, Object value);
			public String getName();
			public Class<?> getType();
			public Type getGenericType();
		}
		
		private class FieldFunctor implements InjectionFunctor {
			private final Field _field;
			private FieldFunctor(Field field){ _field = field; }
			public void inject(Object obj, Object value) {
				Reflections.setFieldValue(obj, _field, value);
			}
			public String getName() {
				return _field.getName();
			}
			public Class<?> getType() {
				return _field.getType();
			}
			public Type getGenericType() {
				return _field.getGenericType();
			}
			public Field getField(){
				return _field;
			}
		}
		
		private class MethodFunctor implements InjectionFunctor {
			private final Method _method;
			private MethodFunctor(Method method){ _method = method; }
			public void inject(Object obj, Object value) {
				Reflections.invokeMethod(_method, obj, value);
			}
			public String getName() {
				return _method.getName();
			}
			public Class<?> getType() {
				return _method.getParameterTypes()[0];
			}
			public Type getGenericType() {
				return _method.getGenericParameterTypes()[0];
			}
		}
		
	}
	
	private static boolean isValidValue(Object value, Class<?> clazz) {
		return value != null && clazz.isAssignableFrom(value.getClass());
	}
	
	@SuppressWarnings("unchecked")
	private static Collection getCollectionInstanceIfPossible(Class<?> clazz) {
		if(clazz.isAssignableFrom(ArrayList.class)) return new ArrayList();
		if(clazz.isAssignableFrom(HashSet.class)) return new HashSet();
		if(clazz.isAssignableFrom(TreeSet.class)) return new TreeSet();
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T[] generateArray(Class<T> clazz, Iterable<Component> comps) {
		// add to a temporary ArrayList then set to Array
		ArrayList<T> list = new ArrayList<T>();
		for (Component c : comps)
			if(clazz.isAssignableFrom(c.getClass())) 
				list.add((T) c);
		return list.toArray((T[]) Array.newInstance(clazz, 0));
	}
	
	private static class ComposerEventListener implements EventListener<Event> {
		
		private final Method _ctrlMethod;
		private final Object _ctrl;
		
		public ComposerEventListener(Method method, Object controller) {
			_ctrlMethod = method;
			_ctrl = controller;
		}
		
		public void onEvent(Event event) throws Exception {
			if (_ctrlMethod.getParameterTypes().length == 0)
				_ctrlMethod.invoke(_ctrl);
			else
				_ctrlMethod.invoke(_ctrl, event);
		}
	}
	
	
	
	// helper: functor //
	private interface PsdoCompFunctor {
		public Iterable<Component> iterable(String selector);
		public Object getImplicit(String name);
		public Object getAttribute(String name);
		public Object getXelVariable(String name);
		public Component getFellowIfAny(String name);
	}
	
	private static class PageFunctor implements PsdoCompFunctor {
		private final Page _page;
		private PageFunctor(Page page){ _page = page; }
		public Iterable<Component> iterable(String selector) {
			return Selectors.iterable(_page, selector);
		}
		public Object getImplicit(String name) {
			return Components.getImplicit(_page, name);
		}
		public Object getXelVariable(String name) {
			return _page.getXelVariable(null, null, name, true);
		}
		public Object getAttribute(String name) {
			return _page.getAttribute(name, true);
		}
		public Component getFellowIfAny(String name) {
			return _page.getFellowIfAny(name);
		}
	}
	
	private static class ComponentFunctor implements PsdoCompFunctor {
		private final Component _comp;
		private ComponentFunctor(Component comp){ _comp = comp; }
		public Iterable<Component> iterable(String selector) {
			return Selectors.iterable(_comp, selector);
		}
		public Object getImplicit(String name) {
			return Components.getImplicit(_comp, name);
		}
		public Object getXelVariable(String name) {
			return getPage().getXelVariable(null, null, name, true);
		}
		public Object getAttribute(String name) {
			return _comp.getAttribute(name, true);
		}
		public Component getFellowIfAny(String name) {
			return _comp.getFellowIfAny(name);
		}
		private Page getPage() {
			return Components.getCurrentPage(_comp);
		}
	}
	
	private static <T> T getIthItem(Iterator<T> iter, int index){
		// shift (index - 1) times
		for(int i = 1; i < index; i++) {
			if(!iter.hasNext())
				return null;
			iter.next();
		}
		return iter.hasNext() ? iter.next() : null;
	}
	
}
