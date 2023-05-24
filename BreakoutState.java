package breakout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import breakout.radioactivity.Alpha;
import breakout.radioactivity.Ball;
import breakout.radioactivity.PreBall;
import breakout.radioactivity.SuperChargedBall;
import breakout.radioactivity.NormalBall;
import breakout.utils.Circle;
import breakout.utils.Point;
import breakout.utils.Rect;
import breakout.utils.Vector;


/**
 * Represents the current state of a breakout game.
 *  
 * @invar | getBlocks() != null
 * @invar | getPaddle() != null
 * @invar | Arrays.stream(getBlocks()).allMatch(b -> b != null)
 * @invar | getBottomRight() != null
 * @invar | Constants.ORIGIN.isUpAndLeftFrom(getBottomRight())
 * @invar | Arrays.stream(getBlocks()).allMatch(b -> getField().contains(b.getLocation()))
 * @invar | getField().contains(getPaddle().getLocation())
 */
public class BreakoutState {

	
	/**
	 * @invar | bottomRight != null
	 * @invar | Constants.ORIGIN.isUpAndLeftFrom(bottomRight)
	 */
	private final Point bottomRight;

	private Ball[] balls;

	private Alpha[] alphas;
	/**
	 * @invar | blocks != null
	 * @invar | Arrays.stream(blocks).allMatch(b -> getFieldInternal().contains(b.getLocation()))
	 * @representationObject
	 */
	private BlockState[] blocks;
	/**
	 * @invar | paddle != null
	 * @invar | getFieldInternal().contains(paddle.getLocation())
	 */
	private PaddleState paddle;

	private final Rect topWall;
	private final Rect rightWall;
	private final Rect leftWall;
	private final Rect[] walls;
	
	/**
	 * Construct a new BreakoutState with the given balls, blocks, paddle.
	 * 
	 * This constructor assumes that no alphas are peers to balls.
	 *  
	 * 
	 * @throws IllegalArgumentException | blocks == null
	 * @throws IllegalArgumentException | bottomRight == null
	 * @throws IllegalArgumentException | paddle == null
	 * @throws IllegalArgumentException | !Constants.ORIGIN.isUpAndLeftFrom(bottomRight)
	 * @throws IllegalArgumentException | !(new Rect(Constants.ORIGIN,bottomRight)).contains(paddle.getLocation())
	 * @throws IllegalArgumentException | !Arrays.stream(blocks).allMatch(b -> (new Rect(Constants.ORIGIN,bottomRight)).contains(b.getLocation()))
	 * 
	 * @post | Arrays.equals(getBlocks(),blocks)
	 * @post | getBottomRight().equals(bottomRight)
	 * @post | getPaddle().equalContent(paddle)
	 */
	public BreakoutState(Ball[] balls, BlockState[] blocks, Point bottomRight, PaddleState paddle) {
		if (blocks == null)
			throw new IllegalArgumentException();
		if (bottomRight == null)
			throw new IllegalArgumentException();
		if (paddle == null)
			throw new IllegalArgumentException();

		if (! Constants.ORIGIN.isUpAndLeftFrom(bottomRight) )
			throw new IllegalArgumentException();
		this.bottomRight = bottomRight;
		if (!getFieldInternal().contains(paddle.getLocation()))
			throw new IllegalArgumentException();
		if (!Arrays.stream(blocks).allMatch(b -> getFieldInternal().contains(b.getLocation())))
			throw new IllegalArgumentException();

		this.balls = new Ball[balls.length];
		for(int i = 0; i < balls.length; ++i) {
			this.balls[i] = balls[i];
		}
		this.blocks = blocks.clone();
		this.paddle = paddle.reproduce();

		this.topWall = new Rect(new Point(0, -1000), new Point(bottomRight.getX(), 0));
		this.rightWall = new Rect(new Point(bottomRight.getX(), 0),
				new Point(bottomRight.getX() + 1000, bottomRight.getY()));
		this.leftWall = new Rect(new Point(-1000, 0), new Point(0, bottomRight.getY()));
		this.walls = new Rect[] { topWall, rightWall, leftWall };
		
		this.alphas = new Alpha[] {};
	}
	
