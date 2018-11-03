import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;

class GamePanel extends JPanel implements KeyListener, MouseListener {
    //constants describing what screen the player is currently on
    public static final int MENU = 0;
    public static final int GAME = 1;
    public static final int CONTROLS = 2;

    private Font scoreFont = new Font("D-Day Stencil", Font.BOLD, 30);
    private int screen, clickX, clickY;//screen user is on,x coord of where player clicked,y coord of where player clicked
    private int pWins, eWins, mapCount;//how many wins p has,how many wins e has,how many maps are available to play
    private long pauseStartTime;//when the pause started (pausing happens between maps)
    private boolean[] keys;//Array of which keys are being pressed
    private boolean playing, muted;//if the players are playing,if the music is muted
    AudioClip sax;//the music
    private Image menuPic, controlsPic;//picture of the menu,picture of the controls
    private FButton start, controls, quit, back, mute;//all the FButtons the user can use
    //start starts the game,controls brings you to the controls screen,quit closes the frame,back goes from game or controls to the menu,mute mutes or unmutes the music

    private Tank p, e;//the 2 Tanks in the game, stands for player and enemy (even though both are player controlled)
    private TankGame mainFrame;
    private ArrayList<Bullet> bullets;
    private ArrayList<Wall> walls;

    public GamePanel(TankGame m) {
        keys = new boolean[KeyEvent.KEY_LAST + 1];
        bullets = new ArrayList<Bullet>();
        walls = new ArrayList<Wall>();
        mainFrame = m;
        setSize(1200, 900);//size is 1200x900 but all maps end at 1200x800, the last 100 pixels are for score and FButtons
        addKeyListener(this);
        addMouseListener(this);
        //starts the click position where no Button is
        clickX = -1;
        clickY = -1;
        screen = MENU;//starts the user in the menu
        start = new FButton(386, 370, 430, 47, 0, "START", 30);
        controls = new FButton(386, 458, 430, 47, 0, "CONTROLS", 30);
        quit = new FButton(387, 548, 430, 47, 0, "QUIT", 30);
        back = new FButton(502, 810, 200, 47, 0, "BACK", 30);
        mute = new FButton(575, 868, 48, 30, 25, "MUTE", 12);
        start.setVisible(true);
        controls.setVisible(true);
        quit.setVisible(true);
        //the back and mute buttons don't exist in the menu
        back.setVisible(false);
        mute.setVisible(false);
        //tries to load the song and menu and controls pictures
        try {
            sax = Applet.newAudioClip(getClass().getResource("Yakety_Sax.wav"));
            menuPic = new ImageIcon("images/menu.png").getImage();
            controlsPic = new ImageIcon("images/controls.png").getImage();
        }
        catch (NullPointerException ex) {
            System.out.println("Resources not found. Please redownload the game files.");
            System.exit(0);
        }
        catch (Exception ex) {
            System.out.println("Something went more wrong than usual");
            System.exit(0);
        }
        playing = false;
        muted = false;
        pWins = 0;
        eWins = 0;
        mapCount = 5;
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
        clickX = e.getX();
        clickY = e.getY();
    }

    //resetClick so that Buttons don't get repeatedly clicked when the user only clicked them once
    public void resetClick() {
        clickX = -1;
        clickY = -1;
    }

    //deals with everything you can do in the menu
    public void menuFunctions() {
        //only does things if the screen the user is on is the menu
        if (screen == MENU) {
            //if the user clicks the start button, the game starts
            if (start.collide(clickX, clickY)) {
                screen = GAME;
                start.setVisible(false);
                controls.setVisible(false);
                quit.setVisible(false);
                back.setVisible(true);
                mute.setVisible(true);
                //first initializing needs newLevel since checkEnded is the last function that's called when playing but is the only other map loading function,
                //so players would attempt to move before they existed
                newLevel();
                playing = true;
                sax.loop();//plays music upon starting the actual playing
                resetClick();
            }
            //if the user clicks the controls button, they are brought to the controls
            if (controls.collide(clickX, clickY)) {
                screen = CONTROLS;
                start.setVisible(false);
                controls.setVisible(false);
                quit.setVisible(false);
                back.setVisible(true);
                resetClick();
            }
            //if the user clicks quit, the everything stops running
            if (quit.collide(clickX, clickY)) {
                System.exit(0);
            }
        }
    }

    //deals with everything you can do in the controls screen
    public void controlsFunctions() {
        //only does things if the screen the user is on is the controls
        if (screen == CONTROLS) {
            //if the user clicks back, they are brought back to the menu
            if (back.collide(clickX, clickY)) {
                screen = MENU;
                start.setVisible(true);
                controls.setVisible(true);
                quit.setVisible(true);
                back.setVisible(false);
                resetClick();
            }
        }
    }

    //deals with everything you can do in the game screen
    public void gameFunctions() {
        //only does things if the screen the user is on is the game
        if (screen == GAME) {
            //if the user clicks back, they are brought back to the menu and the score is reset
            if (back.collide(clickX, clickY)) {
                pWins = 0;
                eWins = 0;
                screen = MENU;
                start.setVisible(true);
                controls.setVisible(true);
                quit.setVisible(true);
                back.setVisible(false);
                mute.setVisible(false);
                sax.stop();
                resetClick();
            }
            //if the user clicks mute, the music is stopped
            if (mute.collide(clickX, clickY)) {
                if (muted) {
                    muted = false;
                    sax.loop();
                }
                else {
                    muted = true;
                    sax.stop();
                }
                resetClick();
            }
            playerActions();//lets players control Tanks
            bulPhysics();//makes the Bullets follow the game's physics
            tankPhysics();//makes the Tanks follow the game's physics
            checkEnded();//lets the game end and switch maps
            //checkEnded comes last since a player's last input used to be able to slip though the map switch but since check ended has bullets.clear() that no longer happens
            //ex. if a player held shoot while in the transition period, a bullet would come out upon entering the new map
        }
    }

