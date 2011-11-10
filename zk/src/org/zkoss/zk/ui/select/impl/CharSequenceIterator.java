/**
 * 
 */
package org.zkoss.zk.ui.select.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A char Iterator implementation for Selector tokenizer.
 * @since 6.0.0
 * @author simonpai
 */
public class CharSequenceIterator implements Iterator<Character>{
	
	private CharSequence _sequence;
	private int _begin;
	private int _end;
	
	private int _curr;
	private char _next;
	private boolean _ready;
	
	public CharSequenceIterator(CharSequence sequence){
		this(sequence, 0);
	}
	
	public CharSequenceIterator(CharSequence sequence, int begin){
		this(sequence, begin, sequence.length());
	}
	
	public CharSequenceIterator(CharSequence sequence, int begin, int end){
		_sequence = sequence;
		if(begin < 0) throw new IllegalArgumentException(
				"Beginning index cannot be less than 0.");
		_begin = begin;
		if(end > _sequence.length()) throw new IllegalArgumentException(
				"End index cannot be greater than sequence length.");
		_end = end;
		
		reset();
	}
	
	public boolean hasNext(){
		return _curr < _end;
	}
	
	public Character next(){
		return nextChar();
	}
	
	public char nextChar(){
		if(!hasNext()) throw new NoSuchElementException();
		seekNext();
		_curr++;
		_ready = false;
		return _next;
	}
	
	public int getIndex(){
		return _curr;
	}
	
	public void skip(){
		if(_curr >= _end) return; 
		_curr++;
		_ready = false;
	}
	
	public void skip(int times){
		if(_curr >= _end) return; 
		for(int i=0; (_curr < _end) && (i < times); i++) _curr++;
		_ready = false;
	}
	
	public char peek(){
		if(!hasNext()) throw new NoSuchElementException();
		seekNext();
		return _next;
	}
	
	public char peek(int offset){
		if(_curr + offset >= _end) throw new NoSuchElementException();
		return _sequence.charAt(_curr + offset);
	}
	
	public void reset(){
		_curr = _begin;
		_ready = false;
	}
	
	// helper //
	private void seekNext(){
		if(_ready) return;
		_next = _sequence.charAt(_curr);
		_ready = true;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}
