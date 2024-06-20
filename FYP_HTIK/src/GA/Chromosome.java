package GA;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import readInput.*;

public class Chromosome {

	private Day[] days;
	private HashMap<String, CSchedule> courseSchedule; 
	private HashMap<String, Venue> venueSchedule;
	private HashMap<String, Invigilator> invigilatorSchedule;
	private int fitness;
	private int unfitness;

	public String toString(ArrayList<String> coursesToBeScheduled){
		StringBuilder string=new StringBuilder();
		for (int i=0; i<coursesToBeScheduled.size(); i++)
		{
			CSchedule current=courseSchedule.get(coursesToBeScheduled.get(i));
			string.append(String.valueOf(current.getDay())+ String.valueOf(current.getSlot()));
		}
		return string.toString();
	}
	
	//cross over constructor
	public Chromosome(Chromosome p1, Chromosome p2, Random random, ErrorHandling problemData){
		this.fitness = 0;
		this.unfitness = 0;
		this.days = null;
		this.courseSchedule = null;
		this.venueSchedule = problemData.getVenue();
		this.invigilatorSchedule = problemData.getInvigilators();
		
		int totalDays = problemData.getTotalDays();
		int totalSlots = problemData.getTotalSlotsPerDay();
		int totalCoursesToSchedule = problemData.getCoursesToBeScheduled().size();

		this.days = new Day[totalDays];
		for (int i = 0; i < totalDays; i++){
			this.days[i]=new Day();
			this.days[i].slots = new slot[totalSlots];
			for (int j=0; j<totalSlots; j++)
				this.days[i].slots[j]=new slot();
		}

		this.courseSchedule = new HashMap<String, CSchedule>();

		int crossOverPoint = random.nextInt(totalCoursesToSchedule);
		Chromosome firstParent = null;
		Chromosome secondParent = null;

		int randomNo = random.nextInt(2);
		if (randomNo == 0){
			firstParent = p1;
			secondParent = p2;
		}
		else{
			firstParent = p2;
			secondParent = p1;
		}

		ArrayList<String> coursesToBeScheduled = problemData.getCoursesToBeScheduled();

		for (int i = 0; i <= crossOverPoint; i++){
			String courseIDOfNextCourse = coursesToBeScheduled.get(i);
			CSchedule cs = new CSchedule(firstParent.courseSchedule.get(courseIDOfNextCourse));
			assignVenue(cs, random);
			this.courseSchedule.put(courseIDOfNextCourse, cs);

			this.days[cs.day].slots[cs.slot].coursesScheduled.add(courseIDOfNextCourse);

		}
		for (int i = crossOverPoint + 1; i < totalCoursesToSchedule; i++){
		    String courseIDOfNextCourse = coursesToBeScheduled.get(i);
		    CSchedule cs = new CSchedule(secondParent.courseSchedule.get(courseIDOfNextCourse));
		    assignVenue(cs, random);
		    this.courseSchedule.put(courseIDOfNextCourse, cs);

		    this.days[cs.day].slots[cs.slot].coursesScheduled.add(courseIDOfNextCourse);
		}

	}

	//randomly create a schedule
	public Chromosome(Random random, ErrorHandling problemData){
		int totalCourses = problemData.getCoursesToBeScheduled().size();

		this.days = new Day[problemData.getTotalDays()];
		for (int i = 0; i < problemData.getTotalDays(); i++){
			this.days[i]=new Day();
			this.days[i].slots = new slot[problemData.getTotalSlotsPerDay()];
			for (int j=0; j<problemData.getTotalSlotsPerDay(); j++)
				this.days[i].slots[j]=new slot();
		}
		this.courseSchedule = new HashMap<String, CSchedule>();

		for (int i = 0; i < totalCourses; i++){
			String courseID = problemData.getCoursesToBeScheduled().get(i);
			int dayNumber=-1;
			int slotNumber=-1;
			if (problemData.getPreferences().containsKey(courseID)){
				dayNumber=problemData.getPreferences().get(courseID).getDay();
				slotNumber=problemData.getPreferences().get(courseID).getSlot();
			}
		
			else {
				dayNumber = random.nextInt(problemData.getTotalDays());
			    slotNumber = random.nextInt(problemData.getTotalSlotsPerDay());
			}
			this.days[dayNumber].slots[slotNumber].coursesScheduled.add(courseID);

			CSchedule cs = new CSchedule(courseID, slotNumber, dayNumber);
			assignVenue(cs, random);
			this.courseSchedule.put(courseID, cs);
		}
	}
	
