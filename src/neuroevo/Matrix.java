package neuroevo;

import java.util.ArrayList;
import java.util.Arrays;

public class Matrix {
	
	Double[][] matrix;
	
	/*
	 * Construct a matrix with specific rows and columns
	 */
	public Matrix(ArrayList<Double> list, int row, int col){
		matrix = new Double[row][col];
		for(int i = 0;i < row;i ++){
			ArrayList<Double> sub_list = new ArrayList<Double>(list.subList(i*col, (i + 1)*col));
			matrix[i] = sub_list.toArray(new Double[col]);
		}
	}
	
	/*
	 * Construct a matrix with only one row (a row vector)
	 */
	public Matrix(ArrayList<Double> list){
		matrix = new Double[1][list.size()];
		matrix[0] = list.toArray(new Double[list.size()]);
	}
	
	/*
	 * Construct a matrix with a 2D array
	 */
	public Matrix(Double[][] array){
		this.matrix = array;
	}
	
	public void Plus(Matrix M) {
		Double[][] input_matrix = M.getMatrix();
		int n = input_matrix.length;
		int m = input_matrix[0].length;
		if (n != matrix.length || m != matrix[0].length) {
			System.out.println("fail to add, dimension mismatch");
		} else {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					matrix[i][j] = matrix[i][j] + input_matrix[i][j];
				}
			}
		}
	}
	
	public void Minus(Matrix M) {
		Double[][] input_matrix = M.getMatrix();
		int n = input_matrix.length;
		int m = input_matrix[0].length;
		if (n != matrix.length || m != matrix[0].length) {
			System.out.println("fail to minus, dimension mismatch");
		} else {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					matrix[i][j] = matrix[i][j] - input_matrix[i][j];
				}
			}
		}
	}
	
	public void Times(Matrix M) {
		Double[][] input_matrix = M.getMatrix();
		int n = input_matrix.length;
		int m = input_matrix[0].length;
		Double[][] output_matrix = new Double[matrix.length][m];
		if (n != matrix[0].length) {
			System.out.println("fail to multiply, dimension mismatch");
		} else {
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < m; j++) {
					output_matrix[i][j] = 0.0;
					for (int k = 0; k < n; k++) {
						System.out.println(output_matrix[i][j]);
					}
				}
			}
		}
		matrix = output_matrix;
	}
	
	public Double[][] getMatrix(){
		return this.matrix;
	}
	
	public void setMatrix(Double[][] matrix){
		this.matrix = matrix;
	}
	
	public ArrayList<Double> getWeights(){
		ArrayList<Double> weights = new ArrayList<Double>();
		for(int i = 0; i < matrix.length; i ++){
			weights.addAll(Arrays.asList(matrix[i]));
		}
		return weights;
	}
	
}
