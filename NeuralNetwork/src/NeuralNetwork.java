/*2015-4-1
 * 
 * */
import java.io.*;
import java.util.*;

/**step
 * 1.pick a network architecture
 * 2.randomly initialize weight
 * 3.implement forward propagation to get h(x)
 * 4.implement code to compute cost function J(θ)
 * 5.implement back propagation to compute partial derivatives J(θ)
 * */

class Record{
	private double[] attr;
	private int type;     //0 - , 1 +
	
	Record(double[] attr, int type){
		this.attr = attr;
		this.type = type;
	}
	
	public double[] getProperty(){
		return attr;
	}
	
	public void setProperty(double[] attr){
		for(int i=0; i<attr.length; i++){
			this.attr[i] = attr[i];
		}
	}
	
	public int getType(){
		return type;
	}
}

public class NeuralNetwork {
	private static final int inputNodeNum = 108;
	private static final int hideNodeNum = 15;
	private static final int outputNodeNum = 2;
	
	private static final double intialEpsilon = 1/*0.05*/;
	private static final double studySpeed = 0.05;
	
	private static double[][] InputToHideWeight = new double[inputNodeNum][hideNodeNum]; 
	private static double[][] HideToOutputWeight = new double[hideNodeNum][outputNodeNum];
	
	private static double[] Input = new double[inputNodeNum];
	private static double[] Hide = new double[hideNodeNum];
	private static double[] Output = new double[outputNodeNum];
	
	private static double[] biasOfHide = new double[hideNodeNum];
	private static double[] biasOfOutput = new double[outputNodeNum];
	
/*	private static double[] valueOfHide = new double[hideNodeNum];
	private static double[] valueOfOutput= new double[outputNodeNum];*/
	
	private final static double trainRatio = 0.7;
	
