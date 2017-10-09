package gui_programs;

import javax.imageio.ImageIO;
//Karamel Quitayen
//G Period Java
//Extra Credit: Yahtzee
//=====================================
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

public class Yahtzee extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private YahtzeePlayer player;
	private DiceButton[] dice;
	
	private ScoringPanel scorePanel;
	private JPanel dicePanel = new JPanel();
	private JPanel btnPanel = new JPanel();
	
	private JButton roll = new JButton("Roll");
	private JButton start = new JButton("Start");
	private JButton game = new JButton("New Game");
	private Timer timer = new Timer(100, new TimerListener());
	
	private int cntr, rollCnt, currTurn, round;
	private boolean[] canSelect;
	//private final String path = "src/Images/Dice/";
	
	//menu bar items
	private JMenuBar menuBar = new JMenuBar();
	private JMenu[] menu = {new JMenu("File"), new JMenu("Edit"), new JMenu("About")};
	private JMenuItem[] subItems = {new JMenuItem("New Game"), new JMenuItem("Current Game"), new JMenuItem("Exit"), 
		new JMenuItem("Dice Color"), new JMenuItem("Instructions"), new JMenuItem("Credits")};
	
	Yahtzee()
	{
		//set up frame
		setTitle("Yahtzee");
		setLocation(600,100);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		currTurn = 0;
		
		//set up panels
		dicePanel.setSize(665,115);
		dicePanel.setLocation(0,0);
		btnPanel.setSize(665,50);
		btnPanel.setLocation(0,115);
		game.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				newGame();
			}
		});
		
		start.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				round++;
				dicePanel.setBorder(BorderFactory.createTitledBorder(null, player.name() + ": Round " + round, TitledBorder.CENTER, 
						TitledBorder.TOP, new Font("Consolas", Font.BOLD, 16), Color.BLACK));
				
				rollCnt = 0;
				
				start.setEnabled(false);
				roll.setEnabled(true);
				
				dicePanel.removeAll();
				dice = new DiceButton[5];
				for (int i = 0 ; i < 5; i++)
				{
					DiceButton d = new DiceButton(1);
					dice[i] = d;
					dicePanel.add(d);
				}
				refresh();
			}
		});
		roll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
			{
				for (int i = 0 ; i < 5; i++)
				{
					if (dice[i].isSelected())
					{
						//dice[i].setDisabledIcon(new ImageIcon(path + "Blue/" + dice[i].getVal() + ".png"));
						//dice[i].setEnabled(false);
						Image img = null;
						String path = "Blue/" + dice[i].getVal() + ".png";
						try {
							img = ImageIO.read(ResourceLoader.load(path));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						
						dice[i].setDisabledIcon(new ImageIcon(img));
						dice[i].setEnabled(false);
					}
					refresh();
				}
				cntr = 0;
				timer.start();
				setEnabled(true);
			}
		});
		btnPanel.add(start);
		btnPanel.add(roll);
		
		//set up menuBar
		menu[0].add(subItems[0]); menu[0].add(subItems[1]); menu[0].add(subItems[2]);
		menu[1].add(subItems[3]);
		menu[2].add(subItems[4]); menu[2].add(subItems[5]);
		for (int i = 0; i < 6; i++)
			subItems[i].addActionListener(this);
		for (int i = 0; i < 3; i++)
			menuBar.add(menu[i]);
		setJMenuBar(menuBar);
		subItems[1].setEnabled(false);
		
		getContentPane().setLayout(null);
		setVisible(true);
		subItems[5].setEnabled(false);
		about();
	}
	
	//actions for menubar items
	public void actionPerformed (ActionEvent e)
	{
		if (currTurn > 0)
		{
			for (int i = 0; i < 13; i++)
				if (e.getSource() == scorePanel.getButton(i))
				{
					canSelect[i] = false;
					scorePanel.getButton(i).setEnabled(false);
					start.setEnabled(true);
					roll.setEnabled(false);
					for (int j = 0; j < 13; j++)
						if (canSelect[j])
							scorePanel.getText()[j].setText("");
					setEnabled(false);
					scorePanel.setPoints(canSelect);
					refresh();
				}
		}
		if (e.getSource() == subItems[0]) //new game
		{
			newGame();
		}
		else if (e.getSource() == subItems[2]) //exit
			System.exit(0);
		else if (e.getSource() == subItems[4]) //directions
			instructions();
		else if (e.getSource() == subItems[5]) //about
			about();
	}
	
	public void instructions()
	{
		setSize(300,300);
		getContentPane().removeAll();
		JPanel titlePnl = new JPanel();
		titlePnl.setSize(300,50);
		titlePnl.setLocation(0,0);
		titlePnl.setBackground(Color.WHITE);
		JLabel title = new JLabel("YAHTZEE");
		title.setFont(new Font("Consolas", Font.BOLD, 25));
		titlePnl.add(title);
		getContentPane().add(titlePnl);
		
		JPanel subPnl = new JPanel();
		subPnl.setSize(300,20);
		subPnl.setLocation(0,50);
		subPnl.setBackground(Color.RED);
		JLabel sub = new JLabel("Game Directions");
		sub.setFont(new Font("Consolas", Font.PLAIN, 20));
		getContentPane().add(subPnl);
		
		JPanel dPnl = new JPanel();
		dPnl.setSize(300,230);
		dPnl.setLocation(0,70);
		JTextArea directions = new JTextArea();
		String words = "There are two players. Each player has 1 turn of 13 rounds \n"
					 + "to try and achieve the most points. In each round, the \n" 
					 + "player rolls 5 dice. The player can roll the dice 3 \n"
					 + "times, and hold any number of dice after each roll.\n"
					 + "Once a dice is held and a roll is made, the dice \n"
					 + "can not be change. After 3 rolls or all dice have \n"
					 + "been held, the player chooses a score for the roll.\n"
					 + "The player must choose a score each round. The player \n"
					 + "with the greates point total wins.";
					 
		directions.setText(words);
		directions.setFont(new Font("Consolas", Font.PLAIN, 12));
		dPnl.add(directions);
		getContentPane().add(dPnl);
		
		refresh();
	}
	
	public static void main(String[] args){
		new Yahtzee();
	}
	
	private void newGame()
	{
		setSize(672,460);
		getContentPane().removeAll();
		subItems[1].setEnabled(true);
		
		player = new YahtzeePlayer("Player 1");
		currTurn = 1;
		turn();
	}
	
	public void setBlue()
	{
		for (int i = 0; i < 5; i++)
		{
			//dice[i].setDisabledIcon(new ImageIcon(path + "Blue/" + dice[i].getVal() + ".png"));
			//dice[i].setEnabled(false);
			Image img = null;
			String path = "Blue/" + dice[i].getVal() + ".png";
			try {
				img = ImageIO.read(ResourceLoader.load(path));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			dice[i].setDisabledIcon(new ImageIcon(img));
			dice[i].setEnabled(false);
		}
		refresh();
	}
	
	public void turn()
	{
		dicePanel.setBorder(BorderFactory.createTitledBorder(null, player.name(), TitledBorder.CENTER, 
				TitledBorder.TOP, new Font("Consolas", Font.BOLD, 16), Color.BLACK));
		
		getContentPane().add(dicePanel);
		getContentPane().add(btnPanel);
		scorePanel = new ScoringPanel();
		getContentPane().add(scorePanel);
		scorePanel.setLocation(0,160);
		start.setEnabled(true);
		roll.setEnabled(false);
		
		canSelect = new boolean[13];
		for (int i = 0; i < 13; i++)
		{
			canSelect[i] = true;
			scorePanel.getButton(i).addActionListener(this);
		}
		setEnabled(false);
		refresh();
	}
	
	public void setEnabled(boolean enabled)
	{
		for (int i = 0; i < 13; i++)
		{
			scorePanel.getButton(i).setEnabled(false);
			if (enabled)
				if (canSelect[i])
					scorePanel.getButton(i).setEnabled(true);
		}
	}
	
	private class TimerListener implements ActionListener
	{
		public void actionPerformed(ActionEvent Evt)
		{
			// method to choose dice and display them
			rollDice();
			// counts number of rolls
			cntr++;
			// when hits 10 stops rolling
			if (cntr == 10)
			{
				timer.stop();
				if (currTurn == 1)
				{
					int[] val = new int[5];
					for (int i = 0 ; i < 5; i++)
						val[i] = dice[i].getVal();
					player.setVal(val);
					player.setScores();
					scorePanel.setText(player.getScores(), canSelect);
				}
				refresh();
				rollCnt++;
				if (rollCnt == 3)
				{
					roll.setEnabled(false);
					setBlue();
				}
			}
		}
	}
	
	private void rollDice()
	{
		//change each dice image to a random new image
		Random randNumGen = new Random();
		for (int i = 0; i < 5; i++)
		{
			if (!dice[i].isSelected())
			{
				dice[i].setVal(randNumGen.nextInt(6) + 1);
				
				//dice[i].setIcon(new ImageIcon(path + "White/" + dice[i].getVal() + ".gif"));
				Image img = null;
				String path = "White/" + dice[i].getVal() + ".gif";
				try {
					img = ImageIO.read(ResourceLoader.load(path));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				dice[i].setIcon(new ImageIcon(img));
			}
		}
		refresh();
	}
	
	public void about()
	{
		setSize(250,320);
		getContentPane().removeAll();
		JPanel aboutPnl = new JPanel();
		aboutPnl.setSize(270,320);
		
		JLabel title = new JLabel("       YAHTZEE      ");
		title.setFont(new Font("Consolas", Font.BOLD, 25));
		aboutPnl.add(title);
		JLabel period = new JLabel("G Period JAVA");
		period.setFont(new Font("Consolas", Font.PLAIN, 20));
		aboutPnl.add(period);
		
		//aboutPnl.add(new JLabel(new ImageIcon("src/Images/Other/Corgi Running.gif")));
		Image img = null;
		String path = "Corgi Running.gif";
		try {
			img = ImageIO.read(ResourceLoader.load(path));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		aboutPnl.add(new JLabel(new ImageIcon(img)));
		
		JLabel author = new JLabel("Karamel Quitayen");
		author.setFont(new Font("Consolas", Font.PLAIN, 20));
		aboutPnl.add(author);
		getContentPane().add(aboutPnl);
		refresh();
	}
	
	private void refresh(){
		getContentPane().repaint();
		getContentPane().revalidate();
	}
}

class DiceButton extends JButton implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private boolean selected, canRoll;
	private int value;
	
	DiceButton(int v) 
	{
		value = v;
		canRoll = true;
		selected = false;
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		
		//setIcon(new ImageIcon("src/Images/Dice/White/" + value + ".gif"));
		
		Image img = null;
		String path = "White/" + value + ".gif";
		try {
			img = ImageIO.read(ResourceLoader.load(path));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		setIcon(new ImageIcon(img));
		
		addActionListener(this);
		
	}
	public void setVal(int i){
		value = i;
	}
	public int getVal(){
		return value;
	}
	public boolean isSelected(){
		return selected;
	}
	public void actionPerformed(ActionEvent e)
	{
		if (canRoll)
		{
			if (!selected)
			{
				//setIcon(new ImageIcon("src/Images/Dice/Red/" + value + ".gif"));
				Image img = null;
				String path = "Red/" + value + ".gif";
				try {
					img = ImageIO.read(ResourceLoader.load(path));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				setIcon(new ImageIcon(img));
				
				selected = true;
			}
			else
			{
				//setIcon(new ImageIcon("src/Images/Dice/White/" + value + ".gif"));
				Image img = null;
				String path = "White/" + value + ".gif";
				try {
					img = ImageIO.read(ResourceLoader.load(path));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				setIcon(new ImageIcon(img));
				selected = false;
			}
		}
	}
}

class ScoringPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private JPanel pointsPanel = new JPanel();
	private JPanel upperLevel = new JPanel();
	private JPanel lowerLevel = new JPanel();
	private JPanel btn1 = new JPanel(new GridLayout(6,1,0,5)); //private JPanel btn1 = new JPanel(new GridLayout(6,1));
	private JPanel btn2 = new JPanel(new GridLayout(7,1,0,5)); //private JPanel btn2 = new JPanel(new GridLayout(7,1));
	private JPanel btn3 = new JPanel(new GridLayout(5,1,0,10)); //private JPanel btn3 = new JPanel(new GridLayout(5,1,0,12));
	private JPanel txt1 = new JPanel(new GridLayout(6,1,0,10)); //private JPanel txt1 = new JPanel(new GridLayout(6,1));
	private JPanel txt2 = new JPanel(new GridLayout(7,1,0,10)); //private JPanel txt2 = new JPanel(new GridLayout(7,1));
	private JPanel txt3 = new JPanel(new GridLayout(5,1,0,5)); //private JPanel txt3 = new JPanel(new GridLayout(5,1));
	private JButton[] rolls;
	private JTextField[] scores;
	private JTextField[] points;
	
	ScoringPanel()
	{
		//set up panel
		setSize(665,250);
		
		upperLevel.setPreferredSize(new Dimension(200,240));
		lowerLevel.setPreferredSize(new Dimension(250,240));
		pointsPanel.setPreferredSize(new Dimension(200,240));
		
		upperLevel.setBorder(BorderFactory.createTitledBorder(null, "Upper Level", TitledBorder.LEFT, 
				TitledBorder.TOP, new Font("Consolas", Font.BOLD, 14), Color.BLACK));
		lowerLevel.setBorder(BorderFactory.createTitledBorder(null, "Lower Level", TitledBorder.LEFT, 
				TitledBorder.TOP, new Font("Consolas", Font.BOLD, 14), Color.BLACK));
		pointsPanel.setBorder(BorderFactory.createTitledBorder(null, "Scores", TitledBorder.LEFT, 
				TitledBorder.TOP, new Font("Consolas", Font.BOLD, 14), Color.BLACK));
		
		String[] str = {"Ones","Twos","Threes","Fours","Fives","Sixes",
						"Three Kind","Four Kind","Full House","Small Straight",
						"Large Straight","Yahtzee","Chance"};
		
		rolls = new JButton[13];
		scores = new JTextField[13];
		for (int i = 0; i < 13; i++)
		{
			rolls[i] = new JButton(str[i]);
			rolls[i].setFont(new Font("Consolas", Font.PLAIN, 12));
			scores[i] = new JTextField("",5);
			scores[i].setEnabled(false);
			
			if (i < 6)
			{
				btn1.add(rolls[i]);
				txt1.add(scores[i]);
			}
			else
			{
				btn2.add(rolls[i]);
				txt2.add(scores[i]);
			}
		}
		
		upperLevel.add(btn1);
		upperLevel.add(txt1);
		lowerLevel.add(btn2);
		lowerLevel.add(txt2);
		
		add(upperLevel);
		add(lowerLevel);
		
		points = new JTextField[5];
		for (int i = 0; i < 5; i++)
		{
			points[i] = new JTextField("", 5);
			points[i].setEnabled(false);
		}
		
		String[] str2 = {"Upper Level:", "Bonus:", "Lower Level:", "Bonus:", "Total:"};
		for (int i = 0; i < 5; i++)
		{
			JLabel btn = new JLabel(str2[i]);
			btn.setFont(new Font("Consolas", Font.PLAIN, 12));
			btn3.add(btn);
			txt3.add(points[i]);
		}
		pointsPanel.add(btn3); pointsPanel.add(txt3);
		add(pointsPanel);
	}
	
	public JTextField[] getText(){
		return scores;
	}
	public JButton getButton(int i){
		return rolls[i];
	}
	
	public void setPoints(boolean[] canSelect)
	{
		for (int i = 0; i < 5; i++)
			points[i].setText("0");
		
		int total = 0;
		for (int i = 0; i < 6; i++)
			if (!canSelect[i]) //pointVal is permanent
				total += Integer.parseInt(scores[i].getText());
		points[0].setText(total + "");
		if (total >= 63)
			points[1].setText("35");
		
		total = 0;
		for (int i = 6; i < 13; i++)
			if (!canSelect[i])
				total += Integer.parseInt(scores[i].getText());
		points[2].setText(total + "");
		/*
		points[4].setText((Integer.parseInt(points[0].getText()) + Integer.parseInt(points[1].getText()) +
				Integer.parseInt(points[2].getText()) + Integer.parseInt(points[3].getText()) + 
				Integer.parseInt(points[4].getText())) + "");
				*/	
	}
	public void setText(int[] values, boolean[] canSelect)
	{
		for (int i = 0; i < 13; i++)
			if (canSelect[i])
				scores[i].setText(values[i] + "");
	}
}