package nlp.Servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import nlp.lucene.DbSearchDemo;
import nlp.lucene.Doc;

/**
 * Servlet implementation class FullTextSearchServlet
 */
@WebServlet("/FullTextSearchServlet")
public class FullTextSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FullTextSearchServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String question=request.getParameter("question");
		System.out.println("FullTextSearchServlet中的question："+question);
		DbSearchDemo demo = new DbSearchDemo();
		List<Doc> docList=demo.search(question);
		HttpSession session=request.getSession();
		session.setAttribute("answers", docList);
		session.setAttribute("question", question);
		request.getRequestDispatcher("knowledge.jsp?currentPage=1").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
