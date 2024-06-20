package GA;
import java.util.Comparator;

public class ChromoCompare implements Comparator<Chromosome>{
    public static ChromoCompare com = new ChromoCompare();

    public int compare(Chromosome c1, Chromosome c2){
        int c1Total = c1.getTotal();
        int c2Total = c2.getTotal();

        if (c1Total < c2Total){
            return -1;
        }

        if (c1Total > c2Total){
            return 1;
        }

        return 0;
    }
}
