package game;

import java.util.Random;

/**
 * 单词类，定义单个单词的属性和方法 
 *
 */
public class Word {
	// 起始 x,y坐标，单词英文宽度，单词中文宽度, 单词高度
	int x, y;
	private int width;
	private String english; // 单词英文
	private String chinese; // 单词中文翻译
	private int speed; // 单词下移速度
	private int index;
	private int score;
	private boolean matched;
	private int basestep;
	public Word(String english, String chinese){
		this.english = english;
		this.chinese = chinese;
		width = Math.max(WordsGame.FONT_SIZE / 2 * english.length(), WordsGame.FONT_SIZE * 2);
		Random rand = new Random();
		x = rand.nextInt(WordsGame.WIDTH - width);
		y = 0;
		index = 0;
		int eLength = english.length();
		basestep = eLength > 9 ? (eLength - 9) / 3 + 3 : 3;
		speed = rand.nextInt(9) + basestep;
		score = this.english.length() * 10;
		matched = false;
	}
	
	private Word(Word w){
		this.x = w.x;
		this.y = w.y;
		this.width = w.width;
		this.english = w.english;
		this.chinese = w.chinese;
		this.speed = w.speed;
		this.index = w.index;
		this.score = w.score;
		this.matched = w.matched;
		this.basestep = w.basestep;
	}
	
	public Word clone(){
		Word w = new Word(this);
		return w;
	}
	// 单词下移方法
	public void step(){
		index++;
		if(index % speed == 0){
			y++;
		}
	}
	// 判断单词是否触底
	public boolean outOfBounds(){
		return y > WordsGame.HEIGHT;
	}
	// 计算单个单词的得分
	public int getScore(){
		return score;
	}
	// 计算是否被击中
	public boolean shooted(Bullet bullet){
		return this.y >= bullet.y - 1;
	}
	
	public String getEnglish(){
		return english;
	}
	
	public String getChinese(){
		return chinese;
	}
	
	public boolean getMatched() {
		return matched;
	}
	
	public boolean match(String line) {
		if(!matched){
			matched = line.equals(getEnglish());
		}
		return matched;
	}
	
	public int getWidth(){
		return width;
	}
	
	public String toString(){
		return english;
	}
}
