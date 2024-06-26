package datesheet_readers;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import GA.CSchedule;
import GA.Day;
import GA.slot;
import Report.ExamReport;
import Report.FinalExamReport;
import readInput.DayDate;
import readInput.ErrorHandling;

public class FinalDateSheetReader {


	private ErrorHandling problemData;
	private Day[]dateSheet;
	private HashMap<String, CSchedule> courseSchedule;


	HashSet<String> courseIdOfScheduledCourses;

	String inputStudentCourseFilePath;
	String inputFinalDateSheetPath;
	String outputDirectoryPath;
	public FinalDateSheetReader(String inputStudentCoursefilePath, String inputFinalDateSheetPath, String outputDirectoryPath){
		this.inputStudentCourseFilePath = inputStudentCoursefilePath;
		this.inputFinalDateSheetPath = inputFinalDateSheetPath;
		this.outputDirectoryPath = outputDirectoryPath;


	}

	public String readStudentCourseFileAndFinalDatesheetAndGenerateReport(){
		this.problemData = new ErrorHandling(this.inputStudentCourseFilePath, "final");
		String status = this.problemData.readDataFromExcelFile();

		if (status != null)
			return status; //an error has occurred while reading data from excel file

		this.courseSchedule = new HashMap<String, CSchedule>();
		this.courseIdOfScheduledCourses = new HashSet<String>();

		this.dateSheet = new Day[this.problemData.getTotalDays()];



		for (int i = 0; i < this.problemData.getTotalDays(); i++){
			dateSheet[i] = new Day(this.problemData.getTotalSlotsPerDay());
			for (int j = 0; j < this.problemData.getTotalSlotsPerDay(); j++){
				dateSheet[i].getSlots()[j] = new slot();
			}

		}

		status = inputFinalDateSheetReader();//read datesheet
		if (status.equals("") == false){
			return status;
		}
		//public FinalExamReport(Day[]dateSheet, HashMap<String, CourseSchedule> courseSchedule, ProblemData problemData, String outputDirectoryPath)
		ExamReport report = new FinalExamReport(this.dateSheet, courseSchedule, problemData, this.outputDirectoryPath + "\\Final Exam Report.xlsx");
		return report.exportToExcel();

	}

	protected String inputFinalDateSheetReader(){
		try {
			Workbook workbook = WorkbookFactory.create(new File(this.inputFinalDateSheetPath));
			if (workbook.getSheet("Complete") == null){
				return "The complete sheet of datesheet is missing!";

			}

			XSSFSheet completeDateSheet = (XSSFSheet)workbook.getSheet("Complete");



			Iterator<Row> rowIterator = completeDateSheet.rowIterator();

			rowIterator.next();//ignore headers
			rowIterator.next();
			rowIterator.next();
			int day = 0;

			while (rowIterator.hasNext()) {

				Row row = rowIterator.next();

				// Now let's iterate over the columns of the current row

				Iterator<Cell> cellIterator = row.cellIterator();
				Cell cell = cellIterator.next(); //day


				while (cell.getStringCellValue().equals("") == false){

					if (cell.getStringCellValue().toLowerCase().equals(DayDate.getDayName(this.problemData.getExamDates().get(day)).toLowerCase()) == false){
						return "Incorrect Day Name in date sheet detected!";
					}
					
					cell = cellIterator.next(); //date

					String date;
					try{
						DataFormatter dataFormatter = new DataFormatter();
						date = dataFormatter.formatCellValue(cell);
						SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
						Date dateData = format.parse(date.toString());
						date = format.format(dateData);

						if (date.toLowerCase().equals(this.problemData.getExamDates().get(day).toLowerCase()) == false)
						{
							return "Incorrect date in date sheet detected!";

						}
					}

					catch (ParseException e){
						return "Date Format Error in Date Sheet!";
					}
					catch (Exception e){
						return "Date Format Error in Date Sheet!";
					}


					for (int slot = 0; slot < this.problemData.getTotalSlotsPerDay(); slot++){
						cell = cellIterator.next(); //courseCode
						String courseCode = cell.getStringCellValue();
				
						cell = cellIterator.next(); //course Name

						if (courseCode.equals("")){
							continue;
						}


						else if (!this.problemData.getAllCourses().containsKey(courseCode)){
							return "Course Code: " + courseCode + " not found from student courses file!";
						}
						else{
							if (this.courseIdOfScheduledCourses.contains(courseCode) == true){
								return "The course: " + this.problemData.getAllCourses().get(courseCode).getCourseName() + "(" + courseCode + ") has been scheduled twice. Please Remove this error!";

							}

							else{

								this.courseIdOfScheduledCourses.add(courseCode);
								this.problemData.getAllCourses().get(courseCode).setToBeScheduled(true); //this line has been added otherwise MS level courses cause problems
								CSchedule cs = new CSchedule(courseCode, slot, day);
								this.courseSchedule.put(courseCode, cs);
								this.dateSheet[day].getSlots()[slot].getCoursesScheduled().add(courseCode);

							}

						}
					}


					row = rowIterator.next();
					cellIterator = row.cellIterator();
					cell = cellIterator.next(); //day
				}

				day++;


			}


			return "";

		}
		catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			return "The date sheet is encrypted.";
		}
		catch (IOException e) {
			return "An error occurred while reading date sheet. The date sheet file may be missing, or permissions may be required to read it.";

		}
		catch (Exception e){
			System.out.println(e);
			e.printStackTrace();
			return "Date Sheet Format is Incorrect. Please correct the format!"; //the exception can occur when the file is there but some cell has incorrect format.
		}

	}

}
