import org.zkoss.zk.ui.Component;
public class SenderComparator implements Comparator {
	private boolean _asc;

	public SenderComparator(boolean asc) {
		_asc = asc;
	}

	public int compare(Object o1, Object o2) {
		String s1 = getValue(o1) , s2 = getValue(o2);
		int v = s1.compareTo(s2);
		return _asc ? v : -v;
	}

	private String getValue(Object o) {
		return ((Mail) o).getSender().toUpperCase();
	}
}
	

public class EmailComparator implements Comparator {
	private boolean _asc;

	public EmailComparator(boolean asc) {
		_asc = asc;
	}

	public int compare(Object o1, Object o2) {
		String s1 = getValue(o1) , s2 = getValue(o2);
		int v = s1.compareTo(s2);
		return _asc ? v : -v;
	}

	private String getValue(Object o) {
		return ((Mail) o).getEmail().toUpperCase();
	}
}


public class SubjectComparator implements Comparator {
	private boolean _asc;

	public SubjectComparator(boolean asc) {
		_asc = asc;
	}

	public int compare(Object o1, Object o2) {
		String s1 = getValue(o1) , s2 = getValue(o2);
		int v = s1.compareTo(s2);
		return _asc ? v : -v;
	}

	private String getValue(Object o) {
		return ((Mail) o).getSubject().toUpperCase();
	}
}