	public static void getInfo(List<Record> recordList){
		try{
//			BufferedReader br1 = new BufferedReader(new FileReader("TrainData.txt"));
			BufferedReader br1 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader br2 = new BufferedReader(new FileReader("OSTC.txt"));
 			String get = null;
			while((get=br1.readLine()) != null){
				String[] tokens = get.split(" ");
				String label = br2.readLine();
				double[] attr = new double[tokens.length];
				for(int i=0; i<attr.length; i++){
					attr[i] = Double.parseDouble(tokens[i]);
				}
				if(label.equals("-1")){
					recordList.add(new Record(attr,0));
				}
				else{
					recordList.add(new Record(attr,1));
				}
			}
			br1.close();
			br2.close();
		}catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static void preprocess(List<Record> recordList){
		double[] min = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
		double[] max = new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
		for(int i=0; i<recordList.size(); i++){
			Record re = recordList.get(i);
			double[] tmp = re.getProperty();
			for(int j=0; j<tmp.length-1; j++){
				if(max[j] < tmp[j+1]) max[j]=tmp[j+1];
				if(min[j] > tmp[j+1]) min[j]=tmp[j+1];
			}
		}
		for(int i=0; i<recordList.size(); i++){
			Record re = recordList.get(i);
			double[] property = re.getProperty();
			double[] set = new double[inputNodeNum];
			for(int j=0; j<property.length-1; j++){
				set[j] = (property[j+1]-min[j])/(max[j]-min[j]);
			}
			re.setProperty(set);
		}
	}
	
	public static void initialWeight(){
/*		InputToHideWeight[0] = new double[]{0.2, -0.3};
		InputToHideWeight[1] = new double[]{0.4, 0.1};
		InputToHideWeight[2] = new double[]{-0.5, 0.2};
		HideToOutputWeight[0] = new double[]{-0.3};
		HideToOutputWeight[1] = new double[]{-0.2};
		biasOfHide[0] = -0.4;
		biasOfHide[1] = 0.2;
		biasOfOutput[0] = 0.1;*/
		Random random = new Random();
		for(int i=0; i<inputNodeNum; i++){
			for(int j=0; j<hideNodeNum; j++){
//				InputToHideWeight[i][j] = random.nextDouble()*2*intialEpsilon-intialEpsilon;
				InputToHideWeight[i][j] = random.nextDouble();
			}
		}
		for(int i=0; i<hideNodeNum; i++){
			for(int j=0; j<outputNodeNum; j++){
//				HideToOutputWeight[i][j] = random.nextDouble()*2*intialEpsilon-intialEpsilon;
				HideToOutputWeight[i][j] = random.nextDouble();
			}
		}
		for(int i=0; i<biasOfHide.length; i++){
			biasOfHide[i] = random.nextDouble()*2*intialEpsilon-intialEpsilon;
		}
		for(int i=0; i<biasOfOutput.length; i++){
			biasOfOutput[i] = random.nextDouble()*2*intialEpsilon-intialEpsilon;
		}
	}
	
	public static double sigmod(double x){
		return 1d / (1+Math.exp(-x));
	}
	
	public static void forwardPropagation(){
		for(int i=0; i<Hide.length; i++){
			Hide[i] = sigmod(dotProduce(Input, InputToHideWeight, i, biasOfHide[i]));
		}
		for(int i=0; i<Output.length; i++){
			Output[i] = sigmod(dotProduce(Hide, HideToOutputWeight, i, biasOfOutput[i]));
		}
		Output[0] /= Output[0]+Output[1];
		Output[1] /= Output[0]+Output[1];
	}
	
	public static double dotProduce(double[] num, double[][] weight, int index, double bias){
		double sum = bias;
		for(int i=0; i<num.length; i++){
			sum += num[i]*weight[i][index];
		}
		return sum;
	}
	
	public static void backPropagation(int type){
		double[] outputErrorItems = outputErrorItems(type);
		double[] hideErrorItems = hideErrorItems(outputErrorItems);
		updateWeight(hideErrorItems, outputErrorItems);
		updateBias(hideErrorItems, outputErrorItems);
	}
	
	public static double[] outputErrorItems(int type){
		double[] outputErrorItems = new double[outputNodeNum];
		double[] target = new double[outputNodeNum];
		/*		switch(type){
		case 0:{
			double[] target = new double[]{1,0,0};
			for(int i=0; i<outputErrorItems.length; i++){
				outputErrorItems[i] = output[i]*(1-output[i])*(target[i]-output[i]);
			}
			break;
		}
		case 1:{
			double[] target = new double[]{0,1,0};
			for(int i=0; i<outputErrorItems.length; i++){
				outputErrorItems[i] = output[i]*(1-output[i])*(target[i]-output[i]);
			}
			break;
		}
		case 2:{
			double[] target = new double[]{0,0,1};
			for(int i=0; i<outputErrorItems.length; i++){
				outputErrorItems[i] = output[i]*(1-output[i])*(target[i]-output[i]);
			}
			break;
		}
		}*/
		switch(type){
		case 0:
			target = new double[]{1, 0, 0};
			break;
		case 1:
			target = new double[]{0, 1, 0};
			break;
		case 2:
			target = new double[]{0, 0, 1};
			break;
		}
		for(int i=0; i<Output.length; i++){
			outputErrorItems[i] = (Output[i]*(1-Output[i])*(target[i]-Output[i]));
		}
		return outputErrorItems;
	}
	
	public static double[] hideErrorItems(double[] outputErrorItems){
		double[] hideErrorItems = new double[hideNodeNum];
		for(int i=0; i<hideErrorItems.length; i++){
			hideErrorItems[i] = Hide[i]*(1-Hide[i])*linearSum(i, outputErrorItems);
		}
		return hideErrorItems;
	}
	
	public static double linearSum(int num, double[] outputErrorItems){
		double sum = 0d;
		for(int i=0; i<outputErrorItems.length; i++){
			sum += HideToOutputWeight[num][i]*outputErrorItems[i];
		}
		return sum;
	}
	
	public static void updateWeight(double[] hideErrorItems, double[] outputErrorItems){
		for(int i=0; i<inputNodeNum; i++){
			for(int j=0; j<InputToHideWeight[i].length; j++){
				InputToHideWeight[i][j] +=  studySpeed*hideErrorItems[j]*Input[i];
			}
		}
		for(int i=0; i<hideNodeNum; i++){
			for(int j=0; j<HideToOutputWeight[i].length; j++){
				HideToOutputWeight[i][j] += studySpeed*outputErrorItems[j]*Hide[i];
			}
		}
		/*for(int i=0; i<InputToHideWeight.length; i++){
			for(int j=0; j<inputNodeNum; j++){
				InputToHideWeight[i][j] +=  studySpeed*hideErrorItems[i]*Input[j];
			}
		}
		for(int i=0; i<HideToOutputWeight.length; i++){
			for(int j=0; j<hideNodeNum; j++){
				HideToOutputWeight[i][j] += studySpeed*outputErrorItems[i]*Hide[j];
			}
		}*/
	}
	
	public static void updateBias(double[] hideErrorItems, double[] outputErrorItems){
		for(int i=0; i<biasOfHide.length; i++){
			biasOfHide[i] += studySpeed*hideErrorItems[i];
		}
		for(int i=0; i<outputErrorItems.length; i++){
			biasOfOutput[i] += studySpeed*outputErrorItems[i];
		}
	}
	
	
	public static double errorItem(Record re){
		double error = 0d;
		if(re.getType() == 0){
			double[] target = {1, 0};
			error += Math.pow(target[0]-Output[0], 2);
			error += Math.pow(target[1]-Output[1], 2);
		}else{
			double[] target = {0, 1};
			error += Math.pow(target[0]-Output[0], 2);
			error += Math.pow(target[1]-Output[1], 2);
		}
		return error;
	}
	
	public static boolean trainEnd(List<Record> recordList, int num){
		int count = 0;
		double error = 0d;
		for(int i=0; i<recordList.size(); i++){
			Record re = recordList.get(i);
			Output= forecast(re.getProperty());
			Output[0] = Output[0]/(Output[0]+Output[1]);
			Output[1] = Output[1]/(Output[0]+Output[1]);
//			System.out.println(i+1+":"+re.getType()+"\t====="+Output[0]+"\t"+Output[1]);
			error += errorItem(re);
			if(!verdict(re.getType())){
				count++;
/*				Input = re.getProperty();
				forwardPropagation();
				backPropagation(re.getType());*/
//				System.out.println(i+1+":"+re.getType()+"\t====="+Output[0]+"\t"+Output[1]);
			} 
/*			else{
				count++;
//				System.out.println("第"+(i+1)+"个实例预测错误");
//				return false;
			} */
		}
		if(num%100 == 0){
			System.out.println(num+"迭代：errorItem:"+error);
			System.out.println(count);
		}
		return false;
		/*if(count < 100){
			return true;
		}else{
			return false;
		}*/
	}
	
	public static double[] forecast(double[] property){
		double[] hideLayer = new double[hideNodeNum];
		double[] outputLayer = new double[outputNodeNum];
		for(int i=0; i<hideLayer.length; i++){
			hideLayer[i] = sigmod(dotProduce(property, InputToHideWeight, i, biasOfHide[i]));
		}
		for(int i=0; i<outputLayer.length; i++){
			outputLayer[i] = sigmod(dotProduce(hideLayer, HideToOutputWeight, i, biasOfOutput[i]));
		}
		return outputLayer;
	}
	
	public static boolean verdict(int type){
		if(Output[type] > Output[1-type]) return true;
		else return false;
	}
	
	public static void show(List<Record> recordList){
		for(int i=0; i<recordList.size(); i++){
			Record re = recordList.get(i);
			double[] tmp = re.getProperty();
			for(int j=0; j<tmp.length; j++){
				System.out.println(tmp[j]);
			}
		}
	}
	
	public static void displayWeight(){
		
	}
	
	public static void sample(List<Record> recordList,
			List<Record> trainList, List<Record> testList){
		List<Record> copy = new ArrayList<Record>();
		copy.addAll(recordList);
		Random r = new Random();
		int trainSize = (int)(copy.size()*trainRatio);
		for(int i=0; i<trainSize; i++){
			int index = Math.abs(r.nextInt()) % copy.size();
			Record re = copy.remove(index);
			trainList.add(re);
		}
		testList.addAll(copy);
	}
	
	public static void storageTrain(List<Record> trainList){
		try{
			BufferedWriter bw1 = new BufferedWriter(new FileWriter("TrainData.txt"));
			BufferedWriter bw2 = new BufferedWriter(new FileWriter("TrainLabel.txt"));
			for(int i=0; i<trainList.size(); i++){
				StringBuffer sb = new StringBuffer();
				double[] attr = trainList.get(i).getProperty();
				for(int j=0; j<attr.length; j++){
					sb.append(attr[j]+"\t");
				}
				bw1.write(sb.toString());
				bw1.newLine();
				bw2.write(Integer.toString(trainList.get(i).getType()));
				bw2.newLine();
			}
			bw1.close();
			bw2.close();
		}catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static void storageTest(List<Record> testList){
		try{
			BufferedWriter bw1 = new BufferedWriter(new FileWriter("TestData.txt"));
			BufferedWriter bw2 = new BufferedWriter(new FileWriter("TestLabel.txt"));
			for(int i=0; i<testList.size(); i++){
				StringBuffer sb = new StringBuffer();
				double[] attr = testList.get(i).getProperty();
				for(int j=0; j<attr.length; j++){
					sb.append(attr[j]+"\t");
				}
				bw1.write(sb.toString());
				bw1.newLine();
				bw2.write(Integer.toString(testList.get(i).getType()));
				bw2.newLine();
			}
			bw1.close();
			bw2.close();
		}catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static void writeNormData(List<Record> recordList){
		int attrLength = recordList.get(0).getProperty().length;
		double[][] data = new double[recordList.size()][attrLength];
		double[] max = new double[attrLength];
		double[] min = new double[attrLength];
//		double[] sum = new double[attrLength];
		
		for(int i=0; i<attrLength; i++){
			max[i] = Double.NEGATIVE_INFINITY;
			min[i] = Double.POSITIVE_INFINITY;
//			sum[i] = 0;
		}
		
		for(int i=0; i<recordList.size(); i++){
			double[] attr = recordList.get(i).getProperty();
			for(int j=0; j<attrLength; j++){
//				sum[j] += attr[j];
				if(max[j] < attr[j]){
					max[j]  = attr[j];
				}
				if(attr[j] < min[j]){
					min[j] = attr[j];
				}
			}
		}

		for(int i=0; i<recordList.size(); i++){
			double[] attr = recordList.get(i).getProperty();
			for(int j=0; j<attrLength; j++){
				data[i][j] = ((attr[j]-min[j]) / (max[j]-min[j]));
			}
		}
		
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter("norm.txt"));
			for(int i=0; i<data.length; i++){
				StringBuffer sb = new StringBuffer();
				for(int j=0; j<data[i].length; j++){
					sb.append(data[i][j]+"\t");
				}
				bw.write(sb.toString());
				bw.newLine();
			}
			bw.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
//		System.out.println();
	}
	
	public static double predict(double[] attr){
		Output= forecast(attr);
		return Output[1]/(Output[0]+Output[1]);
		/*if(Output[0] > Output[1]) return Output[0]/(Output[0]+Output[1]);
		else return Output[1]/(Output[0]+Output[1]);*/
	}
	
	public static void resample(List<Record> trainList){
		int postiveInstance = 0;
		for(int i=0; i<trainList.size(); i++){
			if(trainList.get(i).getType() == 1){
				postiveInstance++;
			}
		}
		Random r = new Random();
		while(trainList.size() > 2*postiveInstance){
			int index = Math.abs(r.nextInt()) % trainList.size();
			if(trainList.get(index).getType() == 0){
				trainList.remove(index);
			}
		}
	}
	
	public static void main(String[] args) {
		List<Record> recordList = new ArrayList<>();
		List<Record> trainList = new ArrayList<>();
		List<Record> testList = new ArrayList<>();
		getInfo(recordList);
		sample(recordList, trainList, testList);
		resample(trainList);
/*		storageTrain(trainList);
		storageTest(testList);
		
		List<Record> trainList = new ArrayList<>();*/
//		List<Record> testList = new ArrayList<>();
//		getInfo(trainList);
//		writeNormData(trainList);
//		preprocess(recordList);
		initialWeight();
//		show(recordList);
		int count = 0;
		System.out.println(trainList.size());
		while(!trainEnd(trainList, count) && count < 5000){
/*			if(count%1000 == 0){
				System.out.println(++count);
			}*/
			count++;
			for(Record re : trainList){
				Input = re.getProperty();
				forwardPropagation();
				backPropagation(re.getType());
/*				Input = re.getProperty();
				forwardPropagation();			
  				if(predict(re.getProperty()) == re.getType()){
					continue;
				}else{
					backPropagation(re.getType());
				}*/
			}
		}
		
		double[][] res = new double[testList.size()][2];
		int[] label = new int[testList.size()];
		
		for(int i=0; i<testList.size(); i++){
			res[i][0] = predict(testList.get(i).getProperty());
			res[i][1] = testList.get(i).getType();
			if(res[i][0] > 0.5) {
				label[i] = 1;
			}else{
				label[i] = 0;
			}
		}
		
		write(res);
		
		int num = 0;
		int stat = 0;
		int truePositive = 0;
		for(int i=0; i<label.length; i++){
			if(label[i] == testList.get(i).getType()) {
				num++;
			}
			if(testList.get(i).getType() == 1) {
				stat++;
				if(label[i] == 1){
					truePositive++;
				}
			}
		}
		
		System.out.println(num);
		System.out.println(stat);
		System.out.println(truePositive);
/*		int[] result = new int[testList.size()];
		for(int i=0; i<testList.size(); i++){
			result[i] = predict(testList.get(i).getProperty());
		}
		int num = 0;
		for(int i=0; i<result.length; i++){
			if(result[i] == testList.get(i).getType()) num++;
		}
		System.out.println(num);*/
	}
	
	public static void write(double[][] res){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter("res.txt"));
			for(int i=0; i<res.length; i++){
				bw.write(res[i][0]+"\t"+res[i][1]);
				bw.newLine();
			}
			bw.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}
