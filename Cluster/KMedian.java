package question1;

import java.util.*;

import common.*;

public class KMedian {

	private static final int CENTROIDS_NUM = 2;
	private static final int ATTRIBUTE_NUM = 2;
	
	public static void intialCentroid(double[][] centroids){
		Random r = new Random();
		for(int i=0; i<CENTROIDS_NUM; i++){
			for(int j=0; j<ATTRIBUTE_NUM; j++){
				centroids[i][j] = Math.abs(r.nextDouble());
			}
		}
	}		
	
	public static boolean convergence(double[][] last, double[][] next){
		return changeScope(last, next)>0.0000001? false:true;
	}
	
	public static double changeScope(double[][] last, double[][] next){
		double sum = 0d;
		for(int i=0; i<CENTROIDS_NUM; i++){
			for(int j=0; j<ATTRIBUTE_NUM; j++){
				sum += Math.abs(last[i][j]-next[i][j]);
			}
		}
		return sum;
	}
	
	public static void updateCentroids(List<Record> recordList, double[][] centroids){
		List<double[]> set1 = new ArrayList<double[]>();
		List<double[]> set2 = new ArrayList<double[]>();
		for(int i=0; i<recordList.size(); i++){
			Record re = recordList.get(i);
			if(allocation(re.getAttr(), centroids) == 0){
				set1.add(re.getAttr());
			}else{
				set2.add(re.getAttr());
			}
		}
		centroids[0] = median(set1);
		centroids[1] = median(set2);
	}
	
	public static int allocation(double[] attr, double[][] centroids){
		return distance(attr, centroids[0])<distance(attr, centroids[1])? 0:1;
	}
	
	public static double distance(double[] attr, double[] centroid){
		//欧几里得距离
//		return Math.pow(attr[0]-centroid[0], 2)+Math.pow(attr[1]-centroid[1], 2);
		return Math.sqrt(Math.pow(attr[0]-centroid[0], 2)+Math.pow(attr[1]-centroid[1], 2));
	}
	
	public static double[] median(List<double[]> recordList){
		double[] median = new double[2];
		double[] value = new double[recordList.size()];
		for(int i=0; i<recordList.size(); i++){
			value[i] = recordList.get(i)[0];
		}
		quickSort(value, 0, value.length-1);
		median[0] = medianValue(value);
		
		for(int i=0; i<recordList.size(); i++){
			value[i] = recordList.get(i)[1];
		}
		quickSort(value, 0, value.length-1);
		median[1] = medianValue(value);
		
		return median;
	}
	
	public static double medianValue(double[] value){
		//more graceful?^_^
		return value[(value.length/2+(value.length+1)/2)/2];
	}
	
	public static void quickSort(double[] value, int low, int high){
		if(low < high){
			int pos = partition(value, low, high);
			quickSort(value, low, pos-1);
			quickSort(value, pos+1, high);
		}
	}
	
	public static int partition(double[] value, int low, int high){
		double key = value[low];
		int index = low;
		int pos = low+1;
		
		for( ; pos <= high; pos++){
			if(value[pos] < key){
				double tmp = value[++index];
				value[index] = value[pos];
				value[pos] = tmp;
			}
		}
		
		value[low] = value[index];
		value[index] = key;
		
		return index;
	}
	
	public static void arrayCopy(double[][] last, double[][] next){
		for(int i=0; i<CENTROIDS_NUM; i++){
			for(int j=0; j<ATTRIBUTE_NUM; j++){
				last[i][j] = next[i][j];
			}
		}
	}
	
	public static double accuracy(int[] result, int[] label){
		double count = 0d;
		for(int i=0; i<result.length; i++){
			if(result[i] == label[i]) count++;
		}
		return count/result.length>0.5? count/result.length : 1-count/result.length;
	}
	
	public static void main(String[] args) {
		List<Record> recordList = new ArrayList<Record>(); 
		ComFun.getInfo(recordList);
		
		double[][] centroids = new double[CENTROIDS_NUM][ATTRIBUTE_NUM];
		intialCentroid(centroids);
		ComFun.pln("0\t"+centroids[0][0]+"\t"+centroids[0][1]+"\t"+
				   centroids[1][0]+"\t"+centroids[1][1]);
		double[][] last = new double[CENTROIDS_NUM][ATTRIBUTE_NUM];
		int conNum = 0;
		while(!convergence(last, centroids)){
			arrayCopy(last, centroids);
			updateCentroids(recordList, centroids);
			ComFun.pln(conNum+1+"\t"+centroids[0][0]+"\t"+centroids[0][1]+"\t"+
					   centroids[1][0]+"\t"+centroids[1][1]);
			conNum++;
		}
		ComFun.pln("迭代次数\t"+conNum);
		
		int[] result = new int[recordList.size()];
		int[] label = new int[recordList.size()];
		for(int i=0; i<recordList.size(); i++){
			result[i] = allocation(recordList.get(i).getAttr(), centroids);
			label[i] = recordList.get(i).getLabel();
		}
		
		ComFun.pln(String.format("%.4f", accuracy(result, label)));
		ComFun.NMI(result, label);
		ComFun.ARI(result, label);
	}

}
