package game;

import java.util.Random;

/**
 * �����࣬���嵥�����ʵ����Ժͷ��� 
 *
 */
public class Word {
	// ��ʼ x,y���꣬����Ӣ�Ŀ�ȣ��������Ŀ��, ���ʸ߶�
	int x, y;
	private int width;
	private String english; // ����Ӣ��
	private String chinese; // �������ķ���
	private int speed; // ���������ٶ�
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
	// �������Ʒ���
	public void step(){
		index++;
		if(index % speed == 0){
			y++;
		}
	}
	// �жϵ����Ƿ񴥵�
	public boolean outOfBounds(){
		return y > WordsGame.HEIGHT;
	}
	// ���㵥�����ʵĵ÷�
	public int getScore(){
		return score;
	}
	// �����Ƿ񱻻���
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
