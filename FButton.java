/**
 * @(#)FButton.java
 *
 *
 * @author 
 * @version 1.00 2015/2/7
 */

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
public class FButton {
	private int x,y,w,h,textx,texty;
	private boolean locked;
	private String text;
    public FButton(int x,int y,int w,int h, String text) {
    	this.x=x;
    	this.y=y;
    	this.w=w;
    	this.h=h;
    	this.text=text;
    	textx=x+(int)w/2-text.length()*11;
    	locked=false;
    }
    public void setLock(boolean bool){
    	locked=bool;
    }
    public boolean collide(int ox,int oy){//otherx,othery
    	if (locked){
    		return false;
    	}
    	return x<ox&&ox<x+w&&y<oy&&oy<y+h;
    }
    public void drawButt(Graphics g){
    	g.setColor(new Color(120,120,120));
    	g.fillRect(x,y,w,h);
    	g.setColor(new Color(30,30,30));
    	Font f = new Font("Georgia", Font.BOLD, 30);
		g.setFont(f);
    	g.drawString(text,textx,y+32);
   	}
}