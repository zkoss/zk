/* ProxyProcessor.java

	Purpose:
		
	Description:
		
	History:
		2:07 PM 2022/1/11, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.processor;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.immutables.metainf.Metainf;

/**
 * Proxy processor to overcome eclipse class loading problems which renders our workarounds
 * for Eclipse bugs and quirks useless. When the condition is not detected we try to use direct
 * delegation to {@code org.zkoss.zephyr.processor.Processor}.
 */
@Metainf.Service
public final class ProxyProcessor implements Processor {
	private static final String DELEGATE_CLASS = "org.zkoss.zephyr.processor.Processor";
	private static final String ECLIPSE_PACKAGE_PREFIX = "org.eclipse.";
	private static final String OSGI_SYSTEM_PROPERTY = "osgi.arch";

	// initialization not quite atomic but that's ok
	private static volatile ClassLoader cachedProxyClassLoader;

	private final Processor delegate = requiresClassLoaderDelegate()
			? createClassLoaderDelegate()
			: createDefaultDelegate();

	@Override
	public Set<String> getSupportedOptions() {
		return delegate.getSupportedOptions();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return delegate.getSupportedAnnotationTypes();
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return delegate.getSupportedSourceVersion();
	}

	@Override
	public void init(ProcessingEnvironment processing) {
		delegate.init(processing);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment round) {
		return delegate.process(annotations, round);
	}

	@Override
	public Iterable<? extends Completion> getCompletions(
			Element element,
			AnnotationMirror annotation,
			ExecutableElement member,
			String userText) {
		return delegate.getCompletions(element, annotation, member, userText);
	}

	private static Processor createDefaultDelegate() {
		return new org.zkoss.zephyr.processor.Processor();
	}

	private static Processor createClassLoaderDelegate() {
		try {
			if (cachedProxyClassLoader == null) {
				cachedProxyClassLoader = new ProxyClassLoader(
						((URLClassLoader) ProxyProcessor.class.getClassLoader()).getURLs(),
						Thread.currentThread().getContextClassLoader());
			}
			return (Processor) cachedProxyClassLoader.loadClass(DELEGATE_CLASS).newInstance();
		} catch (RuntimeException | Error ex) {
			throw ex;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static boolean requiresClassLoaderDelegate() {
		if (System.getProperty(OSGI_SYSTEM_PROPERTY) != null) {
			for (StackTraceElement e : new Exception().getStackTrace()) {
				if (e.getClassName().startsWith(ECLIPSE_PACKAGE_PREFIX)) {
					ClassLoader staticClassLoader = ProxyProcessor.class.getClassLoader();
					ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

					return staticClassLoader instanceof URLClassLoader
							&& contextClassLoader != staticClassLoader;
				}
			}
		}
		return false;
	}

	private static class ProxyClassLoader extends URLClassLoader {
		private final ClassLoader contextLoader;

		ProxyClassLoader(URL[] urls, ClassLoader contextLoader) {
			super(urls, contextLoader);
			this.contextLoader = contextLoader;
		}

		@Override
		public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
			synchronized (getClassLoadingLock(name)) {
				Class<?> c;
				if (name.startsWith(ECLIPSE_PACKAGE_PREFIX)) {
					c = contextLoader.loadClass(name);
				} else {
					c = findLoadedClass(name);
					if (c == null) {
						try {
							c = findClass(name);
						} catch (ClassNotFoundException ex) {
							return super.loadClass(name, resolve);
						}
					}
				}
				if (resolve) {
					resolveClass(c);
				}
				return c;
			}
		}
	}
}
