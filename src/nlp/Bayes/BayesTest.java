package nlp.Bayes;

public class BayesTest {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String classIndex=Bayes.queryClass("D:/NLP/classificationIndex.txt", "D:/NLP/classifications/", "新冠肺炎是什么病?");
		System.out.println(classIndex);
	}

}
