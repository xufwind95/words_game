package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import game.Word;
import game.WordsLoader;

public class Test {

	public static void main(String[] args) {
//		StringBuilder sb = new StringBuilder();
//		String str = sb.toString();
//		System.out.println(str.equals(""));
//		System.out.println("hello".startsWith(""));
		
		try {
			// words/chapter1-1-5.txt
			WordsLoader currentWords = new WordsLoader(new File("words/2单元 1-5.txt"), 0);
			List<Word> words = new ArrayList<Word>(currentWords.getWords());
			for(Word w: words){
				System.out.println(w.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
//		String test = "pursue v. 追捕, 追求, 继续从事";
//		String[] strs = test.split(" ", 2);
//		System.out.println(strs.length);
//		for(String s: strs){
//			System.out.println(s);
//		}
	}

}
