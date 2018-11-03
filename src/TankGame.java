/**
 * @(#)TankGame.java
 * @Frank Gu
 * TankGame class that opens a container for the game in GamePanel.java to be played
 * @version 1.00 2015/1/14
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.applet.*;
import javax.sound.sampled.*;

public class TankGame extends JFrame implements ActionListener {
    javax.swing.Timer myTimer;//timer that triggers an Action after a set amount of ms (to redraw everything)
    GamePanel game;

    //contructor takes in nothing since the game window will be constant no matter how many times you play
    public TankGame() {
        super("Tank Stuff");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1205, 928);
        myTimer = new javax.swing.Timer(20, this);// trigger every 10 ms
        game = new GamePanel(this);
        add(game);
        setResizable(false);
        setVisible(true);
    }

    public void start() {
        myTimer.start();
    }

    //every Action(the only one here being the timer triggering an Action)
    public void actionPerformed(ActionEvent evt) {
        game.menuFunctions();
        game.gameFunctions();
        game.controlsFunctions();
        game.repaint();
    }

    public static void main(String[] arguments) {
        TankGame frame = new TankGame();
    }
}

