package Report;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import GA.CSchedule;
import GA.Chromosome;
import readInput.DayDate;
import readInput.ErrorHandling;
import readInput.Invigilator;
import readInput.Venue;

public class FinalExamExporter extends ExamExporter {

	public FinalExamExporter(ErrorHandling problemData, Chromosome bestSchedule, String outputFileName){
		super(problemData, bestSchedule, outputFileName);
	}

	public String exportDateSheetToExcel(){
		try {

			Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
			FileOutputStream fileOut = new FileOutputStream(this.outputDirectoryPath);

			Sheet CSsheet = workbook.createSheet("CS");
			Sheet CNsheet = workbook.createSheet("CN");
			Sheet CTsheet = workbook.createSheet("CT");
			Sheet IAsheet = workbook.createSheet("IA");
			Sheet IBsheet = workbook.createSheet("IB");
			Sheet completeSheet = workbook.createSheet("Complete");
			Sheet SQLSheet = workbook.createSheet("VenueInvigilator");


			setDatesheetNameHeader(CSsheet, "DATE SHEET FOR FINAL EXAMINATION (CS DEPT)");
			setDatesheetNameHeader(CNsheet, "DATE SHEET FOR FINAL EXAMINATION (CN DEPT)");
			setDatesheetNameHeader(CTsheet, "DATE SHEET FOR FINAL EXAMINATION (CT DEPT)");
			setDatesheetNameHeader(IAsheet, "DATE SHEET FOR FINAL EXAMINATION (IA DEPT)");
			setDatesheetNameHeader(IBsheet, "DATE SHEET FOR FINAL EXAMINATION (IB DEPT)");


			setDatesheetNameHeader(completeSheet, "COMPLETE DATE SHEET");

			setDatesheetSemester(CSsheet, this.problemData.getSemesterName() + " Semester");
			setDatesheetSemester(CNsheet, this.problemData.getSemesterName() + " Semester");
			setDatesheetSemester(CTsheet, this.problemData.getSemesterName() + " Semester");
			setDatesheetSemester(IAsheet, this.problemData.getSemesterName() + " Semester");
			setDatesheetSemester(IBsheet, this.problemData.getSemesterName() + " Semester");

			setDatesheetSemester(completeSheet, this.problemData.getSemesterName() + " Semester");

			this.setFinalDateSheetDayDateHeader(CSsheet);
			this.setFinalDateSheetDayDateHeader(CNsheet);
			this.setFinalDateSheetDayDateHeader(CTsheet);
			this.setFinalDateSheetDayDateHeader(IAsheet);
			this.setFinalDateSheetDayDateHeader(IBsheet);
			this.setFinalDateSheetDayDateHeader(completeSheet);

			Font font = workbook.createFont();
			font.setBold(true);
			font.setFontName("Calibri");
			font.setFontHeightInPoints((short)10);
			font.setColor(IndexedColors.BLACK.getIndex());

			for (int i = 0; i < this.problemData.getTotalSlotsPerDay(); i++)
			{

				// Create a CellStyle with the font
				CellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFont(font);

				headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
				headerCellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
			}

			this.printDateSheet(completeSheet, "complete");
			this.checkDepartmentCourseOffer();
			this.printDateSheet(CSsheet, "CS");
			this.printDateSheet(CNsheet, "CN");
			this.printDateSheet(CTsheet, "CT");
			this.printDateSheet(IAsheet, "IA");
			this.printDateSheet(IAsheet, "IB");
			this.printExamDetailsToSQLSheet(SQLSheet);

			workbook.write(fileOut);
			workbook.close();
			fileOut.close();

			// Closing the workbook

		}
		catch (Exception e) {
			return "An error occurred while exporting the date sheet to excel file.";

		}

		return null;
	}


