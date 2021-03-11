package nlp.Bayes;
import java.util.List;
public class ClassEntry {
	private	float[] average;
	private	float[] covariance;
	private	List<float[]> wordsvec;
	/**
	 * @return the average
	 */
	public float[] getAverage() {
		return average;
	}
	/**
	 * @return the covariance
	 */
	public float[] getCovariance() {
		return covariance;
	}
	/**
	 * @return the wordsvec
	 */
	public List<float[]> getWordsvec() {
		return wordsvec;
	}
	/**
	 * @param average the average to set
	 */
	public void setAverage(float[] average) {
		this.average = average;
	}
	/**
	 * @param covariance the covariance to set
	 */
	public void setCovariance(float[] covariance) {
		this.covariance = covariance;
	}
	/**
	 * @param wordsvec the wordsvec to set
	 */
	public void setWordsvec(List<float[]> wordsvec) {
		this.wordsvec = wordsvec;
	}
	
}
