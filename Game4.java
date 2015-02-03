import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MouseInfo;

public class Game4 extends JFrame implements ActionListener{
	Timer myTimer;   
	GamePanel game;
		
    public Game4() {
		super("Move the Box");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,650);

		myTimer = new Timer(10, this);	 // trigger every 10 ms
		

		game = new GamePanel(this);
		add(game);

		setResizable(false);
		setVisible(true);
    }
	
	public void start(){
		myTimer.start();
	}

	public void actionPerformed(ActionEvent evt){
		game.move();
		game.chase();
		game.repaint();
	}

    public static void main(String[] arguments) {
		Game4 frame = new Game4();		
    }
}

class GamePanel extends JPanel implements KeyListener{
	private int boxx,boxy,chasex,chasey;
	private boolean []keys;
	private Image back;
	private Game4 mainFrame;
	
	public GamePanel(Game4 m){
		keys = new boolean[KeyEvent.KEY_LAST+1];
		back = new ImageIcon("OuterSpace.jpg").getImage();
		mainFrame = m;
	    boxx = 170;
        boxy = 170;
        chasex = 270;
        chasey = 270;
		setSize(800,600);
        addKeyListener(this);
	}
	
    public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
    
    public void chase(){
    	if (boxx-chasex>0){
    		chasex+=3;
    	}
    	else if (boxx-chasex<0){
    		chasex-=3;
    	}
    	if (boxy-chasey>0){
    		chasey+=3;
    	}
    	else if (boxy-chasey<0){
    		chasey-=3;
    	}
    }
	
	public void move(){
		if(keys[KeyEvent.VK_RIGHT] ){
			boxx += 5;
		}
		if(keys[KeyEvent.VK_LEFT] ){
			boxx -= 5;
		}
		if(keys[KeyEvent.VK_UP] ){
			boxy -= 5;
		}
		if(keys[KeyEvent.VK_DOWN] ){
			boxy += 5;
		}
		
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		Point offset = getLocationOnScreen();
		System.out.println("("+(mouse.x-offset.x)+", "+(mouse.y-offset.y)+")");
	}
	
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    
    public void paintComponent(Graphics g){ 	
    	g.drawImage(back,0,0,null); 
		g.setColor(Color.blue);  
		g.fillRect(boxx,boxy,40,40);
		g.setColor(Color.red);
		g.fillRect(chasex,chasey,40,40);
    }
}