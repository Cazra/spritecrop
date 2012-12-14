package spritecrop;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.border.LineBorder;
import pwnee.image.ImageLoader;
import pwnee.image.ImageEffects;


/** 
 * A customized interactive JLabel for displaying an image 
 * with a cropping rectangle.
 */
public class SpriteLabel extends JLabel implements MouseListener, MouseMotionListener {
    
    /** Reference back to the parent panel. */
    SpriteCropPanel panel = null;
    
    /** Distance of image from sides of its containing scroll pane. */
    public int previewOffset = 32;
    
    /** Used for zooming. */
    public int scale = 1;

    // crop rectangle data.
    public int cropStartX = 0;
    public int cropStartY = 0;
    public int cropCurX = 0;
    public int cropCurY = 0;
    
    public SpriteLabel(SpriteCropPanel panel, Icon image) {
        super(image);
        this.panel = panel;
        addMouseListener(this);
        addMouseMotionListener(this);
        
        updateSize();
    }

    
    public void mouseClicked(MouseEvent e) {
        // Do nothing
    }
   
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }
   
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }
   
    /** Pressing the left mouse button sets the start corner of our crop rectangle. */
    public void mousePressed(MouseEvent e) {
        int button = e.getButton();
        
        Point mouseWorld = mouseImgPos(e);
        
        if(button == MouseEvent.BUTTON1) {
            // Left button: begin crop
            cropStartX = cropCurX = mouseWorld.x;
            cropStartY = cropCurY = mouseWorld.y;
        }
        if(button == MouseEvent.BUTTON3) {
          // Right button: end crop
            cropCurX = mouseWorld.x;
            cropCurY = mouseWorld.y;
        }
        
        repaint();
    }
   
    
    public void mouseReleased(MouseEvent e) {
        int button = e.getButton();
        
        int x = (int) Math.min(cropStartX, cropCurX);
        int y = (int) Math.min(cropStartY, cropCurY);
        int w = (int) Math.abs(cropStartX - cropCurX) + 1;
        int h = (int) Math.abs(cropStartY - cropCurY) + 1;
        crop(x,y,w,h);
        
        repaint();
    }
    
    public void mouseMoved(MouseEvent e) {
        updateMouseLabel(e);
        updateColorLabel(e);
    }
    
    /** Dragging any mouse button updates the ending corner of our crop rectangle. */
    public void mouseDragged(MouseEvent e) {
        int button = e.getButton();
        
        updateMouseLabel(e);
        updateColorLabel(e);
        Point mouseWorld = mouseImgPos(e);
        
        cropCurX = mouseWorld.x;
        cropCurY = mouseWorld.y;
        
        repaint();
    }
    
    /** Obtains the mouse's position relative to the preview image. */
    public Point mouseImgPos(MouseEvent e) {
        int x = e.getX()/scale - previewOffset;
        int y = e.getY()/scale - previewOffset;
        
        return new Point(x,y);
    }
    
    
    /** Updates the size of this label to be the scaled image's current size with a buffer around all sides. */
    public void updateSize() {
        Icon curImg = getIcon();
        
        if(curImg != null) {
          int cropW = curImg.getIconWidth();
          int cropH = curImg.getIconHeight();
          
          Dimension iconSize = new Dimension(cropW, cropH);
          Dimension labelSize = new Dimension((iconSize.width + previewOffset*2) * scale , (iconSize.height + previewOffset*2) * scale);
          setPreferredSize(labelSize);
          
          if(panel != null && panel.dimsLabel != null)
            panel.dimsLabel.setText("Size: " + iconSize.width + ", " + iconSize.height);
        }
        
        
    }
    
    
    /** Updates the metrics of the image's cropping rectangle and the corresponding text fields for them. */
    public void crop(int x, int y, int w, int h) {
      cropStartX = x;
      cropStartY = y;
      cropCurX = x + w - 1;
      cropCurY = y + h - 1;
      
      panel.cropXFld.setText("" + x);
      panel.cropYFld.setText("" + y);
      panel.cropWFld.setText("" + w);
      panel.cropHFld.setText("" + h);
      
      repaint();
    }
    
    
    /** Updates the label for displaying the mouse position. */
    public void updateMouseLabel(MouseEvent e) {
        Point mouseWorld = mouseImgPos(e);
        panel.mousePosLabel.setText("Mouse: " + mouseWorld.x + ", " + mouseWorld.y);
    }
    
    /** Updates the label for displaying the color at the current mouse position. */
    public void updateColorLabel(MouseEvent e) {
        try {
            Point mouseWorld = mouseImgPos(e);
            
            Icon icon = getIcon();
            BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = img.createGraphics();
            icon.paintIcon(null, g, 0,0);
            g.dispose();
            
            int[] pixel = img.getRaster().getPixel(mouseWorld.x, mouseWorld.y, new int[4]);
            int color = (pixel[0] << 16) + (pixel[1] << 8) + pixel[2];
            
            panel.colorLabel.setText("Color at mouse: 0x" + Integer.toHexString(color & 0x00FFFFFF).toUpperCase());
        }
        catch(Exception ex) { // Pokemon exception: gotta catch 'em all!
            // An exception will be thrown if the mouse is outside the image's area.
            panel.colorLabel.setText("Color at mouse: out of bounds");
        }
    }
    
    
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        updateSize();
    }
    
    
    public void resetCropData() {
        cropStartX = 0;
        cropStartY = 0;
        cropCurX = 0;
        cropCurY = 0;
    }
    
    
    
    public void paint(Graphics gg) {
        Graphics2D g = (Graphics2D) gg;
        AffineTransform origTrans = g.getTransform();
        Composite origComp = g.getComposite();
        
        g.scale(scale,scale);
        getIcon().paintIcon(this, g, previewOffset, previewOffset);

        drawCropRect(g);
        
        g.setTransform(origTrans);
        g.setComposite(origComp);
    }
    
    
    
    public void drawCropRect(Graphics2D g) {
        AffineTransform origTrans = g.getTransform();
        Composite origComp = g.getComposite();
        
        g.setColor(new Color(0x00FF00));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        
        g.translate(previewOffset, previewOffset);
        if(scale > 1)
            g.translate(0.5,0.5);
        
        int x = (int) Math.min(cropStartX, cropCurX);
        int y = (int) Math.min(cropStartY, cropCurY);
        int w = (int) Math.abs(cropStartX - cropCurX);
        int h = (int) Math.abs(cropStartY - cropCurY);
        
        g.drawRect(x,y,w,h);
        
        g.setTransform(origTrans);
        g.setComposite(origComp);
    }
}