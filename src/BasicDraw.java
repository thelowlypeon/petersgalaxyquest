//package DrawStuff;

import java.awt.Toolkit;
import java.io.IOException;
//import java.io.FileOutputStream;
//import java.util.jar.*;

import javax.swing.*;

public class BasicDraw {

	JFrame myFrame;
	
	public BasicDraw() 
	{	
		Controller myCanvas = new Controller();
		myFrame = new JFrame();
		myFrame.setTitle("Peter's Galaxy Quest!");
		myFrame.setSize(300,300);
		
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		myFrame.getContentPane().add(myCanvas);
		myFrame.pack();
		myFrame.setResizable(false);
		myFrame.setVisible(true);
		
		Toolkit myToolKit = Toolkit.getDefaultToolkit();

	}
	
	public static void main(String[] args)  throws IOException
	{

		BasicDraw myBasicDraw = new BasicDraw();
		
		
//		JarOutputStream jar = 
//			 new JarOutputStream(new FileOutputStream("simple.jar"));
//			jar.putNextEntry(new JarEntry("stub"));
//			jar.close();
	}
	
	
}