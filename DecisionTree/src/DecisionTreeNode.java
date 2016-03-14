
import java.util.*;

enum Condition{AGE, INCOME, STUDENT, CREDIT};

class DecisionTreeNode {
	
	DecisionTreeNode(List<Record> recordList){
		if(keepOn(recordList)){
			mapAtNode = new HashMap<String, DecisionTreeNode>();
			judgeCondition = findBestJudgeCond(recordList);
			System.out.println("keep add note, judge is "+judgeCondition);
			Map<String, List<Record>> splitRecord= new HashMap<String, List<Record>>();
			splitRecord(recordList, splitRecord, judgeCondition);
			Iterator<String> ite = splitRecord.keySet().iterator();
			while(ite.hasNext()){
				String s = ite.next();
				if(splitRecord.get(s).size() != 0){
					mapAtNode.put(s, new DecisionTreeNode(splitRecord.get(s)));
				}
			}
		}
		else{
			this.type = typeOfNode(recordList);
			this.isLeaf = true;
		}
	}
	
	public boolean keepOn(List<Record> recordList){
		for(int i=1; i<recordList.size(); i++){
			if(!recordList.get(i).getAttribute()[5].equals(recordList.get(i-1).getAttribute()[5]))
				return true;
		}
		return false;
	}
	
	public boolean typeOfNode(List<Record> recordList){
		return recordList.get(0).getAttribute()[5].equals("��")? false:true;
	}
	
	public Condition findBestJudgeCond(List<Record> recordList){
		double min = Double.POSITIVE_INFINITY;
		Condition mark = null;
		Condition[] cond = Condition.values();
		for(Condition c : cond){
			Map<String, List<Record>> splitRecord = new HashMap<String, List<Record>>();
			splitRecord(recordList, splitRecord, c);
			double tmp = calculateInfoGain(splitRecord);
			System.out.println(tmp);
			if(min > tmp){
				min = tmp;
				mark = c;
			}
		}
		return mark;
	}
	
