package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.zkoss.lang.Strings;

public class B01795NestedTemplate {
	ArrayList<Profile> _data;
	public B01795NestedTemplate() {
		_data = provideData();
	}
	
	public List<String> getColumns() {
		List list = new ArrayList();
		list.add("skills");
		
		return list;
	}
	
	public List getProfileSkillsX(String column,Profile profile) {
//		System.out.println("XProfile: " + profile);
//		System.out.println("Xcolumn: " + column);
		if (Strings.isEmpty(column)) {
			throw new NullPointerException("column should not be empty");
		}
		if (profile == null) {
			throw new NullPointerException("profile should not be empty");
		}
		return profile.getSkills();
	}
	public List getProfileSkills(Profile profile,String column) {
//		System.out.println("Profile: " + profile);
//		System.out.println("column: " + column);
		if (Strings.isEmpty(column)) {
			throw new NullPointerException("column should not be empty");
		}
		if (profile == null) {
			throw new NullPointerException("profile should not be empty");
		}
		return profile.getSkills();
	}
	
	public ArrayList<Profile> getProfiles() {
		return _data;
	}
	
	public Skill[] getAllSkills() {
		return Skill.values();
	}
	
	private ArrayList<Profile> provideData() {
		ArrayList<Profile> data = new ArrayList<Profile>();
		
		data.add(new Profile("John", 1980, 1, 20, true, new Skill[]{Skill.AJAX}));
		data.add(new Profile("Mary", 1982, 2, 11, true, new Skill[]{Skill.Java,Skill.C}));
		
		return data;
	}
	
	
	static public class Profile {
		
		String name;
		Date birth;
		boolean married;
		List<Skill> skills;
		
		public Profile(String name, int birthYear, int birthMonth, int birthDayOfMonth, boolean married,
				Skill[] skills) {
			this.name = name;
			this.birth = new GregorianCalendar(birthYear, birthMonth, birthDayOfMonth).getTime();
			this.married = married;
			ArrayList<Skill> list = new ArrayList<Skill>();
			for (Skill e : skills) {
				list.add(e);
			}
			this.skills = list;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Date getBirth() {
			return birth;
		}
		
		public void setBirth(Date birth) {
			this.birth = birth;
		}
		
		public boolean isMarried() {
			return married;
		}
		
		public void setMarried(boolean married) {
			this.married = married;
		}
		
		public List<Skill> getSkills() {
			return skills;
		}
		
		public void setSkill(List<Skill> skills) {
			this.skills = skills;
		}
	}
	static public enum Skill {
		AJAX,
		JavaScript,
		Dart,
		Java,
		C,
		Erlang,
		Lisp,
		Haskell,
		Python,
		Ruby,
		Node,
		HTML,
		CSS
	}
}
