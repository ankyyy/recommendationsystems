import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;


public class ContentBasedDataModel implements DataModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1846692269624110691L;
	/**
	 * 
	 */

	private File input;
	private String name;
	private String year;
	private String outline;
	private String runtime;
	private String rating;
	private String genre;
	private String director ;
	private String cast;
	private Map<String,List<String>> movie_map=new HashMap<String,List<String>>();
	/**
	 * @throws Exception 
	 * 
	 */
	
	public ContentBasedDataModel(File input) throws Exception
	{
		this.setInput(input);
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(input)));
		String temp;
		while((temp=br.readLine() )!= null)
		{
			List<String> movie_data=new ArrayList<String>();
			String movie[]=temp.split("%");
			name=movie[0];
			year=movie[1];
			outline=movie[2];
			runtime=movie[3];
			rating=movie[4];
			genre=movie[5];
			director=movie[6];
			cast=movie[7];
			movie_data.add(name);
			movie_data.add(year);
			movie_data.add(outline);
			movie_data.add(runtime);
			movie_data.add(rating);
			movie_data.add(genre);
			movie_data.add(director);
			movie_data.add(cast);
			movie_map.put(name,movie_data);
		}
		
		br.close();
		
	}
	@Override
	public void refresh(Collection<Refreshable> arg0) {

		// TODO Auto-generated method stub

	}

	@Override
	public LongPrimitiveIterator getItemIDs() throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FastIDSet getItemIDsFromUser(long arg0) throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getMaxPreference() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getMinPreference() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumItems() throws TasteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumUsers() throws TasteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumUsersWithPreferenceFor(long arg0) throws TasteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumUsersWithPreferenceFor(long arg0, long arg1)
			throws TasteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Long getPreferenceTime(long arg0, long arg1) throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Float getPreferenceValue(long arg0, long arg1) throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreferenceArray getPreferencesForItem(long arg0)
			throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreferenceArray getPreferencesFromUser(long arg0)
			throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongPrimitiveIterator getUserIDs() throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPreferenceValues() {
		// TODO Auto-generated method stub
		return false;
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
	public File getInput() {
		return input;
	}
	public void setInput(File input) {
		this.input = input;
	}
	public List<String> getNames() throws IOException {
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(input)));
		
		List<String> result = new ArrayList<String>();
		String temp;
		while((temp=br.readLine() )!= null)
		{
			result.add(temp.split("%")[0]);
		}
		br.close();
		return result;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getYear(String name) throws IOException {
		
		List<String> movie_list=new ArrayList<String>();
		movie_list=movie_map.get(name);
		String year=movie_list.get(1);
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	/**
	 * @return the outline
	 */
	public String getOutline(String name) {
		List<String> movie_list=new ArrayList<String>();
		movie_list=movie_map.get(name);
		String outline=movie_list.get(2);
		return outline;
	}
	/**
	 * @param outline the outline to set
	 */
	public void setOutline(String outline) {
		this.outline = outline;
	}
	/**
	 * @return the runtime
	 */
	public String getRuntime(String name) {
		List<String> movie_list=new ArrayList<String>();
		movie_list=movie_map.get(name);
		String runtime=movie_list.get(3);
	
		return runtime;
	}
	/**
	 * @param runtime the runtime to set
	 */
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}
	/**
	 * @return the rating
	 */
	public String getRating(String name) {
		List<String> movie_list=new ArrayList<String>();
		movie_list=movie_map.get(name);
		String rating=movie_list.get(4);
	
		return rating;
	}
	/**
	 * @param rating the rating to set
	 */
	public void setRating(String rating) {
		this.rating = rating;
	}
	/**
	 * @return the genre
	 */
	public String getGenre(String name) {
		List<String> movie_list=new ArrayList<String>();
		movie_list=movie_map.get(name);
		String genre=movie_list.get(5);
		return genre;
	}
	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}
	/**
	 * @return the director
	 */
	public String getDirector(String name) {
		List<String> movie_list=new ArrayList<String>();
		movie_list=movie_map.get(name);
		String director=movie_list.get(6);

		return director;
	}
	/**
	 * @param director the director to set
	 */
	public void setDirector(String director) {
		this.director = director;
	}
	/**
	 * @return the cast
	 */
	public String  getCast(String name) {
		List<String> movie_list=new ArrayList<String>();
		movie_list=movie_map.get(name);
		String cast=movie_list.get(7);
		return cast;
	
	}
	/**
	 * @param cast the cast to set
	 */
	public void setCast(String  cast) {
		this.cast = cast;
	}

}
