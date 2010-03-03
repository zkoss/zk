package org.zkoss.zktest.test2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.Test;
import org.zkoss.zktest.Widget;
import org.zkoss.zktest.ZKClientTestCase;

import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.parser.XSOMParser;
import com.thoughtworks.selenium.Selenium;

public class ComponentPropertiesTest extends ZKClientTestCase {

	public ComponentPropertiesTest() {
		target = "service.zul";
	}

	void log(Object s) {
		System.out.println(s);
	}

	void fire(String engine, String zscript) {
		Widget $u = widget(engine);
		$u.$n().set("value", zscript);
		$u.eval("updateChange_()");
	}

	static List<String> skippedCmps = Arrays
			.asList(new String[] { "zscript", "chart", "jasperrepot", "gmaps",
					"fckeditor", "include", "style", "script" });

	@Test(expected = AssertionError.class)
	public void test1() {
		Map<String, List<Function>> comps = getComponets();

		Properties prop = new Properties();
		try {
			prop.load(ClassLoader
					.getSystemResourceAsStream("values.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Selenium browser : browsers) {
//			try {
				start(browser);

				final String engine = this.getAttribute("//input/@id");
//				int skip = 0;
				int count = 0;
				log("============== total size [" + comps.size()
						+ "] =====================");
				for (Map.Entry<String, List<Function>> me : comps.entrySet()) {
					count++;
//					if (--skip > 0)
//						continue;
					String name = me.getKey();
					if (skippedCmps.contains(name))
						continue;

					log("************** " + name + " [" + count
							+ "] *********************");
					char[] buf = name.toCharArray();
					buf[0] = Character.toUpperCase(buf[0]);
					final String conf = new String(buf);
					String zscript = conf + " " + name + " = new " + conf
							+ "(); " + name + ".setId(\"" + name + "\"); "
							+ name + ".setPage(self.page);";

					Widget $u = widget(engine);

					fire(engine, zscript);

					Widget $f = $u.$f(name);
					assertTrue(name + " component not found!", Boolean
							.valueOf($f.eval(" != null", false)));

					for (Function f : me.getValue()) {
						String methodName = f.getName();
						if (methodName.startsWith("on"))
							continue;
						String type = f.getType();

						if ("string".equals(type)) {
							String value = (String) prop.get(methodName);
							if (value == null) {
								log(name + "." + methodName
										+ " unsupported to auto test.");
								continue;
							}
							fire(engine, name + "." + methodName + "=\""
									+ value + "\";");
							System.out.print(name + "." + methodName + " = "
									+ value + ";");
							String result = $f.get(methodName);
							if (result == null)
								result = $f.eval("_" + methodName);
							assertEquals(methodName + " = " + value, value,
									result);
							log(" test OK");
						} else if ("booleanType".equals(type)) {
							String value = (String) prop.get(methodName);
							if (value == null) {
								log(name + "." + methodName
										+ " unsupported to auto test.");
								continue;
							}
							if ("listbox".equals(name)
									&& "disabled".equals(methodName))
								continue;
							boolean bvalue = true;
							fire(engine, name + "." + methodName + "= "
									+ bvalue + ";");
							System.out.print(name + "." + methodName + " = "
									+ bvalue + ";");
							boolean result = $f.is(methodName);
							if (result != bvalue)
								result = Boolean.valueOf($f.eval("_"
										+ methodName));
							assertEquals(methodName + " = " + value, bvalue,
									result);
							log(" test OK");
							bvalue = false;
							fire(engine, name + "." + methodName + "= "
									+ bvalue + ";");
							System.out.print(name + "." + methodName + " = "
									+ bvalue + ";");
							result = $f.is(methodName);
							if (result != bvalue)
								result = Boolean.valueOf($f.eval("_"
										+ methodName));
							assertEquals(methodName + " = " + value, bvalue,
									result);
							log(" test OK");
						} else if ("intType".equals(type)) {
							String value = (String) prop.get(methodName);
							if (value == null) {
								log(name + "." + methodName
										+ " unsupported to auto test.");
								continue;
							}
							if ("listbox".equals(name)
									&& ("maxlength".equals(methodName) || "tabindex"
											.equals(methodName)))
								continue;
							int ivalue = 1;
							fire(engine, name + "." + methodName + "=" + ivalue
									+ ";");
							System.out.print(name + "." + methodName + " = "
									+ ivalue + ";");
							String result = $f.get(methodName);
							if (result == null)
								result = $f.eval("_" + methodName);
							assertEquals(methodName + " = " + ivalue, "1",
									result);
							log(" test OK");
						}

					}
					fire(engine, name + ".detach();");
				}
//			} finally {
				// stop();
//			}
		}
	}

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, List<Function>> getComponets() {
		try {
			XSOMParser parser = new XSOMParser();
			parser.parse(ClassLoader.getSystemResource("zul.xsd"));
			XSSchemaSet schemaSet = parser.getResult();
			XSSchema s = schemaSet.getSchema(1);
			Iterator<XSElementDecl> jtr = schemaSet.iterateElementDecls();
			XSModelGroupDecl anygroup = s.getModelGroupDecl("anyGroup");
			XSParticle[] children = anygroup.getModelGroup().getChildren();

			HashMap map = new HashMap(children.length);
			for (int i = 0; i < children.length; i++) {
				XSElementDecl el = children[i].getTerm().asElementDecl();
				if (el != null)
					map.put(el.getName(), el);
			}
			ArrayList<XSElementDecl> al = new ArrayList<XSElementDecl>(100);
			while (jtr.hasNext()) {
				XSElementDecl e = jtr.next();
				if (map.containsKey(e.getName()))
					al.add(e);
			}
			Collections.sort(al, new Comparator() {
				public int compare(Object o1, Object o2) {
					return ((XSElementDecl) o1).getName().compareTo(
							((XSElementDecl) o2).getName());
				}
			});

			HashMap<String, List<Function>> compMap = new LinkedHashMap<String, List<Function>>(
					100);
			for (XSElementDecl e : al) {
				List<Function> funs = new ArrayList<Function>(30);
				XSComplexType t = e.getType().asComplexType();
				for (Iterator it = t.getDeclaredAttributeUses().iterator(); it
						.hasNext();) {

					XSAttributeUse xu = (XSAttributeUse) it.next();

					XSAttributeDecl ad = xu.getDecl();
					funs
							.add(new Function(ad.getName(), ad.getType()
									.getName()));
					/**
					 * System.out.print(ad.getName() + "=" +
					 * ad.getType().getName() + ",");
					 */
				}
				getDeclaredAttributeUses(t, funs);
				if (!"zk".equals(e.getName()))
					compMap.put(e.getName(), funs);

			}
			return compMap;
		} catch (Exception exp) {
			exp.printStackTrace(System.out);
		}
		return null;
	}

	static List unusedGroup = Arrays.asList(new String[] { "mouseAttrGroup",
			"mouseExtAttrGroup", "rootAttrGroup", "zkAttrGroup",
			"abstractComponentAttrGroup" });

	static void getDeclaredAttributeUses(XSComplexType t, List<Function> funs) {
		for (XSAttGroupDecl agd : t.getAttGroups()) {
			if (unusedGroup.contains(agd.getName()))
				continue;
			for (XSAttributeUse xu : agd.getDeclaredAttributeUses()) {
				XSAttributeDecl ad = xu.getDecl();
				funs.add(new Function(ad.getName(), ad.getType().getName()));
			}
			getDeclaredAttributeUses(agd, funs);
		}
	}

	static void getDeclaredAttributeUses(XSAttGroupDecl t, List<Function> funs) {
		for (XSAttGroupDecl agd : t.getAttGroups()) {
			if (unusedGroup.contains(agd.getName()))
				continue;
			for (XSAttributeUse xu : agd.getDeclaredAttributeUses()) {
				XSAttributeDecl ad = xu.getDecl();
				funs.add(new Function(ad.getName(), ad.getType().getName()));
			}
			getDeclaredAttributeUses(agd, funs);
		}
	}
}

/**package*/class Function {
	private String _name, _type;

	public Function(String name, String type) {
		_name = name;
		_type = type;
	}

	public String getName() {
		return _name;
	}

	public String getType() {
		return _type;
	}

	@Override
	public String toString() {
		return "name = [" + _name + "] type = [" + _type + "]";
	}
}
