import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class login{
	
	public static void main(String args[]){
		
		final JFrame jf=new JFrame();
		JLabel jl1=new JLabel("DATA COMPRESSION AND STORAGE TOOL");
		JLabel jl2=new JLabel("                        Copyright 2018");
		JButton log=new JButton("Login");
		log.addActionListener(new ActionListener()
		{
    		@Override
    		public void actionPerformed(ActionEvent e)
    		{
    			loginuser lg=new loginuser();
    			String args[]=new String[1];
    			lg.main(args);
    			jf.dispose();
        	}
        	});
		JButton sign=new JButton("Sign up");
		sign.addActionListener(new ActionListener()
		{
    		@Override
    		public void actionPerformed(ActionEvent e)
    		{
    			signup sign=new signup();
    			String args[]=new String[1];
    			signup.main(args);
			jf.dispose();
        	}
        	});
		JPanel p1=new JPanel();
		JPanel p2=new JPanel();
		JPanel p3=new JPanel();
		p1.setLayout(new BoxLayout(p1,BoxLayout.Y_AXIS));
		p1.add(jl1);
		p1.add(jl2);
		p2.add(p1);
		p3.add(log);
		p3.add(sign);
		jf.add(p2,BorderLayout.PAGE_START);
		jf.add(p3,BorderLayout.CENTER);
		jf.setSize(500,300);
		jf.setVisible(true);
		jf.setLocationRelativeTo(null);
	}
}

