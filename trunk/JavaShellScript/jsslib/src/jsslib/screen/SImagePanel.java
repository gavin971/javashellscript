
package jsslib.screen;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.image.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
public class SImagePanel extends JPanel {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 6156379275101333499L;
	private BufferedImage image;
	private int x;
	
	private int y;
	
	public SImagePanel()
	{
		super();   
	}
    
    /**
     * Erzeugt das Objekt mit dem übergebenen Image
     * @param image
     */
    public SImagePanel(BufferedImage image)
	{
		super();  
        this.image = image;
	}

	public SImagePanel(String dateiImg, int x, int y)
	{
		super();   
		this.setBackground(Color.WHITE);
		this.image = loadImage(dateiImg);   
		this.x = x;   
		this.y = y;   

	}
	public void setImage(String dateiImg, int x, int y)
	{
		this.setBackground(Color.WHITE);
		this.image = loadImage(dateiImg);   
		this.x = x;   
		this.y = y;   
	}

	protected void paintComponent(Graphics g) {   
		super.paintComponent(g);
	
		if(image != null)
		{
			double scaleFactor = 1;
			Graphics2D g2 = (Graphics2D)g;
			double scaleX = (double)(this.getWidth()-x)  / image.getWidth();
			double scaleY = (double)(this.getHeight()-y) / image.getHeight();
			if(scaleX <= scaleY) 
				scaleFactor = scaleX;
			else
				scaleFactor = scaleY;
			int newW = (int)(image.getWidth() * scaleFactor);
			int newH = (int)(image.getHeight() * scaleFactor);
            int xoffset = (this.getWidth()-newW)/2;
            int yoffset = (this.getHeight()-newH)/2;
            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

			g2.drawImage(image, xoffset, yoffset, newW, newH, null);
		}
		else
		{
			System.out.print("image null");
		}
	}   
	private BufferedImage loadImage(String ref) {  
		BufferedImage bimg = null;   
		try {   
		    bimg = ImageIO.read(new File(ref));   
		}
		catch (Exception e) {   
		    e.printStackTrace();   
		}   
		return bimg;   
	}  

}
