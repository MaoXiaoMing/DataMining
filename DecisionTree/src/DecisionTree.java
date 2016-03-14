/*2015-3-25
 * 
 * */
import java.io.*;
import java.io.IOException;
import java.util.*;

public class DecisionTree {

	public static void getInfo(List<Record> recordList){
		try{
			BufferedReader br = new BufferedReader(new FileReader("Data.txt"));
			String get = null;
			while( (get=br.readLine()) != null){
				recordList.add(new Record(get.split("\t")));
			}
			br.close();
		}catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static DecisionTreeNode buildDecisionTree(List<Record> recordList){
		return new DecisionTreeNode(recordList);
		
	}
	
	public static boolean classification(DecisionTreeNode rootNode, String[] attribute){
		if(rootNode.isLeaf()) return rootNode.getType();
		else{
			return classification(rootNode.getMap().get(attribute[rootNode.getjudgeType().ordinal()+1]), attribute);
		} 
	}
	
	public static void main(String[] args) {
		List<Record> recordList = new ArrayList<Record>();
		getInfo(recordList);
		DecisionTreeNode rootNode = buildDecisionTree(recordList);
		try{
			BufferedReader br = new BufferedReader(new FileReader("Data.txt"));
			String get = null;
			while((get=br.readLine()) != null){
				String[] tokens = get.split("\t");
				System.out.println(classification(rootNode, tokens));
			}
			br.close();
		}catch (IOException ex){
			ex.printStackTrace();
		}
	}

}
