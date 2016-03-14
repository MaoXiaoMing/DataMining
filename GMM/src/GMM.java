import java.io.*;
import java.util.*;

public class GMM {
	ArrayList<Item> data = null;
	double[][] value;
	int data_Lenth;

	public GMM(){
		super();
		// TODO Auto-generated constructor stub
		
		data = new ArrayList<Item>();
		
		readFile();
		value = new double[this.data_Lenth][2];
		for(int i=0;i<this.data_Lenth;i++){
			value[i][0] = data.get(i).attr1;
			value[i][1] = data.get(i).attr2;
		}
		
	}
	public void readFile() {
		File file = new File("Halfrign.txt");
		String temp;
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			while ((temp = br.readLine()) != null) {
				String[] arg = temp.split("	");
				Item it = new Item(Double.parseDouble(arg[0]),
						Double.parseDouble(arg[1]), Integer.parseInt(arg[2]));
				data.add(it);
			}
			data_Lenth = data.size();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Gaus_mix(){
		double[][] prob = new double[this.data_Lenth][2];
		double[] u1_old = new double[2];
		double[] u2_old = new double[2];
		double[] u1_new = new double[2];
		double[] u2_new = new double[2];
		double[] w = new double[2];
		double [][]cov1 = new double[2][2];
		double [][]cov2 = new double[2][2];
		Random random = new Random();
		w[0] = random.nextDouble();
		w[1] = 1-w[0];
//		w[0] = w[1] = 0.5;
		int t;
		for(int i=0;i<2;i++){
			cov1[i][i] = 1;
			cov2[i][i] = 1;
		}
		
		
		t = Math.abs(random.nextInt())%400;
//		t = 84;
		u1_new[0] = this.data.get(t).attr1;
		u1_new[1] = this.data.get(t).attr2;
		t = Math.abs(random.nextInt())%400;
//		t = 15;
		u2_new[0] = this.data.get(t).attr1;
		u2_new[1] = this.data.get(t).attr2;
		
		
		double deter1,deter2,temp,a,b;
		double[][] valuetemp,valuett ;
		int index = 0;
		
/*		display(w[0], u1_new, cov1);
		display(w[1], u2_new, cov2);*/
		
		do{
			index++;
			deter1 = cov1[0][0]*cov1[1][1]-cov1[0][1]*cov1[1][0];
			deter1 = Math.sqrt(deter1);
			
			deter2 = cov2[0][0]*cov2[1][1]-cov2[0][1]*cov2[1][0];
			deter2 = Math.sqrt(deter2);
			
			for(int i=0;i<2;i++){
				u1_old[i] = u1_new[i];
				u2_old[i] = u2_new[i];
				u1_new[i] = 0;
				u2_new[i] = 0;
 			}
			
			for(int i=0;i<this.data_Lenth;i++){
				
				valuetemp = Matrix.subtraction(chde(value[i]),chde(u1_old));
				valuetemp = Matrix.multiply(Matrix.multiply(valuetemp,Matrix.reverse(cov1)),Matrix.zhz(valuetemp));
				temp = valuetemp[0][0];
				a = w[0]*1/(2*Math.PI*deter1)*Math.pow(Math.E,-0.5*temp);
				
				
				valuetemp = Matrix.subtraction(chde(value[i]),chde(u2_old));
				valuetemp = Matrix.multiply(Matrix.multiply(valuetemp,Matrix.reverse(cov2)),Matrix.zhz(valuetemp));
				temp = valuetemp[0][0];
				b = w[1]*1/(2*Math.PI*deter2)*Math.pow(Math.E,-0.5*temp);
				
				prob[i][0] = a/(a+b);
				prob[i][1] = b/(a+b);
				
				
				u1_new[0] += prob[i][0]*value[i][0];
				u1_new[1] += prob[i][0]*value[i][1];
				u2_new[0] += prob[i][1]*value[i][0];
				u2_new[1] += prob[i][1]*value[i][1];				
			}
			
			a = 0;
			b = 0;
			for(int i=0;i<this.data_Lenth;i++){
				a += prob[i][0];
				b += prob[i][1];
			}
			temp = 0;
			for(int i=0;i<2;i++){      //更新均值
				u1_new[i] = u1_new[i]/a;
				u2_new[i] = u2_new[i]/b;
				temp += Math.abs(u1_new[i] - u1_old[i])
						+ Math.abs(u2_new[i] - u2_old[i]);
				//未考虑协方差变换  tmp
			}
			
			w[0] = a/this.data_Lenth;   //更新权值
			w[1] = b/this.data_Lenth;
			
			for(int j=0;j<2;j++){
				for(int k=0;k<2;k++){
					cov1[j][k] = 0;
					cov2[j][k] = 0;
				}
			}
			//更新协方差矩阵
			for(int i=0;i<this.data_Lenth;i++){
				valuetemp = Matrix.subtraction(chde(value[i]),chde(u1_old));
				valuetemp = Matrix.multiply(Matrix.zhz(valuetemp), valuetemp);
				
				valuett = Matrix.subtraction(chde(value[i]),chde(u2_old));
				valuett = Matrix.multiply(Matrix.zhz(valuett), valuett);
				for(int j=0;j<2;j++){
					for(int k=0;k<2;k++){
						valuetemp[j][k] = valuetemp[j][k]*prob[i][0];
						valuett[j][k] = valuett[j][k]*prob[i][1];
					}
				}
				cov1 = Matrix.add(cov1, valuetemp);
				cov2 = Matrix.add(valuett, cov2);
			}
			
			for(int j=0;j<2;j++){
				for(int k=0;k<2;k++){
					cov1[j][k] = cov1[j][k]/a;
					cov2[j][k] = cov2[j][k]/b;
				}
			}
/*			System.out.println(index+"th paratmeter=======");
			display(w[0], u1_new, cov1);
			display(w[1], u2_new, cov2);*/
		}while(temp > 0.0000001);
		
		System.out.println(index);
		
		ArrayList<Integer>cluster1 = new ArrayList<Integer>();
		ArrayList<Integer>cluster2 = new ArrayList<Integer>();
		for(int i=0;i<this.data_Lenth;i++){
			if(prob[i][0]>0.5){
				cluster1.add(i);
			}else{
				cluster2.add(i);
			}
		}

		System.out.println(accur(cluster1,cluster2));
	}
	
	public static void display(double weight, double[] mV, double[][] cM){
		System.out.println("weight:"+weight);
		System.out.println("meanVector:"+mV[0]+"\t"+mV[1]);
		System.out.println("covarianceMatrix:"+"\t"+cM[0][0]+"\t"+cM[0][1]);
		System.out.println("covarianceMatrix:"+"\t"+cM[1][0]+"\t"+cM[1][1]);
	}
	
	public double[][] chde(double[] a){
		double[][] temp = new double[1][a.length];
		for(int i=0;i<a.length;i++){
			temp[0][i] = a[i];
		}
		return temp;
		
	}
	
	public double accur(ArrayList<Integer> cluster1, ArrayList<Integer> cluster2) {

		int num1 = 0;
		int num2 = 0;
		for (int i = 0; i < cluster1.size(); i++) {
			if (this.data.get(cluster1.get(i)).label == 1) {
				num1++;
			}
		}
		if (num1 < cluster1.size() - num1) {
			num1 = cluster1.size() - num1;
		}

		for (int i = 0; i < cluster2.size(); i++) {
			if (this.data.get(cluster2.get(i)).label == 1) {
				num2++;
			}
		}
		if (num2 < cluster2.size() - num2) {
			num2 = cluster2.size() - num2;
		}
		
		System.out.println(cluster1.size()+"\t"+cluster2.size()+"\t"+num1+"\t"+num2);
		
		return (num1 + num2) * 1.0d / (cluster1.size() + cluster2.size());

	}

	public static void main(String[] args){
		new GMM().Gaus_mix();
	}
}

class Item {
	double attr1;
	double attr2;
	int label;

	public Item(double attr1, double attr2, int label) {
		super();
		this.attr1 = attr1;
		this.attr2 = attr2;
		this.label = label;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub

		return Double.toString(attr1) + Double.toString(attr2)
				+ Integer.toString(label);
	}

	public double getAttr1() {
		return attr1;
	}

	public void setAttr1(double attr1) {
		this.attr1 = attr1;
	}

	public double getAttr2() {
		return attr2;
	}

	public void setAttr2(double attr2) {
		this.attr2 = attr2;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

}
