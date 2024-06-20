package readInput;
import java.util.HashSet;

public class course {
	String courseID;
	String course_Name;
	HashSet<String> studentID; //all students who are registered in this course. (irrespective of their sections)
	boolean toBeScheduled;
	
	course(String courseID, String course_Name, boolean toBeScheduled){
		this.courseID = courseID;
		this.course_Name = course_Name;
		this.toBeScheduled = toBeScheduled;
		this.studentID = new HashSet<String>();
	}

	
	course(String courseID, String course_Name, HashSet<String> studentID){
		this.courseID = courseID;
		this.course_Name = course_Name;
		this.studentID = studentID;
	}


	public String getCourseID() {
		return courseID;
	}


	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}


	public String getCourseName() {
		return course_Name;
	}


	public void setCourseName(String course_Name) {
		this.course_Name = course_Name;
	}

	
	public HashSet<String> getStudentID() {
		return studentID;
	}

	public boolean getToBeScheduled()
	{
		return this.toBeScheduled;
	}
	
	public void setToBeScheduled(boolean toBeScheduled) {
		this.toBeScheduled=toBeScheduled;
	}
	
	public void setStudentRollNumbers(HashSet<String> studentID) {
		this.studentID = studentID;
	}

}
