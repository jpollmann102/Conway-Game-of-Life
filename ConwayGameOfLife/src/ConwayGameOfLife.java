import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.colorchooser.AbstractColorChooserPanel;


public class ConwayGameOfLife extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static double cellSpawnProbability = 0.92;
	
	private static int[][] grid;
	private final static int gridX = 800;
	private final static int gridY = 800;
	private static Random rand = new Random();
	private static int generationCount = -2;
	private static int timer = 100;
	private static Timer programTimer;
	private static boolean running = false;
	
	static JButton stepButton;
	static JButton playButton;
	static JButton resetButton;
	static TextField cellSpawnText;
	static JCheckBox autoRun;
	static JColorChooser colorChooser;
	static JTextArea descriptionLabel;
	static AbstractColorChooserPanel panels[];
	
	public static void main(String[] args){
		
		final ConwayGameOfLife m = new ConwayGameOfLife(gridY, gridX);
		JFrame frame = new JFrame();
		JPanel mainPanel = new JPanel();
		Dimension optionsDimension = new Dimension(670, 600);
		
		playButton = new JButton("Play");
		stepButton = new JButton("Step Through One Generation");
		resetButton = new JButton("Reset");
		cellSpawnText = new TextField(6);
		autoRun = new JCheckBox("Auto Run");
		colorChooser = new JColorChooser();
		descriptionLabel = new JTextArea();
		
		descriptionLabel.setEditable(false);
		descriptionLabel.setBackground(frame.getBackground());
		descriptionLabel.setText("           Conway's Game of Life Rules\nAlive cell with 0-1 neighbors: dead next gen\nAlive cell with 2-3 neighbors: alive next gen\nAlive cell with 4+ neighbors: dead next gen\nDead cell with 3 neighbors: alive nex gen");
		
		panels = colorChooser.getChooserPanels();
		colorChooser.removeChooserPanel(panels[0]);
		colorChooser.removeChooserPanel(panels[2]);
		colorChooser.removeChooserPanel(panels[3]);
		colorChooser.removeChooserPanel(panels[4]);
		colorChooser.setColor(Color.RED);
		
		mainPanel.setLayout(new FlowLayout());
		mainPanel.setPreferredSize(optionsDimension);
		mainPanel.add(playButton);
		mainPanel.add(stepButton);
		mainPanel.add(resetButton);
		mainPanel.add(autoRun);
		mainPanel.add(cellSpawnText);
		mainPanel.add(colorChooser);
		mainPanel.add(descriptionLabel);
		
		frame.setResizable(false);
		frame.getContentPane().add(m, "West");
		frame.getContentPane().add(mainPanel, "East");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		frame.setTitle("Conway's Game of Life");
		frame.pack();
		
		cellSpawnText.setText(Double.toString(cellSpawnProbability));
		
		programTimer = new Timer(timer, new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e){
				m.updateGrid();
				m.repaint();
			}
		});
		
		autoRun.setSelected(false);

		autoRun.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!playButton.isEnabled()){
					playButton.setEnabled(true);
					playButton.setText("Play");
				}
			}
		});
		playButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(running){
					programTimer.stop();
					running = false;
					stepButton.setEnabled(true);
					playButton.setText("Play");
				}else{
					if(autoRun.isSelected()){
						programTimer.start();
						running = true;
						stepButton.setEnabled(false);
						playButton.setText("Pause");
					}else{
						playButton.setEnabled(false);
						playButton.setText("Click to Step -->");
					}
				}
			}
		});
		stepButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				m.updateGrid();
				m.repaint();
			}
		});
		resetButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(autoRun.isSelected()){
					programTimer.stop();
				}
				generationCount = 1;
				clearGrid();
				cellSpawnProbability = Double.parseDouble(cellSpawnText.getText());
				setupGrid();
				m.updateGrid();
				m.repaint();
				playButton.setEnabled(true);
				playButton.setText("Play");
			}
		});
	
	}
	
	public ConwayGameOfLife(int width, int height){
		ConwayGameOfLife.grid = new int[width / 4][height / 4];
		setupGrid();
		updateGrid();
	}
	
	private static void setupGrid(){
		for(int[] row : grid){
			for(int i = 0; i < row.length; i++){
				if(rand.nextDouble() < cellSpawnProbability){
					continue;
				}else{
					row[i] = rand.nextInt(2);
				}
			}
		}
	}
	
	private static void clearGrid(){
		for(int[] row : grid){
			for(int i = 0; i < row.length; i++){
				row[i] = 0;
			}
		}
	}
	
	public void updateGrid(){
		for(int y = 0; y < grid.length; y++){
			for(int x = 0; x < grid[y].length; x++){
				checkCells(y, x);
			}
		}
	}
	
	private void checkCells(int y, int x){
		
		// clean this up if possible
		
		int left = 0, dLeft = 0;
		int right = 0, dRight = 0;
		int top = 0;
		int bottom = 0, dLeftBottom = 0, dRightBottom = 0;
		
		int count;
		
		if(y == 0 && x > 0 && x < grid[x].length - 1){ // first row, not including (0, 0) and (0, length - 1)
			//System.out.println("Success - First Row");
			top = grid[y][x - 1];
			dRight = grid[y + 1][x - 1];
			right = grid[y + 1][x];
			dRightBottom = grid[y + 1][x + 1];
			bottom = grid[y][x + 1];
		}else if(y == grid[y].length - 1 && x > 0 && x < grid[x].length - 1){ // last row, not including (length - 1, 0) and (length - 1, length -1)
			//System.out.println("Success - Last Row");
			top = grid[y][x - 1];
			dLeft = grid[y - 1][x - 1];
			left = grid[y - 1][x];
			dLeftBottom = grid[y - 1][x + 1];
			bottom = grid[y][x + 1];
		}else if(x == 0 && y > 0 && y < grid[y].length - 1){ // first column, not including (0, 0) and (0, length - 1)
			//System.out.println("Success - First Column");
			left = grid[y - 1][x];
			dLeftBottom = grid[y - 1][x + 1];
			bottom = grid[y][x + 1];
			dRightBottom = grid[y + 1][x + 1];
			right = grid[y + 1][x];
		}else if(x == grid[x].length - 1 && y > 0 && y < grid[y].length - 1){ // last column, not including (0, length - 1) and (length - 1, length - 1)
			//System.out.println("Success - Last Column");
			left = grid[y - 1][x];
			dLeft = grid[y - 1][x - 1];
			top = grid[y][x - 1];
			dRight = grid[y + 1][x - 1];
			right = grid[y + 1][x];
		}else if(x == 0 && y == 0){ // top left corner (0, 0)
			//System.out.println("Success - Top Left Corner");
			right = grid[y + 1][x];
			dRightBottom = grid[y + 1][x + 1];
			bottom = grid[y][x + 1];
		}else if(x == grid[x].length - 1 && y == 0){ // top right corner (0, length - 1)
			//System.out.println("Success - Top Right Corner");
			top = grid[y][x - 1];
			dRight = grid[y + 1][x - 1];
			right = grid[y + 1][x];
		}else if(x == 0 && y == grid[y].length - 1){ // bottom left corner (length - 1, 0)
			//System.out.println("Success - Top Right Corner");
			left = grid[y - 1][x];
			dLeftBottom = grid[y - 1][x + 1];
			bottom = grid[y][x + 1];
		}else if(x == grid[x].length - 1 && y == grid[y].length - 1){ // bottom right corner (length - 1, length - 1)
			//System.out.println("Success - Bottom Right Corner");
			left = grid[y - 1][x];
			dLeft = grid[y - 1][x - 1];
			top = grid[y][x - 1];
		}else{
			//System.out.println("Success - Middle");
			left = grid[y - 1][x];
			right = grid[y + 1][x];
			top = grid[y][x - 1];
			bottom = grid[y][x + 1];
			dLeft = grid[y - 1][x - 1];
			dRight = grid[y + 1][x - 1];
			dLeftBottom = grid[y - 1][x + 1];
			dRightBottom = grid[y + 1][x + 1];
		}
		
		count = left + right + top + bottom + dLeft + dRight + dLeftBottom + dRightBottom;
		
		if(grid[y][x] == 1 && count <= 1){
			grid[y][x] = 0;
		}else if(grid[y][x] == 1 && count > 1 && count < 4){
			grid[y][x] = 1;
		}else if(grid[y][x] == 1 && count >= 4){
			grid[y][x] = 0;
		}else if(grid[y][x] == 0 && count == 3){
			grid[y][x] = 1;
		}
		
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(grid.length * 4, grid[0].length * 4);
	}
	
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Color gColor = g.getColor();
		
		g.drawString("Generation: " + generationCount++, gridX - 100, 10);
		for(int y = 0; y < grid.length; y++){
			for(int x = 0; x < grid[y].length; x++){
				if(grid[y][x] == 1){
					g.setColor(colorChooser.getColor());
					g.fillRect(x * 4, y * 4, 4, 4);
				}
			}
		}
		
		g.setColor(gColor);
	}
}
