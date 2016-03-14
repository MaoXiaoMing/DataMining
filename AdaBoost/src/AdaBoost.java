import java.io.*;
import java.util.*;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class AdaBoost {

	private static final int runTimes = 10;
	private static final double trainRatio = 0.7;
	
	public static void getInfo(List<Record> recordList){
		try{
			BufferedReader br = new BufferedReader(new FileReader("iris.data"));
			String get = null;
			double[] max = new double[4];
			double[] min = new double[4];
			for(int i=0; i<max.length; i++){
				max[i] = Double.NEGATIVE_INFINITY;
				min[i] = Double.POSITIVE_INFINITY;
			}
			while((get = br.readLine()) != null){
				String[] tokens = get.split(",");
				double[] attr = new double[]{Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]),
						Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3])};
				for(int i=0; i<attr.length; i++){
					if(max[i] < attr[i]) max[i] = attr[i];
					if(min[i] > attr[i]) min[i] = attr[i];
				}
				switch (tokens[tokens.length-1]){
				case "Iris-setosa":
					recordList.add(new Record(attr, 0));
					break;
				case "Iris-versicolor":
					recordList.add(new Record(attr, 1));
					break;
				case "Iris-virginica":
					recordList.add(new Record(attr, 2));
					break;
				}
			}
			br.close();
			preprocessing(recordList, max, min);
		}catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static void preprocessing(List<Record> recordList, double[] max, double[] min){
		for(Record re : recordList){
			double[] attr = re.getAttribute();
			for(int i=0; i<attr.length; i++){
				attr[i] = (attr[i]-min[i]) / (max[i]-min[i]);
			}
		}
	}
	
	public static void sample(List<Record> recordList, List<Record> sample, double[] weightOfSample){
		sample.clear();
		int recordListLength = recordList.size();
		Random r = new Random();
		sample.clear();
		for(int i=0; i<recordListLength*trainRatio; i++){
			double index = r.nextDouble();
			index -= Math.floor(index);
			double sum = 0d;
			for(int j=0; j<weightOfSample.length; j++){
				if(sum > index){
					sample.add(recordList.get(j-1));
					break;
				} 
				else sum += weightOfSample[j];
			}
		}
	}
	
	public static void intialWeight(double[] weightofSample){
		for(int i=0; i<weightofSample.length; i++){
			weightofSample[i] = 1d/weightofSample.length;
		}
	}
	
	
	public static svm_model trainBasedClassifier(List<Record> sample){
		svm_node[][] train = new svm_node[sample.size()][];
		double[] labels = new double[sample.size()];
		
		for(int i=0; i<sample.size(); i++){
			svm_node[] node = new svm_node[4];
			Record re = sample.get(i);
			for(int j=0; j<node.length; j++){
				node[j] = new svm_node();
			}
			node[0].index = 0;
			node[0].value = re.getAttribute()[0];
			node[1].index = 1;
			node[1].value = re.getAttribute()[1];
			node[2].index = 2;
			node[2].value = re.getAttribute()[2];
			node[3].index = -1;
			node[3].value = re.getAttribute()[3];
			train[i] = node;
			labels[i] = re.getLabel();
		}
		
		svm_problem problem = new svm_problem();
		problem.l = sample.size();
		problem.x = train;
		problem.y = labels;
		
		svm_parameter param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.LINEAR;
        param.cache_size = 100;
        param.eps = 0.00001;
        param.C = 1;
		
        svm.svm_check_parameter(problem, param);
		svm_model model = svm.svm_train(problem, param);
		
		return model;
	}
	
	public static double predict(svm_model model, Record re){
		svm_node[] test = new svm_node[4];
		for(int i=0; i<test.length; i++){
			test[i] = new svm_node();
		}
		test[0].value = re.getAttribute()[0];
		test[0].index = 0;
		test[1].value = re.getAttribute()[1];
		test[1].index = 1;
		test[2].value = re.getAttribute()[2];
		test[2].index = 2;
		test[3].value = re.getAttribute()[3];
		test[3].index = -1;
		return svm.svm_predict(model, test);
	}
	
	public static double calculateEpsilon(List<Record> recordList, double[] result, double[] weightOfSample){
		double epsilon = 0d;
		for(int i=0; i<recordList.size(); i++){
			if(recordList.get(i).getLabel() != result[i]){
				epsilon += weightOfSample[i];
			}
		}
		return epsilon/result.length;
	}
	
	public static void updateWeight(List<Record> recordList, double[] result, double[] weightOfSample, double epsilon){
		double normFactor = 0d;
		for(int i=0; i<recordList.size(); i++){
			if(recordList.get(i).getLabel() == result[i]){
				weightOfSample[i] *= Math.exp(-epsilon);
			}else{
				weightOfSample[i] *= Math.exp(epsilon);
			}
			normFactor += weightOfSample[i];
		}
		for(int i=0; i<weightOfSample.length; i++){
			weightOfSample[i] /= normFactor;
		}
	}
	
	public static double finalPerdict(svm_model[] modelSet, double[] weightOfModel, Record re){
		double[] result = new double[modelSet.length];
		svm_node[] test = new svm_node[4];
		for(int i=0; i<test.length; i++){
			test[i] = new svm_node();
		}
		test[0].index = 0;
		test[0].value = re.getAttribute()[0];
		test[1].index = 1;
		test[1].value = re.getAttribute()[1];
		test[2].index = 2;
		test[2].value = re.getAttribute()[2];
		test[3].index = -1;
		test[3].value = re.getAttribute()[3];
		for(int i=0; i<modelSet.length; i++){
			result[i] = svm.svm_predict(modelSet[i], test);
		}
		double[] max = new double[3];
		for(int i=0; i<result.length; i++){
			max[(int)result[i]] += weightOfModel[i];
		}
		return max[0]>max[1]?(max[0]>max[2]?0:2):(max[1]>max[2]?1:2);
	}
	
	public static void check(List<Record> test, double[] result){
		double count = 0;
		for(int i=0; i<test.size(); i++){
			Record re = test.get(i);
			if(re.getLabel() != result[i]) continue;
			else count++;
//			System.out.println(i+"\t"+re.getLabel()+"\t"+result[i]);
/*			if(re.getLabel()==0 && result[i]==0)
				count++;
			if(re.getLabel()==1 && result[i]==1)
				count++;
			if(re.getLabel()==2 && result[i]==2)
				count++;*/
		}
		System.out.println("\n"+count/test.size()+"\n");
	}
	
	public static void splitData(List<Record> recordList, List<Record> train, List<Record> test){
		List<Record> copy = new ArrayList<Record>();
		copy.addAll(recordList);
		Random r = new Random();
		for(int i=0; i<(int)(recordList.size()*trainRatio); i++){
			int index = Math.abs(r.nextInt()) % (recordList.size()-i);
			train.add(copy.remove(index));
		}
		test.addAll(copy);
	}
	
	public static void main(String[] args) {
		List<Record> recordList = new ArrayList<Record>();
		List<Record> sample = new ArrayList<Record>();
		List<Record> train = new ArrayList<Record>();
		List<Record> test = new ArrayList<Record>();
		svm_model[] modelSet = new svm_model[runTimes];

		getInfo(recordList);
		splitData(recordList, train, test);
		
		double[] weightOfSample = new double[recordList.size()];
		intialWeight(weightOfSample);
		
		double[] weightOfModel = new double[runTimes];
		
		for(int i=0; i<runTimes; i++){
			sample(recordList, sample, weightOfSample);
			modelSet[i] = trainBasedClassifier(sample);
			
			double[] result = new double[recordList.size()];
			for(int j=0; j<result.length; j++){
				result[j] = predict(modelSet[i], recordList.get(j));
			}
//			check(recordList, result);
			double epsilon = calculateEpsilon(recordList, result, weightOfSample);
			System.out.println(epsilon);
			if(epsilon > 0.5){
				intialWeight(weightOfSample);
				i--;
			}
			else{
				weightOfModel[i] = 0.5*(Math.log((1-epsilon) / epsilon));
				updateWeight(recordList, result, weightOfSample, epsilon);
			}
		}
		double[] result = new double[test.size()];
		for(int i=0; i<test.size(); i++){
			result[i] = finalPerdict(modelSet, weightOfModel, test.get(i));
		}
		check(test, result);
	}
}

class Record {
	
	Record(double[] attribute, int label){
		this.attribute = attribute;
		this.label = label;
	}
	
	public void setAttribute(double[] attribute){
		this.attribute = attribute;
	}
	
	public double[] getAttribute(){
		return attribute;
	}
	
	public int getLabel(){
		return label;
	}
	
	double[] attribute;
	int label;
	
}