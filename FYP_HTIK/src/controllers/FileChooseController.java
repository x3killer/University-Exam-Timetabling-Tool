package controllers;
import java.io.File;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import Report.ExamExporter;
import Report.ExamReport;
import Report.FinalExamExporter;
import Report.FinalExamReport;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import readInput.course;
import readInput.*;
import schedule.ExamScheduler;

public class FileChooseController {

	public Label inputFilePathLabel;
	public Label outputDirectoryPathLabel;
	public Button inputFilePathButton;
	public Button outputDirectoryPathButton;
	public CheckBox checkbox1;
	public CheckBox checkbox2;
	public CheckBox checkbox3;
	Button generateButton;
	Button settingsButton;
	String inputFilePath = null;
	String outputDirectoryPath = null;

	public TableView coursesSchdeduledTable;
	public TableColumn courseCodeColumnForScheduledTable;
	public TableColumn courseNameColumnForScheduledTable;

	public TableView coursesNotScheduledTable;
	public TableColumn courseCodeColumnForUnscheduledTable;
	public TableColumn courseNameColumnForUnscheduledTable;
	public Button scheduleButton;
	public Button notToscheduleButton;
	private String examType;
	ExamScheduler examScheduler;
	
	// This method is called when the settings button is clicked
    public void onSettingsButtonClick() {
      	Stage window = new Stage();
      	Parent root;
        // Open a new window or dialog for settings
        try {
        	root = FXMLLoader.load(getClass().getClassLoader().getResource("views/settings.fxml"));
        	window.setTitle("Settings");
        	window.setScene(new Scene(root));
        	window.setMaxHeight(600);
        	window.setMaxWidth(800);
        	window.initStyle(StageStyle.DECORATED);
        	window.initModality(Modality.APPLICATION_MODAL);
        	window.showAndWait();
        }
        catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public ExamScheduler getExamScheduler(){
		return this.examScheduler;
	}
	
	//user can select courses from not to schedule courses table to add them to schedule courses table
	public void toScheduleButtonHandler(){
		this.coursesSchdeduledTable.getSelectionModel().clearSelection();

		ObservableList<course> selectedcourses = this.coursesNotScheduledTable.getSelectionModel().getSelectedItems();
		ObservableList<course> tablecourses = this.coursesNotScheduledTable.getItems();

		this.coursesSchdeduledTable.getItems().addAll(selectedcourses);

		for (int i = 0; i < selectedcourses.size(); i++) {
			selectedcourses.get(i).setToBeScheduled(true);
			this.examScheduler.getProblemData().getCoursesToBeScheduled().add(selectedcourses.get(i).getCourseID());
		}

		tablecourses.removeAll(selectedcourses);
		this.coursesNotScheduledTable.getSelectionModel().clearSelection();

	}

	public void notToScheduleButtonHandler() {
		this.coursesNotScheduledTable.getSelectionModel().clearSelection();
		ObservableList<course> selectedcourses = this.coursesSchdeduledTable.getSelectionModel().getSelectedItems();
		ObservableList<course> tablecourses = this.coursesSchdeduledTable.getItems();

		this.coursesNotScheduledTable.getItems().addAll(selectedcourses);

		for (int i = 0; i < selectedcourses.size(); i++) {
			selectedcourses.get(i).setToBeScheduled(false);
			this.examScheduler.getProblemData().getCoursesToBeScheduled().remove(selectedcourses.get(i).getCourseID());
		}
		tablecourses.removeAll(selectedcourses);
		this.coursesSchdeduledTable.getSelectionModel().clearSelection();
	}

	public void onInputFilePathButtonClick() {

		examScheduler = null;
		examScheduler = new ExamScheduler(this.examType);

		this.coursesSchdeduledTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.coursesNotScheduledTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("Microsoft Excel Documents", "*.xlsx"));
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		File selectedFile = fc.showOpenDialog(null);

		if (selectedFile != null) {
			AlertBox.displayProgress("Reading", "Please Wait the input file is being read!");

			this.coursesSchdeduledTable.getItems().clear();
			this.coursesNotScheduledTable.getItems().clear();

			//read input file
			examScheduler.setInputFilePath(selectedFile.getAbsolutePath());

			String message = examScheduler.readInputFile();

			if (message != null) {
				AlertBox.stopProgress();
				AlertBox.display("Error", message, "ok");
				return;
			}
			this.inputFilePathLabel.setText(selectedFile.getAbsolutePath());
			Tooltip tp = new Tooltip(selectedFile.getAbsolutePath());
			inputFilePath = selectedFile.getAbsolutePath();
			this.inputFilePathLabel.setTooltip(tp);

			ObservableList<course> toBeScheduledcourses = FXCollections.observableArrayList();
			ObservableList<course> notToBeScheduledcourses = FXCollections.observableArrayList();
			for (course c : examScheduler.getProblemData().getAllCourses().values()) {
				if (c.getToBeScheduled() == true){
					toBeScheduledcourses.add(c);

				}

				else {
					notToBeScheduledcourses.add(c);
				}
			}

			this.courseCodeColumnForScheduledTable.setCellValueFactory(new PropertyValueFactory("courseID"));
			this.courseNameColumnForScheduledTable.setCellValueFactory(new PropertyValueFactory("courseName"));
			this.courseCodeColumnForScheduledTable.setCellFactory(WRAPPING_CELL_FACTORY);
			this.courseNameColumnForScheduledTable.setCellFactory(WRAPPING_CELL_FACTORY);
			this.courseCodeColumnForUnscheduledTable.setCellValueFactory(new PropertyValueFactory("courseID"));
			this.courseNameColumnForUnscheduledTable.setCellValueFactory(new PropertyValueFactory("courseName"));
			this.courseCodeColumnForUnscheduledTable.setCellFactory(WRAPPING_CELL_FACTORY);
			this.courseNameColumnForUnscheduledTable.setCellFactory(WRAPPING_CELL_FACTORY);
			this.coursesSchdeduledTable.getItems().addAll(toBeScheduledcourses);
			this.coursesNotScheduledTable.getItems().addAll(notToBeScheduledcourses);
			
			AlertBox.stopProgress();

		}

	}

