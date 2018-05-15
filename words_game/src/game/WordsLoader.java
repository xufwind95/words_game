package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 单词加载器，将单词从文件中读取出来，并进行处理 
 */
public class WordsLoader {
	public static final String ENCODING = "utf-8";
	private String name;
	private String comment;
	private int index;
	private int totalScore;
	private List<Word> words = new ArrayList<Word>();
	
	public WordsLoader(File chapter, int index) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(chapter), ENCODING));
		name = chapter.getName();
		comment = this.name;
		this.index = index;
		totalScore = 0;
		String line;
		while((line = in.readLine()) != null){
			if(line.trim().equals("")){
				continue;
			}
			String[] strs = line.split(" ", 2); 
			Word word = new Word(strs[0].trim(), strs[1].trim());
			words.add(word);
			totalScore += word.getScore();
		}
	}
	// 对单词进行乱序处理
	public void shuffle(){
		Collections.shuffle(words);
	}
	
	public List<Word> getWords(){
		return words;
	}
	
	public int getTotalScore(){
		return totalScore;
	}
	
	public String getName(){
		return name;
	}
	
	public String getComment(){
		return comment;
	}
	
	public int getIndex(){
		return index;
	}
		
	public String toString(){
		return index + "." + name;
	}
}
