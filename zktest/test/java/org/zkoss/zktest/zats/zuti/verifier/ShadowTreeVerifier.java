/** ShadowTreeVerifier.java.

	Purpose:
		
	Description:
		
	History:
		5:09:13 PM Nov 6, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.verifier;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.omg.CORBA.UNKNOWN;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlShadowElement;
import org.zkoss.zk.ui.HtmlShadowElement.Direction;
import org.zkoss.zk.ui.ShadowElement;

/**
 * @author jumperchen
 *
 */
public class ShadowTreeVerifier extends BasicVerifier {
	protected void verify() {
		AbstractComponent host = getHost();
		List<AbstractComponent> children = host.getChildren();
		List<HtmlShadowElement> shadowRoots = host.getShadowRoots();
		ListIterator<HtmlShadowElement> sit = shadowRoots.listIterator();
		HtmlShadowElement seNext = sit.hasNext() ? sit.next() : null;
		
		if (children.isEmpty()) {
			assertOnlySon(seNext);
			assertEmpty(seNext);
			return;
		}
		
		for (ListIterator<AbstractComponent> cit = children.listIterator(); cit.hasNext();) {
			AbstractComponent next = cit.next();
			if (seNext != null) {
				Direction dir = HtmlShadowElement.inRange(seNext, next);
				switch(dir) {
				case AFTER_NEXT:
				case NEXT:
					cit.previous(); // go back
					checkShadowElement(seNext, dir);
					if (sit.hasNext())
						seNext = sit.next();
					else
						seNext = null;
					break;
				case FIRST:
				case IN_RANGE:
				case LAST:
					checkShadowElement(seNext, next, cit);
					if (sit.hasNext())
						seNext = sit.next();
					else
						seNext = null;
					break;
				case UNKNOWN:
					checkShadowElement(seNext, dir);
					if (sit.hasNext())
						seNext = sit.next();
					else {
						seNext = null; // not in the range
					}
					cit.previous(); // go back
					break;
				}
			}
		}
	}

	private void checkShadowElement(HtmlShadowElement se, AbstractComponent current, ListIterator<AbstractComponent> cit) {
		
		List<HtmlShadowElement> children = se.getChildren();
		
		AbstractComponent next = current;
		ListIterator<HtmlShadowElement> sit = children.listIterator();
		if (sit.hasNext()) {
			HtmlShadowElement seNext = sit.next();
			boolean first = true;
			do {
				if (!first) { // avoid first time to invoke
					next = cit.next();
				}
				first = false;
				if (seNext != null) {
					Direction dir = HtmlShadowElement.inRange(seNext, next);
					switch(dir) {
					case NEXT:
						checkShadowElement(seNext, dir);
					case AFTER_NEXT:
						checkNextShadowElement(seNext, next);
						cit.previous(); // go back
						if (sit.hasNext())
							seNext = sit.next();
						else {
							seNext = null; // not in the range
						}
						break;
					case FIRST:
					case LAST:
					case IN_RANGE:
						checkShadowElement(seNext, next, cit);
						if (sit.hasNext()) {
							seNext = sit.next();
							if (!cit.hasNext()) // draw the rest
								sit.previous();
						} else {
							seNext = null; // not in the range
						}
						break;
					case UNKNOWN:
						checkShadowElement(seNext, dir);
						if (sit.hasNext())
							seNext = sit.next();
						else {
							seNext = null; // not in the range
						}
						cit.previous(); // go back
						break;
					case PREVIOUS:
						checkPrevShadowElement(seNext, next);
						// no break;
					default:
						
						// draw the reset shadows
						if (!cit.hasNext()) {
							sit.previous();
							while (sit.hasNext()) {
								checkShadowElement(sit.next(), dir);
							}
						}
					}
				} else {
					switch(HtmlShadowElement.inRange(se, next)) { // in parent range
					case IN_RANGE:
					case FIRST:
					case LAST:
						break;
					default:
						cit.previous(); // go back

						return; // out of parent range;
					}
				}
			} while (cit.hasNext());
			
			// draw the reset shadows
			while (sit.hasNext()) {
				checkShadowElement(sit.next(), Direction.UNKNOWN);
			}
		} else {
			cit.previous();
			do {
				next = cit.next();
				switch(HtmlShadowElement.inRange(se, next)) { // in parent range
				case IN_RANGE:
				case FIRST:
				case LAST:
					break;
				default:
					cit.previous(); // go back

					return; // out of parent range;
				}
			} while (cit.hasNext());
		}
		return;
	}
	private static int getIndex(Component child) {
		if (child == null)
			return -1;
		if (child.getParent() == null)
			fail("The child cannot be orphan" + child);
		if (child instanceof ShadowElement)
			return -1; // cannot compare component with shadow
		return child.getParent().getChildren().indexOf(child);
	}
	private void checkNextShadowElement(HtmlShadowElement se, AbstractComponent next) {
		if (se.getNextSibling() != null) {
			HtmlShadowElement nextSibling = (HtmlShadowElement) se.getNextSibling();
			assertNotNull(nextSibling.getPreviousInsertion());
			int nextIndex = getIndex(nextSibling.getPreviousInsertion());
			int prevIndex = getIndex(next);
			
			if (nextIndex == -1 || prevIndex > nextIndex)
				fail("Wrong insertion point [" + se + "]");
			 
		}
	}
	private void checkPrevShadowElement(HtmlShadowElement se, AbstractComponent prev) {
		if (se.getPreviousSibling() != null) {
			HtmlShadowElement prevSibling = (HtmlShadowElement) se.getPreviousSibling();
			assertNotNull(prevSibling.getNextInsertion());
			int prevIndex = getIndex(prevSibling.getNextInsertion());
			int nextIndex = getIndex(prev);
			
			if (prevIndex == -1 || prevIndex > nextIndex)
				fail("Wrong insertion point [" + se + "]");
			 
		}
	}
	private void checkShadowElement(HtmlShadowElement se, Direction dir) {
		Component previousInsertion, nextInsertion;
		List<HtmlShadowElement> children = se.getChildren();
		switch (dir) {
		case UNKNOWN:
			previousInsertion = se.getPreviousInsertion();
			nextInsertion = se.getNextInsertion();
			if (!(previousInsertion instanceof ShadowElement)) {
				assertFirstChild(se);
			} else {
				assertEquals(((HtmlShadowElement)previousInsertion).getNextInsertion(), se);
			}
			
			if (!(nextInsertion instanceof ShadowElement)) {
				assertLastChild(se);
			} else {
				assertEquals(((HtmlShadowElement)nextInsertion).getPreviousInsertion(), se);
			}
			
			if (children.isEmpty()) {
				assertEmpty(se);
			} else {
				for (Iterator<HtmlShadowElement> sit = children.iterator();	sit.hasNext();) {
					checkShadowElement(sit.next(), dir);
				}
			}
			break;
		default:
			if (children.isEmpty()) {
				assertEmpty(se);
			} else {
				for (Iterator<HtmlShadowElement> sit = children.iterator();	sit.hasNext();) {
					checkShadowElement(sit.next(), dir);
				}
			}
		}
	}
}
