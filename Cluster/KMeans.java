package question1;

import java.util.*;

import common.*;


public class KMeans {

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
		System.out.println(changeScope(last, next));
		return changeScope(last, next)>0.00000000001? false:true;
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
		//采用批量更新，   增量更新确保不产生空簇(NaN)
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
		
		double[] sum = new double[ATTRIBUTE_NUM];
		for(int i=0; i<set1.size(); i++){
			sum[0] += set1.get(i)[0];
			sum[1] += set1.get(i)[1];
		}
		
		centroids[0][0] = sum[0]/set1.size();
		centroids[0][1] = sum[1]/set1.size();
	
		sum[0] = sum[1] = 0d;
		for(int i=0; i<set2.size(); i++){
			sum[0] += set2.get(i)[0];
			sum[1] += set2.get(i)[1];
		}
		
		centroids[1][0] = sum[0]/set2.size();
		centroids[1][1] = sum[1]/set2.size();
	}
	
	public static int allocation(double[] attr, double[][] centroids){
		return distance(attr, centroids[0])<distance(attr, centroids[1])? 0:1;
	}
	
	public static double distance(double[] attr, double[] centroid){
		//欧几里得距离
//		return Math.pow(attr[0]-centroid[0], 2)+Math.pow(attr[1]-centroid[1], 2);
		return Math.sqrt(Math.pow(attr[0]-centroid[0], 2)+Math.pow(attr[1]-centroid[1], 2));
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
		return ((double)count)/result.length>0.5? count/result.length : 1-count/result.length;
	}
	
	public static int[] run(List<Record> recordList){
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
//		int[] label = new int[recordList.size()];
		for(int i=0; i<recordList.size(); i++){
			result[i] = allocation(recordList.get(i).getAttr(), centroids);
//			label[i] = recordList.get(i).getLabel();
		}
		return result;
	}
	
	public static void main(String[] args) {
		List<Record> recordList = new ArrayList<Record>(); 
		ComFun.getInfo(recordList);

		int[] result = run(recordList);
		int[] label = new int[recordList.size()];
		
		for(int i=0; i<recordList.size(); i++){
			label[i] = recordList.get(i).getLabel();
		}
		
		ComFun.pln(String.format("%.4f", accuracy(result, label)));
		ComFun.NMI(result, label);
		ComFun.ARI(result, label);
	}
}
