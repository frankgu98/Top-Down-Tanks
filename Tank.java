/**
 * @(#)Tank.java
 *
 *
 * @author 
 * @version 1.00 2015/1/13
 */
 /*
  *rotate(double theta, double anchorx, double anchory)
Concatenates this transform with a transform that rotates coordinates around an anchor point.*/

//moving and speed are pretty similar
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.image.*;
public class Tank {
	private Image pic;
	private Rectangle box;
	private double x,y,w,h,angle,speed,mx,my,vx,vy;
	private int shootdelay, moving;
    public Tank(double x,double y,double w,double h, double ang, double speed, Image img) {
    	this.x=x;
    	this.y=y;
    	this.w=w;
    	this.h=h;
    	angle=ang;
		this.speed=speed;
    	vx=speed*Math.cos(Math.toRadians(angle));
    	vy=speed*Math.sin(Math.toRadians(angle));
    	mx=x+w/2;
    	my=y+h/2;
    	shootdelay=0;
    	moving=0;
    	pic=img;
    }
    
    public Bullet shoot(){
	    Bullet b= new Bullet(mx+.15*speed*w*Math.cos(Math.toRadians(angle))-3,my+.15*speed*h*Math.sin(Math.toRadians(angle))-3,8,8,angle,10);//-3 on mx and my needed to center bullet
	    shootdelay=40;//20
    	return b;
    }
    
    public void delayer(){
    	shootdelay=Math.max(shootdelay-1,0);
    }
    
    public int getDelay(){
    	return shootdelay;
    }
    public void turn(double amount){
    	angle=(angle+amount)%360;
    	vx=speed*Math.cos(Math.toRadians(angle));
    	vy=speed*Math.sin(Math.toRadians(angle));	
    }
    
    public boolean bulCollide(Bullet b){
    	double bx=b.getX();
    	double by=b.getY();
    	double bw=b.getW();
    	double bh=b.getH();
    	return x+w>=bx&&x<=bx+bw&&y+h>=by&&y<=by+bh;
	}

	//moving is direction, 1==forward 0==not moving -1==reverse
	public void setMoving(int val){
		moving=val;
	}
	public void switchMoving(){
		moving*=-1;
	}
	//a tank technically "moves" every tick but since it's not in the moving state, it doesn't actually move
	//it's in the moving state if pressing the up or down arrow
    public void move(){
    	x+=vx*moving;
    	y+=vy*moving;
    	mx+=vx*moving;
    	my+=vy*moving;
    }
   
   	//what is even going on here
   	public void drawTank(Graphics g){
   		
   		Graphics2D g2D = (Graphics2D)g;
		AffineTransform saveXform = g2D.getTransform();
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians(angle),mx,my);
		g2D.transform(at);
		g2D.drawImage(pic,(int)x,(int)y,null);
		g2D.setTransform(saveXform);
   		g.fillRect((int)x,(int)y,(int)w,(int)h);
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
    
}