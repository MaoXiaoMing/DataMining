
class Record {
	
	Record(String[] attribute){
		this.attribute = attribute;
//		this.flag = flag;
	}
	
	public String toString(){
		String s = null;
		for(int i=0; i<attribute.length; i++)
			s += (attribute[i]+"\t");
		return s;
	}
	
	public String[] getAttribute(){
		return attribute;
	}
	
	private String[] attribute;
//	boolean flag;
}
