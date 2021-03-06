package com.predictionmarketing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
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
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.MemoryIDMigrator;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

/**
 * This class implements the most simple recommender
 * which is directly bundles as a war and can be used
 * as a basis for doing recommendations.
 * 
 * It is implemented as a singleton because keeping the
 * data model in memory can be memory consuming.
 * If you require parallel access or a pool of
 * beans please consider using a static variable
 * for a session bean or using the
 * @javax.ejb.ConcurrencyManagement(javax.ejb.ConcurrencyManagementType)
 * 
 * @author Manuel Blechschmidt <blechschmidt@apaxo.de>
 *
 */
//@Singleton
public class FacebookRecommender {

	/**
	 * Log class which is used for sophisticated error
	 * logging.
	 */
	private Logger log = Logger.getLogger(FacebookRecommender.class.getName());
	
	/**
	 * Recommender which will be hold by this
	 * session bean.
	 */
	private Recommender recommender = null;
	
	/**
	 * An MemoryIDMigrator which is able to create for every string
	 * a long representation. Further it can store the string which
	 * were put in and it is possible to do the mapping back.
	 */
	private MemoryIDMigrator thing2long = new MemoryIDMigrator();
	
	/**
	 * The name of the file used for loading.
	 */
	private static String DATA_FILE_NAME = "data/DemoFriendsLikes.csv";
	
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
	 */
	@PostConstruct
	public void initRecommender() {
		 
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
			
			// parse out the header
			// we are not using the header
			String[] header = parser.getLine();
			
			// should output person name
			log.fine(header[0]+" "+header[1]);
			
			
			String[] line;
			
			// go through every line
			while((line = parser.getLine()) != null) {
			
				String person = line[0];
				String likeName = line[1];
				
				// other lines contained but not used
				// String category = line[2];
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
				userPrefList.add(new GenericPreference(userLong, itemLong, 1));
				log.fine("Adding "+person+"("+userLong+") to "+likeName+"("+itemLong+")");
			}
			
			// create the corresponding mahout data structure from the map
			FastByIDMap<PreferenceArray> preferecesOfUsersFastMap = new FastByIDMap<PreferenceArray>();
			for(Entry<Long, List<Preference>> entry : preferecesOfUsers.entrySet()) {
				preferecesOfUsersFastMap.put(entry.getKey(), new GenericUserPreferenceArray(entry.getValue()));
			}

			// create a data model 
			dataModel = new GenericDataModel(preferecesOfUsersFastMap);
			
			// Instantiate the recommender
			recommender = new GenericBooleanPrefItemBasedRecommender(dataModel, new LogLikelihoodSimilarity(dataModel));
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, DATA_FILE_NAME+" was not found", e);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error during reading line of file", e);
		}
	}
	
	/**
	 * Returns up to 10 recommendations for a certain person
	 * as a string array. If less then 10 things are found
	 * the array will contain less elements.
	 * If no recommendations are found the array will contain 0
	 * elements.
	 * @param personName The Facebook name of the person
	 * @return a string array with recommendations
	 * @throws TasteException If anything goes wrong a TasteException is thrown
	 */
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
		FacebookRecommender obj=new FacebookRecommender();
		obj.initRecommender();
		String[] results=obj.recommendThings("Willi Tscheschner");
		for(String result:results)
		{
			System.out.println(result);
		}
		
		
		
	}
}