    //checks if a the players are still playing (if no one died yet) and stops the game and switches levels if one has
    public void checkEnded() {
        if (!playing) {
            //so the player doesn't continue gliding after death (can be removed for moving explosions which look pretty cool)
            p.setMoving(0);
            e.setMoving(0);
            //if the pause time has been longer than 2 seconds, the level is swwitched and the players can play again
            if (pauseStartTime + 2000 <= System.currentTimeMillis()) {
                newLevel();
                playing = true;
            }
        }
    }

    //loads a new level
    public void newLevel() {
        walls.clear();//so you don't put a map onto another map
        //read walls and tank information from text files
        Scanner inFile = null;
        try {
            inFile = new Scanner(new File("maps/map" + (int) (mapCount * Math.random()) + ".txt"));
            p = new Tank(inFile.nextLine());
            e = new Tank(inFile.nextLine());
            int n = Integer.parseInt(inFile.nextLine());
            for (int i = 0; i < n; i++) {
                walls.add(new Wall(inFile.nextLine()));
            }
        }
        catch (NullPointerException ex) {
            System.out.println("Resources not found. Please redownload the game files.");
            System.exit(0);
        }
        catch (IOException ex) {
            System.out.println("Map files corrupted. Please redownload the game files.");
            System.exit(0);
        }
        catch (Exception ex) {
            System.out.println("Something went more wrong than usual");
            System.exit(0);
        }
    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }

    //goes through all the Bullets and decides their behaviour
    public void bulPhysics() {
        //has to go through backwards to make removing bullets possible
        for (int i = bullets.size() - 1; i > -1; i--) {
            Bullet bul = bullets.get(i);
            //if either player collided with a Bullet, the score is changed, a player starts dying, and they are no longer playing
            if (p.bulCollide(bul)) {
                p.setDying(true);
                eWins++;
                pauseStartTime = System.currentTimeMillis();
                bullets.clear();
                playing = false;
                break;
            }
            if (e.bulCollide(bul)) {
                e.setDying(true);
                pWins++;
                pauseStartTime = System.currentTimeMillis();
                bullets.clear();
                playing = false;
                break;
            }
            //if a Bullet hits a Wall, the bullet bounces off
            for (Wall w : walls) {
                if (w.collide(bul)) {
                    w.bounceOff(bul);
                }
            }
            //moves the Bullet and checks if it moved too far (and removes the Bullet if it did)
            bul.move();
            if (bul.getTravelled() > 4999) {
                bullets.remove(i);
            }
        }
    }

    //does all the physics and limits what Tanks can actually do
    public void tankPhysics() {
        //delayer reduces the delay on when a Tank can shoot again
        p.delayer();
        e.delayer();
        p.move();
        e.move();
        for (Wall w : walls) {
            w.collide(p);
            w.collide(e);
        }
    }

    //takes in player input and controls the Tanks based on that
    public void playerActions() {
        //only allows the players to act if they're playing
        if (playing) {
            //has it so the Tank only actually "moves" when pressing the up or down keys even though it's technically always moving (though if setMoving is 0, the Tank is moving at 0units/s)
            if (keys[KeyEvent.VK_W]) {
                p.setMoving(1);
            }
            else if (keys[KeyEvent.VK_S]) {
                p.setMoving(-1);
            }
            else {
                p.setMoving(0);
            }
            if (keys[KeyEvent.VK_A]) {
                p.turn(-3);
            }
            if (keys[KeyEvent.VK_D]) {
                p.turn(3);//3
            }
            if (keys[KeyEvent.VK_V] && p.getDelay() == 0) {
                bullets.add(p.shoot());
            }

            if (keys[KeyEvent.VK_UP]) {
                e.setMoving(1);
            }
            else if (keys[KeyEvent.VK_DOWN]) {
                e.setMoving(-1);
            }
            else {
                e.setMoving(0);
            }
            if (keys[KeyEvent.VK_LEFT]) {
                e.turn(-3);
            }
            if (keys[KeyEvent.VK_RIGHT]) {
                e.turn(3);
            }
            if (keys[KeyEvent.VK_P] && e.getDelay() == 0) {
                bullets.add(e.shoot());
            }
        }
    }

    //gets which keys are and aren't pressed
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    //draws the scores of the 2 Tanks
    public void drawScore(Graphics g) {
        g.setFont(scoreFont);
        g.drawImage(p.getPic(), 85, 840, null);
        g.drawString(Integer.toString(pWins), 140, 870);
        g.drawImage(e.getPic(), 1000, 840, null);
        g.drawString(Integer.toString(eWins), 1055, 870);
    }

    //draws what the user sees based on what screen they're on
    public void paintComponent(Graphics g) {
        if (screen == MENU) {
            g.drawImage(menuPic, 0, 0, null);
        }
        else if (screen == GAME) {
            //fills the background with light grey
            g.setColor(new Color(230, 230, 230));
            g.fillRect(0, 0, getWidth(), getHeight());
            //draws the Walls dark grey
            g.setColor(new Color(77, 77, 77));
            for (Wall w : walls) {
                w.drawRect(g);
            }
            //draws the Bullets black
            g.setColor(Color.black);
            for (Bullet bul : bullets) {
                bul.draw(g);
            }
            p.drawTank(g);
            e.drawTank(g);
            drawScore(g);
        }
        else if (screen == CONTROLS) {
            g.drawImage(controlsPic, 0, 0, null);
        }
        //draws all the FButtons
        start.drawBut(g);
        controls.drawBut(g);
        quit.drawBut(g);
        back.drawBut(g);
        mute.drawBut(g);
    }
}
