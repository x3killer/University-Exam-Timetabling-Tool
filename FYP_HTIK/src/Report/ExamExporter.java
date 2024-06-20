package Report;
import java.util.HashSet;

import GA.Chromosome;
import readInput.ErrorHandling;

public abstract class ExamExporter {
	public abstract String exportDateSheetToExcel();

	protected String outputDirectoryPath;
	protected ErrorHandling problemData;
	protected Chromosome bestSchedule;

	public class ExamsScheduledInSlot{
		public HashSet<String> csExamsScheduled;
		public HashSet<String> iaExamsScheduled;
		public HashSet<String> ibExamsScheduled;
		public HashSet<String> cnExamsScheduled;
		public HashSet<String> ctExamsScheduled;

		public ExamsScheduledInSlot(){
			this.csExamsScheduled = new HashSet<String>();
			this.iaExamsScheduled = new HashSet<String>();
			this.ibExamsScheduled = new HashSet<String>();
			this.cnExamsScheduled = new HashSet<String>();
			this.ctExamsScheduled = new HashSet<String>();
		}

	}
	

	
	public class ExamsScheduledDays{
		public ExamsScheduledInSlot[]slots;
		
		public ExamsScheduledDays(int totalSlots){
			this.slots = new ExamsScheduledInSlot[totalSlots];
			for (int i = 0; i < totalSlots; i++){
				this.slots[i] = new ExamsScheduledInSlot();
			}
		}
	}

	public ExamsScheduledDays[]dateSheet;

	public ExamExporter(ErrorHandling problemData, Chromosome bestSchedule, String outputFileName){
		this.outputDirectoryPath = outputFileName;
		this.problemData = problemData;
		this.bestSchedule = bestSchedule;

		this.dateSheet = new ExamsScheduledDays[problemData.getTotalDays()];
		for (int i = 0; i < problemData.getTotalDays(); i++){
			this.dateSheet[i] = new ExamsScheduledDays(problemData.getTotalSlotsPerDay());
		}
	}

	public int findMaximumNumberOfExamsScheduledInAnySlot(int day){

		int max = this.bestSchedule.getDayWiseSchedule()[day].getSlots()[0].getCoursesScheduled().size();
		for (int i = 1; i < this.bestSchedule.getDayWiseSchedule()[day].getSlots().length; i++){
			if (max < this.bestSchedule.getDayWiseSchedule()[day].getSlots()[i].getCoursesScheduled().size())
				max = this.bestSchedule.getDayWiseSchedule()[day].getSlots()[i].getCoursesScheduled().size();

		}

		return max;
	}

	void checkDepartmentCourseOffer(){
		for (String courseID : this.problemData.getCoursesToBeScheduled()){
			int day = this.bestSchedule.getCourseWiseSchedule().get(courseID).getDay();
			int slot = this.bestSchedule.getCourseWiseSchedule().get(courseID).getSlot();

			for (String studentID : this.problemData.getAllCourses().get(courseID).getStudentID()){
				if (this.problemData.getAllStudents().get(studentID).getDegree().contains("CS")){
					this.dateSheet[day].slots[slot].csExamsScheduled.add(courseID);
				}

				if (this.problemData.getAllStudents().get(studentID).getDegree().contains("IA")){
					this.dateSheet[day].slots[slot].iaExamsScheduled.add(courseID);
				}

				if (this.problemData.getAllStudents().get(studentID).getDegree().contains("IB")){
					this.dateSheet[day].slots[slot].ibExamsScheduled.add(courseID);
				}

				if (this.problemData.getAllStudents().get(studentID).getDegree().contains("CN")){
					this.dateSheet[day].slots[slot].cnExamsScheduled.add(courseID);
				}
				
				if (this.problemData.getAllStudents().get(studentID).getDegree().contains("CT")){
					this.dateSheet[day].slots[slot].ctExamsScheduled.add(courseID);
				}
			}
		}
	}

	public int findMaximumNumberOfCNExamsScheduledInAnySlot(int day){
		int max = this.dateSheet[day].slots[0].cnExamsScheduled.size();
		for (int i = 1; i < this.dateSheet[day].slots.length; i++){
			if (max < this.dateSheet[day].slots[i].cnExamsScheduled.size())
				max = this.dateSheet[day].slots[i].cnExamsScheduled.size();
		}

		return max;
	}

	public int findMaximumNumberOfCSExamsScheduledInAnySlot(int day){

		int max = this.dateSheet[day].slots[0].csExamsScheduled.size();
		for (int i = 1; i < this.dateSheet[day].slots.length; i++){
			if (max < this.dateSheet[day].slots[i].csExamsScheduled.size())
				max = this.dateSheet[day].slots[i].csExamsScheduled.size();

		}
		return max;
	}

	public int findMaximumNumberOfIAExamsScheduledInAnySlot(int day){

		int max = this.dateSheet[day].slots[0].iaExamsScheduled.size();
		for (int i = 1; i < this.dateSheet[day].slots.length; i++){
			if (max < this.dateSheet[day].slots[i].iaExamsScheduled.size())
				max = this.dateSheet[day].slots[i].iaExamsScheduled.size();

		}
		return max;
	}


	public int findMaximumNumberOfIBExamsScheduledInAnySlot(int day){
		int max = this.dateSheet[day].slots[0].ibExamsScheduled.size();
		for (int i = 1; i < this.dateSheet[day].slots.length; i++){
			if (max < this.dateSheet[day].slots[i].ibExamsScheduled.size())
				max = this.dateSheet[day].slots[i].ibExamsScheduled.size();
		}
		return max;
	}
	
	public int findMaximumNumberOfCTExamsScheduledInAnySlot(int day){
		int max = this.dateSheet[day].slots[0].ctExamsScheduled.size();
		for (int i = 1; i < this.dateSheet[day].slots.length; i++){
			if (max < this.dateSheet[day].slots[i].ctExamsScheduled.size())
				max = this.dateSheet[day].slots[i].ctExamsScheduled.size();
		}
		return max;
	}

}


