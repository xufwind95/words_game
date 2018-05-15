package game;
/**
 * �ӵ��� 
 * 
 */
public class Bullet {
	// �ӵ�����ʼ����
	public int x, y;
	private Word word; // Ŀ�굥��
	
	public Bullet(Word word){
		x = word.x + word.getWidth() / 2 - WordsGame.FONT_SIZE / 2;
		y = WordsGame.HEIGHT;
		this.word = word;
	}
	// �ӵ����ƶ�����
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
