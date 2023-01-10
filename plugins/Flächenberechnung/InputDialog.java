import ij.gui.*;
import ij.gui.ImageCanvas;
import ij.*;
import ij.plugin.frame.RoiManager;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.Arrays;

import java.awt.*;
import java.util.Properties;
import java.awt.image.*;
import ij.process.*;
import ij.measure.*;
import ij.plugin.*;
import ij.plugin.frame.Recorder;
import ij.plugin.frame.RoiManager;
import ij.plugin.filter.Analyzer;
import ij.plugin.tool.PlugInTool;
import ij.macro.*;
import ij.*;
import ij.util.*;
import ij.text.*;
import java.awt.event.*;
import java.util.*;
import java.awt.geom.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
* This is a non-modal dialog box used to ask the user to perform some task
* while a macro or plugin is running. It implements the waitForUser() macro
* function. It is based on Michael Schmid's Wait_For_User plugin.
*/
public class InputDialog extends Dialog implements ActionListener, KeyListener {
    protected Button button;
	protected Button button_back;
	protected Button button_delete;
	protected Button button_getSelection;
    protected MultiLineLabel label;
    static protected int xloc=-1, yloc=-1;
    private boolean escPressed;
	
	private static boolean protectStatusBar;
	private static Thread statusBarThread;
	
	private ImagePlus impplus;
	public String openerType;
	public Roi saveroi;
    
    public InputDialog(String title, String text, String type, ImagePlus imp) {
        super(IJ.getInstance(), title, false);
        protectStatusBar(false);
        if (text!=null && text.startsWith("IJ: "))
            text = text.substring(4);
        label = new MultiLineLabel(text, 175);
        if (!IJ.isLinux()) label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        if (IJ.isMacOSX()) {
            RoiManager rm = RoiManager.getInstance();
            if (rm!=null) rm.runCommand("enable interrupts");
        }
        GridBagLayout gridbag = new GridBagLayout(); //set up the layout
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);
        c.insets = new Insets(6, 6, 0, 6); 
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.WEST;
        add(label,c); 
        
		button = new Button("  OK  ");
        button.addActionListener(this);
        button.addKeyListener(this);
        c.insets = new Insets(2, 6, 6, 6); 
        c.gridx = 0; c.gridy = 2; c.anchor = GridBagConstraints.EAST;
        add(button, c);
		
		impplus = imp;
		openerType = type;
		saveroi = imp.getRoi();
		
		if(type.equals("Points")){
			/*button_back = new Button("  BACK  ");
			button_back.addActionListener(this);
			button_back.addKeyListener(this);
			c.insets = new Insets(2, 6, 6, 6); 
			c.gridx = 0; c.gridy = 2; c.anchor = GridBagConstraints.CENTER;
			add(button_back, c);*/
			
			button_delete = new Button("  DELETE  ");
			button_delete.addActionListener(this);
			button_delete.addKeyListener(this);
			c.insets = new Insets(2, 6, 6, 6); 
			c.gridx = 0; c.gridy = 2; c.anchor = GridBagConstraints.WEST;
			add(button_delete, c);			
		}
		
		if(type.equals("Baitplates")){
			/*button_back = new Button("  BACK  ");
			button_back.addActionListener(this);
			button_back.addKeyListener(this);
			c.insets = new Insets(2, 6, 6, 6); 
			c.gridx = 0; c.gridy = 2; c.anchor = GridBagConstraints.CENTER;
			add(button_back, c);*/
			
			button_delete = new Button("  DELETE  ");
			button_delete.addActionListener(this);
			button_delete.addKeyListener(this);
			c.insets = new Insets(2, 6, 6, 6); 
			c.gridx = 0; c.gridy = 2; c.anchor = GridBagConstraints.WEST;
			add(button_delete, c);			
		}
		
		if(type.equals("AKWHA")){
			/*button_back = new Button("  BACK  ");
			button_back.addActionListener(this);
			button_back.addKeyListener(this);
			c.insets = new Insets(2, 6, 6, 6); 
			c.gridx = 0; c.gridy = 2; c.anchor = GridBagConstraints.CENTER;
			add(button_back, c);*/
			
			button_delete = new Button("  DELETE  ");
			button_delete.addActionListener(this);
			button_delete.addKeyListener(this);
			c.insets = new Insets(2, 6, 6, 6); 
			c.gridx = 0; c.gridy = 2; c.anchor = GridBagConstraints.WEST;
			add(button_delete, c);			
		}
		
		if(type.equals("Rectangle")){			
			button_delete = new Button("  DELETE  ");
			button_delete.addActionListener(this);
			button_delete.addKeyListener(this);
			c.insets = new Insets(2, 6, 6, 6); 
			c.gridx = 0; c.gridy = 2; c.anchor = GridBagConstraints.WEST;
			add(button_delete, c);			
		}
		
		if(type.equals("Line")){			
			button_delete = new Button("  DELETE  ");
			button_delete.addActionListener(this);
			button_delete.addKeyListener(this);
			c.insets = new Insets(2, 6, 6, 6); 
			c.gridx = 0; c.gridy = 2; c.anchor = GridBagConstraints.WEST;
			add(button_delete, c);			
		}
		
		if(type.equals("AlterSelection")){
			button_getSelection = new Button("  GET SELECTION  ");
			button_getSelection.addActionListener(this);
			button_getSelection.addKeyListener(this);
			c.insets = new Insets(2, 6, 6, 6); 
			c.gridx = 0; c.gridy = 2; c.anchor = GridBagConstraints.CENTER;
			add(button_getSelection, c);
			
			button_delete = new Button("  DELETE  ");
			button_delete.addActionListener(this);
			button_delete.addKeyListener(this);
			c.insets = new Insets(2, 6, 6, 6); 
			c.gridx = 0; c.gridy = 2; c.anchor = GridBagConstraints.WEST;
			add(button_delete, c);			
		}
        
