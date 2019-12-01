package othello;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import spotHelperFiles.*;

public class OthelloWidget extends JPanel
		implements ActionListener, SpotListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* Enum to identify player. */
	private enum Player {
		BLACK, WHITE
	};

	private JSpotBoard _board; /* SpotBoard playing area. */
	private JLabel _message; /* Label for messages. */
	private boolean _game_ended; /* Indicates if game has been won already. */
	private Player _next_to_play; /* Identifies who has next turn. */

	public OthelloWidget() {

		/* Create SpotBoard and message label. */

		_board = new JSpotBoard(8,
				8); /* Default coloration is chessboard pattern. */
		_message = new JLabel();

		/* Set layout and place SpotBoard at center. */

		setLayout(new BorderLayout());
		add(_board, BorderLayout.CENTER);

		/* Create subpanel for message area and reset button. */

		JPanel reset_message_panel = new JPanel();
		reset_message_panel.setLayout(new BorderLayout());

		/* Reset button. Add ourselves as the action listener. */

		JButton reset_button = new JButton("Restart");
		reset_button.addActionListener(this);
		reset_message_panel.add(reset_button, BorderLayout.EAST);
		reset_message_panel.add(_message, BorderLayout.CENTER);

		/* Add subpanel in south area of layout. */

		add(reset_message_panel, BorderLayout.SOUTH);

		/*
		 * Add ourselves as a spot listener for all of the spots on the spot
		 * board.
		 */
		_board.addSpotListener(this);

		/* Reset game. */
		resetGame();
	}

	/*
	 * resetGame
	 * 
	 * Resets the game by clearing all the spots on the board, resetting game
	 * status fields, and displaying start message.
	 */
	private void resetGame() {
		/*
		 * Clear all spots on the board. Uses the fact that SpotBoard implements
		 * Iterable<Spot> to do this in a for-each loop. Set the four center
		 * spots to the appropriate colors.
		 */

		for (Spot s : _board) {
			s.clearSpot();
		}
		for (int x = 3; x <= 4; x++) {
			for (int y = 3; y <= 4; y++) {
				Color spotColor = ((x + y) % 2 == 0) ? Color.WHITE
						: Color.BLACK;
				_board.getSpotAt(x, y).setSpotColor(spotColor);
				_board.getSpotAt(x, y).setSpot();
			}
		}

		/* Reset game ended and next to play fields */
		_game_ended = false;
		_next_to_play = Player.BLACK;

		/* Display game start message. */

		_message.setText("Welcome to Othello. Black to play.");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		/* Handles reset game button. Simply reset the game. */
		resetGame();
	}

	/*
	 * Implementation of SpotListener below. Implements game logic as responses
	 * to enter/exit/click on spots.
	 */

	@Override
	public void spotClicked(Spot s) {

		/*
		 * If game already ended do nothing.
		 */
		if (_game_ended ) {
			return;
		}

		Color player_color = (_next_to_play == Player.BLACK) ? Color.BLACK : Color.WHITE;
		// Do nothing if move is not valid for this spot.
		if (!isMoveValid(s, player_color)) {
			return;
		}
		
		/*
		 * Set up player and next player name strings, and player color as local
		 * variables to be used later.
		 */

		String next_player_name = null;
		Color next_player_color = null;

		if (_next_to_play == Player.BLACK) {
			next_player_name = "White";
			_next_to_play = Player.WHITE;
			next_player_color = Color.WHITE;

		} else {
			next_player_name = "Black";
			_next_to_play = Player.BLACK;
			next_player_color = Color.BLACK;
		}

		// Move is valid so make the move.
		makeMove(s, player_color);

		/*
		 * Check if spot clicked caused the game to end.
		 */

		if (hasMove(next_player_color)) {
			_message.setText(next_player_name + " to play");
		} else if (hasMove(player_color)) {
			_next_to_play = (_next_to_play == Player.WHITE) ? Player.BLACK
					: Player.WHITE;
			String playerAgain = (_next_to_play == Player.WHITE) ? "White" : "Black";
			_message.setText(playerAgain + " to play again");
		} else {
			_game_ended = true;
			int blackScore = 0;
			int whiteScore = 0;
			for (Spot spot : _board) {
				if (!spot.isEmpty()) {
					if (spot.getSpotColor().equals(Color.BLACK)) {
						blackScore++;
					} else {
						whiteScore++;
					}
				}
			}
			if (blackScore > whiteScore) {
				_message.setText("Game over. Black wins. Score: " + whiteScore
						+ " to " + blackScore);
			} else if (blackScore < whiteScore) {
				_message.setText("Game over. White wins. Score: " + whiteScore
						+ " to " + blackScore);
			} else {
				_message.setText("Game over. Tie game. Score: " + whiteScore
						+ " to " + blackScore);
			}
		}
	}

	/*
	 * Checks whether a player can move in a certain Spot.
	 */
	private boolean isMoveValid(Spot s, Color player_color) {
		if (!s.isEmpty()) {
			return false;
		}
		return !flankedSpots(s, player_color).isEmpty();
	}

	private boolean hasMove(Color player_color) {
		for (Spot s : _board) {
			if (isMoveValid(s, player_color)) {
				return true;
			}
		}
		return false;
	}

	private void makeMove(Spot s, Color player_color) {
		// Flip all spots that get sandwiched.
		List<Spot> spotsToChange = flankedSpots(s, player_color);
		s.setSpotColor(player_color);
		s.setSpot();
		for (Spot spot : spotsToChange) {
			spot.setSpotColor(player_color);
		}
	}

	/*
	 * returns a List with the Spots that would be flanked if a Spot was placed.
	 */
	private List<Spot> flankedSpots(Spot s, Color player_color) {
		// Store the spots that will flip colors.
		List<Spot> spotsToChange = new ArrayList<Spot>();

		// Get coordinates of the spot.
		int xPos = s.getSpotX();
		int yPos = s.getSpotY();

		int height = _board.getSpotHeight(); /* board height = 8. */
		int width = _board.getSpotWidth(); /* board width = 8. */

		// Check down.
		List<Spot> downSpots = new ArrayList<Spot>();
		for (int y = yPos + 1; y <= height - 1; y++) {
			Spot curSpot = _board.getSpotAt(xPos, y);
			if (curSpot.isEmpty()) {
				break;
			}
			if (!curSpot.getSpotColor().equals(player_color)) {
				downSpots.add(curSpot);
			} else {
				if (downSpots.size() > 0) {
					spotsToChange.addAll(downSpots);
				}
				break;
			}
		}

		// Check up.
		List<Spot> upSpots = new ArrayList<Spot>();
		for (int y = yPos - 1; y >= 0; y--) {
			Spot curSpot = _board.getSpotAt(xPos, y);
			if (curSpot.isEmpty()) {
				break;
			}
			if (!curSpot.getSpotColor().equals(player_color)) {
				upSpots.add(curSpot);
			} else {
				if (upSpots.size() > 0) {
					spotsToChange.addAll(upSpots);
				}
				break;
			}
		}

		// Check right.
		List<Spot> rightSpots = new ArrayList<Spot>();
		for (int x = xPos + 1; x <= width - 1; x++) {
			Spot curSpot = _board.getSpotAt(x, yPos);
			if (curSpot.isEmpty()) {
				break;
			}
			if (!curSpot.getSpotColor().equals(player_color)) {
				rightSpots.add(curSpot);
			} else {
				if (rightSpots.size() > 0) {
					spotsToChange.addAll(rightSpots);
				}
				break;
			}
		}

		// Check left.
		List<Spot> leftSpots = new ArrayList<Spot>();
		for (int x = xPos - 1; x >= 0; x--) {
			Spot curSpot = _board.getSpotAt(x, yPos);
			if (curSpot.isEmpty()) {
				break;
			}
			if (!curSpot.getSpotColor().equals(player_color)) {
				leftSpots.add(curSpot);
			} else {
				if (leftSpots.size() > 0) {
					spotsToChange.addAll(leftSpots);
				}
				break;
			}
		}

		// Check down right diagonal.
		List<Spot> downRightSpots = new ArrayList<Spot>();
		int xEnd = Math.min(width - 1, xPos + (height - 1) - yPos);
		for (int x = xPos + 1; x <= xEnd; x++) {
			int y = yPos + (x - xPos);
			Spot curSpot = _board.getSpotAt(x, y);
			if (curSpot.isEmpty()) {
				break;
			}
			if (!curSpot.getSpotColor().equals(player_color)) {
				downRightSpots.add(curSpot);
			} else {
				if (downRightSpots.size() > 0) {
					spotsToChange.addAll(downRightSpots);
				}
				break;
			}
		}

		// Check down left diagonal.
		List<Spot> downLeftSpots = new ArrayList<Spot>();
		xEnd = Math.max(0, xPos + yPos - (height - 1));
		for (int x = xPos - 1; x >= xEnd; x--) {
			int y = yPos - (x - xPos);
			Spot curSpot = _board.getSpotAt(x, y);
			if (curSpot.isEmpty()) {
				break;
			}
			if (!curSpot.getSpotColor().equals(player_color)) {
				downLeftSpots.add(curSpot);
			} else {
				if (downLeftSpots.size() > 0) {
					spotsToChange.addAll(downLeftSpots);
				}
				break;
			}
		}

		// Check up right diagonal.
		List<Spot> upRightSpots = new ArrayList<Spot>();
		xEnd = Math.min(width - 1, xPos + yPos);
		for (int x = xPos + 1; x <= xEnd; x++) {
			int y = yPos - (x - xPos);
			Spot curSpot = _board.getSpotAt(x, y);
			if (curSpot.isEmpty()) {
				break;
			}
			if (!curSpot.getSpotColor().equals(player_color)) {
				upRightSpots.add(curSpot);
			} else {
				if (upRightSpots.size() > 0) {
					spotsToChange.addAll(upRightSpots);
				}
				break;
			}
		}

		// Check up left diagonal.
		List<Spot> upLeftSpots = new ArrayList<Spot>();
		xEnd = Math.max(0, xPos - yPos);
		for (int x = xPos - 1; x >= xEnd; x--) {
			int y = yPos + (x - xPos);
			Spot curSpot = _board.getSpotAt(x, y);
			if (curSpot.isEmpty()) {
				break;
			}
			if (!curSpot.getSpotColor().equals(player_color)) {
				upLeftSpots.add(curSpot);
			} else {
				if (upLeftSpots.size() > 0) {
					spotsToChange.addAll(upLeftSpots);
				}
				break;
			}
		}
		return spotsToChange;
	}

	@Override
	public void spotEntered(Spot s) {
		/* Highlight a Spot only if it is a valid move */

		if (_game_ended) {
			return;
		}

		Color player_color = (_next_to_play == Player.BLACK) ? Color.BLACK
				: Color.WHITE;
		if (isMoveValid(s, player_color)) {
			s.highlightSpot();
		}
	}

	@Override
	public void spotExited(Spot s) {
		if (s.isHighlighted()) {
			s.unhighlightSpot();
		}
	}
}
