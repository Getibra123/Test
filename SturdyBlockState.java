package breakout;

import java.awt.Color;

import breakout.radioactivity.Ball;
import breakout.utils.Rect;

/**
 * @immutable
 *
 */
public class SturdyBlockState extends BlockState {

	private final int livesLeft;

	public SturdyBlockState(Rect location, int lives) {
		super(location);
		livesLeft = lives;
	}

	public int getLivesLeft() {
		return livesLeft;
	}
	
	@Override
	public BlockState blockStateAfterHit(int squaredSpeed) {
		if (livesLeft == 1) {
			//only fast balls may destroy the final life of a sturdy block.
			if (squaredSpeed >= Constants.BALL_SPEED_THRESH * Constants.BALL_SPEED_THRESH) {return null; }
			else return this;
		} else {
			return new SturdyBlockState(getLocation(), livesLeft - 1);
		}
	}

	@Override
	public Ball ballStateAfterHit(Ball ballState) {
		return ballState;
	}

	@Override
	public PaddleState paddleStateAfterHit(PaddleState paddleState) {
		return paddleState;
	}

	@Override
	public Color getColor() {
		switch (livesLeft) {
		case 1:
			return Constants.STURDY_COLOR1;
		case 2:
			return Constants.STURDY_COLOR2;
		default:
			return Constants.STURDY_COLOR3;
		}
	}

}
