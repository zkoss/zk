/**
 * 
 */
package org.zkoss.zk.ui.select.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.select.impl.Selector.Combinator;

/**
 * An implementation of Iterator&lt;Component> that realizes the selector matching
 * algorithm. The iteration is lazily evaluated. i.e. The iterator will not
 * perform extra computation until .next() is called.
 * @since 6.0.0
 * @author simonpai
 */
public class ComponentIterator implements Iterator<Component> {
	
	private final Page _page;
	private final Component _root;
	private final List<Selector> _selectorList;
	private final int _posOffset;
	private final boolean _allIds;
	private final Map<String, PseudoClassDef> _localDefs = 
		new HashMap<String, PseudoClassDef>();
	
	private Component _offsetRoot;
	private ComponentMatchCtx _currCtx;
	
	/**
	 * Create an iterator which selects from all the components in the page.
	 * @param page the reference page for selector
	 * @param selector the selector string
	 */
	public ComponentIterator(Page page, String selector){
		this(page, null, selector);
	}
	
	/**
	 * Create an iterator which selects from all the descendants of a given
	 * component, including itself.
	 * @param root the reference component for selector
	 * @param selector the selector string
	 */
	public ComponentIterator(Component root, String selector){
		this(root.getPage(), root, selector);
	}
	
	private ComponentIterator(Page page, Component root, String selector){
		if(page == null && root == null) 
			throw new IllegalArgumentException("Page or root component cannot be null.");
		if(selector == null || selector.isEmpty()) 
			throw new IllegalArgumentException("Selector string cannot be empty.");
		
		_selectorList = new Parser().parse(selector);
		if (_selectorList.isEmpty())
			throw new IllegalStateException("Empty selector");
		
		_posOffset = getCommonSeqLength(_selectorList);
		_allIds = isAllIds(_selectorList, _posOffset);
		
		_root = root;
		_page = page;
	}
	
	private static int getCommonSeqLength(List<Selector> list) {
		List<String> strs = null;
		int max = 0;
		for (Selector selector : list) {
			if (strs == null) {
				strs = new ArrayList<String>();
				for (SimpleSelectorSequence seq : selector)
					if (!Strings.isEmpty(seq.getId())) {
						strs.add(seq.toString());
						strs.add(seq.getCombinator().toString());
					} else
						break;
				max = strs.size();
			} else {
				int i = 0;
				for (SimpleSelectorSequence seq : selector)
					if (i >= max || Strings.isEmpty(seq.getId()) || 
							!strs.get(i++).equals(seq.toString()) || 
							!strs.get(i++).equals(seq.getCombinator().toString()))
						break;
				if (i-- < max)
					max = i;
			}
		}
		return (max + 1) / 2;
	}
	
	private static boolean isAllIds(List<Selector> list, int offset) {
		for (Selector s : list)
			if (s.size() > offset)
				return false;
		return true;
	}
	
	// custom pseudo class definition //
	/**
	 * Add or set pseudo class definition.
	 * @param name the pseudo class name
	 * @param def the pseudo class definition
	 */
	public void setPseudoClassDef(String name, PseudoClassDef def){
		_localDefs.put(name, def);
	}
	
	/**
	 * Remove a pseudo class definition.
	 * @param name the pseudo class name
	 * @return the original definition
	 */
	public PseudoClassDef removePseudoClassDef(String name){
		return _localDefs.remove(name);
	}
	
	/**
	 * Clear all custom pseudo class definitions.
	 */
	public void clearPseudoClassDefs(){
		_localDefs.clear();
	}
	
	
	
	// iterator //
	private boolean _ready = false;
	private Component _next;
	private int _index = -1;
	
	/**
	 * Return true if it has next component.
	 */
	public boolean hasNext() {
		loadNext();
		return _next != null;
	}
	
	/**
	 * Return the next matched component. A NoSuchElementException will be 
	 * throw if next component is not available.
	 */
	public Component next() {
		if(!hasNext()) throw new NoSuchElementException();
		_ready = false;
		return _next;
	}
	
	/**
	 * Throws UnsupportedOperationException.
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Return the next matched component, but the iteration is not proceeded.
	 */
	public Component peek() {
		if(!hasNext()) throw new NoSuchElementException();
		return _next;
	}
	
	/**
	 * Return the index of the next component.
	 */
	public int nextIndex() {
		return _ready ? _index : _index+1;
	}
	
	
	
	// helper //
	private void loadNext(){
		if(_ready) return;
		_next = seekNext();
		_ready = true;
	}
	
	private Component seekNext() {
		_currCtx = _index < 0 ? buildRootCtx() : buildNextCtx();
		
		while (_currCtx != null && !_currCtx.isMatched()) 
			_currCtx = buildNextCtx();
		if (_currCtx != null) {
			_index++;
			return _currCtx.getComponent();
		}
		return null;
	}
	
