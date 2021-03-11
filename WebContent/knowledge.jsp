<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="nlp.lucene.*,java.util.List,java.util.ArrayList"
    pageEncoding="UTF-8"
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>新冠肺炎知识库</title>
<%
//	request.setCharacterEncoding("utf-8");
	String question="";
	Object o=session.getAttribute("question");
	if(o==null)
		question="开始搜索吧...";
	else
		question=o.toString();
	System.out.println("knowledge中的question："+question);
%>
</head>
<body Background="images/bear.jpg"> 
<div Align="center">
<font size=12 Color=black Face="宋体">
欢迎来到新冠肺炎知识库
</Font>
<br>
<form action="FullTextSearchServlet">
	<textarea  name="question" cols="80" placeholder="<%=question %>" id="introduce"></textarea>
	 <font size=16 Color=blank Face="隶书">
		 <input Type=submit Value="搜索"><br><br>
	 </Font>
</form>
<%  
	//如果换页的时候request失效，只能改把scoreDocs存在session中
	List<Doc>docList=(List<Doc>)session.getAttribute("answers");
	int itemNum=0;
	if(docList!=null)
		itemNum=docList.size();
	String rc=request.getParameter("currentPage");
	int currentPage=1;
	if(rc!=null)
		currentPage=Integer.parseInt(rc);
	int pageSize=5;
	int pageCounts=(itemNum-1)/pageSize+1;
	if(currentPage<1)currentPage=1;
	if(currentPage>pageCounts)currentPage=pageCounts;
	int start=(currentPage-1)*pageSize;
	if(docList!=null)
	{
		for(int i=start;i<start+pageSize&&i<itemNum;i++)
		{
			Doc doc=docList.get(i);
			String url=doc.getUrl();
			String Title=doc.getTitle();
			String content=doc.getContent();
			out.print("<a href="+url+">"+Title+"</a>"+"<br>");
			out.print(content+"<br>");
			out.print("<hr>");
		}
	}			
%>
<Font size=5 Color=blue Face="隶书">
<a href="knowledge.jsp?currentPage=<%=currentPage-1 %>">上一页</a>
<% 
	for(int i=1;i<=pageCounts;i++)
	{
		if(i!=currentPage)
		{
			out.print("<a href=\"knowledge.jsp?currentPage="+i+"\">"+i+"</a>&nbsp;&nbsp;");
		}
		else
		{
			out.print(i+"&nbsp;&nbsp;");
		}
	}
%>
<a href="knowledge.jsp?currentPage=<%=currentPage+1 %>">下一页</a>
</Font>
</Div>
</body>
</html>