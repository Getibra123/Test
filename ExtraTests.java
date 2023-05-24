package breakout;


import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import breakout.radioactivity.Ball;
import breakout.radioactivity.NormalBall;
import breakout.radioactivity.SuperChargedBall;
import breakout.utils.*;

/**
 * /!\ changing something early in the file must be done with great care
 * as lots of tests depend on early setups (e.g. methods named "typical...")  
 */
class ExtraTests {
		
	//fields for testing BreakoutState
	private Point BR;
	private BlockState ablock;
	private BlockState[] someblocks;
	private Ball aball;
	private Ball[] someballs;
	private PaddleState apad;
	
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
	}
	

	
	
	// BASIC BLOCK BOUNCING



	@Nested
	class ExtraTest1 {
		@Test
		/**
		 * normal paddles: 1 color.
		 * replicating paddle: painted using 3 colors.
		 */
		void PaintingOfPaddles() {
			PaddleState apad2 = new ReplicatingPaddleState(
					apad.getCenter(),
					Constants.TYPICAL_PADDLE_COLORS(),
					Constants.TYPICAL_PADDLE_COLORS()[0],
					4);
			
			assertEquals( 1 , apad.getActualColors().length);
			assertEquals( 3 , apad2.getActualColors().length);
		}
		
		@Test
		void PaintingOfSlowFastBalls() {
			
			aball.setVelocity( new Vector( 5, 0)); //slow
			
			BreakoutState bstate = new BreakoutState(
					someballs,
					someblocks,
					BR,
					apad);
			
			Color slowCol = bstate.getBalls()[0].getColor();
			
			aball.setVelocity( new Vector( 12, 0)); //fast
			BreakoutState bstateFast = new BreakoutState(
					someballs,
					someblocks,
					BR,
					apad);
			
			Color fastCol = bstateFast.getBalls()[0].getColor();
			
			assertNotEquals( slowCol , fastCol );
			
		}
		
		@Test
		void SturdyBlockWith1LifeGetsSlowNormalBall() {
			Ball[] balls = new Ball[] { Setups.typicalNormalBall(1) };
			BlockState[] blocks = Setups.typicalBlocks();
			
			SturdyBlockState stub = (SturdyBlockState) blocks[1];
			SturdyBlockState nstub = new SturdyBlockState( stub.getLocation() , 1 );
			blocks[1] = nstub; //replacing this block with a 1life left sturdy block.
			
			BreakoutState bstate = new BreakoutState(
					balls,
					blocks,
					BR,
					apad);
			
			bstate.tickDuring(400);
			assertTrue( bstate.getBalls()[0].getVelocity().getX() <= 0 ); //we have bounced
			assertTrue( bstate.getBlocks().length == 4); // block was not destroyed since ball is slow.
			
			SturdyBlockState resBlock = (SturdyBlockState) bstate.getBlocks()[1];
			assertEquals( 1, resBlock.getLivesLeft() );
		}
		
		@Test
		void SturdyBlockWith1LifeGetsSlowSuperBall() {
			Ball[] balls = new Ball[] { Setups.typicalSuperBall(1) };
			BlockState[] blocks = Setups.typicalBlocks();
			
			SturdyBlockState stub = (SturdyBlockState) blocks[1];
			SturdyBlockState nstub = new SturdyBlockState( stub.getLocation() , 1 );
			blocks[1] = nstub; //replacing this block with a 1life left sturdy block.
			
			BreakoutState bstate = new BreakoutState(
					balls,
					blocks,
					BR,
					apad);
			
			bstate.tickDuring(400);
			assertTrue( bstate.getBalls()[0].getVelocity().getX() <= 0 ); //we have bounced
			assertTrue( bstate.getBlocks().length == 4); // block was not destroyed since ball is slow.
			
			SturdyBlockState resBlock = (SturdyBlockState) bstate.getBlocks()[1];
			assertEquals( 1, resBlock.getLivesLeft() );
		}
		
		@Test
		void SturdyBlockWith1LifeGetsFastNormalBall() {
			Ball b = Setups.typicalNormalBall(1); b.setVelocity( new Vector( 11 , 0 ) ); //makes it fast.
			Ball[] balls = new Ball[] { b };
			BlockState[] blocks = Setups.typicalBlocks();
			
			SturdyBlockState stub = (SturdyBlockState) blocks[1];
			SturdyBlockState nstub = new SturdyBlockState( stub.getLocation() , 1 );
			blocks[1] = nstub; //replacing this block with a 1life left sturdy block.
			
			BreakoutState bstate = new BreakoutState(
					balls,
					blocks,
					BR,
					apad);
			
			bstate.tickDuring(400);
			assertTrue( bstate.getBalls()[0].getVelocity().getX() <= 0 ); //we have bounced
			assertTrue( bstate.getBlocks().length == 3); // block was destroyed since ball is fast
		}
		
		@Test
		void SturdyBlockWith1LifeGetsFastSuperBall() {
			Ball b = Setups.typicalSuperBall(1); b.setVelocity( new Vector( 11 , 0 ) ); //makes it fast.
			Ball[] balls = new Ball[] { b };
			BlockState[] blocks = Setups.typicalBlocks();
			
			SturdyBlockState stub = (SturdyBlockState) blocks[1];
			SturdyBlockState nstub = new SturdyBlockState( stub.getLocation() , 1 );
			blocks[1] = nstub; //replacing this block with a 1life left sturdy block.
			
			BreakoutState bstate = new BreakoutState(
					balls,
					blocks,
					BR,
					apad);
			
			bstate.tickDuring(200);
			assertTrue( bstate.getBlocks().length == 3); // block was destroyed since ball is fast
			assertTrue( bstate.getBalls()[0].getVelocity().getX() >= 0 ); //we have not bounced; super balls keep their vel.
		}
		
		@Test
		void sturdyBlockGetsNormalBall() {
			Ball[] balls = new Ball[] { Setups.typicalNormalBall(1) };
			BlockState[] blocks = Setups.typicalBlocks();
			
			BreakoutState bstate = new BreakoutState(
					balls,
					blocks,
					BR,
					apad);
			
			//400 ms suffice for a slow ball to hit the block
			assertTrue( bstate.getBalls()[0].getVelocity().getX() >= 0);
			assertTrue( bstate.getBlocks()[1].getLocation().getTopLeft().getY() <= bstate.getBalls()[0].getCenter().getY());
			assertTrue( bstate.getBalls()[0].getCenter().getY() <= bstate.getBlocks()[1].getLocation().getBottomRight().getY() );			
			
			bstate.tickDuring(400);
			assertTrue( bstate.getBalls()[0].getVelocity().getX() <= 0);
			assertTrue( bstate.getBlocks().length == 4);
			
			SturdyBlockState sblock = (SturdyBlockState) bstate.getBlocks()[1];
			assertEquals( 2 , sblock.getLivesLeft() );
					
		}
		
		@Test
		void sturdyBlockGetsSuperBall() {
			Ball[] balls = new Ball[] { Setups.typicalSuperBall(1) };
			BlockState[] blocks = Setups.typicalBlocks();
			
			BreakoutState bstate = new BreakoutState(
					balls,
					blocks,
					BR,
					apad);
			
			//400 ms suffice for a slow ball to hit the block
			assertTrue( bstate.getBalls()[0].getVelocity().getX() >= 0);
			assertTrue( bstate.getBlocks()[1].getLocation().getTopLeft().getY() <= bstate.getBalls()[0].getCenter().getY());
			assertTrue( bstate.getBalls()[0].getCenter().getY() <= bstate.getBlocks()[1].getLocation().getBottomRight().getY() );			
			
			bstate.tickDuring(400);
			assertTrue( bstate.getBalls()[0].getVelocity().getX() <= 0);
			assertTrue( bstate.getBlocks().length == 4);
			
			SturdyBlockState sblock = (SturdyBlockState) bstate.getBlocks()[1];
			assertEquals( 2 , sblock.getLivesLeft() );
			
			//superballs just bounce on sturdy blocks with > 1 lives left.
					
		}
		
		@Test
		void normalBlockGetsSuperBall() {
			Ball[] balls = new Ball[] { Setups.typicalSuperBall(0) };
			BlockState[] blocks = Setups.typicalBlocks();
			
			BreakoutState bstate = new BreakoutState(
					balls,
					blocks,
					BR,
					apad);
			
			//400 ms suffice for a slow ball to hit the block
			bstate.tickDuring(400);
			assertTrue( bstate.getBalls()[0].getVelocity().getX() >= 0);//speed has not changed
			assertTrue( bstate.getBlocks().length == 3); //block has been destroyed
			assertEquals(
					Constants.INIT_BALL_DIAMETER + 500,
					bstate.getBalls()[0].getLocation().getDiameter());
					
		}
	}





	@Nested
	class ExtraTest2 {
		
		@Test
		void ReplPaddleGetsNormalBallAndUseReplSources() {
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
			
			
			//the new balls have been created at repl sources,
			//hence there is at least a 1500 difference in the Y coordinate
			//for at least 1 pair of balls b1 b2.
			Ball[] theballs = bstate.getBalls();
			ArrayList<Integer> ballYDiffs = new ArrayList<Integer>();
			for (int i = 0 ; i < theballs.length ; i++) {
				for (int j = 0 ; j < theballs.length ; j++) {
					ballYDiffs.add( theballs[i].getCenter().getY() - theballs[j].getCenter().getY());
				}
			}
			assertTrue(
					ballYDiffs.stream().anyMatch( d -> d > 1500 )); //typically > 2000		
		}
		
		@Test
		void ReplPaddleGetsNormalBallAndDecrementsPaddle() {
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
			
			ReplicatingPaddleState rpad = (ReplicatingPaddleState) bstate.getPaddle();
			assertEquals( 3, rpad.getCount() );
		}
		
		@Test
		void ReplPaddleGetsSuperBall() {
			SuperChargedBall sball = new SuperChargedBall(
					aball.getLocation(),
					aball.getVelocity(),
					Constants.SUPERCHARGED_BALL_LIFETIME);
			//right above the paddle
			sball.setLocation( new Circle(
					apad.getCenter().plus( new Vector( 0, - (Constants.BLOCK_HEIGHT / 2) )),
					Constants.INIT_BALL_DIAMETER ));
			// with down velocity
			sball.setVelocity( new Vector( 0 , 7 ));
			
			ReplicatingPaddleState replpad = new ReplicatingPaddleState(
					apad.getCenter(),
					Constants.TYPICAL_PADDLE_COLORS(),
					Constants.TYPICAL_PADDLE_COLORS()[0],
					4);
			
			BreakoutState bstate = new BreakoutState(
					new Ball[] { sball } ,
					someblocks,
					BR,
					replpad);
			
			assertTrue( bstate.getBalls()[0].getVelocity().getY() >= 0 ); //looking down
			
			bstate.tickDuring(400);
			
			assertTrue( bstate.getBalls().length == 4); //3 new balls have been generated.
			
			//generated balls are superballs. 
			SuperChargedBall sball1 = (SuperChargedBall) bstate.getBalls()[1];
			SuperChargedBall sball2 = (SuperChargedBall) bstate.getBalls()[2];
		}
		
		@Test
		/**
		 * A superball travels a line of normal blocks (so no bounces)
		 * and bounces on the wall. Its diameter shrinks as it travels further.
		 */
		void superBallsShrink() {
			Ball sball = Setups.typicalSuperBall(0);
			sball.setVelocity( new Vector( - 17 , 0 ) ); //to the left and fast
			
			Point tl = new Point( 0, Constants.HEIGHT - Constants.BLOCK_HEIGHT);
			Vector blockDiag = new Vector( Constants.BLOCK_WIDTH , Constants.BLOCK_HEIGHT );
			Vector moveRight = new Vector( Constants.BLOCK_WIDTH , 0);
			
			// 8 blocks filling the last line from left to right.
			ArrayList<BlockState> blocks = new ArrayList<BlockState>();
			for (int i = 0 ; i < 8 ; i++) {
				Point locTL = tl.plus( moveRight.scaled(i ));
				Rect locRec = new Rect( locTL , locTL.plus(blockDiag ));
				blocks.add( new NormalBlockState( locRec ));
			}
			
			blocks.add(ablock);
			
			apad.setCenter( apad.getCenter().plus( new Vector( 0 , - Constants.BLOCK_HEIGHT )));
			BreakoutState bstate = new BreakoutState(
					new Ball[] { sball },
					blocks.toArray( new BlockState[] { null } ),
					BR,
					apad);
			
			assertEquals(9 , bstate.getBlocks().length );
			
			assertTrue( bstate.getBalls()[0].getVelocity().getX() <= 0);
			bstate.tickDuring(9000);
			assertTrue( bstate.getBalls()[0].getVelocity().getX() >= 0); //has bounced on wall
			SuperChargedBall thing = (SuperChargedBall) bstate.getBalls()[0];
			assertEquals( 1 , bstate.getBlocks().length);
			assertEquals( Constants.INIT_BALL_DIAMETER , bstate.getBalls()[0].getLocation().getDiameter() );
			
			
		}
		
		@Test
		void superBallsDie() {
			SuperChargedBall sball = new SuperChargedBall(
					aball.getLocation(),
					new Vector( -1 , 0),
					300);
			
			BreakoutState bstate = new BreakoutState(
					new Ball[] { sball },
					someblocks,
					BR,
					apad);
			
			assertEquals( sball.getClass() , bstate.getBalls()[0].getClass() );
			bstate.tickDuring(400);
			assertEquals( 1 , bstate.getBalls().length );
			assertNotEquals( sball.getClass() , bstate.getBalls()[0].getClass() );
			
		}
		
		@Test
		void superChargedBallConstrDefensive() {
			assertThrows( IllegalArgumentException.class,
					() -> new SuperChargedBall( aball.getLocation() , aball.getVelocity() , 11000) );
		}
		
		@Test
		/**
		 * When count == 2, and a ball hits the repl paddle, the paddle is replaced with a normal one. 
		 */
		void ReplPaddleGetsNormalBallAndUpcastsPaddle() {
			//put ball right above the paddle
			aball.setLocation( new Circle(
					apad.getCenter().plus( new Vector( 0, - (Constants.BLOCK_HEIGHT / 2) )),
					Constants.INIT_BALL_DIAMETER ));
			// with down velocity
			aball.setVelocity( new Vector( 0 , 7 ));
			
			//replpad has count = 2
			ReplicatingPaddleState replpad = new ReplicatingPaddleState(
					apad.getCenter(),
					Constants.TYPICAL_PADDLE_COLORS(),
					Constants.TYPICAL_PADDLE_COLORS()[0],
					2);
			
			BreakoutState bstate = new BreakoutState(
					someballs,
					someblocks,
					BR,
					replpad);
			
			assertTrue( bstate.getBalls()[0].getVelocity().getY() >= 0 ); //looking down
			
			bstate.tickDuring(400);
			
			
			
			assertEquals( 1, bstate.getPaddle().getActualColors().length); //only normal paddles have this.
		}
		
		@Test
		void PowerupBlockGetsSuperBall() {
			Ball[] balls = new Ball[] { Setups.typicalSuperBall(3) };
			BlockState[] blocks = Setups.typicalBlocks();
			
			BreakoutState bstate = new BreakoutState(
					balls,
					blocks,
					BR,
					apad);
			
			PowerupBallBlockState powBlock = (PowerupBallBlockState) bstate.getBlocks()[3];
			
			//400 ms suffice for a slow ball to hit the block
			bstate.tickDuring(400);
			assertTrue( bstate.getBalls()[0].getVelocity().getX() >= 0);
			assertTrue( bstate.getBlocks().length == 3);
			
			SuperChargedBall superBall = (SuperChargedBall) bstate.getBalls()[0];
			assertEquals( Constants.INIT_BALL_DIAMETER + 600 , superBall.getLocation().getDiameter() );		
		}
		
		@Test
		void BreakoutStatePaddleEncapsIn() {
			BreakoutState bstate = new BreakoutState(
					someballs,
					someblocks,
					BR,
					apad);
			
			apad.setCenter( apad.getCenter().plus(new Vector( 1, 0)));
			
			assertNotEquals( apad.getCenter() , bstate.getPaddle().getCenter() );
		}
		
		@Test
		void BreakoutStatePaddleEncapsOut() {
			BreakoutState bstate = new BreakoutState(
					someballs,
					someblocks,
					BR,
					apad);
			
			PaddleState other = bstate.getPaddle();
			other.setCenter( other.getCenter().plus( new Vector(1,0)));
			assertNotEquals( other.getCenter() , bstate.getPaddle().getCenter() );
		}
		
		@Test
		/**
		 * Repl paddle constr does not: always builds repl paddles with only 2 colors
		 * 
		 * "subclass methods that break superclass invariants"
		 */
		void ReplPadleConsUpholdsParentSpec() {
			ReplicatingPaddleState rpad =
					new ReplicatingPaddleState(
							apad.getCenter(),
							Constants.TYPICAL_PADDLE_COLORS(),
							Constants.TYPICAL_PADDLE_COLORS()[2],
							4);
			
			Color[] colorsBack = rpad.getPossibleColors();
			assertNotEquals( colorsBack[0] , colorsBack[1]);
			assertNotEquals( colorsBack[1] , colorsBack[2]);
			assertNotEquals( colorsBack[0] , colorsBack[2]);
		}
		
		@Test
		/**
		 * superballs that bounce on paddles have their output diameter increased (+100)
		 * 
		 * "superclass methods that (unless overridden) break subclass invariants"
		 */
		void superBallOnPaddleAndDiameter() {

			SuperChargedBall sball = new SuperChargedBall(
					aball.getLocation(),
					aball.getVelocity(),
					Constants.SUPERCHARGED_BALL_LIFETIME);
			//right above the paddle
			sball.setLocation( new Circle(
					apad.getCenter().plus( new Vector( 0, - (Constants.BLOCK_HEIGHT / 2) )),
					Constants.INIT_BALL_DIAMETER ));
			// with down velocity
			sball.setVelocity( new Vector( 0 , 7 ));
			
			BreakoutState bstate = new BreakoutState(
					new Ball[] { sball } ,
					someblocks,
					BR,
					apad);
			
			assertTrue( bstate.getBalls()[0].getVelocity().getY() >= 0 ); //looking down
			int initDiam = bstate.getBalls()[0].getLocation().getDiameter();
			
			bstate.tickDuring(400);
			
			assertTrue( bstate.getBalls()[0].getLocation().getDiameter() > initDiam);
		}
		
		
	}
}
	
	
	
	
	
	
	
	
	
	
	
	

