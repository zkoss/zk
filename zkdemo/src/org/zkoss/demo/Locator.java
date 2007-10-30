package org.zkoss.demo;

public class Locator {
	private static double _lat = 35.65834875778273 ;
	private static double _lng = 139.745721717804;
	private static String _info ="年間約300万人が訪れる人気観光スポットであり、東京のランドマークです";
	private static String _title = "東京タワー";
	
	public double getLat() {
		return _lat;
	}
	public void setLat(double lat) {
		_lat = lat;
	}
	public double getLng() {
		return _lng;
	}
	public void setLng(double lng) {
		_lng = lng;
	}
	public String getTitle() {
		return _title;
	}
	public void setTitle(String title) {
		_title = title;
	}
	
	public static double[] locate(String location){
		System.out.println("["+_lat+","+_lng+"]");
		return new double[]{_lat,_lng};
	}
	
	public static String getInfo(String location){
		System.out.println(_info );
		return _info;
	}
	
}
