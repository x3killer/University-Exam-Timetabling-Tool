package GA;
import java.util.HashSet;

public class slot{
	
	HashSet<String> coursesScheduled;

	public slot(){
		this.coursesScheduled=new HashSet<String>();
	}
	
	public HashSet<String> getCoursesScheduled(){
		return this.coursesScheduled;
		
	}
	
}
