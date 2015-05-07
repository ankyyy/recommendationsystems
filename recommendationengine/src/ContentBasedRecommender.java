import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;


public class ContentBasedRecommender implements Recommender {
	private ContentBasedDataModel datamodel;
	private ContentBasedSimilarity similarity;

	 public ContentBasedRecommender(ContentBasedDataModel datamodel,ContentBasedSimilarity similarity) {
		 this.datamodel=datamodel;
		 this.similarity=similarity;
	}
	@Override
	public void refresh(Collection<Refreshable> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public float estimatePreference(long arg0, long arg1) throws TasteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DataModel getDataModel() {
		// TODO Auto-generated method stub
		
		return datamodel;
	}

	@Override
	public List<org.apache.mahout.cf.taste.recommender.RecommendedItem> recommend(long arg0, int arg1)
			throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<org.apache.mahout.cf.taste.recommender.RecommendedItem> recommend(long arg0, int arg1, IDRescorer arg2)
			throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removePreference(long arg0, long arg1) throws TasteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPreference(long arg0, long arg1, float arg2)
			throws TasteException {
		// TODO Auto-generated method stub

	}
	public List<ContentBasedRecommendedItem> recommend(String name,int number) throws IOException
	{
		List<String> list =datamodel.getNames();
		List<ContentBasedRecommendedItem> recommendations=new ArrayList<ContentBasedRecommendedItem>();
		
//		System.out.println(list);
		for(int i=0;i<list.size();i++)
		{
			if(list.get(i).equals(name))
				continue;
			double score=similarity.itemSimilarity(name, list.get(i));
			recommendations.add(new ContentBasedRecommendedItem(list.get(i),score));
		}
			Collections.sort(recommendations);
			return recommendations.subList(0, number);
	
	}
	
}
