package controllers;
import GA.Parameter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.Timer;
import java.util.TimerTask;


public class StatsMessageBoxController {
	public Label messageLabel;
	public String message;
	public Label statsLabel;

	public TextField populationSizeTextField;
	public TextField tournamentSizeTextField;
	public TextField childrenPerGenTextField;
	public TextField mutationRateTextField;


	int previousPopulationSize;
	int previousTournamentSize;
	int PreviousChildrenPerItr;
	double previousMutationRate;

	public Button stopButton;
	public Button updateParametersButton;
	
	public Timer timer;
    public long startTime;

	public void setStats(String statMessage){
		this.statsLabel.setText(statMessage);
	}


	public void onStopButtonClicked(){
		// Stop the timer
        timer.cancel();
        timer.purge();
		Stage stage = (Stage)this.stopButton.getScene().getWindow();
		stage.close();
	}

	public void setMessageLabelText(String message){

		this.messageLabel.setText(message);

	}

	public void onUpdateParametersButtonClicked(){
		this.messageLabel.setText("Please Wait while the algorithm's parameters are being updated!");

		this.disableAllInputs();
		Parameter.mutex.lock();
		Parameter.parameterValuesChanged = true;
		
		// Start the timer if it's not already running
        if (timer == null) {
            timer = new Timer();
            startTime = System.currentTimeMillis();
        }

		try
		{
			Parameter.population_Size = Integer.parseInt(this.populationSizeTextField.getText());
			if (Parameter.population_Size < 2)
				throw new Exception();

			this.previousPopulationSize = Parameter.population_Size;
		}
		catch (Exception e)
		{
			this.populationSizeTextField.setText(String.valueOf(this.previousPopulationSize));
			Parameter.population_Size = this.previousPopulationSize;
		}

		try
		{
			Parameter.tournament_Size = Integer.parseInt(this.tournamentSizeTextField.getText());
			if (Parameter.tournament_Size<1 || Parameter.tournament_Size>Parameter.population_Size)
				throw new Exception();

			this.previousTournamentSize = Parameter.tournament_Size;
		}
		catch (Exception e)
		{
			this.tournamentSizeTextField.setText(String.valueOf(this.previousTournamentSize));
			Parameter.tournament_Size = this.previousTournamentSize;
		}

		try
		{
			Parameter.childrenPerIteration = Integer.parseInt(this.childrenPerGenTextField.getText());
			if (Parameter.childrenPerIteration<1 || Parameter.childrenPerIteration>Parameter.population_Size)
				throw new Exception();

			this.PreviousChildrenPerItr = Parameter.childrenPerIteration;
		}
		catch (Exception e)
		{
			this.childrenPerGenTextField.setText(String.valueOf(this.PreviousChildrenPerItr));
			Parameter.childrenPerIteration = this.PreviousChildrenPerItr;
		}


		try
		{
			Parameter.mRate = Double.parseDouble(this.mutationRateTextField.getText());
			if (Parameter.mRate < 0 || Parameter.mRate>1)
				throw new Exception();

			this.previousMutationRate = Parameter.mRate;
		}
		catch (Exception e)
		{
			this.mutationRateTextField.setText(String.valueOf(this.previousMutationRate));
			Parameter.mRate = this.previousMutationRate;
		}

		Parameter.mutex.unlock();

	}

	@FXML
	public void initialize(){
		
		// Initialize the timer
        timer = new Timer();
        startTime = System.currentTimeMillis();
     // Start the timer
        startTimer();
        
		this.previousMutationRate = Parameter.mRate;
		this.previousPopulationSize = Parameter.population_Size;
		this.previousTournamentSize = Parameter.tournament_Size;
		this.PreviousChildrenPerItr = Parameter.childrenPerIteration;

		this.tournamentSizeTextField.setText(String.valueOf(this.previousTournamentSize));
		this.populationSizeTextField.setText(String.valueOf(this.previousPopulationSize));
		this.mutationRateTextField.setText(String.valueOf(this.previousMutationRate));
		this.childrenPerGenTextField.setText(String.valueOf(this.PreviousChildrenPerItr));
		this.disableAllInputs();
	}

	public void disableAllInputs(){

		this.updateParametersButton.setDisable(true);

		this.stopButton.setDisable(true);

		this.childrenPerGenTextField.setDisable(true);
		this.tournamentSizeTextField.setDisable(true);
		this.populationSizeTextField.setDisable(true);
		this.mutationRateTextField.setDisable(true);

	}
	public void init(String message, String initialStat){
		this.message = message;
		this.messageLabel.setText(message);
		this.statsLabel.setText(initialStat);
	}

	public void enableAllInputs(){
		this.updateParametersButton.setDisable(false);
		this.stopButton.setDisable(false);

		this.childrenPerGenTextField.setDisable(false);
		this.tournamentSizeTextField.setDisable(false);
		this.mutationRateTextField.setDisable(false);
		this.populationSizeTextField.setDisable(false);

	}
	
	private void startTimer() {
	    timer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	            // Calculate elapsed time
	            long elapsedTime = System.currentTimeMillis() - startTime;
	            long elapsedSeconds = elapsedTime / 1000;
	            long milliseconds = elapsedTime % 1000;

	            // Print elapsed time to console
	            System.out.println("Elapsed Time: " + elapsedSeconds + " seconds " + milliseconds + " milliseconds");
	        }
	    }, 0, 1000); // Update console every second
	}

}