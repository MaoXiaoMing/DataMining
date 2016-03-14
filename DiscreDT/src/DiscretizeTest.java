
import java.io.*;
import java.util.*;


public class DiscretizeTest {

	private final static double trainRatio = 0.7;
	
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
	
	public static void getSample(List<Record> recordList, List<Record> sample){
		Random r = new Random();
		for(int i=0; i<(int)(recordList.size()*trainRatio); i++){
			int index = Math.abs(r.nextInt());
			sample.add(recordList.get(index%150));
		}
	}
	
	public static void main(String[] args) {
		List<Record> recordList = new ArrayList<Record>();
		getInfo(recordList);
		
		List<Record> sample = new ArrayList<Record>();
		
		getSample(recordList, sample);
		
		DisDT ddt =  new DisDT(sample);
		int[] result = new int[recordList.size()];
		for(int i=0; i<recordList.size(); i++){
			result[i] = ddt.predict(ddt.rootNode, recordList.get(i));
		}
		double count = 0;
		for(int i=0; i<recordList.size(); i++){
//			System.out.println(result[i]+"\t"+recordList.get(i).getLabel());
			if(result[i] != recordList.get(i).getLabel()) count++;
		}
		System.out.println(1-count/recordList.size());
	}

}

class DisDT{
	
	DisDT(List<Record> recordList){
		rootNode = new TreeNode(recordList);
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
	
	TreeNode rootNode;
}

class TreeNode{
	
	TreeNode(List<Record> recordList){
		if(keepOn(recordList)){
//			System.out.println("node start");
//			displayRecord(recordList);
			mapAtNode = new HashMap<Integer, TreeNode>();
			findBestJudgeCond(recordList);
			List<List<Record>> splitRecord = new ArrayList<List<Record>>();
			splitRecord(recordList, splitRecord);
//			System.out.println();
//			System.out.println(this.judge);
//			System.out.println();
//			System.out.println(this.value);
//			System.out.println();
//			debug(splitRecord.get(0));
//			System.out.println("==========");
//			debug(splitRecord.get(1));
//			System.out.println("node end");
			mapAtNode.put(0, new TreeNode(splitRecord.get(0)));
			mapAtNode.put(1, new TreeNode(splitRecord.get(1)));
		}
		else{
			this.label = labelOfNode(recordList);
			this.isLeaf = true;
		}
	}
	
	public boolean keepOn(List<Record> recordList){
		for(int i=1; i<recordList.size(); i++){
			if(recordList.get(i).getLabel() != recordList.get(i-1).getLabel()){
				return true;
			}
		}
		return false;
	}
	
	public void displayRecord(List<Record> listRecord){
		for(int i=0; i<listRecord.size(); i++){
			Record re = listRecord.get(i);
			double[] tmp = re.getAttribute();
			System.out.println(tmp[0]+"\t"+tmp[1]+"\t"+tmp[2]+"\t"+tmp[3]+"\t"+re.getLabel());
		}
	}
	
	public int labelOfNode(List<Record> recordList){
		//change to vote
//		System.out.println();
//		debug(recordList);
//		System.out.println();
		return recordList.get(0).getLabel();
	}
	
	public void debug(List<Record> recordList){
		System.out.println("size" + "\t" + recordList.size());
//		System.out.println("size");
		for(int i=0; i<recordList.size(); i++){
			System.out.println(recordList.get(i).getLabel());
		}
	}
	
	public void findBestJudgeCond(List<Record> recordList){
		double[] value = new double[1];
		double min = Double.POSITIVE_INFINITY;
		for(int i=0; i<4; i++){
			double tmp = gainInfoByCondition(recordList, i, value);
			if(tmp < min){
				this.judge = i;
				min = tmp;
				this.value = value[0];
			}
		}
	}
	
	public double gainInfoByCondition(List<Record> recordList, int condition, double[] value){
		//计算不同特征下的最大信息增益
		double min = Double.POSITIVE_INFINITY;
		List<Record> copy = new ArrayList<Record>();
		copy.addAll(recordList);
		List<Record> sortList = new ArrayList<Record>();
		List<Double> valueSet = new ArrayList<Double>();
		
		for(int i=0; i<recordList.size(); i++){
			Record re = getMinRecord(copy, condition);
			sortList.add(re);
		}
		
		getValueSet(sortList, valueSet, condition);
		
		for(int i=0; i<valueSet.size(); i++){
			double gainInfo = calculateGainInfo(recordList, condition, valueSet.get(i));
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
				splitRecord.get(0).add(recordList.get(i));
			}else{
				splitRecord.get(1).add(recordList.get(i));
			}
		}
	}
	
/*	public double calculateImpurity(List<Record> recordList){
		return 1;
	}*/
	
/*	public Map<Integer, TreeNode> getMap(){
		return mapAtNode;
	}
	
	public int getjudgeType(){
		return judge;
	}
	
	public int getLabelType(){
		return label;
	}
	
	public boolean isLeaf(){
		return isLeaf;
	}*/
	
	Map<Integer, TreeNode> mapAtNode;
	boolean isLeaf = false;
	int label;
	int judge;
	double value;
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