	/**
	 * Construct a new BreakoutState with the given blocks, paddle + balls/alphas.
	 * 
	 * @throws IllegalArgumentException | blocks == null
	 * @throws IllegalArgumentException | bottomRight == null
	 * @throws IllegalArgumentException | paddle == null
	 * @throws IllegalArgumentException | !Constants.ORIGIN.isUpAndLeftFrom(bottomRight)
	 * @throws IllegalArgumentException | !(new Rect(Constants.ORIGIN,bottomRight)).contains(paddle.getLocation())
	 * @throws IllegalArgumentException | !Arrays.stream(blocks).allMatch(b -> (new Rect(Constants.ORIGIN,bottomRight)).contains(b.getLocation()))
	 * @post | Arrays.equals(getBlocks(),blocks)
	 * @post | getBottomRight().equals(bottomRight)
	 * @post | getPaddle().equalContent(paddle)
	 */
	public BreakoutState(BlockState[] blocks, Point bottomRight, PaddleState paddle,
			Ball[] balls, Alpha[] alphas) {
		if (blocks == null)
			throw new IllegalArgumentException();
		if (bottomRight == null)
			throw new IllegalArgumentException();
		if (paddle == null)
			throw new IllegalArgumentException();
		if (!Constants.ORIGIN.isUpAndLeftFrom(bottomRight))
			throw new IllegalArgumentException();
		this.bottomRight = bottomRight;
		if (!getFieldInternal().contains(paddle.getLocation()))
			throw new IllegalArgumentException();
		if (!Arrays.stream(blocks).allMatch(b -> getFieldInternal().contains(b.getLocation())))
			throw new IllegalArgumentException();

		//TODO
		this.balls = null;
		this.alphas = null;

		
		this.blocks = blocks.clone();
		this.paddle = paddle;

		this.topWall = new Rect(new Point(0, -1000), new Point(bottomRight.getX(), 0));
		this.rightWall = new Rect(new Point(bottomRight.getX(), 0),
				new Point(bottomRight.getX() + 1000, bottomRight.getY()));
		this.leftWall = new Rect(new Point(-1000, 0), new Point(0, bottomRight.getY()));
		this.walls = new Rect[] { topWall, rightWall, leftWall };
	}
	
	/**
	 * Returns a fresh ball, with fresh peers (alphas, other balls).
	 * In other words getBalls gives a deepcopy.
	 *
	 * @creates result
     * @creates ...result
	 */
	public Ball[] getBalls() {
		Ball[] resBalls = new Ball[balls.length];
		Alpha[] resAlphas = new Alpha[alphas.length];
		for(int i = 0; i < balls.length; ++i) {
			resBalls[i] = balls[i];
		}
		for(int j = 0; j < alphas.length; ++j) {
			resAlphas[j] = alphas[j];
		}
		//we add links where relevant.
		for (int i = 0; i < balls.length; ++i) {
			for (int j = 0; j < alphas.length; ++j) {
				//checking the symmetric statement would work
				//thanks to bidir invar.
				if (alphas[j].getBalls().contains(balls[i])) {
					resBalls[i].linkTo(resAlphas[j]);
				}
			}
		}
		return resBalls;
	}
	

	/**
	 * Returns a fresh alpha, with fresh peers (balls, other alphas).
	 * In other words getAlphas gives a deepcopy.
	 *
	 * @creates result
     * @creates ...result
	 */
	public Alpha[] getAlphas() {
		Ball[] resBalls = new Ball[balls.length];
		Alpha[] resAlphas = new Alpha[alphas.length];
		for(int i = 0; i < balls.length; ++i) {
			resBalls[i] = balls[i];
		}
		for(int j = 0; j < alphas.length; ++j) {
			resAlphas[j] = alphas[j];
		}
		//we add links where relevant.
		for (int i = 0; i < balls.length; ++i) {
			for (int j = 0; j < alphas.length; ++j) {
				//checking the symmetric statement would work
				//thanks to bidir invar.
				if (alphas[j].getBalls().contains(balls[i])) {
					resBalls[i].linkTo(resAlphas[j]);
				}
			}
		}
		return resAlphas;
	}

	/**
	 * Return the blocks of this BreakoutState. 
	 *
	 * @creates | result
	 */
	public BlockState[] getBlocks() {
		return blocks.clone();
	}

	/**
	 * Return the paddle of this BreakoutState. 
	 */
	public PaddleState getPaddle() {
		return paddle.reproduce();
	}

	/**
	 * Return the point representing the bottom right corner of this BreakoutState.
	 * The top-left corner is always at Coordinate(0,0). 
	 */
	public Point getBottomRight() {
		return bottomRight;
	}

	// internal version of getField which can be invoked in partially inconsistent states
	private Rect getFieldInternal() {
		return new Rect(Constants.ORIGIN, bottomRight);
	}
	
	/**
	 * LEGIT
	 * 
	 * Return a rectangle representing the game field.
	 * @post | result != null
	 * @post | result.getTopLeft().equals(Constants.ORIGIN)
	 * @post | result.getBottomRight().equals(getBottomRight())
	 */
	public Rect getField() {
		return getFieldInternal();
	}
	
