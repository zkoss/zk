/* F110_ZK_6097_XsdTest.java

        Purpose:
                
        Description:
                
        History:
                Sun Aug 31 12:05:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class F110_ZK_6097_XsdTest {

	private static final String NS = "http://www.zkoss.org/2005/zul";
	private static final Pattern COMPONENT_NAME =
			Pattern.compile("<component-name>\\s*([^<\\s]+)\\s*</component-name>");
	private static Schema _schema;
	private static String _xsdText;
	private static Set<String> _langComponentNames;
	private static Set<String> _anyGroup;
	private static Set<String> _anyGroupSingle;

	/** Reachable only from the parent content models that may hold them, never from a generic group. */
	private static final Set<String> PARENT_ONLY = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			"absolutechildren", "anchorchildren", "area", "auxhead", "auxheader", "bandpopup",
			"breadcrumbitem", "caption", "carouselitem", "center", "column", "columns", "comboitem",
			"east", "foot", "footer", "listcell", "listfoot", "listfooter", "listhead", "listheader",
			"listitem", "menu", "menuitem", "menuseparator", "north", "panelchildren",
			"row", "rows", "south", "splitter", "tab", "tabpanel", "tabpanels", "tabs", "track",
			"treecell", "treechildren", "treecol", "treecols", "treefoot", "treefooter", "treeitem",
			"treerow", "west")));

	/** In lang.xml but carrying no xs:element declaration: a doc sample, the macro base, the region base. */
	private static final Set<String> UNDECLARED = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			"foo", "macro", "layoutregion")));

	/**
	 * Components reachable from neither group. Empty — ZK-6081 closed the last two.
	 * Adding a name here must be a deliberate, documented gap, not a way to quiet
	 * {@link #everyLangXmlComponentIsReachableFromBothGroups}.
	 */
	private static final Set<String> KNOWN_UNREACHABLE = Collections.emptySet();

	/**
	 * The duplicate-attribute errors zul.xsd already carries on master. Anything else must fail:
	 * Xerces recovers by dropping the offending particle, so a blanket ignore would leave every
	 * assertion below validating against a quietly weakened schema.
	 */
	private static final Set<String> TOLERATED_SCHEMA_ERRORS = Collections.unmodifiableSet(
			new HashSet<>(Arrays.asList("ag-props-correct.2", "ct-props-correct.4")));

	private static class LenientSchemaErrorHandler implements ErrorHandler {
		@Override
		public void warning(SAXParseException e) { /* ignore */ }
		@Override
		public void error(SAXParseException e) throws SAXException {
			final String msg = e.getMessage();
			if (msg == null || TOLERATED_SCHEMA_ERRORS.stream().noneMatch(msg::contains))
				throw e;
		}
		@Override
		public void fatalError(SAXParseException e) throws SAXException { throw e; }
	}

	/** Throws on the first error, so a failure names the offending element. */
	private static class StrictValidationErrorHandler implements ErrorHandler {
		final List<SAXParseException> errors = new ArrayList<>();
		@Override
		public void warning(SAXParseException e) { /* ignore */ }
		@Override
		public void error(SAXParseException e) throws SAXException { throw e; }
		@Override
		public void fatalError(SAXParseException e) throws SAXException { throw e; }
	}

	@BeforeAll
	static void initSchema() throws Exception {
		_xsdText = read(F110_ZK_6097_XsdTest.class.getResourceAsStream("/metainfo/xml/zul.xsd"));
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// Tolerates only the duplicate-attribute errors zul.xsd already carries on
		// master; every other schema error still fails the run — see the handler.
		factory.setErrorHandler(new LenientSchemaErrorHandler());
		_schema = factory.newSchema(new StreamSource(new StringReader(_xsdText)));

		Set<String> names = new LinkedHashSet<>();
		Matcher m = COMPONENT_NAME.matcher(readCeLangXml());
		while (m.find())
			names.add(m.group(1));
		_langComponentNames = Collections.unmodifiableSet(names);

		_anyGroup = groupMembers("anyGroup");
		_anyGroupSingle = groupMembers("anyGroupSingle");
	}

	private static String read(InputStream is) throws Exception {
		try (InputStream in = is) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	private static String readCeLangXml() throws Exception {
		// zhtml and the EE zml/stateless modules ship metainfo/zk/lang.xml too: pick by language-name.
		Enumeration<URL> urls = F110_ZK_6097_XsdTest.class.getClassLoader().getResources("metainfo/zk/lang.xml");
		while (urls.hasMoreElements()) {
			String text = read(urls.nextElement().openStream());
			if (text.contains("<language-name>xul/html</language-name>"))
				return text;
		}
		throw new IllegalStateException("the CE xul/html lang.xml is not on the test classpath");
	}

	/** The element refs listed INSIDE one named xs:group — a ref anywhere else in the file does not count. */
	private static Set<String> groupMembers(String groupName) {
		Matcher group = Pattern.compile(
				"<xs:group\\s+name=\"" + Pattern.quote(groupName) + "\"\\s*>(.*?)</xs:group>",
				Pattern.DOTALL).matcher(_xsdText);
		if (!group.find())
			throw new IllegalStateException("zul.xsd declares no <xs:group name=\"" + groupName + "\">");
		Set<String> refs = new LinkedHashSet<>();
		Matcher m = Pattern.compile("<xs:element\\s+ref=\"([^\"]+)\"").matcher(group.group(1));
		while (m.find())
			refs.add(m.group(1));
		return Collections.unmodifiableSet(refs);
	}

	private static boolean isDeclared(String name) {
		return Pattern.compile("<xs:element\\s+name=\"" + Pattern.quote(name) + "\"").matcher(_xsdText).find();
	}

	private static void validate(String zulContent) throws Exception {
		Validator validator = _schema.newValidator();
		// Use strict handler during document validation
		validator.setErrorHandler(new StrictValidationErrorHandler());
		validator.validate(new StreamSource(new StringReader(zulContent)));
	}

	private static String zul(String body) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<zk xmlns=\"" + NS + "\">\n"
				+ body + "\n"
				+ "</zk>\n";
	}

	// The seven new components: reachable, childable where they render children, EL-tolerant attributes.

	private static final String[] ZK6097_COMPONENTS = {
			"avatar", "avatargroup", "badge", "breadcrumb", "carousel", "chip", "confirmpopup" };

	@Nested
	class ZK6097GroupRefTests {
		@Test
		public void anyGroupAcceptsEveryNewComponent() {
			for (String name : ZK6097_COMPONENTS)
				assertDoesNotThrow(() -> validate(zul("<div>\n  <" + name + "/>\n</div>")),
						"div (anyGroup) should accept <" + name + ">");
		}

		@Test
		public void anyGroupSingleAcceptsEveryNewComponent() {
			for (String name : ZK6097_COMPONENTS)
				assertDoesNotThrow(() -> validate(zul(
						"<borderlayout>\n  <center>\n    <" + name + "/>\n  </center>\n</borderlayout>")),
						"center (anyGroupSingle) should accept <" + name + ">");
		}

		@Test
		public void layoutRegionAcceptsBreadcrumb() {
			assertDoesNotThrow(() -> validate(zul(
					"<borderlayout>\n"
					+ "  <north>\n"
					+ "    <breadcrumb>\n"
					+ "      <breadcrumbitem label=\"Home\" href=\"/\"/>\n"
					+ "      <breadcrumbitem label=\"Here\"/>\n"
					+ "    </breadcrumb>\n"
					+ "  </north>\n"
					+ "</borderlayout>"
			)), "north (layoutRegionType) should accept a breadcrumb");
		}

		@Test
		public void parentOnlyItemsStayOutOfAnyGroup() {
			assertThrows(SAXException.class, () -> validate(zul("<div>\n  <carouselitem/>\n</div>")),
					"carouselitem is parent-only and must not be reachable from div");
			assertThrows(SAXException.class, () -> validate(zul("<div>\n  <breadcrumbitem/>\n</div>")),
					"breadcrumbitem is parent-only and must not be reachable from div");
		}

		@Test
		public void parentOnlyItemsAreStillReachableFromTemplateAndZk() {
			// Parent-only keeps them out of anyGroup, so templateType and zkType are
			// the only two places left that can name them — and templating a
			// carousel's slides is the documented way to bind one to a model.
			for (String name : new String[] { "breadcrumbitem", "carouselitem" }) {
				assertDoesNotThrow(() -> validate(zul(
						"<template name=\"model\">\n  <" + name + "/>\n</template>")),
						"template should accept <" + name + ">");
				assertDoesNotThrow(() -> validate(zul("<" + name + "/>")),
						"zk (zkType) should accept a bare <" + name + ">");
			}
		}
	}

	@Nested
	class ZK6097ContentModelTests {
		@Test
		public void leafComponentsAcceptBaseGroup() {
			assertDoesNotThrow(() -> validate(zul(
					"<avatar>\n  <custom-attributes tone=\"muted\"/>\n</avatar>")),
					"avatar should accept baseGroup meta-elements");
			assertDoesNotThrow(() -> validate(zul(
					"<chip label=\"Tag\">\n  <custom-attributes tone=\"muted\"/>\n</chip>")),
					"chip should accept baseGroup meta-elements");
			assertDoesNotThrow(() -> validate(zul(
					"<confirmpopup>\n  <attribute name=\"onOK\">doIt();</attribute>\n</confirmpopup>")),
					"confirmpopup should accept baseGroup meta-elements");
			assertDoesNotThrow(() -> validate(zul(
					"<breadcrumb>\n"
					+ "  <breadcrumbitem label=\"Home\">\n"
					+ "    <custom-attributes tone=\"muted\"/>\n"
					+ "  </breadcrumbitem>\n"
					+ "</breadcrumb>")),
					"breadcrumbitem should accept baseGroup meta-elements");
		}

		@Test
		public void carouselAcceptsBaseGroupBesideItsItems() {
			assertDoesNotThrow(() -> validate(zul(
					"<carousel>\n"
					+ "  <attribute name=\"onSelect\">log();</attribute>\n"
					+ "  <custom-attributes tone=\"muted\"/>\n"
					+ "  <carouselitem/>\n"
					+ "  <carouselitem/>\n"
					+ "</carousel>"
			)), "carousel should accept attribute/custom-attributes beside its carouselitems");
		}

		@Test
		public void breadcrumbAcceptsBaseGroupBesideItsItems() {
			assertDoesNotThrow(() -> validate(zul(
					"<breadcrumb>\n"
					+ "  <custom-attributes tone=\"muted\"/>\n"
					+ "  <breadcrumbitem label=\"Home\"/>\n"
					+ "  <breadcrumbitem label=\"Here\"/>\n"
					+ "</breadcrumb>"
			)), "breadcrumb should accept custom-attributes beside its breadcrumbitems");
		}

		@Test
		public void avatargroupAcceptsBaseGroupBesideItsItems() {
			assertDoesNotThrow(() -> validate(zul(
					"<avatargroup maxItems=\"3\">\n"
					+ "  <variables who=\"team\"/>\n"
					+ "  <avatar label=\"A\"/>\n"
					+ "  <avatar label=\"B\"/>\n"
					+ "</avatargroup>"
			)), "avatargroup should accept variables beside its avatars");
		}

		@Test
		public void carouselitemHoldsRealComponents() {
			assertDoesNotThrow(() -> validate(zul(
					"<carousel width=\"320px\" height=\"160px\">\n"
					+ "  <carouselitem>\n"
					+ "    <div sclass=\"carousel-slide\">Slide 1</div>\n"
					+ "  </carouselitem>\n"
					+ "  <carouselitem>\n"
					+ "    <label value=\"Slide 2\"/>\n"
					+ "  </carouselitem>\n"
					+ "</carousel>"
			)), "carouselitem is childable and the fixtures put a div in a slide");
		}

		@Test
		public void badgeWrapsRealComponents() {
			assertDoesNotThrow(() -> validate(zul(
					"<badge count=\"3\" placement=\"top_right\">\n"
					+ "  <button label=\"TR\"/>\n"
					+ "</badge>"
			)), "badge in wrap mode renders the component it decorates as its child");
		}
	}

	@Nested
	class ZK6097EnumAttrTests {
		@Test
		public void enumeratedAttributesAcceptEl() {
			assertDoesNotThrow(() -> validate(zul(
					"<confirmpopup severity=\"${vm.sev}\" placement=\"${vm.place}\""
					+ " defaultFocus=\"${vm.focus}\"/>")),
					"confirmpopup severity/placement/defaultFocus should accept EL");
			assertDoesNotThrow(() -> validate(zul("<carousel effect=\"${vm.effect}\"/>")),
					"carousel effect should accept EL");
			assertDoesNotThrow(() -> validate(zul(
					"<chip severity=\"${vm.sev}\" size=\"${vm.size}\"/>")),
					"chip severity/size should accept EL");
			assertDoesNotThrow(() -> validate(zul(
					"<badge severity=\"${vm.sev}\" placement=\"${vm.place}\"/>")),
					"badge severity/placement should accept EL");
			assertDoesNotThrow(() -> validate(zul(
					"<avatar shape=\"${vm.shape}\" size=\"${vm.size}\"/>")),
					"avatar shape/size should accept EL");
			assertDoesNotThrow(() -> validate(zul(
					"<avatargroup size=\"${vm.size}\" shape=\"${vm.shape}\"/>")),
					"avatargroup size/shape should accept EL");
		}

		@Test
		public void enumeratedAttributesAcceptBindingAnnotations() {
			assertDoesNotThrow(() -> validate(zul(
					"<chip severity=\"@load(vm.sev)\" size=\"@load(vm.size)\"/>")),
					"chip severity/size should accept @load bindings");
			assertDoesNotThrow(() -> validate(zul(
					"<avatargroup shape=\"@load(vm.shape)\"/>")),
					"avatargroup shape should accept @load bindings");
		}

		@Test
		public void enumeratedAttributesStillAcceptLiteralTokens() {
			assertDoesNotThrow(() -> validate(zul(
					"<confirmpopup severity=\"warning\" placement=\"bottom\" defaultFocus=\"cancel\"/>")),
					"confirmpopup literal tokens should still validate");
			assertDoesNotThrow(() -> validate(zul(
					"<carousel effect=\"fade\"/>\n"
					+ "<chip severity=\"error\" size=\"medium\"/>\n"
					+ "<badge severity=\"info\" placement=\"bottom_left\"/>\n"
					+ "<avatar shape=\"square\" size=\"large\"/>\n"
					+ "<avatargroup size=\"small\" shape=\"circle\"/>")),
					"literal tokens should still validate across the wave");
		}

		@Test
		public void unionWidensEnumeratedAttributesToAnyToken() {
			// Deliberate loss: the annotationType member that unblocks EL also lets nonsense
			// tokens through, so the Java setter is the only real gate on these attributes.
			assertDoesNotThrow(() -> validate(zul("<avatar shape=\"triangle\"/>")),
					"union types trade token checking for EL support");
			assertDoesNotThrow(() -> validate(zul("<chip size=\"gigantic\"/>")),
					"union types trade token checking for EL support");
		}
	}

	// Parity guard over the CE xul/html lang.xml only — EE components in anyGroup are out of its reach.

	@Nested
	class LangXsdParityTests {

		@Test
		public void ceLangXmlWasActuallyLoaded() {
			assertTrue(_langComponentNames.size() > 100,
					"expected the CE xul/html lang.xml, got " + _langComponentNames.size() + " names");
			assertTrue(_langComponentNames.containsAll(Arrays.asList("listbox", "window", "carousel")),
					"loaded lang.xml is missing well-known CE components");
		}

		@Test
		public void everyLangXmlComponentIsReachableFromBothGroups() {
			List<String> gaps = new ArrayList<>();
			for (String name : _langComponentNames) {
				if (PARENT_ONLY.contains(name) || UNDECLARED.contains(name)
						|| KNOWN_UNREACHABLE.contains(name))
					continue;
				boolean inAny = _anyGroup.contains(name);
				boolean inSingle = _anyGroupSingle.contains(name);
				if (!inAny || !inSingle)
					gaps.add(name + " missing from " + (inAny ? "anyGroupSingle"
							: inSingle ? "anyGroup" : "anyGroup and anyGroupSingle"));
			}
			assertTrue(gaps.isEmpty(),
					"declared in lang.xml but not a member of both zul.xsd element groups: " + gaps);
		}

		@Test
		public void exemptionsHaveNotGoneStale() {
			List<String> stale = new ArrayList<>();
			for (String name : PARENT_ONLY)
				if (_anyGroup.contains(name) && _anyGroupSingle.contains(name))
					stale.add(name + " is in both groups now");
			for (String name : UNDECLARED)
				if (isDeclared(name))
					stale.add(name + " has an xs:element declaration now");
			for (String name : KNOWN_UNREACHABLE)
				if (_anyGroup.contains(name) && _anyGroupSingle.contains(name))
					stale.add(name + " is reachable now, the gap is closed");
			List<String> exempt = new ArrayList<>(PARENT_ONLY);
			exempt.addAll(UNDECLARED);
			exempt.addAll(KNOWN_UNREACHABLE);
			for (String name : exempt)
				if (!_langComponentNames.contains(name))
					stale.add(name + " is not a CE lang.xml component any more");
			assertTrue(stale.isEmpty(), "drop these exemptions from the guard: " + stale);
		}
	}
}
