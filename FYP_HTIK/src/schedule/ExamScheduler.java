package schedule;
import java.util.ArrayList;
import java.util.HashMap;
import controllers.StatsMessageBoxController;
import readInput.*;
import GA.*;
import javafx.application.Platform;


public class ExamScheduler {

	GeneticAlgorithm ga;
	private GThread thread;
	
	public class GThread extends Thread{
		@Override
		public void run() {
		  
		   System.out.println("Exam Scheduler Thread started.");
		   runScheduler();
		   System.out.println("Exam Scheduler Thread stopped.");
		}


		public void terminate(){
			terminate = true;
		}

	}

	private int itrCounter;
	private boolean terminate = false;


	ErrorHandling problemData;

	public String inputFilePath;
	public String outputDirectoryPath;
	public String examType;
	Chromosome bestSchedule;

	StatsMessageBoxController statsBoxController;
	public ExamScheduler(String examType){
		ga = null;
		this.inputFilePath = null;
		this.outputDirectoryPath = null;
		this.thread = null;
		this.bestSchedule = null;
		this.terminate = false;
		this.statsBoxController = null;
		this.examType = examType;
	}

	public void createThread(){
		this.thread = new GThread();
	}

	public GThread getThread(){
		return this.thread;
	}

	public void setOutputDirectoryPath(String outputDirectoryPath){
		this.outputDirectoryPath = outputDirectoryPath;
	}

	public void setInputFilePath(String inputFilePath){
		this.inputFilePath = inputFilePath;
	}

	public String readInputFile(){
		this.problemData = new ErrorHandling(this.inputFilePath, examType);
		String status = problemData.readDataFromExcelFile();

		return status;
	}

	public void runScheduler(){
		ga = new evaluateChromo(this.problemData);

		ga.initPopulation(Parameter.population_Size);
		int population_Size = Parameter.population_Size;
		int childrenPerItr = Parameter.childrenPerIteration;
		int tournament_Size = Parameter.tournament_Size;
		double mRate = Parameter.mRate;


		itrCounter = 0;
		this.terminate = false;

		this.statsBoxController.enableAllInputs();
		ArrayList<Chromosome> newChildren = new ArrayList<Chromosome>();

		do{
			Parameter.mutex.lock();
			if (Parameter.parameterValuesChanged == true){
				childrenPerItr = Parameter.childrenPerIteration;
				tournament_Size = Parameter.tournament_Size;
				mRate = Parameter.mRate;

				if (population_Size < Parameter.population_Size){
					ga.increasePopulationSize(Parameter.population_Size - population_Size);
					population_Size = Parameter.population_Size;
					System.out.println("New Population Size is :" + Parameter.population_Size);
				}
				else if (population_Size > Parameter.population_Size){
					ga.decreasePopulationSize(population_Size - Parameter.population_Size);
					population_Size = Parameter.population_Size;
					System.out.println("New Population Size is :" + Parameter.population_Size);
				}

				this.statsBoxController.enableAllInputs();
				Platform.runLater(() -> {
					this.statsBoxController.setMessageLabelText("Please wait while the exam timetable and the report are being created. Press the stop button to stop.");
				});

				Parameter.parameterValuesChanged = false;
			}

			Parameter.mutex.unlock();

			newChildren.clear();

			for (int j = 0; j < childrenPerItr; j++){
				Chromosome p1 = ga.tournament(tournament_Size);
				Chromosome p2 = null;
				do{
					p2 = ga.tournament(tournament_Size);

				} while (p2 == p1);

				newChildren.add(ga.CrossOver(p1, p2));
				ga.doMutation(newChildren.get(j), mRate);
				ga.evaluateChromo(newChildren.get(j));
			}

			ga.sortAccordingToFitness(newChildren);
			ga.insertNewChromsomesInPopulationIfTheyAreFitter(newChildren);

			itrCounter++;

			Parameter.exportScheduleMutex.lock();
			this.bestSchedule = ga.best();
			Parameter.exportScheduleMutex.unlock();
			Platform.runLater(() -> {
				this.statsBoxController.setStats("Iteration: " + itrCounter + "\nPopulation Fitness: " + ga.getTotalPopulationFitness() + "\nPopulationUnFitness: " + ga.getTotalPopulationUnfitness() + "\nPopulation Total: " + (ga.getTotalPopulationFitness() + ga.getTotalPopulationUnfitness()) + "\nBest Individual Fitness: " + this.bestSchedule.getFitnessValue() + "\nBest Individual Unfitness: " + this.bestSchedule.getUnfitnessValue() + "\nBest Individual Total: " + (this.bestSchedule.getFitnessValue() + this.bestSchedule.getUnfitnessValue()));
			});

		} while (terminate == false);

	}

	public String getExamType(){
		return this.examType;
	}

	public Chromosome getBestSchedule(){
		return this.bestSchedule;
	}

	public void setStatsMessageBoxController(StatsMessageBoxController controller){
		this.statsBoxController = controller;
	}

	public ErrorHandling getProblemData(){
		return this.problemData;
	}


	public Day[] getDayWiseSchedule(){
		return this.bestSchedule.getDayWiseSchedule();
	}

	public HashMap<String, CSchedule> getCourseWiseSchedule(){
		return this.bestSchedule.getCourseWiseSchedule();
	}

}
