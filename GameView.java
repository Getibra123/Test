package breakout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import breakout.radioactivity.Alpha;
import breakout.radioactivity.Ball;
import breakout.utils.Circle;
import breakout.utils.Point;
import breakout.utils.Rect;
import breakout.utils.Vector;

@SuppressWarnings("serial")
public class GameView extends JPanel {
	

	
	long prevTimestamp = 0;
	
	public BreakoutState breakoutState;
	private Timer ballTimer;
	private boolean leftKeyDown = false;
	private boolean rightKeyDown = false;
	
	private void gameChanged() {
		repaint(10);
	}
	
	private void startMovingBalls() {
		ballTimer = new Timer(Constants.BALL_DELAYMS, actionEvent -> {
			long timestamp = System.currentTimeMillis();
			moveBalls(timestamp);
		});
		ballTimer.start();
	}
	
	/**
	 * Create a new GameView for playing breakout starting from a given breakoutState.
	 * @param breakoutState initial state for the game.
	 */
	public GameView(BreakoutState breakoutState) {
		this.breakoutState = breakoutState;

		setBackground(Color.black);
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_RIGHT -> { rightKeyDown = true; break; }
				case KeyEvent.VK_LEFT -> { leftKeyDown = true; break; }		
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_RIGHT -> { rightKeyDown = false; break; }
				case KeyEvent.VK_LEFT -> { leftKeyDown = false; break; }
				}
			}
		});
		startMovingBalls();
	}
	
	private void moveBalls(long timestamp) {
		if (prevTimestamp != 0) {
			int elapsedTime = (int) (timestamp - prevTimestamp);
			// very high elapsed times (for example during debugging) are annoying.
			elapsedTime = Math.min(elapsedTime, Constants.MAX_ELAPSED_TIME);

			int curPaddleDir = 0;
			if (leftKeyDown && !rightKeyDown) {
				breakoutState.movePaddleLeft(elapsedTime);
				curPaddleDir = -1;
			}
			if (!leftKeyDown && rightKeyDown) {
				breakoutState.movePaddleRight(elapsedTime);
				curPaddleDir = 1;
			}
			breakoutState.tick(curPaddleDir, elapsedTime);
			if (breakoutState.isDead()) {
				JOptionPane.showMessageDialog(this, "Game over :-(");
				System.exit(0);
			}
			if (breakoutState.isWon()) {
				JOptionPane.showMessageDialog(this, "Gewonnen!");
				System.exit(0);
			}
			gameChanged();
		}
		prevTimestamp = timestamp;
	}

	@Override
	public Dimension getPreferredSize() {
		Point size = toGUICoord(breakoutState.getBottomRight().plus(new Vector(200,200)));
		return new Dimension(size.getX(), size.getY());
	}
	
	@Override
	public boolean isFocusable() {
		return true;
	}
	
	// Convert point in the game coordinate system to the GUI coordinate system.
	private Point toGUICoord(Point loc) {
		return new Point(loc.getX()/50, loc.getY()/50).plus(new Vector(5,5));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Point botRight = toGUICoord(breakoutState.getBottomRight());
		g.setColor(Color.black);
		g.drawRect(0,0,botRight.getX(),botRight.getY());
		
		paintPaddle(g);
		paintBalls(g);
		paintAlphas(g);
		paintBlocks(g);
		paintLinks(g);
		paintReplSources(g);
		
		//TODO
		
		// domi: this fixes a visual latency bug on my system...
		Toolkit.getDefaultToolkit().sync();
	}

	private void paintPaddle(Graphics g) {
		PaddleState paddle = breakoutState.getPaddle();
		Color[] cols = paddle.getActualColors();
		if (cols.length == 1) {
			g.setColor( cols[0] );
			Rect loc = paddle.getLocation();
			Point tl = loc.getTopLeft();
			Point br = loc.getBottomRight();
			paintPaddle(g, tl, br);	
		}
		else {
			Point tl = paddle.getLocation().getTopLeft();
			Point br = paddle.getLocation().getBottomRight();
			Rect rect0 = new Rect(
					tl,
					new Point(tl.getX() + (Constants.PADDLE_WIDTH / 3), br.getY()));
			Rect rect1 = new Rect(
					new Point( tl.getX() + (Constants.PADDLE_WIDTH / 3), tl.getY() ),
					new Point( tl.getX() + 2 * (Constants.PADDLE_WIDTH / 3) , br.getY() ));
			Rect rect2 = new Rect(
					new Point( tl.getX() + 2 * (Constants.PADDLE_WIDTH / 3) , tl.getY() ),
					br);
			Rect[] rects = new Rect[] {rect0, rect1, rect2};
			for (int i = 0 ; i < 3 ; i++) {
				paintGameRect(g , cols[i] , rects[i]);
			}
		}

	}

	private void paintPaddle(Graphics g, Point tlg, Point brg) {
		Point tl = toGUICoord(tlg);
		Point br = toGUICoord(brg);
		g.fillRect(tl.getX(),tl.getY(),br.getX()-tl.getX(),br.getY()-tl.getY());
	}
	
	private void paintGameRect( Graphics g, Color c, Rect r) {
		g.setColor(c);
		Point guitl = toGUICoord(r .getTopLeft());
		Point guibr = toGUICoord(r .getBottomRight());
		g.fillRect(guitl.getX(),guitl.getY(),guibr.getX()-guitl.getX(),guibr.getY()-guitl.getY());
	}

	private void paintBalls(Graphics g) {
		for (Ball ball : breakoutState.getBalls()) {
			g.setColor(ball.getColor());
			Circle c = ball.getLocation();
			Point tl = c.getTopLeftPoint();
			Point br = c.getBottomRightPoint(); 
			paintBall(g, tl, br);
		}
	}

	private void paintBall(Graphics g, Point tlg, Point brg) {
		Point tl = toGUICoord(tlg);
		Point br = toGUICoord(brg);
		g.fillOval(tl.getX(),tl.getY(),br.getX()-tl.getX(), br.getY()-tl.getY());
	}
	
	private void paintAlphas(Graphics g) {
		for (Alpha alpha : breakoutState.getAlphas()) {
			Point center = alpha.getCenter();
			int diam = alpha.getLocation().getDiameter();
			int radius = diam/2;
			Point tl = center.plus(new Vector(-radius,-radius / 2)); //alphas are squished ovals for now
			Color color = alpha.getColor();
			paintAlpha(g, color, tl, diam, radius);
			
		}
	}
	
	private void paintAlpha(Graphics g, Color color, Point tlg , int width, int height) {
		g.setColor(color);
		Point tl = toGUICoord(tlg);
		g.fillOval(tl.getX(), tl.getY(), width/50 , height/50);
	}
	
	private void paintLinks(Graphics g) {
		g.setColor(Color.red);  

	    for (Ball ball : breakoutState.getBalls()) {
	        for (Alpha alpha : ball.getAlphas()) {
	           
	            Point ballCenter = toGUICoord(ball.getLocation().getCenter());
	            Point alphaCenter = toGUICoord(alpha.getCenter());

	            g.drawLine(ballCenter.getX(), ballCenter.getY(), alphaCenter.getX(), alphaCenter.getY());
	        }
	    }
	
		
		
	}
	
	private void paintBlock(Graphics g, Point tlg, Point brg) {
		Point tl = toGUICoord(tlg);
		Point br = toGUICoord(brg);
		g.fillRect(tl.getX(),tl.getY(),br.getX()-tl.getX(),br.getY()-tl.getY());
	}

	private void paintBlocks(Graphics g) {
		for (BlockState block : breakoutState.getBlocks()) {
			g.setColor(block.getColor());			
			Rect loc = block.getLocation();
			Point tl = loc.getTopLeft();
			Point br = loc.getBottomRight();
			paintBlock(g, tl, br);
		}
	}
	
	private void paintReplSources(Graphics g) {
		g.setColor(Color.blue);
		for (Point rsource : Constants.REPL_SOURCE()) {
			Circle c = new Circle( rsource, 1500);
			Point tl = c.getTopLeftPoint();
			Point br = c.getBottomRightPoint();
			Point guiTl = toGUICoord(tl);
			Point guiBr = toGUICoord(br);
			//Point guiSource = toGUICoord( rsource );
		    g.drawOval(guiTl.getX(), guiTl.getY(), guiBr.getX()- guiTl.getX(), (guiBr.getY()-guiTl.getY() ) / 2);
		}
	}

}
