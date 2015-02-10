/**
 * @(#)TankGame.java
 *
 * @Frank Gu
 *	TankGame class that opens a container for the game in GamePanel to be played
 *
 * @version 1.00 2015/1/14
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.applet.*;
import javax.sound.sampled.*;
public class TankGame extends JFrame implements ActionListener{
	javax.swing.Timer myTimer;//timer that triggers an Action after a set amount of ms (to redraw everything)
	GamePanel game;
	
	//contructor takes in nothing since the game window will be constant no matter how many times you play
    public TankGame() {
		super("Tank Stuff");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1205,928);
		myTimer = new javax.swing.Timer(10, this);// trigger every 10 ms
		game = new GamePanel(this);
		add(game);
		setResizable(false);
		setVisible(true);
    }

	public void start(){
		myTimer.start();
	}
	
	//every Action(the only one here being the timer triggering an Action)
	public void actionPerformed(ActionEvent evt){
		game.menuFunctions();
		game.gameFunctions();
		game.controlsFunctions();
		game.repaint();
	}

    public static void main(String[] arguments) {
		TankGame frame = new TankGame();		
    }
}

class GamePanel extends JPanel implements KeyListener,MouseListener{
	//constants describing what screen the player is currently on
	public static final int MENU=0;
	public static final int GAME=1;
	public static final int CONTROLS=2;

	private int screen,clickx,clicky;//screen user is on,x coord of where player clicked,y coord of where player clicked
	private int pwins,ewins,mapcount;//how many wins p has,how many wins e has,how many maps are available to play
	private long pausestarttime;//when the pause started (pausing happens between maps)
	private boolean[] keys;//Array of which keys are being pressed
	private boolean playing,muted;//if the players are playing,if the music is muted
	AudioClip sax;//the music
	private Image menuPic,controlsPic;//picture of the menu,picture of the controls
	private FButton start,controls,quit,back,mute;//all the FButtons the user can use
	//start starts the game,controls brings you to the controls screen,quit closes the frame,back goes from game or controls to the menu,mute mutes or unmutes the music
	
	private Tank p,e;//the 2 Tanks in the game, stands for player and enemy (even though both are player controlled)
	private TankGame mainFrame;
	private ArrayList<Bullet> bullets;
	private ArrayList<Wall> walls;
	
	public GamePanel(TankGame m){
		keys = new boolean[KeyEvent.KEY_LAST+1];
		bullets=new ArrayList<Bullet>();
		walls=new ArrayList<Wall>();
		mainFrame = m;
		setSize(1200,900);//size is 1200x900 but all maps end at 1200x800, the last 100 pixels are for score and FButtons
        addKeyListener(this);
		addMouseListener(this);
		//starts the click position where no Button is
		clickx=-1;
		clicky=-1;
		screen=MENU;//starts the user in the menu
    	start=new FButton(386,370,430,47,0,"START",30);
    	controls=new FButton(386,458,430,47,0,"CONTROLS",30);
    	quit=new FButton(387,548,430,47,0,"QUIT",30);
    	back=new FButton(502,810,200,47,0,"BACK",30);
    	mute=new FButton(575,868,48,30,25,"MUTE",12);
    	//the back and mute buttons don't exist in the menu
    	back.setLock(true);
    	mute.setLock(true);
    	//tries to load the song and menu and controls pictures
    	try{
	    	sax = Applet.newAudioClip(getClass().getResource("Yakety_Sax.wav"));
	    	menuPic=new ImageIcon("images/menu.png").getImage();
	    	controlsPic=new ImageIcon("images/controls.png").getImage();
    	}
    	catch(NullPointerException ex){
    		System.out.println("Don't touch the resources");
    		System.exit(0);
    	}
    	catch(Exception ex){
    		System.out.println("Something went more wrong than usual");
    		System.exit(0);
    	}
    	playing=false;
        muted=false;
        pwins=0;
    	ewins=0;
    	mapcount=5;
	}
	
	public void mousePressed(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseClicked(MouseEvent e){
		clickx=e.getX();
		clicky=e.getY();
	}
	
	//resetClick so that Buttons don't get repeatedly clicked when the user only clicked them once
	public void resetClick(){
		clickx=-1;
		clicky=-1;
	}
	
	//deals with everything you can do in the menu
	public void menuFunctions(){
		//only does things if the screen the user is on is the menu
		if(screen==MENU){
			//if the user clicks the start button, the game starts
			if (start.collide(clickx,clicky)){
				screen=GAME;
				start.setLock(true);
				controls.setLock(true);
				quit.setLock(true);
				back.setLock(false);
				mute.setLock(false);
				//first initializing needs newLevel since checkEnded is the last function that's called when playing but is the only other map loading function,
				//so players would attempt to move before they existed
				newLevel();
				playing=true;
				sax.loop();//plays music upon starting the actual playing
				resetClick();
			}
			//if the user clicks the controls button, they are brought to the controls
			if (controls.collide(clickx,clicky)){
				screen=CONTROLS;
				start.setLock(true);
				controls.setLock(true);
				quit.setLock(true);
				back.setLock(false);
				resetClick();
			}
			//if the user clicks quit, the everything stops running
			if (quit.collide(clickx,clicky)){
				System.exit(0);
			}
		}
	}
	
	//deals with everything you can do in the controls screen
	public void controlsFunctions(){
		//only does things if the screen the user is on is the controls
		if(screen==CONTROLS){
			//if the user clicks back, they are brought back to the menu
			if (back.collide(clickx,clicky)){
				screen=MENU;
				start.setLock(false);
				controls.setLock(false);
				quit.setLock(false);
				back.setLock(true);
				resetClick();
			}
		}
	}
	
	//deals with everything you can do in the game screen
	public void gameFunctions(){
		//only does things if the screen the user is on is the game
		if(screen==GAME){
			//if the user clicks back, they are brought back to the menu and the score is reset
			if (back.collide(clickx,clicky)){
				pwins=0;
				ewins=0;
				screen=MENU;
				start.setLock(false);
				controls.setLock(false);
				quit.setLock(false);
				back.setLock(true);
				mute.setLock(true);
				sax.stop();
				resetClick();
			}
			//if the user clicks mute, the music is stopped
			if(mute.collide(clickx,clicky)){
				if(muted){
					muted=false;
					sax.loop();
				}
				else{
					muted=true;
					sax.stop();
				}
				resetClick();
			}
			playerActions();//lets players control Tanks
			bulPhysics();//makes the Bullets follow the game's physics
			tankPhysics();//makes the Tanks follow the game's physics
			checkEnded();//lets the game end and switch maps
			//checkEnded comes last since a player's last input used to be able to slip though the map switch but since check ended has bullets.clear() that no longer happens
			//ex. if a player held shoot while in the transition period, a bullet would come out upon entering the new map
		}
	}
	
	//checks if a the players are still playing (if no one died yet) and stops the game and switches levels if one has
	public void checkEnded(){
		if (!playing){
			//so the player doesn't continue gliding after death (can be removed for moving explosions which look pretty cool)
			p.setMoving(0);
			e.setMoving(0);
			//if the pause time has been longer than 2 seconds, the level is swwitched and the players can play again
			if(pausestarttime+2000<=System.currentTimeMillis()){
				newLevel();
				playing=true;
			}
		}
	}
		
	//loads a new level
	public void newLevel(){
		walls.clear();//so you don't put a map onto another map
		//read walls and tank information from text files
		Scanner infile=null;
    	try{
    		infile=new Scanner(new File("maps/map"+(int)(mapcount*Math.random())+".txt"));	
			p=new Tank(infile.nextLine());
	    	e=new Tank(infile.nextLine());
	    	int n=Integer.parseInt(infile.nextLine());
	    	for (int i=0;i<n;i++){
	    		walls.add(new Wall(infile.nextLine()));
			}
    	}
    	catch(NullPointerException ex){
    		System.out.println("Don't touch the resources");
    		System.exit(0);
    	}
    	catch(IOException ex){
    		System.out.println("Don't mess with the maps");
    		System.exit(0);
    	}
    	catch(Exception ex){
    		System.out.println("Something went more wrong than usual");
    		System.exit(0);
    	}
	}
	
    public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
   
    public void bulPhysics(){
    	for (int i=bullets.size()-1; i> -1; i--){//needed to unmess up arraylist
    		Bullet bul=bullets.get(i);
	    	if (p.bulCollide(bul)){
	    		p.setDying(true);
	    		ewins++;
	    		pausestarttime=System.currentTimeMillis();
	    		bullets.clear();
				playing=false;
				break;
			}
			if (e.bulCollide(bul)){
				e.setDying(true);
				pwins++;
				pausestarttime=System.currentTimeMillis();
				bullets.clear();
				playing=false;
				break;
			}
			for (Wall w:walls){
				if (w.collide(bul)){
					w.bounceOff(bul);
				}
			}
			bul.move();
			if (bul.getTravelled()>4999){
    			bullets.remove(i);
    		}
    	}
    }
    
    public void tankPhysics(){
    	p.delayer();
		e.delayer();
    	p.move();
		e.move();
    	for (Wall w:walls){
			w.collide(p);
			w.collide(e);
		}
    }
	
	public void playerActions(){
		
		if(playing){
			
			//has it so the tank only actually "moves" when pressing the up or down keys
			if(keys[KeyEvent.VK_W]){
				p.setMoving(1);
			}
			else if(keys[KeyEvent.VK_S]){
				p.setMoving(-1);
			}
			else{
				p.setMoving(0);
			}
			if(keys[KeyEvent.VK_A]){
				p.turn(-3);
			}
			if(keys[KeyEvent.VK_D]){
				p.turn(3);//3
			}
			if(keys[KeyEvent.VK_V]&&p.getDelay()==0){
				bullets.add(p.shoot());
			}
			
			if(keys[KeyEvent.VK_UP]){
				e.setMoving(1);
			}
			else if(keys[KeyEvent.VK_DOWN]){
				e.setMoving(-1);
			}
			else{
				e.setMoving(0);
			}
			if(keys[KeyEvent.VK_LEFT]){ 
				e.turn(-3);
			}
			if(keys[KeyEvent.VK_RIGHT]){
				e.turn(3);//3
			}
			if(keys[KeyEvent.VK_P]&&e.getDelay()==0){
				bullets.add(e.shoot());
			}
		}
	}
	
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    
    public void drawScore(Graphics g){
		Font f = new Font("D-Day Stencil", Font.BOLD, 30);
		g.setFont(f);
    	g.drawImage(p.getPic(),85,840,null);
    	g.drawString(Integer.toString(pwins),140,870);
    	g.drawImage(e.getPic(),1000,840,null);
    	g.drawString(Integer.toString(ewins),1055,870);
    }
    
    public void paintComponent(Graphics g){
    	if(screen==MENU){
    		g.drawImage(menuPic,0,0,null);	
    	}	
    	if(screen==GAME){
	    	g.setColor(new Color(230,230,230));
	    	g.fillRect(0,0,getWidth(),getHeight());
			g.setColor(Color.blue); 
	
			g.setColor(new Color(77,77,77));
			for (Wall w:walls){
				w.drawRect(g);
			}
			g.setColor(Color.black);  
			for (Bullet bul:bullets){
				bul.draw(g);
			}
			p.drawTank(g);
			e.drawTank(g);
			drawScore(g);
	    }
	    if(screen==CONTROLS){
	    	g.drawImage(controlsPic,0,0,null);	
	    }
	    start.drawButt(g);
    	controls.drawButt(g);
    	quit.drawButt(g);
    	back.drawButt(g);
    	mute.drawButt(g);
    }
}