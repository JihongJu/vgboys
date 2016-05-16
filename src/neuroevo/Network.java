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
	private ArrayList<Double> weights;
	private ArrayList<Double> outputs;
	
	/* Relationships */
	public ArrayList<Network> parents;
	public ArrayList<Network> children;
	
	//Genetic object to produce new networks inside the 
	static private GenticsSNES geneticGenerator;
	
	public Network(int netid, ArrayList<Integer> layout, ArrayList<Double> weights) throws Exception {
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
			throw new Exception("Network layout mismatch: Weights do "
					+ "not match with Network's layout.\n");
		}
	}
	
	public Network() throws Exception {
		/*TODO 
		 * layout is an array of number of nodes for each layer from input layer, 
		 * hidden layers to output layer;
		 * weights is an array of flatten connection weights for full-connected networks.
		 * Example: a network with layout [2, 2, 1] has weights [w00, w01, w10, w11, w00, w01]
		 * where wji represents weights for connection from i-th node in the previous layer to
		 * j-th node in the next layer.
		*/
		weights=geneticGenerator.getNextNetwork();
		layout=geneticGenerator.getLayout();
		if(layoutCheck(layout, weights)) {
		this.netid = 0;//TODO set better value here (is this id even needed?)
		//this.layout = layout;
		this.inputDimension = layout.get(0);			// include bias node
		this.outputDimension = layout.get(layout.size() - 1);
		this.numHiddenLayers = layout.size() - 2;		// exclude input and output layer
		//this.weights = weights;
		}
		else {
			throw new Exception("Network layout mismatch: Weights do "
					+ "not match with Network's layout.\n");
		}
	}
	
	static public void setGenetics(GenticsSNES givenGenerator){
		geneticGenerator= givenGenerator;
		return;
	}
	
	public ArrayList<Double> fire(ArrayList<Double> inputs) throws Exception {
		/* A function fires the Network and returns the output given the inputs.
		 * Output is possibilities for each actions
		*/
		//first check if input and inputDimension match or not, 
		if(inputs.size() + 1 == inputDimension) {
			this.inputs = inputs;
			// add bias value
			this.inputs.add(1.0);
			
			// calculate outputs use parse weights
			ArrayList<Double> previous_layer = this.inputs;
			for (int layer_id = 1; layer_id < this.layout.size(); layer_id ++) {
				ArrayList<Double> current_layer = new ArrayList<Double>();
				for (int node_id = 0 ; node_id < this.layout.get(layer_id); node_id ++) {
					ArrayList<Double> current_weights = parseWeights(this.layout, this.weights, layer_id, node_id);
			
					double current_node = 0;
					if (previous_layer.size() == current_weights.size()) {
						for (int i = 0; i < previous_layer.size(); i++) {
							current_node += previous_layer.get(i) * current_weights.get(i);
						}
						current_layer.add(current_node);
					}
					else {
						throw new Exception("Network weights mismatch: Weights do"
								+ "not match with previous layer.\n");
					}
				}
				previous_layer = activation(current_layer);
			}
			outputs = previous_layer;
			if (outputs.size() == this.outputDimension) {
				return outputs;
			}
			else {
				throw new Exception("Output dimension mismatch: The dimension "
						+ "of outputs does not match with the Network.\n");
			}
		}
		else {
			throw new Exception("Input dimension mismatch: The dimension "
					+ "of inputs does not match with the Network.\n");
		}
		
	}
	
	private ArrayList<Double> activation(ArrayList<Double> values) {
		//A sigmoid activation function
		ArrayList<Double> activatedValues = new ArrayList<>();
		values.stream().map((v) -> 1 / (1 + Math.exp(-v))).forEach(v -> activatedValues.add(v));
		return activatedValues;
	}
	
	private boolean layoutCheck(ArrayList<Integer> layout, ArrayList<Double> weights) {
		//check if weights match with layout or not
		int total_weights = 0;
		for(int i = 0; i < layout.size() - 1; i ++){
			total_weights += layout.get(i) * layout.get(i + 1);
		}
		return total_weights == weights.size();
	}
	
	private ArrayList<Double> parseWeights(ArrayList<Integer> layout, ArrayList<Double> weights,
			int layer_id, int node_id) {
		// parse the flatten weights array and return a matrix
		int start = 0;
		int end = 0;
		// determine start and end based on layer_id and node_id
		for (int l = 0; l <= layer_id - 2; l++) {
			if (l >= 0) {
			start += layout.get(l) * layout.get(l + 1);
			}
		}
		if (layer_id > 0) {
			start += layout.get(layer_id - 1) * node_id;
		}
		end += start + layout.get(layer_id - 1);
		// parse the firing weights for node(layer_id,node_id)
		ArrayList<Double> node_weights = new ArrayList<Double>(weights.subList(start, end));
		
		return node_weights;
		
	}
	
	

	public Network mutation() throws Exception {
	//TODO move to genetics
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

		child = new Network(child_id, this.layout, child_weights);
		return child;
	}
	
}
