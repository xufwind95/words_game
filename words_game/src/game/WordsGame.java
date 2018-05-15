package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * �������
 *
 */
public class WordsGame extends JPanel{
	// ��Ϸ����Ŀ�Ⱥ͸߶�
	public static final int WIDTH = 805;
	public static final int HEIGHT = 550;
	// ������Ϸ�������С����ɫ
	public static final int FONT_SIZE = 16;
	public static final int FONT_COLOR = 0xeeeeee;//��ɫ 
	// ������Ϸ״̬
	public static final int GAME_START = 1;
	public static final int GAME_RUNNING = 2;
	public static final int GAME_END = 3;
	public static final int GAME_PAUSE = 4;
	
	// ���õ����ļ����Ŀ¼
	public static final String WORDS_DIR = "words";
	
	// ��ʼ����Ϸ����ͼƬ
	private static BufferedImage bg = ImageLoader.getImage(ImageLoader.BG);
	private static BufferedImage bullet = ImageLoader.getImage(ImageLoader.BULLET);
	private static BufferedImage pause = ImageLoader.getImage(ImageLoader.PAUSE);
	private static BufferedImage start = ImageLoader.getImage(ImageLoader.START);
	private static BufferedImage gameover = ImageLoader.getImage(ImageLoader.GAMEOVER);
	
	// �������ڵ�����
	private Timer timer;
	private int timerInterval = 1000/120;
	private WordsLoader[] wordsList;
	private WordsLoader currentWords;
	private int curScore = 0;
	private List<Word> curRestWords; // ��ǰ��Ԫʣ��ĵ���
	private Set<Word> movingWords; // �����ƶ��ĵ���
	private Set<Word> scoredWords; // �õ������ĵ���
	private Set<Word> missingWords;// û�еõ������ĵ���
	private Set<Bullet> bullets; //��ǰ���ӵ�
	private int index;
	private final int step = 400;
	private int game_status;
	// �û����뵥�ʼ�¼
	private StringBuilder inputLine = new StringBuilder();
	
