package breakout;

import java.awt.Color;

import breakout.radioactivity.Ball;
import breakout.utils.Rect;

/**
 * @immutable
 *
 */
public class NormalBlockState extends BlockState {

	/**
	 * Construct a block occupying a given rectangle in the field.
	 * | @pre | location != null
	 * | @post | getLocation().equals(location)
	 */
	public NormalBlockState(Rect location) {
		super(location);
	}

	@Override
	public BlockState blockStateAfterHit(int squaredSpeed) {
		return null;
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
		return Constants.NORMAL_BLOCK_COLOR;
	}

}
