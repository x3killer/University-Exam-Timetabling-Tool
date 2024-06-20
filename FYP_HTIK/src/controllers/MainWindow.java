package controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainWindow {

	Button finalExamScheduleButton;
	Button finalExamReportGenerateButton;

	public void handleFinalExamsSheduleButtonClick() {
		Stage window = new Stage();
		Parent root;
		try {
			FXMLLoader load = new FXMLLoader(getClass().getClassLoader().getResource("views/FileChoose.fxml"));
			root = load.load();
			FileChooseController controller = load.getController();
			controller.setExamType("final");
			window.setTitle("Exam Scheduler");
			window.setScene(new Scene(root));
			window.initStyle(StageStyle.DECORATED);
			window.initModality(Modality.APPLICATION_MODAL);
			window.showAndWait();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void handleReadFinalExamScheduleAndGenerateReportButton(){
		Stage window = new Stage();
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getClassLoader().getResource("views/ReadFinalExamAndGenerateReportView.fxml"));
			window.setTitle("Exam  Scheduler");
			window.setScene(new Scene(root));
			window.initStyle(StageStyle.DECORATED);
			window.initModality(Modality.APPLICATION_MODAL);

			window.setMaxHeight(800);
			window.setMaxWidth(800);
			window.showAndWait();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void exportMYSQL(){
		Stage window = new Stage();
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getClassLoader().getResource("views/exportMysql.fxml"));
			window.setTitle("Export");
			window.setScene(new Scene(root));
			window.initStyle(StageStyle.DECORATED);
			window.initModality(Modality.APPLICATION_MODAL);

			window.setMaxHeight(800);
			window.setMaxWidth(800);
			window.showAndWait();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
