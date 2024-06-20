package GA;

public class Day {

	protected slot []slots;
	
	public Day(){
		this.slots=null;
	}
	public Day(int slotCount){
		this.slots=new slot[slotCount];
	}
	
	public slot[] getSlots(){
		return this.slots;
	}
	
}
