/**
 * @(#)Wall.java
 *
 * @Frank Gu
 *	Wall class made to obstruct movement and bounce Bullets in TankGame
 *	It can draw itself, detect collision with Bullets and Tanks, and bounce Bullets
 * @version 1.00 2015/1/20
 */
import java.util.*;
import java.awt.*;

public class Wall {
	private double x,y,w,h;//x coord,y coord,width,height
	//contructor takes in information from a text file
    public Wall(String in){
    	String[] info=in.split(",");
    	x=Integer.parseInt(info[0]);
    	y=Integer.parseInt(info[1]);
    	w=Integer.parseInt(info[2]);
    	h=Integer.parseInt(info[3]);
    }
    
    //takes in a Graphics argument to draw the Wall
   	public void drawRect(Graphics g){
   		g.fillRect((int)x,(int)y,(int)w,(int)h);
   	}
   	
   	//checks collision with a Bullet
   	public boolean collide(Bullet b){
		double bx=b.getX();
    	double by=b.getY();
    	double bw=b.getW();
    	double bh=b.getH();
    	//if there is a collision, the Bullet is shoved out and the function returns true
    	if (bx+bw>=x&&bx<=x+w&&by+bh>=y&&by<=y+h){
    		shoveOut(b);
    		return true;
    	}
    	else{
    		return false;
    	}	
    }
   	
   	//moves Bullet to the last point it was at outside the Wall
   	private void shoveOut(Bullet b){
   		double bx=b.getX();
    	double by=b.getY();
    	double bw=b.getW();
    	double bh=b.getH();
   		b.switchVx();
   		b.switchVy();
   		//has the Bullet go in the opposite direction it came into the Wall until it's out of the Wall
   		while(bx+bw>=x&&bx<=x+w&&by+bh>=y&&by<=y+h){
   			b.move();
   			bx=b.getX();
   			by=b.getY();
   		}
   		b.switchVx();
   		b.switchVy();
   	}
   	
   	//bounces the Bullet off the Wall
   	public void bounceOff(Bullet b){
   		double bx=b.getX();
    	double by=b.getY();
    	double bw=b.getW();
    	double bh=b.getH();
    	//if the Bullet is shoved to the top or bottom of the Wall, that means it hit the top/bottom and needs to have its velocity in y reversed
    	if(bx+bw>=x&&bx<=x+w){
    		b.switchVy();
    	}
    	//if the Bullet is shoved to the right or left of the Wall, that means it hit the right/left and needs to have its velocity in x reversed
    	else if(by+bh>=y&&by<=y+h){
    		b.switchVx();
    	}
    	//if it's been shoved out to an exact diagonal from a corner, it'll have both x and y velocities reversed
    	else{
    		b.switchVx();
    		b.switchVy();
    	}
   	}
   	
    //checks collision with a Tank
    public boolean collide(Tank t){
		double tx=t.getX();
    	double ty=t.getY();
    	double tw=t.getW();
    	double th=t.getH();
    	//if there is a collision, the Tank is shoved out and the function returns true
    	if (tx+tw>=x&&tx<=x+w&&ty+th>=y&&ty<=y+h){
    		shoveOut(t);
    		return true;
    	}
    	else{
    		return false;
    	}	
    }
    
    //moves Tank to the last point it was at outside the Wall
    private void shoveOut(Tank t){
   		double tx=t.getX();
    	double ty=t.getY();
    	double tw=t.getW();
    	double th=t.getH();
    	t.switchMoving();
    	//has the Tank go in the opposite direction it came into the Wall until it's out of the Wall
   		while(tx+tw>=x&&tx<=x+w&&ty+th>=y&&ty<=y+h){
   			t.move();
   			tx=t.getX();
   			ty=t.getY();
   		}
   		//no need to switchMoving again since the player either has a key down and will have his moving set or will have no key down and will have moving automatically set to 0
   	}
    
   	
}