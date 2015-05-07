/*
 * Author:Ankit Kumar Mishra
 * Date:20/04/2015
 * 
 * Following Class calculates similarity matrix based on user-user similarity
 * It generates random item ids for each user and for every pair of user calculates similarity value
 * similarity=no of matched items/maximum number of items between both users
 * This is stored in a similarity matrix and written into a file called result.csv into csv format
 * Time complexity=O(n^3)
 */




import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Random;

public class Userbasedrecommendation {
private static int[][] array=new int[2000][2000];
private static float[][] similarity=new float[2000][2000];
	public static void main(String[] args) {
		
		Random rand=new Random();
		int i,j;
		try{
			for(i=0;i<100;i++)
		{
				int temp[]=new int[100];
				int size=rand.nextInt(100);
			for(j=0;j<size;j++)
			{
				temp[j]=rand.nextInt(90)+1;

			}

			Arrays.sort(temp);
			for(j=0;j<size;j++)
			{
				array[i][j]=temp[j]+1;
				
			}
		}
			for(i=0;i<100;i++)
			{
				System.out.print("User "+(i+1)+": ");
				for(j=0;j<100;j++)
				{
					System.out.print(array[i][j]+" , ");
				}
				System.out.println();
			}
			int k;
			for(k=0;k<100;k++){
		for(i=0;i<100;i++)
		{
			int count=0,size=0;
			for(j=0;j<100;j++)
			{
				if(array[k][j]!=0 && array[i][j]!=0 && array[k][j] == array[i][j])
					count=count+1;
				if(array[k][j]!=0 || array[i][j]!=0)
					size=size+1;
			//	System.out.print(array[i][j]+" ");
			}
			if(size==0)
				size=size+1; 
			float res=(count/size)*100;
			similarity[k][i]=res;
			}
		}
			
			System.out.println("Executing!!");
			File f =new File("data/result.csv");
			FileOutputStream out=new FileOutputStream(f);
			DataOutputStream outd=new DataOutputStream(out);
			outd.writeChars("User based Similarity matrix\nRow and Column represents each user \n\n");
		//Writing into file result.csv
			for(i=0;i<100;i++)
				{
//					 System.out.print("User "+(i+1)+": ");
					String s="User "+(i+1)+":, ";
					outd.writeChars(s);
					for(j=0;j<100;j++)
					{
//						System.out.print(similarity[i][j]+" , ");
						
						outd.writeFloat(similarity[i][j]);
						outd.writeChars(similarity[i][j]+", ");
//						System.out.format("%f ,",similarity[i][j]); 
					}
				outd.writeChars("\n");
				//System.out.println();
				
				}
			outd.close();
			for(i=0;i<100;i++)
			{
				System.out.print("User "+(i+1)+":");
				for(j=0;j<100;j++)
				{
					if(similarity[i][j]==100 && i!=j)
						System.out.print(" User "+(j+1));
				}
				System.out.println();
			}
			System.out.println("Completed!!");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
