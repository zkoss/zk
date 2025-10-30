package org.zkoss.zktest.test2.B65_ZK_1969.mvvm;

import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

import org.zkoss.zktest.test2.B65_ZK_1969.tutorial.Car;
import org.zkoss.zktest.test2.B65_ZK_1969.tutorial.CarService;
import org.zkoss.zktest.test2.B65_ZK_1969.tutorial.CarServiceImpl;

public class SearchViewModel {
	
	private String keyword;
	private List<Car> carList;
	private Car selectedCar;
	
	private CarService carService = new CarServiceImpl();
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getKeyword() {
		return keyword;
	}

	public List<Car> getCarList(){
		return carList;
	}
	
		
	public void setSelectedCar(Car selectedCar) {
		this.selectedCar = selectedCar;
	}
	public Car getSelectedCar() {
		return selectedCar;
	}

	
	@Command
	@NotifyChange("carList")
	public void search(){
		carList = carService.search(keyword);
	}
}