	public void setDatesheetNameHeader(Sheet sheet, String text){
		// Create a Font for styling header cells
		Workbook workbook = sheet.getWorkbook();

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setItalic(true);
		headerFont.setFontName("Arial");
		headerFont.setFontHeightInPoints((short)14);
		headerFont.setColor(IndexedColors.RED.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

		//create a row
		Row headerRow = sheet.createRow(0);

		// Create cells
		for (int i = 0; i < 8; i++) {
			Cell cell = headerRow.createCell(i);
			if (i == 0)
				cell.setCellValue(text);
			cell.setCellStyle(headerCellStyle);

		}


		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
	}

	public void printDateSheet(Sheet completeSheet, String dept){
		int currentRowNumber = 3;

		Workbook workbook = completeSheet.getWorkbook();
		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontName("Calibri");
		font.setFontHeightInPoints((short)10);
		font.setColor(IndexedColors.BLACK.getIndex());


		//the following loop creates rows without writing data about exam schedule.
		for (int i = 0; i < this.problemData.getTotalDays(); i++){
			// Create a CellStyle with the font
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(font);

			headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
			headerCellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);

			headerCellStyle.setBorderBottom(BorderStyle.THIN);
			headerCellStyle.setBorderLeft(BorderStyle.THIN);
			headerCellStyle.setBorderTop(BorderStyle.THIN);
			headerCellStyle.setBorderRight(BorderStyle.THIN);
			headerCellStyle.setWrapText(true);

			int maximumExamsScheduledInAnySlot = 0;

			if (dept.equals("CN")){
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfCNExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("CS")){
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfCSExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("CT")){
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfCTExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("IA")){
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfIAExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("IB")){
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfIBExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("complete")){
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfExamsScheduledInAnySlot(i);
			}


			if (maximumExamsScheduledInAnySlot == 0)
				maximumExamsScheduledInAnySlot = 1;

			//creating rows for each day
			for (int j = currentRowNumber; j < currentRowNumber + maximumExamsScheduledInAnySlot; j++){
				Row row = completeSheet.createRow(j);
				row.setHeightInPoints(17);
				Cell cell = row.createCell(0);
				cell.setCellStyle(headerCellStyle);
				cell.setCellValue(DayDate.getDayName(this.problemData.getExamDates().get(i)));

				cell = row.createCell(1);
				cell.setCellStyle(headerCellStyle);
				cell.setCellValue(this.problemData.getExamDates().get(i));


				int l = 2;

				//creating cells for each row
				for (int k = 0; k < this.problemData.getTotalSlotsPerDay(); k++){
					cell = row.createCell(k + l);
					cell.setCellStyle(headerCellStyle);
					//cell.setCellValue("qwe");
					cell = row.createCell(k + l + 1);
					cell.setCellStyle(headerCellStyle);
					// cell.setCellValue("123");   
					l++;

				}

			}//end of row creation loop

			currentRowNumber += maximumExamsScheduledInAnySlot;
			Row row = completeSheet.createRow(currentRowNumber);
			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
			cellStyle.setFillPattern(FillPatternType.THICK_VERT_BANDS);
			for (int z = 0; z < this.problemData.getTotalSlotsPerDay() * 2 + 2; z++){

				row.createCell(z);
				row.getCell(z).setCellStyle(cellStyle);

			}

			currentRowNumber++;

		}

		//writing exam data to created rows
		currentRowNumber = 3;
		for (int i = 0; i < this.problemData.getTotalDays(); i++){
			int maximumExamsScheduledInAnySlot = 0;

			if (dept.equals("CN")){
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfCNExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("CS")){
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfCSExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("CT")){
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfCTExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("IA")){
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfIAExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("IB")){
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfIBExamsScheduledInAnySlot(i);
			}
			else if (dept.equals("complete")){
				maximumExamsScheduledInAnySlot = this.findMaximumNumberOfExamsScheduledInAnySlot(i);
			}
			
			if (maximumExamsScheduledInAnySlot == 0)
				maximumExamsScheduledInAnySlot = 1;

			int l = 2;
			for (int j = 0; j < this.problemData.getTotalSlotsPerDay(); j++){


				int k = 0;

				if (dept.equals("complete")){
					for (String courseCode : this.bestSchedule.getDayWiseSchedule()[i].getSlots()[j].getCoursesScheduled()){
						completeSheet.getRow(currentRowNumber + k).getCell(j + l).setCellValue(courseCode);
						completeSheet.getRow(currentRowNumber + k).getCell(j + l + 1).setCellValue(this.problemData.getAllCourses().get(courseCode).getCourseName());

						k++;
					}
				}

				else if (dept.equals("IA")){
					for (String courseCode : this.dateSheet[i].slots[j].iaExamsScheduled){
						completeSheet.getRow(currentRowNumber + k).getCell(j + l).setCellValue(courseCode);
						completeSheet.getRow(currentRowNumber + k).getCell(j + l + 1).setCellValue(this.problemData.getAllCourses().get(courseCode).getCourseName());

						k++;
					}
				}

				else if (dept.equals("CN")){
					for (String courseCode : this.dateSheet[i].slots[j].cnExamsScheduled){
						completeSheet.getRow(currentRowNumber + k).getCell(j + l).setCellValue(courseCode);
						completeSheet.getRow(currentRowNumber + k).getCell(j + l + 1).setCellValue(this.problemData.getAllCourses().get(courseCode).getCourseName());

						k++;
					}
				}

				else if (dept.equals("CS")){
					for (String courseCode : this.dateSheet[i].slots[j].csExamsScheduled){
						completeSheet.getRow(currentRowNumber + k).getCell(j + l).setCellValue(courseCode);
						completeSheet.getRow(currentRowNumber + k).getCell(j + l + 1).setCellValue(this.problemData.getAllCourses().get(courseCode).getCourseName());

						k++;
					}
				}

				else if (dept.equals("CT")){
					for (String courseCode : this.dateSheet[i].slots[j].ctExamsScheduled){
						completeSheet.getRow(currentRowNumber + k).getCell(j + l).setCellValue(courseCode);
						completeSheet.getRow(currentRowNumber + k).getCell(j + l + 1).setCellValue(this.problemData.getAllCourses().get(courseCode).getCourseName());

						k++;
					}
				}
				
				else if (dept.equals("IB")){
					for (String courseCode : this.dateSheet[i].slots[j].ibExamsScheduled){
						completeSheet.getRow(currentRowNumber + k).getCell(j + l).setCellValue(courseCode);
						completeSheet.getRow(currentRowNumber + k).getCell(j + l + 1).setCellValue(this.problemData.getAllCourses().get(courseCode).getCourseName());

						k++;
					}
				}

				l++;

			}
			currentRowNumber += (maximumExamsScheduledInAnySlot + 1);

		}



		for (int i = 0; i < this.problemData.getTotalSlotsPerDay() * 2 + 2; i++){
			completeSheet.autoSizeColumn(i);

		}

    }
				

