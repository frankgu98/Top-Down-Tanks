import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.MouseInfo;
import java.io.*;
//approximate hitbox, if turned, hitmox can still stay the same
public class TankGame extends JFrame implements ActionListener{
	javax.swing.Timer myTimer;   
	GamePanel game;
		
    public TankGame() {
		super("Tank Stuff");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200,850);

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

class GamePanel extends JPanel implements KeyListener{
	private boolean []keys;

	private Tank p;
	private Image back;
	private TankGame mainFrame;
	private Image tankPic;
	private ArrayList<Bullet> bullets;
	private ArrayList<Wall> walls;
	private Wall wall1;
	
	public GamePanel(TankGame m){
		keys = new boolean[KeyEvent.KEY_LAST+1];
		bullets=new ArrayList<Bullet>();
		walls=new ArrayList<Wall>();
		mainFrame = m;    
		setSize(1200,800);
        addKeyListener(this);
        p=new Tank(170,170,39,39,0,4,new ImageIcon("tank1.png").getImage());
        //read walls from text file
        Scanner infile=null;
    	try{
    		infile=new Scanner(new File("map1.txt"));
    	}
    	catch(IOException ex){
    		System.out.println("That file apparently doesn't exist");
    	}
    	int n=Integer.parseInt(infile.nextLine());
    	for (int i=0;i<n;i++){
    		walls.add(new Wall(infile.nextLine()));
    	}

        
        
        //setFocusable(true);
        //requestFocus();
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
				System.out.println("hi");
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
    	for (Wall w:walls){
			w.collide(p);
		}
    }
	
	public void pactions(){
		p.delayer();
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
		//even with checking going left before checking going right, you still can't shoot while moving forwards and turning left
		//you can also turn both ways going backwards and shoot
		//works when shooting isn't spacebar......
		if(keys[KeyEvent.VK_A]){
			p.turn(-3);
		}
		if(keys[KeyEvent.VK_D]){
			p.turn(3);//3
		}
		if(keys[KeyEvent.VK_SPACE]&&p.getDelay()==0){
			bullets.add(p.shoot());
		}
		p.move();
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