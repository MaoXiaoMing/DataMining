package question1;

import java.util.*;

import common.*;

public class FuzzyCMeans {

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
	
	public static void intialFuzzyPartition(List<Record> recordList, double[][] centroids,
			double[][] fuzzyPartition){
		updateFuzzyPartition(recordList, centroids, fuzzyPartition);
	}
	
	public static boolean convergence(double[][] last, double[][] next){
		return changeScope(last, next)>0.0000000000001? false:true;
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
	
	public static void updateCentroids(List<Record> recordList, double[][] centroids,
			double[][] fuzzyPartition){
		double[][] sum = new double[2][2];
		double[] weight = new double[2];
		for(int i=0; i<recordList.size(); i++){
			Record re = recordList.get(i);
			sum[0][0] += re.getAttr()[0] * Math.pow(fuzzyPartition[i][0], 2);
			sum[0][1] += re.getAttr()[1] * Math.pow(fuzzyPartition[i][0], 2);
			weight[0] += Math.pow(fuzzyPartition[i][0], 2);
			sum[1][0] += re.getAttr()[0] * Math.pow(fuzzyPartition[i][1], 2);
			sum[1][1] += re.getAttr()[1] * Math.pow(fuzzyPartition[i][1], 2);
			weight[1] += Math.pow(fuzzyPartition[i][1], 2);
		}
		centroids[0][0] = sum[0][0] / weight[0];
		centroids[0][1] = sum[0][1] / weight[0];
		centroids[1][0] = sum[1][0] / weight[1];
		centroids[1][1] = sum[1][1] / weight[1];
	}
	
	public static void updateFuzzyPartition(List<Record> recordList, double[][] centroids,
			double[][] fuzzyPartition){
		double[] distance = new double[2];
		for(int i=0; i<recordList.size(); i++){
			Record re = recordList.get(i);
			distance[0] = distance(re.getAttr(), centroids[0]);
			distance[1] = distance(re.getAttr(), centroids[1]);
			fuzzyPartition[i][0] = (1/distance[0]) / ((1/distance[0]) + (1/distance[1]));
			fuzzyPartition[i][1] = (1/distance[1]) / ((1/distance[0]) + (1/distance[1]));
		}
	}
	
	
	public static int allocation(double[] attr, double[][] centroids){
		return distance(attr, centroids[0])<distance(attr, centroids[1])? 0:1;
	}
	
	public static double distance(double[] attr, double[] centroid){
		return Math.pow(attr[0]-centroid[0], 2)+Math.pow(attr[1]-centroid[1], 2);
//		return Math.sqrt(Math.pow(attr[0]-centroid[0], 2)+Math.pow(attr[1]-centroid[1], 2));
	}
	
	public static void arrayCopy(double[][] last, double[][] next){
		for(int i=0; i<CENTROIDS_NUM; i++){
			for(int j=0; j<ATTRIBUTE_NUM; j++){
				last[i][j] = next[i][j];
			}
		}
	}
	
	public static void check(List<Record> recordList, double[][] centroids){
		int[][] count = new int[2][2];
		int[] result = new int[recordList.size()];
		int[] l = new int[recordList.size()];
		
		List<Record> cluster1 = new ArrayList<Record>();
		List<Record> cluster2 = new ArrayList<Record>();
		for(int i=0; i<recordList.size(); i++){
			Record re = recordList.get(i);
			l[i] = re.getLabel();
			if(distance(re.getAttr(), centroids[0]) > distance(re.getAttr(), centroids[1])){
				result[i] = 0;
				cluster1.add(re);
				if(re.getLabel() == 0) count[0][0]++;
				else count[0][1]++;
			}
			else{
				result[i] = 1;
				cluster2.add(re);
				if(re.getLabel() == 0) count[1][0]++;
				else count[1][1]++;
			} 
		}
		int[] label = new int[2];
		judgeClusterType(cluster1, cluster2, count, label);
		ComFun.NMI(result, l);
		ComFun.ARI(result, l);
	}
	
	public static void judgeClusterType(List<Record> cluster1, List<Record> cluster2, 
			int[][] count, int[] label){
		double[] t = new double[4];
		int size = cluster1.size()+cluster2.size();
		t[0] = (double)count[0][0] / size;
		t[1] = (double)count[0][1] / size;
		t[2] = (double)count[1][0] / size;
		t[3] = (double)count[1][1] / size;
		int index = maxRatio(t);
		double accuracy = 0d;
		if(index==0 || index==3){
			accuracy =  (double)(count[0][0]+count[1][1]) / size;
		}else{
			accuracy =  (double)(count[0][1]+count[1][0]) / size;
		}
		System.out.println(accuracy);
	}
	
	public static int maxRatio(double[] t){
		int index = 0;
		double max = Double.NEGATIVE_INFINITY;
		for(int i=0; i<t.length; i++){
			if(t[i] > max){
				index = i;
				max = t[i];
			}
		}
		return index;
	}
	
	public static void main(String[] args) {
		List<Record> recordList = new ArrayList<Record>();
		ComFun.getInfo(recordList);
		
		double[][] centroids = new double[CENTROIDS_NUM][ATTRIBUTE_NUM];
		intialCentroid(centroids);
		
		double[][] fuzzyPartition = new double[recordList.size()][CENTROIDS_NUM];
		intialFuzzyPartition(recordList, centroids, fuzzyPartition);
		
		double[][] last = new double[CENTROIDS_NUM][ATTRIBUTE_NUM];
		
		int conNum = 0;
		while(!convergence(last, centroids)){
			arrayCopy(last, centroids);
			updateCentroids(recordList, centroids, fuzzyPartition);
			updateFuzzyPartition(recordList, centroids, fuzzyPartition);
			ComFun.pln(conNum+1+"\t"+centroids[0][0]+"\t"+centroids[0][1]+"\t"+
					   centroids[1][0]+"\t"+centroids[1][1]);
			conNum++;
		}
		check(recordList, centroids);		
	}
}
