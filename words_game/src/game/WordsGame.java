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
 * 程序入口
 *
 */
public class WordsGame extends JPanel{
	// 游戏界面的宽度和高度
	public static final int WIDTH = 805;
	public static final int HEIGHT = 550;
	// 设置游戏的字体大小和颜色
	public static final int FONT_SIZE = 16;
	public static final int FONT_COLOR = 0xeeeeee;//黄色 
	// 设置游戏状态
	public static final int GAME_START = 1;
	public static final int GAME_RUNNING = 2;
	public static final int GAME_END = 3;
	public static final int GAME_PAUSE = 4;
	
	// 设置单词文件存放目录
	public static final String WORDS_DIR = "words";
	
	// 初始化游戏所需图片
	private static BufferedImage bg = ImageLoader.getImage(ImageLoader.BG);
	private static BufferedImage bullet = ImageLoader.getImage(ImageLoader.BULLET);
	private static BufferedImage pause = ImageLoader.getImage(ImageLoader.PAUSE);
	private static BufferedImage start = ImageLoader.getImage(ImageLoader.START);
	private static BufferedImage gameover = ImageLoader.getImage(ImageLoader.GAMEOVER);
	
	// 任务周期调度器
	private Timer timer;
	private int timerInterval = 1000/120;
	private WordsLoader[] wordsList;
	private WordsLoader currentWords;
	private int curScore = 0;
	private List<Word> curRestWords; // 当前单元剩余的单词
	private Set<Word> movingWords; // 正在移动的单词
	private Set<Word> scoredWords; // 得到分数的单词
	private Set<Word> missingWords;// 没有得到分数的单词
	private Set<Bullet> bullets; //当前的子弹
	private int index;
	private final int step = 400;
	private int game_status;
	// 用户输入单词记录
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
	
	// 初始化游戏参数
	private void initGameData(){
		currentWords = (WordsLoader) JOptionPane.showInputDialog(this, "单词章节",
				"选择", JOptionPane.PLAIN_MESSAGE, new ImageIcon(bullet),
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
	//重写画图方法
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);	
		//画游戏背景
		g.drawImage(bg, 0, 0, null);
		synchronized(this){
			//画单词
			draw_words(g);
			//画子弹
			draw_bullet(g);		
			//画分数
			draw_score(g);			
		}
		//画用户输入的单词
		draw_inputLine(g);
		// 画游戏状态(暂停的和结束的)
		draw_game_status(g);
	}
	// 画分数
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
	// 画单词
	private void draw_words(Graphics g){
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE);
		for(Word word: movingWords){
			g.setFont(font);
			// 绘制阴影
			g.setColor(Color.gray);
			g.drawString(word.getEnglish(), word.x + 1, word.y + 1);
			g.setColor(new Color(FONT_COLOR));
			// 画中文
			g.drawString(word.getChinese(), word.x, word.y - FONT_SIZE);
			// 判断单词是否已经被选择
			if(word.getMatched()){
				g.setColor(Color.red);
			}
			g.drawString(word.getEnglish(), word.x, word.y);
			// 判断单词是否有部分被匹配
			String cur_input = inputLine.toString();
			if((!cur_input.equals("")) && (!word.getMatched()) && word.getEnglish().startsWith(cur_input)){
				g.setColor(Color.yellow);
				g.drawString(cur_input, word.x, word.y);
			}
		}
	}
	// 画子弹
	private void draw_bullet(Graphics g){
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE);
		for(Bullet bt: bullets){
			g.setFont(font);
			g.setColor(Color.white);
			g.drawImage(bullet, bt.x, bt.y, null);
		}
	}
	// 画用户输入的单词
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
	// 画游戏状态
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
			g.drawString("[F1] 暂停", x, y);
			y+=68;
			g.drawString("[ESC] 结束游戏 ", x, y);
			y+=68;
			g.drawString("[C] 继续 ", x, y);
			break;
		case GAME_END:
			g.drawImage(gameover, 0, 0, null);
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
			x = 220;
			y = 170;
			g.setColor(Color.BLACK);
			g.drawString("分数: "+ curScore, x, y);
			y+=48;
			g.drawString("击中: "+ (scoredWords.size()), x, y);
			y+=48;
			g.drawString("错过: "+ (missingWords.size()), x, y);
			y+=48;
			g.drawString("最高: "+ (currentWords.getTotalScore()), x, y);
			y+=48;
			g.drawString("[S] 重新开始", x, y);
			
			break;			
		}
	}
	private void word_stepin(){
		// 当前没有单词时直接退出不再入场
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
	// 计算分数和单词移动
	private void cal_Score_mov(){
		int now_get_score = 0;
		Set<Word> cur_socred_words = new HashSet<Word>();
		Set<Word> cur_mis_words = new HashSet<Word>();
		Set<Bullet> cur_out_bullets = new HashSet<Bullet>();
		// 判断单词和对应子弹是否向互撞
		for(Bullet bullet: bullets){
			Word word = bullet.getWord();
			if(word.shooted(bullet)){
				cur_socred_words.add(word);
				cur_out_bullets.add(bullet);
				now_get_score += word.getScore();
			}
		}
		// 判断单词是否出界，出界的应该移动到哪个结果集
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
		// 移动单词中剔除相撞的和出界的单词，加入新的单词
		// 从得分单词中计算分数
		synchronized(this){
			movingWords.removeAll(cur_socred_words);// 从移动的单词中去掉出界的和被击中的
			movingWords.removeAll(cur_mis_words); // 从移动的单词中去掉出界的和被击中的
			bullets.removeAll(cur_out_bullets); // 中子弹中移除已经击中目标的或目标已经出界的
			scoredWords.addAll(cur_socred_words); // 将得分的集合中加入得分的
			missingWords.addAll(cur_mis_words); // 将未得分的加入到未得分的集合
			curScore += now_get_score; // 计算分数
		}
	}
	private void componentMove(){
		// 单词进入
		word_stepin();
		
		// 计算当前单词是否和输入匹配,匹配的修改状态
		String line = inputLine.toString();
		for(Word w: movingWords){
			if(!w.getMatched() && w.match(line)){
				inputLine = new StringBuilder(); // 输入清零
				synchronized(this){
					bullets.add(new Bullet(w));
				}
				break;
			}
		}			
		// 移动单词
		for(Word word: movingWords){
			word.step();
		}
		// 移动子弹
		for(Bullet bullet: bullets){
			bullet.step();
		}
		// 判断和单词是否相撞并获取撞击单词的分数
		// 判断越界并获取越界且已经匹配的单词的分数
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
	// 暂停按键处理，对相应的键设置状态
	private void keyPauseAction(int key, char ch){
		if(key == KeyEvent.VK_C){
			game_status = GAME_RUNNING;
		}
		if(key == KeyEvent.VK_ESCAPE){
			game_status = GAME_END;
			System.exit(0);
		}
	}
	// 处理游戏结束
	private void game_endAction(int key, char ch){
		//根据界面的提示,按ESC的直接退出,按S的继续游戏
		if(key == KeyEvent.VK_ESCAPE){
			System.exit(0);
		}
		// 按S的重新开始
		if(key == KeyEvent.VK_S){
			game_status = GAME_START;
			initGameData();
			return;
		}
	}
	// 开始游戏
	public void start(){
		game_status = GAME_START;
		// 画选择面板
		// 构建一个单元的单词
		initGameData();
		//定义周期任务，让各个组件运动起来
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
		//定义周期任务更新画面
		timer.schedule(new TimerTask(){
			public void run(){
				repaint();
			}
		}, 0, 1000/24);
		
		// 监听键盘事件，使输入生效
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
