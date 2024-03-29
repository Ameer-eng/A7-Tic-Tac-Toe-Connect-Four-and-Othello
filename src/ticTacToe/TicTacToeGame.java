package ticTacToe;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class TicTacToeGame {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				/* Create top level window. */

				JFrame main_frame = new JFrame();
				main_frame.setTitle("Tic Tac Toe");
				main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				/* Create panel for content. Uses BorderLayout. */
				JPanel top_panel = new JPanel();
				top_panel.setLayout(new BorderLayout());
				main_frame.setContentPane(top_panel);

				/*
				 * Create TicTacToeWidget component and put into center of
				 * content panel.
				 */

				TicTacToeWidget ticTacToe = new TicTacToeWidget();
				top_panel.add(ticTacToe, BorderLayout.CENTER);

				/* Pack main frame and make visible. */

				main_frame.pack();
				main_frame.setVisible(true);
			}
		});
	}
}
