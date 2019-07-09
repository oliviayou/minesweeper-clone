/**
 * Minesweeper Game
 * May 7th, 2018 
 * Mine class defines mine behaviour using object oriented programming
 **/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.Serializable;
import javafx.scene.media.*;


public class Mine {

	 public static class MineInfo implements Serializable{
		 
		  public int row;
		  public int col;
		  public int minesNearBy;
		  public boolean isMine;
		  public boolean isExploded;
		  public enumStatus status;
		  
		  public MineInfo(Mine m){
			   this.row = m.row;
			   this.col = m.col;
			   this.minesNearBy = m.minesNearBy;
			   this.isMine = m.isMine;
			   this.isExploded = m.isExploded;
			   this.status = m.status;
		  }
	 }
	 
	 // define possible statuses of a cell
	 public enum enumStatus {
		 NONE, OPEN, MARKED
	 };
	
	 // define possible clicks as left and right 
	 public enum enumClick {
		 LEFT, RIGHT
	 };
	
	 public JButton button;
	 public enumStatus status; 
	 public boolean isMine; 
	 public int row; 
	 public int col; 
	 public int minesNearBy; // number of adjacent mines
	 public boolean isExploded; // whether the game is over
	
	 public Mine(int r, int c) {
		  row = r;
		  col = c;
		  Reset(); 
		  InitMine(); 
	 }
	
	 public Mine() {
	 }
	
	 // reset the status of the game
	 public void Reset() {
		  status = enumStatus.NONE; 
		  isMine = false; 
		  isExploded = false; 
		  minesNearBy = 0; 
	 }
	
	 // set the colours of the number displayed on the cell to be the following
	 public Color getColor(int minesAround) {
		 
		 if (minesAround == 1) {
			 return Color.magenta;
		 }
		 else if (minesAround == 2) {
			 return Color.blue;
		 }
		 else if (minesAround == 3) {
			 return Color.red;
		 }
		 else if (minesAround == 4) {
			 return Color.pink;
		 }
		 else {
			 return Color.black;
		 }
	 }
	
	 // method to redraw all mines
	 public void Redraw() {
	  switch (WinMines.gameStatus) {
	  // if game status is none (just started)
	  case NONE:
		  button.setText(""); // clear text
		  button.setIcon(null); // clear icon
		  button.setForeground(null); // clear foreground
		  button.setBackground(null); // clear background
		  break;
	
	  // if game has started or completed:
	  case READY:
	  case STARTED:
	  case COMPLETED:
		   switch (status) {
		   
			   // if game has not started, clear all text
			   case NONE:
			    button.setText("");
			    button.setIcon(null);
			    button.setForeground(null);
			    button.setBackground(null);
			    break;
			    
			   // if the cell is marked, display flag icon and clear the background and foreground
			   case MARKED:
			    button.setIcon(iconFlag);
			    button.setForeground(null);
			    button.setBackground(null);
			    break;
			    
			   // if the cell is open:
			   case OPEN:
			    // if there are no mines nearby, set the cell to be blank
			    if (minesNearBy == 0) {
			    	button.setText("");
			    }
			    
			    // if there are mines nearby, display the number inside the cell
			    else {
			    	button.setText(Integer.toString(minesNearBy));
			    }
			    
			    button.setIcon(null); // don't display an icon
			    button.setForeground(getColor(minesNearBy));
			    button.setBackground(Color.green); // a blank cell is green
			    break;
		   }
		   
		   break;
	
		  // if the game is over, show all mines
		  case GAMEOVER:
			  if (isMine == true) {
				  button.setIcon(iconMine);
				  button.setForeground(Color.black);
				  button.setBackground(null);
			  }
			  break;
		  }
	  button.revalidate();
	  button.repaint();
	 }
	
	 // check if cell is a mine. if not a mine, set it to true
	 public boolean SetIsMine() {
		 if (isMine != true) {
			 isMine = true; // if it is a mine, return true
			 return true;
		 }	
		 return false;
	 }
	
	 // method to check if the cell is a mine
	 public boolean GetIsMine() {
		 return isMine;
	 }
	
