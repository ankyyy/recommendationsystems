import java.io.IOException;
import java.util.Collection;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;


public class ContentBasedSimilarity implements ItemSimilarity {

	private ContentBasedDataModel datamodel;
	public ContentBasedSimilarity(ContentBasedDataModel datamodel ) {
		this.datamodel=datamodel;
		
		// TODO Auto-generated constructor stub
	}
	@Override
	public void refresh(Collection<Refreshable> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public long[] allSimilarItemIDs(long arg0) throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] itemSimilarities(long arg0, long[] arg1)
			throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double itemSimilarity(long arg0, long arg1) throws TasteException {
		// TODO Auto-generated method stub
		
		return 0;
	}
	public double itemSimilarity(String name1,String name2) throws IOException{
		double director_score=compareDirector(name1,name2);
		double cast_score=compareCast(name1,name2);
		double year_score=compareYear(name1,name2);
		double genre_score=compareGenre(name1, name2);
		
		double score=(double)(genre_score*2+director_score*2+cast_score*2+year_score*1)/(double)7;
		return score;
		
	}
	private double compareYear(String name1, String name2) throws IOException {
		// TODO Auto-generated method stub
		String year1=datamodel.getYear(name1);
		String year2=datamodel.getYear(name2);
		if(year1.equals("not available") || year2.equals("not available"))
			return 0;
		
		double score=0;
		if(year1.equals(year2))
		{
			score=1;
		}
//		System.out.println(score);
		return score;
	}
	private double compareCast(String name1, String name2) throws IOException {
		// TODO Auto-generated method stub
		String cast1=datamodel.getCast(name1);
		String cast2= datamodel.getCast(name2);
		int len1=cast1.split("-").length;
		int len2=cast2.split("-").length;
		if(len1==0 || len2==0)
			return 0;
		
		String cast_array1[]=new String[len1];
		String cast_array2[]=new String[len2];
		cast_array1=cast1.split("-");
		cast_array2=cast2.split("-");
		int count=0;
		for(int i=1;i<len1;i++)
		{
			for(int j=1;j<len2;j++)
			{
				if(cast_array1[i].equals(cast_array2[j]))
				{
					count=count+1;
				}
			}
		}
		double score=(double)count/(double)Math.min(len2-1, len1-1);		
//		System.out.println(len1);
		return score;
	}
	private double compareDirector(String name1, String name2) throws IOException {
		// TODO Auto-generated method stub
		
		String director1= datamodel.getDirector(name1);
		String director2= datamodel.getDirector(name2);
		
		if(director1.equals("not available") || director2.equals("not available"))
			return 0;
		double score=0;
		if(director1.equals(director2))
		{
			score=1;
		}
//		System.out.println(score);
		return score;
	}
	private double compareGenre(String name1,String name2) throws IOException
	{
		String genre1= datamodel.getGenre(name1);
		String genre2= datamodel.getGenre(name2);
		int len1=genre1.split("-").length;
		int len2=genre2.split("-").length;
		if(len1==0 || len2==0)
			return 0;
		String genre_array1[]=genre1.split("-");
		String genre_array2[]=genre2.split("-");
		
		int count=0;
		for(int i=0;i<genre_array1.length;i++)
		{
			for(int j=0;j<genre_array2.length;j++)
			{
				if(genre_array1[i].equals(genre_array2[j]))
				{
					count=count+1;
				}
			}
		}
		double score=(double)count/(double)Math.min(len2, len1);	
//		System.out.println(score);
			
		return score;	
	}

}
