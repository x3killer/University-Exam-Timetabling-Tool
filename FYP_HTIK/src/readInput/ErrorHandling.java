package readInput;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import controllers.setting;


public class ErrorHandling {

	private String inputFilePath;
	private String semesterName;
	private HashMap<String, course> allCourses; //contains a list of all courses
	private HashMap<String, Student> allStudents;
	private ArrayList<String> coursesToBeScheduled; //contains course id of courses that are to be scheduled
	private HashMap<String, String> holidays;
	private ArrayList<String> examDates;
	private ArrayList<String> slotTimings;
	private int totalDays;
	private int totalSlotsPerDay;
	private int seatingCapacityPerSlot;
	private String examStartDate;
	private boolean saturdayOff;
	private String examType;
	private HashMap<String, Venue> venue;
	private HashMap<String, PreferenceDaySlot> preferences;
	private HashMap<String, Invigilator> invigilators;
	public static HashMap<String, StudentCount> studentCountMap;
	
	public class PreferenceDaySlot{
		String courseID;
		int day;
		int slot;

		public PreferenceDaySlot(String courseID, int day, int slot){
			this.courseID = courseID;
			this.day = day;
			this.slot = slot;
		}
		public String getCourseID() {
			return courseID;
		}
		public void setCourseID(String courseID) {
			this.courseID = courseID;
		}
		public int getDay() {
			return day;
		}
		public void setDay(int day) {
			this.day = day;
		}
		public int getSlot() {
			return slot;
		}
		public void setSlot(int slot) {
			this.slot = slot;
		}
	}
	
	public ErrorHandling(String inputFilePath, String examType){
		this.inputFilePath = inputFilePath;
		this.preferences = new HashMap<String, PreferenceDaySlot>();
		this.allCourses = new HashMap<String, course>();
		this.allStudents = new HashMap<String, Student>();
		this.coursesToBeScheduled = new ArrayList<String>();
		this.holidays = new HashMap<String, String>();
		this.examDates = new ArrayList<String>();
		this.slotTimings = new ArrayList<String>();
		this.examType = examType;
		this.venue = new HashMap<String, Venue>();
		this.invigilators = new HashMap<String, Invigilator>();
		this.studentCountMap = new HashMap<String, StudentCount>();
	}

	public ArrayList<String> getSlotTimings(){
		return setting.slotTimings;
	}

	protected String readSlotTimings(Workbook workbook){
		XSSFSheet slotTimingsSheet = (XSSFSheet)workbook.getSheet("SlotTimings");

		if (slotTimingsSheet == null)
			return "SlotTimings sheet not found!";

		Iterator<Row> rowIterator = slotTimingsSheet.rowIterator();

		Row row = null;
		short i = 0;
		try{
			while (rowIterator.hasNext()) {
				row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				Cell cell = cellIterator.next();
				String currslotTiming = cell.getStringCellValue();
				this.slotTimings.add(currslotTiming);
				i++;
			}
		}

		catch (Exception e){
			System.out.println("ERROR");
			return "An error occurred in reading SlotTimings Sheet.";
		}

		if (i < this.totalSlotsPerDay)
			return "Total Slots are " + this.totalSlotsPerDay + ". However, the timings of only " + i + " slots are given.";

		return null;
	}

	protected void populateExamDates(){
		this.examDates.add(setting.sDate);//0th day's date.

		for (short i = 1; i < setting.totalDays; i++){
			boolean flag = false;
			String date = null;
			String previousDate = this.examDates.get(i - 1);
			while (flag == false){
				date = DayDate.getNextDate(previousDate);
				if (this.holidays.containsKey(date) || DayDate.getDayName(date).toLowerCase().equals("sunday") || (this.saturdayOff && DayDate.getDayName(date).toLowerCase().equals("saturday"))){
					previousDate = date;
					continue;
				}

				flag = true;
			}

			this.examDates.add(date);
		}
	}

