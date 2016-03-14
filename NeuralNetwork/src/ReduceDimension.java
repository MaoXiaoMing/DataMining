
import java.io.*;

import Jama.*;

public class ReduceDimension {

	public static void readInfo(double[][] data, double[] values, double[][] vectors){
		try{
			BufferedReader br1 = new BufferedReader(new FileReader("tmp.txt"));
			BufferedReader br2 = new BufferedReader(new FileReader("1.txt"));
			BufferedReader br3 = new BufferedReader(new FileReader("2.txt"));
			String get = null;
			for(int i=0; i<data.length; i++){
				get=br1.readLine();
				String[] tokens = get.trim().split("\t");
				for(int j=0; j<tokens.length; j++){
					data[i][j] = Double.parseDouble(tokens[j]);
				}
			}
			
			for(int i=0; i<values.length; i++){
				get=br2.readLine();
				values[i] = Double.parseDouble(get);
			}
			
			for(int i=0; i<vectors.length; i++){
				get=br3.readLine();
				String[] tokens = get.trim().split(" ");
				for(int j=0; j<tokens.length; j++){
					vectors[i][j] = Double.parseDouble(tokens[j]);
				}
			}
			
			br1.close();
			br2.close();
			br3.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static double[][] chooseVectors(double[] values, double[][] vectors){
		double sum = 0d;
		for(int i=0; i<values.length; i++){
			sum += values[i];
		}
		int index = 0;
		double tmp = 0d;
		for( ; index<values.length; index++){
			tmp += values[index];
			if(tmp > 0.90*sum){
				break;
			}
		}
		
		double[][] newVectors = new double[values.length][index+1];
		for(int i=0; i<newVectors.length; i++){
			for(int j=0; j<newVectors[i].length; j++){
				newVectors[i][j] = vectors[i][j];
			}
		}
/*		for(int i=0; i<=index; i++){
			double[] ve = new double[vectors.length];
			for(int j=0; j<ve.length; j++){
				ve[j] = vectors[i][j];
			}
			newVectors[i] = ve;
		}*/
		
		return newVectors;
	}
	
	public static void write(Matrix re){
		double[][] data = re.getArray();
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter("reduceData.txt"));
			for(int i=0; i<data.length; i++){
				StringBuffer sb = new StringBuffer();
				for(int j=0; j<data[i].length; j++){
					sb.append(data[i][j]+"\t");
				}
				bw.write(sb.toString().trim());
				bw.newLine();
			}
			bw.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		double[][] data = new double[50000][207];
		double[] values = new double[207];
		double[][] vectors = new double[207][207];
		
		readInfo(data, values, vectors);
		
		double[][] newVectors = chooseVectors(values, vectors);
		
		Matrix d = new Matrix(data);
		Matrix v = new Matrix(newVectors);
		
		Matrix re = d.times(v);
		write(re);
		System.out.println();
	}

}
