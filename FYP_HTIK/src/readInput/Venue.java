package readInput;

import java.util.ArrayList;

public class Venue {
    String rNumber;
    int capacity;
    String invigilatorId; // Add field to store invigilator ID

    public Venue(String rNumber, int capacity) {
        this.rNumber = rNumber;
        this.capacity = capacity;
    }

    // Getters and setters for rNumber, capacity, and invigilatorId
    public String getRNumber() {
        return rNumber;
    }

    public void setRNumber(String rNumber) {
        this.rNumber = rNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getInvigilatorId() {
        return invigilatorId;
    }

    public void setInvigilatorId(String invigilatorId) {
        this.invigilatorId = invigilatorId;
    }
}