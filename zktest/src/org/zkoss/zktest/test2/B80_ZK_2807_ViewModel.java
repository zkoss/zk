/* B80_ZK_2807_ViewModel.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 28 11:09:33 CST 2015, Created by Christopher

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.util.function.Consumer;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

/**
 * 
 * @author Christopher
 */
public class B80_ZK_2807_ViewModel {
private ListModelList<Car> cars;
	
	@Init
	public void init() {
		cars = new ListModelList<Car>();
		
		cars.add(new Car("truck", "green"));
		cars.add(new Car("racer", "red"));
		cars.add(new Car("jeep", "yellow"));
	}
	
	@Command
	public void changeAllRed() {
		cars.forEach(new Consumer<Car>() {
			public void accept(Car car) {
				car.setColor("red");
			}
		});
		BindUtils.postNotifyChange(null, null, this, "isRed"); //notifying the method only does not work in ZK 8
//		BindUtils.postNotifyChange(null, null, this, "isRed(each.color)"); //only this exact syntax works in ZK 8
	}
	
	@Command
	public void changeAllRedWorkAround() {
		cars.forEach(new Consumer<Car>() {
			public void accept(Car car) {
				car.setColor("red");
				BindUtils.postNotifyChange(null,  null, car, "color");
			}
		});
	}
	
	@Command
	public void changeAllRedWorkAround2() {
		cars.forEach(new Consumer<Car>() {
			public void accept(Car car) {
				car.setColor("red");
			}
		});
//		BindUtils.postNotifyChange(null, null, this, "isRed"); //notifying the method only does not work in ZK 8
		BindUtils.postNotifyChange(null, null, this, "isRed(each.color)"); //only this exact syntax works in ZK 8
	}
	
	public boolean isRed(String color) {
		return color.contains("red");
	}
	
	public ListModelList<Car> getCars() {
		return cars;
	}

	public class Car{
		private String name;
		private String color;

		public Car(String name, String color) {
			super();
			this.name = name;
			this.color = color;
		}

		public String getName() {
			return name;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public String getColor() {
			return color;
		}
	}
}