	/**
	 * LEGIT
	 * 
	 * same balls (using equalContent), in same order.
	 */
	public static boolean sameBallArray(Ball[] arr1, Ball[] arr2) {
		if (arr1 == null) {return arr2 == null;}
		if (arr2 == null) {return false;} //arr1 not null, arr2 null.
		if (arr1.length != arr2.length) {return false;}
		boolean res = true;
		for (int i = 0 ; i < arr1.length ; i++) {
			res = res && arr1[i].equalContent(arr2[i]); //loc,vel,charge for Ball
		}
		return res;
	}
	
	private void bounceWalls(PreBall ball) {
		for (Rect wall : walls) {
			if (ball.collidesWith(wall)) {
				ball.hitWall(wall);
			}
		}
	}
	


	private void clampPreBall(PreBall b) {
		Circle loc = getFieldInternal().constrain(b.getLocation());
		b.setLocation(loc);
	}

	/**
	 * @mutates | ball
	 * @mutates_properties | getPaddle()
	 * @post | result.getCenter() .equals( ball.getCenter() )
	 */
	private Ball collideBallBlocks(Ball ball) {
		for (BlockState block : blocks) {
			if (ball.collidesWith(block.getLocation())) {
				//does not affect the balls
				boolean destroyed = hitBlock(block, ball.getVelocity().getSquareLength());
				//may affect ball diam/velocity
				ball.hitBlock(block.getLocation(), destroyed);
				paddle = block.paddleStateAfterHit(paddle);
				return block.ballStateAfterHit(ball);
			}
		}
		return ball;
	}

	
	/**
	 * "Hits" the block argument once with a ball having speed sqrt(squaredSpeed).
	 * This can result in the block being destructed, i.e. not tracked in getBlocks() (returns ture in that case).
	 * Or no destruction occurs; in this case the block is updated instead of being removed. (returns false in that case).
	 * 
	 * Does not affect the balls.
	 * 
	 * @pre | squaredSpeed >= 0
	 * // @post | sameBallArray( old( balls ), balls )
	 * // @post | false
	 * @mutates | this
	 */
	private boolean hitBlock(BlockState block, int squaredSpeed) {
		boolean destroyed = true;
		ArrayList<BlockState> nblocks = new ArrayList<BlockState>();
		for (BlockState b : blocks) {
			if (b != block) {
				nblocks.add(b);
			} else {
				BlockState nb = block.blockStateAfterHit(squaredSpeed);
				if (nb != null) {
					nblocks.add(nb);
					destroyed = false;
				}
			}
		}
		blocks = nblocks.toArray(new BlockState[] {});
		return destroyed;
	}

	/**
	 * TODO
	 * 
	 * 
	 * If ball collides the paddle,
	 *  - its speed is mirrored
	 *  - if the paddle is a replicator paddle new balls (with no peers) get emitted (-> this.balls grows)
	 *  - a new alpha linked with ball is anyway created (-> this.alphas grows)
	 * 
	 * @mutates | ball
	 * @mutates | this
	 * @post | ball.getCenter().equals(old( ball.getCenter() ))
	 */
	private void collideBallPaddle(Ball ball, Vector paddleVel) {

	}

	/**
	 * TODO
	 */
	private void collideAlphaPaddle(Alpha alpha, Vector paddleVel) {

	}

	/**
	 * LEGIT
	 * 
	 * Move all moving objects one step forward. please prefer tickDuring.
	 * 
	 * @pre | paddleDir == -1 || paddleDir == 0 || paddleDir == 1
	 * 
	 * @pre | elapsedTime >= 0
	 * @pre | elapsedTime <= Constants.MAX_ELAPSED_TIME
	 * 
	 * @mutates | this
	 * @mutates | ...getBalls()
	 */
	void tick(int paddleDir, int elapsedTime) {
		//the comments indicate some events that may occur in each call.
		stepPreBalls(elapsedTime); //move balls/alphas a bit
		bouncePreBallsOnWalls(); //preball velocities may change. Magnetism may occur.
		removeDeadPreBalls(); //remove preballs out of field. must unlink.
		bounceBallsOnBlocks(); //blocks may be affected and ball may be affected/replaced (but not their center). 
		bounceBallsOnPaddle(paddleDir); //balls velocities may change, and new balls may be created. radioactivity may occur.
		bounceAlphasOnPaddle(paddleDir); //anti-radioactivity may occur
		clampPreBalls(); //for balls to remain within field.
		balls = Arrays.stream(balls).filter(x -> x != null).toArray(Ball[]::new);
		alphas = Arrays.stream(alphas).filter(x -> x != null).toArray(Alpha[]::new);
		noLongerSuperCharged(); //useless supercharged instances replaced with normal balls. remember to unlink.
	}

