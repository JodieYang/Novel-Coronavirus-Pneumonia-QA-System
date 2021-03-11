package nlp.wordsplit.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class PreprocessText {
	public static void main(String[] args) throws IOException {
		//words.txt:原始语料文件   split_words.txt:经jieba分词后的文件
		preProcessText("D:/NLP/data/words.txt", "D:/NLP/data/split_words.txt", "UTF-8");
	}
	public static void preProcessText(String fileRead, String fileWrite, String encoding) throws IOException {
		File file = new File(fileRead);
		File file1 = new File(fileWrite);
		if (file.isFile() && file.exists()) {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file1),encoding);
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			String lineTxt = null;
			//一行一行读取，然后分词然后写入
			while ((lineTxt = bufferedReader.readLine()) != null) {
				bufferedWriter.write(CnnUtils.jiebaStr(lineTxt));
			}
			writer.flush();
			writer.close();
			read.close();
			System.out.println("文件分词成功");
		} else {
			System.out.println("找不到指定的文件");
			return;
		}
	}
}
