package nlp.Servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import nlp.lucene.util.JdbcUtil;
import nlp.robot.RobotReply;
import nlp.word2vec.Word2Vec;
import nlp.wordsplit.util.CnnUtils;

public class QuestionTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Word2Vec vec=new Word2Vec();
		try {
			vec.loadJavaModel("D:/NLP/data/wordsVector.model");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		String question="您好";
		List<String> quesList=CnnUtils.jiebaList(question);
		
		String sourcePath="D:/NLP/data/questions.txt";
		File file = new File(sourcePath);
		InputStreamReader read=null;
		try {
			read = new InputStreamReader(new FileInputStream(file), "UTF-8");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		BufferedReader bufferedReader = new BufferedReader(read);
		String linetxt = null;
		String question2=null;
		double max=0;
		try {
			while ((linetxt = bufferedReader.readLine()) != null&&max<0.9) 
			{
				List<String>lineList=CnnUtils.jiebaList(linetxt);
				double similarity=vec.sentenceSimilarity(quesList, lineList);
				if(similarity>max)
				{
					max=similarity;
					question2=linetxt;
				}
			}
			read.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		String answer=null;
		if(max>0.7)
		{
			JdbcUtil jdbc=new JdbcUtil();
			String sql="select * from QA where question=?";
			answer=jdbc.queryAnswer(sql,question2);
			jdbc.closeAll();
		}
		else   //注意看知识库是否需要成为链接
			answer=RobotReply.Reply(question)+"    "+" 点击知识库获取更多有关新冠肺炎的知识";
		System.out.println(answer);
	}

}
