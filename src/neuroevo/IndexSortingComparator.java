package neuroevo;

import java.util.Comparator;
import java.util.ArrayList;

//As taken from http://stackoverflow.com/questions/4859261/get-the-indices-of-an-array-after-sorting
public class IndexSortingComparator implements Comparator<Integer>{
    private final ArrayList<Double> scores;

    public IndexSortingComparator(ArrayList<Double> scores)
    {
        this.scores = scores;
    }

    public Integer[] createIndexArray()
    {
    	Integer[] indexes = new Integer[scores.size()];
        for (int i = 0; i < scores.size(); i++)
        {
        	indexes[i]=i;
        }
        return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2)
    {
        return scores.get(index2).compareTo(scores.get(index1));
    }
	
}
