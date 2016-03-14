package question1;

import java.util.*;

import common.*;

public class GaussianMixture {

	public static void intialParameter(List<Record> recordList, Parameter[] p){
		//初始化参数
		Random r = new Random();
//		p[0] = new Parameter();
//		p[1] = new Parameter();
//		p[0].setMeanVector(recordList.get(84).getAttr());
		p[0].setMeanVector(recordList.get(Math.abs(r.nextInt())%recordList.size()).getAttr());
		p[1].setMeanVector(recordList.get(Math.abs(r.nextInt())%recordList.size()).getAttr());
//		p[1].setMeanVector(recordList.get(15).getAttr());
		
/*		int[] count = new int[2];
		double[] attr;
		Record re;
		List<Record> cluster1 = new ArrayList<Record>();
		List<Record> cluster2 = new ArrayList<Record>();
		for(int i=0; i<recordList.size(); i++){
			re = recordList.get(i);
			attr = re.getAttr();
			if(distance(attr, p[0].getMeanVector()) < distance(attr, p[1].getMeanVector())){
				count[0]++;
				cluster1.add(re);
			}else{
				count[1]++;
				cluster2.add(re);
			}
		}
		p[0].setWeight((double)count[0]/recordList.size());
		p[1].setWeight((double)count[1]/recordList.size());
		
		double[][] coMatrix0 = generateCoMatrix(cluster1);
		double[][] coMatrix1 = generateCoMatrix(cluster2);
		
		p[0].setCovarianceMatrix(coMatrix0);
		p[1].setCovarianceMatrix(coMatrix1);*/
		
		double[][] coMatrix1 = new double[][]{{1,0},{0,1}};
		double[][] coMatrix2 = new double[][]{{1,0},{0,1}};
		p[0].setCovarianceMatrix(coMatrix1);
		p[1].setCovarianceMatrix(coMatrix2);
		
		double[] tmp = new double[2];
//		tmp[0] = 0.8960820947346951;
//		tmp[0] = 0.5;
		tmp[0] = r.nextDouble();
		tmp[1] = 1-tmp[0];
		p[0].setWeight(tmp[0]);
		p[1].setWeight(tmp[1]);
/*		for(int i=0; i<p.length; i++){
			double[] meanVector = new double[2];
			double[][] covarianceMatrix = new double[2][2];
//			meanVector = recordList.get(i*250).getAttr();
			meanVector = recordList.get(Math.abs(r.nextInt())%recordList.size()).getAttr();
			for(int j=0; j<meanVector.length; j++){
//				meanVector[j] = r.nextDouble();
				covarianceMatrix[j][j] = 1;
				for(int k=0; k<covarianceMatrix.length; k++){
					covarianceMatrix[j][k] = r.nextDouble();
				}
			}
//			p[i] = new Parameter(tmp[i], meanVector, covarianceMatrix);
		}*/
	}
	
/*	public static double[][] generateCoMatrix(List<Record> cluster){
		double[][] coMatrix = new double[2][2];
		double[] sum = new double[2];
		for(int i=0; i<cluster.size(); i++){
			sum[0] += cluster.get(i).getAttr()[0];
			sum[1] += cluster.get(i).getAttr()[1];
		}
		
		sum[0] /= 400;
		sum[1] /= 400;
		
		for(int i=0; i<2; i++){
			for(int j=0; j<2; j++){
				double cov = 0d;
				for(Record re : cluster){
					cov += (re.getAttr()[i]-sum[i])*(re.getAttr()[j]-sum[j]);
				}
				coMatrix[i][j] = cov;
			}
		}
		
		return coMatrix;
	}*/
/*	
	public static double distance(double[] attr, double[] centroid){
		return Math.sqrt(Math.pow(attr[0]-centroid[0], 2)+Math.pow(attr[1]-centroid[1], 2));
	}
	*/
	public static void parameterCopy(Parameter[] last, Parameter[] next){
		for(int i=0; i<2; i++){
			last[i].weight = next[i].weight;
			for(int j=0; j<2; j++){
				last[i].meanVector[j] = next[i].meanVector[j];
				for(int k=0; k<2; k++){
					last[i].covarianceMatrix[j][k] = next[i].covarianceMatrix[j][k];
				}
			}
		}
	}
	
	public static boolean convergence(Parameter[] last, Parameter[] next){
		return changeScope(last, next)>0.0000001? false:true;
	}
	
	public static double changeScope(Parameter[] last, Parameter[] next){
		double sum = 0d;
		for(int i=0; i<2; i++){
			sum += Math.abs(next[i].weight-last[i].weight);
			for(int j=0; j<2; j++){
				sum += Math.abs(next[i].meanVector[j]-last[i].meanVector[j]);
				for(int k=0; k<2; k++){
					sum += Math.abs(next[i].covarianceMatrix[j][k]-last[i].covarianceMatrix[j][k]);
				}
			}
		}
		return sum;
	}
	
