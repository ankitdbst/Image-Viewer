/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package components;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractAction;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;


import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/**
 *
 * @author anks
 */


public class ImageViewerApp extends JFrame {
    
    private JLabel photographLabel = new JLabel();
    private JToolBar buttonBar = new JToolBar();
    
    private String imagedir = "images/";
    
    private MissingIcon placeholderIcon = new MissingIcon();
    
    /**
     * List of all the descriptions of the image files with caption.
     */
    private String[] imageCaptions = { "Amazing Beauty 1", "Amazing Beauty 2",
    "Amazing Beauty 3", "Amazing Beauty 4"};
    
    /**
     * List of all the image files to load.
     */
    private String[] imageFileNames = { "1.jpg", "2.jpg",
    "3.jpg", "4.jpg"};

    /*
    KeyListener kl=new KeyAdapter()
     {
      @Override
      public void keyPressed(KeyEvent evt)
      {
       //If someone click Esc key, this program will exit
       if(evt.getKeyCode()==KeyEvent.VK_ESCAPE)
       {
        System.exit(0);
       }
      }
     };
     */

    /**
     * Main of the program. Loads the Swing elements on the "Event
     * Dispatch Thread".
     *
     * @param args
     */

    public static void main(String args[]) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ImageViewerApp app = new ImageViewerApp();
                app.setVisible(true);                
            }
        });

    }
    
    /**
     * Default constructor
     */
    public ImageViewerApp() {

        //this.addKeyListener(kl);

        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Image Viewer: Please Select an Image");
        
        // A label for displaying the pictures
        photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
        photographLabel.setHorizontalTextPosition(JLabel.CENTER);
        photographLabel.setHorizontalAlignment(JLabel.CENTER);
        photographLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Two glue components to add thumbnail buttons to the toolbar inbetween thease glue compoents.
        buttonBar.add(Box.createGlue());
        buttonBar.add(Box.createGlue());
        
        add(buttonBar, BorderLayout.NORTH);
        add(photographLabel, BorderLayout.CENTER);        

        setSize(900, 700);
        //this.setResizable(false);

        // switching to fullscreen mode
        //GraphicsEnvironment.getLocalGraphicsEnvironment().
        //getDefaultScreenDevice().setFullScreenWindow(this);

        // this centers the frame on the screen
        setLocationRelativeTo(null);
        
        // start the image loading SwingWorker in a background thread
        loadimages.execute();
    }
     //Create a KeyListener that can listen when someone press Esc key on keyboard
     //You can change for what key that you want, by change value at:
     //VK_ESCAPE    

    
    /**
     * SwingWorker class that loads the images a background thread and calls publish
     * when a new one is ready to be displayed.
     */
    
    private SwingWorker<Void, ThumbnailAction> loadimages = new SwingWorker<Void, ThumbnailAction>() {
        
        /**
         * Creates full size and thumbnail versions of the target image files.
         */
        @Override
        protected Void doInBackground() throws Exception {
            for (int i = 0; i < imageCaptions.length; i++) {
                ImageIcon icon;
                icon = createImageIcon(imagedir + imageFileNames[i], imageCaptions[i]);
                
                ThumbnailAction thumbAction;
                if(icon != null){
                    
                    ImageIcon thumbnailIcon = new ImageIcon(getScaledImage(icon.getImage(), 64, 64));
                    
                    thumbAction = new ThumbnailAction(icon, thumbnailIcon, imageCaptions[i]);
                    
                }else{
                    // the image failed to load for some reason
                    // so load a placeholder instead
                    thumbAction = new ThumbnailAction(placeholderIcon, placeholderIcon, imageCaptions[i]);
                }
                publish(thumbAction);
            }            
            return null;
        }
        
        /**
         * Process all loaded images.
         */
        @Override
        protected void process(List<ThumbnailAction> chunks) {
            for (ThumbnailAction thumbAction : chunks) {
                JButton thumbButton = new JButton(thumbAction);
                // add the new button BEFORE the last glue
                // this centers the buttons in the toolbar
                buttonBar.add(thumbButton, buttonBar.getComponentCount() - 1);
            }
        }
    };
    
    /**
     * Creates an ImageIcon if the path is valid.
     * @param String - resource path
     * @param String - description of the file
     */
    protected ImageIcon createImageIcon(String path,
            String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
    
    /**
     * Action class that shows the image specified in it's constructor.
     */
    private class ThumbnailAction extends AbstractAction{
        
        /**
         *The icon if the full image we want to display.
         */
        private Icon displayPhoto;
        
        /**
         * @param Icon - The full size photo to show in the button.
         * @param Icon - The thumbnail to show in the button.
         * @param String - The descriptioon of the icon.
         */
        public ThumbnailAction(Icon photo, Icon thumb, String desc){
            displayPhoto = photo;
            
            // The short description becomes the tooltip of a button.
            putValue(SHORT_DESCRIPTION, desc);
            
            // The LARGE_ICON_KEY is the key for setting the
            // icon when an Action is applied to a button.
            putValue(LARGE_ICON_KEY, thumb);
        }
        
        /**
         * Shows the full image in the main area and sets the application title.
         */
        public void actionPerformed(ActionEvent e) {
            photographLabel.setIcon(displayPhoto);
            setTitle("Image Viewer: " + getValue(SHORT_DESCRIPTION).toString());
        }
    }
}
