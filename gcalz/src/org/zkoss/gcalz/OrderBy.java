package org.zkoss.gcalz;

public class OrderBy 
{
	private static final String lastmodifiedStr = "lastmodified";
	private static final String starttimeStr = "starttime";
    public static final OrderBy lastmodified = new OrderBy(lastmodifiedStr);
    public static final OrderBy starttime = new OrderBy(starttimeStr);
    
	private String key;

	private OrderBy(String lkey) 
	{
		key = lkey;
	}
	
	public String toString()
	{
		return key;
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OrderBy other = (OrderBy) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}



}//end of class...