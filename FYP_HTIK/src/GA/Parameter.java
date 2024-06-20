package GA;
import java.util.concurrent.locks.ReentrantLock;

public class Parameter{
	
	public static int population_Size = 100;
	public static double mRate = 0.03;
	public static int tournament_Size = 5;
	public static int childrenPerIteration = 1;
	
	
	public static ReentrantLock mutex = new ReentrantLock();
	public static ReentrantLock exportScheduleMutex= new ReentrantLock();
	public static boolean parameterValuesChanged=false;

}