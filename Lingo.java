import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

public class Lingo extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<String> wordList = new ArrayList<>();
	
	private String randomWord = "";
	private int guess = 0;
	private int games = 0;
	
	private HashMap<Character, Integer> countLetters = new HashMap<Character, Integer>();
	
	private HashMap<Integer, Character> guessedLetters = new HashMap<Integer, Character>();	

	private JTextField[] fields = new JTextField[25];
	private JPanel p = new JPanel(new BorderLayout());
	private JPanel f = new JPanel(new GridLayout(5,5));
	private JPanel b = new JPanel(new FlowLayout());
	private JTextField input;
	private JButton send;

	Lingo() {
		newGame();
	}
	
	public void createPanel() {
		add(p);	
		add(f, BorderLayout.CENTER);
		add(b, BorderLayout.SOUTH);
		setSize(500, 500);
		setTitle("Lingo");
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void createFields() {
		for(int i=0; i<fields.length; i++) {
			fields[i] = new JTextField();
			fields[i].setHorizontalAlignment(JTextField.CENTER);
			fields[i].setFont(new Font("SansSerif", Font.BOLD, 24));
			fields[i].setEditable(false);
			f.add(fields[i]);
		}
		
		input = new JTextField("", 10); b.add(input);
		send = new JButton("Send"); b.add(send);
		
		input.setFont(new Font("SansSerif", Font.BOLD, 24));
		
		input.addActionListener(this);
		send.addActionListener(this);
		
	}
	
	public void guess(String word) throws IOException {
		if(guess == 5) {
			JOptionPane.showMessageDialog(this, "Het woord is niet geraden :(");		
			games++;
			newGame();
		} 
		else if(isValidWord(word)) {
			setWordOnScreen(word);
			guess++;
			if(guessed(word)) {
				guess = 5;
				JOptionPane.showMessageDialog(this, "Je hebt het woord geraden!");
				games++;
				newGame();
			}
		}
	}
	
	public void setColors() {
		b.setBackground(new Color(0,153,255));
		f.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		
		for(int i=0; i<fields.length; i++) {
		fields[i].setBackground(new Color(0,153,255));
		fields[i].setBorder(BorderFactory.createLineBorder(Color.WHITE));
		}
	}
	
	public void addWords() throws IOException {
		File wordsFile = new File("lingo.txt");
		Scanner sc = new Scanner(wordsFile);
		while(sc.hasNext()) {
			wordList.add(sc.next());
		}
		sc.close();
	}
	
	public boolean isValidWord(String word) throws IOException {
		if(word.length() < 5 || word.length() > 5) {
			input.setBackground(Color.red);
			return false;
		}
		if(!inDictionary(word)) {
			input.setBackground(Color.red);
			return false;
		}
		input.setBackground(Color.white);
		input.setText("");
		return true;
	}
	
	public boolean inDictionary(String word) {
		for(String s: wordList) {
			if(s.equals(word)) {
				//System.out.println("In dictionary!");
				return true;
			}
		}
		//System.out.println("Not in dictionary");
		return false;
	}
	
	public void setRandomWord() {
		int random = (int)(Math.random() * wordList.size());
		randomWord = wordList.get(random);
		System.out.println(randomWord);
		setGuessedLetter(0,randomWord.charAt(0));
		putGuessedLetters();
		countLetters();
	}
	
	public void setWordOnScreen(String word) {
		putGuessedLetters();
		for(int i=0; i<randomWord.length(); i++) {
			int pos = i+(guess*5);
			
			char letter = word.charAt(i);
			
			fields[pos].setBackground(new Color(0,153,255));
			fields[pos].setText(""+letter);
			
			if(containsLetter(letter)) {
				if(letterCorrectSpot(letter, i)) {
					setLetterValue(letter, -1);
					setGuessedLetter(i,letter);
					fields[pos].setBackground(Color.orange);
					fields[pos].setText(""+letter);
				} else {
					setLetterValue(letter, -1);
					fields[pos].setBackground(Color.yellow);
					fields[pos].setText(""+letter);
				}
			}
	
		}
		
		countLetters();
		
		
	}
	
	public boolean containsLetter(char letter) {
			if(randomWord.contains(""+letter) && getLetterValue(letter) > 0) {
				return true;
			}
		return false;
	}
	
	public boolean letterCorrectSpot(char letter, int at) {
			if(randomWord.charAt(at) == letter && getLetterValue(letter) > 0) {
				return true;
			}
		return false;
	}
	
	public void setGuessedLetter(int at, char letter) {
		guessedLetters.put(at, letter);
	}
	
	public void putGuessedLetters() {
		for (Map.Entry<Integer, Character> entry : guessedLetters.entrySet()) {
		    int key = entry.getKey();
		    char letter = entry.getValue();
		    
		    		int pos = key+(guess*5);
				fields[pos].setBackground(new Color(0,153,255));
				fields[pos].setText(""+letter);
			
		}
	}
	
	public int getLetterValue(char letter) {	
		return countLetters.get(letter);
	}
	public void setLetterValue(char letter, int value) {
		if(value == 1) {
		countLetters.put(letter, countLetters.get(letter) + 1);
		} else {
		countLetters.put(letter, countLetters.get(letter) - 1);		
		}
	}
	public void countLetters() {
		int count = 0;
		for (char c = 'a'; c <= 'z'; c++) {
			if(randomWord.contains(""+c)) {
				for(int i=0;i<randomWord.length();i++) {
					if(randomWord.charAt(i) == c) {
						count++;
					}
				}

			}
			countLetters.put(c, count);
			//System.out.println(c+" "+count);
			count=0;
		}
		
	}
	
	public boolean guessed(String word) {
		if(randomWord.equals(word)) {
			return true;
		}
		return false;
	}
	
	public void newGame() {
		if(games == 0) {
		createFields();
		} else {
			guess = 0;
			resetFields();
		}
		createPanel();
		setColors();
		guessedLetters.clear();
		countLetters.clear();
		
		try {
			addWords();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setRandomWord();
	}
	
	public void resetFields() {
		for(int i=0; i<fields.length; i++) {
			fields[i].setText("");
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	
		if(e.getSource() == send) {
			try {
				guess(input.getText());
				putGuessedLetters();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}	
}