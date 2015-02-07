import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.MouseInfo;
import java.io.*;
//approximate hitbox, if turned, hitbox can still stay the same
//add powerups if time
public class TankGame extends JFrame implements ActionListener{
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
	
	public void pause(){
		
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
	private boolean[] keys;
	private int[] ppos,epos;//player position and enemy position
	private int screen,mousex,mousey;
	private boolean playing,actionsenabled;
	private int pwins,ewins,mapcount;
	private Image menuPic;
	private FButton start, controls, quit;
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

    	start=new FButton(386,370,430,47,"START");
    	controls=new FButton(386,458,430,47,"CONTROLS");
    	quit=new FButton(387,548,430,47,"QUIT");
    	
    	menuPic=new ImageIcon("menu.png").getImage();
    	playing=false;
        actionsenabled=false;
        pwins=0;
    	ewins=0;
    	mapcount=1;
    	screen=MENU;
        //read walls and tank information from text files
	}
	
	public void mousePressed(MouseEvent e){
		mousex=e.getX();
		mousey=e.getY();	
	}
	public void mouseMoved(MouseEvent e){}
	public void mouseDragged(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	
	public void menuFunctions(){
		if(screen==MENU){
			if (start.collide(mousex,mousey)){
				screen=GAME;
				start.setLock(true);
				controls.setLock(true);
				quit.setLock(true);
			}
			if (controls.collide(mousex,mousey)){
				
			}
			if (quit.collide(mousex,mousey)){
				System.exit(0);
			}
		}
	}
	
	public void playingFunctions(){
		if(screen==GAME){
			System.out.println(actionsenabled);
			checkEnded();
			playerActions();
			bulPhysics();
			tankPhysics();
		}
	}
	
	public void checkEnded(){
		if (!playing){
			actionsenabled=false;
			clearLevel();
			newLevel();
			playing=true;
			actionsenabled=true;
		}
	}
	
	public void clearLevel(){
		bullets.clear();
		walls.clear();
	}
		
	public void newLevel(){
		Scanner infile=null;
		while (infile==null){
	    	try{
	    		infile=new Scanner(new File("map"+(int)(mapcount*Math.random())+".txt"));
	    	}
	    	catch(IOException ex){
	    		System.out.println("Don't mess up the maps");
	    	}
	    	catch(Exception ex){
	    		System.out.println("Something went more wrong than usual");
	    	}
	    	
		}
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
	    		ewins++;
				playing=false;
			}
			if (e.bulCollide(bul)){
				pwins++;
				playing=false;
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
    	g.drawImage(p.getPic(),35,840,null);
    	g.drawString(Integer.toString(pwins),90,870);
    	g.drawImage(e.getPic(),1000,840,null);
    	g.drawString(Integer.toString(ewins),1055,870);
    }
    
    public void paintComponent(Graphics g){
    	//g.drawImage(back,0,0,null);
    	if(screen==MENU){
    		g.drawImage(menuPic,0,0,null);
    		start.drawButt(g);
    		controls.drawButt(g);
    		quit.drawButt(g);
    	}
    	
    	if(screen==GAME){
	    	g.setColor(new Color(230,230,230)); 
	    	g.fillRect(0,0,getWidth(),getHeight());
			g.setColor(Color.blue); 
			if (playing){
				p.drawTank(g);
				e.drawTank(g);
			} 
	
			g.setColor(new Color(77,77,77));
			for (Wall w:walls){
				w.drawRect(g);
			}
			g.setColor(Color.black);  
			for (Bullet bul:bullets){
				bul.draw(g);
			}
			drawScore(g);
	    }
    }
}