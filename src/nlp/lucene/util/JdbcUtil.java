package nlp.lucene.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import nlp.lucene.Doc;

public class JdbcUtil
{
	private static final Logger log = LoggerFactory.getLogger(JdbcUtil.class);//日志对象
	private Connection con = null;//创建的连接对象
	private PreparedStatement ps = null;//创建预编译语句对象
	private ResultSet rs = null;//创建结果集对象
	
	/**
	 * 无参的构造方法，使用默认配置的一些东西，默认情况下不使用连接池
	 */
	public JdbcUtil()
	{
		//注意修改Album数据库名称
		String url="jdbc:sqlserver://localhost:1433;DatabaseName=COVID";
//		String url="jdbc:mysql://172.16.4.253:3306/new-health-province?useUnicode=true&characterEncoding=utf-8";
		String user="sa";
		String password="123456";
		con=getConnection("sqlserver", url, user, password);
	}
	
	/**
	 * 使用JNDI获取连接，需事先在tomcat以及项目的web.xml里面配置好各种参数
	 */
	public JdbcUtil(String jndi)
	{
		con=getConnection(jndi);
	}
	
	/**
	 * 有参的构造方法
	 * @param url
	 * @param user 用户名
	 * @param password 
	 */
	public JdbcUtil(String url,String user,String password)
	{
		con=getConnection("sqlserver", url, user, password);
	}
	
	/**
	 * 有参的构造方法
	 * @param url
	 * @param user 用户名
	 * @param password 
	 */
	public JdbcUtil(String database,String url,String user,String password)
	{
		con=getConnection(database, url, user, password);
	}
	
	
	private Connection getConnection(String database,String url,String user,String password)
	{
		try
		{
			String className = "";
			if("mysql".equals(database))
				className="com.mysql.jdbc.Driver";
			else if("orcal".equals(database))
				className="oracle.jdbc.driver.OracleDriver";
			else className="com.microsoft.sqlserver.jdbc.SQLServerDriver";
			Class.forName(className);
			log.debug("开始尝试连接数据库！");
			con = DriverManager.getConnection(url, user, password);
			log.debug("连接成功！");
		}
		catch (Exception e)
		{
			log.error("连接数据库失败！", e);
		}
		return con;
	}
	
	/**
	 * 获取连接，使用数据库连接池
	 * @param jndi 配置在tomcat的context.xml里面的东西
	 * @return 创建的连接对象
	 */
	public Connection getConnection(String jndi)
	{
		try
		{
			log.debug("开始尝试连接数据库！");
			Context context=new InitialContext();
			DataSource dataSource=(DataSource)context.lookup("java:comp/env/"+jndi);
			con=dataSource.getConnection();
			log.debug("连接成功！");
		}
		catch (Exception e)
		{
			log.error("连接数据库失败！", e);
		}
		return con;
	}
	
	/**
	 * 关闭所有占有的资源
	 */
	public void closeAll()
	{
		try
		{
			if(rs!=null)
				rs.close();
			if(ps!=null)
				ps.close();
			if (con != null)
				con.close();
			log.debug("数据库连接已关闭！");
		}
		catch (Exception e)
		{
			log.error("尝试关闭数据库连接时出错：", e);
		}
	}
	
	/**
	 * 执行数据库的更新操作，包括增、删、改，执行后需手动关闭数据库连接
	 * @param sql 需要执行的预编译语句
	 * @param params 预编译语句的参数列表
	 * @return 受影响的行数
	 */
	public int update(String sql,String... params)
	{
		log.debug("执行SQL："+sql);
		int count = 0;//受影响的行数
		try
		{
			ps=con.prepareStatement(sql);
			for(int i=0;i<params.length;i++)
				ps.setString(i+1, params[i]);
			count=ps.executeUpdate();
		}
		catch (SQLException e)
		{
			log.debug("执行update时出错：", e);
		}
		log.debug("受影响的行数:{}", count);
		return count;
	}
	
	/**
	 * 执行数据库的更新操作，执行后会自动关闭数据库连接
	 * @param sql 需要执行的预编译语句
	 * @param params 预编译语句的参数列表
	 * @return 受影响的行数
	 */
	public int updateWithClose(String sql,String... params)
	{
		int count = update(sql, params);
		closeAll();//关闭连接
		return count;
	}
	
