package readInput;

public class Invigilator {

	private String Tid;
	private String name;
	
	public Invigilator(String Tid, String name){
		this.Tid = Tid;
		this.name = name;
	}
	
	public String getId(){
		return Tid;
	}
	
	public String getName(){
		return name;
	}
	
	public String toString(){
		return name;
	}
}
