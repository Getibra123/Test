package breakout;

import breakout.utils.Point;
import breakout.utils.Rect;
import breakout.utils.Vector;

import java.awt.Color;
import java.util.Arrays;
import java.util.Random;

/**
 * Represents the state of a paddle in the breakout game.
 * @invar | getCenter() != null
 * @invar | getPossibleColors() != null
 * @invar | getPossibleColors() .length == 3
 * @invar | Arrays.stream(getPossibleColors()).allMatch(c -> c != null)
 * @invar | getCurColor() != null
 * @invar | Arrays.stream( getPossibleColors() ).anyMatch( c -> c .equals( getCurColor() ) )
 * @invar | ! (getPossibleColors()[0] .equals( getPossibleColors()[1]))
 * @invar | ! (getPossibleColors()[0] .equals( getPossibleColors()[2]))
 * @invar | ! (getPossibleColors()[1] .equals( getPossibleColors()[2]))
 */
public abstract class PaddleState {
	

	/**
	 * @invar | center != null
	 */
	private Point center;
	/**
	 * @invar | possibleColors != null
	 * @invar | possibleColors.length == 3
	 * @invar | Arrays.stream(possibleColors).allMatch(c -> c != null)
	 * @invar | !(possibleColors[0] .equals( possibleColors[1] ))
	 * @invar | !(possibleColors[1] .equals( possibleColors[2] ))
	 * @invar | !(possibleColors[1] .equals( possibleColors[2] ))
	 * @representationObject
	 */
	private final Color[] possibleColors;
	
	/**
	 * @invar | Arrays.stream(possibleColors).anyMatch( c -> c .equals(curColor))
	 */
	private Color curColor;

	/**
	 * Construct a paddle located around a given center in the field.
	 * @pre | center != null
	 * @pre | possibleColors != null
	 * @pre | possibleColors.length == 3
	 * @pre | Arrays.stream(possibleColors).allMatch(c -> c != null)
	 * @pre | ! (possibleColors[0] .equals( possibleColors[1]))
	 * @pre | ! (possibleColors[0] .equals( possibleColors[2]))
	 * @pre | ! (possibleColors[1] .equals( possibleColors[2]))    
	 * @post | getCenter().equals(center)
	 * @post | Arrays.equals(possibleColors , getPossibleColors())
	 */
	public PaddleState(Point center, Color[] possibleColors, Color curColor) {
		this.center = center;
		this.possibleColors = possibleColors.clone(); //we consider Color as immutable
		this.curColor = curColor;
	}
	
	/**
	 * Return the center point of this paddle.
	 */
	public Point getCenter() {
		return center;
	}
	
	/**
	 * @creates | result
	 */
	public Color[] getPossibleColors() {
		return possibleColors.clone();
	}
	
	public Color getCurColor() {
		return curColor;
	}
	
	/**
	 * To paint the array, only use those colors.
	 * 
	 * @post | result != null
	 * @post | result.length == 1 || result.length == 3
	 * @post | Arrays.stream( result ).anyMatch( c -> c .equals( getCurColor() ) )
	 * @creates | result 
	 */
	public abstract Color[] getActualColors();

	/**
	 * Return the rectangle occupied by this paddle in the field.
	 * 
	 * @post | result != null
	 * @post | result.getTopLeft().equals(getCenter().plus(new Vector(-Constants.PADDLE_WIDTH/2,-Constants.PADDLE_HEIGHT/2)))
	 * @post | result.getBottomRight().equals(getCenter().plus(new Vector(Constants.PADDLE_WIDTH/2,Constants.PADDLE_HEIGHT/2)))
	 */
	public Rect getLocation() {
		Vector halfDiag = new Vector(-Constants.PADDLE_WIDTH/2,-Constants.PADDLE_HEIGHT/2);
		return new Rect(center.plus(halfDiag), center.plus(halfDiag.scaled(-1)));
	}
	
	/**
	 * Returns the number of generated balls + 1 (the ball that is bouncing on the paddle).
	 * 
	 * @post | result > 0
	 * @post | result <=  Constants.MAX_BALL_REPLICAS + 1
	 */
	public abstract int numberOfBallsAfterHit();

	/**
	 * Return the new state of the paddle after it is hit by a ball.
	 * 
	 * @post | result != null
	 * @post | result.getLocation().equals(getLocation())
	 */
	public abstract PaddleState stateAfterHit();

	/**
	 * @mutates | this
	 */
	public void setCenter(Point c) {
		center = c;
	}
	
	/**
	 * Moves this PaddleState in a given direction, within a given rectangle.
	 * 
	 * @pre | v != null
	 * @pre | field != null
	 * @mutates | this
	 * @post | field.contains(getLocation())
	 */
	public void move(Vector v, Rect field) {
		Point ncenter = getCenter().plus(v);
		setCenter( field.minusMargin(Constants.PADDLE_WIDTH / 2, 0).constrain(ncenter) );
	}
	
	/**
	 * Randomly picks a color in getPossibleColors() and makes it the current color (retrieved with getCurColor()).
	 * The resulting getCurColor() should still be in getPossibleColors() (see public invar) 
	 */
	public void tossCurColor() {
		Random rand = new Random();
		int randInt = rand.nextInt( possibleColors.length );
		curColor = possibleColors[randInt];
	}
	
	/**
	 * @creates | result
	 * 
	 * a copy of the paddle at hand.
	 */
	public abstract PaddleState reproduce();
	
	public abstract boolean equalContent(PaddleState other);

}
