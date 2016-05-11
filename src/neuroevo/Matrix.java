package neuroevo;

import java.util.ArrayList;

public class Matrix {
	
	Double[][] matrix;
	
	/*
	 * Construct a matrix with specific rows and columns
	 */
	public Matrix(ArrayList<Double> list, int row, int col){
		matrix = new Double[row][col];
		for(int i = 0;i < row;i ++){
			ArrayList<Double> sub_list = (ArrayList<Double>) list.subList(i*col, (i + 1)*col);
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
	
	public Matrix Times(Matrix M){
		Double[][] input_matrix = M.getMatrix();
		
		return null;
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
	
	public void mulMatrix(Matrix M) {
		Double[][] input_matrix = M.getMatrix();
		int n = input_matrix.length;
		int m = input_matrix[0].length;
		if (n != matrix[0].length) {
			System.out.println("fail to multiply, dimension mismatch");
		} else {
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < m; j++) {
					for (int k = 0; k < n; k++) {
						matrix[i][j] += (matrix[i][k] * input_matrix[k][j]);
					}
				}
			}
		}
	}
	
	public Double[][] getMatrix(){
		return this.matrix;
	}
	
	public void setMatrix(Double[][] matrix){
		this.matrix = matrix;
	}
	
}
