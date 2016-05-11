package neuroevo;

import java.util.ArrayList;

public class Network {
	
	public int netid;
	
	/* Layout */
	public ArrayList<Integer> layout;
	public int inputDimension, outputDimension;
	public int numHiddenLayers;
	
	/* Inputs, Weights and Outputs*/
	private ArrayList<Double> inputs;
	private ArrayList<Double> weights;	//TODO extend to bitmap encoding
	private ArrayList<Integer> outputs;
	
	/* Relationships */
	public ArrayList<Network> parents;
	public ArrayList<Network> children;
	
	
	public Network(int netid, ArrayList<Integer> layout, ArrayList<Double> weights) {
		/*TODO 
		 * layout is an array of number of nodes for each layer from input layer, 
		 * hidden layers to output layer;
		 * weights is an array of flatten connection weights for full-connected networks.
		 * Example: a network with layout [2, 2, 1] has weights [w00, w01, w10, w11, w00, w01]
		 * where wji represents weights for connection from i-th node in the previous layer to
		 * j-th node in the next layer.
		*/
		if(layoutCheck(layout, weights)) {
		this.netid = netid;
		this.layout = layout;
		this.inputDimension = layout.get(0);			// include bias node
		this.outputDimension = layout.get(layout.size() - 1);
		this.numHiddenLayers = layout.size() - 2;		// exclude input and output layer
		this.weights = weights;
		}
		else {
			throw new IllegalArgumentException("Network layout mismatch: Weights do"
					+ "not match with Network's layout.\n");
		}
	}
	
	public ArrayList<Integer> fire(ArrayList<Double> inputs) {
		//TODO A function fires the Network and returns the output given the inputs.
		//first check if input and inputDimension match or not, 
		if(inputs.size() + 1 == inputDimension) {
			this.inputs = inputs;
			// add bias value
			this.inputs.add(1.0);
			
			// calculate outputs use parse weights
			
			
			return outputs;
		}
		else {
			throw new IllegalArgumentException("Input dimension mismatch: The dimension "
					+ "of inputs does not match with the Network.\n");
		}
		
	}
	
	private boolean layoutCheck(ArrayList<Integer> layout, ArrayList<Double> weights) {
		//check if weights match with layout or not
		int total_weights = 0;
		for(int i = 0; i < layout.size() - 2; i ++){
			total_weights += layout.get(i) * layout.get(i + 1);
		}
		return total_weights == weights.size();
	}
	
	private ArrayList<Double> parseWeights(ArrayList<Integer> layout, ArrayList<Double> weights,
			int layer_id, int node_id) {
		//TODO parse the flatten weights array and return a matrix
		int start = 0;
		int end = 0;
		//TODO determine start and end based on layer_id and node_id
		for (int l = 0; l <= layer_id - 2; l++) {
			start += layout.get(l) * layout.get(l + 1);
		}
		start += layout.get(layer_id - 1) * node_id;
		end += start + layout.get(layer_id - 1);

		ArrayList<Double> node_weights = (ArrayList<Double>) weights.subList(start + 1, end);
		
		return node_weights;
		
	}
	

	public Network crossover(ArrayList<Network> parents) {
	//TODO implement crossover
		Network child;
		//TODO initialize child_id and child_weights
		int child_id = -1;
		ArrayList<Double> child_weights = new ArrayList<Double>();
		
		child = new Network(child_id, layout, child_weights);
		return child;
	}
	

	public Network mutation() {
	//TODO implement mutation
		int type = (int)(Math.random() * 5) + 1;
		int num_of_weights = this.weights.size();
		int mutation_index = (int)(Math.random() * num_of_weights) + 1;
		switch(type){
			case 1 :
				// completely replace it with a new random value
				double new_weight = Math.random(); //scale?
				this.weights.set(mutation_index, new_weight);
				break;
			case 2 :
				// change the weight by some percentage.
				double persentage = Math.random() * 2 - 1;
				this.weights.set(mutation_index, this.weights.get(mutation_index) * persentage);
				break;
			case 3 :
				// add or subtract a random number between 0 and 1
				double change = Math.random() * 2 - 1;
				this.weights.set(mutation_index, this.weights.get(mutation_index) + change);
				break;
			case 4 :
				// Change the sign of a weight
				this.weights.set(mutation_index,this.weights.get(mutation_index) * (-1));
				break;
			case 5 :
				// swap weights on a single neuron
				int swap_index = (int)(Math.random() * num_of_weights) + 1;
				while(swap_index == mutation_index){
					swap_index = (int)(Math.random() * num_of_weights) + 1;
				}
				double temp = this.weights.get(swap_index);
				this.weights.set(swap_index, this.weights.get(mutation_index));
				this.weights.set(mutation_index, temp);
				break;
		}
		Network child;
		//TODO initialize child_id and child_weights
		int child_id = -1;
		ArrayList<Double> child_weights = new ArrayList<Double>();

		child = new Network(child_id, layout, child_weights);
		return child;
	}
	
}
