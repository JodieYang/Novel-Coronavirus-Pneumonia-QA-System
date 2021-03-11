package nlp.lucene;

import java.nio.file.FileSystems;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
//import org.apache.lucene.document.StringField;
//import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;
import java.util.List;
import java.util.ArrayList;
import nlp.lucene.util.JdbcUtil;

/**
 * 基于Lucene5.5.4的数据库搜索demo
 */
public class DbSearchDemo
{
	public static final String INDEX_PATH = "D:\\NLP\\data\\lucene2";
	//记得改数据库名称！！！
	public static final String JDBC_URL = "jdbc:sqlserver://localhost:1433;DatabaseName=COVID";
	public static final String USER = "sa";
	public static final String PWD = "123456";
	
	/**
	 * 创建索引
	 */
	public void creatIndex()
	{
		IndexWriter indexWriter = null;
		try
		{
			Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(INDEX_PATH));
			//Analyzer analyzer = new StandardAnalyzer();
			Analyzer analyzer = new IKAnalyzer(true);
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			indexWriter = new IndexWriter(directory, indexWriterConfig);
			indexWriter.deleteAll();// 清除以前的index
			JdbcUtil jdbc = new JdbcUtil(JDBC_URL, USER, PWD);
			List<Doc> docList = jdbc.queryDoc("select * from knowledge");
			jdbc.closeAll();
			if(docList!=null)
			{
				for(int i=0;i<docList.size();i++)
				{	Document document = new Document();
				//	document.add(new Field("id", rs.getString("id"), TextField.TYPE_STORED));
					document.add(new Field("Title", docList.get(i).getTitle(), TextField.TYPE_STORED));
					document.add(new Field("content", docList.get(i).getContent(), TextField.TYPE_STORED));
				//	document.add(new Field("tag", rs.getString("tags"), TextField.TYPE_STORED));
					document.add(new Field("url",docList.get(i).getUrl(), TextField.TYPE_STORED));
					//indexWriter.addDocuments(Collection<Document>);
					indexWriter.addDocument(document);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{//注意：没有indexWriter.commit();!!!
				if(indexWriter != null) indexWriter.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 搜索
	 */
	public List<Doc> search(String keyWord)
	{
		DirectoryReader directoryReader = null;
		List<Doc> docList=new ArrayList<>();
		try
		{
			// 1、创建Directory
			Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(INDEX_PATH));
			// 2、创建IndexReader
			directoryReader = DirectoryReader.open(directory);
			// 3、根据IndexReader创建IndexSearch
			IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
			// 4、创建搜索的Query
			// Analyzer analyzer = new StandardAnalyzer();
			Analyzer analyzer = new IKAnalyzer(true); // 使用IK分词
			
			// 简单的查询，创建Query表示搜索域为content包含keyWord的文档
			//Query query = new QueryParser("content", analyzer).parse(keyWord);
			
			String[] fields = {"Title", "content"};
			// MUST 表示and，MUST_NOT 表示not ，SHOULD表示or
			BooleanClause.Occur[] clauses = {BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD};
			// MultiFieldQueryParser表示多个域解析， 同时可以解析含空格的字符串，如果我们搜索"上海 中国" 
			Query multiFieldQuery = MultiFieldQueryParser.parse(keyWord, fields, clauses, analyzer);
			
			// 5、根据searcher搜索并且返回TopDocs
			TopDocs topDocs = indexSearcher.search(multiFieldQuery, 20); // 搜索前20条结果
			System.out.println("共找到匹配处：" + topDocs.totalHits);
			// 6、根据TopDocs获取ScoreDoc对象
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			System.out.println("共找到匹配文档数：" + scoreDocs.length);
			QueryScorer scorer = new QueryScorer(multiFieldQuery, "content");
		//	SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<span style=\"color:red\">", "</span>");
			SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
			Highlighter highlighter = new Highlighter(htmlFormatter, scorer);
			highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer));
			for (ScoreDoc scoreDoc : scoreDocs)
			{
				// 7、根据searcher和ScoreDoc对象获取具体的Document对象
				Document document = indexSearcher.doc(scoreDoc.doc);
				String content = document.get("content");
				Doc doc=new Doc();
				doc.setTitle(document.get("Title"));
				doc.setUrl(document.get("url"));
				//这边不能用content.subString()来截断，因为可能截断后没有高亮部分，会返回null
				doc.setContent(highlighter.getBestFragment(analyzer, "content", content));
				docList.add(doc);	
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(directoryReader != null) directoryReader.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return docList;
	}
	//现在这个页面运行创建索引
	public static void main(String args[])
	{
		DbSearchDemo demo = new DbSearchDemo();
		demo.creatIndex();
		//下面search仅仅作为测试使用，测试完记得删除
	//	List<Doc>docList=demo.search("新冠肺炎对人体健康的危害");
	//	for(int i=0;i<docList.size();i++)
	//	{
	//		System.out.println();
	//		System.out.println(docList.get(i).getContent());
	//	}
		//demo.search("android");
	}
}