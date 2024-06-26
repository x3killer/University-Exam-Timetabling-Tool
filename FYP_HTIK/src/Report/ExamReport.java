package Report;
import java.util.HashMap;
import org.apache.poi.ss.usermodel.*;

import GA.CSchedule;
import GA.Day;
import readInput.ErrorHandling;

public abstract class ExamReport {

	protected Day[]dateSheet;
	protected HashMap<String, CSchedule> courseSchedule; 
	protected ErrorHandling problemData;
	protected String outputDirectoryPath;

	public abstract String exportToExcel();

	public void printDateSheetList(Workbook workbook){
		Sheet dateSheetListSheet = workbook.createSheet(FinalReportName.dateSheet);
		int rowNo = 0;
		Row row = dateSheetListSheet.createRow(rowNo++);

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		Font f = workbook.createFont();
		f.setBold(true);
		cellStyle.setFont(f);

		Cell cell = row.createCell(0);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("Date");



		cell = row.createCell(1);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("CourseId");

		cell = row.createCell(2);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("StartTime");

		cell = row.createCell(3);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("EndTime");


		cell = row.createCell(4);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("CourseName");

		cellStyle = workbook.createCellStyle();
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);


		for (int i = 0; i < this.problemData.getTotalDays(); i++){
			for (int j = 0; j < this.problemData.getTotalSlotsPerDay(); j++){
				for (String c : this.dateSheet[i].getSlots()[j].getCoursesScheduled()){
					row = dateSheetListSheet.createRow(rowNo++);
					cell = row.createCell(0);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(this.problemData.getExamDates().get(i));


					cell = row.createCell(1);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(c);

					cell = row.createCell(2);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(this.problemData.getSlotTimings().get(j).split(" - ")[0]);

					cell = row.createCell(3);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(this.problemData.getSlotTimings().get(j).split(" - ")[1]);

					cell = row.createCell(4);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(this.problemData.getAllCourses().get(c).getCourseName());

				}
			}
		}

		dateSheetListSheet.autoSizeColumn(0);
		dateSheetListSheet.autoSizeColumn(1);
		dateSheetListSheet.autoSizeColumn(2);
		dateSheetListSheet.autoSizeColumn(3);
		dateSheetListSheet.autoSizeColumn(4);
	}

}