	 // method to open the cell is the game has started
	 public void OpenMine() {
		 if (WinMines.gameStatus != WinMines.enumGameStatus.STARTED && WinMines.gameStatus != WinMines.enumGameStatus.READY) {
			 return;
		 }
		 
		 switch (status) {
		 case OPEN:
		 case MARKED:
			 break;
		 case NONE:
			 status = enumStatus.OPEN;
			 
			 // if a mine is clicked, game is over
			 if (isMine == true) {
				 isExploded = true;
				 WinMines.gameStatus = WinMines.enumGameStatus.GAMEOVER;
				 Redraw();
				 
			 } else {
				 // if mine is not clicked, increment number of cells opened by one
				 WinMines.minesOpened++;
				 Redraw();
			 }
			 break;
		  }
	 }
	
	 // mark the cell with a flag
	 private void MarkMine() {
		  switch (status) {
		  
			  // if it is already open, you cannot flag the cell
			  case OPEN:
				  break;
				  
			  // if it is already marked, reset it to unmarked
			  case MARKED:
				   status = enumStatus.NONE;
				   Redraw();
				   break;
				   
			  // if the cell is not marked, mark it with a flag
			  case NONE:
				   status = enumStatus.MARKED;
				   Redraw();
				   break;
		  	}
	 }
	
	 Icon iconFlag = new ImageIcon("flag.jpg"); 
	 Icon iconMine = new ImageIcon("mine.png"); 
	
	 private void InitMine() {
		  button = new JButton(); 
		  button.setOpaque(true); 
		  button.setHorizontalTextPosition(SwingConstants.CENTER);  // center mine within cell
		  button.setFont(new Font("Arial", Font.BOLD, 16)); 
		  
		  // set the border of the cells to be bold and dark grey
		  button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.darkGray, 1), BorderFactory.createLineBorder(Color.darkGray, 1)));
		
		  // add mouse action listener
		  button.addMouseListener(new MouseAdapter() {
			   public void mousePressed(MouseEvent e) {
				    // no return value if the game is over
				    if (WinMines.gameStatus == WinMines.enumGameStatus.GAMEOVER) {
				    	return;
				    }
				    // no return if the button cannot be clicked
				    if (button.isEnabled() == false) {
				     return;
				    }
		    
				    WinMines.mineOpenedRow = -1;
				    WinMines.mineOpenedCol = -1;
				
				    // if user left and right clicks, and all mines are located correctly,  open all cells nearby
				    if (SwingUtilities.isLeftMouseButton(e)&& SwingUtilities.isRightMouseButton(e)) {
				    	if (WinMines.getMarksNearby(row, col) == minesNearBy) {
				    		WinMines.openNearBy(row, col);
				    	}
				    }
		    
				    // if the user left clicks, initiate the following:
				    else if (e.getButton() == MouseEvent.BUTTON1) {
				    	// if no mine is open, then open the cell
					    if (WinMines.minesOpened == 0) {
					    	WinMines.generateRandomMines(row, col);
					    }
					     
					    try{
					    	Media media = new Media(new File("beep.mp3").toURI().toString());
					    	if (WinMines.mines[row][col].isMine) {
					    		media = new Media(new File("bomb.mp3").toURI().toString());
					    	}
					    	MediaPlayer player = new MediaPlayer(media); //check
					    	player.play();
					     } catch (Throwable t){
					    	 t.printStackTrace();
					     }
					     
					     OpenMine();
					     
					     // if the game has not ended,then track row and column of opened mine
					     if (!isExploded) {
						      WinMines.mineOpenedRow = row;
						      WinMines.mineOpenedCol = col;
					     } else {
					     }
			     
					     // sent this event to the parent class WinMines
					     e.getComponent().getParent().dispatchEvent(e);
				    }
		    
				    // if the user right clicks, then mark the cell with a flag
				    else if (e.getButton() == MouseEvent.BUTTON3) {
					     MarkMine();
					     // sent this event to the parent class, WinMines
					     e.getComponent().getParent().dispatchEvent(e);
				    }
			    }	
		  });
	 }
	
	 // method to return the number of mines nearby
	 public int GetMinesNearBy() {
		 return minesNearBy;
	 }
	
	 // method to set the number of mines nearby to number passed to parameters
	 public void SetMinesNearBy(int num) {
		 minesNearBy = num;
	 }

}