package GA;

import java.util.ArrayList;
import java.util.List;

import readInput.Invigilator;

public class CSchedule {
	int slot;
	int day;
	String courseID;
	String venue; // Variable to hold the venue for the course
    ArrayList<String> allocatedVenues; // Variable to hold the list of allocated venues
    String allocatedVenuesString;
    ArrayList<Invigilator> allocatedInvigilators;
    ArrayList<String> venueDetailsWithCommas;

	
	public CSchedule(String courseID, int slot, int day){
		this.courseID = courseID;
		this.slot = slot;
		this.day = day;
		this.venue = null;
		this.allocatedVenues = new ArrayList<>();
		this.allocatedInvigilators = new ArrayList<>();
		this.venueDetailsWithCommas = new ArrayList<>();
	}
	
	public CSchedule(CSchedule that){
		this.slot = that.slot;
		this.day = that.day;
		this.courseID = that.courseID;
	}
	
    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
    
	public int getSlot(){
		return this.slot;
	}
	
	public ArrayList<String> getAllocatedVenues() {
        return allocatedVenues;
    }
	
	public int getDay(){
		return this.day;
	}
	
	public String getCourseId(){
		return this.courseID;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}

	public void setAllocatedVenues(ArrayList<String> allocatedVenues) {
		this.allocatedVenues = allocatedVenues;
	}

	public void setAllocatedVenuesString(String allocatedVenuesString) {
		this.allocatedVenuesString = allocatedVenuesString;
	}
	
	public ArrayList<Invigilator> getAllocatedInvigilators() {
        return allocatedInvigilators;
    }

    public void setAllocatedInvigilators(ArrayList<Invigilator> allocatedInvigilators) {
        this.allocatedInvigilators = allocatedInvigilators;
    }

    // Getter and setter for venueDetailsWithCommas
    public ArrayList<String> getVenueDetailsWithCommas() {
        return venueDetailsWithCommas;
    }

    public void setVenueDetailsWithCommas(ArrayList<String> venueDetailsWithCommas) {
        this.venueDetailsWithCommas = venueDetailsWithCommas;
    }
	
}
