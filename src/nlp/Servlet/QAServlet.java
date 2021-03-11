package nlp.Servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nlp.lucene.util.JdbcUtil;
import nlp.robot.RobotReply;
import nlp.word2vec.Word2Vec;
import nlp.wordsplit.util.CnnUtils;

/**
 * Servlet implementation class QAServlet
 */
@WebServlet("/QAServlet")
public class QAServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QAServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Word2Vec vec=new Word2Vec();
		try {
			vec.loadJavaModel("D:/NLP/data/wordsVector.model");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		String question=request.getParameter("question");
		System.out.println("问题："+question);
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
		//注意看知识库是否需要成为链接
		else 
			answer=RobotReply.Reply(question);
		//以防数据库返回的是空串
		//数据未做清洗，所以程序中要做过滤
		if(answer==null||answer=="")
			answer=RobotReply.Reply(question);
		System.out.println(answer);
		HttpSession session=request.getSession();
		if(session.getAttribute("qa")!=null)
		{
			ArrayList<QAPattern>qa=(ArrayList<QAPattern>)session.getAttribute("qa");
			QAPattern qap=new QAPattern();
			qap.setQuestion(question);
			qap.setAnswer(answer);
			qa.add(qap);
		}
		else
		{
			List<QAPattern>qa=new ArrayList<>();
			QAPattern qap=new QAPattern();
			qap.setQuestion(question);
			qap.setAnswer(answer);
			qa.add(qap);
			session.setAttribute("qa",qa);
		}
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
