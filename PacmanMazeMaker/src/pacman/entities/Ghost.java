package pacman.entities;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import pacman.maze.Maze;
import pacman.maze.Maze.Direction;

public class Ghost extends Entity {
	
	public final static double[][] ghostShape = new double[][] {
		{0.1, 0.3},{0.2, 0.1}, {0.8, 0.1}, {0.9, .3}, {0.9, 0.9}, {0.8, 0.7}, {0.5, 0.9}, {0.2, 0.7}, {0.1, 0.9}
	};
	
	public Color color;
	public Point target;
	public boolean free;
	public boolean out;
	double tilex, tiley;
	public boolean random;
	public boolean scared;
	public boolean relaxed;
	Random rand = new Random();
	Point origin;
	int jailTime;
	
	public Ghost(Maze maze, int x, int y, Color color) {
		
		super(maze, x, y);
		this.color = color;
		this.lead = 0.2;
		this.target = new Point(x, y);
		this.dir = Direction.up;
		tilex = -1;
		tiley = -1;
		speed = 0;
		origin = new Point(x, y);
		jailTime = 0;
		
	}
	
	public void update() {
				
		super.update();
		
		if(free) {
			
			if(jailTime > 0) {
				
				jailTime --;
				return;
				
			}
			
			if(scared)
				speed = 0.03;
			else
				speed = 0.04;
			
			if( (Math.abs(x - tilex) > 0.8) && (Math.abs(Math.abs(x - Math.floor(x)) - 0.5) > 0.35) ||
					(Math.abs(y - tiley) > 0.8) && (Math.abs(Math.abs(y - Math.floor(y)) - 0.5) > 0.35) ||
					(walled(dir))) {
				
				tilex = x;
				tiley = y;
				
				if(out) {
					
					if(random) {
						
						ArrayList<Direction> dirs = getAvailableDirections();
						if(dirs.size() > 0) this.dir = dirs.get(rand.nextInt(dirs.size()));
						
					} else if(scared || relaxed) fleeTarget();
					
					else chaseTarget();
					
				} else
					escape();
								
			}
			
		}
		
	}
	
	public void reset() {
		
		this.x = origin.x;
		this.y = origin.y;
		jailTime = 500;
		out = false;
		scared = false;
		speed = 0;
		
	}
	
	public void target(Point p) {
		
		this.target.x = p.x;
		this.target.y = p.y;
		
	}
	
	public void bump() {
		
		this.tilex = this.tiley = -1;
		
	}
	
	ArrayList<Direction> getAvailableDirections() {
		
		ArrayList<Direction> dirs = new ArrayList<>();
		
		for(Direction d : Direction.values()) {
			
			if(!walled(d))
				dirs.add(d);
			
		}
		
		if(dirs.contains(this.dir.opposite()))
			dirs.remove(this.dir.opposite());
		
		return dirs;
		
	}
	
	void escape() {
		
		if(y < maze.playerStart().y - 3) out = true;
		
		if(!walled(Direction.up))
			dir = Direction.up;
		else
			if(walled(Direction.right))
				dir = Direction.left;
			else
				dir = Direction.right;		
	}
	
	void prefer(Direction[] dirs) {
		
		ArrayList<Direction> openDirs = getAvailableDirections();
		
		for(Direction dir : dirs) {
			
			if(openDirs.contains(dir)) {
				
				this.dir = dir;
				return;
				
			}
			
		}
		
	}

	void chaseTarget() {
		
		final Direction l = Direction.left, r = Direction.right, u = Direction.up, d = Direction.down;
		
		if(Math.abs(x - target.x) > Math.abs(y - target.y))
			if(target.x < x)
				if(target.y < y) prefer(new Direction[]{l, u, d, r});
				else prefer(new Direction[]{l, d, u, r});
			else
				if(target.y < y) prefer(new Direction[]{r, u, d, l});
				else prefer(new Direction[]{r, d, u, l});
		else
			if(target.y < y)
				if(target.x < x) prefer(new Direction[]{u, l, r, d});
				else prefer(new Direction[]{u, r, l, d});
			else
				if(target.x < x) prefer(new Direction[]{d, l, r, u});
				else prefer(new Direction[]{d, r, l, u});
	}
	
	void fleeTarget() {
		
		final Direction r = Direction.right, l = Direction.left, d = Direction.down, u = Direction.up;
		
		if(Math.abs(x - target.x) > Math.abs(y - target.y))
			if(target.x < x)
				if(target.y < y) prefer(new Direction[]{r, d, u, l});
				else prefer(new Direction[]{r, u, d, l});
			else
				if(target.y < y) prefer(new Direction[]{l, d, u, r});
				else prefer(new Direction[]{l, u, d, r});
		else
			if(target.y < y)
				if(target.x < x) prefer(new Direction[]{d, r, l, u});
				else prefer(new Direction[]{d, l, r, u});
			else
				if(target.x < x) prefer(new Direction[]{u, r, l, d});
				else prefer(new Direction[]{u, l, r, d});
	}
	
}