	public static void EStep(List<Record> recordList, Parameter[] last, double[][] probability){
		for(int i=0; i<recordList.size(); i++){
			for(int j=0; j<last.length; j++){
				probability[i][j] = calculateProbability(recordList.get(i), last[j]);
			}
		}
		double[] weight = new double[]{last[0].weight, last[1].weight};
		for(int i=0; i<recordList.size(); i++){
			double sum = weight[0]*probability[i][0]+weight[1]*probability[i][1];
			probability[i][0] = weight[0]*probability[i][0]/sum;
			probability[i][1] = weight[1]*probability[i][1]/sum;
		}
		
	}
	
	public static double calculateProbability(Record re, Parameter p){
		double part1 = 1d/(2*Math.PI*Math.sqrt(deterOfMatrix(p.getCovarianceMatrix())));
		double part2 = Math.exp(-0.5*matrixMultiple(re, p));
		return part1*part2;
	}
	
	public static double deterOfMatrix(double[][] coMatrix){
		return coMatrix[0][0]*coMatrix[1][1] - coMatrix[0][1]*coMatrix[1][0];
	}
	
	public static double matrixMultiple(Record re, Parameter p){
		double[] attr = re.getAttr();
		double[] mV = p.getMeanVector();
		double[][] invertCM = invertMatrix(p.getCovarianceMatrix());
		double[] diff = new double[]{attr[0]-mV[0], attr[1]-mV[1]};
		
		double result = diff[0]*(diff[0]*invertCM[0][0]+diff[1]*invertCM[1][0])+diff[1]*(diff[0]*invertCM[0][1]+diff[1]*invertCM[1][1]);
		
		return result;
	}
	
	public static double[][] invertMatrix(double[][] cM){
		double[][] iMatrix = new double[2][2];
		double divisor = cM[0][0]*cM[1][1]-cM[0][1]*cM[1][0];
		iMatrix[0][0] = cM[1][1] / divisor;
		iMatrix[1][1] = cM[0][0] / divisor;
		iMatrix[0][1] = -cM[0][1]/ divisor;
		iMatrix[1][0] = -cM[1][0]/ divisor;
		
		return iMatrix;
	}
	
	public static void MStep(List<Record> recordList, Parameter[] next, Parameter[] last, double[][] probability){
		double[][] sum = new double[2][2];
		double[] divisor = new double[2];
		
		for(int i=0; i<recordList.size(); i++){
			double[] attr = recordList.get(i).getAttr();
			sum[0][0] += probability[i][0]*attr[0];
			sum[0][1] += probability[i][0]*attr[1];
			sum[1][0] += probability[i][1]*attr[0];
			sum[1][1] += probability[i][1]*attr[1];
			
			divisor[0] += probability[i][0];
			divisor[1] += probability[i][1];
		}
		
//		double[][] newMeanVector = new double[][]{{sum[0][0]/divisor[0], sum[0][1]/divisor[1]} , {sum[1][0]/divisor[0], sum[1][1]/divisor[1]}};
		
		double[][][] sumCoMatrix = new double[2][2][2];
		double[][] tmpMatrix = new double[2][2];
		for(int i=0; i<recordList.size(); i++){
			double[] attr = recordList.get(i).getAttr();
			tmpMatrix = generateCoMatrix(probability[i][0], attr, last[0].getMeanVector());
			addCoMatrix(sumCoMatrix[0], tmpMatrix);
			tmpMatrix = generateCoMatrix(probability[i][1], attr, last[1].getMeanVector());
			addCoMatrix(sumCoMatrix[1], tmpMatrix);
		}
		
		next[0].setWeight(divisor[0]/recordList.size());
		next[1].setWeight(divisor[1]/recordList.size());		
		for(int i=0; i<2; i++){
			for(int j=0; j<2; j++){
				next[0].covarianceMatrix[i][j] = sumCoMatrix[0][i][j] / divisor[0];
				next[1].covarianceMatrix[i][j] = sumCoMatrix[1][i][j] / divisor[1];
			}
		}
		next[0].meanVector[0] = sum[0][0] / divisor[0];
		next[0].meanVector[1] = sum[0][1] / divisor[0];
		next[1].meanVector[0] = sum[1][0] / divisor[1];
		next[1].meanVector[1] = sum[1][1] / divisor[1];

	}
	
	public static double[][] generateCoMatrix(double p, double[] attr, double[] meanVector){
		double[][] matrix = new double[2][2];
		double[] tmp = new double[]{attr[0]-meanVector[0], attr[1]-meanVector[1]};
		matrix[0][0] = p*Math.pow(tmp[0], 2);
		matrix[0][1] = matrix[1][0] = p*tmp[0]*tmp[1];
		matrix[1][1] = p*Math.pow(tmp[1], 2);
		return matrix;
	}
	
