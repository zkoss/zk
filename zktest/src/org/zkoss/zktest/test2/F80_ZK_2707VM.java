package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import java.util.Calendar;
import java.util.Date;


public class F80_ZK_2707VM {
	
	private Date time;
	private Date min;
	private Date max;
	private int interval;
	
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Date getMin() {
		return min;
	}

	public void setMin(Date min) {
		this.min = min;
	}

	public Date getMax() {
		return max;
	}

	public void setMax(Date max) {
		this.max = max;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	@Init
	public void init() {
		this.time = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(0, 0, 0, 10, 10);
		this.min = calendar.getTime();
		calendar.set(0, 0, 0, 12, 10);
		this.max = calendar.getTime();
	}
	
	@Command
	@NotifyChange("min")
	public void change_min() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.min);
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		this.min = calendar.getTime();
	}
	
	@Command
	@NotifyChange("max")
	public void change_max() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.max);
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		this.max = calendar.getTime();
	}
	
	@Command
	@NotifyChange("interval")
	public void change_interval() {
		this.interval += 60;
	}
}