	/**
	 * LEGIT
	 * 
	 * ticking by 20ms increments
	 */
	public void tickDuring(int elapsedTime) {
		for (int i = 0 ; i + 20 <= elapsedTime ; i += 20) {
			tick(0, 20);
		}
		if( elapsedTime % 20 != 0) { 
		  tick(0, elapsedTime % 20);
		}
	}

	private void clampPreBalls() {
		for(int i = 0; i < balls.length; ++i) {
			if(balls[i] != null) {
				clampPreBall(balls[i]);
			}		
		}
		for(int i = 0; i < alphas.length; ++i) {
			if(alphas[i] != null) {
				clampPreBall(alphas[i]);
			}
		}
	}

	/**
	 * @pre | paddleDir == -1 || paddleDir == 0 || paddleDir == 1
	 */
	private void bounceBallsOnPaddle(int paddleDir) {
		Vector paddleVel = Constants.PADDLE_VEL.scaled(paddleDir);
		for(int i = 0; i < balls.length; ++i) {
			if(balls[i] != null) {
				collideBallPaddle(balls[i], paddleVel);
			}
		}
	}
	
	private void bounceAlphasOnPaddle(int paddleDir) {
		Vector paddleVel = Constants.PADDLE_VEL.scaled(paddleDir);
		//note that collideAlphaPaddle may change this.balls
		for(int i = 0; i < alphas.length; ++i) {
			if(alphas[i] != null) {
				collideAlphaPaddle(alphas[i], paddleVel);
			}
		}
	}

	private void bounceBallsOnBlocks() {
		for(int i = 0; i < balls.length; ++i) {
			if(balls[i] != null) {
				balls[i] = collideBallBlocks(balls[i]);
			}
		}
	}
	
	/**
	 * TODO
	 * 
	 * @pre | Arrays.stream(balls).allMatch(bal -> bal != null)
	 * @pre | Arrays.stream(alphas).allMatch(a -> a != null)
	 * 
	 * if balls[i] is out of field, balls[i] is set to null (same for alpha).
	 * Note that this means the private invariant is momentarily broken.
	 * 
	 * this method should unlink the balls/alphas out of bound before dereferencing them.
	 * 
	 * @mutates | this
	 */
	private void removeDeadPreBalls() {
		
	}
	
	/**
	 * LEGIT 
	 */
	private boolean belowLimit(PreBall ball) {
		return ball.getLocation().getBottommostPoint().getY() > bottomRight.getY();
	}

	private void bouncePreBallsOnWalls() {
		for(int i = 0; i < balls.length; ++i) {
			bounceWalls(balls[i]);
		}
		for(int i = 0; i < alphas.length; ++i) {
			bounceWalls(alphas[i]);
		}
	}

	
	private void stepPreBalls(int elapsedTime) {
		for(int i = 0; i < balls.length; ++i) {
			balls[i].move(balls[i].getVelocity().scaled(elapsedTime), elapsedTime);
		}
		for(int i = 0; i < alphas.length; ++i) {
			alphas[i].move(alphas[i].getVelocity().scaled(elapsedTime), elapsedTime);
		}
	}
	
	/**
	 * LEGIT
	 * 
	 * Move the paddle right.
	 * 
	 * @mutates | this
	 */
	public void movePaddleRight(int elapsedTime) {
		paddle.move(Constants.PADDLE_VEL.scaled(elapsedTime), getField());
	}

	/**
	 * LEGIT
	 * 
	 * Move the paddle left.
	 * 
	 * @mutates | this
	 */
	public void movePaddleLeft(int elapsedTime) {
		paddle.move(Constants.PADDLE_VEL.scaled(-elapsedTime), getField());
	}

	/**
	 * LEGIT
	 * 
	 * Return whether this BreakoutState represents a game where the player has won.
	 * 
	 * @post | result == (getBlocks().length == 0 && !isDead())
	 * @inspects | this
	 */
	public boolean isWon() {
		return getBlocks().length == 0 && !isDead();
	}

	/**
	 * LEGIT
	 * 
	 * Return whether this BreakoutState represents a game where the player is dead.
	 * 
	 * @post | result == (getBalls().length == 0)
	 * @inspects | this
	 */
	public boolean isDead() {
		return getBalls().length == 0;
	}

	private void noLongerSuperCharged() {
		for (int i = 0 ; i < balls.length ; i++) {
			balls[i] = balls[i].backToNormal();
		}
	}
}
