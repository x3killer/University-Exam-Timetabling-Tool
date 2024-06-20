package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class setting {

	@FXML
	public Button saveSettingsButton, loadSettingsButton;
	
	@FXML
	public TextField semesterN, numDaysField, morningSlotField, morningSlotField1, afternoonSlotField, afternoonSlotField1, nightSlotField, nightSlotField1;
	
	@FXML
	public CheckBox morningCheckBox, afternoonCheckBox, nightCheckBox, weekendBreakCheckBox;
	
	@FXML
	public DatePicker startDatePicker;

	public static ArrayList<String> slotTimings;
	
	//public boolean time1,time2,time3,time4;
	public static String semesterName;
	public static String sDate;
	public static int totalDays;
	public static int totalSelectedSlots = 0;
	
	public setting() {
		this.slotTimings = new ArrayList<String>();
	}
	
	public ArrayList<String> getSlotTimings(){
		return this.slotTimings;
	}
	
	public void handleSaveSettings() {
		semesterName = semesterN.getText();
	    totalDays = Integer.parseInt(numDaysField.getText());
	    String currslottiming;
	    
	    if (morningCheckBox.isSelected()) {
            totalSelectedSlots++;
            String startTime = morningSlotField.getText();
            String endTime = morningSlotField1.getText();
            currslottiming = startTime + " - " + endTime;
            this.slotTimings.add(currslottiming);
        }

        if (afternoonCheckBox.isSelected()) {
            totalSelectedSlots++;
            String startTime = afternoonSlotField.getText();
            String endTime = afternoonSlotField1.getText();
            currslottiming = startTime + " - " + endTime;
            this.slotTimings.add(currslottiming);
        }

        if (nightCheckBox.isSelected()) {
            totalSelectedSlots++;
            String startTime = nightSlotField.getText();
            String endTime = nightSlotField1.getText();
            currslottiming = startTime + " - " + endTime;
            this.slotTimings.add(currslottiming);
        }
	    
	    System.out.println("Selected Slot Timings:");
        for (String slotTiming : slotTimings) {
            System.out.println(slotTiming);
        }
	    
	    LocalDate examStartDate = startDatePicker.getValue();
	    if(examStartDate != null) {
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            sDate = examStartDate.format(formatter);
	    }
	    
	    // Save settings to a text file
        saveSettingsToFile();
	   
	     
	     // Display an Alert with the selected settings and total slots
	     Alert alert = new Alert(Alert.AlertType.INFORMATION);
	     alert.setTitle("Settings Saved");
	     alert.setHeaderText(null);
	     alert.setContentText("Your settings have been saved successfully.\n");
	     alert.showAndWait();
	     
	     Stage stage = (Stage)this.saveSettingsButton.getScene().getWindow();
		 stage.close();  
	        
	}
	
	private void saveSettingsToFile() {
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Save Settings");
	    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
	    File selectedFile = fileChooser.showSaveDialog(null);

	    if (selectedFile != null) {
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
	            // Write settings to the file
	            writer.write("Semester Name: " + semesterName);
	            writer.newLine();
	            writer.write("Starting Date: " + sDate);
	            writer.newLine();
	            writer.write("Number of Exam Days: " + totalDays);
	            writer.newLine();

	            // Write selected slot timings to the file
	            for (String slotTiming : slotTimings) {
	                writer.write("Slot Timing: " + slotTiming);
	                writer.newLine();
	            }

	            // Write checkbox selections to the file
	            writer.write("Morning Checkbox: " + morningCheckBox.isSelected());
	            writer.newLine();
	            writer.write("Afternoon Checkbox: " + afternoonCheckBox.isSelected());
	            writer.newLine();
	            writer.write("Night Checkbox: " + nightCheckBox.isSelected());
	            writer.newLine();

	            // Write time fields to the file if the checkbox is selected
	            if (morningCheckBox.isSelected()) {
	                writer.write("Morning Start Time: " + morningSlotField.getText());
	                writer.newLine();
	                writer.write("Morning End Time: " + morningSlotField1.getText());
	                writer.newLine();
	            }
	            if (afternoonCheckBox.isSelected()) {
	                writer.write("Afternoon Start Time: " + afternoonSlotField.getText());
	                writer.newLine();
	                writer.write("Afternoon End Time: " + afternoonSlotField1.getText());
	                writer.newLine();
	            }
	            if (nightCheckBox.isSelected()) {
	                writer.write("Night Start Time: " + nightSlotField.getText());
	                writer.newLine();
	                writer.write("Night End Time: " + nightSlotField1.getText());
	                writer.newLine();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	public void loadSettingsFromFile() {
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Load Settings");
	    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
	    File selectedFile = fileChooser.showOpenDialog(null);

	    if (selectedFile != null) {
	        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
	            String line;
	            // Clear existing settings
	            slotTimings.clear();
	            totalSelectedSlots = 0;
	            while ((line = reader.readLine()) != null) {
	                String[] parts = line.split(": ");
	                if (parts.length == 2) {
	                    String key = parts[0];
	                    String value = parts[1];
	                    switch (key) {
	                        case "Semester Name":
	                            semesterName = value;
	                            break;
	                        case "Starting Date":
	                            sDate = value;
	                            break;
	                        case "Number of Exam Days":
	                            totalDays = Integer.parseInt(value);
	                            break;
	                        case "Slot Timing":
	                            slotTimings.add(value);
	                            totalSelectedSlots++;
	                            break;
	                        case "Morning Checkbox":
	                            morningCheckBox.setSelected(Boolean.parseBoolean(value));
	                            break;
	                        case "Afternoon Checkbox":
	                            afternoonCheckBox.setSelected(Boolean.parseBoolean(value));
	                            break;
	                        case "Night Checkbox":
	                            nightCheckBox.setSelected(Boolean.parseBoolean(value));
	                            break;
	                        case "Morning Start Time":
	                            morningSlotField.setText(value);
	                            break;
	                        case "Morning End Time":
	                            morningSlotField1.setText(value);
	                            break;
	                        case "Afternoon Start Time":
	                            afternoonSlotField.setText(value);
	                            break;
	                        case "Afternoon End Time":
	                            afternoonSlotField1.setText(value);
	                            break;
	                    }
	                }
	            }
	            // Update UI with loaded settings
	            updateUIWithLoadedSettings();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	     // Display an Alert with the selected settings and total slots
		     Alert alert = new Alert(Alert.AlertType.INFORMATION);
		     alert.setTitle("Settings Saved");
		     alert.setHeaderText(null);
		     alert.setContentText("Your settings have been saved successfully.\n");
		     alert.showAndWait();
		     
		     Stage stage = (Stage)this.loadSettingsButton.getScene().getWindow();
			 stage.close();  
	    }
	}
	
	private void updateUIWithLoadedSettings() {
	    // Update UI elements with loaded settings
	    semesterN.setText(semesterName);
	    numDaysField.setText(String.valueOf(totalDays));
	    startDatePicker.setValue(LocalDate.parse(sDate, DateTimeFormatter.ofPattern("dd-MMM-yyyy")));

	    for (String slotTiming : slotTimings) {
	        String[] times = slotTiming.split(" - ");
	        String startTime = times[0];
	        String endTime = times[1];

	        if (startTime.startsWith("M")) {
	            morningCheckBox.setSelected(true);
	            morningSlotField.setText(startTime); // Set start time
	            morningSlotField1.setText(endTime); // Set end time
	        } else if (startTime.startsWith("A")) {
	            afternoonCheckBox.setSelected(true);
	            afternoonSlotField.setText(startTime); // Set start time
	            afternoonSlotField1.setText(endTime); // Set end time
	        } else if (startTime.startsWith("N")) {
	            nightCheckBox.setSelected(true);
	            nightSlotField.setText(startTime); // Set start time
	            nightSlotField1.setText(endTime); // Set end time
	        }
	    }
	}


}
