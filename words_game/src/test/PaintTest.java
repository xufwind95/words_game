package test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

class Words{
	private String words;
	int x, y, width, height;
	private int speed;
	private int index = 0;
	public Words(String words){
		this.words = words;
		width = Math.max(PaintTest.FONT_SIZE / 2 * words.length(), PaintTest.FONT_SIZE * 2);
		height = PaintTest.FONT_SIZE * 2;
		Random rand = new Random();
		x = rand.nextInt(PaintTest.WIDTH - width);
		y = 0;
		speed = rand.nextInt(9) + 3;
	}
	public void step(){
		index++;
		if(index % speed == 0){
			y++;
		}
	}
	public String getWords(){
		return this.words;
	}
}
class Bullet{
	int x, y = PaintTest.HEIGHT;
	public Bullet(int x){
		this.x = x;
	}
	public void step(){
		y--;
	}
}
public class PaintTest extends JPanel{
	public static final int WIDTH = 805;
	public static final int HEIGHT = 550;	
	private static BufferedImage bg ; // 背景设置
	private static BufferedImage bullet ;
	private Timer timer;
	private int timerInterval = 1000/120;
	public static final int FONT_SIZE = 16;
	public static final int FONT_COLOR = 0xeeeeee;//黄色 
	private Words testword;
	private Bullet bt;
	private StringBuffer line;
	public PaintTest(String word){
		try {
			testword = new Words(word);
			bt = new Bullet(testword.x);
			bg = ImageIO.read(getClass().getResource("../1bg.png"));
			bullet = ImageIO.read(getClass().getResource("../1bullet.png"));
			line = new StringBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(bg, 0, 0, null);
		paintWord(g);
		paintBullet(g);
		drawLine(g);
	}
	public void paintWord(Graphics g){
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE);
		g.setFont(font);
		g.setColor(new Color(FONT_COLOR));
		g.drawString(testword.getWords(), testword.x, testword.y);
	}
	public void paintBullet(Graphics g){
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE);
		g.setFont(font);
		g.setColor(Color.white);
		g.drawImage(bullet, bt.x, bt.y, null);
	}
	public void drawLine(Graphics g){	
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, 20);
		int x = 270;
		int y = 475;
		String s = this.line.toString();
		font = new Font(Font.SERIF, Font.BOLD, 35);
		g.setFont(font);
		g.setColor(Color.black);
		g.drawString(s, x, y);	
	}
	public void action(){
		timer = new Timer();
		timer.schedule(new TimerTask(){
			public void run(){
				testword.step();
				bt.step();
			}
		}, 0, timerInterval);
		timer.schedule(new TimerTask(){
			public void run(){
				repaint();
			}
		}, 0, 1000/24);
		this.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				int key = e.getKeyCode();
				char ch = e.getKeyChar();
				keyAction(key, ch);
			}
		});
		this.setFocusable(true);
		this.requestFocus();
	}
	private void keyAction(int key, char ch){
		if(key==KeyEvent.VK_ESCAPE){
			System.exit(0);
		}
		
		if(key == KeyEvent.VK_BACK_SPACE && line.length()>0){
			line.deleteCharAt(line.length()-1);
			return;
		}
		if (Character.isLetterOrDigit(ch) || ch=='-' || ch=='_' || ch=='$' || ch=='.'){
			if(line.length() <= 20){
				line.append(ch);
			}
		}
	}
	public static void main(String[] args){
		JFrame frame = new JFrame("快打  [F1]pause");
		frame.setSize(WIDTH, HEIGHT);
		PaintTest pt = new PaintTest("test");
		frame.add(pt);
		frame.setLocationRelativeTo(null);
//		frame.setIconImage(bg); // 设置游戏logo
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		pt.action();
	}
}
