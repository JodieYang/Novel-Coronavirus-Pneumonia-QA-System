<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>新冠肺炎知识库</title>
</head>
<Body Background="images/sky.jpg"> 
<Div style="text-align:center">
<br><br><br><br><br><br><br>
<Font size=16 Color=black Face="宋体">
欢迎来到新冠肺炎知识库
</Font>
<br><br>
<form action="FullTextSearchServlet">
 <textarea  name="question" cols="80" placeholder='开始提问吧！' id="introduce"></textarea>
 <Input Type=submit Name="ok" Value="提问"><br><br>
</form>
</Div>
</body>
</html>