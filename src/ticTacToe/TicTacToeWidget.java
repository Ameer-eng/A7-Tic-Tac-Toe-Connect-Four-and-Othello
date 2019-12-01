package ticTacToe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import spotHelperFiles.*;

public class TicTacToeWidget extends JPanel
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
	private boolean _game_won; /* Indicates if game has been won already. */
	private Player _next_to_play; /* Identifies who has next turn. */
	private int _move_count; /* How many moves have happened. */
	private boolean _game_drawn; /* Indicates if game has been drawn already. */

	public TicTacToeWidget() {

		/* Create SpotBoard and message label. */

		_board = new JSpotBoard(3, 3);
		Color defaultBackgroundLight = new Color(0.8f, 0.8f, 0.8f);
		for (Spot s : _board) {
			s.setBackground(defaultBackgroundLight);
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
		 * Clear all spots on board. Uses the fact that SpotBoard implements
		 * Iterable<Spot> to do this in a for-each loop.
		 */

		for (Spot s : _board) {
			s.clearSpot();
		}

		/* Reset game won and next to play fields */
		_game_won = false;
		_game_drawn = false;
		_next_to_play = Player.WHITE;
		_move_count = 0;

		/* Display game start message. */

		_message.setText("Welcome to Tic Tac Toe. White to play.");
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

		/* If game already won or drawn or Spot is not empty, do nothing. */
		if (_game_won || _game_drawn || !s.isEmpty()) {
			return;
		}

		/*
		 * Set up player and next player name strings, and player color as local
		 * variables to be used later.
		 */

		String player_name = null;
		String next_player_name = null;
		Color player_color = null;

		if (_next_to_play == Player.BLACK) {
			player_color = Color.BLACK;
			player_name = "Black";
			next_player_name = "White";
			_next_to_play = Player.WHITE;
		} else {
			player_color = Color.WHITE;
			player_name = "White";
			next_player_name = "Black";
			_next_to_play = Player.BLACK;
		}

		/* Set color of spot clicked and toggle. */
		s.setSpotColor(player_color);
		s.toggleSpot();

		/*
		 * Check if spot clicked caused a win or draw. If so, mark game as won
		 * or drawn.
		 */

		// Increment the move count.
		_move_count++;

		checkWinDraw(s, player_color);

		/*
		 * Update the message depending on what happened.
		 */

		if (_game_won) {
			_message.setText(player_name + " wins!");
		} else if (_game_drawn) {
			_message.setText("Draw game.");
		} else {
			_message.setText(next_player_name + " to play");
		}
	}

	private void checkWinDraw(Spot s, Color player_color) {
		// Get coordinates of the spot.
		int x = s.getSpotX();
		int y = s.getSpotY();

		int n = _board.getSpotHeight(); /* board dimension = 3. */

		// Check column for win.
		for (int i = 0; i < n; i++) {
			if (_board.getSpotAt(x, i).isEmpty()) {
				break;
			}
			if (!_board.getSpotAt(x, i).getSpotColor().equals(player_color)) {
				break;
			}
			if (i == n - 1) {
				_game_won = true;
				return;
			}
		}

		// Check row for win.
		for (int i = 0; i < n; i++) {
			if (_board.getSpotAt(i, y).isEmpty()) {
				break;
			}
			if (!_board.getSpotAt(i, y).getSpotColor().equals(player_color)) {
				break;
			}
			if (i == n - 1) {
				_game_won = true;
				return;
			}
		}

		// Check diagonal for win.
		if (x == y) {
			for (int i = 0; i < n; i++) {
				if (_board.getSpotAt(i, i).isEmpty()) {
					break;
				}
				if (!_board.getSpotAt(i, i).getSpotColor()
						.equals(player_color)) {
					break;
				}
				if (i == n - 1) {
					_game_won = true;
					return;
				}
			}
		}

		// Check anti-diagonal for win.
		if (y == n - 1 - x) {
			for (int i = 0; i < n; i++) {
				if (_board.getSpotAt(i, n - 1 - i).isEmpty()) {
					break;
				}
				if (!_board.getSpotAt(i, n - 1 - i).getSpotColor()
						.equals(player_color)) {
					break;
				}
				if (i == n - 1) {
					_game_won = true;
					return;
				}
			}
		}

		// Check for draw.
		_game_drawn = (_move_count == n * n);
	}

	@Override
	public void spotEntered(Spot s) {
		/* Highlight spot if game still going on. */

		if (_game_won || _game_drawn || !s.isEmpty()) {
			return;
		}
		s.highlightSpot();
	}

	@Override
	public void spotExited(Spot s) {
		/* Unhighlight spot. */

		s.unhighlightSpot();
	}

}
