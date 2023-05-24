package breakout;

import java.awt.Color;

import breakout.utils.Rect;
import breakout.radioactivity.Alpha;
import breakout.radioactivity.Ball;
import breakout.radioactivity.SuperChargedBall;
import breakout.utils.Circle;

/**
 * @immutable
 *
 */
public class PowerupBallBlockState extends NormalBlockState {

	public PowerupBallBlockState(Rect location) {
		super(location);
	}

	@Override
	public Ball ballStateAfterHit(Ball b) {
		int superDiam = Constants.INIT_BALL_DIAMETER + 600 ;
		Circle superLoc = new Circle( b.getCenter(), superDiam);
		SuperChargedBall res = new SuperChargedBall(
				superLoc,
				b.getVelocity(),
				Constants.SUPERCHARGED_BALL_LIFETIME);
		//TODO
		return null;
	}

	@Override
	public Color getColor() {
		return Constants.POWERUP_BLOCK_COLOR;
	}

}
