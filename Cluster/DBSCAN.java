package question1;

import java.util.*;

import common.*;

public class DBSCAN {
	
	public static void intialDistanceMatrix(List<Record> recordList, double[][] distance){
		for(int i=0; i<recordList.size(); i++){
			for(int j=i+1; j<recordList.size(); j++){
				distance[i][j] = distance[j][i] = distance(recordList, i, j);
			}
		}
	}
	
	public static double distance(List<Record> recordList, int i, int j){
		double[] attr1 = recordList.get(i).getAttr();
		double[] attr2 = recordList.get(j).getAttr();
		return Math.sqrt(Math.pow(attr1[0]-attr2[0], 2)+Math.pow(attr1[1]-attr2[1], 2));
	}
	
	public static void displayDistanceMatrix(double[][] distanceMatrix){
		for(int i=0; i<distanceMatrix.length; i++){
			for(int j=0; j<distanceMatrix.length; j++){
				System.out.print(String.format("%.4f", distanceMatrix[i][j])+" ");
			}
			System.out.println();
		}
	}
	
	public static double findEps(double[][] distanceMatrix){
		double max = Double.NEGATIVE_INFINITY;
		for(int i=0; i<distanceMatrix.length; i++){
			if(max < min(distanceMatrix[i], i))
				max = min(distanceMatrix[i], i);
		}
		return max;
	}
	
	public static double min(double[] distance, int index){
		double min = Double.POSITIVE_INFINITY;
		for(int i=0; i<distance.length; i++){
			if(min>distance[i] && i!=index)
				min = distance[i];
		}
		return min;
	}
	
	
	public static int findMinPts(double[][] distanceMatrix, double eps,
			List<List<Integer>> clusters, List<Integer> corePointSet){
		int minPts = 2;
		while(clusterNum(distanceMatrix, minPts, eps, clusters, corePointSet) != 2){
//			System.out.println(clusterNum(distanceMatrix, minPts, eps));
//			System.out.println(corePointSet.size());
			minPts++;
		}
		return minPts;
	}
	
	public static int clusterNum(double[][] distanceMatrix, int minPts, double eps,
			List<List<Integer>> clusters, List<Integer> corePointSet){
		corePointSet.clear();
		for(int i=0; i<distanceMatrix.length; i++){
			if(isCorePoint(distanceMatrix[i], minPts, eps)){
				corePointSet.add(i);
			}	
		}
		int num = calculateClusterNum(distanceMatrix, clusters, corePointSet, eps);
//		System.out.println(num);
		return num;
	}
	
	public static boolean isCorePoint(double[] distance, int minPts, double eps){
		int count = 0;
		for(int i=0; i<distance.length; i++){
			if(distance[i] < eps)
				count++;
		}
		if(count >= minPts) return true;
		else return false;
	}
	
	public static int calculateClusterNum(double[][] distanceMatrix,List<List<Integer>> clusters, List<Integer> corePointSet, double eps){
		clusters.clear();
		int num = 0;
		int size = corePointSet.size();
//		boolean[] visit = new boolean[size];
		for(int i=0; i<size; i++){
			int index = corePointSet.get(i);
			if(!isContains(clusters, index)){
				List<Integer> cluster = new ArrayList<Integer>();
				boolean[] visit = new boolean[distanceMatrix.length];
				linkCorePoint(distanceMatrix, index, corePointSet, eps, visit);
				for(int j=0; j<visit.length; j++){
					if(visit[j] == true){
						cluster.add(j);
					}
				}
				clusters.add(cluster);
				num++;
			}
		}
		return num;
			/*if(!visit[i]){
				visit[i] = true;
				List<Integer> cluster = new ArrayList<Integer>();
				int indexOut = corePointSet.get(i);
				cluster.add(indexOut);
				for(int j=0; j<size; j++){
					int indexIn = corePointSet.get(j);
					if(!visit[j] && distanceMatrix[indexOut][indexIn] < eps){
						visit[j] = true;
						cluster.add(indexIn);
					}
				}
				clusters.add(cluster);
				num++;
			}*/
	}
	
	public static void linkCorePoint(double[][] distanceMatrix, int index, List<Integer> corePointSet, double eps, boolean[] visit){
		boolean[][] linkMatrix = new boolean[distanceMatrix.length][distanceMatrix.length];
		for(int i=0; i<distanceMatrix.length; i++){
			for(int j=0; j<distanceMatrix.length; j++){
				if(distanceMatrix[i][j] < eps)
					linkMatrix[i][j] = true;
			}
		}
		
		DFS(linkMatrix, corePointSet, visit, index);
	}
	
