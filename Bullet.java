/**
 * @(#)Bullet.java
 *
 *
 * @author 
 * @version 1.00 2015/1/14
 */
 //int only when drawing
import java.util.*;
import java.awt.*;
//bullets bounce
public class Bullet {
	private double x,y,w,h,vx,vy,speed,travelled;

    public Bullet(double x,double y,double w,double h, double ang, double speed){
    	this.x=x;
    	this.y=y;
    	this.w=w;
    	this.h=h;
    	this.speed=speed;
    	vx=speed*Math.cos(Math.toRadians(ang));
    	vy=speed*Math.sin(Math.toRadians(ang));
    	travelled=0;
    }
    
    
    public void move(){
    	x+=vx;
    	y+=vy;
    	travelled+=speed;
    }
    
   	public void draw(Graphics g){
   		g.fillOval((int)x,(int)y,(int)w,(int)h);
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
   	public double getVx(){
   		return vx;
   	}
   	public double getVy(){
   		return vy;
   	}
   	public double getTravelled(){
   		return travelled;
   	}
   	public void switchVx(){
   		vx*=-1;
   	}
   	public void switchVy(){
   		vy*=-1;
   	}
}