	public void mutate(ErrorHandling problemData, Random random, double mRate){
		int mutationProbability=  (int)(100*mRate);
		if (mutationProbability==0)
			return;
		
		int totalCoursesToSchedule= problemData.getCoursesToBeScheduled().size();		
		for (int i=0; i<totalCoursesToSchedule; i++){
			
			if (random.nextInt(100)+1<= mutationProbability){
				String courseID= problemData.getCoursesToBeScheduled().get(i);
				if (problemData.getPreferences().containsKey(courseID))
					continue;
				
				int randomDay=random.nextInt(problemData.getTotalDays());
				int randomSlot=random.nextInt(problemData.getTotalSlotsPerDay());
				
				int previouslyAssignedDay= this.courseSchedule.get(courseID).day;
				int previouslyAssignedSlot= this.courseSchedule.get(courseID).slot;
				this.days[previouslyAssignedDay].slots[previouslyAssignedSlot].coursesScheduled.remove(courseID);
				
				this.courseSchedule.get(courseID).day=randomDay;
				this.courseSchedule.get(courseID).slot=randomSlot;
				this.days[randomDay].slots[randomSlot].coursesScheduled.add(courseID);
				
				// Mutate venue as well
                if (this.venueSchedule != null && !this.venueSchedule.isEmpty()) {
                    CSchedule cs = this.courseSchedule.get(courseID);
                    assignVenue(cs, random);
                }
			}
		}
	}
	
	public void assignVenue(CSchedule cs, Random random) {
	    // Initialize variables
	    ArrayList<Venue> selectedVenues = new ArrayList<>();
	    int totalStudents = ErrorHandling.studentCountMap.get(cs.getCourseId()).getCapacity();

	    // Check if venue schedule is available
	    if (this.venueSchedule != null && !this.venueSchedule.isEmpty()) {
	        // Get list of venues
	        ArrayList<Venue> venueList = new ArrayList<>(this.venueSchedule.values());

	        // Shuffle the list of venues
	        Collections.shuffle(venueList, random);

	        // Initialize a set to keep track of assigned venues for the current time slot
	        Set<String> assignedVenueIDs = new HashSet<>();

	        // Iterate over venues
	        int remainingStudents = totalStudents;
	        for (Venue venue : venueList) {
	            int venueCapacity = venue.getCapacity();

	            // Check if venue capacity is sufficient
	            if (remainingStudents <= venueCapacity) {
	                // Check if the venue slot is available and there is no clash
	                if (isVenueSlotAvailable(venue, cs) && !hasClash(venue, cs) && !assignedVenueIDs.contains(venue.getRNumber())) {
	                    selectedVenues.add(venue);
	                    remainingStudents -= venueCapacity; // Reduce remaining students

	                    // Add the assigned venue ID to the set
	                    assignedVenueIDs.add(venue.getRNumber());

	                    // Calculate the number of invigilators needed for this venue
	                    int invigilatorCount = (int) Math.ceil((double) venueCapacity / 25);
	                    // Select invigilators based on the number needed
	                    ArrayList<Invigilator> selectedInvigilators = selectInvigilators(invigilatorCount, random);
	                    // Assign the list of allocated invigilators to the course schedule
	                    cs.setAllocatedInvigilators(selectedInvigilators);
	                }
	            } else {
	                // Assign venue's full capacity and reduce remaining students accordingly
	                if (isVenueSlotAvailable(venue, cs) && !hasClash(venue, cs) && !assignedVenueIDs.contains(venue.getRNumber())) {
	                    selectedVenues.add(venue);
	                    remainingStudents -= venueCapacity;

	                    // Add the assigned venue ID to the set
	                    assignedVenueIDs.add(venue.getRNumber());

	                    // Calculate the number of invigilators needed for this venue
	                    int invigilatorCount = (int) Math.ceil((double) venueCapacity / 25);
	                    // Select invigilators based on the number needed
	                    ArrayList<Invigilator> selectedInvigilators = selectInvigilators(invigilatorCount, random);
	                    // Assign the list of allocated invigilators to the course schedule
	                    cs.setAllocatedInvigilators(selectedInvigilators);
	                }
	            }

	            // Check if all students are assigned
	            if (remainingStudents <= 0) {
	                break;
	            }
	        }
	    }

	    // Assign venues to CSchedule
	    ArrayList<String> venueIDs = selectedVenues.stream().map(Venue::getRNumber).collect(Collectors.toCollection(ArrayList::new));
	    // Create a new list to store venue details with commas
	    ArrayList<String> venueDetailsWithCommas = new ArrayList<>();
	    for (Venue venue : selectedVenues) {
	        // Concatenate venue details with commas
	        venueDetailsWithCommas.add(venue.getRNumber());
	    }
	    // Set the venue details with commas to CSchedule
	    cs.setVenueDetailsWithCommas(venueDetailsWithCommas);
	}


