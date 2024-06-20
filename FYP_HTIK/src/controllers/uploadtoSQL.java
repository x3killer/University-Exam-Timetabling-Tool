package controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


public class uploadtoSQL {
	
	public Label finalDateSheetFilePathLabel;
	String finalDateSheetFilePath = null;
	
	@FXML
	public Button exportButton;
	
	public void onFinalDateSheetPathButtonClick(){
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("Microsoft Excel Documents", "*.xlsx"));
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		File selectedFile = fc.showOpenDialog(null);
		if (selectedFile != null)
		{
			this.finalDateSheetFilePathLabel.setText(selectedFile.getAbsolutePath());
			Tooltip tp = new Tooltip(selectedFile.getAbsolutePath());
			this.finalDateSheetFilePath = selectedFile.getAbsolutePath();
			this.finalDateSheetFilePathLabel.setTooltip(tp);
		}
	}
	
	public void onUploadButtonClick(){
        if (this.finalDateSheetFilePath == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select the final date sheet file.");
            alert.showAndWait();
            return;
        }

        uploadDataToMySQL(this.finalDateSheetFilePath);
    }

    private void uploadDataToMySQL(String selectedFile) {
        try {
            FileInputStream inputStream = new FileInputStream(selectedFile);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(6);
            
            // Connect to MySQL Database
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "020528");
            
            
         // Check if the table has data
            Statement checkStatement = conn.createStatement();
            ResultSet resultSet = checkStatement.executeQuery("SELECT COUNT(*) FROM exam");
            resultSet.next();
            int rowCount = resultSet.getInt(1);
            checkStatement.close();
            resultSet.close();

            // If table has data, clear all the data
            if (rowCount > 0) {
                Statement clearStatement = conn.createStatement();
                clearStatement.executeUpdate("TRUNCATE TABLE exam");
                clearStatement.close();
            }
            
            // Prepare SQL Insert Statement
            String sql = "INSERT INTO exam (courseID, examDate, startTime, endTime, roomID, invigilator) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            
         // Iterate through rows and insert data into MySQL
            int rowIndex = 0; // Track the index of the row
            for (Row row : sheet) {
                // Skip the first row (header row)
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                String date = row.getCell(0).getStringCellValue();
                String Starttime = row.getCell(1).getStringCellValue();
                String Endtime = row.getCell(2).getStringCellValue();
                String courseId = row.getCell(3).getStringCellValue();
                String venue = row.getCell(4).getStringCellValue();
                String invigilator = row.getCell(5).getStringCellValue();
                
                DateFormat inputDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                Date date1 = inputDateFormat.parse(date);
                DateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = outputDateFormat.format(date1);

                DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

             // Parse time strings to Date objects
             Date startDate = new SimpleDateFormat("HH:mm").parse(Starttime);
             Date endDate = new SimpleDateFormat("HH:mm").parse(Endtime);

             // Format Date objects to time strings
             String formattedStartTime = timeFormat.format(startDate);
             String formattedEndTime = timeFormat.format(endDate);

                
                statement.setString(1, courseId);
                statement.setDate(2, java.sql.Date.valueOf(formattedDate));
                statement.setString(3, formattedStartTime);
                statement.setString(4, formattedEndTime);
                statement.setString(5, venue);
                statement.setString(6, invigilator);
;
                
                statement.executeUpdate();
            }
            
            // Close resources
            statement.close();
            conn.close();
            workbook.close();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Data uploaded successfully to MySQL.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while uploading data to MySQL.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}
