package connectFour;

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

public class ConnectFourWidget extends JPanel
		implements SpotListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* Enum to identify player. */
	private enum Player {
		RED, BLACK
	};

	private JSpotBoard _board; /* SpotBoard playing area. */
	private JLabel _message; /* Label for messages. */
	private boolean _game_won; /* Indicates if game has been won already. */
	private Player _next_to_play; /* Identifies who has next turn. */
	private int _move_count; /* How many moves have happened. */
	private boolean _game_drawn; /* Indicates if game has been drawn already. */
	private List<Spot> winningSpots; /* Holds the winning Spots or is empty. */

	public ConnectFourWidget() {

		/* Create SpotBoard and message label. */

		_board = new JSpotBoard(7, 6);
		Color light = new Color(0.8f, 0.8f, 0.8f);
		Color dark = new Color(0.5f, 0.5f, 0.5f);
		for (int i = 0; i < _board.getSpotWidth(); i++) {
			Color currentColor = (i % 2 == 0) ? light : dark;
			for (int j = 0; j < _board.getSpotHeight(); j++) {
				_board.getSpotAt(i, j).setBackground(currentColor);
			}
		}
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
		 * Clear and unhighlight all spots on board. Uses the fact that
		 * SpotBoard implements Iterable<Spot> to do this in a for-each loop.
		 */

		for (Spot s : _board) {
			s.clearSpot();
			s.unhighlightSpot();
		}

		/* Reset game won and game drawn and next to play fields */
		_game_won = false;
		_game_drawn = false;
		_next_to_play = Player.RED;
		_move_count = 0;
		winningSpots = null;

		/* Display game start message. */

		_message.setText("Welcome to Connect Four. Red to play.");
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
		 * If game already won or drawn or there are no empty Spots in the
		 * column, do nothing.
		 */
		if (_game_won || _game_drawn
				|| !_board.getSpotAt(s.getSpotX(), 0).isEmpty()) {
			return;
		}

		/*
		 * Set up player and next player name strings, and player color as local
		 * variables to be used later.
		 */

		String player_name = null;
		String next_player_name = null;
		Color player_color = null;

		if (_next_to_play == Player.RED) {
			player_color = Color.RED;
			player_name = "Red";
			next_player_name = "Black";
			_next_to_play = Player.BLACK;
		} else {
			player_color = Color.BLACK;
			player_name = "Black";
			next_player_name = "Red";
			_next_to_play = Player.RED;
		}

		/*
		 * Set color of first empty spot in the column of the Spot clicked and
		 * toggle.
		 */
		for (int i = _board.getSpotHeight() - 1; i >= 0; i--) {
			Spot curSpot = _board.getSpotAt(s.getSpotX(), i);
			if (curSpot.isEmpty()) {
				curSpot.setSpotColor(player_color);
				curSpot.toggleSpot();
				s = curSpot;
				break;
			}
		}

		/*
		 * Check if spot clicked caused a win or draw. If so, mark game as won
		 * or drawn.
		 */

		// Increment the move count.
		_move_count++;

		winningSpots = checkWinDraw(s, player_color);

		/*
		 * Update the message depending on what happened.
		 */

		if (_game_won) {
			int x = s.getSpotX();
			for (int i = 0; i < _board.getSpotHeight(); i++) {
				_board.getSpotAt(x, i).unhighlightSpot();
			}
			for (Spot spot : winningSpots) {
				spot.highlightSpot();
			}
			_message.setText(player_name + " wins!");
		} else if (_game_drawn) {
			int x = s.getSpotX();
			for (int i = 0; i < _board.getSpotHeight(); i++) {
				_board.getSpotAt(x, i).unhighlightSpot();
			}
			_message.setText("Draw game.");
		} else {
			_message.setText(next_player_name + " to play");
		}
	}

	/*
	 * Checks whether there is a win or a draw and returns an ArrayList
	 * containing the winning pieces or null if no win is found.
	 */
	private List<Spot> checkWinDraw(Spot s, Color player_color) {
		// Get coordinates of the spot.
		int xPos = s.getSpotX();
		int yPos = s.getSpotY();

		int height = _board.getSpotHeight(); /* board height = 6. */
		int width = _board.getSpotWidth(); /* board width = 7. */

		// Holds the winning spots.
		List<Spot> winSpots = new ArrayList<Spot>(4);

		// Check column for win.
		for (int i = 0; i < height; i++) {
			Spot curSpot = _board.getSpotAt(xPos, i);
			if (curSpot.isEmpty()
					|| !curSpot.getSpotColor().equals(player_color)) {
				winSpots = new ArrayList<Spot>(4);
			} else {
				winSpots.add(curSpot);
				if (winSpots.size() == 4) {
					_game_won = true;
					return winSpots;
				}
			}
		}

		// Check row for win.
		winSpots = new ArrayList<Spot>(4);
		for (int i = 0; i < width; i++) {
			Spot curSpot = _board.getSpotAt(i, yPos);
			if (curSpot.isEmpty()
					|| !curSpot.getSpotColor().equals(player_color)) {
				winSpots = new ArrayList<Spot>(4);
			} else {
				winSpots.add(curSpot);
				if (winSpots.size() == 4) {
					_game_won = true;
					return winSpots;
				}
			}
		}

		// Check diagonals for win.
		winSpots = new ArrayList<Spot>(4);
		int intercept = yPos - xPos;
		if (intercept >= -3 && intercept <= 2) {
			int startX = Math.max(-intercept, 0);
			int endX = Math.min(height - 1 - intercept, width - 1);
			for (int i = startX; i <= endX; i++) {
				Spot curSpot = _board.getSpotAt(i, intercept + i);
				if (curSpot.isEmpty()
						|| !curSpot.getSpotColor().equals(player_color)) {
					winSpots = new ArrayList<Spot>(4);
				} else {
					winSpots.add(curSpot);
					if (winSpots.size() == 4) {
						_game_won = true;
						return winSpots;
					}
				}
			}
		}

		// Check anti-diagonals for win.
		winSpots = new ArrayList<Spot>(4);
		intercept = yPos + xPos;
		if (intercept >= 3 && intercept <= 8) {
			int startX = Math.max(0, intercept - height + 1);
			int endX = Math.min(intercept, width - 1);
			for (int i = startX; i <= endX; i++) {
				Spot curSpot = _board.getSpotAt(i, intercept - i);
				if (curSpot.isEmpty()
						|| !curSpot.getSpotColor().equals(player_color)) {
					winSpots = new ArrayList<Spot>(4);
				} else {
					winSpots.add(curSpot);
					if (winSpots.size() == 4) {
						_game_won = true;
						return winSpots;
					}
				}
			}
		}

		// Check for draw.
		_game_drawn = (_move_count == height * width);

		return null;
	}

	@Override
	public void spotEntered(Spot s) {
		/* Highlight all empty spots in column if game still going on. */

		if (_game_won || _game_drawn) {
			return;
		}

		int x = s.getSpotX();
		for (int i = 0; i < _board.getSpotHeight(); i++) {
			if (_board.getSpotAt(x, i).isEmpty()) {
				_board.getSpotAt(x, i).highlightSpot();
			}
		}
	}

	@Override
	public void spotExited(Spot s) {
		/* Unhighlight spots in the column. */

		if (_game_won || _game_drawn) {
			return;
		}

		int x = s.getSpotX();
		for (int i = 0; i < _board.getSpotHeight(); i++) {
			if (winningSpots == null || !winningSpots.contains(s)) {
				_board.getSpotAt(x, i).unhighlightSpot();
			}
		}
	}

}
