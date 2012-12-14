package spritecrop;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** The main window for this Pwnee game example. */
public class SpriteCropMain extends JFrame implements WindowListener, WindowFocusListener {
    
    public SpriteCropPanel panel;
    public SpriteCropConfig config;
    
    public SpriteCropMain() {
        super("SpriteCrop");
        int screenX = 640;    
        int screenY = 480;
        this.setSize(screenX,screenY);
        this.setJMenuBar(new SpriteCropMenuBar(this));
        
        panel = new SpriteCropPanel(this);
        this.add(panel);
        
        this.addWindowListener(this);
        this.addWindowFocusListener(this);
        
        // finishing touches on Game window
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
        config = SpriteCropConfig.load();
        
        
    }
     
    
    // WindowListener interface stuff
    
    public void windowActivated(WindowEvent e) {
        System.err.println("Window Activated");
    }
    
    public void windowClosed(WindowEvent e)  {
        System.err.println("program terminated. Restoring original display settings.");
    }
    
    public void windowClosing(WindowEvent e) {
        System.err.println("Window closing");
        config.save();
    }
    
    public void windowDeactivated(WindowEvent e) {
        System.err.println("Window deactivated");
    }
    
     public void windowDeiconified(WindowEvent e) {
        System.err.println("Window Deiconified");
    }
    
     public void windowIconified(WindowEvent e) {
        System.err.println("Window Iconified");
    }
    
     public void windowOpened(WindowEvent e) {
        System.err.println("Window opened");
    }
    
    public void windowGainedFocus(WindowEvent e) {
        System.err.println("Window gained focus");
    }
    
    public void windowLostFocus(WindowEvent e)  {
        System.err.println("Window lost focus");
    }
    
    /** Cleans up the application (possibly asking the user if they want to save their changes) and then exits it. */
    public void close() {
        // do some cleaning up.
        System.err.println("cleaning up before closing...");
        
        // terminate.
        System.exit(0);
    }
    
    /** Append the current working map's name to the window's title. */
    public void updateTitle(String mapName) {
        this.setTitle("SpriteCrop - " + mapName);
    }
    
    
    /** Creates the game window and makes it fullscreen if the user provided the argument "fullscreen". */
    public static void main(String[] args) {
        SpriteCropMain window = new SpriteCropMain();
    }

}