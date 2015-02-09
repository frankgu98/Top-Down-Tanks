/**
 * @(#)Bullet.java
 *
 * @Frank Gu
 *	Bullet class made to be shot by a Tank in TankGame
 *	It can move, draw itself, and stores data on its position
 * @version 1.00 2015/1/14
 */

import java.util.*;
import java.awt.*;

public class Bullet {
	private double x,y,w,h,vx,vy,speed,travelled;//x coord,y coord,width,height,velocity in x,velocity in y,speed,how far it travelled
	//contructor takes in information from tank class
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
    
    //moves bullet by changing x and y values by their velocities
    public void move(){
    	x+=vx;
    	y+=vy;
    	travelled+=speed;
    }
    
    //takes in a Graphics argument to draw the bullet
    //simplifies drawing the bullet
   	public void draw(Graphics g){
   		g.fillOval((int)x,(int)y,(int)w,(int)h);
   	}
   	
   	//getting and setting values related to the bullet
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
   	public double getTravelled(){
   		return travelled;
   	}
   	//needed for bouncing
   	public void switchVx(){
   		vx*=-1;
   	}
   	public void switchVy(){
   		vy*=-1;
   	}
}