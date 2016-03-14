/*2015-3-26
 * 
 * */


import java.util.List;
import java.util.ArrayList;
import java.io.*;

//test inner for double return value;

public class NavieBayesian {

	public static void getInfo(List<Record> recordList){
		try{
			BufferedReader br = new BufferedReader(new FileReader("Data.txt"));
			String get = null;
			get = br.readLine();
			while((get=br.readLine()) != null){
				String[] tokens = get.split("\t");
				Record re = new Record(tokens);
				recordList.add(re);
			}
			br.close();
		}catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static int labelCount(List<Record> recordList, String label){
		int count;
		count = 0;
		for(Record re : recordList){
			if(re.getAttribute()[4].equals(label)) count++;
		}
		return count;
	}
	
	public static double conditionalProbability(List<Record> recordList, Condition condition, 
			String type, String label){
		double countX, countY;
		countX = countY = 0;
		for(Record re : recordList){
			if(re.getAttribute()[4].equals(label)){
				countY++;
				if(re.getAttribute()[condition.ordinal()].equals(type)){
					countX++;
				}
			}
		}
//		System.out.println(countX/countY);
		return countX/countY;
	}
	
	public static String posteriorProbability(List<Record> recordList, String[] attribute){
		double sum1,sum2, p1, p2;
		sum1 = sum2 = 1.0;
		p1 = p2 = 0.0;
		int count1, count2;
		count1 = count2 = 0;
		for(int i=0; i<attribute.length-1; i++){
			sum1 *= conditionalProbability(recordList, Condition.values()[i], attribute[i], "yes");
			sum2 *= conditionalProbability(recordList, Condition.values()[i], attribute[i], "no");
		}
		count1 = labelCount(recordList, "yes");
		count2 = labelCount(recordList, "no");
		p1 = (double)count1 / (count1+count2);
		p2 = (double)count2 / (count1+count2);
		
//		System.out.println(p1*sum1);
//		System.out.println(p2*sum2);
		return (p1*sum1)>(p2*sum2) ? "yes":"no";
	}
	
	public static void main(String[] args) {
		List<Record> recordList = new ArrayList<Record>();
		getInfo(recordList);
		for(Record re : recordList){
			System.out.println(posteriorProbability(recordList, re.getAttribute()));
		}
//		System.out.println(posteriorProbability(recordList,new String[] {"sunny","cool","high","TRUE","11"}));
	}

}

enum Class{NO, YES};
enum Condition{OUTLOOK, TEMPERATURE, HUMIDITY, WINDY};

class Record{
	
	Record(String[] attribute){
		this.attribute = attribute;
	}
	
	public String[] getAttribute(){
		return attribute;
	}
	
	private String[] attribute;
}
