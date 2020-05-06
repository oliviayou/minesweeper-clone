/**
 * Mine sweeper
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
		 
		private static final long serialVersionUID = 1L;
		public int row;
		  public int col;
		  public int minesNearBy;
		  public boolean isMine;
		  public boolean hasExploded;
		  public enumStatus status;
		  
		  public MineInfo(Mine m){
			   this.row = m.row;
			   this.col = m.col;
			   this.minesNearBy = m.minesNearBy;
			   this.isMine = m.isMine;
			   this.hasExploded = m.hasExploded;
			   this.status = m.status;
		  }
	 }
	 
	 public enum enumStatus {
		 NONE, 
		 OPEN, 
		 MARKED;
	 }
	
	 public enum enumClick {
		 LEFT, 
		 RIGHT;
	 }
	
	 public JButton button;
	 public enumStatus status; 
	 public boolean isMine; 
	 public int row; 
	 public int col; 
	 public int minesNearBy; 
	 public boolean hasExploded; 
	
	 public Mine(int r, int c) {
		  row = r;
		  col = c;
		  Reset(); 
		  InitMine(); 
	 }
	
	 public Mine() {
	 }
	
	 public void Reset() {
		  status = enumStatus.NONE; 
		  isMine = false; 
		  hasExploded = false; 
		  minesNearBy = 0; 
	 }
	
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
			 return Color.cyan;
		 }
		 else {
			 return Color.black;
		 }
	 }
	
	 public void Redraw() {
		  switch (WinMines.gameStatus) {
		  // game just started
		  case NONE:
			  button.setText(""); // clear text
			  button.setIcon(null); // clear icon
			  button.setForeground(null); // clear foreground
			  button.setBackground(null); // clear background
			  break;
		
		  // game started or completed
		  case READY:
		  case STARTED:
		  case COMPLETED:
			   switch (status) {
			   
				   // game has not started
				   case NONE: 
					    button.setText("");
					    button.setIcon(null);
					    button.setForeground(null);
					    button.setBackground(null);
					    break;
				    
				   case MARKED:
					    button.setIcon(iconFlag);
					    button.setForeground(null);
					    button.setBackground(null);
					    break;
				    
				   case OPEN:
					    if (minesNearBy == 0) {
					    	button.setText("");
					    }
				    
				    else {
				    	button.setText(Integer.toString(minesNearBy));
				    }
				    
				    button.setIcon(null); 
				    button.setForeground(getColor(minesNearBy));
				    button.setBackground(Color.lightGray); // open cell
				    break;
			   }
			   
			   break;
		
			  // game over - show all mines
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
	
	 public boolean SetIsMine() {
		 if (isMine != true) {
			 isMine = true;
			 return true;
		 }	
		 return false;
	 }
	
	 public boolean GetIsMine() {
		 return isMine;
	 }
	
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
				 if (isMine == true) {
					 hasExploded = true;
					 WinMines.gameStatus = WinMines.enumGameStatus.GAMEOVER;
					 Redraw();
				 } else {
					 WinMines.minesOpened++;
					 Redraw();
				 }
				 break;
		  }
	 }
	
	 private void MarkMine() {
		  switch (status) {
			  case OPEN:
				  break;  
				  
			  case MARKED:
				   status = enumStatus.NONE; //reset to unmarked
				   Redraw();
				   break;
				   
			  case NONE:
				   status = enumStatus.MARKED; //mark cell
				   Redraw();
				   break;
		  	}
	 }
	
	 Icon iconFlag = new ImageIcon(new ImageIcon("flag.png").getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
	 Icon iconMine = new ImageIcon(new ImageIcon("mine.png").getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));

	
	 private void InitMine() {
		  button = new JButton(); 
		  button.setOpaque(true); 
		  button.setHorizontalTextPosition(SwingConstants.CENTER);
		  button.setFont(new Font("Arial", Font.BOLD, 16)); 
		  
		  button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.darkGray, 1), BorderFactory.createLineBorder(Color.darkGray, 1)));
		
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
		    
				    // left click
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
					    	MediaPlayer player = new MediaPlayer(media); 
					    	player.play();
					     } catch (Throwable t){
					    	 t.printStackTrace();
					     }
					     
					     OpenMine();
					     
					     if (!hasExploded) {
						      WinMines.mineOpenedRow = row;
						      WinMines.mineOpenedCol = col;
					     } else {
					     }
			     
					     e.getComponent().getParent().dispatchEvent(e);
				    }
		    
				    // user right clicks to mark the cell with a flag
				    else if (e.getButton() == MouseEvent.BUTTON3) {
					     MarkMine();
					     e.getComponent().getParent().dispatchEvent(e);
				    }
			    }	
		  });
	 }
	
	 public int GetMinesNearBy() {
		 return minesNearBy;
	 }
	
	 public void SetMinesNearBy(int num) {
		 minesNearBy = num;
	 }

}