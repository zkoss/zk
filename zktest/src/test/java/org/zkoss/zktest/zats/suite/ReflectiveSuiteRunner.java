/* ReflectiveSuiteRunner.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 08 12:50:14 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.suite;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

/**
 * A customized JUnit Runner that uses reflection to get the test execution list at runtime.
 * The annotated class must have a static {@code suite()} method that returns {@code Class<?>[]}.
 *
 * <pre><code>
 * {@literal @}RunWith(ReflectiveSuiteRunner.class)
 * public class MySuite {
 *     public static Class<?>[] suite() {
 *
 *     }
 * }
 * </code></pre>
 *
 * @author rudyhuang
 */
public class ReflectiveSuiteRunner extends ParentRunner<Runner> {
	private final List<Runner> runners;

	public ReflectiveSuiteRunner(Class<?> setupClass) throws InitializationError {
		super(setupClass);
		try {
			runners = buildRunners(setupClass);
		} catch (Throwable e) {
			throw new InitializationError(e);
		}
	}

	private List<Runner> buildRunners(Class<?> setupClass)
			throws ReflectiveOperationException, InitializationError {
		final Method suiteMethod = setupClass.getDeclaredMethod("suite");
		final RunnerBuilder builder = new AllDefaultPossibilitiesBuilder();
		return builder.runners(setupClass, (Class<?>[]) suiteMethod.invoke(setupClass));
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	@Override
	protected Description describeChild(Runner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(Runner child, RunNotifier runNotifier) {
		child.run(runNotifier);
	}
}