	public void splitRecord(List<Record> recordList, 
			Map<String, List<Record>> splitRecord, Condition splitCondition){
			switch(splitCondition) {
			case AGE:
				for(Record re : recordList){
					String[] attribute = re.getAttribute();
					if(attribute[Condition.AGE.ordinal()+1].equals("������")){
						List<Record> reL = splitRecord.get("������");
						if(reL == null){
							reL = new ArrayList<Record>();
							reL.add(re);
							splitRecord.put("������", reL);
						}
						else{
							reL.add(re);
							splitRecord.replace("������", reL);
						}
					}
					if(attribute[Condition.AGE.ordinal()+1].equals("����")){
						List<Record> reL = splitRecord.get("����");
						if(reL == null){
							reL = new ArrayList<Record>();
							reL.add(re);
							splitRecord.put("����", reL);
						}
						else{
							reL.add(re);
							splitRecord.replace("����", reL);
						}
					}
					if(attribute[Condition.AGE.ordinal()+1].equals("����")){
						List<Record> reL = splitRecord.get("����");
						if(reL == null){
							reL = new ArrayList<Record>();
							reL.add(re);
							splitRecord.put("����", reL);
						}
						else{
							reL.add(re);
							splitRecord.replace("����", reL);
						}
					}
				}
				break;
			case INCOME:
				for(Record re : recordList){
					String[] attribute = re.getAttribute();
					if(attribute[Condition.INCOME.ordinal()+1].equals("��")){
						List<Record> reL = splitRecord.get("��");
						if(reL == null){
							reL = new ArrayList<Record>();
							reL.add(re);
							splitRecord.put("��", reL);
						}
						else{
							reL.add(re);
							splitRecord.replace("��", reL);
						}
					}
					if(attribute[Condition.INCOME.ordinal()+1].equals("��")){
						List<Record> reL = splitRecord.get("��");
						if(reL == null){
							reL = new ArrayList<Record>();
							reL.add(re);
							splitRecord.put("��", reL);
						}
						else{
							reL.add(re);
							splitRecord.replace("��", reL);
						}
					}
					if(attribute[Condition.INCOME.ordinal()+1].equals("��")){
						List<Record> reL = splitRecord.get("��");
						if(reL == null){
							reL = new ArrayList<Record>();
							reL.add(re);
							splitRecord.put("��", reL);
						}
						else{
							reL.add(re);
							splitRecord.replace("��", reL);
						}
					}
				}
				break;
			case STUDENT:
				for(Record re : recordList){
					String[] attribute = re.getAttribute();
					if(attribute[Condition.STUDENT.ordinal()+1].equals("��")){
						List<Record> reL = splitRecord.get("��");
						if(reL == null){
							reL = new ArrayList<Record>();
							reL.add(re);
							splitRecord.put("��", reL);
						}
						else{
							reL.add(re);
							splitRecord.replace("��", reL);
						}
					}
					if(attribute[Condition.STUDENT.ordinal()+1].equals("��")){
						List<Record> reL = splitRecord.get("��");
						if(reL == null){
							reL = new ArrayList<Record>();
							reL.add(re);
							splitRecord.put("��", reL);
						}
						else{
							reL.add(re);
							splitRecord.replace("��", reL);
						}
					}
				}
				break;
			case CREDIT:
				for(Record re : recordList){
					String[] attribute = re.getAttribute();
					if(attribute[Condition.CREDIT.ordinal()+1].equals("һ��")){
						List<Record> reL = splitRecord.get("һ��");
						if(reL == null){
							reL = new ArrayList<Record>();
							reL.add(re);
							splitRecord.put("һ��", reL);
						}
						else{
							reL.add(re);
							splitRecord.replace("һ��", reL);
						}
					}
					if(attribute[Condition.CREDIT.ordinal()+1].equals("����")){
						List<Record> reL = splitRecord.get("����");
						if(reL == null){
							reL = new ArrayList<Record>();
							reL.add(re);
							splitRecord.put("����", reL);
						}
						else{
							reL.add(re);
							splitRecord.replace("����", reL);
						}
					}
				}
				break;
			}
	}
	
	public double calculateInfoGain(Map<String, List<Record>> splitRecord){
		double sum = 0.0;
		Iterator<String> ite = splitRecord.keySet().iterator();
		while(ite.hasNext()){
			String s = ite.next();
			List<Record> reL = splitRecord.get(s);
			sum += reL.size()*calculateImpurity(reL);
		}
		return sum;
	}
	
	public double calculateImpurity(List<Record> recordList){
		String[] attribute = null;
		double count1, count2;
		count1 = count2 = 0;
		for(Record re : recordList){
			attribute = re.getAttribute();
			if(attribute[5].equals("��")){
				count1++;
			}else{
				count2++;
			}
		}
		if(count1 == 0){
			return -((count2/recordList.size())*(Math.log(count2/recordList.size())/Math.log(2.0)));
		}else if(count2 ==0){
			return -((count1/recordList.size()))*((Math.log(count1/recordList.size()))/Math.log(2.0));
		}else
			return -(((count1/recordList.size()))*((Math.log(count1/recordList.size()))/Math.log(2.0))+
				((count2/recordList.size())*(Math.log(count2/recordList.size())/Math.log(2.0))));
	}
	
	
	
	public Condition getjudgeType(){
		return judgeCondition;
	}
	
	public boolean isLeaf(){
		return isLeaf;
	}
	
	public boolean getType(){
		return type;
	}
	
	public Map<String, DecisionTreeNode> getMap(){
		return mapAtNode;
	}
	
	private Map<String, DecisionTreeNode> mapAtNode;
	private boolean isLeaf = false;
	private boolean type;
	//class type
//	private String judgeCondition;
	private Condition judgeCondition;
}
