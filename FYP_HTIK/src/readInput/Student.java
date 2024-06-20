package readInput;
import java.util.HashSet;

public class Student {

	private String studentID;
	private String name;
	private String degree;
	private HashSet<String> courses;

	public Student(String studentID, String name, String degree) {
		super();
		this.studentID = studentID;
		this.name = name;
		this.degree = degree;
		this.courses = new HashSet<String>();
	}

	public Student(String studentID, String name, String degree,
		HashSet<String> courses) {
		super();
		this.studentID = studentID;
		this.name = name;
		this.degree = degree;

		this.courses = courses;
	}

	public String getstudentID() {
		return studentID;
	}

	public void setstudentID(String studentID) {
		this.studentID = studentID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}


	public HashSet<String> getCourses() {
		return courses;
	}

	public void setCourses(HashSet<String> courses) {
		this.courses = courses;
	}

}
