
import java.io.*;

public class changeFileFormat {

	public static void main(String[] args) {
		try{
			BufferedReader br = new BufferedReader(new FileReader("TrainData.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter("Integer.txt"));
			String get = null;
			while((get = br.readLine()) != null){
				String[] tokens = get.split("\t");
				StringBuffer sb = new StringBuffer();
				for(int i=0; i<tokens.length; i++){
					sb.append(Integer.toString((int)Double.parseDouble(tokens[i]))+"\t");
				}
				bw.write(sb.toString());
				bw.newLine();
			}
			br.close();
			bw.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

}
