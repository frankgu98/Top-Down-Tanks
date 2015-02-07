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
	private int x,y,w,h;
	private boolean locked;
	private String text;
    public FButton(int x,int y,int w,int h, String text) {
    	this.x=x;
    	this.y=y;
    	this.w=w;
    	this.h=h;
    	this.text=text;
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
    	g.fillRect((int)x,(int)y,(int)w,(int)h);
    	g.setColor(new Color(30,30,30));
    	Font f = new Font("D-Day Stencil", Font.BOLD, 30);
		g.setFont(f);
    	g.drawString(text,x,y);
   	}
}