package question1;

import java.util.*;

import common.*;

public class LinkageMethods {
	
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
	
	public static void displayDistance(double[][] distanceMatrix){
		for(int i=0; i<distanceMatrix.length; i++){
			for(int j=0; j<distanceMatrix.length; j++){
				System.out.print(String.format("%.4f", distanceMatrix[i][j])+" ");
			}
			System.out.println();
		}
	}
	
	public static void intialClusters(List<Record> recordList, List<List<Record>> clusters){
		for(int i=0; i<recordList.size(); i++){
			List<Record> listRecord = new ArrayList<Record>();
			listRecord.add(recordList.get(i));
			clusters.add(listRecord);
		}
	}
	
	public static void agglomerativeClustering(List<Record> recordList, List<List<Record>> clusters, 
			double[][] distanceMatrix){
		int aggloNum = recordList.size()-1;
		
		while(aggloNum > 1){
			agglomerative(recordList, clusters, distanceMatrix);
			aggloNum--;
		}
	}
	
	public static void agglomerative(List<Record> recordList, List<List<Record>> clusters,
			double[][] distanceMatrix){
		int[] mark = new int[2];
		findMinDistance(recordList, clusters, distanceMatrix, mark);
//		debugCombine(clusters, mark);
		combineCluster(clusters, mark);
	}
	
	public static void debugCombine(List<List<Record>> clusters, int[] mark){
		System.out.println();
		System.out.println("combine info======================");
		System.out.println("first cluster");
		List<Record> listRecord = clusters.get(mark[0]);
		System.out.println("first cluster size:"+listRecord.size());
		for(int i=0; i<listRecord.size(); i++){
			System.out.println(listRecord.get(i).getId());
		}
		System.out.println("second cluster");
		listRecord = clusters.get(mark[1]);
		System.out.println("second cluster size:"+listRecord.size());
		for(int i=0; i<listRecord.size(); i++){
			System.out.println(listRecord.get(i).getId());
		}
		System.out.println();
	}
	
	public static void combineCluster(List<List<Record>> clusters, int[] mark){
		clusters.get(mark[0]).addAll(clusters.get(mark[1]));
		clusters.remove(mark[1]);
	}
	
	public static void findMinDistance(List<Record> recordList, List<List<Record>> clusters,
			double[][] distanceMatrix, int[] mark){
		double distance = Double.POSITIVE_INFINITY;
		double tmp = 0d;
		for(int i=0; i<clusters.size(); i++){
			for(int j=i+1; j<clusters.size(); j++){
				//little trick？
				if((tmp = clusterDistance(clusters.get(i), clusters.get(j), distanceMatrix, 2)) < distance){
					distance = tmp;
					mark[0] = i;
					mark[1] = j;
				}
			}
		}
	}

	public static double clusterDistance(List<Record> cluster1, List<Record> cluster2,
			double[][] distanceMatrix, int distanceType){
		double distance = 0d;
		switch(distanceType){
		case 0:
			distance = single(cluster1, cluster2, distanceMatrix);
			break;
		case 1:
			distance = average(cluster1, cluster2, distanceMatrix);
			break;
		case 2:
			distance = complete(cluster1, cluster2, distanceMatrix);
			break;
		case 3:
			distance = ward(cluster1, cluster2, distanceMatrix);
			break;
		}
		return distance;
	}
	
	public static double single(List<Record> cluster1, List<Record> cluster2, double[][] distanceMatrix){
		//对离群点or噪音影响很敏感			0.663	0.513
		double distance = Double.POSITIVE_INFINITY;
		for(int i=0; i<cluster1.size(); i++){
			for(int j=0; j<cluster2.size(); j++){
				if(distanceMatrix[cluster1.get(i).getId()-1][cluster2.get(j).getId()-1] < distance){
					distance = distanceMatrix[cluster1.get(i).getId()-1][cluster2.get(j).getId()-1];
				}
			}
		}
		return distance;
	}
	
	public static double average(List<Record> cluster1, List<Record> cluster2, double[][] distanceMatrix){
		double distance = 0d;
		for(int i=0; i<cluster1.size(); i++){
			for(int j=0; j<cluster2.size(); j++){
				distance += distanceMatrix[cluster1.get(i).getId()-1][cluster2.get(j).getId()-1];
			}
		}
		return distance/(cluster1.size()*cluster2.size());
	}
	
