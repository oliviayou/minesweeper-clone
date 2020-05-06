/**
 * Minesweeper
 * Main program to define mine class
 **/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.TimerTask;
import javafx.embed.swing.JFXPanel;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;

public class WinMines extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private Container pane; 
	private JPanel board = new JPanel();; 
	final int CELL_SIZE = 30; 
	
	final static int EASY_MINES_NUM = 10;
	final static int MEDIUM_MINES_NUM = 40;
	final static int HARD_MINES_NUM = 99;
	
	final static int EASY_SIZE_ROW = 9;
	final static int EASY_SIZE_COL = 9;
	final static int MEDIUM_SIZE_ROW = 16;
	final static int MEDIUM_SIZE_COL = 16;
	final static int HARD_SIZE_ROW = 16;
	final static int HARD_SIZE_COL = 30;
	
	 // initialize number of rows in easy 
	 static int SIZE_ROW = EASY_SIZE_ROW;
	 static int SIZE_COL = EASY_SIZE_COL;
	 static int MINES_NUM = EASY_MINES_NUM;
	
	 private JPanel minesBoard; 
	 private final JLabel timer = new JLabel(""); 
	 public static Mine[][] mines = new Mine[HARD_SIZE_ROW][HARD_SIZE_COL]; // max board size
	 private int secondsLapsed; 
	
	 public enum enumGameStatus {
		 READY, STARTED, GAMEOVER, NONE, COMPLETED
	 }
	
	 public static enumGameStatus gameStatus;
	
	 public static int minesOpened; 
	 static JFrame frame = new JFrame("Minesweeper"); 
	
	 public static int mineOpenedRow;
	 public static int mineOpenedCol;
	
	 // default constructor
	 public WinMines() {
		  createMines();
		  secondsLapsed = 0;
		  gameStatus = enumGameStatus.NONE;
	 }
	
	 // Save board to serialized file
	 public void saveBoard() {
		  try {
			   Mine.MineInfo[][] boardInfo = new Mine.MineInfo[SIZE_ROW][SIZE_COL];
			   for (int i = 0; i < SIZE_ROW; i++) {
				   for (int j = 0; j < SIZE_COL; j++) {
					   boardInfo[i][j] = new Mine.MineInfo(mines[i][j]);
				   }
			   }
			   // Write object to serialized file
			   ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("out.bin"));
			   out.writeObject(boardInfo);
			   out.close();
		  } catch (Throwable t) {
			  t.printStackTrace();
		  }
 	}
	
	 public void loadBoard() {
		 try {
			 // Read serialized file
			 ObjectInputStream in = new ObjectInputStream(new FileInputStream("out.bin"));
			 Mine.MineInfo[][] boardInfo = (Mine.MineInfo[][]) in.readObject();
			
			 // Restore the current board with the serialized version loaded
			 for (int i = 0; i < SIZE_ROW; i++) {
				 for (int j = 0; j < SIZE_COL; j++) {
					 mines[i][j].hasExploded = boardInfo[i][j].hasExploded;
				     mines[i][j].isMine = boardInfo[i][j].isMine;
				     mines[i][j].minesNearBy = boardInfo[i][j].minesNearBy;
				     mines[i][j].status = boardInfo[i][j].status;
				     mines[i][j].Redraw();
				 }
			 }
		 } catch (Throwable t) {
			 t.printStackTrace();
		 }
	 }
	
	 public void createMines() {
		  for (int row = 0; row < HARD_SIZE_ROW; row++) {
			  for (int col = 0; col < HARD_SIZE_COL; col++) {
				  mines[row][col] = new Mine(row, col);
			  }
		  }
	 }
	
	 public static void initMines() {
		 for (int row = 0; row < HARD_SIZE_ROW; row++) {
			 for (int col = 0; col < HARD_SIZE_COL; col++) {
				 mines[row][col].Reset();
			 }
		 }
	 }
	
	 public static void generateRandomMines(int r, int c) {
		  int minesNum = 0;
		  Random rand = new Random();
		  int counter = 0;
		  
		  // generates mines up to 10000 times to prevent infinite loop
		  while (minesNum < MINES_NUM && counter <= 10000) {
			   int n = rand.nextInt(SIZE_ROW * SIZE_COL);
			   int row = n / SIZE_COL;
//			   int col = n - row * SIZE_COL;
			   int col = n % SIZE_COL;

			   
			   // ensure first click opens an large area of cells
			   if (row < r - 1 || row > r + 1 || col < c - 1 || col > c + 1) {
				   if (mines[row][col].SetIsMine()) {
					   minesNum++;
				   }
			   }
			   counter++; 
		  }
		  
		  if (minesNum != MINES_NUM) {
			  JOptionPane.showMessageDialog(frame, "Error, only " + minesNum + " mines are generated.");
		  }
		
		  // calculate number displayed in cell
		  for (int row = 0; row < SIZE_ROW; row++) {
			   for (int col = 0; col < SIZE_COL; col++) {
				    int mineNum = 0;
				    if (IsMine(row - 1, col - 1) == true)
				    	mineNum++;
				    if (IsMine(row - 1, col) == true)
				    	mineNum++;
				    if (IsMine(row - 1, col + 1) == true)
				    	mineNum++;
				    if (IsMine(row, col - 1) == true)
				    	mineNum++;
				    if (IsMine(row, col + 1) == true)
				    	mineNum++;
				    if (IsMine(row + 1, col - 1) == true)
				    	mineNum++;
				    if (IsMine(row + 1, col) == true)
				    	mineNum++;
				    if (IsMine(row + 1, col + 1) == true)
				    	mineNum++;
				    mines[row][col].SetMinesNearBy(mineNum);
				   }
			  }
		 }
	
	 public void StartTimer() {
		  gameStatus = enumGameStatus.STARTED;
		  secondsLapsed = 0;
	 }
	
	 public void StopTimer() {
		 timer.setText("");
	 }
	
	 // timer automatically called every second
	 public void Timer() {
		  if (gameStatus == enumGameStatus.STARTED) {
			  secondsLapsed++;
			  timer.setText(secondsLapsed / 60 + ":" + String.format("%02d", secondsLapsed % 60)); //minutes : seconds
		  }
	 }
	
	 // over-riding method for action listener
	 public void actionPerformed(ActionEvent event) {
	 };
	
	 // checks if cell is on board
	 public static boolean IsInBoard(int row, int col) {
		  if (row < 0 || row >= SIZE_ROW || col < 0 || col >= SIZE_COL) {
			  return false;
		  }
		  return true;
	 }
	
	 public static boolean IsMine(int row, int col) {
		  if (IsInBoard(row, col) == false) {
			  return false;
		  }
		  return mines[row][col].GetIsMine();
	 }
	
	 // add the mine GUI to the board
	 public void AddMinesToBoard() {
		  minesBoard = new JPanel(new GridLayout(0, SIZE_COL));
		
		  minesBoard.setBorder(new LineBorder(Color.BLACK)); 
		  minesBoard.setPreferredSize(new Dimension(CELL_SIZE * SIZE_COL, CELL_SIZE * SIZE_ROW));
		
		  board.add(minesBoard, BorderLayout.CENTER); 
		  pane.add(board, BorderLayout.CENTER); 
		
		  for (int row = 0; row < SIZE_ROW; row++) {
			   for (int col = 0; col < SIZE_COL; col++) {
				   minesBoard.add(mines[row][col].button);
			   }
		  }
		
		  minesBoard.addMouseListener(new MouseAdapter() {
			   public void mousePressed(MouseEvent e) {
				
					 if (e.getButton() == MouseEvent.BUTTON1) { // left click
					
					     if (gameStatus == enumGameStatus.READY) {
					    	 StartTimer();
					     }
					     // when user opens a cell without a cell with mines around it, open all nearby cells
					     if (mineOpenedRow > -1 && mineOpenedCol > -1) {
					    	 TryOpenEmptyMines(mineOpenedRow, mineOpenedCol);
					     }
					     CheckGameStatus();
				    }
			   }
		  });

		  // redraw mines when new game begins
		  RedrawMines(); 
	  }
	
	 // reset and initialize mines when new game starts
	 public void NewGame() {
		  gameStatus = enumGameStatus.READY; 
		  initMines(); 
		  board.removeAll(); 
		  AddMinesToBoard(); 
		  minesOpened = 0; 
		  timer.setText(""); 
		  frame.repaint();
		  frame.revalidate();
	 }
	
	 // recursively open the mines when there are no adjacent mines
	 public void TryOpenMine(int row, int col) {
		  if (IsInBoard(row, col) == false) {
			  return;
		  }
		  // only try opening mine when it's not marked or opened
		  if (mines[row][col].status == Mine.enumStatus.NONE) {
			   mines[row][col].OpenMine();
			   if (mines[row][col].GetMinesNearBy() == 0 && mines[row][col].status == Mine.enumStatus.OPEN) {
				   TryOpenEmptyMines(row, col);
			   }
		  }
	 }
	
	 // recursively find all the nearby cells that have no mines around it
	 public void TryOpenEmptyMines(int row, int col) {
		  // when its not a mine and no adjacent mines, open all cells
		  if (mines[row][col].GetIsMine() == false && mines[row][col].GetMinesNearBy() == 0) {
			   // checks if cell is a mine
			   TryOpenMine(row - 1, col - 1);
			   TryOpenMine(row - 1, col);
			   TryOpenMine(row - 1, col + 1);
			   TryOpenMine(row, col - 1);
			   TryOpenMine(row, col + 1);
			   TryOpenMine(row + 1, col - 1);
			   TryOpenMine(row + 1, col);
			   TryOpenMine(row + 1, col + 1);
		  }
	 }
	
	 private static void OpenMine(int row, int col) {
		  if (IsInBoard(row, col) == false) {
			  return;
		  }
		  mines[row][col].OpenMine();
	 }
	
	 // checked is the row and column is marked
	 public static boolean isMarked(int row, int col) {
		  if (IsInBoard(row, col) == false) {
			  return false;
		  }
		  if (mines[row][col].status == Mine.enumStatus.MARKED) {
			  return true;
		  } else {
			  return false;
		  }
	 }
	
	 public static int getMarksNearby(int row, int col) {
		  int num = 0;
		  if (isMarked(row - 1, col - 1) == true) {
			  num++;
		  }
		  if (isMarked(row - 1, col) == true) {
			  num++;
		  }
		  if (isMarked(row - 1, col + 1) == true) {
			  num++;
		  }
		  if (isMarked(row, col - 1) == true) {
			  num++;
		  }
		  if (isMarked(row, col + 1) == true) {
			  num++;
		  }
		  if (isMarked(row + 1, col - 1) == true) {
			  num++;
		  }
		  if (isMarked(row + 1, col) == true) {
			  num++;
		  }
		  if (isMarked(row + 1, col + 1) == true) {
			  num++;
		  }
		  return num;
	 }
	
	 // opens all nearby cells when user left and right clicks at the same time
	 // only occurs if enough mines are marked/opened around the cell
	 public static void openNearBy(int row, int col) {
		  OpenMine(row - 1, col - 1);
		  OpenMine(row - 1, col);
		  OpenMine(row - 1, col + 1);
		  OpenMine(row, col - 1);
		  OpenMine(row, col + 1);
		  OpenMine(row + 1, col - 1);
		  OpenMine(row + 1, col);
		  OpenMine(row + 1, col + 1);
	 }
	
	 public void CheckGameStatus() {
		  if (gameStatus == enumGameStatus.STARTED || gameStatus == enumGameStatus.READY) {
			   if (minesOpened > 0) {
				   gameStatus = enumGameStatus.STARTED;
			   }
			   if (minesOpened == SIZE_ROW * SIZE_COL - MINES_NUM) {
				    gameStatus = enumGameStatus.COMPLETED;
				    JOptionPane.showMessageDialog(frame,
				      "Congratulations, you win!"); // output message to the user
			   }
		  }
		  if (gameStatus == enumGameStatus.GAMEOVER) {
			  StopTimer();
			  JOptionPane.showMessageDialog(frame, "Game over, you hit the mine"); 
		  }
	 }
	
	 public void RedrawMines() {
		  for (int row = 0; row < SIZE_ROW; row++) {
			   for (int col = 0; col < SIZE_COL; col++) {
				   mines[row][col].Redraw();
			   }
		  }
	 }
	
	 public void addBoardToPane() {
		  addMenuToBoard();
		  pane.add(board, BorderLayout.CENTER);
	 }
	
	 public void addMenuToBoard() {
		  JToolBar tools = new JToolBar();
		  pane.add(tools, BorderLayout.PAGE_START); 
		
		  final ButtonGroup group = new ButtonGroup();
		  JToggleButton easyBtn = new JToggleButton("Easy"); 
		  easyBtn.addActionListener(new ActionListener() {
			   @Override
			   public void actionPerformed(ActionEvent e) {
				    SIZE_ROW = EASY_SIZE_ROW;
				    SIZE_COL = EASY_SIZE_COL;
				    MINES_NUM = EASY_MINES_NUM;
				    NewGame();
			   }
		  });
		  group.add(easyBtn); // toggle group so only one button can be toggled
		  easyBtn.setSelected(true); 
		  tools.add(easyBtn); // add to toolbar
		
		  JToggleButton mediumBtn = new JToggleButton("Medium"); 
		  mediumBtn.addActionListener(new ActionListener() { 
		     @Override
		     public void actionPerformed(ActionEvent e) {
				      SIZE_ROW = MEDIUM_SIZE_ROW;
				      SIZE_COL = MEDIUM_SIZE_COL;
				      MINES_NUM = MEDIUM_MINES_NUM;
				      NewGame(); 
			      }
		     });
		  	group.add(mediumBtn); 
		  	tools.add(mediumBtn); 
		
		  JToggleButton hardBtn = new JToggleButton("Hard"); 
		  hardBtn.addActionListener(new ActionListener() { 
		   @Override
		   public void actionPerformed(ActionEvent e) {
			    SIZE_ROW = HARD_SIZE_ROW;
			    SIZE_COL = HARD_SIZE_COL;
			    MINES_NUM = HARD_MINES_NUM;
			    NewGame(); 
			    }
		  	});
		  group.add(hardBtn);
		  tools.add(hardBtn); 
		
		  JToggleButton loadBtn = new JToggleButton("Load");
		  loadBtn.addActionListener(new ActionListener() {
			   @Override
			   public void actionPerformed(ActionEvent e) {
				    loadBoard();
				    frame.repaint();
				    frame.revalidate();
			    }
		  });
		  group.add(loadBtn); 
		  tools.add(loadBtn); 
		
		  JToggleButton saveBtn = new JToggleButton("Save");
		  saveBtn.addActionListener(new ActionListener() {
			  @Override
			  public void actionPerformed(ActionEvent e) {
				  saveBoard();
			  }
		  });
		  group.add(saveBtn);
		  tools.add(saveBtn); 
		
		  tools.add(Box.createHorizontalGlue()); // horizontal tool bar
		  tools.add(timer); 
		  tools.addSeparator(); // separate level buttons from timer
	 }
	
	 // define the application that runs the static instance of the WinMines class
	 static WinMines app;
	
	 public static void main(String[] args) {
		  // Initialize the feature that produces sound
		  new JFXPanel();
		  
		  app = new WinMines();
		
		  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		  frame.setLocationByPlatform(true);
		
		  app.pane = frame.getContentPane(); 
		  app.addBoardToPane(); 
		
		  frame.pack();
		  frame.setSize(1024, 640); 
		  frame.setVisible(true); 
		
		  TimerTask repeatedTask = new TimerTask() {
			   public void run() {
				   app.Timer();
			   }
		  };
		  
		  java.util.Timer timer = new java.util.Timer("Timer");
		  long delay = 1000L; // delay timer by one second for task execution
		  long period = 1000L; // increment by one second every second
		  timer.scheduleAtFixedRate(repeatedTask, delay, period); 
	
		  app.NewGame();
	 }
}