	public String readDataFromExcelFile(){
		Workbook workbook;
		try {
			workbook = WorkbookFactory.create(new File(inputFilePath));
			DataFormatter dataFormatter = new DataFormatter();

			if (workbook.getSheet("Settings") == null){
				return "Settings sheet not found.";
			}
			
			if (workbook.getSheet("Venue") == null){
				return "Venue sheet not found.";
			}
			
			if (workbook.getSheet("Invigilator") == null){
				return "Invigilator sheet not found.";
			}
			
			if (workbook.getSheet("StudentCount") == null){
				return "Student Count sheet not found.";
			}

			if (workbook.getSheetIndex("StudentCourses") == -1){
				return "StudentCourses sheet not found.";
			}

			String saturdayOff = this.readSettingValue(workbook.getSheet("Settings"), "SaturdayOff");

			if (saturdayOff == null || saturdayOff.equals("")){
				return "saturdayOff not found in settings sheet.";
			}

			if (saturdayOff.toLowerCase().equals("yes")){
				this.saturdayOff = true;
			}
			else if (saturdayOff.toLowerCase().equals("no")){
				this.saturdayOff = false;
			}

			else return "incorrect value of saturdayOff";

			if (this.examType.equals("final")){

				if (readHolidaysForFinalExams(workbook.getSheet("Settings"), "Holiday") == false)
					return "An error occurred in reading holidays.";
			}

			XSSFSheet studentCoursesSheet = (XSSFSheet)workbook.getSheet("StudentCourses");

			Iterator<Row> rowIterator = studentCoursesSheet.rowIterator();

			Row row = rowIterator.next();//ignore header

			while (rowIterator.hasNext()) {
				row = rowIterator.next();

				// Now let's iterate over the columns of the current row
				Iterator<Cell> cellIterator = row.cellIterator();

				//student ID
				Cell cell = cellIterator.next();
				String stuID = dataFormatter.formatCellValue(cell);
				
			            
				//student name
				cell = cellIterator.next();
				String studentName = dataFormatter.formatCellValue(cell);
				

				//CourseID
				cell = cellIterator.next();
				String courseID = dataFormatter.formatCellValue(cell);

				//Course Name
				cell = cellIterator.next();
				String courseName = dataFormatter.formatCellValue(cell);

				cellIterator.next(); //ignore course relation for now.

				//section
				cell = cellIterator.next();
				String sectionName = dataFormatter.formatCellValue(cell);


				 //student degree
				cell = cellIterator.next();
				String degree = dataFormatter.formatCellValue(cell);


				//Teacher
				cell = cellIterator.next();
				String teacherName = dataFormatter.formatCellValue(cell);

				boolean toBeScheduled = true;
				
				if (courseName.toLowerCase().contains("bahasa")|| courseName.toLowerCase().contains("work")|| courseName.toLowerCase().contains("interpersonal") ||courseName.toLowerCase().contains("leadership") || courseName.toLowerCase().contains("proposal") || courseName.toLowerCase().contains("project") || courseName.toLowerCase().contains("project-i")){

					toBeScheduled = false;
				}

				course c = null;
				Student st = null;
				
				//if the course is already added in the hashmap
				if (this.allCourses.containsKey(courseID)){
					c = this.allCourses.get(courseID);
					if (c.toBeScheduled == false)
						c.toBeScheduled = toBeScheduled;
				}
				else{//else if course is not already added in the hashmap.
					c = new course(courseID, courseName, toBeScheduled);
					this.allCourses.put(courseID, c);
					if (toBeScheduled){
						this.coursesToBeScheduled.add(courseID);
					}
				}

				if (this.allStudents.containsKey(stuID)){
					st = this.allStudents.get(stuID);
				}
				else { //if student does not exist in the hashmap, add.
					st = new Student(stuID, studentName, degree);

					this.allStudents.put(stuID, st);
				}

				st.getCourses().add(courseID);
				c.getStudentID().add(stuID);

			}

		}
		catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			return "The input file is encrypted!";

		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			return "An Error Occurred while reading Student Course File. The file may be missing or permissions may be required to read it.!";

		}
		catch (Exception e){
			System.out.println(e);
			return "An Error Occurred while reading the Student Course file.!";

		}

		this.populateExamDates();

		String message = this.readSlotTimings(workbook);
		if (message != null)
			return message;
		
		message = this.readVenueInfo(workbook);
		if (message != null)
			System.out.println(message);
		
		message = this.readStudentCountInfo(workbook);
		if (message != null)
			System.out.println(message);
		
		message = this.readInvigilatorInfo(workbook);
		if (message != null)
			System.out.println(message);
		
