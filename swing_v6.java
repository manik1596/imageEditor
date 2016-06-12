import javax.swing.*;
import javax.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.*;
import javax.imageio.*;
import java.io.*;
import java.awt.image.*;
import javax.swing.JOptionPane.*;

class LoadImage extends Component {
	BufferedImage img;
	public LoadImage() {
		img = null;
	}
	public LoadImage(File f) {
		try {
			img = ImageIO.read(f);		//Reads image from system and stores in buffer
		} catch(IOException e) { JOptionPane.showMessageDialog(null,"Image cannot been loaded"); }
	}
	
	public LoadImage(BufferedImage b){
		img = b;
	}
	 public void paint(Graphics g) {
        g.drawImage(img, 0, 0, null);		//Draws image on desired location
    }
	public Dimension getPreferredSize() {
        if (img == null) {
             return new Dimension(100,100);		//If img is not present make empty window with dimensions 100,100
        } else {
           return new Dimension(img.getWidth(), img.getHeight());		//Creates a window of width and height of image
       }
    }
    public BufferedImage getImg() {
    	return img;
    }
	public void setImg(BufferedImage n) {
		img = n;
	}
}

class PixelNode {
	Color pixel;
	PixelNode next,prev;
	PixelNode() {
		pixel = null;
		next = null;
	}
	PixelNode(Color p) {
		this();
		pixel = p;
	}
	Color getPixel() { return pixel; }
	PixelNode getNext() { return next; }
	PixelNode getPrev() { return prev; }
	void setPixel(Color p) { pixel = p; }
	void setNext(PixelNode n) { next = n; }
	void setPrev(PixelNode p) { prev = p; }
}

class PixelList {
	public PixelNode first,last;
	PixelList() {
		first = last = null;
	}
	PixelNode traverseBack(PixelNode temp,int count) {
		for(int i = 0;i < count;++i) {
			if(temp.getPrev() == null) return null;
			else temp = temp.getPrev();
		}
		return temp;
	}
	PixelNode traverseForward(PixelNode temp,int count) {
		for(int i = 0;i < count;++i) {
			if(temp.getNext() == null) return null;
			else temp = temp.getNext();
		}
		return temp;
	}
	void addPixel(Color n) {
		PixelNode p = new PixelNode(n);
		if(first == null) {
			first = last = p;
		}
		else {
			p.setPrev(last);
			last.setNext(p);
			last = last.getNext();
		}
	}
	void display() {
		PixelNode temp = first;
		while(temp != null) {
			System.out.println(temp.getPixel().getRGB());
			temp = temp.getNext();
		}
	}
	BufferedImage toGrayscale(BufferedImage img) {
		PixelNode temp = first;
		int average;
		while(temp != null) {
			average = (temp.getPixel().getRed() + temp.getPixel().getGreen() + temp.getPixel().getBlue())/3;
			temp.setPixel(new Color(average,average,average));
			temp = temp.getNext();
		}
		temp = first;
		for(int i = 0;i < img.getWidth(); ++i)
			for(int j = 0;j < img.getHeight(); ++j) {
				img.setRGB(i,j,temp.getPixel().getRGB());
				temp = temp.getNext();
			}
		return img;
	}
	BufferedImage toWarm(BufferedImage img,int w) {
		PixelNode temp = first;
		int red;
		while(temp != null) {
			red = (temp.getPixel().getRed() * (w+100))/100;
			if(red > 255) red = 255;
			temp.setPixel(new Color(red,temp.getPixel().getGreen(),temp.getPixel().getBlue()));
			temp = temp.getNext();
		}
		temp = first;
		for(int i = 0;i < img.getWidth(); ++i)
			for(int j = 0;j < img.getHeight(); ++j) {
				img.setRGB(i,j,temp.getPixel().getRGB());
				temp = temp.getNext();
			}
		return img;
	}
	PixelNode checkNull(PixelNode n) {
		if(n == null) return new PixelNode(new Color(0,0,0));
		else return n;
	}
	BufferedImage toSoften(BufferedImage img) {
		PixelNode temp = first.getNext().getNext();
		PixelList newList = new PixelList();
		int r,g,b;
		while(temp.getNext().getNext() != null) {
			try {
				r = (temp.getPrev().getPrev().getPixel().getRed() + temp.getPrev().getPixel().getRed() + temp.getPixel().getRed() + temp.getNext().getPixel().getRed() + temp.getNext().getNext().getPixel().getRed())/5;
				g = (temp.getPrev().getPrev().getPixel().getGreen() + temp.getPrev().getPixel().getGreen() + temp.getPixel().getGreen() + temp.getNext().getPixel().getGreen() + temp.getNext().getNext().getPixel().getGreen())/5;
				b = (temp.getPrev().getPrev().getPixel().getBlue() + temp.getPrev().getPixel().getBlue() + temp.getPixel().getBlue() + temp.getNext().getPixel().getBlue() + temp.getNext().getNext().getPixel().getBlue())/5;
				newList.addPixel(new Color(r,g,b));
				temp = temp.getNext();
			} catch(Exception e) { System.out.println(e); }
		}
		temp = newList.first;
		for(int i = 0;i < img.getWidth(); ++i)
			for(int j = 0;j < img.getHeight(); ++j) {
				try {
					img.setRGB(i,j,temp.getPixel().getRGB());
					temp = temp.getNext();
				} catch(Exception e) { System.out.println(e); }
			}
		return img;
	}
}

