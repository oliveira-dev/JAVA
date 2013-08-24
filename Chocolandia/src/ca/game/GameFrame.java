package ca.game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import ca.game.gfx.SpriteSheet;

public class GameFrame extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	
	//Definir o tamanho da janela
	public static final int WIDTH = 300;
	public static final int HEIGHT = WIDTH/12*9;
	public static final int SCALE = 3;
	public static final String NAME ="Choco Landia";
	
	private JFrame frame;
	
	public boolean running = false;
	public int tickCount = 0;
	
	//Criar uma imagem
	private BufferedImage image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	
	private SpriteSheet spriteSheet= new SpriteSheet("/sprite_sheet.jpg");
	
	public GameFrame(){
		
		//Defenir minimos, maximox e tamanho preferido para a janela
		setMinimumSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		setMaximumSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		//Criar nova janela
		frame = new JFrame(NAME);
		//Defenir modo de encerrar
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		//Centrar o nome da janela no centro da barra de informações
		frame.add(this,BorderLayout.CENTER);
		frame.pack();
		
		//Defenir se e posivel redimensionar, e se a localizaçãi é relativa a algo
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		running = true;
		new Thread(this).start();
		
	}
	public synchronized void stop() {
		running = false;
	}
	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D/64D;
		
		int frames = 0;
		int ticks = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		while (running){
			long now = System.nanoTime();
			delta += (now-lastTime)/nsPerTick;
			lastTime = now;
			boolean shouldRender = false;
			
			while (delta >= 1){
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}
			//Insire uma pausa nas frames para nao subcaregar o sistema
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (shouldRender){
				frames++;
				render();
			}
			if (System.currentTimeMillis()-lastTimer >= 1000){
				lastTimer += 1000;
				System.out.println("fps "+frames+", ticks "+ticks);
				frames = 0;
				ticks = 0;
			}
		}
	}
	public void tick(){
		tickCount++;
		for (int i = 0; i < pixels.length;i++){
			pixels[i]= i + tickCount;
		}
	}
	public void render(){
		//Criar uma estategia de buffering
		BufferStrategy bs = getBufferStrategy();
		if ( bs == null){
			//Em caso de nao ter buffer, criar um triple-buffer
			createBufferStrategy(3);
			return;
		}
		//Cria os graficos principais
		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(),getHeight(),null);
		g.dispose();
		bs.show();
	}
	public static void main (String[]args){
		new GameFrame().start();
	}
}