//	@Disabled
//	@Nested
//	/**
//	 * Those tests should fail on the provided bad implementation, succeed on the model solution.
//	 */
//	class OldTaskB {
//		
//		//fields for testing BreakoutState
//		private Point BR;
//		private BlockState ablock;
//		private BlockState[] someblocks;
//		private Ball aball;
//		private Ball[] someballs;
//		private PaddleState apad;
//
//		@BeforeAll
//		static void setUpBeforeClass() throws Exception {
//		}
//
//		@BeforeEach
//		/**
//		 * the BreakoutState tests below depend on the values given here.
//		 */
//		void setUp() {
//			BR = new Point(Constants.WIDTH, Constants.HEIGHT);
//			
//			ablock = new BlockState(
//					new Rect( Constants.ORIGIN, new Point(Constants.BLOCK_WIDTH,Constants.BLOCK_HEIGHT)) );
//			someblocks = new BlockState[] { ablock };
//			
//			apad = new PaddleState(
//					new Point( Constants.WIDTH / 2, (3 * Constants.HEIGHT) / 4),
//					Constants.TYPICAL_PADDLE_COLORS());
//			
//			aball =
//					new Ball(
//						new Circle(
//							new Point(BR.getX() / 2 , Constants.HEIGHT / 2)
//							, Constants.INIT_BALL_DIAMETER)
//						, Constants.INIT_BALL_VELOCITY);
//			someballs = new Ball[] { aball };
//		}
//		
//		
//		//ABOUT Ball		
//
//		@Test //1
//		void ballDefensiveConstructorCircle() {
//			assertThrows( IllegalArgumentException.class,
//					() -> new Ball(null, new Vector(0,0)));
//		}
//		
//		
//		@Test //2
//		void ballDefensiveConstructorVector() {
//			assertThrows( IllegalArgumentException.class,
//					() -> new Ball(new Circle(Constants.ORIGIN, Constants.INIT_BALL_DIAMETER), null));
//		}
//		
//		@Test //3
//		/**
//		 * The constructor, getLocation() and setLocation() methods do not copy their circle argument.
//		 * This is expected since Circle is immutable. 
//		 */
//		void ballNoCopyOfImmutableFieldCircle() {
//			Circle circ = new Circle(Constants.ORIGIN, Constants.INIT_BALL_DIAMETER);
//			Ball ball = new Ball(circ, new Vector(0,0));
//			assertTrue ( ball.getLocation() == circ);
//			ball.setLocation( circ );
//			assertTrue ( ball.getLocation() == circ);
//			//reference test. this should hold according to assignment
//			// "If a class has a field of immutable type..."
//		}
//		
//		@Test //4
//		/**
//		 * The Ball constructor, getVelocity() and setVelocity() methods do not copy their vector argument.
//		 * This is expected since Vector is immutable. 
//		 */
//		void ballNoCopyOfImmutableFieldVector() {
//			
//			Vector vec = new Vector( Constants.INIT_BALL_VELOCITY.getX() , Constants.INIT_BALL_VELOCITY.getY() );
//			Ball ball = new Ball( new Circle(Constants.ORIGIN, Constants.INIT_BALL_DIAMETER),
//								  vec) ;
//			assertTrue ( ball.getVelocity() == vec );
//			ball.setVelocity( vec );
//			assertTrue ( ball.getVelocity() == vec);
//
//		}
//		
//		
//		
//		//ABOUT PaddleState
//
//		@Test //5
//		/**
//		 * The possibleColors field is correctly encaspulated (IN):
//		 * modifying an array that has been passed to PaddleState constructor does nothing.
//		 */
//		void paddlePossibleColorsEncapsIn() {
//			Color[] colors = new Color[] {
//					Color.green, Color.magenta, Color.orange
//			};
//			PaddleState pad = new PaddleState(new Point(1000,1000) , colors);
//			
//			colors[0] = Color.red;
//			assertEquals(Color.green, pad.getPossibleColors()[0]);
//		}
//		
//		@Test //6
//		/**
//		 * The possibleColors field is correctly encapsulated (OUT):
//		 * the possibleColors getter does the required copy.
//		 */
//		void paddlePossibleColorsEncapsOut() {
//			PaddleState pad = new PaddleState( new Point(1000, 1000) , Constants.TYPICAL_PADDLE_COLORS() );
//			Color[] colors = pad.getPossibleColors();
//			colors[0] = null;
//			assertNotSame( null , pad.getPossibleColors()[0]);
//		}
//		
//		//BreakoutState
//		
//		@Test //7
//		/**
//		 * The BreakoutState constructor makes a deep copy of the ball array.
//		 */
//		void breakoutStateBallsEncapsIn() {
//
//			
//			BreakoutState bstate = new BreakoutState( someballs, someblocks, BR, apad);
//			aball.setVelocity( new Vector(0,0));
//			
//			assertEquals( bstate.getBalls()[0].getVelocity() , Constants.INIT_BALL_VELOCITY );
//		}
//		
//		@Test //8
//		/**
//		 * The BreakoutState balls getter makes a deep copy of the array
//		 */
//		void breakoutStateBallsEncapsOut() {
//			BreakoutState bstate = new BreakoutState( someballs, someblocks, BR, apad);
//			Ball leak = bstate.getBalls()[0];
//			leak.setVelocity( new Vector(0,0) );
//			assertEquals( bstate.getBalls()[0].getVelocity() , Constants.INIT_BALL_VELOCITY);
//			
//		}
//		
//		@Test //9
//		/**
//		 * The BreakoutState constructor makes a copy of the blocks array
//		 */
//		void breakoutStateBlocksEncapsIn() {
//			BreakoutState bstate = new BreakoutState( someballs, someblocks, BR, apad);
//			someblocks[0] = new BlockState(new Rect(Constants.ORIGIN,new Point(2 * Constants.WIDTH, Constants.HEIGHT) ) );
//			
//			BlockState block = bstate.getBlocks()[0];
//			
//			assertEquals( ablock.getLocation().getWidth() , block.getLocation().getWidth() );
//			
//		}
//		
//		@Test //10
//		/**
//		 * The BreakoutState blocks getter does a copy
//		 */
//		void breakoutStateBlocksEncapsOut() {
//			BreakoutState bstate = new BreakoutState( someballs, someblocks, BR, apad);
//			BlockState[] leak = bstate.getBlocks();
//			leak[0] = new BlockState(new Rect(Constants.ORIGIN,new Point(2 * Constants.WIDTH, Constants.HEIGHT) ) );
//			
//			assertEquals( ablock.getLocation().getWidth() , bstate.getBlocks()[0].getLocation().getWidth() );
//		}
//		
//		
//		@Test //11
//		/**
//		 * The BreakoutState constructor is defensive against a ball array with a null Ball
//		 */
//		void breakoutStateDefensiveNullBall() {
//			assertThrows( IllegalArgumentException.class,
//					() -> new BreakoutState( new Ball[] {null}, someblocks, BR, apad) );
//		}
//		
//		@Test //12
//		/**
//		 * The BreakoutSTate constructor is defensive against a block array with a null block
//		 */
//		void breakoutStateDefensiveNullBlock() {
//			assertThrows( IllegalArgumentException.class , 
//					() -> new BreakoutState( someballs , new BlockState[] {null}, BR, apad));
//		}
//		
//		@Test //13
//		/**
//		 * We randomly pick a paddle color *within* paddle.getPossibleColors()
//		 */
//		void breakoutStateTossColorInPaddle() {
//			BreakoutState bstate = new BreakoutState( someballs , someblocks, BR, apad);
//			bstate.tossPaddleColor();
//			assertTrue( Arrays.stream(bstate.getPaddle().getPossibleColors()).anyMatch(c -> c .equals(bstate.getCurPaddleColor())) );
//		}
//		
//		@Test //14
//		/**
//		 * When the ball hits a wall it is not unexpectedly teleported at the center of the field.
//		 */
//		void breakoutStateNoTeleportBall() {
//			//a ball that is going to bounce on the right wall.
//			Point nearRightWall = new Point(Constants.WIDTH - (Constants.INIT_BALL_DIAMETER / 2) - 100 , Constants.HEIGHT / 2);
//			Vector rightVel = new Vector(15,1);
//			Ball myball = new Ball( new Circle(nearRightWall, Constants.INIT_BALL_DIAMETER) , rightVel);
//			
//			BreakoutState bstate = new BreakoutState( new Ball[] { myball }, someblocks, BR, apad);
//			
//			bstate.tickDuring(200);
//			
//			assertNotEquals( new Vector(15,1) , bstate.getBalls()[0].getVelocity() ); //has bounced
//			assertTrue( bstate.getBalls()[0].getCenter().getX() > (Constants.WIDTH * 3) / 4 ); //has remained near right wall
//		}
//		
//		@Test //15
//		/**
//		 * When the ball hits a block the paddle is not unexpectedly attracted towards the center
//		 */
//		void breakoutStateNoPaddleFlick() {
//			//a ball about to hit a block located at the top left corner, from below
//			Point nearTopLeftBlock = new Point(Constants.BLOCK_WIDTH / 2, Constants.BLOCK_HEIGHT + 300);
//			Vector upVelocity = new Vector(0, -10);
//			Ball myball = new Ball( new Circle(nearTopLeftBlock, Constants.INIT_BALL_DIAMETER) , upVelocity );
//			
//			//the paddle is to the right of the center of the game field
//			PaddleState mypad = new PaddleState( new Point(Constants.WIDTH * 3 / 4, Constants.HEIGHT * 3 / 4) ,Constants.TYPICAL_PADDLE_COLORS());
//			Point initPadLocation = mypad.getCenter();
//			
//			BreakoutState bstate = new BreakoutState( new Ball[] {myball} , someblocks, BR, mypad);
//			bstate.tickDuring(200);
//			
//			assertEquals( initPadLocation, bstate.getPaddle().getCenter());
//		}
//		
//		@Test //16
//		/**
//		 * Moving the paddle to the right during x ms, and then to the left during x ms is the same than doing nothing.
//		 */
//		void breakoutStateMovePaddleLeftRight() {
//			BreakoutState bstate = new BreakoutState(someballs, someblocks, BR, apad);
//			Point initPadLocation = apad.getCenter();
//			
//			bstate.movePaddleLeft(200);
//			bstate.movePaddleRight(200);
//			
//			assertEquals( initPadLocation, bstate.getPaddle().getCenter());
//		}
//		
//	}
//	
//	@Disabled
//	@Nested
//	/**
//	 * Those tests check that a specific class invariant or a precondition on the constructor
//	 * is present
//	 */
//	class OldMissingSpecTests {
//		
//		@Test
//		/**
//		 * There is a "every possible color is non-null" private or public invariant,
//		 * or a precondition in the PaddleState constructor
//		 */
//		void paddleNonNullColors() {
//			Color[] colors = new Color[] {
//					Color.green, Color.magenta, null
//			};
//			try {
//				new PaddleState( new Point(1000, 1000) , colors);
//				throw new Exception();
//			} catch (AssertionError e) {
//				assert true;
//			} catch (Exception e) {
//				fail("Expected an assertion error caused by a null color");
//			}
//		}
//		
//		  @Test
//		  /**
//		   * Ball.move has a `!= null` precondition (or some of the method it  uses)
//		   */
//		  void ballMovePre() {
//			  try {
//				  Ball ball = new Ball(new Circle(Constants.ORIGIN, Constants.INIT_BALL_DIAMETER) , Constants.INIT_BALL_VELOCITY);
//				  ball.move(null);
//				  throw new Exception();
//			  } catch (AssertionError e) {
//				  assert true;
//			  } catch (Exception e) {
//				  fail("Assertion error was expected because of null argument");
//			  }
//
//		  }
//		  
//		
//	}
//	
//	@Disabled
//	@Nested
//	/**
//	 * correction tests for task A
//	 */
//	class OldTaskA {
//		
//		//fields for testing BreakoutState
//		private Point BR;
//		private BlockState ablock;
//		private BlockState[] someblocks;
//		private Ball aball;
//		private Ball[] someballs;
//		private PaddleState apad;
//
//		@BeforeAll
//		static void setUpBeforeClass() throws Exception {
//		}
//
//		@BeforeEach
//		/**
//		 * the BreakoutState tests below depend on the values given here.
//		 */
//		void setUp() {
//			BR = new Point(Constants.WIDTH, Constants.HEIGHT);
//			
//			ablock = new BlockState(
//					new Rect( Constants.ORIGIN, new Point(Constants.BLOCK_WIDTH,Constants.BLOCK_HEIGHT)) );
//			someblocks = new BlockState[] { ablock };
//			
//			apad = new PaddleState(
//					new Point( Constants.WIDTH / 2, (3 * Constants.HEIGHT) / 4),
//					Constants.TYPICAL_PADDLE_COLORS());
//			
//			aball =
//					new Ball(
//						new Circle(
//							new Point(BR.getX() / 2 , Constants.HEIGHT / 2)
//							, Constants.INIT_BALL_DIAMETER)
//						, Constants.INIT_BALL_VELOCITY);
//			someballs = new Ball[] { aball };
//		}
//		
//		@Test
//		/**
//		 * BlockState constructor has != null pre.
//		 */
//		void blockStateCstrPre() {
//			try {
//				new BlockState( null );
//				throw new Exception();
//			} catch (AssertionError e) {
//				assert true; //theres a fsc4j precondition as expected
//			} catch (Exception e) {
//				fail("Expected fsc4j arg != null precondition in BlockState constructor");
//			}
//		}
//		
//		@Test
//		/**
//		 * Vector.plus has != null pre
//		 */
//		void vectorPlusPre() {
//			try {
//				Vector avec = new Vector(100, 200);
//				avec.plus(null);
//				throw new Exception();
//			} catch (AssertionError e) {
//				assert true; //theres a fsc4j precond as expected
//			} catch (Exception e) {
//				fail("Expected fsc4j arg != null precond in Vector.plus");
//			}
//		}
//		
//		@Test
//		/**
//		 * An out of field ball is removed 
//		 */
//		void removeDeadBallTest() {
//			Circle newcirc = new Circle( new Point(Constants.WIDTH / 2 , Constants.HEIGHT - 700), Constants.INIT_BALL_DIAMETER);
//			// the above is almost out of field
//			aball.setLocation(newcirc);
//			aball.setVelocity(new Vector(0,20));
//			BreakoutState bstate = new BreakoutState (someballs, someblocks, BR, apad );
//			assertEquals(1, bstate.getBalls().length);
//			bstate.tickDuring(1000);
//			assertEquals(0, bstate.getBalls().length);
//		}
//	}



