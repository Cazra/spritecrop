package spritecrop;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.*;
import java.awt.event.*;
import javax.swing.event.MenuKeyEvent;
import java.io.*;


/** The menu bar for the SpriteCrop application. */
public class SpriteCropMenuBar extends JMenuBar implements ActionListener {
    
    SpriteCropMain frame;
    
    JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem exitItem = new JMenuItem("Exit");
    
    
    public SpriteCropMenuBar(SpriteCropMain frame) {
        super();
        this.frame = frame;
        
        this.add(fileMenu);
        fileMenu.setMnemonic(KeyEvent.VK_F);
            fileMenu.add(openItem);
            openItem.addActionListener(this);
            openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

            fileMenu.add(exitItem);
            exitItem.addActionListener(this);
            exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
    }
    
    
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        SpriteCropPanel panel = frame.panel;
        
        // File menu
        if(source == openItem) {
            JFileChooser openDia = new JFileChooser(frame.config.vars.get("lastOpen"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter ("bmp, gif, jpg, or png file", "bmp", "gif", "jpg", "png");
            openDia.setFileFilter(filter);
            
            int retVal = openDia.showOpenDialog(this);
            
            // If successful, load the file as our preview image.
            if(retVal == JFileChooser.APPROVE_OPTION) {
                panel.loadImageIconFromFile(openDia);
            }
        }

        if(source == exitItem) {
            System.err.println("File -> exitItem fired.");
            frame.close();
        }

    }
}

