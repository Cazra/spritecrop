package spritecrop;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import javax.swing.border.LineBorder;
import pwnee.image.ImageLoader;
import pwnee.image.ImageEffects;

/** The main panel for the application. */
public class SpriteCropPanel extends JPanel implements ActionListener, ChangeListener {
  /** Reference back to the parent frame. */
  SpriteCropMain frame;
  
  /** The scrolling pane that houses our image. */
  JScrollPane imgScrollPane;
  
  /** Pushing this button updates crop rectangle in the image to match the cropping fields. */
  JButton cropBtn = new JButton("Show crop rectangle");
  
  // Fields contain metric information for the crop rectangle in our image.
  JTextField cropXFld = new JTextField("0",5);
  JTextField cropYFld = new JTextField("0",5);
  JTextField cropWFld = new JTextField("32",5);
  JTextField cropHFld = new JTextField("32",5);
  
  /** The custom ImageIcon that displays our image. */
  SpriteLabel curImgLabel;
  
  ImageIcon curImg;
  String imgPath = "";
  
  /** A slider used to control the image's zoom level. */
  JSlider zoomSlider;
  
  /** Label displays the image's zoom level. */
  JLabel zoomLabel = new JLabel("Zoom: x1");
  
  // Text for displaying some image and mouse data that might be useful to the user. 
  JLabel mousePosLabel = new JLabel("Mouse: 0,0");
  JLabel dimsLabel = new JLabel("Size: 32,32");
  JLabel colorLabel = new JLabel("Color at mouse: out of bounds");
  
  
  public SpriteCropPanel(SpriteCropMain frame) {
    super(new BorderLayout());
    this.frame = frame;
    constructComponents();
  }
  
  
  
  /** Constructs the dialog's top JPanel. */
    public void constructComponents() {
      initSpriteLabel();
      imgScrollPane = new JScrollPane(curImgLabel);
      this.add(imgScrollPane, BorderLayout.CENTER);
      this.add(makePreviewUtilsPanel(), BorderLayout.SOUTH);
    }
    
    /** Constructs the subpanel with fields for cropping the sprite's image. */
    public JPanel constructCroppingPanel() {
        JPanel cropPanel = DialogUtils.makeVerticalFlowPanel();
        cropPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Crop metrics"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));
        cropPanel.setMaximumSize(new Dimension(200,200));
        
        JPanel fieldsPnl = new JPanel(new GridLayout(2,2));
        
        fieldsPnl.add(DialogUtils.makeLabelFieldPanel(new JLabel("crop left X: "), cropXFld));
        fieldsPnl.add(DialogUtils.makeLabelFieldPanel(new JLabel("crop top Y: "), cropYFld));
        fieldsPnl.add(DialogUtils.makeLabelFieldPanel(new JLabel("crop width: "), cropWFld));
        fieldsPnl.add(DialogUtils.makeLabelFieldPanel(new JLabel("crop height: "), cropHFld));
        
        cropPanel.add(fieldsPnl);
        cropPanel.add(cropBtn);
        cropBtn.addActionListener(this);
        
        return cropPanel;
    }
    
    
    /** Initializes our SpriteLabel. */
    public void initSpriteLabel() {
        curImg = loadImageIconFromResource("BadImg.png");
        curImgLabel = new SpriteLabel(this, curImg);
        curImgLabel.panel = this;
    }
    
    /** Creates a JPanel to house the shiny buttons for the previewPanel. */
    public JPanel makePreviewUtilsPanel() {
        JPanel topPanel = new JPanel();
        
        JPanel cropPanel = constructCroppingPanel();
        
        JPanel result = DialogUtils.makeVerticalFlowPanel();
        result.add(makeZoomSliderPanel());
        result.add(mousePosLabel);
        result.add(colorLabel);
        result.add(dimsLabel);
        result.add(new JPanel());
        
        topPanel.add(cropPanel);
        topPanel.add(result);
      
        return topPanel;
    }
    
    /** Creates a JPanel with our zoom slider and the JLabel next to it that tells our current zoom ammount. */
    public JPanel makeZoomSliderPanel() {
        JPanel result = new JPanel();
        zoomSlider = new JSlider(0,3,0);
        zoomSlider.setPreferredSize(new Dimension(100,30));
        zoomSlider.setSnapToTicks(true);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setMajorTickSpacing(1);
        zoomSlider.addChangeListener(this);
        result.add(zoomSlider);
        result.add(zoomLabel);
        
        return result;
    }
    
    
    
    
    /** Converts the zoomSlider value to its corresponding zoom value (2^n). */
    public int getZoomValue() {
        return (int) Math.pow(2,zoomSlider.getValue());
    }
    
    /** Attempts to crop our sprite image using the values from the fields in the cropping panel. */
    public void tryCropImage() {
        int cropX, cropY, cropW, cropH;
        
        try {
            cropX = (new Integer(cropXFld.getText())).intValue();
            cropY = (new Integer(cropYFld.getText())).intValue();
            cropW = (new Integer(cropWFld.getText())).intValue();
            cropH = (new Integer(cropHFld.getText())).intValue();
            
            if(cropW < 1 || cropH < 1)
                throw new NumberFormatException();
                
            curImgLabel.crop(cropX,cropY,cropW,cropH);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Bad integer values");
        }
        
    }
    
    /** Loads an ImageIcon from the classpath. */
    public ImageIcon loadImageIconFromResource(String path) {
        ImageLoader imgLoader = new ImageLoader();
        Image img = imgLoader.loadFromFile(path);
        ImageIcon result = new ImageIcon(img);
        return result;
    }
    
    /** Loads the current image from a file. */
    public void loadImageIconFromFile(JFileChooser openDia) {
      String path = "Could not load image.";
      try {
          path = openDia.getSelectedFile().getPath();
          curImg = new ImageIcon(path);
          imgPath = path;
          frame.updateTitle(path);
          
          frame.config.vars.put("lastOpen", path);
      }
      catch (Exception ex) {
          curImg = loadImageIconFromResource("BadImg.png");
          imgPath = "";
      }
      
      curImgLabel.setIcon(curImg);
      curImgLabel.resetCropData();
      imgScrollPane.updateUI();
    }
    
    
    /** Event listener for buttons */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source == cropBtn) {
            tryCropImage();
        }
    }
    
    
    /** Event listener for the zoom slider */
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if(source == zoomSlider) {
            int zoomVal = getZoomValue();
            zoomLabel.setText("Zoom: x" + zoomVal);
            curImgLabel.scale = zoomVal;
            curImgLabel.updateSize();
            
            imgScrollPane.updateUI();
        }
    }
}

