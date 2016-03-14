
import java.io.*;
import java.util.*;

public class RandomForest {

	private final static int TreeNum = 100;
	private final static int FeatureNum = 3;
	private final static double TrainRatio = 0.7;
	
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
//			preprocessing(recordList, max, min);
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
	
	public static void splitData(List<Record> recordList, List<Record> train, List<Record> test){
		List<Record> copy = new ArrayList<Record>();
		copy.addAll(recordList);
		Random r = new Random();
		for(int i=0; i<(int)(recordList.size()*TrainRatio); i++){
			int index = Math.abs(r.nextInt()) % (recordList.size()-i);
			train.add(copy.remove(index));
		}
		test.addAll(copy);
	}
	
	public static void sample(List<Record> recordList, List<Record> sample){
		sample.clear();
		Random r = new Random();
		for(int i=0; i<recordList.size(); i++){
			Record re = recordList.get(Math.abs(r.nextInt())%recordList.size());
			sample.add(re);
//			if(!sample.contains(re)) sample.add(re);
		}
	}
	
	public static void featureChoose(int[] feature){
		Random r = new Random();
		List<Integer> l = new ArrayList<Integer>();
		for(int i=0; i<4; i++){
			l.add(i);
		}
		int index = Math.abs(r.nextInt())%4;
		l.remove(index);
		for(int i=0; i<feature.length; i++){
			feature[i] = l.get(i);
		}
	}
	
	public static DecisionTree DecisionTree(List<Record> sample, int[] feature){
		DecisionTree dt = new DecisionTree(sample, feature);
		return dt;
	}
	
	public static int combinePredict(DecisionTree[] dt, Record re){
		int[] count = new int[3];
		int[] result = new int[dt.length];
		
		for(int i=0; i<dt.length; i++){
			result[i] = dt[i].predict(dt[i].rootNode, re);
		}
		for(int i=0; i<result.length; i++){
			count[result[i]]++;
		}
		return count[0]>count[1]?(count[0]>count[2]?0:2):(count[1]>count[2]?(1):(2));

	}
	
	public static void main(String[] args) {
		List<Record> recordList = new ArrayList<Record>();
		List<Record> sample = new ArrayList<Record>();
		List<Record> train = new ArrayList<Record>();
		List<Record> test = new ArrayList<Record>();
		int[][] feature = new int[TreeNum][FeatureNum];
		DecisionTree[] forest = new DecisionTree[TreeNum];
		
		getInfo(recordList);
		splitData(recordList, train, test);
		
		for(int i=0; i<TreeNum; i++){
//			System.out.println("tree"+i+"\tstart");
			sample(train, sample);
			featureChoose(feature[i]);
			forest[i] = DecisionTree(sample, feature[i]);
//			System.out.println("tree"+i+"\tend");
		}
		DecisionTree.display(forest[0].rootNode, 0);
		int[] result = new int[test.size()];
		for(int i=0; i<test.size(); i++){
			Record re = test.get(i);
			result[i] = combinePredict(forest, re);
		}
		
		double count = 0;
		for(int i=0; i<test.size(); i++){
//			System.out.println(result[i]+"\t"+recordList.get(i).getLabel());
			if(result[i] != test.get(i).getLabel()) count++;
		}
		System.out.println("\n"+(1-count/test.size()));
	}
}

class DecisionTree{
//	private int[] feature;
	TreeNode rootNode;
	
	DecisionTree(List<Record> sample, int[] feature){
//		this.feature = feature;
		rootNode = new TreeNode(sample, feature);
	}
	
	public int predict(TreeNode node, Record re){
		if(node.isLeaf){
			return node.label;
		}
		else if(re.getAttribute()[node.judge] < node.value){
			return predict(node.mapAtNode.get(0), re);
		}else{
			return predict(node.mapAtNode.get(1), re);
		}
	}
	
	public static void display(TreeNode node, int layer){
		if(layer != 0){
			System.out.println();
			for(int i=0; i<layer; i++){
				System.out.print("\t");
			}
			System.out.print("|__");
		}
		if(node.isLeaf){
			System.out.print("type="+node.label);
		}else{
			System.out.print("attr"+node.judge+"<"+node.value);
			display(node.mapAtNode.get(0), layer+1);
			display(node.mapAtNode.get(1), layer+1);
		}
			
	}
}

//enum condition{SEPAL_LENGTH, SEPAL_WIDTH, PETAL_LENGTH, PETAL_WIDTH};

class TreeNode{
//	condition judgeCondition;
	Map<Integer, TreeNode> mapAtNode;
	boolean isLeaf = false;
	int label;
	int judge;
	double value;
	
	TreeNode(List<Record> sample, int[] feature){
		if(keepon(sample)){
//			System.out.println("node start");
//			displayRecord(sample, feature);
			mapAtNode = new HashMap<Integer, TreeNode>();
			findBestJudgeCond(sample, feature);
			List<List<Record>> splitRecord = new ArrayList<List<Record>>();
//			System.out.println(this.judge);
//			System.out.println();
//			System.out.println(this.value);
//			System.out.println();
			splitRecord(sample, splitRecord);
//			System.out.println();
//			debug(splitRecord.get(0));
//			System.out.println("==========");
//			debug(splitRecord.get(1));
//			System.out.println("node end");
			mapAtNode.put(0, new TreeNode(splitRecord.get(0), feature));
			mapAtNode.put(1, new TreeNode(splitRecord.get(1), feature));
		}
		else{
			this.label = labelOfNode(sample);
			this.isLeaf = true;
		}
	}
	
