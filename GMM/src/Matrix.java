
import java.util.Arrays;


public class Matrix {
	private Matrix() {
	}

	public static double[][] reverse(double[][] matrix) {
		double[][] temp;
		
		// �õ�����Ľ���
		int m_length = matrix.length;
		// ����n*��2n-1������ʽ�������������ԭ����͵�λ����
		temp = new double[m_length][2 * m_length];
		// �������صľ���,��ʼ��
//		back_temp = Arrays.copyOf(matrix,matrix.length);
		// ��ԭ�����ֵ���� temp���󣬲���ӵ�λ�����ֵ
		double[][] back_temp =  new double[m_length][matrix[0].length];
		for (int x = 0; x < m_length; x++) {
			for (int y = 0; y < temp[0].length; y++) {
				if (y > m_length - 1) {
					if (x == (y - m_length))
						temp[x][y] = 1;
					else
						temp[x][y] = 0;
				} else {
					temp[x][y] = matrix[x][y];
				}
			}
		}
//		System.out.println("��Ͼ���:");
//		showMatrix(temp);
		// ��˹��Ԫ�������
		for (int x = 0; x < m_length; x++) {
			double var = temp[x][x];
			// �ж϶Խ�����Ԫ���Ƿ�Ϊ0���ǵĻ��������н��н����У���û������������
			// �����Ϊԭ����û�������Ȼ��ȡֵҪ��Ϊ0���е�ֵ
			for (int w = x; w < temp[0].length; w++) {
				if (temp[x][x] == 0) {
					int k;
					for (k = x + 1; k < temp.length; k++) {
						if (temp[k][k] != 0) {
							for (int t = 0; t < temp[0].length; t++) {
								// System.out.println(">>>"+k+"<<<");
								double tmp = temp[x][t];
								temp[x][t] = temp[k][t];
								temp[k][t] = tmp;
							}
							break;
						}
					}
					// System.out.println(""+k);
					// ��������޷���temp�������߻�Ϊ��λ���󣬷���ԭ����
					if (k >= temp.length)
						return back_temp;
					var = temp[x][x];
/*					System.out.print("�� " + x + "�α任ǰ�滻��Ԫ�ϵ� 0");
					System.out.println("(��  " + x + " ����� " + k + " �н��н���)��");
					showMatrix(temp);
*/
				}
				temp[x][w] /= var;
			}
			// ����x�е�Ԫ�س��Խ����ϵ�Ԫ���ⶼ��Ϊ0����������λ����
			for (int z = 0; z < m_length; z++) {
				double var_tmp = 0.0f;
				for (int w = x; w < temp[0].length; w++) {
					// System.out.println("-"+x+"-"+z+"-"+w+"+++" + temp[z][w]);
					if (w == x)
						var_tmp = temp[z][x];
					if (x != z)
						temp[z][w] -= (var_tmp * temp[x][w]);
				}
			}
			/*
			 * System.out.println("�� " + x + "�α任:"); showMatrix(temp);
			 */}

		// ȡ������ֵ
		for (int x = 0; x < m_length; x++) {
			for (int y = 0; y < m_length; y++) {
				back_temp[x][y] = temp[x][y + m_length];
			}
		}
//		showMatrix(matrix);
//		System.out.println("�����Ϊ");
//		showMatrix(back_temp);
		return back_temp;
	}

//	������֤���
	public static double[][] multiply(double[][] x, double[][] y) {
		double[][] r = new double[x.length][y[0].length];
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < y[0].length; j++) {
				r[i][j] = 0.0f;
				for (int k = 0; k < y.length; k++)
					r[i][j] += x[i][k] * y[k][j];
			}
		}
		/*
		 * for(int i=0;i<r.length;i++) { for( int j=0;j<r[0].length;j++)
		 * System.out.print(r[i][j]+" "); System.out.println(); }
		 */
		return r;
	}

	public static void showMatrix(double[][] ma) {
		int x = ma.length;
		int y = ma[0].length;
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++)
				System.out.print("\t" + ma[i][j]);
			System.out.println();
		}
	}
	
//�ж����������Ƿ���ͬ
	public static boolean equal(double [][] a, double [][] b){
		boolean flag = true;
		if(!((a.length == b.length)&& (a[0].length == b[0].length))){
			flag = false;
		}else{
			for(int i=0;i<a.length;i++){
				for(int j=0;j<a[0].length;j++){
					if(a[i][j] != b[i][j]){
						flag = false;
						break;
					}
				}
			}
			
		}
		return flag;
	}
	
	public static double[][] subtraction (double [][] a, double [][] b){
		if(!((a.length == b.length)&& (a[0].length == b[0].length))){
			System.out.println("������ά�����ԣ�����������");
			return null;
		}else{
			for(int i=0;i<a.length;i++){
				for(int j=0;j<a[0].length;j++){
				  a[i][j] = a[i][j] - b[i][j];
				}
			}
			return a;
		}
					
	}
	
	public static double[][] add (double [][] a, double [][] b){
		if(!((a.length == b.length)&& (a[0].length == b[0].length))){
			System.out.println("������ά�����ԣ��������ӷ�");
			return null;
		}else{
			for(int i=0;i<a.length;i++){
				for(int j=0;j<a[0].length;j++){
				  a[i][j] = a[i][j] + b[i][j];
				}
			}
			return a;
		}
					
	}
	
	
	public static double[][] zhz(double[][] a){
		int hang=a.length;
		int lie=a[0].length;
		double[][] result=new double[lie][hang];
		for(int i=0;i<hang;i++){
			for(int j=0;j<lie;j++){
			  result[j][i]=a[i][j];
			}
		}
		return result;
	}
	
	
}