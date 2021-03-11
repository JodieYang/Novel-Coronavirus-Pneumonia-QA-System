package nlp.word2vec;

public class GenerateVector {
	
	public static void genVec(String sourcePath,String goalPath)
	{
		try {
		Word2Vec.trainJavaModel(sourcePath, goalPath);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
    public static void main(String[] args) throws Exception {
    	genVec("D:/NLP/data/split_words.txt","D:/NLP/data/wordsVector.model");
    }
}