	public void displayRecord(List<Record> listRecord, int[] feature){
		for(int i=0; i<listRecord.size(); i++){
			Record re = listRecord.get(i);
			double[] tmp = re.getAttribute();
			System.out.println(tmp[feature[0]]+"\t"+tmp[feature[1]]+"\t"+tmp[feature[2]]+"\t"+re.getLabel());
		}
	}
	
	public void debug(List<Record> recordList){
		System.out.println("size" + "\t" + recordList.size());
//		System.out.println("size");
		for(int i=0; i<recordList.size(); i++){
			System.out.println(recordList.get(i).getLabel());
		}
	}
	
	boolean keepon(List<Record> sample){
		for(int i=1; i<sample.size(); i++){
			if(sample.get(i).getLabel() != sample.get(i-1).getLabel()){
				return true;
			}
		}
		return false;
	}
	
	void findBestJudgeCond(List<Record> sample, int[] feature){
		double[] value = new double[1];
		double min = Double.POSITIVE_INFINITY;
		for(int i=0; i<feature.length; i++){
			double tmp = gainInfoByCondition(sample, feature[i], value);
			if(tmp < min){
				this.judge = feature[i];
				min = tmp;
				this.value = value[0];
			}
		}
	}
	
	public double gainInfoByCondition(List<Record> sample, int condition, double[] value){
		//计算不同特征下的最大信息增益
		double min = Double.POSITIVE_INFINITY;
		List<Record> copy = new ArrayList<Record>();
		copy.addAll(sample);
		List<Record> sortList = new ArrayList<Record>();
		List<Double> valueSet = new ArrayList<Double>();
		
		for(int i=0; i<sample.size(); i++){
			Record re = getMinRecord(copy, condition);
			sortList.add(re);
		}
		
		getValueSet(sortList, valueSet, condition);
		
		for(int i=0; i<valueSet.size(); i++){
			double gainInfo = calculateGainInfo(sample, condition, valueSet.get(i));
			if(gainInfo < min){
				min = gainInfo;
				value[0] = valueSet.get(i);
			}
		}
		return min;
	}
	
	public Record getMinRecord(List<Record> copy, int condition){
		int index = 0;
		double min = Double.POSITIVE_INFINITY;
		for(int i=0; i<copy.size(); i++){
			if(min > copy.get(i).getAttribute()[condition]){
				min = copy.get(i).getAttribute()[condition];
				index = i;
			}
		}
		return copy.remove(index);
	}
	
	public void getValueSet(List<Record> sortList, List<Double> valueSet, int condition){
		valueSet.clear();
		for(int i=1; i<sortList.size(); i++){
			if(/*(sortList.get(i-1).getLabel()!=sortList.get(i).getLabel()) &&*/
					(sortList.get(i-1).getAttribute()[condition] != sortList.get(i).getAttribute()[condition])){
				valueSet.add((sortList.get(i-1).getAttribute()[condition]+sortList.get(i).getAttribute()[condition])/2);
			}
		}
	}
	
	public double calculateGainInfo(List<Record> recordList, int condition, double value){
/*		List<Integer> small = new ArrayList<Integer>();
		List<Integer> large = new ArrayList<Integer>();*/
/*		Map<Integer, Integer> small = new HashMap<Integer, Integer>();
		Map<Integer, Integer> large = new HashMap<Integer, Integer>();*/
		double[] small, large;
		small = new double[3];
		large = new double[3];
		for(int i=0; i<recordList.size(); i++){
			if(recordList.get(i).getAttribute()[condition] < value){
				/*int tmp = small.get(recordList.get(i).getLabel());
				small.replace(recordList.get(i).getLabel(), ++tmp);*/
				small[recordList.get(i).getLabel()]++;
			}else{
				/*int tmp = large.get(recordList.get(i).getLabel());
				large.replace(recordList.get(i).getLabel(), ++tmp);*/
				large[recordList.get(i).getLabel()]++;
			}
		}
		return GainInfo(small, large);
		
	}
	
	public double GainInfo(double[] small, double[] large){
		double sum1, sum2;
		sum1 = sum2 = 0d;
		double count1, count2;
		count1 = count2 = 0;
		for(int i=0; i<small.length; i++){
			count1 += small[i];
			count2 += large[i];
		}
		for(int i=0; i<small.length; i++){
			sum1 += Math.pow(small[i]/count1, 2);
			sum2 += Math.pow(large[i]/count2, 2);
		}
		sum1 = 1-sum1;
		sum2 = 1-sum2;
		return count1/(count1+count2)*sum1+count2/(count1+count2)*sum2;
	}
	
	public void splitRecord(List<Record> recordList, List<List<Record>> splitRecord){
		splitRecord.add(new ArrayList<Record>());
		splitRecord.add(new ArrayList<Record>());

		for(int i=0; i<recordList.size(); i++){
			if(recordList.get(i).getAttribute()[this.judge] < this.value){
//				System.out.println(recordList.get(i).getAttribute()[this.judge]+"\tsplit to left");
				splitRecord.get(0).add(recordList.get(i));
			}else{
//				System.out.println(recordList.get(i).getAttribute()[this.judge]+"\tsplit to right");
				splitRecord.get(1).add(recordList.get(i));
			}
		}
	}
	
	int labelOfNode(List<Record> sample){
		return sample.get(0).getLabel();
	}
}

class Record {
	
	Record(double[] attribute, int lable){
		setAttribute(attribute);
		this.label = lable;
	}
	
	public double[] getAttribute(){
		return attribute;
	}
	
	public void setAttribute(double[] attribute){
		this.attribute = attribute;
	}
	
	public int getLabel(){
		return label;
	}
	
	private double[] attribute;
	private int label;
}