	private ComponentMatchCtx buildRootCtx() {
		Component rt = _root == null ? _page.getFirstRoot() : _root;
		
		if (_posOffset > 0) {
			Selector selector = _selectorList.get(0);
			for (int i = 0; i < _posOffset; i++) {
				SimpleSelectorSequence seq = selector.get(i);
				Component rt2 = rt.getFellowIfAny(seq.getId());
				
				if (rt2 == null)
					return null;
				
				// match local properties
				if (!ComponentLocalProperties.matchType(rt2, seq.getType()) || 
						!ComponentLocalProperties.matchClasses(
								rt2, seq.getClasses()) ||
						!ComponentLocalProperties.matchAttributes(
								rt2, seq.getAttributes()) ||
						!ComponentLocalProperties.matchPseudoClasses(
								rt2, seq.getPseudoClasses(), _localDefs))
					return null;
				
				// check combinator for second and later jumps
				if (i > 0) {
					switch (selector.getCombinator(i - 1)) {
					case DESCENDANT:
						if (!isDescendant(rt2, rt))
							return null;
						break;
					case CHILD:
						if (rt2.getParent() != rt)
							return null;
						break;
					case GENERAL_SIBLING:
						if (!isGeneralSibling(rt2, rt))
							return null;
						break;
					case ADJACENT_SIBLING:
						if (rt2.getPreviousSibling() != rt)
							return null;
						break;
					}
				}
				rt = rt2;
			}
			_offsetRoot = rt.getParent();
		}
		
		ComponentMatchCtx ctx = new ComponentMatchCtx(rt, _selectorList);
		
		if (_posOffset > 0)
			for (Selector selector : _selectorList)
				ctx.setQualified(selector.getSelectorIndex(), _posOffset - 1);
		else
			matchLevel0(ctx);
		
		System.out.println(ctx); // TODO: debugger
		return ctx;
	}
	
	private ComponentMatchCtx buildNextCtx() {
		
		if (_allIds)
			return null;
		
		// TODO: how to skip tree branches
		
		if (_currCtx.getComponent().getFirstChild() != null) 
			return buildFirstChildCtx(_currCtx);
		
		while (_currCtx.getComponent().getNextSibling() == null) {
			_currCtx = _currCtx.getParent();
			if(_currCtx == null || _currCtx.getComponent() == 
					(_posOffset > 0 ? _offsetRoot : _root))
				return null; // reached root
		}
		
		return buildNextSiblingCtx(_currCtx);
	}
	
	private ComponentMatchCtx buildFirstChildCtx(ComponentMatchCtx parent) {
		
		ComponentMatchCtx ctx = new ComponentMatchCtx(
				parent.getComponent().getFirstChild(), parent);
		
		if (_posOffset == 0)
			matchLevel0(ctx);
		
		for (Selector selector : _selectorList) {
			int i = selector.getSelectorIndex();
			int posStart = _posOffset > 0 ? _posOffset - 1 : 0;
			
			for (int j = posStart; j < selector.size() - 1; j++) {
				switch (selector.getCombinator(j)) {
				case DESCENDANT:
					if (parent.isQualified(i, j) && checkIdSpace(selector, j+1, ctx))
						ctx.setQualified(i, j);
					// no break
				case CHILD:
					if (parent.isQualified(i, j) && match(selector, ctx, j+1)) 
						ctx.setQualified(i, j+1);
					break;
				}
			}
		}
		System.out.println(ctx); // TODO: debugger
		return ctx;
	}
	
	private ComponentMatchCtx buildNextSiblingCtx(ComponentMatchCtx ctx) {
		
		ctx.moveToNextSibling();
		
		for (Selector selector : _selectorList) {
			int i = selector.getSelectorIndex();
			int posEnd = _posOffset > 0 ? _posOffset - 1 : 0;
			int len = selector.size();
			
			// clear last position, may be overridden later
			ctx.setQualified(i, len - 1, false);
			
			for (int j = len - 2; j >= posEnd; j--) {
				Combinator cb = selector.getCombinator(j);
				ComponentMatchCtx parent = ctx.getParent();
				
				switch (cb) {
				case DESCENDANT:
					boolean parentPass = parent != null && parent.isQualified(i, j);
					ctx.setQualified(i, j, 
							parentPass && checkIdSpace(selector, j+1, ctx));
					if (parentPass && match(selector, ctx, j+1))
						ctx.setQualified(i, j+1);
					break;
				case CHILD:
					ctx.setQualified(i, j+1, parent != null && 
							parent.isQualified(i, j) && match(selector, ctx, j+1));
					break;
				case GENERAL_SIBLING:
					if (ctx.isQualified(i, j)) 
						ctx.setQualified(i, j+1, match(selector, ctx, j+1));
					break;
				case ADJACENT_SIBLING:
					ctx.setQualified(i, j+1, ctx.isQualified(i, j) && 
							match(selector, ctx, j+1));
					ctx.setQualified(i, j, false);
				}
			}
		}
		
		if (_posOffset == 0)
			matchLevel0(ctx);
		
		System.out.println(ctx); // TODO: debugger
		return ctx;
	}
	
	private static boolean checkIdSpace(Selector selector, int index, 
			ComponentMatchCtx ctx) {
		return !selector.requiresIdSpace(index) || 
			!(ctx.getComponent() instanceof IdSpace);
	}
	
	private static boolean isDescendant(Component c1, Component c2) {
		if (c1 == c2)
			return true; // first c1 can be IdSpace
		while ((c1 = c1.getParent()) != null) {
			if (c1 == c2)
				return true;
			if (c1 instanceof IdSpace)
				return c1 == c2;
		}
		return false;
	}
	
	private static boolean isGeneralSibling(Component c1, Component c2) {
		while (c1 != null) {
			if (c1 == c2)
				return true;
			c1 = c1.getPreviousSibling();
		}
		return false;
	}
	
	private void matchLevel0(ComponentMatchCtx ctx) {
		for (Selector selector : _selectorList)
			if (match(selector, ctx, 0))
				ctx.setQualified(selector.getSelectorIndex(), 0);
	}
	
	private boolean match(Selector selector, ComponentMatchCtx ctx, int index) {
		return ctx.match(selector.get(index), _localDefs);
	}
	
}
