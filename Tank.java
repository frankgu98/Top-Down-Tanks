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
import java.io.*;
public class Tank {
	private Image pic;
	private double x,y,w,h,angle,speed,mx,my,vx,vy;
	private int shootdelay, moving;
    public Tank(String in) {
    	String[] info=in.split(",");
    	x=Double.parseDouble(info[0]);
    	y=Double.parseDouble(info[1]);
    	w=Double.parseDouble(info[2]);
    	h=Double.parseDouble(info[3]);
    	angle=Double.parseDouble(info[4]);
		speed=Double.parseDouble(info[5]);
    	vx=speed*Math.cos(Math.toRadians(angle));
    	vy=speed*Math.sin(Math.toRadians(angle));
    	mx=x+w/2;
    	my=y+h/2;
    	shootdelay=0;
    	moving=0;
    	pic=new ImageIcon(info[6]).getImage();
    }
    
    public Bullet shoot(){
	    Bullet b= new Bullet(mx+.25*speed*w*Math.cos(Math.toRadians(angle))-3,my+.25*speed*h*Math.sin(Math.toRadians(angle))-3,8,8,angle,9);//-3 on mx and my needed to center bullet
	    shootdelay=30;//20
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
   		//g.fillRect((int)x,(int)y,(int)w,(int)h);
   	}
   	
   	public Image getPic(){
   		return pic;
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