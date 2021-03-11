package nlp.Bayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import nlp.vec.*;
import nlp.wordsplit.util.*;
public class Bayes {
	/*//获取关键词
	getKeyWord();
	//从文件中读取出分类的数据
	readData();
	//统计关键词出现在分类文本中的次数
	getCountKeyWordClass();
	//计算关键词出现在分类文本中的概率
	getChanceKeyWord();
	//统计关键词出现在总文本中的次数
	getCountKeyWordAllText();
	//统计关键词同时出现在所有文本中的概率
	getChanceKeyWordSameTime();
	//使用朴素贝叶斯算法求出分类的概率
	classByBayes();
	*/
	private static Word2VEC vec;
	private static int size;
	private static Map<String,ClassEntry> classifications;
	private static int classes_num;
	private static int allwords=0;
	private static float PI=(float)3.1415926;
	private static void BayesClassify(String classes_path,String root_path)
	{
		//读取一共有多少类
		//classses_path文件格式：第一行 种类数
		//剩下行:每类的名字，文件名
		vec=new Word2VEC();
		try
		{
			vec.loadJavaModel("D:/NLP/data/wordsVector.model");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//词向量的维度
		size=vec.getSize();
		//所有类别
		classifications=new HashMap<>();
		
		File file = new File(classes_path);
		if (file.isFile() && file.exists()) 
		{
			try {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(read);
				//读取种类数
				classes_num=Integer.parseInt(bufferedReader.readLine());
				String label=null;
				//读取类名（文件名）
				while((label=bufferedReader.readLine())!=null)
				{
					//System.out.println(label);
					String classFilePath=root_path+label+".txt";
					File classFile=new File(classFilePath);
					if(file.isFile()&&file.exists())
					{
						//try
						InputStreamReader read1 = new InputStreamReader(new FileInputStream(classFile), "UTF-8");
						BufferedReader bufferedReader1 = new BufferedReader(read1);
						//读取每类文件中的每个问题，一行一个问题
						String classQuestion;
						while((classQuestion=bufferedReader1.readLine())!=null)
						{
							List<String> strList=new ArrayList<>();
							strList=CnnUtils.jiebaList(classQuestion);
							int tempWordsNum=strList.size();
							for(int i=0;i<tempWordsNum;i++)
							{
								float[] wordvec=vec.getWordVector(strList.get(i));
								if(wordvec!=null)
								{
									allwords++;
									if(classifications.containsKey(label))
									{
										List<float[]>classification=classifications.get(label).getWordsvec();
										classification.add(wordvec);
									}
									else
									{
										List<float[]>wordsvec=new ArrayList<>();
										wordsvec.add(wordvec);
										ClassEntry newClass=new ClassEntry();
										newClass.setWordsvec(wordsvec);
										classifications.put(label, newClass);
									}
								}
							}
						}
						read1.close();
					}
					//System.out.println(allwords);
					List<float[]>wordsvec=classifications.get(label).getWordsvec();
					float[] average=calAve(wordsvec);
					classifications.get(label).setAverage(average);
					float[] cov=calCov(wordsvec,average);
					classifications.get(label).setCovariance(cov);
				}
				read.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
		
		private static float[] calAve(List<float[]> wordsvec)
		{
			float[] ave=new float[size];
			int wordNum=wordsvec.size();
			float[][] wordsvecF=new float[wordNum][size];
			for(int i=0;i<wordNum;i++)
			{
				wordsvecF[i]=wordsvec.get(i);
			}
			for(int j=0;j<size;j++)
			{
				for(int i=0;i<wordNum;i++)
				{
					ave[j]+=wordsvecF[i][j];
				}
				ave[j]/=wordNum;
			}
			return ave;
		}
		
		private static float[] calCov(List<float[]> wordsvec,float[] ave)

		{
			float[] cov=new float[size];
			int wordNum=wordsvec.size();
			float[][] wordsvecF=new float[wordNum][size];
			for(int i=0;i<wordNum;i++)
			{
				wordsvecF[i]=wordsvec.get(i);
			}
			for(int j=0;j<size;j++)
			{
				for(int i=0;i<wordNum;i++)
				{
					cov[j]+=(wordsvecF[i][j]-ave[j])*(wordsvecF[i][j]-ave[j]);
				}
				cov[j]/=wordNum;
				//方差
				Math.sqrt(cov[j]);
			}
			return cov;
		}
		//class_path:类别文件
		//root_path:各类文件所在的目录
		//question:问题
		public static String queryClass(String classes_path,String root_path,String question)
		{
			//构建分类器
			BayesClassify(classes_path,root_path);
			List<String> qList=new ArrayList<>();
			qList=CnnUtils.jiebaList(question);
			float[] qVec=new float[size];
			int trueWordsNum=0;
			for(int i=0;i<qList.size();i++)
			{
				float[] tempQVec=vec.getWordVector(qList.get(i));
				if(tempQVec!=null)
				{
					for(int j=0;j<size;j++)
					{
						qVec[j]+=tempQVec[j];
					}
					trueWordsNum++;
				}
			}
	//		System.out.println(trueWordsNum);
			for(int i=0;i<size;i++)
			{
				qVec[i]/=trueWordsNum;
			//	System.out.println(qVec[i]);
			}
			double max=-Double.MAX_VALUE;
			String maxS=null;
			for(Map.Entry<String, ClassEntry> entry:classifications.entrySet())
			{
				int classAllwords=entry.getValue().getWordsvec().size();
				float[] ave=entry.getValue().getAverage();
				float[] cov=entry.getValue().getCovariance();
				double p=classAllwords/(float)allwords;
				for(int i=0;i<size;i++)
				{
					p=p*1/(Math.sqrt(2*PI)*cov[i])*Math.exp(-Math.pow((qVec[i]-ave[i]),2)/(2*Math.pow(cov[i], 2)));
				}
				if(max<p)
				{
					max=p;
					maxS=entry.getKey();
				}
			}
			return maxS;
		}
}