	// Helper method to check if the venue slot is available
	private boolean isVenueSlotAvailable(Venue venue, CSchedule cs) {
	    // Check if any course is already scheduled in the venue at the same time slot
	    for (CSchedule scheduledCS : courseSchedule.values()) {
	        if (scheduledCS.getVenue() != null && scheduledCS.getVenue().equals(venue.getRNumber()) &&
	            scheduledCS.getDay() == cs.getDay() && scheduledCS.getSlot() == cs.getSlot()) {
	            return false; // Venue slot is not available
	        }
	    }
	    return true; // Venue slot is available
	}

	// Helper method to check if there is a clash with other courses scheduled in the same venue
	private boolean hasClash(Venue venue, CSchedule cs) {
	    // Check if any course is already scheduled in the same venue at the same time slot
	    for (CSchedule scheduledCS : courseSchedule.values()) {
	        if (scheduledCS.getVenue() != null && scheduledCS.getVenue().equals(venue.getRNumber()) &&
	            scheduledCS.getDay() == cs.getDay() && scheduledCS.getSlot() == cs.getSlot()) {
	            return true; // Clash detected
	        }
	    }
	    return false; // No clash
	}

	// Helper method to select invigilators based on the number needed
	private ArrayList<Invigilator> selectInvigilators(int invigilatorCount, Random random) {
	    ArrayList<Invigilator> invigilatorList = new ArrayList<>(this.invigilatorSchedule.values());
	    ArrayList<Invigilator> selectedInvigilators = new ArrayList<>();
	    for (int i = 0; i < invigilatorCount; i++) {
	        Invigilator selectedInvigilator = invigilatorList.get(random.nextInt(invigilatorList.size()));
	        selectedInvigilators.add(selectedInvigilator);
	    }
	    return selectedInvigilators;
	}

