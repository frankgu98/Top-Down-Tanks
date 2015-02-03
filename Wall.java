/**
 * @(#)Wall.java
 *
 *
 * @author 
 * @version 1.00 2015/1/20
 */
import java.util.*;
import java.awt.*;

public class Wall {
	private double x,y,w,h;
    public Wall(String in){
    	String[] info=in.split(",");
    	x=Integer.parseInt(info[0]);
    	y=Integer.parseInt(info[1]);
    	w=Integer.parseInt(info[2]);
    	h=Integer.parseInt(info[3]);
    }
    
    public double getX(){
   		return x;
   	}
   	public double getY(){
   		return y;
   	}
   	public double getW(){
   		return w;
   	}
   	public double getH(){
   		return h;
   	}
   	
   	public void drawRect(Graphics g){
   		g.fillRect((int)x,(int)y,(int)w,(int)h);
   	}
   	
   	public void bounceOff(Bullet b){
   		double bx=b.getX();
    	double by=b.getY();
    	double bw=b.getW();
    	double bh=b.getH();
    	if(bx+bw>=x&&bx<=x+w){
    		b.switchVy();
    	}
    	else if(by+bh>=y&&by<=y+h){
    		b.switchVx();
    	}
    	//if it's been shoved out to an exact diagonal from a corner
    	else{
    		b.switchVx();
    		b.switchVy();
    	}
   	}
   	
   	private void shoveOut(Bullet b){
   		double bx=b.getX();
    	double by=b.getY();
    	double bw=b.getW();
    	double bh=b.getH();
   		b.switchVx();
   		b.switchVy();
   		while(bx+bw>=x&&bx<=x+w&&by+bh>=y&&by<=y+h){
   			b.move();
   			bx=b.getX();
   			by=b.getY();
   		}
   		
   		b.switchVx();
   		b.switchVy();
   	}
   	
   	public boolean collide(Bullet b){
		double bx=b.getX();
    	double by=b.getY();
    	double bw=b.getW();
    	double bh=b.getH();
    	if (bx+bw>=x&&bx<=x+w&&by+bh>=y&&by<=y+h){
    		shoveOut(b);
    		return true;
    	}
    	else{
    		return false;
    	}	
    }
    
    public boolean collide(Tank t){
		double tx=t.getX();
    	double ty=t.getY();
    	double tw=t.getW();
    	double th=t.getH();
    	if (tx+tw>=x&&tx<=x+w&&ty+th>=y&&ty<=y+h){
    		shoveOut(t);
    		return true;
    	}
    	else{
    		return false;
    	}	
    }
    private void shoveOut(Tank t){
   		double tx=t.getX();
    	double ty=t.getY();
    	double tw=t.getW();
    	double th=t.getH();
    	//shoves the tank out
    	t.switchMoving();
   		while(tx+tw>=x&&tx<=x+w&&ty+th>=y&&ty<=y+h){
   			t.move();
   			tx=t.getX();
   			ty=t.getY();
   		}
   	}
    
}