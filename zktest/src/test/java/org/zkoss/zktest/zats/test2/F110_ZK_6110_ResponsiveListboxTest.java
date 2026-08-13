/* F110_ZK_6110_ResponsiveListboxTest.java

        Purpose:
                
        Description:
                
        History:
                Thu Jul 23 14:50:29 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F110_ZK_6110_ResponsiveListboxTest extends WebDriverTestCase {

	private static final int WIDE = 1024; // >= md (768)
	private static final int NARROW = 600; // sm range (576..767) -> stacking

	/** Repro: wide→table (no stacking class, header shown, no data-label);
	 * narrow→stacking (header hidden, each td gets its column's data-label);
	 * widen→table again. */
	@Test
	public void testBasicStacking() {
		connect("/test2/F110-ZK-6110-responsive-listbox-basic.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;

		driver.manage().window().setSize(new Dimension(WIDE, h));
		waitResponse();
		JQuery lb = jq("$lb1");
		assertFalse(lb.hasClass("z-listbox--stacking"), "no stacking class in table mode");
		assertTrue(jq("$lb1 .z-listbox-header").isVisible(), "header visible in table mode");
		assertEquals(0, jq("$lb1 td[data-label]").length(), "no data-label in table mode");

		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(lb.hasClass("z-listbox--stacking")); // stacking class present when narrow
		assertFalse(jq("$lb1 .z-listbox-header").isVisible());
		assertTrue(jq(".z-listbox-body tbody > tr.z-listitem > td[data-label]").length() > 0);
		assertEquals("Name", jq(".z-listbox-body tbody > tr.z-listitem:eq(0) td:eq(0)").attr("data-label"));
		assertEquals("Email", jq(".z-listbox-body tbody > tr.z-listitem:eq(0) td:eq(1)").attr("data-label"));
		assertEquals("Department", jq(".z-listbox-body tbody > tr.z-listitem:eq(0) td:eq(2)").attr("data-label"));
		assertEquals(3, jq(".z-listbox-body tbody > tr.z-listitem").length()); // 3 items in stacking

		driver.manage().window().setSize(new Dimension(WIDE, h)); // restored to table mode when widened
		waitResponse();
		assertFalse(lb.hasClass("z-listbox--stacking"));
		assertTrue(jq("$lb1 .z-listbox-header").isVisible());
	}

	/** Repro: responsive="none"; narrow the window → must never enter stacking. */
	@Test
	public void testResponsiveNone() {
		connect("/test2/F110-ZK-6110-responsive-listbox-none.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertFalse(jq("$lb1").hasClass("z-listbox--stacking"));
		assertTrue(jq("$lb1 .z-listbox-header").isVisible(), "header stays visible");
	}

	/** Repro: responsiveColumns="sm-2 md-none"; narrow→stacking with
	 * --zk-resp-cols=2 (2 cards/row); widen→table (md-none). */
	@Test
	public void testResponsiveColumnsCascade() {
		connect("/test2/F110-ZK-6110-responsive-listbox-columns.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb1").hasClass("z-listbox--stacking"), "stacking below md");
		String cols = getEval("jq('$lb1')[0].style.getPropertyValue('--zk-resp-cols')").trim();
		assertEquals("2", cols, "--zk-resp-cols should be 2 for sm-2");

		driver.manage().window().setSize(new Dimension(WIDE, h));
		waitResponse();
		assertFalse(jq("$lb1").hasClass("z-listbox--stacking"), "md-none -> table at/above md");
	}

	/** Repro: a listheader with responsiveVisible="false"; narrow→that column's
	 * cell gets z-cell-hide-stacking, the other columns' cells do not. */
	@Test
	public void testResponsiveVisibleHidesColumn() {
		connect("/test2/F110-ZK-6110-responsive-listbox-responsivevisible.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb1").hasClass("z-listbox--stacking"));
		// The Email column (index 1) is hidden in stacking: its <td> carries
		// z-cell-hide-stacking (and _markStackingCells removed its data-label).
		assertTrue(jq(".z-listbox-body tbody > tr.z-listitem:eq(0) td:eq(1)").hasClass("z-cell-hide-stacking"));
		// The other two columns are NOT hidden.
		assertFalse(jq(".z-listbox-body tbody > tr.z-listitem:eq(0) td:eq(0)").hasClass("z-cell-hide-stacking"));
		assertFalse(jq(".z-listbox-body tbody > tr.z-listitem:eq(0) td:eq(2)").hasClass("z-cell-hide-stacking"));
		// Visible columns still labelled.
		assertEquals("Name", jq(".z-listbox-body tbody > tr.z-listitem:eq(0) td:eq(0)").attr("data-label"));
		assertEquals("Department", jq(".z-listbox-body tbody > tr.z-listitem:eq(0) td:eq(2)").attr("data-label"));
	}

	/** Repro: checkmark + multiple selection; narrow→cell count still equals
	 * column count and labels are not shifted (checkmark is a span inside the
	 * first cell, not a separate td). */
	@Test
	public void testCheckmarkNoLabelShift() {
		connect("/test2/F110-ZK-6110-responsive-listbox-checkmark.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb1").hasClass("z-listbox--stacking"), "stacking when narrow");
		// td count == column count (checkmark is a span inside first td, not a separate td)
		assertEquals(2, jq(".z-listbox-body tbody > tr.z-listitem:eq(0) td").length());
		assertEquals("Name", jq(".z-listbox-body tbody > tr.z-listitem:eq(0) td:eq(0)").attr("data-label"));
		assertEquals("Email", jq(".z-listbox-body tbody > tr.z-listitem:eq(0) td:eq(1)").attr("data-label"));
	}

	/** Repro: click an item in stacking to select+focus it → the focus ring is
	 * drawn once on the card (tr.z-listitem-focus), not per-cell, so there is no
	 * gap at the first cell's right / last cell's left edge. */
	@Test
	public void testFocusRingOnCardNotPerCell() {
		connect("/test2/F110-ZK-6110-responsive-listbox-basic.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb1").hasClass("z-listbox--stacking"), "stacking when narrow");
		click(jq(".z-listbox-body tbody > tr.z-listitem:eq(0)"));
		waitResponse();
		JQuery focused = jq(".z-listbox-body tbody > tr.z-listitem-focus");
		assertTrue(focused.exists());
		// The whole-card focus ring is drawn as an outline (paints on top of the
		// opaque selected cells; an inset box-shadow would be occluded by them).
		assertEquals("2px", focused.css("outline-width"));
		assertNotEquals("none", focused.css("outline-style"));
		// ...and the table-mode per-cell focus box-shadow is cleared in stacking.
		assertEquals("none", jq(".z-listbox-body tbody > tr.z-listitem-focus > td:eq(0)").css("box-shadow"));
	}

	/** Repro: a listheader with visible="false" must stay hidden in stacking too
	 * (a hidden column must not reappear as a card entry). The Email column
	 * (index 1) is visible=false; in stacking its cell carries z-cell-hide-stacking
	 * and has no data-label, while Name/Department show. */
	@Test
	public void testHiddenColumnStaysHiddenInStacking() {
		connect("/test2/F110-ZK-6110-responsive-listbox-hidden-column.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb1").hasClass("z-listbox--stacking"));
		assertTrue(jq(".z-listbox-body tbody > tr.z-listitem:eq(0) td:eq(1)").hasClass("z-cell-hide-stacking"));
		assertFalse(jq(".z-listbox-body tbody > tr.z-listitem:eq(0) td:eq(0)").hasClass("z-cell-hide-stacking"));
		assertFalse(jq(".z-listbox-body tbody > tr.z-listitem:eq(0) td:eq(2)").hasClass("z-cell-hide-stacking"));
	}

	/** Repro: checkmark + first listheader responsiveVisible="false". The
	 * checkmark rides inside that first cell, so the cell must stay shown (not
	 * z-cell-hide-stacking) and the per-card checkbox stays visible; only the
	 * column's data-label is suppressed. */
	@Test
	public void testCheckmarkSurvivesResponsiveVisibleFalseFirstColumn() {
		connect("/test2/F110-ZK-6110-responsive-listbox-checkmark.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb2").hasClass("z-listbox--stacking"));
		// The first column is responsiveVisible="false" AND hosts the selection
		// checkmark. Expected (option 2): the cell stays shown so the checkbox
		// survives, but the column's DATA is suppressed — no data-label, and the
		// (bare-text) value is collapsed (content font-size:0) while the checkbox
		// keeps its own icon size. Query the cells off the lb2 node directly — a
		// "$id descendant :eq" jq selector is unreliable with two listboxes.
		String cells = "jq('$lb2')[0].querySelectorAll('.z-listbox-body tbody > tr.z-listitem')[0].querySelectorAll('td')";
		String cm = cells + "[0].querySelector('.z-listitem-checkable')";
		String content = cells + "[0].querySelector('.z-listcell-content')";
		assertEquals("false", getEval("''+" + cells + "[0].classList.contains('z-cell-hide-stacking')"),
				"checkmark cell must not be display:none in stacking");
		assertEquals("true", getEval("''+(" + cm + "!=null && " + cm + ".offsetWidth>0)"),
				"the per-card checkbox survives and is rendered");
		assertEquals("true", getEval("''+(" + cells + "[0].getAttribute('data-label')==null)"),
				"the responsiveVisible=false column's label is suppressed");
		assertEquals("0px", getEval("getComputedStyle(" + content + ").fontSize"),
				"the column's value is collapsed (content font-size:0)");
		assertEquals("true", getEval("''+(getComputedStyle(" + cm + ").fontSize!='0px')"),
				"the checkbox keeps its own icon size");
		assertEquals("Email", getEval(cells + "[1].getAttribute('data-label')"),
				"the visible column keeps its data-label");
		// Layout: the card becomes a [checkbox gutter | content] grid so the fields
		// auto-indent beside the checkbox instead of it occupying a lonely row.
		String row = "jq('$lb2')[0].querySelectorAll('.z-listbox-body tbody > tr.z-listitem')[0]";
		assertEquals("grid", getEval("getComputedStyle(" + row + ").display"),
				"card uses the checkbox-gutter grid layout");
		assertEquals("1", getEval("getComputedStyle(" + cells + "[0]).gridColumnStart"),
				"checkbox sits in gutter column 1");
		assertEquals("2", getEval("getComputedStyle(" + cells + "[1]).gridColumnStart"),
				"fields flow into content column 2");
		// The gutter cell spans every row and stretches to fill column 1, so the
		// per-cell hover/focus paint has no empty strip to show the card background
		// through (no stray focus line / white hover gap).
		assertEquals("stretch", getEval("getComputedStyle(" + cells + "[0]).alignSelf"),
				"gutter cell stretches to fill its column");
	}

	/** Repro (ZK-6110 refinement): a normal checkmark card (lb1, first column
	 * visible) pulls the checkbox into a left gutter via z-cell-cm-lead: the cell
	 * is padded past the gutter and the checkbox floats back into it (hanging
	 * indent), so labels and values line up at the content edge while the cell's
	 * content keeps normal flow — pure CSS, no DOM move, value kept. */
	@Test
	public void testCheckmarkGutterOnNormalCard() {
		connect("/test2/F110-ZK-6110-responsive-listbox-checkmark.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb1").hasClass("z-listbox--stacking"));
		String cells = "jq('$lb1')[0].querySelectorAll('.z-listbox-body tbody > tr.z-listitem')[0].querySelectorAll('td')";
		String cm = cells + "[0].querySelector('.z-listitem-checkable')";
		String content0 = cells + "[0].querySelector('.z-listcell-content')";
		// The checkbox rides in the first VISIBLE cell — tagged z-cell-cm-lead, and
		// (unlike lb2's responsiveVisible=false gutter) NOT z-cell-cm-only.
		assertEquals("true", getEval("''+" + cells + "[0].classList.contains('z-cell-cm-lead')"),
				"normal checkbox cell tagged z-cell-cm-lead");
		assertEquals("false", getEval("''+" + cells + "[0].classList.contains('z-cell-cm-only')"),
				"not the collapsed responsiveVisible=false gutter");
		// Hanging indent: the checkbox floats back into the 28px gutter while the
		// caption and value keep normal flow at the 36px content edge.
		double cmLeft = Double.parseDouble(getEval("''+(" + cm + ".getBoundingClientRect().left - "
				+ cells + "[0].getBoundingClientRect().left)"));
		assertTrue(cmLeft >= 4 && cmLeft < 28,
				"checkbox sits inside the 28px gutter (offset=" + cmLeft + ")");
		double contentLeft = Double.parseDouble(getEval("''+(" + content0 + ".getBoundingClientRect().left - "
				+ cells + "[0].getBoundingClientRect().left)"));
		assertTrue(contentLeft >= 30,
				"cell content starts in the content area past the gutter (offset=" + contentLeft + ")");
		// Paint-level check: a laid-out box can still be clipped away (offsetWidth
		// stays > 0), so hit-test the checkbox's center instead.
		assertEquals("true", getEval("(function(){var cm=" + cm + ";cm.scrollIntoView({block:'center'});"
				+ "var r=cm.getBoundingClientRect(),e=document.elementFromPoint(r.left+r.width/2,r.top+r.height/2);"
				+ "return ''+(e===cm||cm.contains(e));}())"),
				"checkbox must actually paint in the gutter (not clipped)");
		// The two checkmark layouts (cm-lead / cm-only) must present the checkbox
		// at the same position within the card.
		String cmPos = "(function(w){var cm=jq(w)[0].querySelector('.z-listitem-checkable'),"
				+ "tr=cm.closest('tr'),r=cm.getBoundingClientRect(),t=tr.getBoundingClientRect();"
				+ "return Math.round(r.left-t.left)+','+Math.round(r.top-t.top);})";
		assertEquals(getEval(cmPos + "('$lb2')"), getEval(cmPos + "('$lb1')"),
				"cm-lead and cm-only checkboxes must share the same in-card position");
		assertTrue(getEval(cells + "[0].textContent").contains("John"),
				"first column value stays visible (not collapsed)");
		// Both columns keep their labels, aligned in the same content column.
		assertEquals("Name", getEval(cells + "[0].getAttribute('data-label')"));
		assertEquals("Email", getEval(cells + "[1].getAttribute('data-label')"));
	}

	/** Repro: updating a listcell's label while stacking rerenders its whole td
	 * (the content wrapper div defeats the in-place text fast path, so setLabel
	 * always goes through rerender -> Listitem.replaceChildHTML_); the responsive
	 * stamps must be re-applied — a normal cell keeps its data-label, a
	 * responsiveVisible=false cell stays hidden, and a footer cell keeps its
	 * data-label after a listfooter update. */
	@Test
	public void testCellUpdateKeepsStampsInStacking() {
		connect("/test2/F110-ZK-6110-responsive-listbox-cell-update.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb1").hasClass("z-listbox--stacking"));
		String cells = "jq('$lb1')[0].querySelectorAll('.z-listbox-body tbody > tr.z-listitem')[0].querySelectorAll('td')";
		assertEquals("Name", getEval(cells + "[0].getAttribute('data-label')"));
		assertEquals("true", getEval("''+" + cells + "[1].classList.contains('z-cell-hide-stacking')"));

		click(jq("$updateName"));
		waitResponse();
		assertTrue(getEval(cells + "[0].textContent").contains("Johnny"), "label updated");
		assertEquals("Name", getEval(cells + "[0].getAttribute('data-label')"),
				"data-label must survive the cell rerender");

		click(jq("$updateEmail"));
		waitResponse();
		assertEquals("true", getEval("''+" + cells + "[1].classList.contains('z-cell-hide-stacking')"),
				"responsiveVisible=false column must stay hidden after its cell rerenders");

		String foot = "jq('$lb1')[0].querySelector(':scope > .z-listbox-footer > table > tbody > tr').querySelectorAll('td')";
		assertEquals("Name", getEval(foot + "[0].getAttribute('data-label')"));
		click(jq("$updateFoot"));
		waitResponse();
		assertTrue(getEval(foot + "[0].textContent").contains("Total: 2"), "footer label updated");
		assertEquals("Name", getEval(foot + "[0].getAttribute('data-label')"),
				"footer data-label must survive the listfooter rerender");
	}

	/** Repro: a checkable listbox nested inside a listcell — the outer
	 * (checkmark-less) stacking listbox must not mistake the NESTED checkmark
	 * for its own: no z-cell-cm-lead / z-cell-cm-only on the outer row's cells,
	 * and the outer data-labels stay intact. */
	@Test
	public void testNestedCheckmarkNotMisclassified() {
		connect("/test2/F110-ZK-6110-responsive-listbox-nested-checkmark.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$outer").hasClass("z-listbox--stacking"));
		assertFalse(jq("$inner").hasClass("z-listbox--stacking"), "inner listbox never opted in");
		// Outer row's own cells only (direct-child chain — the inner listbox's
		// rows are deeper in the subtree).
		String cells = "jq('$outer')[0].querySelectorAll(':scope > .z-listbox-body > table > tbody > tr.z-listitem')[0].querySelectorAll(':scope > td')";
		assertEquals("false", getEval("''+" + cells + "[0].classList.contains('z-cell-cm-lead')"));
		assertEquals("false", getEval("''+" + cells + "[1].classList.contains('z-cell-cm-lead')"),
				"nested checkmark must not tag the outer cell as checkbox host");
		assertEquals("false", getEval("''+" + cells + "[1].classList.contains('z-cell-cm-only')"));
		assertEquals("Name", getEval(cells + "[0].getAttribute('data-label')"));
		assertEquals("Choices", getEval(cells + "[1].getAttribute('data-label')"));
		// Sanity: the nested checkmarks themselves still render.
		assertTrue(jq("$inner .z-listitem-checkable").exists());
		// The stacking card CSS must not leak into the nested (non-stacking)
		// listbox: its body keeps table layout, not the card grid.
		String innerTbody = "jq('$inner')[0].querySelector('.z-listbox-body > table > tbody')";
		assertEquals("table-row-group", getEval("getComputedStyle(" + innerTbody + ").display"),
				"nested listbox body must keep table layout (no card-grid leak)");
		assertEquals("table-row", getEval("getComputedStyle(" + innerTbody + ".querySelector('tr.z-listitem')).display"),
				"nested listitem must stay a table row (not a flex card)");
	}

	/** Repro: a responsiveVisible="false" checkmark cell (z-cell-cm-only) whose
	 * listcell hosts a child component. The column's data is suppressed with
	 * font-size:0, which silences text but not an element — the component would
	 * render squeezed inside the 28px gutter cell. It must be hidden like the
	 * rest of the column's data, while the checkbox itself stays rendered. */
	@Test
	public void testCmOnlyHidesChildComponent() {
		connect("/test2/F110-ZK-6110-responsive-listbox-checkmark.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(WIDE, h));
		waitResponse();
		assertTrue(Integer.parseInt(getEval("''+jq('$cmOnlyBtn')[0].offsetWidth")) > 0,
				"table mode shows the component normally");
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb2").hasClass("z-listbox--stacking"));
		String cells = "jq('$lb2')[0].querySelectorAll('.z-listbox-body tbody > tr.z-listitem')[0].querySelectorAll('td')";
		String cm = cells + "[0].querySelector('.z-listitem-checkable')";
		assertEquals("true", getEval("''+" + cells + "[0].classList.contains('z-cell-cm-only')"));
		assertEquals("0", getEval("''+jq('$cmOnlyBtn')[0].offsetWidth"),
				"the hidden column's child component must be hidden with its data");
		assertEquals("true", getEval("''+(" + cm + "!=null && " + cm + ".offsetWidth>0)"),
				"the checkbox itself still renders");
	}

	/** Repro: a cm-only card with two visible fields — the checkbox gutter cell
	 * must span every field row. An implicit row grid resolves grid-row-end:-1
	 * back to line 1 (span 1), leaving an unpainted 28px notch beside the
	 * fields below the first, visible on hover/selection. */
	@Test
	public void testCmOnlyGutterSpansAllFieldRows() {
		connect("/test2/F110-ZK-6110-responsive-listbox-checkmark.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb2").hasClass("z-listbox--stacking"));
		String cells = "jq('$lb2')[0].querySelectorAll('.z-listbox-body tbody > tr.z-listitem')[0].querySelectorAll('td')";
		double gutterBottom = Double.parseDouble(
				getEval("''+" + cells + "[0].getBoundingClientRect().bottom"));
		double lastFieldBottom = Double.parseDouble(
				getEval("''+" + cells + "[2].getBoundingClientRect().bottom"));
		assertTrue(gutterBottom >= lastFieldBottom - 1,
				"gutter cell must span all field rows (gutter bottom=" + gutterBottom
						+ ", last field bottom=" + lastFieldBottom + ")");
	}

	/** Repro: a cm-lead cell with image + label — the content must stay on one
	 * line below the caption; fragmented grid items would stack a blank (nbsp)
	 * row and put the image and label on separate rows. */
	@Test
	public void testCheckmarkImageCellKeepsOneContentLine() {
		connect("/test2/F110-ZK-6110-responsive-listbox-checkmark.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb4").hasClass("z-listbox--stacking"));
		String cell = "jq('$imgCell')[0]";
		assertEquals("true", getEval("''+(" + cell + ".querySelector('img').naturalWidth>0)"),
				"the fixture image must actually load");
		String overlap = "(function(){var c=" + cell + ",ct=c.querySelector('.z-listcell-content'),"
				+ "im=c.querySelector('img'),tw=document.createTreeWalker(ct,NodeFilter.SHOW_TEXT),t,last=null;"
				+ "while((t=tw.nextNode())){if(t.textContent.trim())last=t;}"
				+ "var r=document.createRange();r.selectNodeContents(last);"
				+ "var tr=r.getBoundingClientRect(),ir=im.getBoundingClientRect();"
				+ "return ''+(ir.top<tr.bottom&&tr.top<ir.bottom);}())";
		assertEquals("true", getEval(overlap),
				"image and label must share one content line (no fragmented rows)");
	}

	/** Repro (PR review): a checkmark cell whose listcell hosts a child component —
	 * it must stay in the content area beside the value, never inside the 28px
	 * checkbox gutter. */
	@Test
	public void testCheckmarkGutterKeepsChildComponentInContentColumn() {
		connect("/test2/F110-ZK-6110-responsive-listbox-checkmark.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb3").hasClass("z-listbox--stacking"));
		String cell = "jq('$lb3')[0].querySelectorAll('.z-listbox-body tbody > tr.z-listitem')[0].querySelector('td')";
		assertEquals("true", getEval("''+" + cell + ".classList.contains('z-cell-cm-lead')"));
		// The button must sit in the content column (right of the 28px gutter),
		// aligned with the value — not wrapped into the gutter column.
		String offset = getEval("''+(jq('$cellBtn')[0].getBoundingClientRect().left - "
				+ cell + ".getBoundingClientRect().left)");
		assertTrue(Double.parseDouble(offset) >= 30,
				"child component must not be squeezed into the checkbox gutter (offset=" + offset + ")");
	}

	/** Repro: a listbox with NO responsive attribute of its own resolves the
	 * effective value from an ancestor attribute (Listbox.getEffectiveResponsive
	 * / getEffectiveResponsiveColumns, the same chain as the global library
	 * property); an invalid effective value is clamped to null (no stacking). */
	@Test
	public void testEffectiveResponsiveFromAncestorAttribute() {
		connect("/test2/F110-ZK-6110-responsive-listbox-libdefault.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		// lbInherit inherits responsive=stacking + responsiveColumns=sm-2 from the ancestor.
		assertTrue(jq("$lbInherit").hasClass("z-listbox--stacking"));
		String cols = getEval("jq('$lbInherit')[0].style.getPropertyValue('--zk-resp-cols')").trim();
		assertEquals("2", cols, "responsiveColumns inherited (sm-2) -> 2 cards/row");
		// lbBad's ancestor value is invalid: getEffectiveResponsive clamps to null.
		assertFalse(jq("$lbBad").hasClass("z-listbox--stacking"));
	}

	/** Repro (PR review): responsiveColumns="md-none" has no numeric anchor, so the
	 * client falls back to the default cascade "sm-1 md-none". The fallback is a
	 * reachable authoring choice and must be silent — never an error box. */
	@Test
	public void testCascadeFallbackIsSilent() {
		connect("/test2/F110-ZK-6110-responsive-listbox-cascade-fallback.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb1").hasClass("z-listbox--stacking"), "fallback cascade stacks below md");
		assertEquals("1", getEval("jq('$lb1')[0].style.getPropertyValue('--zk-resp-cols')").trim(),
				"the default cascade resolves to 1 card/row");
		assertEquals(0, jq(".z-error").length(), "the fallback must not raise the ZK error box");

		driver.manage().window().setSize(new Dimension(WIDE, h));
		waitResponse();
		assertFalse(jq("$lb1").hasClass("z-listbox--stacking"), "md-none -> table at/above md");
	}

	/** Repro (PR review): with responsive inherited from an ancestor attribute,
	 * clearing the listbox's own value must restore the inherited stacking rather
	 * than pushing null and leaving the widget non-responsive. */
	@Test
	public void testRestoringNullFallsBackToInheritedResponsive() {
		connect("/test2/F110-ZK-6110-responsive-listbox-reset-default.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb1").hasClass("z-listbox--stacking"),
				"inherits responsive=stacking from the ancestor attribute");

		click(jq("$btnOptOut"));
		waitResponse();
		assertFalse(jq("$lb1").hasClass("z-listbox--stacking"), "responsive=none opts out");

		click(jq("$btnRestore"));
		waitResponse();
		assertTrue(jq("$lb1").hasClass("z-listbox--stacking"),
				"clearing the own value restores the inherited stacking");
	}

	/** Repro (PR review): a footer cell whose content is an element rather than text
	 * still carries content, so stacking must keep it; a genuinely empty footer cell
	 * must still collapse. */
	@Test
	public void testFooterImageCellSurvivesStacking() {
		connect("/test2/F110-ZK-6110-responsive-listbox-footer-image.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb1").hasClass("z-listbox--stacking"));
		String imgCell = "jq('$ftImage')[0]";
		assertEquals("false", getEval("''+" + imgCell + ".classList.contains('z-cell-hide-stacking')"),
				"a footer cell holding an image must not be treated as empty");
		assertEquals("true", getEval("''+(jq('$ftImg')[0].naturalWidth>0)"),
				"the fixture image must actually load");
		// Paint-level: the image is really on screen, not merely laid out.
		String hit = "(function(){var im=jq('$ftImg')[0],r=im.getBoundingClientRect();"
				+ "if(r.width<=0||r.height<=0)return 'false';"
				+ "var els=document.elementsFromPoint(r.left+r.width/2,r.top+r.height/2);"
				+ "return ''+(els.indexOf(im)>=0);}())";
		assertEquals("true", getEval(hit), "the footer image must be hit-testable on screen");
		assertEquals("true", getEval("''+jq('$ftEmpty')[0].classList.contains('z-cell-hide-stacking')"),
				"a genuinely empty footer cell must still collapse");
		// A cell wrapping only empty markup is still empty: keeping it would leave a
		// blank slot in the card where the total used to collapse away.
		assertEquals("true", getEval("''+jq('$ftBlankLabel')[0].classList.contains('z-cell-hide-stacking')"),
				"a footer cell holding only an empty label must still collapse");
	}

	/** Repro (PR review): a listbox authored without a listhead has no column
	 * information, so stacking must stamp nothing on its cells — neither labels and
	 * hide classes nor the checkbox-gutter layout — while checkmarks stay usable. */
	@Test
	public void testHeadlessListboxIsNotStamped() {
		connect("/test2/F110-ZK-6110-responsive-listbox-no-listhead.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb1").hasClass("z-listbox--stacking"), "still stacks without a listhead");
		String cell = "jq('$lb1')[0].querySelectorAll('.z-listbox-body tbody > tr.z-listitem')[0]"
				+ ".querySelectorAll('td')[0]";
		assertEquals("false", getEval("''+" + cell + ".classList.contains('z-cell-cm-lead')"),
				"no column info -> no checkbox-gutter layout");
		assertEquals("false", getEval("''+" + cell + ".classList.contains('z-cell-hide-stacking')"),
				"no column info -> nothing hidden");
		assertEquals("true", getEval("''+(" + cell + ".getAttribute('data-label')==null)"),
				"no column info -> no data-label");
		assertEquals("false", getEval("''+jq('$ftEmpty')[0].classList.contains('z-cell-hide-stacking')"),
				"without column info the empty-cell collapsing must not kick in either");
		// The checkmark must remain visible and clickable in stacking mode.
		String cm = "jq('$lb1')[0].querySelector('.z-listitem-checkable')";
		String cmHit = "(function(){var e=" + cm + ";if(!e)return 'false';"
				+ "var r=e.getBoundingClientRect();if(r.width<=0||r.height<=0)return 'false';"
				+ "var els=document.elementsFromPoint(r.left+r.width/2,r.top+r.height/2);"
				+ "return ''+els.some(function(x){return x===e||e.contains(x);});}())";
		assertEquals("true", getEval(cmHit), "the checkmark must stay hit-testable while stacked");
	}

	/** Repro (PR review): detaching the listhead while stacked removes all column
	 * information, so the stamps applied for hidden columns must be cleared instead
	 * of leaving those cells display:none forever. */
	@Test
	public void testDetachingListheadClearsHideStamps() {
		connect("/test2/F110-ZK-6110-responsive-listbox-listhead-removed.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lb1").hasClass("z-listbox--stacking"));
		String cell = "jq('$lb1')[0].querySelectorAll('.z-listbox-body tbody > tr.z-listitem')[0]"
				+ ".querySelectorAll('td')[1]";
		assertEquals("true", getEval("''+" + cell + ".classList.contains('z-cell-hide-stacking')"),
				"responsiveVisible=false hides the cell while stacked");

		click(jq("$btnRemoveHead"));
		waitResponse();
		assertEquals("false", getEval("''+" + cell + ".classList.contains('z-cell-hide-stacking')"),
				"detaching the listhead must clear the stale hide stamp");
		// Paint-level: the cell is genuinely laid out again, not a zero-height leftover.
		assertEquals("true", getEval("''+(" + cell + ".getBoundingClientRect().height>0)"),
				"the un-stamped cell must occupy space again");
		assertEquals("true", getEval("''+(" + cell + ".getAttribute('data-label')==null)"),
				"no column left to label the cell with");
	}

	/** A responsive-hidden column sitting between the checkmark cell and the
	 * first shown field must not leave a divider line beside the checkbox. */
	@Test
	public void testNoDividerBesideCheckboxWhenHiddenColumnFollows() {
		connect("/test2/F110-ZK-6110-responsive-listbox-checkmark-edge.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lbGap").hasClass("z-listbox--stacking"));
		assertEquals("0px", shownFieldBorderTop("$lbGapControl"),
				"control: suppression already works with no hidden column between");
		assertEquals("0px", shownFieldBorderTop("$lbGap"),
				"no divider beside the checkbox when a hidden column follows it");
	}

	/** border-top of the first field cell that is still shown in stacking; cells
	 * of responsive-hidden columns are display:none and never carry one. */
	private String shownFieldBorderTop(String listbox) {
		return getEval("(function(){var tds=jq('" + listbox + "')[0]"
				+ ".querySelectorAll('.z-listbox-body tbody > tr.z-listitem')[0].querySelectorAll('td'),"
				+ "shown=[],i;for(i=0;i<tds.length;i++)"
				+ "if(!tds[i].classList.contains('z-cell-hide-stacking'))shown.push(tds[i]);"
				+ "return getComputedStyle(shown[1]).borderTopWidth;}())");
	}

	/** A checkmark cell holding only a child component has no leading separator
	 * in its markup, so the cancelling indent must not pull it left of the
	 * content edge the other cells align to. */
	@Test
	public void testCheckmarkCellWithoutLeadingSeparatorKeepsContentEdge() {
		connect("/test2/F110-ZK-6110-responsive-listbox-checkmark-edge.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lbNoSep").hasClass("z-listbox--stacking"));
		String cells = "jq('$lbNoSep')[0].querySelectorAll("
				+ "'.z-listbox-body tbody > tr.z-listitem')[0].querySelectorAll('td')";
		assertEquals("true", getEval("''+" + cells + "[0].classList.contains('z-cell-cm-lead')"),
				"the component cell still hosts the checkmark");
		String drift = getEval("''+(jq('$noSepBtn')[0].getBoundingClientRect().left - "
				+ cells + "[1].querySelector('.z-listcell-content').getBoundingClientRect().left)");
		assertTrue(Math.abs(Double.parseDouble(drift)) < 1.5,
				"component aligns with the other cells' content edge (drift=" + drift + ")");
	}

	/** A cell carrying the checkmark and nothing else must lay out as the
	 * checkbox gutter beside the fields; as a band of its own the cell's content
	 * box collapses and the checkbox overflows it and is clipped away. */
	@Test
	public void testEmptyCheckmarkCellLaysOutAsGutter() {
		connect("/test2/F110-ZK-6110-responsive-listbox-checkmark-edge.zul");
		waitResponse();
		int h = driver.manage().window().getSize().height;
		driver.manage().window().setSize(new Dimension(NARROW, h));
		waitResponse();
		assertTrue(jq("$lbEmptyCm").hasClass("z-listbox--stacking"));
		String cell = "jq('$lbEmptyCm')[0].querySelectorAll("
				+ "'.z-listbox-body tbody > tr.z-listitem')[0].querySelectorAll('td')[0]";
		String cm = cell + ".querySelector('.z-listitem-checkable')";
		String overflow = getEval("''+(" + cm + ".getBoundingClientRect().bottom - "
				+ cell + ".getBoundingClientRect().bottom)");
		assertTrue(Double.parseDouble(overflow) <= 0.5,
				"checkbox stays inside its cell (overflow=" + overflow + "px)");
		assertEquals("true", getEval("(function(){var cm=" + cm + ";cm.scrollIntoView({block:'center'});"
				+ "var r=cm.getBoundingClientRect(),e=document.elementFromPoint(r.left+r.width/2,r.top+r.height/2);"
				+ "return ''+(e===cm||cm.contains(e));}())"),
				"checkbox must actually paint (not clipped by the collapsed cell)");
	}
}
