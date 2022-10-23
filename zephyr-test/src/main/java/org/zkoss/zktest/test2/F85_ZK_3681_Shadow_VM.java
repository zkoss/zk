package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Init;

public class F85_ZK_3681_Shadow_VM {
	private List<F85_ZK_3681_Issue> issues = new ArrayList<F85_ZK_3681_Issue>();

	public List<F85_ZK_3681_Issue> getIssues() {
		return issues;
	}

	@Init
	public void init() {
		issues.add(newIssue(true, "ZK-3300", "Zhtml components support MVVM"));
		issues.add(newIssue(false, "ZK-3521", "Missing Spinner Error message if number out of bound"));
		issues.add(newIssue(true, "ZK-3525", "implement frozen alternative"));
		issues.add(newIssue(false, "ZK-3663", "ZK doesn't render <nodom>'s child components in a zhtml"));
		issues.add(newIssue(false, "ZK-3679", "a custom checked exception is wrapped as OperationException"));
	}

	private F85_ZK_3681_Issue newIssue(boolean isDone, String id, String desc) {
		return new F85_ZK_3681_Issue(isDone, id, desc);
	}
}