public class swing_v6 {
	JFrame frame = new JFrame("Photo Editor");	// parameter is the title
	JPanel panel = new JPanel();
	JFrame pic,softenFrame,grayFrame,warmthFrame;
	LoadImage image,newImg;
	PixelList p;
	
	swing_v6() {
	frame.add(panel);
	panel.setLayout(new GridLayout(0,3));
	}

	void createWindow(int w,int h) {
		int width = w;
		int height = h;	
		frame.setVisible(true);		//makes Frame visible
		frame.setSize(w,h);		//size of the window
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//closes the program when window is closed
	}
	
	void createLabel(String l) {
		JLabel label = new JLabel(l);	
		panel.add(label);
	}
	
	void createOpenButton() {
		JButton button = new JButton("Open");
		panel.add(button);
		button.addActionListener(new openL());
	}
	
	void createFilter1Button() {
		JButton b2 = new JButton("GrayScale");
		panel.add(b2);
		b2.addActionListener(new grayscale());
	}
	
	void createFilter2Button() {
		JButton b3 = new JButton("Soften");
		panel.add(b3);
		b3.addActionListener(new soften());
	}
	
	void createFilter3Button() {
		JButton b4 = new JButton("Warmth");
		panel.add(b4);
		b4.addActionListener(new warmth());
	}
	
	void createExitButton() {
		JButton b4 = new JButton("Exit");
		panel.add(b4);
		b4.addActionListener(new exit());
	}
	
	void createSaveButton() {
		JButton b5 = new JButton("Save");
		panel.add(b5);
		b5.addActionListener(new save());
	}
	
	class soften implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				image.setImg(p.toSoften(image.getImg()));
				softenFrame = new JFrame("Soften Image");
				softenFrame.add(image);
				softenFrame.pack();
				softenFrame.setVisible(true);
			} catch(Exception er) {}
		}
	}
	
	class warmth implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				newImg.setImg(p.toWarm(image.getImg(),50));
				warmthFrame = new JFrame("Warm image");
				warmthFrame.add(newImg);
				warmthFrame.pack();
				warmthFrame.setVisible(true);
			} catch(Exception er) { JOptionPane.showMessageDialog(null,"Image has not been loaded"); }
		}
	}
	class grayscale implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				newImg.setImg(p.toGrayscale(image.getImg()));
				grayFrame = new JFrame("Gray Scale");
				grayFrame.add(newImg);
				grayFrame.pack();
				grayFrame.setVisible(true);
			} catch(Exception er) { JOptionPane.showMessageDialog(null,"Image has not been loaded"); }
		}
	}
	class openL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser c = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"JPG & PNG Images", "jpg", "png");
			c.setFileFilter(filter);
			int returnVal = c.showOpenDialog(null);
    		if(returnVal == JFileChooser.APPROVE_OPTION) {
    				pic = new JFrame(c.getSelectedFile().getName() + "");
    				image = newImg = new LoadImage(c.getSelectedFile());
            		pic.add(image);
            		pic.pack();
            		pic.setVisible(true);
					makeList();
  			}
		}
	}
	class exit implements ActionListener{
		public void actionPerformed(ActionEvent e){
			frame.dispose();
		}
	}
	class save implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser c = new JFileChooser();
			try {
				if(newImg == null) throw new Exception();
				int returnVal = c.showSaveDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File outputfile = c.getSelectedFile();
					
				}
			} catch(Exception f) {
				JOptionPane.showMessageDialog(null,"Image has not been loaded");
			}
		}
	}
	void makeList() {
		p = new PixelList();
		for(int i = 0;i < image.getImg().getWidth(); ++i)
			for(int j = 0;j < image.getImg().getHeight(); ++j)
				p.addPixel(new Color(image.getImg().getRGB(i,j)));
	}
	public static void main(String[] args) {
		swing_v6 s = new swing_v6();
		s.createWindow(800,600);
		s.createOpenButton();
		s.createFilter1Button();
		s.createFilter2Button();
		s.createFilter3Button();
		s.createSaveButton();
		s.createExitButton();
	}
}