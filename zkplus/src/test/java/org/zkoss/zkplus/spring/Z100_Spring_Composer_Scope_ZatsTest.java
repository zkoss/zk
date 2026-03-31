package org.zkoss.zkplus.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.zkoss.zats.mimic.DefaultZatsEnvironment;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 * ZATS Mimic tests verifying that FQCN and Spring Prototype Composer scopes
 * are behaviorally equivalent across ZK lifecycle scenarios.
 *
 * <p>Tests cover four lifecycle groups:
 * <ol>
 *   <li>Basic: two components on the same page</li>
 *   <li>Recreate: container cleared and Executions.createComponents() called</li>
 *   <li>Include re-src: include.setSrc() triggers new composition</li>
 *   <li>Detach+Reattach: comp.detach() + parent.appendChild() does NOT re-compose</li>
 * </ol>
 */
public class Z100_Spring_Composer_Scope_ZatsTest {

	private static DefaultZatsEnvironment env;

	@BeforeAll
	static void init() {
		env = new DefaultZatsEnvironment("src/test/resources/webapp/WEB-INF", "/zkplus-test");
		env.init("src/test/resources/webapp");
	}

	@AfterAll
	static void end() {
		if (env != null) {
			env.destroy();
		}
	}

	@AfterEach
	void after() {
		if (env != null) {
			env.cleanup();
		}
	}

	// -------------------------------------------------------------------------
	// Group 1 — Basic: two components on same page
	// -------------------------------------------------------------------------

	@Test
	void fqcnApplyCreatesNewInstancePerComponent() {
		DesktopAgent desktop = env.newClient().connect("/test/Z100-Spring-Composer-FQCN.zul");
		String hash1 = desktop.query("#hash1").as(Label.class).getValue();
		String hash2 = desktop.query("#hash2").as(Label.class).getValue();
		assertFalse(hash1.isEmpty(), "hash1 should be populated by Composer");
		assertFalse(hash2.isEmpty(), "hash2 should be populated by Composer");
		assertNotEquals(hash1, hash2, "FQCN apply must create a distinct Composer per component");
	}

	@Test
	void springPrototypeCreatesNewInstancePerComponent() {
		DesktopAgent desktop = env.newClient().connect("/test/Z100-Spring-Composer-Prototype.zul");
		String hash1 = desktop.query("#hash1").as(Label.class).getValue();
		String hash2 = desktop.query("#hash2").as(Label.class).getValue();
		assertFalse(hash1.isEmpty(), "hash1 should be populated by Composer");
		assertFalse(hash2.isEmpty(), "hash2 should be populated by Composer");
		assertNotEquals(hash1, hash2, "Spring Prototype must create a distinct Composer per component");
	}

	@Test
	void springSingletonSharesOneInstance() {
		DesktopAgent desktop = env.newClient().connect("/test/Z100-Spring-Composer-Singleton.zul");
		String hash1 = desktop.query("#hash1").as(Label.class).getValue();
		String hash2 = desktop.query("#hash2").as(Label.class).getValue();
		assertFalse(hash1.isEmpty(), "hash1 should be populated by Composer");
		assertFalse(hash2.isEmpty(), "hash2 should be populated by Composer");
		assertEquals(hash1, hash2, "[CONFIRMED RISK] Spring Singleton returns the same Composer for every component");
	}

	// -------------------------------------------------------------------------
	// Group 2 — Recreate: container cleared + Executions.createComponents()
	// -------------------------------------------------------------------------

	@Test
	void fqcnCreatesNewInstanceAfterRecreation() {
		DesktopAgent desktop = env.newClient().connect("/test/Z100-Recreate-FQCN.zul");
		String hash1 = desktop.query("#hashLabel").as(Label.class).getValue();

		desktop.query("#textbox").type("hello");
		assertEquals("hello", desktop.query("#textbox").as(Textbox.class).getValue(),
				"Textbox should hold typed value before recreation");

		desktop.query("#recreateBtn").click();

		String hash2 = desktop.query("#hashLabel").as(Label.class).getValue();
		assertNotEquals(hash1, hash2, "FQCN must create a new Composer instance after component recreation");
		assertEquals("", desktop.query("#textbox").as(Textbox.class).getValue(),
				"Textbox must be empty after recreation (fresh component state)");
	}

