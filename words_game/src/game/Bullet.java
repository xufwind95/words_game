package game;
/**
 * 子弹类 
 * 
 */
public class Bullet {
	// 子弹的起始坐标
	public int x, y;
	private Word word; // 目标单词
	
	public Bullet(Word word){
		x = word.x + word.getWidth() / 2 - WordsGame.FONT_SIZE / 2;
		y = WordsGame.HEIGHT;
		this.word = word;
	}
	// 子弹的移动方法
	public void step(){
		y--;
	}
	
	public Word getWord(){
		return word;
	}
	public String toString(){
		return word.getEnglish();
	}
}
