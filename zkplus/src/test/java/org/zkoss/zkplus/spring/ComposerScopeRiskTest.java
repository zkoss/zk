/* ComposerScopeRiskTest.java

	Purpose:
		Document the mechanistic root cause behind the Singleton Composer risk.

	Description:
		Two root-cause mechanisms explain why a Spring Singleton Composer is unsafe:

		1. Utils.newComposer() passthrough: when apply="${bean}" resolves to an existing
		   Composer instance (i.e. a Spring bean), the instance is returned as-is without
		   creating a new one.  A Singleton is therefore reused across every composition.

		2. ConventionWires.wireController() stores the Composer as a component attribute.
		   The Composer lives exactly as long as its host component (Component-scoped).

		These are unit-level tests of the mechanism.  End-to-end lifecycle verification
		(FQCN vs Prototype vs Singleton across page load, recreation, include reload, and
		detach+reattach) is covered by Z100_Spring_Composer_Scope_ZatsTest.

*/
package org.zkoss.zkplus.spring;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.impl.Utils;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ConventionWires;

/**
 * Unit tests documenting the two mechanistic root causes of the Spring Singleton Composer risk.
 *
 * <ol>
 *   <li>{@link Utils#newComposer} passes through an existing Composer instance unchanged.</li>
 *   <li>{@link ConventionWires#wireController} stores the Composer as a component attribute,
 *       proving the Composer is Component-scoped.</li>
 * </ol>
 *
 * <p>End-to-end lifecycle verification is in {@link Z100_Spring_Composer_Scope_ZatsTest}.
 */
public class ComposerScopeRiskTest {

	public static class StatefulComposer implements Composer<Component> {
		public String currentUser = null;

		@Override
		public void doAfterCompose(Component comp) throws Exception {
		}
	}

	// =========================================================================
	// Group 1 — Utils.newComposer instantiation behaviour
	// =========================================================================

	@Nested
	@DisplayName("Utils.newComposer instantiation behaviour")
	class InstantiationBehaviour {

		@Test
		@DisplayName("FQCN String → new instance per call via reflection")
		void fqcnCreatesNewInstancePerCall() throws Exception {
			String fqcn = StatefulComposer.class.getName();

			Composer<?> c1 = Utils.newComposer(null, fqcn);
			Composer<?> c2 = Utils.newComposer(null, fqcn);

			assertNotNull(c1);
			assertNotSame(c1, c2, "FQCN apply must create a distinct Composer instance on every composition.");
		}

		@Test
		@DisplayName("Class object → new instance per call via reflection")
		void classObjectCreatesNewInstancePerCall() throws Exception {
			Composer<?> c1 = Utils.newComposer(null, StatefulComposer.class);
			Composer<?> c2 = Utils.newComposer(null, StatefulComposer.class);

			assertNotNull(c1);
			assertNotSame(c1, c2, "Class-object apply must create a distinct Composer instance on every composition.");
		}

		@Test
		@DisplayName("Existing Composer instance → returned as-is (root of Singleton risk)")
		void existingComposerInstanceIsReturnedDirectly() throws Exception {
			// This is what happens when apply="${myComposer}" and the EL resolves to
			// a Composer already in memory (e.g. a Spring Singleton).
			StatefulComposer alreadyResolved = new StatefulComposer();

			Composer<?> result1 = Utils.newComposer(null, alreadyResolved);
			Composer<?> result2 = Utils.newComposer(null, alreadyResolved);

			assertSame(alreadyResolved, result1,
					"Utils.newComposer must pass through an existing Composer instance unchanged.");
			assertSame(alreadyResolved, result2,
					"RISK ROOT: Utils.newComposer returns the same object every time — "
					+ "a Spring Singleton therefore leaks across all users and desktops.");
		}
	}

	// =========================================================================
	// Group 2 — Composer is Component-scoped
	// =========================================================================

	@Nested
	@DisplayName("Composer scope: Component-scoped (not Desktop-scoped)")
	class ComposerScope {

		@Test
		@DisplayName("ConventionWires.wireController stores Composer as component attribute")
		void composerIsStoredAsComponentAttribute() {
			Component comp = mock(Component.class);
			when(comp.getId()).thenReturn("win");

			StatefulComposer composer = new StatefulComposer();
			ConventionWires.wireController(comp, composer);

			// Composer is stored under these keys on the Component.
			// When the Component is garbage-collected, so is the Composer → Component-scoped.
			verify(comp).setAttribute("_$composer$_", composer);
			verify(comp).setAttribute("$composer", composer);
			verify(comp).setAttribute("win$composer", composer);
		}
	}
}
