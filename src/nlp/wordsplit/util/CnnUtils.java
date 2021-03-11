package nlp.wordsplit.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.huaban.analysis.jieba.JiebaSegmenter;

public class CnnUtils {
	
    public static List<Term> CutWordsStandards(String str){
        List<Term> standardsList = HanLP.segment(str);
        return standardsList;
    }
    
    public static List<Term> cutWordsNLP (String str) {
        List<Term> nlpList = NLPTokenizer.segment(str);
		return nlpList;
    }
   
    public static List<String> jiebaList(String str) {
    	 JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();
         List<String> strings = jiebaSegmenter.sentenceProcess(str);
         List<String> strList = new ArrayList<String>();
         String[] marks = {",","，","。",".","/","、","？","?",":","：","《","》",
        		 "<",">","[","]","【","】","{","}","\\","|","*",";","；","-","@","@","~",
           		 "......","+","%","&","#","$","￥","..."};
         //去除常用标点
         for (int i = 0; i < strings.size(); i++) {
             if (Arrays.asList(marks).contains(strings.get(i))) {
                 continue;
             }
             strList.add(strings.get(i));
         }
         return strList;
    }
   
    public static String jiebaStr(String str) {
   	 	JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();
        List<String> strings = jiebaSegmenter.sentenceProcess(str);
        String sb = "";
        String[] marks = {",","，","。",".","/","、","？","?",":","：","《","》",
       		 "<",">","[","]","【","】","{","}","\\","|","*",";","；","-","@","@","~",
       		 "......","+","%","&","#","$","￥","..."};
        //去除常用标点
        for (int i = 0; i < strings.size(); i++) {
            if (Arrays.asList(marks).contains(strings.get(i))) {
                continue;
            }
            sb = sb+strings.get(i)+" ";
        }
        return sb;
   }
    public static void main(String[] args) {
    	String a = "计算机在现代社会各个方面中起着必不可少的作用";
    	System.out.println("标准分词List<Term> standardsList = HanLP.segment(str);"+"\n"+CutWordsStandards(a));
    	System.out.println("NLP分词List<Term> nlpList = NLPTokenizer.segment(str);"+"\n"+cutWordsNLP(a));
    	System.out.println("jieba分词List<String> strings = jiebaSegmenter.sentenceProcess(str);"+"\n"+jiebaList(a));
	}
}
