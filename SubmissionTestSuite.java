package breakout;


import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import breakout.radioactivity.Alpha;
import breakout.radioactivity.Ball;
import breakout.radioactivity.NormalBall;
import breakout.radioactivity.SuperChargedBall;
import breakout.utils.*;

class SubmissionTestSuite {

	//fields for testing BreakoutState
	private Point BR;
	private BlockState ablock;
	private BlockState[] someblocks;
	private Ball aball;
	private Ball[] someballs;
	private PaddleState apad;
	
	private Alpha alpha;
	private Alpha[] alphas;
	
	@BeforeEach
	void setUp() {
		BR = new Point(Constants.WIDTH, Constants.HEIGHT);
		//top left of target normal block
		
		ablock = new NormalBlockState(
				new Rect( Constants.ORIGIN, new Point(Constants.BLOCK_WIDTH,Constants.BLOCK_HEIGHT)) );
		someblocks = new BlockState[] { ablock };
		
		apad = new NormalPaddleState(
				new Point( Constants.WIDTH / 2, (3 * Constants.HEIGHT) / 4),
				Constants.TYPICAL_PADDLE_COLORS(),
				Constants.TYPICAL_PADDLE_COLORS()[0]);
		
		aball =
				new NormalBall(
					new Circle(
						new Point(BR.getX() / 2 , Constants.HEIGHT / 2)
						, Constants.INIT_BALL_DIAMETER)
					, Constants.INIT_BALL_VELOCITY);
		someballs = new Ball[] { aball };
		
		Alpha alph = new Alpha( aball.getLocation() , aball.getVelocity() );
		alph.setPosition( aball.getCenter().plus(new Vector( BR.getX() / 4 , 0 ) ));
		alpha = alph;
		alphas = new Alpha[] { alpha };
		
	}
	
		
		
	@Test
	void sanity() {
		Ball[] balls = new Ball[] { Setups.typicalNormalBall(4) }; //1.5 blocks from right wall
		BlockState[] blocks = Setups.typicalBlocks();
		
		BreakoutState bstate = new BreakoutState(
				balls,
				blocks,
				BR,
				apad);
		
		assertTrue( bstate.getBalls()[0].getVelocity().getX() == 7); //should hold because Setups must remain unchanged.
		assertEquals(1, bstate.getBalls().length);
		assertEquals(4, bstate.getBlocks().length);
		
		bstate.tickDuring( 400 );
		
		//ticking 400ms is not enough to bounce on the wall (1.5 blocks)
		//but enough to travel > 0.5 blocks.
		assertTrue( bstate.getBalls()[0].getVelocity().getX() >= 0 );  //vel. has not changed.
		
		//extra ~second is needed to bounce on the wall
		bstate.tickDuring(900);
		assertFalse( bstate.getBalls()[0].getVelocity().getX() >= 0 );
		
		assertEquals(1, bstate.getBalls().length);
		assertEquals(4, bstate.getBlocks().length);			
	}
	
	@Test
	void normalBlockGetsNormalBall() {
		Ball[] balls = new Ball[] { Setups.typicalNormalBall(0) };
		BlockState[] blocks = Setups.typicalBlocks();
		
		BreakoutState bstate = new BreakoutState(
				balls,
				blocks,
				BR,
				apad);
		
		//400 ms suffice for a slow ball to hit the block
		bstate.tickDuring(400);
		assertTrue( bstate.getBalls()[0].getVelocity().getX() <= 0);
		assertTrue( bstate.getBlocks().length == 3);
				
	}
	
	@Test
	void ReplPaddleGetsNormalBallAndSameVelocities() {
		//put ball right above the paddle
		aball.setLocation( new Circle(
				apad.getCenter().plus( new Vector( 0, - (Constants.BLOCK_HEIGHT / 2) )),
				Constants.INIT_BALL_DIAMETER ));
		// with down velocity
		aball.setVelocity( new Vector( 0 , 7 ));
		
		ReplicatingPaddleState replpad = new ReplicatingPaddleState(
				apad.getCenter(),
				Constants.TYPICAL_PADDLE_COLORS(),
				Constants.TYPICAL_PADDLE_COLORS()[0],
				4);
		
		BreakoutState bstate = new BreakoutState(
				someballs,
				someblocks,
				BR,
				replpad);
		
		assertTrue( bstate.getBalls()[0].getVelocity().getY() >= 0 ); //looking down
		
		bstate.tickDuring(400);
		
		assertTrue( bstate.getBalls().length == 4); //3 new balls have been generated.
		
		assertTrue( Arrays.stream(bstate.getBalls()).allMatch( 
		b -> b.getVelocity().equals( bstate.getBalls()[0].getVelocity() ) ));
		
	}
	
