<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="java.util.List,java.util.ArrayList,nlp.Servlet.QAPattern"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width,initial-scale=1">
	<title>新冠肺炎知识问答系统</title>
<style>
.box{
	width: 100%;
	height: 500px;
	position:absolute;
	top:0;
	background: url("images/star.gif") no-repeat;  //在这里可以进行更改
}
#content{
	width: 98%;
	height: 500px;
	margin-right:1%;
	margin-left:1%;
}
form{
	width: 100%;
	height:60px;
	line-height:40px;
	border-top:1px solid #ccc; 
	position:absolute;
	bottom:0;
	left:0;
}
#content form img{
	border:1px solid #ccc;
	border-radius:7px;
	padding:2px; 
	cursor:pointer;
	margin:5px 0 0 5px;
	float:left;
	width:40px;
	height:40px;
}
#content form input[type='text']{
	width:80%;
	height:40px;
	line-height:27px;
	border-radius:7px; 
	border:1px solid #ccc;
	float:left;
	margin-left:5px; 
	margin-top:5px; 
	outline:none;
	padding:0 5px; 
}
#content form input[type='button']{
	width:12%;
	height: 40px;
	border-radius:5px; 
	background:#fff;
	border:none;
	margin-left:1%; 
	cursor:pointer;
	font-size:27px;
	margin-top:5px;
}

.info_box{
	width: 100%;
	height:440px;
	overflow-y:auto;
}
.info_box::-webkit-scrollbar {display:none}
.info_box div{
	margin:0 0 5px;
	position:relative;
}
.info_box .info_r{
	
}
.info_box .info_l{

}
.info_box .info_l img{
	position:absolute;
	left:0;
	top:0;
}
.info_box .info_r img{
	position:absolute;
	right:0;
	top:0;
}
.info_box .info_l span{margin-left: 60px;margin-top:16px;}
.info_box .info_r span{margin-left:28%;margin-top:16px;}


.info_box span{
	width: 66%;
	height: auto;
	font-size:14px;
	background:#ddd;
	border-radius: 10px;
	padding:8px;  
	color:#333;
	display: block;
	position:relative;
	top:0;
}


.info_box .info_l img{
	float:left;
	width:50px;
    height:50px;
}



.info_r img{
	float:right;
	width:50px;
    height:50px;
}
</style>
<%
	String hello="欢迎来到疫情问答系统，我是机器人 小疫，快来向我提问吧~";
	List<QAPattern> qa=null;
	if(session.getAttribute("qa")!=null)
		qa=(ArrayList<QAPattern>)session.getAttribute("qa");
%>
</head>
<Body>
<div class="box">
	<div id="content">
		<div class="info_box">
			<div class="info_l">
				<img src="images/e_doctor.jpg" class='pic_l'>
				<span class='infoo'>欢迎进入新冠肺炎问答系统，我是机器人 小疫，快来向我提问吧！点击<a href="medium.jsp">知识库</a>获取更多新冠肺炎相关资讯</span>
			</div>	 
		</div>
		<%
			if(qa!=null)
			{
				for(int i=0;i<qa.size();i++)
				{
					String question=qa.get(i).getQuestion();
					String answer=qa.get(i).getAnswer();
					%>
					<script>
						var nDiv = document.createElement('div');
						var spans = document.createElement('span');
						var imgs = document.createElement('img');
						var sTxt = document.createTextNode("<%=question %>");
						var info_box = document.getElementsByClassName('info_box')[0];
						spans.appendChild(sTxt);
						nDiv.appendChild(spans);
						nDiv.appendChild(imgs);
						// nDiv.style.display='block';
						info_box.insertBefore(nDiv,info_box.lastChild);
						spans.className='infor';
					    nDiv.className='info_r';
					    imgs.src='images/me.jpg';
					    
						var nDiv = document.createElement('div');
						var spans = document.createElement('span');
						var imgs = document.createElement('img');
						var sTxt = document.createTextNode("<%=answer%>");
						var info_box = document.getElementsByClassName('info_box')[0];
						spans.appendChild(sTxt);
						nDiv.appendChild(spans);
						nDiv.appendChild(imgs);
						// nDiv.style.display='block';
						info_box.insertBefore(nDiv,info_box.lastChild);
					    spans.className='infol';
						nDiv.className='info_l';
						imgs.src='images/e_doctor.jpg';
					</script>
					<% 
				}
			}
		%>
		<form name="form1">
			<img src="images/me.jpg" id='pic'>
			<input type="text" name="question" placeholder='开始提问吧！' id='inp'>
			<input type="button" value='发送' id='send'>
		</form>
	</div>
</div>

<script>
var send =document.getElementById('send');
var pic =document.getElementById('pic');
var txt =document.getElementById('inp');
var info_box = document.getElementsByClassName('info_box')[0];

var onoff=true;
pic.onclick=function(){
	if(onoff){
		pic.src='images/me.jpg';
		onoff=false;
	}
	else{
		pic.src='images/e_doctor.jpg';
		onoff=true;
	}
};

send.onclick=function(){
	if(txt.value==''){
		alert('请输入内容');
	}
	
	else{
		form1.action="QAServlet";
  		form1.submit();
	}
}
</script>
	
</body>
</html>

