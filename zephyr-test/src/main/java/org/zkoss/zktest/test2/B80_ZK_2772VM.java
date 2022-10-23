/* B80_ZK_2772VM.java

	Purpose:
		
	Description:
		
	History:
		1:41 PM 9/22/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

/**
 * @author jumperchen
 */
public class B80_ZK_2772VM
{
	private ListModelList<Customer> customers;

	@Init
	public void init()
	{
		this.customers = new ListModelList<Customer>();
		for(int i = 0; i < 20; i++)
			this.customers.add(new Customer("Name " + i, "Name " + i, "Name " + i, "Name " + i, "Name " + i, "Name " + i, "Long long long long long long long long long long long long long name " + i, "Name " + i, "Name " + i, "Name " + i));
	}

	public ListModelList<Customer> getCustomers()
	{
		return customers;
	}

	public static class Customer
	{
		private String name0;

		private String name1;

		private String name2;

		private String name3;

		private String name4;

		private String name5;

		private String name6;

		private String name7;

		private String name8;

		private String name9;

		public Customer() {}

		public Customer(String name0, String name1, String name2, String name3, String name4, String name5, String name6, String name7, String name8, String name9)
		{
			this.name0 = name0;
			this.name1 = name1;
			this.name2 = name2;
			this.name3 = name3;
			this.name4 = name4;
			this.name5 = name5;
			this.name6 = name6;
			this.name7 = name7;
			this.name8 = name8;
			this.name9 = name9;
		}

		public String getName0()
		{
			return name0;
		}

		public void setName0(String name0)
		{
			this.name0 = name0;
		}

		public String getName1()
		{
			return name1;
		}

		public void setName1(String name1)
		{
			this.name1 = name1;
		}

		public String getName2()
		{
			return name2;
		}

		public void setName2(String name2)
		{
			this.name2 = name2;
		}

		public String getName3()
		{
			return name3;
		}

		public void setName3(String name3)
		{
			this.name3 = name3;
		}

		public String getName4()
		{
			return name4;
		}

		public void setName4(String name4)
		{
			this.name4 = name4;
		}

		public String getName5()
		{
			return name5;
		}

		public void setName5(String name5)
		{
			this.name5 = name5;
		}

		public String getName6()
		{
			return name6;
		}

		public void setName6(String name6)
		{
			this.name6 = name6;
		}

		public String getName7()
		{
			return name7;
		}

		public void setName7(String name7)
		{
			this.name7 = name7;
		}

		public String getName8()
		{
			return name8;
		}

		public void setName8(String name8)
		{
			this.name8 = name8;
		}

		public String getName9()
		{
			return name9;
		}

		public void setName9(String name9)
		{
			this.name9 = name9;
		}

	}
}