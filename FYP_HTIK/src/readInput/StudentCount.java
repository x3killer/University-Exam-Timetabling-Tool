package readInput;

public class StudentCount {
    private String courseID;
    private int capacity;

    public StudentCount(String courseID, int capacity) {
        this.courseID = courseID;
        this.capacity = capacity;
    }

    public String getCourseID() {
        return courseID;
    }

    public int getCapacity() {
        return capacity;
    }
}
