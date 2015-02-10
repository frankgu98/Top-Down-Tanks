/**
 * @(#)FButton.java
 *
 * @Frank Gu
 *	JButtons are hard, so I made a FButton class to let the user use their mouse to select things
 *	It can draw itself, detect collision with a point, and lock itself (to not collide with a point)
 * @version 1.00 2015/2/7
 */

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
public class FButton {
	//x coord,y coord,width,height,x coord of start of text,y coord of start of text,font size
	private int x,y,w,h,textx,texty,fontsize;
	private boolean locked;//if the button is locked
	private String text;//text associated with the button
	//contructor takes in information normally
    public FButton(int x,int y,int w,int h,int xshift,String text,int fontsize) {
    	this.x=x;
    	this.y=y;
    	this.w=w;
    	this.h=h;
    	this.text=text;
    	this.fontsize=fontsize;
    	//textx is based on the box's midpoint, the length of the string, and an xshift (xshift needed since smaller fonts start too far left)
    	textx=x+(int)(w/2)-text.length()*11+xshift;
    	//texty is based on the box's midpoint and the fontsize
    	texty=y+(int)(h/2)+(int)(fontsize/2.15);
    	//starts unlocked (usuable by user)
    	locked=false;	
    }
    
    public void setLock(boolean bool){
    	locked=bool;
    }
    //checks if a point is within the rect's bounds
    public boolean collide(int ox,int oy){//otherx,othery
    	if (locked){
    		return false;
    	}
    	return x<ox&&ox<x+w&&y<oy&&oy<y+h;
    }
    
    //takes in a Graphics argument to draw the button
    public void drawButt(Graphics g){
    	//only draws if it isn't locked
    	if(!locked){
    		//draws the rect
	    	g.setColor(new Color(120,120,120));
	    	g.fillRect(x,y,w,h);
	    	//draws the text
	    	g.setColor(new Color(30,30,30));
	    	Font f = new Font("Georgia", Font.BOLD, fontsize);
			g.setFont(f);
	    	g.drawString(text,textx,texty);
    	}
   	}
}