		setResizable(false);
        addKeyListener(this);
        pack();
        
		if (xloc==-1)
            centerOnImageJScreen(this);
        else
            setLocation(xloc, yloc);
        
		setAlwaysOnTop(true);
    }
	
	public static void protectStatusBar(boolean protect) {
		protectStatusBar = protect;
		if (!protectStatusBar)
			statusBarThread = null;
	}
	
	public static void centerOnImageJScreen(Window win) {
		center(win, IJ.getInstance());
	}

	/** Positions the specified window in the center of the screen that contains target. */
	public static void center(Window win, Component target) {
		if (win == null)
			return;
		Rectangle bounds = getMaxWindowBounds(target);
		Dimension window = win.getSize();
		if (window.width == 0)
			return;
		int left = bounds.x + Math.max(0, (bounds.width - window.width) / 2);
		int top = bounds.y + Math.max(0, (bounds.height - window.height) / 4);
		win.setLocation(left, top);
	}
	
	public static Rectangle getMaxWindowBounds(Component component) {
		return getScreenBounds(component, true);
	}
	
	public static Rectangle getScreenBounds(Component component, boolean accountForInsets) {
		if (GraphicsEnvironment.isHeadless())
			return new Rectangle(0,0,0,0);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();		
		GraphicsConfiguration gc = component == null ? ge.getDefaultScreenDevice().getDefaultConfiguration() :
													   component.getGraphicsConfiguration();   
		Insets insets = accountForInsets ? Toolkit.getDefaultToolkit().getScreenInsets(gc) : null;
		return shrinkByInsets(gc.getBounds(), insets);
	}
	
	private static Rectangle shrinkByInsets(Rectangle bounds, Insets insets) {
		Rectangle shrunk = new Rectangle(bounds);
		if (insets == null) return shrunk; 
		shrunk.x += insets.left;
		shrunk.y += insets.top;
		shrunk.width -= insets.left + insets.right;
		shrunk.height -= insets.top + insets.bottom;
		return shrunk;
	}
    
    /*public InputDialog(String text) {
        this("Action Required", text);
    }*/

    public void show() {
        super.show();
        synchronized(this) {  //wait for OK
            try {wait();}
            catch(InterruptedException e) {return;}
        }
    }
    
    public void close() {
        synchronized(this) { notify(); }
        xloc = getLocation().x;
        yloc = getLocation().y;
        dispose();
    }

    //public void actionPerformed(ActionEvent e) {
    //    close();
    //}
	
	public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == button) {
			//Abfrage ob mehr als ein Punkt eingegeben wurde
        	if(openerType.equals("Rectangle") || openerType.equals("Points") || openerType.equals("Line") || openerType.equals("AKWHA") || openerType.equals("Baitplates")){
        		Roi rois;
				rois = impplus.getRoi();
				
				if(rois != null) {
					if(openerType.equals("AKWHA")){
						Point[] points;
						points = rois.getContainedPoints();
						if(points.length == 12) {
							close();
						} else {
							System.out.println("Fehler: keine ausreichende Auswahl getätigt");
						}
					} else {
						if(openerType.equals("Baitplates")){
							//impplus.deleteRoi();
						}
						close();
					}
				} else {
					//close();
					System.out.println("Fehler: keine Auswahl getätigt");
				}
			} else {
				close();
			}
        	
        	
        }
        if(source == button_getSelection) {
        	impplus.deleteRoi();
        	impplus.setRoi(saveroi);
        }
		if (source == button_delete) {
            
			if(openerType.equals("Rectangle")){
				IJ.setTool("rectangle");
			}
			
			if(openerType.equals("Points")){
				IJ.setTool("multipoint");
			} 
			
			if(openerType.equals("Line")){
				IJ.setTool("line");
			}
			
			impplus.deleteRoi();
			
			/*
			//Anzeigen aller ROIs
			Roi rois;
			rois = impplus.getRoi();
			Point[] points;
			
			if( rois instanceof PointRoi ){
				points = rois.getContainedPoints();
				//System.out.println("Length: " + points.length);
				
				for(int i = 0; i < points.length; i++){
					int x = points[i].x;
					int y = points[i].y;
					System.out.println(String.valueOf(i) + ": " + String.valueOf(x) + "," + String.valueOf(y));
				}
				
			}*/	
			
        }
    }
	
	Point[] removeLast(Point[] remove_array){
		int length = remove_array.length - 1;
		Point[] removedElement = Arrays.copyOf(remove_array, length);
		return removedElement;	
	}
	
    public void keyPressed(KeyEvent e) { 
        int keyCode = e.getKeyCode(); 
        IJ.setKeyDown(keyCode); 
        if (keyCode==KeyEvent.VK_ENTER || keyCode==KeyEvent.VK_ESCAPE) {
            escPressed = keyCode==KeyEvent.VK_ESCAPE;
            close();
        }
    }
    
    public boolean escPressed() {
        return escPressed;
    }
    
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode(); 
        IJ.setKeyUp(keyCode); 
    }
    
    public void keyTyped(KeyEvent e) {}
    
    /** Returns a reference to the 'OK' button */
    public Button getButton() {
        return button;
    }
    
    /** Display the next WaitForUser dialog at the specified location. */
    public static void setNextLocation(int x, int y) {
        xloc = x;
        yloc = y;
    }
}

