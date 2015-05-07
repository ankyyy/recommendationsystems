import java.io.File;
import java.util.List;


public class ContentBasedRecommenderDemo {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ContentBasedDataModel datamodel=new ContentBasedDataModel(new File("data/main2.csv"));
		ContentBasedSimilarity similarity= new ContentBasedSimilarity(datamodel);
		ContentBasedRecommender recommend=new ContentBasedRecommender(datamodel,similarity);
		String name="Transformers: Age of Extinction";
		List<ContentBasedRecommendedItem> result=recommend.recommend(name,5);
		System.out.println("Recommendations  for "+name );
		System.out.println(name+"\n"+datamodel.getDirector(name)+"\n"+datamodel.getCast(name)+"\n"+datamodel.getGenre(name));
		System.out.println("-----------------------------------------------------------------------------");

		for(ContentBasedRecommendedItem res:result){
			System.out.println(res.getName()+"\n"+res.getScore()+"\n"+datamodel.getDirector(res.getName())+"\n"+datamodel.getCast(res.getName())+"\n"+datamodel.getGenre(res.getName()));
			System.out.println("-----------------------------------------------------------------------------");
		}
	}

}
