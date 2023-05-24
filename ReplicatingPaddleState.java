package breakout;

import java.awt.Color;
import java.util.Arrays;

import breakout.utils.*;

public class ReplicatingPaddleState extends PaddleState {
	
	/**
	 * count = the number of balls that will be generated upon hitting this paddle + 1.
	 */
	private int count;

	@Override
	public int numberOfBallsAfterHit() {
		return count;
	}
	
	/**
	 * Returns the remaining amount of ball replications this paddle will perform + 1
	 */
	public int getCount() {
		return count;
	}

	public ReplicatingPaddleState(Point center, Color[] possCols, Color curColor, int count) {
		super(center, possCols, curColor);
		this.count = count;
	}

	@Override
	public PaddleState stateAfterHit() {
		if (count > 2) {
			count -= 1;
			return this;
		} else {
			PaddleState res = new NormalPaddleState(getCenter(), getPossibleColors(), getCurColor());
			res.tossCurColor();
			return res;
		}
	}
	
	@Override
	public Color[] getActualColors() {
		return getPossibleColors();
	}
	
	@Override
	public PaddleState reproduce() {
		return new ReplicatingPaddleState( getCenter() , getPossibleColors() , getCurColor() , count);
	}
	
	@Override
	public boolean equalContent(PaddleState other) {
		if (getClass() != other.getClass()) { return false; }
		ReplicatingPaddleState oth = (ReplicatingPaddleState) other;
		if (!getCenter() .equals( oth.getCenter() )) { return false; }
		if ( ! Arrays.equals(getPossibleColors(), oth.getPossibleColors())) { return false; }
		if ( ! getCurColor() .equals( oth.getCurColor() )) { return false; }
		if ( count != oth.getCount() ) { return false; }
		return true;
		
	}

}
