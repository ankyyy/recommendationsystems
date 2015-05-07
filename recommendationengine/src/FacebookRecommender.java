
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVParser;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.MemoryIDMigrator;
import org.apache.mahout.cf.taste.impl.model.mongodb.MongoDBDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

//@Singleton
public class FacebookRecommender {

	private static final DataModelBuilder NULL = null;

	/**
	 * Log class which is used for sophisticated error
	 * logging.
	 */
	private Logger log = Logger.getLogger(FacebookRecommender.class.getName());
	
	/**
	 * Recommender which will be hold by this
	 * session bean.
	 */
	private static Recommender recommender = null;
	
	/**
	 * An MemoryIDMigrator which is able to create for every string
	 * a long representation. Further it can store the string which
	 * were put in and it is possible to do the mapping back.
	 */
	private static MemoryIDMigrator thing2long = new MemoryIDMigrator();
	
	/**
	 * The name of the file used for loading.
	 */
	private static String DATA_FILE_NAME = "data/main.csv";
	
	/**
	 * A data model which is needed for the recommender implementation.
	 * It provides a standardized interface for using the recommender.
	 * The data model can be become quite memory consuming.
	 * In our case it will be around 2 mb. 
	 */
	private static DataModel dataModel;
	
	/**
	 * This function will init the recommender
	 * it will load the CSV file from the resource folder,
	 * parse it and create the necessary data structures
	 * to create a recommender.
	 * The 
	 * @throws TasteException 
	 */
	@PostConstruct
	public void initRecommender() throws TasteException {
		 
		try {
			// get the file which is part of the WAR as
//			URL url = getClass().getClassLoader().getResource(DATA_FILE_NAME);
			
			// create a file out of the resource
			File data = new File(DATA_FILE_NAME);
			
			// create a map for saving the preferences (likes) for
			// a certain person
			Map<Long,List<Preference>> preferecesOfUsers = new HashMap<Long,List<Preference>>();
			
			// use a CSV parser for reading the file
			// use UTF-8 as character set
			CSVParser parser = new CSVParser(new InputStreamReader(new FileInputStream(data), "UTF-8"));
	
			String[] line;
			
			// go through every line
			while((line = parser.getLine()) != null) {
			
				
				String person = line[0];
				String likeName = line[1];
				float value = Float.parseFloat(line[2]);
				// String id = line[3];
				// String created_time = line[4];
				
				// create a long from the person name
				long userLong = thing2long.toLongID(person);
				
				// store the mapping for the user
				thing2long.storeMapping(userLong, person);
				
				// create a long from the like name
				long itemLong = thing2long.toLongID(likeName);
				
				// store the mapping for the item
				thing2long.storeMapping(itemLong, likeName);
				
				List<Preference> userPrefList;
				// if we already have a userPrefList use it
				// otherwise create a new one.
				if((userPrefList = preferecesOfUsers.get(userLong)) == null) {
					userPrefList = new ArrayList<Preference>();
					preferecesOfUsers.put(userLong, userPrefList);
				}
				// add the like that we just found to this user
				userPrefList.add(new GenericPreference(userLong, itemLong,value));
				log.fine("Adding "+person+"("+userLong+") to "+likeName+"("+itemLong+")");
			}
		
			// create the corresponding mahout data structure from the map
			FastByIDMap<PreferenceArray> preferecesOfUsersFastMap = new FastByIDMap<PreferenceArray>();
			for(Entry<Long, List<Preference>> entry : preferecesOfUsers.entrySet()) {
				preferecesOfUsersFastMap.put(entry.getKey(), new GenericUserPreferenceArray(entry.getValue()));
			}
			
			
			// create a data model 
			dataModel = new GenericDataModel(preferecesOfUsersFastMap);
//			dataModel = new ContentBasedDataModel(userPrefList);
//			dataModel = new FileDataModel(new File("data/main.csv"));
//			System.out.println(dataModel.getMaxPreference());
			
			// Instantiate the recommender

			
//			recommender = new GenericItemBasedRecommender(dataModel, new EuclideanDistanceSimilarity(dataModel));
//			recommender = new GenericItemBasedRecommender(dataModel, new TanimotoCoefficientSimilarity(dataModel));
			recommender = new GenericItemBasedRecommender(dataModel, new LogLikelihoodSimilarity(dataModel));
//			recommender = new GenericItemBasedRecommender(dataModel, new PearsonCorrelationSimilarity(dataModel));
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, DATA_FILE_NAME+" was not found", e);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error during reading line of file", e);
		}
	}
	
	
	public String[] recommendThings(String personName) throws TasteException {
		List<String> recommendations = new ArrayList<String>(); 
		try {
			List<RecommendedItem> items = recommender.recommend(thing2long.toLongID(personName), 10);
			for(RecommendedItem item : items) {
				recommendations.add(thing2long.toStringID(item.getItemID()));
			}
		} catch (TasteException e) {
			log.log(Level.SEVERE, "Error during retrieving recommendations", e);
			throw e;
		}
		return recommendations.toArray(new String[recommendations.size()]);
	}
	public static void main (String a[]) throws TasteException
	{
		RandomUtils.useTestSeed();
		FacebookRecommender obj=new FacebookRecommender();
		obj.initRecommender();
//		UserSimilarity usersimilarity = new LogLikelihoodSimilarity(dataModel);
//		UserNeighborhood userneighborhood = new NearestNUserNeighborhood(100,usersimilarity,dataModel);
//		UserBasedRecommender rec= new GenericUserBasedRecommender(dataModel,  userneighborhood,usersimilarity);
		
//		for(LongPrimitiveIterator users = dataModel.getUserIDs(); users.hasNext();) {
//			long itemId = users.nextLong();
//			long[] recommendations = rec.mostSimilarUserIDs(itemId, 2);
//			List<RecommendedItem> recommendeditem = rec.recommend(itemId, 2);
//			
//			for(RecommendedItem recommendation : recommendeditem) {
//				System.out.println(thing2long.toStringID(itemId)+ "," + thing2long.toStringID(recommendation.getItemID()) );
//			}
//			}
		
		
////		String[] results=obj.recommendThings("16117882");
////		int x=1;
		for(LongPrimitiveIterator items = dataModel.getItemIDs(); items.hasNext();) {
			long itemId = items.nextLong();
			List<RecommendedItem>recommendations = ((GenericItemBasedRecommender) recommender).mostSimilarItems(itemId,5 );
			
			for(RecommendedItem recommendation : recommendations) {
				System.out.println(thing2long.toStringID(itemId)+ "," + thing2long.toStringID(recommendation.getItemID()) + "," + recommendation.getValue());
			}
		}
//		for(String result:results)
//		{
//			System.out.println(result);
//		}
		
//		RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator ();
		
		
		RecommenderEvaluator eval= new AverageAbsoluteDifferenceRecommenderEvaluator();
		RecommenderBuilder build= new RecommenderBuilder(){

			@Override
			public Recommender buildRecommender(DataModel arg0)
					throws TasteException {
		
//				UserSimilarity usersimilarity = new SpearmanCorrelationSimilarity(dataModel);
				
//				UserNeighborhood userneighborhood = new NearestNUserNeighborhood(600,usersimilarity,dataModel);
//				UserNeighborhood userneighborhood = new ThresholdUserNeighborhood(0.2,usersimilarity,dataModel);
//				UserBasedRecommender recommender = new GenericUserBasedRecommender(dataModel,  userneighborhood,usersimilarity);
				return recommender;
			}
			
		};
		double score = eval.evaluate(build,NULL, dataModel, 0.53,1.0);
		System.out.println(score);
//		IRStatistics stats = evaluator.evaluate(build, null, dataModel, null, 5,
//				GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,
//				0.5);
//		System.out.println(stats.getPrecision());
//		System.out.println(stats.getRecall());

//		1.0455876202425756E
	}}