	/**
	 * 执行数据库的查询操作
	 * @param sql 需要执行的预编译语句
	 * @param params 预编译语句的参数列表
	 * @return 返回查询的结果集，类型为ResultSet
	 */
	public ResultSet query(String sql,String... params)
	{
		log.debug("执行查询SQL："+sql);
		try
		{
			ps=con.prepareStatement(sql);
			for(int i=0;i<params.length;i++)
				ps.setString(i+1, params[i]);
			rs=ps.executeQuery();
		}
		catch (SQLException e)
		{
			log.debug("执行query时出错：", e);
		}
		//由于查询是返回结果集，在调用此方法的时候还要用ResultSet.Next()的方法，
		//所以这里还不能关闭数据库连接
		return rs;
	}
	
	public List<Doc> queryDoc(String sql,String... params)
	{
		
		List<Doc>docList=new ArrayList<>();
		log.debug("执行查询SQL："+sql);
		try
		{
			rs=query(sql,params);
			while(rs!=null&&rs.next())
			{
				String Title=rs.getString("Title");
				String url=rs.getString("url");
				String content=rs.getString("content");
				//确保没有空值
				if(Title!=null&&url!=null&&content!=null)
				{
					Doc doc=new Doc();
					doc.setTitle(Title);
					doc.setUrl(url);
					doc.setContent(content);
					docList.add(doc);
				}
			}
		}
		catch (SQLException e)
		{
			log.debug("执行query时出错：", e);
		}
		
		
		//由于查询是返回结果集，在调用此方法的时候还要用ResultSet.Next()的方法，
		//所以这里还不能关闭数据库连接
		return docList;
	}
	
	
	/**
	 * 执行数据库的查询操作，带分页
	 * @param sql 需要执行的预编译语句
	 * @param params 预编译语句的参数列表
	 * @return 返回查询的结果集，类型为ResultSet
	 */
	//只要不关闭，就可以返回结果集，没关系
	//但用完记得关闭
	public ResultSet queryByPage(int page,int pageSize, String sql,String... params)
	{
		sql = sql + " limit "+(page-1)*pageSize+","+pageSize;
		return query(sql, params);
	}
	
	/**
	 * 查询结果集第一行的第一列的int型数据，一般都是获取count(*)的值
	 * @param sql 需要执行的预编译语句
	 * @param params 预编译语句的参数列表
	 * @return 返回的int型数据
	 */
	public int queryInt(String sql,String... params)
	{
		ResultSet resultSet = query(sql, params);
		int result = 0;
		try
		{
			if(resultSet!=null&&resultSet.next())
				result = resultSet.getInt(1);//注意第一个索引是1，一般都是获取count(*)的值
		}
		catch (Exception e)
		{
			log.error("执行queryInt出错：", e);
		}
		return result;
	}

	/**
	 * 查询结果集第一行的第一列的String型数据
	 * @param sql 需要执行的预编译语句
	 * @param params 预编译语句的参数列表
	 * @return 返回的int型数据
	 */
	public String queryString(String sql,String... params)
	{
		ResultSet rs = query(sql, params);
		String result = "";
		try
		{
			if(rs!=null&&rs.next())
				result = rs.getString(1);//注意第一个索引是1
		}
		catch (Exception e)
		{
			log.error("执行queryString出错：", e);
		}
		return result;
	}
	public String queryAnswer(String sql,String... params)
	{
		ResultSet rs = query(sql, params);
		String result = "";
		try
		{
			if(rs!=null&&rs.next())
				result = rs.getString("answer");//注意第一个索引是1
		}
		catch (Exception e)
		{
			log.error("执行queryString出错：", e);
		}
		return result;
	}

	
	/**
	 * 查询结果集最后一行的第一列的int型数据，一般都是获取count(*)的值
	 * @param sql 需要执行的预编译语句
	 * @param params 预编译语句的参数列表
	 * @return 返回的int型数据
	 */
	public int queryIntWithClose(String sql,String... params)
	{
		int result = queryInt(sql, params);
		closeAll();
		return result;
	}
	
	public static void main(String[] args) throws Exception
	{
		//关于查询的示例代码
		JdbcUtil jdbc = new JdbcUtil();
		String Title="新冠肺炎与普通肺炎的差别是什么？";
		String sql="select * from QA where question=?";
		String answer= jdbc.queryAnswer(sql,Title);
		System.out.println(answer);
		String sql1="select * from knowledge";
		List<Doc>docList=jdbc.queryDoc(sql1);
		System.out.println(docList.size());
		jdbc.closeAll();
	}
}
