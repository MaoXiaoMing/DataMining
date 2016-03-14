import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

import java.io.*;
import java.util.Date;

public class LibSVM {

	public static void getInfo(svm_node[][] datas, double[] labels){
		try{
			BufferedReader br1 = new BufferedReader(new FileReader("input.txt"));
			BufferedReader br2 = new BufferedReader(new FileReader("OSTC.txt"));
			String get = null;
			int count = 0;
			while((get=br1.readLine()) != null){
				String[] tokens = get.split(" ");
				svm_node[] pa = new svm_node[tokens.length];
				int label = Integer.parseInt(br2.readLine());
				for(int i=0; i<tokens.length; i++){
					pa[i] = new svm_node();
					pa[i].index = i;
					pa[i].value = Double.parseDouble(tokens[i]);
				}
				pa[tokens.length-1].index = -1;
				datas[count] = pa;
				labels[count] = label;
				count++;
			}
			br1.close();
			br2.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
        //定义训练集点a{10.0, 10.0} 和 点b{-10.0, -10.0}，对应lable为{1.0, -1.0}
/*        svm_node pa0 = new svm_node();
        pa0.index = 0;
        pa0.value = 10.0;
        svm_node pa1 = new svm_node();
        pa1.index = -1;
        pa1.value = 10.0;
        svm_node pb0 = new svm_node();
        pb0.index = 0;
        pb0.value = -10.0;
        svm_node pb1 = new svm_node();
        pb1.index = 0;
        pb1.value = -10.0;
        svm_node[] pa = {pa0, pa1}; //点a
        svm_node[] pb = {pb0, pb1}; //点b
        svm_node[][] datas = {pa, pb}; //训练集的向量表
        double[] lables = {1.0, -1.0}; //a,b 对应的lable
*/        
		svm_node[][] datas = new svm_node[50000][];
		double[] lables = new double[50000];
		getInfo(datas, lables);
        //定义svm_problem对象
        svm_problem problem = new svm_problem();
        problem.l = 50000; //向量个数
        problem.x = datas; //训练集向量表
        problem.y = lables; //对应的lable数组
        
        //定义svm_parameter对象
        svm_parameter param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.LINEAR;
        param.cache_size = 100;
//        param.eps = 0.00001;
        param.eps = 0.001;
        param.C = 1;
        
        //训练SVM分类模型
        System.out.println(svm.svm_check_parameter(problem, param)); //如果参数没有问题，则svm.svm_check_parameter()函数返回null,否则返回error描述。
        svm_model model = svm.svm_train(problem, param); //svm.svm_train()训练出SVM分类模型
        System.out.println(new Date());
        //定义测试数据点c
/*        svm_node pc0 = new svm_node();
        pc0.index = 0;
        pc0.value = -0.1;
        svm_node pc1 = new svm_node();
        pc1.index = -1;
        pc1.value = 0.0;
        svm_node[] pc = {pc0, pc1};*/
        
        //预测测试数据的lable
//        System.out.println(svm.svm_predict(model, pc));
    }

}
