package neuroevo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class TestNet {

	public static void main(String[] args) throws Exception {
		// A test net
		int test_net_id = 0;
		ArrayList<Integer> test_layout = new ArrayList<Integer>(Arrays.asList(2, 3, 4, 5));
		ArrayList<Double> test_weights = new ArrayList<Double>(Arrays.asList(1.1, 1.2, 2.1, 2.2, 3.1, 3.2, 
				1.1, 1.2, 1.3, 2.1, 2.2, 2.3, 3.1, 3.2, 3.3, 4.1, 4.2, 4.3,  
				1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 5.4));
		Network test_net = new Network(test_net_id, test_layout, test_weights);
		
		ArrayList<Double> inputs = new ArrayList<Double>(Arrays.asList(3.0));
		ArrayList<Double> outputs = test_net.fire(inputs);
		for (double elem : outputs) {
			System.out.println(elem);
		}
	}

}
