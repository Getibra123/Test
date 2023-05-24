package breakout;

import java.awt.Color;

import breakout.utils.Rect;

/**
 * @immutable
 *
 */
public class ReplicatorBlockState extends NormalBlockState {

	public ReplicatorBlockState(Rect location) {
		super(location);
	}

	@Override
	public PaddleState paddleStateAfterHit(PaddleState paddleState) {
		return new ReplicatingPaddleState(paddleState.getCenter(), paddleState.getPossibleColors(), paddleState.getCurColor(), 4);
	}

	@Override
	public Color getColor() {
		return Constants.REPLICATOR_BLOCK_COLOR;
	}

}
