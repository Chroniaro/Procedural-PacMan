package pacman;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import pacman.entities.Ghost;
import pacman.entities.Player;
import pacman.maze.*;
import pacman.maze.Maze.Direction;

public class Game {
	
	Maze maze;
	boolean doubled;
	Player player;
	boolean[] keys;
	boolean started;
	Ghost[] ghosts;
	int updates;
	public String text;
	public int score;
	private int previousDotCount;
	int ghostEatCount = 0;
	CopyOnWriteArrayList<PacComponent.SplashModel> splashes;
	
	public Game() {
		
		updates = -200;
		maze = Maze.create(20, 30);
		player = new Player(maze, maze.playerStart().x, maze.playerStart().y);
		keys = new boolean[4];
		resetGhosts();
		text = "";
		score = 0;
		previousDotCount = maze.dotCount;
		splashes = new CopyOnWriteArrayList<>();
		
	}
	
	public void resetGhosts() {
		
		ghosts = new Ghost[]{ new Ghost(maze, maze.playerStart().x, maze.playerStart().y - 3, Color.cyan),
				new Ghost(maze, maze.playerStart().x, maze.playerStart().y - 2, Color.magenta),
				new Ghost(maze, maze.playerStart().x + 1, maze.playerStart().y - 2, Color.red),
				new Ghost(maze, maze.playerStart().x - 1, maze.playerStart().y - 2, Color.orange.darker())};
		
	}

	public void update() {
		
		if(started) {
			
			if(updates == -199)
				text = "";
			if(updates == -150)
				text = "Ready?";
			if(updates == -50)
				text = "";
			
			updates++;
			
			if(updates < 0)
				return;
			
			ghosts[0].target(new Point((int)player.x, (int)player.y));
			
			switch(player.dir) {
			
			case up:
				ghosts[1].target(new Point((int)player.x, (int)player.y - 4));
				break;
				
			case down:
				ghosts[1].target(new Point((int)player.x, (int)player.y + 4));
				break;
				
			case left:
				ghosts[1].target(new Point((int)player.x - 4, (int)player.y));
				break;
				
			case right:
				ghosts[1].target(new Point((int)player.x + 4, (int)player.y));
				break;
			
			}
			
			ghosts[2].target(new Point((int)(3 * player.x - 2 * ghosts[0].x), (int)(3 * player.x - 2 * ghosts[0].x)));
			ghosts[3].random = true;
			
			if(player.power == 0)
				ghostEatCount = 0;
			
			for(Ghost g : ghosts) {
			
				if(updates >= 3500 && updates % 3500 == 0) {
					
					g.dir = g.dir.opposite();
					g.relaxed = true;
					
				}
				
				if(updates >= 3500 && updates % 3500 == 500) {
						
					g.dir = g.dir.opposite();
					g.relaxed = false;
						
				}
				
	
				if(Math.abs(g.x - player.x) + Math.abs(g.y - player.y) < 0.5) {
					
					if(g.scared) {
						
						ghostEatCount++;
						score += 100 * Math.pow(2, ghostEatCount);
						splashes.add(new PacComponent.SplashModel("" + 100 * Math.pow(2, ghostEatCount), g.x, g.y, Color.white));
						g.reset();
						
					} else {
						
						if(!player.deathAnimation()) return;
						else {
							
							splashes.add(new PacComponent.SplashModel("-100", player.x, player.y, Color.red));
							score = Math.max(0, score - 100);
							
							resetGhosts();
							updates = -200;
							if(player.die()) 
								text = "You Lose!";
							
						}
						
					}
						
					
				}
				
				if(g.scared)
					g.scared = player.power > 0;
				else
					g.scared = player.power > Player.MAX_POWER - 5;
				
				if(g.scared) g.target(new Point((int)player.x, (int)player.y));
				
				g.update();
				
			}
			
			if(maze.dotCount != previousDotCount) {
			
				if(maze.dotCount == 0) {
					
					text = "You Won!";
					PacMan.win();
					
				}
				
				previousDotCount = maze.dotCount;
				score += 10;
			
			}
			
			if(keys[0]) player.turn(Direction.left);
			if(keys[1]) player.turn(Direction.right);
			if(keys[2]) player.turn(Direction.up);
			if(keys[3]) player.turn(Direction.down);
			player.update();
			
			if(updates == 100) ghosts[0].free = true;
			if(updates == 500) ghosts[1].free = true;
			if(updates == 1000) ghosts[2].free = true;
			if(updates == 1500) ghosts[3].free = true;
			
		} else {
			
			text = "Press Space to Start";
			
		}
		
	}
	
	public void processPress(KeyEvent e) {
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE && !started) started = true;
		
		if(e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) keys[0] = true;
		if(e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) keys[1] = true;
		if(e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) keys[2] = true;
		if(e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) keys[3] = true;
		
	}
	
	public void processRelease(KeyEvent e) {
		
		if(e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) keys[0] = false;
		if(e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) keys[1] = false;
		if(e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) keys[2] = false;
		if(e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) keys[3] = false;
		
	}
	
}
