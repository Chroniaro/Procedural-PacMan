package pacman;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

public abstract class PacMan {
	
	static boolean running = true;
	static Game game;

	public static void main(String[] args) throws InterruptedException {
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Pacman");
		frame.setVisible(true);
		
		game = new Game();
		frame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				game.processRelease(e);
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
				game.processPress(e);
				
			}
		});
		
		PacComponent view = new PacComponent(game);
		frame.add(view);
		frame.pack();
		
		long time = 0;
		
		while(running) {
			
			game.update();
			
			do {
				
				view.repaint();
				
			} while(System.currentTimeMillis() - time < 10);
			
			time = System.currentTimeMillis();
			
		}
		
	}
	
	public static void win() {
		
		System.out.println("You Win!");
		running = false;
		
	}
	
	public static void lose() {
		
		System.out.println("You Lose!");
		running = false;
		
	}
	
}
