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
		setSize(1205,950);

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
		game.pactions();
		game.bulPhysics();
		game.tankPhysics();
		game.repaint();
	}

    public static void main(String[] arguments) {
		TankGame frame = new TankGame();		
    }
}

//for death: show explosion, wait a while, switch maps
class GamePanel extends JPanel implements KeyListener{
	private boolean[] keys;
	private int[] ppos,epos;//player position and enemy position
	private boolean playing;
	private int pwins,ewins,mapcount;
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
        pwins=0;
    	ewins=0;
    	mapcount=1;
    	newLevel();
        //read walls and tank information from text files

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
	    		System.out.println("Something wen tmore wrong than usual");
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
    		//stuff broke here
	    	if (p.bulCollide(bul)){
				playing=false;
			}
			if (e.bulCollide(bul)){
				playing=false;
			}
			for (Wall w:walls){
				if (w.collide(bul)){
					w.bounceOff(bul);
				}
			}
			bul.move();
			if (bul.getTravelled()>2999){
    			bullets.remove(i);
    		}
    	}
    }
    
    public void tankPhysics(){
    	p.move();
		e.move();
    	for (Wall w:walls){
			w.collide(p);
			w.collide(e);
		}
    }
	
	public void pactions(){
		p.delayer();
		e.delayer();
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
	
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    
    public void paintComponent(Graphics g){
    	//g.drawImage(back,0,0,null);  	
    	g.setColor(new Color(230,230,230)); 
    	g.fillRect(0,0,getWidth(),getHeight());
		g.setColor(Color.blue);  
		p.drawTank(g);
		e.drawTank(g);
		g.setColor(new Color(77,77,77));
		for (Wall w:walls){
			w.drawRect(g);
		}
		g.setColor(Color.black);  
		for (Bullet bul:bullets){
			bul.draw(g);

		}
    }
}