/**
 * 
 */
package org.zkoss.zk.ui.select.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.select.impl.Selector.Combinator;

/**
 * An implementation of Iterator&lt;Component> that realizes the selector matching
 * algorithm. The iteration is lazily evaluated. i.e. The iterator will not
 * perform extra computation until .next() is called.
 * @author simonpai
 */
public class ComponentIterator implements Iterator<Component> {
	
	private Page _page;
	private Component _root;
	private List<Selector> _selectorList;
	private Map<String, PseudoClassDef> _localDefs;
	
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
		if((page == null && root == null) || selector == null || selector.isEmpty()) 
			throw new IllegalArgumentException();
		
		_localDefs = new HashMap<String, PseudoClassDef>();
		_selectorList = new Parser().parse(selector);
		_root = root;
		_page = page;
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
		
		while(_currCtx != null && !_currCtx.isMatched()) _currCtx = buildNextCtx();
		if(_currCtx != null) {
			_index++;
			return _currCtx.getComponent();
		}
		return null; 
	}
	
	private ComponentMatchCtx buildRootCtx(){
		Component rt = _root == null? _page.getFirstRoot(): _root;
		ComponentMatchCtx ctx = new ComponentMatchCtx(rt, _selectorList);
		matchLevel0(_selectorList, ctx);
		return ctx;
	}
	
	private ComponentMatchCtx buildNextCtx(){
		
		if(_currCtx.getComponent().getFirstChild() != null) 
			return buildFirstChildCtx(_currCtx);
		
		while(_currCtx.getComponent().getNextSibling() == null) {
			_currCtx = _currCtx.getParent();
			if(_currCtx == null || _currCtx.getComponent() == _root) 
				return null; // reached root
		}
		
		return buildNextSiblingCtx(_currCtx);
	}
	
	private ComponentMatchCtx buildFirstChildCtx(ComponentMatchCtx parent){
		
		ComponentMatchCtx ctx = new ComponentMatchCtx(
				parent.getComponent().getFirstChild(), parent);
		matchLevel0(_selectorList, ctx);
		
		for(Selector selector : _selectorList) {
			int i = selector.getSelectorIndex();
			
			for(int j=0; j < selector.size()-1; j++){
				switch(selector.getCombinator(j)){
				case DESCENDANT:
					if(parent.isQualified(i, j)) ctx.setQualified(i, j);
					// no break
				case CHILD:
					if(parent.isQualified(i, j) && match(selector, ctx, j+1)) 
						ctx.setQualified(i, j+1);
					break;
				}
			}
		}
		return ctx;
	}
	
	private ComponentMatchCtx buildNextSiblingCtx(ComponentMatchCtx ctx){
		
		ctx.moveToNextSibling();
		
		for(Selector selector : _selectorList) {
			int i = selector.getSelectorIndex();
			ctx.setQualified(i, selector.size()-1, 
					match(selector, ctx, selector.size()-1));
			
			for(int j = selector.size() - 2; j > -1; j--){
				Combinator cb = selector.getCombinator(j);
				ComponentMatchCtx parent = ctx.getParent();
				
				switch(cb){
				case DESCENDANT:
				case CHILD:
					if(parent != null && parent.isQualified(i, j) && 
							match(selector, ctx, j+1))
						ctx.setQualified(i, j+1);
					break;
				case GENERAL_SIBLING:
					if(ctx.isQualified(i, j)) 
						ctx.setQualified(i, j+1, 
								match(selector, ctx, j+1));
					break;
				case ADJACENT_SIBLING:
					ctx.setQualified(i, j+1, ctx.isQualified(i, j) && 
							match(selector, ctx, j+1));
					ctx.setQualified(i, j, false);
				}
			}
		}
		
		matchLevel0(_selectorList, ctx);
		return ctx;
	}
	
	private void matchLevel0(List<Selector> list, ComponentMatchCtx ctx) {
		for(Selector selector : list)
			if(match(selector, ctx, 0)) 
				ctx.setQualified(selector.getSelectorIndex(), 0);
	}
	
	private boolean match(Selector selector, ComponentMatchCtx ctx, int index) {
		return ctx.match(selector.get(index), _localDefs);
	}
	
	// TODO: remove after testing
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ComponentIterator: \n* index: ").append(_index);
		for(ComponentMatchCtx c = _currCtx; c != null; c = c.getParent())
			sb.append("\n").append(c);
		return sb.append("\n\n").toString();
	}
	
}