	public static double complete(List<Record> cluster1, List<Record> cluster2, double[][] distanceMatrix){
		double distance = Double.NEGATIVE_INFINITY;
		for(int i=0; i<cluster1.size(); i++){
			for(int j=0; j<cluster2.size(); j++){
				if(distanceMatrix[cluster1.get(i).getId()][cluster2.get(j).getId()] > distance){
					distance = distanceMatrix[cluster1.get(i).getId()][cluster2.get(j).getId()];
				}
			}
		}
		return distance;
	}
	
	public static double ward(List<Record> cluster1, List<Record> cluster2, double[][] distanceMatrix){
		double distance = 0d;
		
		return distance;
	}

	public static void debugPoint(Record re){
		System.out.println(re.getId());
		System.out.println(re.getAttr()[0]+"\t"+re.getAttr()[1]);
		System.out.println(re.getLabel());
	}
	
	public static void check(List<Record> recordList, List<List<Record>> clusters){
		//stupid me........stupid code............
		//考虑两个簇的样本数来决定标号
		List<Record> cluster = clusters.get(0);
		int[][] label = new int[2][2];
		int[] result = new int[recordList.size()];
		int[] l = new int[recordList.size()];
		
		for(int i=0; i<cluster.size(); i++){
			Record re = cluster.get(i);
			l[cluster.get(i).getId()] = recordList.get(cluster.get(i).getId()).getLabel();
			result[cluster.get(i).getId()] = 0; 
			if(re.getLabel() == 0) label[0][0]++;
			else label[0][1]++;
		}
		
		cluster = clusters.get(1);
		for(int i=0; i<cluster.size(); i++){
			Record re = cluster.get(i);
			l[cluster.get(i).getId()] = recordList.get(cluster.get(i).getId()).getLabel();
			result[cluster.get(i).getId()] = 1; 
			if(re.getLabel() == 0) label[1][0]++;
			else label[1][1]++;
		}
		
		double[] t = new double[4];
/*		t[0] = ((double)label[0][0] / (label[0][0]+label[0][1]));
		t[1] = 1-t[0];
		t[2] = ((double)label[1][0] / (label[0][0]+label[1][1]));
		t[3] = 1-t[2];*/
		t[0] = ((double)label[0][0] / recordList.size());
		t[1] = ((double)label[0][1] / recordList.size());
		t[2] = ((double)label[1][0] / recordList.size());
		t[3] = ((double)label[1][1] / recordList.size());
		int index = maxRatio(t);
		double accuracy = 0d;
		if(index==0 || index==3){
			accuracy =  (double)(label[0][0]+label[1][1]) / recordList.size();
		}else{
			accuracy =  (double)(label[0][1]+label[1][0]) / recordList.size();
		}
		System.out.println(accuracy);
		ComFun.NMI(result, l);
		ComFun.ARI(result, l);
/*		if(index==0 || index==3){
			accuracy =  t[0]*recordList.size()/clusters.get(0).size() + t[3]*recordList.size()/clusters.get(1).size();
		}else{
			accuracy =  t[1]*recordList.size()/clusters.get(0).size() + t[2]*recordList.size()/clusters.get(1).size();
		}*/
/*		switch(index){
		case 0:
			accuracy =  t[0]*clusters.get(0).size()/recordList.size() + t[3]*clusters.get(1).size()/recordList.size();
			break;
		case 1:
			accuracy =  t[1]*clusters.get(0).size()/recordList.size() + t[2]*clusters.get(1).size()/recordList.size();
			break;
		}*/
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
		double[][] distanceMatrix = new double[recordList.size()][recordList.size()];
		intialDistanceMatrix(recordList, distanceMatrix);

		List<List<Record>> clusters = new ArrayList<List<Record>>();
		intialClusters(recordList, clusters);
		
		agglomerativeClustering(recordList, clusters, distanceMatrix);
//		System.out.println(clusters.size()+"\t"+clusters.get(0).size()+"\t"+clusters.get(1).size());
//		debugPoint(clusters.get(1).get(0));
		check(recordList, clusters);
	}

}
