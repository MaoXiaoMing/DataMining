package question1;

import java.util.*;

import common.ComFun;
import common.Record;
import Jama.*;

public class SpectralClustering {

	public static void computeWeightMatrix(List<Record> recordList, double[][] W){
		for(int i=0; i<recordList.size(); i++){
			for(int j=i; j<recordList.size(); j++){
				W[i][j] = W[j][i] = similarity(recordList.get(i), recordList.get(j));
			}
		}
	}
	
	public static double similarity(Record re1, Record re2){
		double[] attr1 = re1.getAttr();
		double[] attr2 = re2.getAttr();
		double tmp = 0.0;
		double x_length = 0.0;
		double y_length = 0.0;
		for(int i=0; i<attr1.length; i++){
			tmp += attr1[i]*attr2[i];
			x_length += attr1[i]*attr1[i];
			y_length += attr2[i]*attr2[i];
		}
		tmp /= Math.sqrt(x_length*y_length);
		return tmp;
		
	}
	
	public static double distance(Record re1, Record re2){
		double[] attr1 = re1.getAttr();
		double[] attr2 = re2.getAttr();
		return Math.sqrt(Math.pow(attr1[0]-attr2[0], 2)+Math.pow(attr1[1]-attr2[1], 2));
	}
	
/*	public static void computeDiagonalMatrix(double[][] W, double[][] D){
		
		for(int i=0; i<D.length; i++){
			double sum = 0d;
			for(int j=0; j<D.length; j++){
				sum += W[j][i];
			}
			D[i][i] = sum;
		}
	}*/
	
	public static void computeDiagonalMatrix(double[][] W, double[][] D){
		for(int i=0; i<D.length; i++){
			for(int j=0; j<D.length; j++){
				D[i][i] += W[i][j];
			}
			D[i][i] = 1/Math.sqrt(D[i][i]);
		}
	}
	
/*	public static void computeMatrixL(double[][] D, double[][] W, double[][] L){
		for(int i=0; i<L.length; i++){
			for(int j=0; j<L.length; j++){
				L[i][j] = D[i][j] - W[i][j];
			}
		}
	}*/
	
	public static int[] findMaxValueIndex(Matrix d){
		double[][] array = d.getArray();
		int[] index = new int[2];
		double[] max = new double[2];
		max[0] = max[1] = Double.NEGATIVE_INFINITY;
		for(int i=0; i<d.getColumnDimension(); i++){
			if(array[i][i] > max[0]){
				max[1] = max[0];
				index[1] = index[0];
				max[0] = array[i][i];
				index[0] = i;
			}else if(array[i][i] > max[1]){
				max[1] = array[i][i];
				index[1] = i;
			}
		}
		return index;
	}
	
	public static void getColumn(Matrix v, int[] index, double[][] newData){
		double[][] d = v.getArray();
		for(int i=0; i<index.length; i++){
			for(int j=0; j<v.getRowDimension(); j++){
				newData[j][index[i]] = d[index[i]][j];
			}
		}
	}
	
	public static void computeAffinityMatrix(List<Record> recordList, double[][] W){
		for(int i=0; i<W.length; i++){
			for(int j=0; j<W.length; j++){
				if(i==j){
					W[i][j] = 0;
				}else{
					W[i][j] = Math.exp(-distance(recordList.get(i), recordList.get(j)));
				}
			}
		}
	}

	public static void normlization(double[][] newData){
		for(int i=0; i<newData.length; i++){
			double divisor = 0d;
			for(int j=0; j<newData[i].length; j++){
				divisor += Math.pow(newData[i][j], 2);
			}
			for(int j=0; j<2; j++){
				newData[i][j] /= Math.sqrt(divisor); 
			}
		}
	}
	
	public static List<Record> rebuildRecord(double[][] newData){
		List<Record> newRecord = new ArrayList<Record>();
		for(int i=0; i<newData.length; i++){
			double[] attr = new double[]{newData[i][0], newData[i][1]};
			newRecord.add(new Record(attr));
		}
		return newRecord;
	}
	
	public static void main(String[] args) {
		List<Record> recordList = new ArrayList<Record>(); 
		ComFun.getInfo(recordList);
		int dataNum = recordList.size();
		
		double[][] W = new double[dataNum][dataNum];
		double[][] D = new double[dataNum][dataNum];
		computeAffinityMatrix(recordList, W);
		computeDiagonalMatrix(W, D);
		
		Matrix matrixW = new Matrix(W);
		Matrix matrixD = new Matrix(D);
		
		Matrix A = matrixD.times(matrixW).times(matrixD);
		
		EigenvalueDecomposition eig = A.eig();
		Matrix v = eig.getV();
		Matrix d = eig.getD();
		
		int[] index = findMaxValueIndex(d);
		double[][] newData = new double[d.getRowDimension()][2];
		getColumn(v, index, newData);
		normlization(newData);
		
		List<Record> newList = rebuildRecord(newData);
		int[] result = KMeans.run(newList);
		int[] label = new int[recordList.size()];
		
		for(int i=0; i<recordList.size(); i++){
			label[i] = recordList.get(i).getLabel();
		}
		
		ComFun.pln(String.format("%.4f", KMeans.accuracy(result, label)));
		ComFun.NMI(result, label);
		ComFun.ARI(result, label);
		System.exit(-1);
/*		double[][] W = new double[dataNum][dataNum];
		double[][] D = new double[dataNum][dataNum];
		double[][] L = new double[dataNum][dataNum];
		
		computeWeightMatrix(recordList, W);
		computeDiagonalMatrix(W, D);
		computeMatrixL(D, W, L);
		
		Matrix m = new Matrix(D);
		
		SingularValueDecomposition svd = new SingularValueDecomposition(m);*/
/*		EigenvalueDecomposition eig = m.eig();
		Matrix v = eig.getV();
		Matrix d = eig.getD();
		
		int[] index = findMinValueIndex(d);
		double[][] newData = new double[d.getRowDimension()][2];
		getColumn(v, index, newData);
		System.out.println();*/
	}

}