	@Test
	void springPrototypeCreatesNewInstanceAfterRecreation() {
		DesktopAgent desktop = env.newClient().connect("/test/Z100-Recreate-Prototype.zul");
		String hash1 = desktop.query("#hashLabel").as(Label.class).getValue();

		desktop.query("#textbox").type("hello");
		assertEquals("hello", desktop.query("#textbox").as(Textbox.class).getValue(),
				"Textbox should hold typed value before recreation");

		desktop.query("#recreateBtn").click();

		String hash2 = desktop.query("#hashLabel").as(Label.class).getValue();
		assertNotEquals(hash1, hash2, "Spring Prototype must create a new Composer instance after component recreation");
		assertEquals("", desktop.query("#textbox").as(Textbox.class).getValue(),
				"Textbox must be empty after recreation (fresh component state)");
	}

	// -------------------------------------------------------------------------
	// Group 3 — Include re-src: include.setSrc() triggers new composition
	// -------------------------------------------------------------------------

	@Test
	void fqcnCreatesNewInstanceOnIncludeReload() {
		DesktopAgent desktop = env.newClient().connect("/test/Z100-Include-FQCN.zul");
		String hash1 = desktop.query("#inc #hashLabel").as(Label.class).getValue();
		assertFalse(hash1.isEmpty(), "hash1 should be populated before reload");

		desktop.query("#reloadBtn").click();

		String hash2 = desktop.query("#inc #hashLabel").as(Label.class).getValue();
		assertNotEquals(hash1, hash2, "FQCN must create a new Composer instance on include reload");
	}

	@Test
	void springPrototypeCreatesNewInstanceOnIncludeReload() {
		DesktopAgent desktop = env.newClient().connect("/test/Z100-Include-Prototype.zul");
		String hash1 = desktop.query("#inc #hashLabel").as(Label.class).getValue();
		assertFalse(hash1.isEmpty(), "hash1 should be populated before reload");

		desktop.query("#reloadBtn").click();

		String hash2 = desktop.query("#inc #hashLabel").as(Label.class).getValue();
		assertNotEquals(hash1, hash2, "Spring Prototype must create a new Composer instance on include reload");
	}

	@Test
	void springSingletonKeepsSameInstanceOnIncludeReload() {
		DesktopAgent desktop = env.newClient().connect("/test/Z100-Include-Singleton.zul");
		String hash1 = desktop.query("#inc #hashLabel").as(Label.class).getValue();
		assertFalse(hash1.isEmpty(), "hash1 should be populated before reload");

		desktop.query("#reloadBtn").click();

		String hash2 = desktop.query("#inc #hashLabel").as(Label.class).getValue();
		assertEquals(hash1, hash2, "[CONFIRMED RISK] Spring Singleton returns the same Composer even after include reload");
	}

	// -------------------------------------------------------------------------
	// Group 4 — Detach + Reattach: comp.detach() + parent.appendChild(comp)
	//           does NOT trigger resolveComposer() — Composer is component-scoped
	// -------------------------------------------------------------------------

	@Test
	void fqcnComposerUnchangedOnDetachReattach() {
		DesktopAgent desktop = env.newClient().connect("/test/Z100-DetachReattach-FQCN.zul");
		String hash1 = desktop.query("#hashLabel").as(Label.class).getValue();
		assertFalse(hash1.isEmpty(), "hash1 should be populated");

		desktop.query("#detachReattachBtn").click();

		String hash2 = desktop.query("#hashLabel").as(Label.class).getValue();
		assertEquals(hash1, hash2, "FQCN Composer must be unchanged after detach+reattach — Composer is component-scoped");
	}

	@Test
	void springPrototypeComposerUnchangedOnDetachReattach() {
		DesktopAgent desktop = env.newClient().connect("/test/Z100-DetachReattach-Prototype.zul");
		String hash1 = desktop.query("#hashLabel").as(Label.class).getValue();
		assertFalse(hash1.isEmpty(), "hash1 should be populated");

		desktop.query("#detachReattachBtn").click();

		String hash2 = desktop.query("#hashLabel").as(Label.class).getValue();
		assertEquals(hash1, hash2, "Spring Prototype Composer must be unchanged after detach+reattach — Composer is component-scoped");
	}
}
