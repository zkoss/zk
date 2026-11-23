package org.zkoss.zktest.test2;

import java.io.Serializable;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class F85_ZK_3681_Issue implements Serializable {
	private boolean isDone;
	private String id;
	private String description;

	public F85_ZK_3681_Issue() {
		this(false, "", "");
	}

	public F85_ZK_3681_Issue(boolean isDone, String id, String description) {
		this.isDone = isDone;
		this.id = id;
		this.description = description;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean done) {
		isDone = done;
	}

	@Pattern(regexp = "^[A-Z]+-\\d+$", message = "ID is invalid.")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Size(min = 3, message = "Description is too short (minimum is 3 characters)")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
