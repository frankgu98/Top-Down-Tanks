import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.MouseInfo;
import java.io.*;
import java.applet.*;
import javax.sound.sampled.AudioSystem;
import java.net.URL;
import javax.sound.sampled.*;
//approximate hitbox, if turned, hitbox can still stay the same
//add powerups if time


//does he want the linked list assignment done when i'm back?



public class TankGame extends JFrame implements ActionListener{
	public static final int MENU=0;
	public static final int GAME=1;
	public static final int CONTROLS=2;
	javax.swing.Timer myTimer;   
	GamePanel game;
	
    public TankGame() {
		super("Tank Stuff");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1205,928);
		myTimer = new javax.swing.Timer(10, this);	 // trigger every 10 ms
		game = new GamePanel(this);
		
		add(game);
		setResizable(false);
		setVisible(true);
    }
	
	public void start(){
		myTimer.start();
	}

	public void actionPerformed(ActionEvent evt){
		game.menuFunctions();
		game.playingFunctions();
		game.repaint();
	}

    public static void main(String[] arguments) {
		TankGame frame = new TankGame();		
    }
}

//for death: show explosion, wait a while, switch maps
class GamePanel extends JPanel implements KeyListener,MouseMotionListener, MouseListener{
	public static final int MENU=0;
	public static final int GAME=1;
	public static final int CONTROLS=2;
	public int screen;
	private long pausestarttime;
	private boolean[] keys;
	private int clickx,clicky;
	private boolean playing,actionsenabled,muted;
	private int pwins,ewins,mapcount;
	AudioClip sax;
	private Image menuPic,controlsPic;
	private FButton start,controls,quit,back,mute;
	private Tank p,e;
	private TankGame mainFrame;
	private ArrayList<Bullet> bullets;
	private ArrayList<Wall> walls;
	
	public GamePanel(TankGame m){
		keys = new boolean[KeyEvent.KEY_LAST+1];
		bullets=new ArrayList<Bullet>();
		walls=new ArrayList<Wall>();
		mainFrame = m;
		setSize(1200,900);
        addKeyListener(this);
        addMouseMotionListener(this);
		addMouseListener(this);
		clickx=-1;
		clicky=-1;
		screen=MENU;
    	start=new FButton(386,370,430,47,0,"START",30);
    	controls=new FButton(386,458,430,47,0,"CONTROLS",30);
    	quit=new FButton(387,548,430,47,0,"QUIT",30);
    	back=new FButton(502,810,200,47,0,"BACK",30);
    	mute=new FButton(577,868,40,30,25,"MUTE",12);
    	back.setLock(true);
    	mute.setLock(true);
    	sax = Applet.newAudioClip(getClass().getResource("Yakety_Sax.wav"));
    	menuPic=new ImageIcon("menu.png").getImage();
    	controlsPic=new ImageIcon("controls.png").getImage();
    	playing=false;
        actionsenabled=false;
        muted=false;
        pwins=0;
    	ewins=0;
    	mapcount=4;
	}
	
	public void mousePressed(MouseEvent e){
		clickx=e.getX();
		clicky=e.getY();	
	}
	public void mouseMoved(MouseEvent e){}
	public void mouseDragged(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	
	public void resetClick(){
		clickx=-1;
		clicky=-1;
	}
	
	public void menuFunctions(){
		if(screen==MENU){
			if (start.collide(clickx,clicky)){
				screen=GAME;
				start.setLock(true);
				controls.setLock(true);
				quit.setLock(true);
				back.setLock(false);
				mute.setLock(false);
				//first initializing, needed since checkEnded is the last function that's called when playing but is the only other map loading function,
				//so players would attempt to move before they existed
				newLevel();
				playing=true;
				actionsenabled=true;
				sax.loop();//plays music upon starting the actual playing
				resetClick();
			}
			if (controls.collide(clickx,clicky)){
				screen=CONTROLS;
				start.setLock(true);
				controls.setLock(true);
				quit.setLock(true);
				back.setLock(false);
				resetClick();
			}
			if (quit.collide(clickx,clicky)){
				System.exit(0);
			}
		}
		if(screen==GAME){
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
			if(mute.collide(clickx,clicky)){//have mute stay the same over changing screens
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
		}
		if(screen==CONTROLS){
			if (back.collide(clickx,clicky)){
				screen=MENU;
				start.setLock(false);
				controls.setLock(false);
				quit.setLock(false);
				back.setLock(true);
			}
			resetClick();
		}
	}
	
	public void playingFunctions(){
		if(screen==GAME){
			playerActions();
			bulPhysics();
			tankPhysics();
			checkEnded();//checkEnded comes last since a player's last input used to be able to slip though the map switch
			//ex. if a player held shoot while in the transition period, a bullet would come out upon entering the new map
		}
	}
	
	public void checkEnded(){
		if (!playing){
			actionsenabled=false;
			p.setMoving(0);
			e.setMoving(0);
			if(pausestarttime+2000<=System.currentTimeMillis()){
				walls.clear();
				newLevel();
				actionsenabled=true;
				playing=true;
			}
		}
	}
		
	public void newLevel(){
		//read walls and tank information from text files
		Scanner infile=null;
    	try{
    		infile=new Scanner(new File("maps/map"+(int)(mapcount*Math.random())+".txt"));
    	}
    	catch(IOException ex){
    		System.out.println("Don't mess up the maps");
    		System.exit(0);
    	}
    	catch(Exception ex){
    		System.out.println("Something went more wrong than usual");
    		System.exit(0);
    	}
		walls.clear();
		p=new Tank(infile.nextLine());
	    e=new Tank(infile.nextLine());
	    int n=Integer.parseInt(infile.nextLine());
	    for (int i=0;i<n;i++){
	    	walls.add(new Wall(infile.nextLine()));
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
			if (bul.getTravelled()>3999){
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
		
		if(actionsenabled){
			
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
	    	g.setColor(new Color(230,230,230)); //230
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