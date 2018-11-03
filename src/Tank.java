/**
 * @(#)Tank.java
 * @Frank Gu
 * Tank class made to be controlled by players in TankGame
 * @version 1.00 2015/1/13
 */

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

public class Tank {
    private Image pic;
    private Image[] deathframes;
    //x coord,y coord,width,height,angle,speed,midpoint x,midpoint y,velocity in x,velocity in y,frame Tank is in in death animation
    private double x, y, w, h, angle, speed, mx, my, vx, vy, deathind;
    //time until Tank can shoot,if the Tank is going forward (1), reverse (-1), stationary (0)
    private int shootdelay, moving;
    private boolean dying;//if the tank is dying (in the dying animations)

    //contructor takes in information from a text file
    public Tank(String in) {
        String[] info = in.split(",");
        x = Double.parseDouble(info[0]);
        y = Double.parseDouble(info[1]);
        w = Double.parseDouble(info[2]);
        h = Double.parseDouble(info[3]);
        angle = Double.parseDouble(info[4]);
        speed = Double.parseDouble(info[5]);
        mx = x + w / 2;
        my = y + h / 2;
        vx = speed * Math.cos(Math.toRadians(angle));
        vy = speed * Math.sin(Math.toRadians(angle));
        deathind = 0;//this also allows me to not have to reset deathind every new map since a new Tank is initialized after someone dies
        shootdelay = 0;
        pic = new ImageIcon("images/" + info[6]).getImage();
        //death animation is the same for all Tanks
        deathframes = new Image[15];
        for (int i = 0; i < 15; i++) {
            deathframes[i] = new ImageIcon("images/explosion/explosion" + i + ".png").getImage();
        }
        dying = false;
        moving = 0;
    }

    //returns a Bullet based on the Tank's position
    public Bullet shoot() {
        //Bullet is generaed outside the Tank to prevent collision with the Tank that shot it,small spray created by Math.random() which can be up to
        //3 degrees in either direction,-3 on mx and my needed to center Bullet
        Bullet b = new Bullet(mx + .25 * speed * w * Math.cos(Math.toRadians(angle)) - 3, my + .25 * speed * h * Math.sin(Math.toRadians(angle)) - 3, 8, 8, angle + Math.random() * 6 - 3, 6);
        shootdelay = 30;
        return b;
    }

    //reduces delay left until Tank can shoot again
    public void delayer() {
        shootdelay = Math.max(shootdelay - 1, 0);
    }

    //turns the Tank by a certain amount (in degrees)
    public void turn(double amount) {
        angle = (angle + amount) % 360;
        vx = speed * Math.cos(Math.toRadians(angle));
        vy = speed * Math.sin(Math.toRadians(angle));
    }

    //check if a Bullet is colliding with the Tank
    public boolean bulCollide(Bullet b) {
        double bx = b.getX();
        double by = b.getY();
        double bw = b.getW();
        double bh = b.getH();
        return x + w >= bx && x <= bx + bw && y + h >= by && y <= by + bh;
    }

    //moves the Tank
    //a Tank technically "moves" every tick but since it's not always in the moving state, it doesn't always move
    //a Tank is in the moving state if the associated keys are pressed in the GamePanel.java
    public void move() {
        x += vx * moving;
        y += vy * moving;
        mx += vx * moving;
        my += vy * moving;
    }

    //takes in a Graphics to draw the Tank
    public void drawTank(Graphics g) {
        //if the Tank is dying
        if (dying) {
            //and if it's not done drawing its death, the Tank will cycle through the frames of its death every time the screen is updated
            if (deathind < 15) {
                Image deathframe = deathframes[(int) deathind];
                int fw = deathframe.getWidth(null);
                int fh = deathframe.getHeight(null);
                //offx and offy are the offsets for the pictures and are used to centre the explosion to where the Tank died
                double offx = (fw - w) / 2;
                double offy = (fh - h) / 2;
                g.drawImage(deathframe, (int) (x - offx), (int) (y - offy), null);
                deathind += .2;//doesn't go 1 frame of death per sscreen update, each frame is on the screen for 5 updates so it looks less fast
            }
            //if it's done drawing its death, but still dying, nothing is drawn (in the game, this is during the pause between map shifts as players trash talk eachother or something)
        }
        //if the Tank isn't dying, it's drawn normally
        else {
            //rotates the Tank's picture
            Graphics2D g2D = (Graphics2D) g;
            AffineTransform saveXform = g2D.getTransform();
            AffineTransform at = new AffineTransform();
            at.rotate(Math.toRadians(angle), mx, my);
            g2D.transform(at);

            g2D.drawImage(pic, (int) x, (int) y, null);
            g2D.setTransform(saveXform);
        }
    }

    //getting and setting values
    public void setDying(Boolean bool) {
        dying = bool;
    }

    //moving is direction, 1==forward 0==not moving -1==reverse
    public void setMoving(int val) {
        moving = val;
    }

    public void switchMoving() {
        moving *= -1;
    }

    public int getDelay() {
        return shootdelay;
    }

    public Image getPic() {
        return pic;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getW() {
        return w;
    }

    public double getH() {
        return h;
    }

}