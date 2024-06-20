package GA;
import java.util.HashSet;

import readInput.DayDate;
import readInput.ErrorHandling;

public class evaluateChromo extends GeneticAlgorithm {

	public evaluateChromo(ErrorHandling problemData) {
		super(problemData);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void evaluateChromo(Chromosome c){
		Day[] days = c.getDayWiseSchedule();
		int unfitness = 0;
		int fitness = 0;

		//finding number of students who have more than 1 exam on same day
		int countOfCoursesToSchedule = this.problemData.getCoursesToBeScheduled().size();

		for (int i = 0; i < countOfCoursesToSchedule - 1; i++) {

			CSchedule cs1 = c.getCourseWiseSchedule().get(problemData.getCoursesToBeScheduled().get(i));
			for (int j = i + 1; j < countOfCoursesToSchedule; j++){
				CSchedule cs2 = c.getCourseWiseSchedule().get(problemData.getCoursesToBeScheduled().get(j));

				if (cs1.getDay() == cs2.getDay()){
					for (String stuID : problemData.getAllCourses().get(cs1.getCourseId()).getStudentID()){
						if (problemData.getAllCourses().get(cs2.getCourseId()).getStudentID().contains(stuID)){
							if (cs1.getSlot() == cs2.getSlot())
								unfitness += 4; //if the slot is the same too.

							else unfitness += 3; //if the slot is not the same.
						}
					}

				}


//				else if (DayDate.getNextDate(problemData.getExamDates().get(cs1.getDay())).equals(problemData.getExamDates().get(cs2.getDay())) || DayDate.getNextDate(problemData.getExamDates().get(cs2.getDay())).equals(problemData.getExamDates().get(cs1.getDay()))){
//					for (String stuID : problemData.getAllCourses().get(cs1.getCourseId()).getStudentID()){
//						if (problemData.getAllCourses().get(cs2.getCourseId()).getStudentID().contains(stuID)){
//							unfitness+=2;
//						}
//					}
//				}
//
		}
	}
		c.setUnfitnessValue(unfitness);
		c.setFitnessValue(fitness);
	}
}
