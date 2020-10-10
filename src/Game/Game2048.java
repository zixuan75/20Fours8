package Game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BorderLayout;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
public class Game2048 extends JPanel {

	enum State {
		start, won, running, over
	}    
	final Color[] colorTable = {
			new Color(255, 255, 255), new Color(0, 0, 0), new Color(0xFF00FF), // 1
			new Color(0, 0, 255)/* 3*/, new Color(255, 255, 255)/* 4*/, new Color(255, 255, 255),// 8
			new Color(255, 255, 255)/*16*/, new Color(237, 207, 114), new Color(237, 204, 97), 
			new Color(237, 200, 80), new Color(237, 197, 63), new Color(237, 194, 46)
			};


	final static int target = 512;
	final static Integer MAX_SPAWNS = 1000000000;

	static int highest;
	static int score;

	private Color gridColor = new Color(0xBBADA0);
	private Color emptyColor = new Color(0xCDC1B4);
	private Color startColor = new Color(0xFFEBCD);

	private Random rand = new Random();

	private Tile[][] tiles;
	private List<Integer> spawns;
	private int side = 4;
	private State gamestate = State.start;
	private boolean checkingAvailableMoves;

	public Game2048() {
		setPreferredSize(new Dimension(900, 700));
		setBackground(new Color(0xFAF8EF));
		setFont(new Font("SansSerif", Font.BOLD, 20));
		setFocusable(true);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				startGame();
				repaint();
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					moveUp();
					break;
				case KeyEvent.VK_DOWN:
					moveDown();
					break;
				case KeyEvent.VK_LEFT:
					moveLeft();
					break;
				case KeyEvent.VK_RIGHT:
					moveRight();
					break;
				}
				repaint();
			}
		});
	}

	@Override
	public void paintComponent(Graphics gg) {
		super.paintComponent(gg);
		Graphics2D g = (Graphics2D) gg;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		drawGrid(g);
	}

	void startGame() {
		if (gamestate != State.running) {
			score = 0;
			highest = 0;
			gamestate = State.running;
			tiles = new Tile[side][side];
			spawns = new ArrayList<Integer>();
			addRandomTile(true);
			addRandomTile(true);
		}
	}

	void drawGrid(Graphics2D g) {
		g.setColor(gridColor);
		g.fillRoundRect(200, 100, 499, 499, 15, 15);

		if (gamestate == State.running) {

			for (int r = 0; r < side; r++) {
				for (int c = 0; c < side; c++) {
					if (tiles[r][c] == null) {
						g.setColor(emptyColor);
						g.fillRoundRect(215 + c * 121, 115 + r * 121, 106, 106, 7, 7);
					} else {
						drawTile(g, r, c);
					}
				}
			}
			if (!movesAvailable()) {
				gamestate = State.over;
			}
			if (highest == target) {
				gamestate = State.won;
			}
		} else {
			g.setColor(startColor);
			g.fillRoundRect(215, 115, 469, 469, 7, 7);

			g.setColor(gridColor.darker());
			g.setFont(new Font("SansSerif", Font.BOLD, 128));
			g.drawString("2048", 310, 270);

			g.setFont(new Font("SansSerif", Font.BOLD, 20));

			if (gamestate == State.won) {
				g.drawString("you made it!", 390, 350);

			} else if (gamestate == State.over) {
				g.drawString("Game over!", 400, 350);
				g.drawString("Score: " + score, 400, 380);
			}

			g.setColor(gridColor);
			g.drawString("click to start a new game", 330, 470);
			g.drawString("(use arrow keys to move tiles)", 310, 530);
		}
	}

	void drawTile(Graphics2D g, int r, int c) {
		int value = tiles[r][c].getValue();

		if (value >= 4) { 
			g.setColor(colorTable[(int) (Math.log(value) / Math.log(2)) + 2]);
		} else if (value == 3){
			g.setColor(colorTable[2]);
		} else if (value == 1){
			g.setColor(colorTable[3]);
		}
		g.fillRoundRect(215 + c * 121, 115 + r * 121, 106, 106, 7, 7);
		String s = String.valueOf(value);

		g.setColor(value < 4 ? colorTable[0] : colorTable[1]);

		FontMetrics fm = g.getFontMetrics();
		int asc = fm.getAscent();
		int dec = fm.getDescent();

		int x = 215 + c * 121 + (106 - fm.stringWidth(s)) / 2;
		int y = 115 + r * 121 + (asc + (106 - (asc + dec)) / 2);

		g.drawString(s, x, y);
	}
 
    private void addRandomTile(boolean firstSpawn) {
        int pos = rand.nextInt(side * side);
        int row, col;
        do {
            pos = (pos + 1) % (side * side);
            row = pos / side;
            col = pos % side;
        } while (tiles[row][col] != null);
        int val;
        int num = rand.nextInt(4);
        if (num == 0)
        	val = 1;
        else if (num == 1)
        	val = 3;
        else
        	val = 4;
        int numSpawns = spawns.size();
//        if (numSpawns > 5) {
//        	if (3 == spawns.get(numSpawns - 1)
//        			&& 3 == spawns.get(numSpawns - 2)
//        			&& 3 == spawns.get(numSpawns - 3)) {
//            	val = rand.nextInt(2) == 0 ? 1 : 4;
//            } else if (1 == spawns.get(numSpawns - 1)
//        			&& 1 == spawns.get(numSpawns - 2)
//        			&& 1 == spawns.get(numSpawns - 3)) {
//            	val = rand.nextInt(2) == 0 ? 3 : 4;
//            } 
//        	
//        }
        int numThrees = 0, numOnes = 0;
        for (int i = 0; i < side; i++) {
        	for (int j = 0; j < side; j++) {
        		if (tiles[i][j] != null) {
        			if (3 == tiles[i][j].getValue())
        				numThrees++;
        			if (1 == tiles[i][j].getValue())
        				numOnes++;
        		}
        	}
        }
        if (numThrees > 3) {
        	val = rand.nextInt(2) == 0 ? 1 : 4;
        }
        if (numOnes > 3) {
        	val = rand.nextInt(2) == 0 ? 3 : 4;
        }
        spawns.add(val);
        tiles[row][col] = new Tile(val);
    }
 
    private boolean move(int countDownFrom, int yIncr, int xIncr) {
        boolean moved = false;
 
        for (int i = 0; i < side * side; i++) {
            int j = Math.abs(countDownFrom - i);
 
            int r = j / side;
            int c = j % side;
 
            if (tiles[r][c] == null)
                continue;
 
            int nextR = r + yIncr;
            int nextC = c + xIncr;
 
            while (nextR >= 0 && nextR < side && nextC >= 0 && nextC < side) {
 
                Tile next = tiles[nextR][nextC];
                Tile curr = tiles[r][c];
 
                if (next == null) {
 
                    if (checkingAvailableMoves)
                        return true;
 
                    tiles[nextR][nextC] = curr;
                    tiles[r][c] = null;
                    r = nextR;
                    c = nextC;
                    nextR += yIncr;
                    nextC += xIncr;
                    moved = true;
 
                } else if (next.canMergeWith(curr)) {
 
                    if (checkingAvailableMoves)
                        return true;
 
                    int value = next.mergeWith(curr);
                    if (value > highest)
                        highest = value;
                    score += value;
                    tiles[r][c] = null;
                    moved = true;
                    break;
                } else
                    break;
            }
        }
 
        if (moved) {
            if (highest < target) {
                clearMerged();
                addRandomTile(false);
            }
        }
 
        return moved;
    }
 
    boolean moveUp() {
        return move(0, -1, 0);
    }
 
    boolean moveDown() {
        return move(side * side - 1, 1, 0);
    }
 
    boolean moveLeft() {
        return move(0, 0, -1);
    }
 
    boolean moveRight() {
        return move(side * side - 1, 0, 1);
    }
 
    void clearMerged() {
        for (Tile[] row : tiles)
            for (Tile tile : row)
                if (tile != null)
                    tile.setMerged(false);
    }
 
    boolean movesAvailable() {
        checkingAvailableMoves = true;
        boolean hasMoves = moveUp() || moveDown() || moveLeft() || moveRight();
        checkingAvailableMoves = false;
        return hasMoves;
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setTitle("2048");
            f.setResizable(true);
            f.add(new Game2048(), BorderLayout.CENTER);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
 
class Tile {
    private boolean merged;
    private int value;
 
    Tile(int val) {
        value = val;
    }
 
    int getValue() {
        return value;
    }
 
    void setMerged(boolean m) {
        merged = m;
    }
 
    boolean canMergeWith(Tile other) {
    	if (value == 1) {
    		return !merged && other != null && !other.merged && 3 == other.getValue();
    	} else if (value == 3) {
    		return !merged && other != null && !other.merged && 1 == other.getValue();
    	}
        return !merged && other != null && !other.merged && value == other.getValue();
    } 
 
    int mergeWith(Tile other) {
        if (canMergeWith(other)) {
            value += other.getValue();
            merged = true;
            return value;
        }
        return -1;
    }
}