	public void setExamType(String examType){
		this.examType = examType;
	}

	public String getExamType(String examType){
		return this.examType;
	}

	public void onOutputFilePathButtonClick() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(new File(System.getProperty("user.dir")));
		File selectedDirectory = dc.showDialog(null);

		if (selectedDirectory != null) {
			this.outputDirectoryPathLabel.setText(selectedDirectory.getAbsolutePath());

			Tooltip tp = new Tooltip(selectedDirectory.getAbsolutePath());
			this.outputDirectoryPathLabel.setTooltip(tp);

			outputDirectoryPath = selectedDirectory.getAbsolutePath();
			examScheduler.setOutputDirectoryPath(outputDirectoryPath);

		}

	}

	public void onGenerateButtonClick() {
		if (this.inputFilePath == null || this.outputDirectoryPath == null) {
			AlertBox.display("Error", "Please give all input/output paths first!", "ok");
			return;
		}

		StatsMessageBoxController messageBoxController = null;
		Stage window = new Stage();
		Parent root;
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/StatsMessageBox.fxml"));
			root = loader.load();
			window.setTitle("Creating Final Timetable");
			window.setScene(new Scene(root));
			window.initStyle(StageStyle.DECORATED);
			window.initModality(Modality.APPLICATION_MODAL);
			window.setMaxHeight(600);
			window.setMaxWidth(800);
			messageBoxController = loader.getController();
			messageBoxController.init("Please wait while the exam timetable and the report are being created. Press the stop button to stop.",
				"Initializing...");
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		examScheduler.setStatsMessageBoxController(messageBoxController);
		examScheduler.createThread();
		examScheduler.getThread().start();
		window.showAndWait();

		examScheduler.getThread().terminate();
		try {
			examScheduler.getThread().join();
		}
		catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		AlertBox.displayProgress("", "Please wait while the exam timetable are being exported.");
		ExamExporter exporter = null;

		exporter = new FinalExamExporter(this.examScheduler.getProblemData(), this.examScheduler.getBestSchedule(), this.outputDirectoryPath + "\\final date sheet.xlsx");

		String message = exporter.exportDateSheetToExcel();


		if (message != null) {
			AlertBox.stopProgress();
			AlertBox.display("Error", message, "ok");
			return;

		}
		AlertBox.stopProgress();
		AlertBox.display("Success", "Exam Timetable exported Succesfully!", "ok");



	}

	// to make text inside the tables wrappable.
	public static final Callback<TableColumn<course, String>, TableCell<course, String>> WRAPPING_CELL_FACTORY = new Callback<TableColumn<course, String>, TableCell<course, String>>() {
		@Override
		public TableCell<course, String> call(TableColumn<course, String> param) {
			TableCell<course, String> tableCell = new TableCell<course, String>() {

				@Override
				protected void updateItem(String item, boolean empty) {
					if (item == getItem())
						return;

					super.updateItem(item, empty);

					if (item == null) {
						super.setText(null);
						super.setGraphic(null);
					}
					else {
						super.setText(null);
						Label l = new Label(item);
						l.setWrapText(true);
						VBox box = new VBox(l);
						l.heightProperty().addListener((observable, oldValue, newValue) -> {
							box.setPrefHeight(newValue.doubleValue() + 7);
							Platform.runLater(() -> this.getTableRow().requestLayout());
						});
						super.setGraphic(box);
					}
				}
			};
			
			return tableCell;
		}
	};
}
