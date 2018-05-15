package game;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 图片加载 
 */
public class ImageLoader {
	// 指定默认文件前缀
	public static final String THEME = "1";
	public static final int BG = 1;
	public static final int BULLET = 2;
	public static final int PAUSE = 3;
	public static final int START = 4;
	public static final int GAMEOVER = 5;
	private BufferedImage bg;
	private BufferedImage bullet;
	private BufferedImage pause;
	private BufferedImage start;
	private BufferedImage gameover;
	private static BufferedImage no_match;
	
	private static ImageLoader imageLoader = new ImageLoader();
	private ImageLoader(){
		try {
			bg = ImageIO.read(getClass().getResource("../" + THEME + "bg.png"));
			bullet = ImageIO.read(getClass().getResource("../" + THEME + "bullet.png"));
			pause = ImageIO.read(getClass().getResource("../" + THEME +"bg.png"));
			start = ImageIO.read(getClass().getResource("../" + THEME +"bg.png"));
			gameover = ImageIO.read(getClass().getResource("../" + THEME +"bg.png"));
			no_match = ImageIO.read(getClass().getResource("../" + THEME + "typer.png"));
		} catch (IOException e) {
			System.out.println("获取图片信息失败!");
			System.exit(-1);
		}
	}
	public static BufferedImage getImage(int index){
		switch(index){
		case BG:
			return imageLoader.bg;
		case BULLET:
			return imageLoader.bullet;
		case PAUSE:
			return imageLoader.pause;
		case START:
			return imageLoader.start;
		case GAMEOVER:
			return imageLoader.gameover;
		}
		// 没有找到匹配图片的返回空白图片
		System.out.println("no such image!");
		return no_match;
	}
}