	@Test
	void PowerupBlockGetsNormalBall() {
		Ball[] balls = new Ball[] { Setups.typicalNormalBall(3) };
		BlockState[] blocks = Setups.typicalBlocks();
		
		BreakoutState bstate = new BreakoutState(
				balls,
				blocks,
				BR,
				apad);
		
		//400 ms suffice for a slow ball to hit the block
		bstate.tickDuring(400);
		assertTrue( bstate.getBalls()[0].getVelocity().getX() <= 0);
		assertTrue( bstate.getBlocks().length == 3);
		
		SuperChargedBall superBall = (SuperChargedBall) bstate.getBalls()[0];
		assertEquals( Constants.INIT_BALL_DIAMETER + 600 , superBall.getLocation().getDiameter() ); 
				
	}
	
	@Test
	void ReplBlockGetsNormalBall() {
		Ball[] balls = new Ball[] { Setups.typicalNormalBall(2) };
		BlockState[] blocks = Setups.typicalBlocks();
		
		BreakoutState bstate = new BreakoutState(
				balls,
				blocks,
				BR,
				apad);
		
		//400 ms suffice for a slow ball to hit the block
		bstate.tickDuring(400);
		assertTrue( bstate.getBalls()[0].getVelocity().getX() <= 0);
		assertTrue( bstate.getBlocks().length == 3);
		
		ReplicatingPaddleState rPad = (ReplicatingPaddleState) bstate.getPaddle(); //this should not throw exceptions
		assertEquals( 4, rPad.getCount() );
				
	}
	
	@Test
	void ReplBlockGetsSuperBall() {
		Ball[] balls = new Ball[] { Setups.typicalSuperBall(2) };
		BlockState[] blocks = Setups.typicalBlocks();
		
		BreakoutState bstate = new BreakoutState(
				balls,
				blocks,
				BR,
				apad);
		
		//400 ms suffice for a slow ball to hit the block
		bstate.tickDuring(400);
		assertTrue( bstate.getBalls()[0].getVelocity().getX() >= 0); //no change of vel.
		assertTrue( bstate.getBlocks().length == 3);
		
		ReplicatingPaddleState rPad = (ReplicatingPaddleState) bstate.getPaddle(); //this should not throw exceptions
				
	}
	
	@Test
	/**
	 * a couple calls to public methods of the multi class abstraction and other methods
	 */
	void multiClassSanity() {
		assertTrue(aball.getAlphas().isEmpty());
		aball.linkTo(alpha);
		assertTrue( alpha.getBalls().size() >= 0 );
		aball.unLink(alpha);
		assertNotEquals(0, aball.getEcharge());
		assertNotEquals(0, aball.regetEcharge());
		
		BreakoutState bs1 = new BreakoutState(
				someballs, someblocks, BR, apad);
		BreakoutState bs2 = new BreakoutState(
				someblocks, BR, apad, someballs, alphas);
		
		bs1.getAlphas(); bs2.getBalls();
		
		assertTrue( BreakoutState.sameBallArray( new Ball[] {} , new Ball[] {} ));
		
		Setups.typicalBouncePosition();
	}
	
	@Test
	/**
	 * no custom equals for balls
	 */
	void BallEqualityByRef() {
		Ball ball2 = aball.clone();
		assertTrue( aball.equalContent(ball2));
		Set<Ball> ballSet = new HashSet<Ball>();
		ballSet.add(aball); ballSet.add(ball2);
		assertEquals(2, ballSet.size() );
	}
	
	@Test
	/**
	 * no custom equals for balls
	 */
	void AlphaEqualityByRef() {
		Alpha alpha2 = alpha.clone();
		assertTrue( alpha.equalContent(alpha2));
		Set<Alpha> alphaSet = new HashSet<Alpha>();
		alphaSet.add(alpha); alphaSet.add(alpha2);
		assertEquals(2, alphaSet.size() );
	}


}