	public static void DFS(boolean[][] linkMatrix, List<Integer> corePointSet, boolean[] visit, int index){
		visit[index] = true;
		for(int i=0; i<corePointSet.size(); i++){
			int t = corePointSet.get(i);
			if(linkMatrix[index][t] == true && !visit[t])
				DFS(linkMatrix, corePointSet, visit, t);
		}
	}
	
	public static boolean isContains(List<List<Integer>> clusters, int index){
		for(int i=0; i<clusters.size(); i++){
			List<Integer> cluster = clusters.get(i);
			if(cluster.contains(index)){
				return true;
			}
		}
		return false;
	}
	
/*	public static boolean contact(double[][] distanceMatrix, int index1, int index2, double eps){
		boolean bool = false;
		boolean[][] contactMatrix = new boolean[distanceMatrix.length][distanceMatrix.length];
		for(int i=0; i<distanceMatrix.length; i++){
			for(int j=0; j<distanceMatrix.length; j++){
				if(distanceMatrix[i][j] < eps)
					contactMatrix[i][j] = true;
			}
		}
		return DFS(contactMatrix, index1, index2);
	}*/
	
	public static void clustering(List<Record> recordList, List<List<Integer>> clusters){
		List<Integer> cluster1 = clusters.get(0);
		List<Integer> cluster2 = clusters.get(1);
		for(int i=0; i<recordList.size(); i++){
			if(!cluster1.contains(i) && !cluster2.contains(i)){
				if(allocation(recordList, i, cluster1, cluster2) == 1)
					cluster1.add(i);
				else cluster2.add(i);
			}
		}
	}
	
	public static int allocation(List<Record> recordList, int index, List<Integer> cluster1, List<Integer> cluster2){
		double[] min = new double[2];
		min[0] = min[1] = Double.POSITIVE_INFINITY;
		for(int i=0; i<cluster1.size(); i++){
			double distance = distance(recordList, cluster1.get(i), index);
			if(distance < min[0])
				min[0] = distance;
		}
		
		for(int i=0; i<cluster2.size(); i++){
			double distance = distance(recordList, cluster2.get(i), index);
			if(distance < min[1])
				min[1] = distance;
		}

		return min[0]<min[1]? 1:2;
	} 
	
	public static double accuracy(int[] result, int[] label){
		double count = 0d;
		for(int i=0; i<result.length; i++){
			if(result[i] == label[i]) count++;
		}
		return count/result.length>0.5? count/result.length : 1-count/result.length;
	}
	
	public static void check(List<Record> recordList, List<List<Integer>> clusters){
		int[][] count = new int[2][2];
		int[] result = new int[recordList.size()];
		int[] l = new int[recordList.size()];
		
		List<Integer> cluster = clusters.get(0);
		for(int i=0; i<cluster.size(); i++){
			l[cluster.get(i)] = recordList.get(cluster.get(i)).getLabel();
			result[cluster.get(i)] = 0; 
			if(recordList.get(i).getId() == 0) {
				count[0][0]++;
			}
			else{
				count[0][1]++;
			}
		}
		
		cluster = clusters.get(1);
		for(int i=0; i<cluster.size(); i++){
			l[cluster.get(i)] = recordList.get(cluster.get(i)).getLabel();
			result[cluster.get(i)] = 1; 
			if(recordList.get(i).getId() == 0) {
				count[1][0]++;
			}
			else{
				count[1][1]++;
			}
		}
		
		double[] t = new double[4];
		int size = recordList.size();
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
		ComFun.pln(String.format("%.4f", accuracy(result, l)));
		ComFun.NMI(result, l);
		ComFun.ARI(result, l);
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
		double eps = 0;
		int minPts = 1;
		
		List<Record> recordList = new ArrayList<Record>();
		ComFun.getInfo(recordList);
		
		double[][] distanceMatrix = new double[recordList.size()][recordList.size()];
		intialDistanceMatrix(recordList, distanceMatrix);
//		displayDistance(distanceMatrix);
		eps = findEps(distanceMatrix);
//		System.out.println(Eps);
		List<Integer> corePointSet = new ArrayList<Integer>();
		List<List<Integer>> clusters = new ArrayList<List<Integer>>();
		
		minPts = findMinPts(distanceMatrix, eps, clusters, corePointSet);
		System.out.println(eps+"\t"+minPts);
//		System.out.println(eps+"\t"+minPts+"\t"+corePointSet.size()+"\t"+clusters.size());
//		System.out.println(minPts);
		
		clustering(recordList, clusters);
		check(recordList, clusters);
	}
}
