/* Game.java

	Purpose:
		
	Description:
		
	History:
		Jul 12, 2012, Created by Ian Tsai(Zanyking)

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under ZOL in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zktest.test2.cdi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Named;

import org.zkoss.zkplus.cdi.CDIUtil;

/**
 * @author Ian Y.T Tsai(zanyking)
 * 
 */
@Named
@SessionScoped
public class Game {

	private int number;
	private int smallest;
	private int biggest;
	private int remainingGuesses;
	private int guess;

	public Game(@MaxNumber int maxNumber) {
		this.biggest = maxNumber;
	}

	public int getBiggest() {
		return biggest;
	}

	public int getGuess() {
		return guess;
	}

	public void setGuess(int guess){
		this.guess = guess;
	}

	public int getSmallest(){
		return smallest;
	}
	
	public int getRemainingGuesses() {
		return remainingGuesses;
	}

	public String check() {
		if (guess > number) {
			biggest = guess - 1;
		}
		if (guess < number) {
			smallest = guess + 1;
		}
		if (guess == number) {
			//TODO: send "correct"
			return "success!";
		}
		remainingGuesses--;
		return null;
	}

	public void reset(){
		this.smallest = 0;
		this.guess = 0;
		this.remainingGuesses = 10;
		this.number = 1345;// get from producer method, see how to do this in CDI...

	}

}