	public WordsGame(){
		File dir = new File(WORDS_DIR);
		File[] files = dir.listFiles();
		int length = files.length;
		if(length == 0){
			System.exit(-1);
		}
		Arrays.sort(files);
		wordsList = new WordsLoader[length];
		for(int i = 0; i < length; i++){
			try {
				wordsList[i] = new WordsLoader(files[i], i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		currentWords = wordsList[0];
		movingWords = new HashSet<Word>();
		scoredWords = new HashSet<Word>();
		missingWords = new HashSet<Word>();
		bullets = new HashSet<Bullet>();
	}
	
	// ��ʼ����Ϸ����
	private void initGameData(){
		currentWords = (WordsLoader) JOptionPane.showInputDialog(this, "�����½�",
				"ѡ��", JOptionPane.PLAIN_MESSAGE, new ImageIcon(bullet),
				wordsList, currentWords);
		if (currentWords == null) {
			System.exit(0);
		}
		
		curScore = 0;
		inputLine = new StringBuilder();
		currentWords.shuffle();
		curRestWords = new LinkedList<Word>();
		for(Word w: currentWords.getWords()){
			curRestWords.add(w.clone());
		}
		movingWords.clear();
		scoredWords.clear();
		missingWords.clear();
		bullets.clear();	
		game_status = GAME_RUNNING;
		index = 0;
	}
	//��д��ͼ����
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);	
		//����Ϸ����
		g.drawImage(bg, 0, 0, null);
		synchronized(this){
			//������
			draw_words(g);
			//���ӵ�
			draw_bullet(g);		
			//������
			draw_score(g);			
		}
		//���û�����ĵ���
		draw_inputLine(g);
		// ����Ϸ״̬(��ͣ�ĺͽ�����)
		draw_game_status(g);
	}
	// ������
	private void draw_score(Graphics g){
		int x = 18;
		int y = 36;
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, 20);
		g.setFont(font);
		g.setColor(Color.gray);
		g.drawString("SCORE:"+ curScore, x+1, y+1);
		g.setColor(new Color(0xeeeeee));
		g.drawString("SCORE:"+ curScore, x, y);
	}
	// ������
	private void draw_words(Graphics g){
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE);
		for(Word word: movingWords){
			g.setFont(font);
			// ������Ӱ
			g.setColor(Color.gray);
			g.drawString(word.getEnglish(), word.x + 1, word.y + 1);
			g.setColor(new Color(FONT_COLOR));
			// ������
			g.drawString(word.getChinese(), word.x, word.y - FONT_SIZE);
			// �жϵ����Ƿ��Ѿ���ѡ��
			if(word.getMatched()){
				g.setColor(Color.red);
			}
			g.drawString(word.getEnglish(), word.x, word.y);
			// �жϵ����Ƿ��в��ֱ�ƥ��
			String cur_input = inputLine.toString();
			if((!cur_input.equals("")) && (!word.getMatched()) && word.getEnglish().startsWith(cur_input)){
				g.setColor(Color.yellow);
				g.drawString(cur_input, word.x, word.y);
			}
		}
	}
	// ���ӵ�
	private void draw_bullet(Graphics g){
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE);
		for(Bullet bt: bullets){
			g.setFont(font);
			g.setColor(Color.white);
			g.drawImage(bullet, bt.x, bt.y, null);
		}
	}
	// ���û�����ĵ���
	private void draw_inputLine(Graphics g){
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, 20);	
		int x = 270;
		int y = 475;
		String s = inputLine.toString();
		font = new Font(Font.SERIF, Font.BOLD, 35);
		g.setFont(font);
		g.setColor(Color.black);
		g.drawString(s, x, y);
	}
	// ����Ϸ״̬
	private void draw_game_status(Graphics g){
		int x = 220, y = 170;
		
		switch(game_status){
		case GAME_START:
			g.drawImage(start, 0, 0, null);
		case GAME_PAUSE:
			g.drawImage(pause, 0, 0, null);
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 35));
			x = 220;
			y = 170;
			g.setColor(Color.BLACK);
			g.drawString("[F1] ��ͣ", x, y);
			y+=68;
			g.drawString("[ESC] ������Ϸ ", x, y);
			y+=68;
			g.drawString("[C] ���� ", x, y);
			break;
		case GAME_END:
			g.drawImage(gameover, 0, 0, null);
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
			x = 220;
			y = 170;
			g.setColor(Color.BLACK);
			g.drawString("����: "+ curScore, x, y);
			y+=48;
			g.drawString("����: "+ (scoredWords.size()), x, y);
			y+=48;
			g.drawString("���: "+ (missingWords.size()), x, y);
			y+=48;
			g.drawString("���: "+ (currentWords.getTotalScore()), x, y);
			y+=48;
			g.drawString("[S] ���¿�ʼ", x, y);
			
			break;			
		}
	}
	private void word_stepin(){
		// ��ǰû�е���ʱֱ���˳������볡
		if(curRestWords.isEmpty()){
			return;
		}
		if(index % step == 0){
			Word w = curRestWords.remove(0);
			synchronized(this){
				movingWords.add(w);
			}
		}
		index++;
	}
	// ��������͵����ƶ�
	private void cal_Score_mov(){
		int now_get_score = 0;
		Set<Word> cur_socred_words = new HashSet<Word>();
		Set<Word> cur_mis_words = new HashSet<Word>();
		Set<Bullet> cur_out_bullets = new HashSet<Bullet>();
		// �жϵ��ʺͶ�Ӧ�ӵ��Ƿ���ײ
		for(Bullet bullet: bullets){
			Word word = bullet.getWord();
			if(word.shooted(bullet)){
				cur_socred_words.add(word);
				cur_out_bullets.add(bullet);
				now_get_score += word.getScore();
			}
		}
		// �жϵ����Ƿ���磬�����Ӧ���ƶ����ĸ������
		for(Word w: movingWords){
			if(w.outOfBounds() && !cur_socred_words.contains(w)){
				if(w.getMatched()){
					cur_socred_words.add(w);
					for(Bullet b: bullets){
						if(b.getWord() == w){
							cur_out_bullets.add(b);
						}
					}
					now_get_score += w.getScore();
				}else{
					cur_mis_words.add(w);
				}
			}
		}
		// �ƶ��������޳���ײ�ĺͳ���ĵ��ʣ������µĵ���
		// �ӵ÷ֵ����м������
		synchronized(this){
			movingWords.removeAll(cur_socred_words);// ���ƶ��ĵ�����ȥ������ĺͱ����е�
			movingWords.removeAll(cur_mis_words); // ���ƶ��ĵ�����ȥ������ĺͱ����е�
			bullets.removeAll(cur_out_bullets); // ���ӵ����Ƴ��Ѿ�����Ŀ��Ļ�Ŀ���Ѿ������
			scoredWords.addAll(cur_socred_words); // ���÷ֵļ����м���÷ֵ�
			missingWords.addAll(cur_mis_words); // ��δ�÷ֵļ��뵽δ�÷ֵļ���
			curScore += now_get_score; // �������
		}
	}
	private void componentMove(){
		// ���ʽ���
		word_stepin();
		
		// ���㵱ǰ�����Ƿ������ƥ��,ƥ����޸�״̬
		String line = inputLine.toString();
		for(Word w: movingWords){
			if(!w.getMatched() && w.match(line)){
				inputLine = new StringBuilder(); // ��������
				synchronized(this){
					bullets.add(new Bullet(w));
				}
				break;
			}
		}			
		// �ƶ�����
		for(Word word: movingWords){
			word.step();
		}
		// �ƶ��ӵ�
		for(Bullet bullet: bullets){
			bullet.step();
		}
		// �жϺ͵����Ƿ���ײ����ȡײ�����ʵķ���
		// �ж�Խ�粢��ȡԽ�����Ѿ�ƥ��ĵ��ʵķ���
		cal_Score_mov();
		
	}
	private void keyAction(int key, char ch){
		if(key == KeyEvent.VK_F1){
			game_status = GAME_PAUSE;
			return;
		}
		
		if(key==KeyEvent.VK_ESCAPE){
			System.exit(0);
		}
		
		if(key == KeyEvent.VK_BACK_SPACE && inputLine.length()>0){
			inputLine.deleteCharAt(inputLine.length()-1);
			return;
		}
		if (Character.isLetterOrDigit(ch) || ch=='-' || ch=='_' || ch=='$' || ch=='.'){
			if(inputLine.length() <= 20){
				inputLine.append(ch);
			}
		}		
	}
	// ��ͣ������������Ӧ�ļ�����״̬
	private void keyPauseAction(int key, char ch){
		if(key == KeyEvent.VK_C){
			game_status = GAME_RUNNING;
		}
		if(key == KeyEvent.VK_ESCAPE){
			game_status = GAME_END;
			System.exit(0);
		}
	}
	// ������Ϸ����
	private void game_endAction(int key, char ch){
		//���ݽ������ʾ,��ESC��ֱ���˳�,��S�ļ�����Ϸ
		if(key == KeyEvent.VK_ESCAPE){
			System.exit(0);
		}
		// ��S�����¿�ʼ
		if(key == KeyEvent.VK_S){
			game_status = GAME_START;
			initGameData();
			return;
		}
	}
	// ��ʼ��Ϸ
	public void start(){
		game_status = GAME_START;
		// ��ѡ�����
		// ����һ����Ԫ�ĵ���
		initGameData();
		//�������������ø�������˶�����
		timer = new Timer();
		timer.schedule(new TimerTask(){
			public void run(){
				if(game_status == GAME_RUNNING){
					componentMove(); 
					if(curRestWords.isEmpty() && movingWords.isEmpty()){
						game_status = GAME_END;
					}
				}
			}
		}, 0, timerInterval);
		//��������������»���
		timer.schedule(new TimerTask(){
			public void run(){
				repaint();
			}
		}, 0, 1000/24);
		
		// ���������¼���ʹ������Ч
		this.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				int key = e.getKeyCode();
				char ch = e.getKeyChar();
				switch(game_status){
				case GAME_RUNNING:
					keyAction(key, ch);
					break;
				case GAME_PAUSE:
					keyPauseAction(key, ch);
					break;
				case GAME_END:
					game_endAction(key, ch);
					break;
				}
			}
		});
		this.setFocusable(true);
		this.requestFocus();
	}
	public static void main(String[] args){
		JFrame frame = new JFrame("[F1]pause");
		frame.setSize(WIDTH, HEIGHT);
		WordsGame game = new WordsGame();
		frame.add(game);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		game.start();
	}
}