	public static void addCoMatrix(double[][] sumMatrix, double[][] matrix){
		sumMatrix[0][0] += matrix[0][0];
		sumMatrix[0][1] += matrix[0][1];
		sumMatrix[1][0] += matrix[1][0];
		sumMatrix[1][1] += matrix[1][1];
	}
	
	public static void clustering(List<Record> recordList, Parameter[] next,
			List<Record> cluster1, List<Record> cluster2){
		for(int i=0; i<recordList.size(); i++){
			Record re = recordList.get(i);
			System.out.println(i+"th:"+/*next[0].getWeight()**/calculateProbability(re, next[0])+"\t"+/*next[1].getWeight()**/calculateProbability(re, next[1]));
			if(next[0].getWeight()*calculateProbability(re, next[0]) > next[1].getWeight()*calculateProbability(re, next[1]))
				cluster1.add(re);
			else 
				cluster2.add(re);
		}
	}
	
	public static void clustering(List<Record> recordList, Parameter[] p, double[][] prob, 
			List<Record> cluster1, List<Record> cluster2){
		for(int i=0; i<recordList.size(); i++){
			if(p[0].getWeight()*prob[i][0] > p[1].getWeight()*prob[i][1]){
				cluster1.add(recordList.get(i));
			}
			else cluster2.add(recordList.get(i));
		}
	}
	
	public static void check(List<Record> recordList, List<Record> cluster1, List<Record> cluster2){
		int[][] count = new int[2][2];
		Record re;
		int[] result = new int[recordList.size()];
		int[] l = new int[recordList.size()];
		
		for(int i=0; i<cluster1.size(); i++){
			re = cluster1.get(i);
			l[re.getId()] = recordList.get(re.getId()).getLabel();
			result[re.getId()] = 0; 
			if(re.getLabel() == 0) count[0][0]++;
			else count[0][1]++;
		}
		
		for(int i=0; i<cluster2.size(); i++){
			re = cluster2.get(i);
			l[re.getId()] = recordList.get(re.getId()).getLabel();
			result[re.getId()] = 1; 
			if(re.getLabel() == 1) count[1][0]++;
			else count[1][1]++;
		}
		System.out.println(count[0][0]+"\t"+count[0][1]+"\t"+count[1][0]+"\t"+count[1][1]);
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
		List<Record> recordList = new ArrayList<Record>();
		ComFun.getInfo(recordList);
		
		Parameter[] next = new Parameter[]{new Parameter(), new Parameter()};
		intialParameter(recordList, next);
		
/*		System.out.println("intial parameter=============");
		next[0].displayInfo();
		next[1].displayInfo();*/
		
		Parameter[] last = new Parameter[]{new Parameter(), new Parameter()};
		double[][] probability = new double[recordList.size()][2];
		int count  = 0;
		do{
			count++;
			parameterCopy(last, next);
			EStep(recordList, last, probability);
			MStep(recordList, next, last, probability);
/*			System.out.println(count+"th parameter=============");
			next[0].displayInfo();
			next[1].displayInfo();*/
		}while(!convergence(last, next));
		System.out.println(count);
		
/*		System.out.println("final parameter=============");
		next[0].displayInfo();
		next[1].displayInfo();*/
		
		List<Record> cluster1 = new ArrayList<Record>();
		List<Record> cluster2 = new ArrayList<Record>();
//		clustering(recordList, next, cluster1, cluster2);
		clustering(recordList, next, probability, cluster1, cluster2);
		check(recordList, cluster1, cluster2);
	}
}

class Parameter{
	double weight;
	double[] meanVector; 
	double[][] covarianceMatrix;
	
	Parameter(){
		meanVector = new double[2];
		covarianceMatrix = new double[2][2];
	}
	
	Parameter(double weight, double[] meanVector, double[][] covarianceMatrix){
		this.weight = weight;
		this.meanVector = meanVector;
		this.covarianceMatrix = covarianceMatrix;
	}
	
	public void displayInfo(){
		System.out.println("weight:"+weight);
		System.out.println("meanVector:"+meanVector[0]+"\t"+meanVector[1]);
		System.out.println("covarianceMatrix:"+"\t"+covarianceMatrix[0][0]+"\t"+covarianceMatrix[0][1]);
		System.out.println("covarianceMatrix:"+"\t"+covarianceMatrix[1][0]+"\t"+covarianceMatrix[1][1]);
	}
	
	public void setWeight(double weight){
		this.weight = weight;
	}
	
	public double getWeight(){
		return weight;
	}
	
	public void setMeanVector(double[] meanVector){
		this.meanVector = meanVector;
	}
	
	public double[] getMeanVector(){
		return meanVector;
	}
	
	public void setCovarianceMatrix(double[][] covarianceMatrix){
		this.covarianceMatrix = covarianceMatrix;
	}
	
	public double[][] getCovarianceMatrix(){
		return covarianceMatrix;
	}
}