	// Helper method to extract building code from venue code
	private String extractBuildingCode(String venueCode) {
	    // Assuming venue code format is "BuildingCodeRoomNumber"
	    String[] parts = venueCode.split("(?<=\\D)(?=\\d)");
	    return parts[0];
	}
	
//	public void assignVenue(CSchedule cs, Random random) {
//	    // Initialize variables
//	    ArrayList<Venue> selectedVenues = new ArrayList<>();
//	    int totalStudents = ErrorHandling.studentCountMap.get(cs.getCourseId()).getCapacity();
//
//	    // Check if venue schedule is available
//	    if (this.venueSchedule != null && !this.venueSchedule.isEmpty()) {
//	        // Get list of venues
//	        ArrayList<Venue> venueList = new ArrayList<>(this.venueSchedule.values());
//
//	        // Shuffle the list of venues
//	        Collections.shuffle(venueList, random);
//
//	        // Extract building code from the first venue
//	        String buildingCode = extractBuildingCode(venueList.get(0).getRNumber());
//
//	        // Filter venues based on building code
//	        venueList = venueList.stream()
//	                .filter(venue -> extractBuildingCode(venue.getRNumber()).equals(buildingCode))
//	                .collect(Collectors.toCollection(ArrayList::new));
//
//	        // Iterate over venues
//	        int remainingStudents = totalStudents;
//	        for (Venue venue : venueList) {
//	            int venueCapacity = venue.getCapacity();
//
//	            // Check if venue capacity is sufficient
//	            if (remainingStudents <= venueCapacity) {
//	                // Check if the venue slot is available
//	                if (isVenueSlotAvailable(venue, cs)) {
//	                    selectedVenues.add(venue);
//	                    remainingStudents -= venueCapacity; // Reduce remaining students
//
//	                    // Calculate the number of invigilators needed for this venue
//	                    int invigilatorCount = (int) Math.ceil((double) venueCapacity / 25);
//	                    // Select invigilators based on the number needed
//	                    ArrayList<Invigilator> selectedInvigilators = selectInvigilators(invigilatorCount, random);
//	                    // Assign the list of allocated invigilators to the course schedule
//	                    cs.setAllocatedInvigilators(selectedInvigilators);
//	                }
//	            } else {
//	                // Assign venue's full capacity and reduce remaining students accordingly
//	                if (isVenueSlotAvailable(venue, cs)) {
//	                    selectedVenues.add(venue);
//	                    remainingStudents -= venueCapacity;
//
//	                    // Calculate the number of invigilators needed for this venue
//	                    int invigilatorCount = (int) Math.ceil((double) venueCapacity / 25);
//	                    // Select invigilators based on the number needed
//	                    ArrayList<Invigilator> selectedInvigilators = selectInvigilators(invigilatorCount, random);
//	                    // Assign the list of allocated invigilators to the course schedule
//	                    cs.setAllocatedInvigilators(selectedInvigilators);
//	                }
//	            }
//
//	            // Check if all students are assigned
//	            if (remainingStudents <= 0) {
//	                break;
//	            }
//	        }
//	    }
//
//	    // Assign venues to CSchedule
//	    ArrayList<String> venueIDs = selectedVenues.stream().map(Venue::getRNumber).collect(Collectors.toCollection(ArrayList::new));
//	    // Create a new list to store venue details with commas
//	    ArrayList<String> venueDetailsWithCommas = new ArrayList<>();
//	    for (Venue venue : selectedVenues) {
//	        // Concatenate venue details with commas
//	        venueDetailsWithCommas.add(venue.getRNumber());
//	    }
//	    // Set the venue details with commas to CSchedule
//	    cs.setVenueDetailsWithCommas(venueDetailsWithCommas);
//	}
//
//	// Helper method to select invigilators based on the number needed
//	private ArrayList<Invigilator> selectInvigilators(int invigilatorCount, Random random) {
//	    ArrayList<Invigilator> invigilatorList = new ArrayList<>(this.invigilatorSchedule.values());
//	    ArrayList<Invigilator> selectedInvigilators = new ArrayList<>();
//	    for (int i = 0; i < invigilatorCount; i++) {
//	        Invigilator selectedInvigilator = invigilatorList.get(random.nextInt(invigilatorList.size()));
//	        selectedInvigilators.add(selectedInvigilator);
//	    }
//	    return selectedInvigilators;
//	}
//
//	// Helper method to extract building code from venue code
//	private String extractBuildingCode(String venueCode) {
//	    // Assuming venue code format is "BuildingCodeRoomNumber"
//	    String[] parts = venueCode.split("(?<=\\D)(?=\\d)");
//	    return parts[0];
//	}
//
//	// Helper method to check if the venue slot is available
//	private boolean isVenueSlotAvailable(Venue venue, CSchedule cs) {
//	    // Check if any course is already scheduled in the venue at the same time slot
//	    for (CSchedule scheduledCS : courseSchedule.values()) {
//	        if (scheduledCS.getVenue() != null && scheduledCS.getVenue().equals(venue.getRNumber()) &&
//	            scheduledCS.getDay() == cs.getDay() && scheduledCS.getSlot() == cs.getSlot()) {
//	            return false; // Venue slot is not available
//	        }
//	    }
//	    return true; // Venue slot is available
//	}

	public Day[] getDayWiseSchedule(){
		return this.days;
	}

	public HashMap<String, CSchedule> getCourseWiseSchedule(){
		return this.courseSchedule;
	}

	public final void setFitnessValue(int fitness){
		this.fitness = fitness;
	}

	public void setUnfitnessValue(int unfitness){
		this.unfitness = unfitness;
	}

	public int getFitnessValue(){
		return this.fitness;
	}

	public int getUnfitnessValue(){
		return this.unfitness;
	}
	
	public int getTotal(){
		return this.fitness+this.unfitness;
	}
	
	// Method to set venue information
    public void setVenueSchedule(HashMap<String, Venue> venueSchedule) {
        this.venueSchedule = venueSchedule;
    }

    // Method to get venue information
    public HashMap<String, Venue> getVenueSchedule() {
        return this.venueSchedule;
    }
}