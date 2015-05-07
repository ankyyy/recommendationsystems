

public class ContentBasedRecommendedItem implements Comparable<Object>{
	private String name;
	private double score;
	public ContentBasedRecommendedItem(String name,double score){
		this.setName(name);
		this.setScore(score);
	}
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		ContentBasedRecommendedItem item=(ContentBasedRecommendedItem)arg0;
		if(item.score>score)
			return 1;
		else if(item.score<score)
			return -1;
		else
		 return 0;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}
	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}


}