		return this.readPreferences(workbook);
	}

	protected String readPreferences(Workbook workbook){
		XSSFSheet slotTimingsSheet = (XSSFSheet)workbook.getSheet("preferences");

		if (slotTimingsSheet == null)
			return null; //preferences sheet does not exist

		Iterator<Row> rowIterator = slotTimingsSheet.rowIterator();

		Row row = null;
		try{
			while (rowIterator.hasNext()) {

				row = rowIterator.next();

				Iterator<Cell> cellIterator = row.cellIterator();
				Cell cell = cellIterator.next();
				String courseID = cell.getStringCellValue();
				if (this.coursesToBeScheduled.contains(courseID) == false)
					return "The course ID: " + courseID + " in preferences list is invalid!";

				cell = cellIterator.next();
				int day = Integer.valueOf((int)cell.getNumericCellValue());
				cell = cellIterator.next();

				int slot = Integer.valueOf((int)cell.getNumericCellValue());

				if (day<0 || day>this.getTotalDays())
					return "Invalid day for " + courseID + " in preference list!";

				if (slot<0 || slot>this.getTotalSlotsPerDay())
					return "invalid slot for" + courseID + " in preference list!";

				this.preferences.put(courseID, new PreferenceDaySlot(courseID, day, slot));

			}
		}

		catch (Exception e){
			System.out.println("ERROR");
			System.out.println(e);
			return "An error occurred in reading Preferences Sheet.";
		}

		return null;
	}
	
	
	protected boolean readHolidaysForFinalExams(Sheet settingsSheet, String settingName){
		Iterator<Row> settingsIterator = settingsSheet.rowIterator();

		try{

			Row headerRow = settingsIterator.next();
			Row columnRow = settingsIterator.next();

			Iterator<Cell> headerIterator = headerRow.cellIterator();
			Iterator<Cell> columnIterator = columnRow.cellIterator();

			while (headerIterator.hasNext()){
				Cell headerValue = headerIterator.next();
				Cell value = columnIterator.next();

				if (headerValue.getStringCellValue().toLowerCase().compareTo(settingName.toLowerCase()) == 0){
					DataFormatter dataFormatter = new DataFormatter();
					SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
					Date dateData;

					try {
						dateData = format.parse(dataFormatter.formatCellValue(value));
						holidays.put(format.format(dateData), format.format(dateData));

					}
					catch (ParseException e) {
						e.printStackTrace();
						return false;
					}
				}
			}

			return true; //read successful
		}

		catch (Exception E){
			return false;
		}

	}
	
	protected String readVenueInfo(Workbook workbook){
	    XSSFSheet venueSheet = (XSSFSheet)workbook.getSheet("Venue");

	    if (venueSheet == null)
	        return "Venue sheet not found.";

	    Iterator<Row> rowIterator = venueSheet.rowIterator();

	    Row row = null;
	    row = rowIterator.next();
	    try{
	        while (rowIterator.hasNext()) {
	            row = rowIterator.next();

	            Iterator<Cell> cellIterator = row.cellIterator();
	            Cell cell = cellIterator.next();
	            String roomID = cell.getStringCellValue();

	            // Check if the next cell contains a numeric value
	            if (cellIterator.hasNext()) {
	                cell = cellIterator.next();
	                if (cell.getCellType() == CellType.NUMERIC) {
	                    int seatingCapacity = (int) cell.getNumericCellValue();
	                    // Create Venue object and store it in the HashMap
	                    venue.put(roomID, new Venue(roomID, seatingCapacity));
	                } else {
	                    // If the cell doesn't contain a numeric value, skip this row
	                    System.out.println("Invalid seating capacity for room " + roomID);
	                }
	            } else {
	                // If there are no more cells in the row, skip this row
	                System.out.println("No seating capacity found for room " + roomID);
	            }
	        }
	    } catch (Exception e){
	        System.out.println("ERROR");
	        System.out.println(e);
	        return "An error occurred in reading Venue Sheet.";
	    }
	    return null; // No error
	}
	
	protected String readStudentCountInfo(Workbook workbook) {
	    XSSFSheet studentCountSheet = (XSSFSheet) workbook.getSheet("StudentCount");

	    if (studentCountSheet == null)
	        return "StudentCount sheet not found.";

	    Iterator<Row> rowIterator = studentCountSheet.rowIterator();

	    Row row = null;
	    row = rowIterator.next();
	    try {
	        while (rowIterator.hasNext()) {
	            row = rowIterator.next();

	            Iterator<Cell> cellIterator = row.cellIterator();
	            Cell cell = cellIterator.next();
	            String courseID = null;
	            if (cell.getCellType() == CellType.STRING) {
	                // Handle string cell value
	                courseID = cell.getStringCellValue();
	            }

	            cell = cellIterator.next();
	            int capacity = (int) cell.getNumericCellValue();

	            // Store the student count information in the HashMap
	            studentCountMap.put(courseID, new StudentCount(courseID, capacity));
	        }
	    } catch (Exception e) {
	        System.out.println("ERROR");
	        System.out.println(e);
	        return "An error occurred in reading StudentCount Sheet.";
	    }
	    return null; // No error
	}
	
	protected String readInvigilatorInfo(Workbook workbook){
        XSSFSheet inviSheet = (XSSFSheet)workbook.getSheet("Invigilator");

        if (inviSheet == null)
            return "Invigilator sheet not found.";

        Iterator<Row> rowIterator = inviSheet.rowIterator();

        Row row = null;
        row = rowIterator.next();
        try{
            while (rowIterator.hasNext()) {
                row = rowIterator.next();

                Iterator<Cell> cellIterator = row.cellIterator();
                Cell cell = cellIterator.next();
                String invigilatorID = null;
                if (cell.getCellType() == CellType.NUMERIC) {
                    // Handle numeric cell value
                    invigilatorID = String.valueOf((int) cell.getNumericCellValue());
                } else if (cell.getCellType() == CellType.STRING) {
                    // Handle string cell value
                    invigilatorID = cell.getStringCellValue();
                }
                
                cell = cellIterator.next();
                String invigilatorName = cell.getStringCellValue();

                invigilators.put(invigilatorID, new Invigilator(invigilatorID, invigilatorName));
            }
        } catch (Exception e){
            System.out.println("ERROR");
            System.out.println(e);
            return "An error occurred in reading Invigilator Sheet.";
        }
        return null; // No error
    }
	
	private String readSettingValue(Sheet settingsSheet, String settingName){
		Iterator<Row> settingIterator = settingsSheet.rowIterator();

		try{
			Row headerRow = settingIterator.next();
			Row columnRow = settingIterator.next();

			Iterator<Cell> headerIterator = headerRow.cellIterator();
			Iterator<Cell> columnIterator = columnRow.cellIterator();


			while (headerIterator.hasNext()){
				Cell headerValue = headerIterator.next();
				Cell value = columnIterator.next();

				if (headerValue.getStringCellValue().toLowerCase().compareTo(settingName.toLowerCase()) == 0){

					DataFormatter dataFormatter = new DataFormatter();

					if (settingName.toLowerCase().equals("examstartdate") == false)
						return dataFormatter.formatCellValue(value);

					SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
					Date dateData;
					try {
						dateData = format.parse(dataFormatter.formatCellValue(value));
						return format.format(dateData);

					}
					catch (ParseException e) {
						e.printStackTrace();
						return null;
					}
				}
			}
		}
		catch (Exception E){
			return null;
		}
		return null;
	}

	public HashMap<String, PreferenceDaySlot> getPreferences(){
		return this.preferences;
	}

	public String getSemesterName(){
		return setting.semesterName;
	}
	public ArrayList<String> getExamDates(){
		return this.examDates;
	}
	public HashMap<String, course> getAllCourses(){
		return this.allCourses;
	}

	public ArrayList<String> getCoursesToBeScheduled(){
		return this.coursesToBeScheduled;
	}

	public int getTotalDays(){
		return setting.totalDays;
	}

	public int getTotalSlotsPerDay(){
		return setting.totalSelectedSlots;
	}

	public HashMap<String, Student> getAllStudents() {
		return allStudents;
	}

	public String getExamStartDate(){
		return this.examStartDate;
	}
	
	public HashMap<String, Venue> getVenue() {
		return venue;
	}
	
	public HashMap<String, Invigilator> getInvigilators() {
        return invigilators;
    }
}