	public void setDatesheetSemester(Sheet sheet, String text){

		Workbook workbook = sheet.getWorkbook();

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setItalic(true);
		headerFont.setFontName("Arial");
		headerFont.setFontHeightInPoints((short)14);
		headerFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

		//create a row
		Row headerRow = sheet.createRow(1);

		//Create cells

		for (int i = 0; i < 8; i++) {
			Cell cell = headerRow.createCell(i);
			if (i == 0)
				cell.setCellValue(text);
			cell.setCellStyle(headerCellStyle);

		}


		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));
	}

	public void setFinalDateSheetDayDateHeader(Sheet sheet){

		// Create a Font for styling header cells
		Workbook workbook = sheet.getWorkbook();

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);

		headerFont.setFontName("Arial");
		headerFont.setFontHeightInPoints((short)11);
		headerFont.setColor(IndexedColors.BLACK.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		Row headerRow = sheet.createRow(2);

		headerRow.setHeightInPoints((float)18);

		Cell cell = headerRow.createCell(0);
		cell.setCellValue("Day");
		cell.setCellStyle(headerCellStyle);
		sheet.autoSizeColumn(0);

		cell = headerRow.createCell(1);
		cell.setCellValue("Date");
		cell.setCellStyle(headerCellStyle);
		sheet.autoSizeColumn(1);

		int j = 2;
		for (int i = 0; i < this.problemData.getTotalSlotsPerDay(); i++){
			cell = headerRow.createCell(i + j);
			cell.setCellValue("Code");
			cell.setCellStyle(headerCellStyle);
			sheet.autoSizeColumn(i + j);

			cell = headerRow.createCell(i + j + 1);
			cell.setCellValue(this.problemData.getSlotTimings().get(i));
			cell.setCellStyle(headerCellStyle);
			sheet.autoSizeColumn(i + j + 1);
			j++;
		}

	}

	public void printExamDetailsToSQLSheet(Sheet sqlSheet) {
	    int rowNumber = 0;

	    // Create header row
	    Row headerRow = sqlSheet.createRow(rowNumber++);
	    String[] headers = {"Date", "Start Time", "End Time", "Course ID", "Venue", "Invigilator"};
	    CellStyle headerCellStyle = sqlSheet.getWorkbook().createCellStyle();
	    Font headerFont = sqlSheet.getWorkbook().createFont();
	    headerFont.setBold(true);
	    headerCellStyle.setFont(headerFont);

	    for (int i = 0; i < headers.length; i++) {
	        Cell headerCell = headerRow.createCell(i);
	        headerCell.setCellValue(headers[i]);
	        headerCell.setCellStyle(headerCellStyle);
	    }

	    // Populate exam details
	    for (int i = 0; i < problemData.getTotalDays(); i++) {
	        for (int j = 0; j < problemData.getTotalSlotsPerDay(); j++) {
	            String startTime = problemData.getSlotTimings().get(j).split(" - ")[0];
	            String endTime = problemData.getSlotTimings().get(j).split(" - ")[1];

	            for (String courseCode : bestSchedule.getDayWiseSchedule()[i].getSlots()[j].getCoursesScheduled()) {
	                Row examRow = sqlSheet.createRow(rowNumber++);
	                examRow.createCell(0).setCellValue(problemData.getExamDates().get(i)); // Date
	                examRow.createCell(1).setCellValue(startTime); // Start Time
	                examRow.createCell(2).setCellValue(endTime); // End Time
	                examRow.createCell(3).setCellValue(courseCode); // CourseID
	                                
	                // Include venue information
	                CSchedule courseSchedule = bestSchedule.getCourseWiseSchedule().get(courseCode);
	                ArrayList<String> venueDetailsWithCommas = courseSchedule.getVenueDetailsWithCommas();
	                // Concatenate venue details with commas
	                StringBuilder venueDetails = new StringBuilder();
	                for (String venueDetail : venueDetailsWithCommas) {
	                    venueDetails.append(venueDetail).append(", ");
	                }
	                // Remove the trailing comma and space
	                if (venueDetails.length() > 2) {
	                    venueDetails.setLength(venueDetails.length() - 2);
	                }
	                examRow.createCell(4).setCellValue(venueDetails.toString()); // Venue
	                                
	                // Include invigilator information
	                ArrayList<Invigilator> invigilators = courseSchedule.getAllocatedInvigilators();
	                if (invigilators != null && !invigilators.isEmpty()) {
	                    StringBuilder invigilatorNames = new StringBuilder();
	                    for (Invigilator invigilator : invigilators) {
	                        invigilatorNames.append(invigilator.getName()).append(", ");
	                    }
	                    // Remove the trailing comma and space
	                    if (invigilatorNames.length() > 2) {
	                        invigilatorNames.setLength(invigilatorNames.length() - 2);
	                    }
	                    examRow.createCell(5).setCellValue(invigilatorNames.toString()); // Invigilator
	                } else {
	                    examRow.createCell(5).setCellValue("No invigilator assigned"); // No Invigilator
	                }
	            }
	        }
	    }

	    // Auto-size columns
	    for (int i = 0; i < headers.length; i++) {
	        sqlSheet.autoSizeColumn(i);
	    }
	}

}
