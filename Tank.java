/**
 * @(#)Tank.java
 *
 * @Frank Gu
 *	Tank class made to be controlled by players in TankGame
 * @version 1.00 2015/1/13
 */

//moving and speed are pretty similar

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
public class Tank {
	private Image pic;
	private Image[] deathframes;
	private double x,y,w,h,angle,speed,mx,my,vx,vy,deathind;
	private int shootdelay,moving;
	private boolean dying;
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
    	pic=new ImageIcon("images/"+info[6]).getImage();
    	deathframes=new Image[15];
    	for (int i=0;i<15;i++){
    		deathframes[i]=new ImageIcon("images/explosion/explosion"+i+".png").getImage();	
    	}
    	dying=false;
    	deathind=0;//new tank created every new map, no need to reset deathind
    }

    public Bullet shoot(){
	    Bullet b= new Bullet(mx+.25*speed*w*Math.cos(Math.toRadians(angle))-3,my+.25*speed*h*Math.sin(Math.toRadians(angle))-3,8,8,angle+Math.random()*6-3,6);//-3 on mx and my needed to center bullet
	    shootdelay=30;//20
    	return b;
    }
    
    public void delayer(){
    	shootdelay=Math.max(shootdelay-1,0);
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
	
	//moves the tank
	//a tank technically "moves" every tick but since it's not always in the moving state, it doesn't always move
	//it's in the moving state if pressing the up or down arrow
    public void move(){
    	x+=vx*moving;
    	y+=vy*moving;
    	mx+=vx*moving;
    	my+=vy*moving;
    }
   
   	public void drawTank(Graphics g){
   		if (dying){
   			if(deathind<15){
	   			Image deathframe=deathframes[(int)deathind];
	   			int fw=deathframe.getWidth(null);
	   			int fh=deathframe.getHeight(null);
	   			double offx=(fw-w)/2;
	   			double offy=(fh-h)/2;
	   			g.drawImage(deathframe,(int)(x-offx),(int)(y-offy),null);
	   			deathind+=.2;
   			}
   		}
   		else{
	   		Graphics2D g2D = (Graphics2D)g;
			AffineTransform saveXform = g2D.getTransform();
			AffineTransform at = new AffineTransform();
			at.rotate(Math.toRadians(angle),mx,my);
			g2D.transform(at);
			g2D.drawImage(pic,(int)x,(int)y,null);
			g2D.setTransform(saveXform);
   		}
   	}
   	    
    public void setDying(Boolean bool){
    	dying=bool;
    }
   	public int getDelay(){
    	return shootdelay;
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