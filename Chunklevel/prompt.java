import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
public class prompt{
	public static void main(String args[]){
		final JFrame jf=new JFrame();
		JLabel jl=new JLabel(args[0]);
		JButton ok=new JButton("OK");
		ok.addActionListener(new ActionListener()
		{
    		@Override
    		public void actionPerformed(ActionEvent e)
    		{
			jf.dispose();
        	}
        	});
        	JPanel p1=new JPanel();
        	p1.add(jl);
        	p1.add(ok);
        	jf.add(p1);
        	jf.setSize(400,100);
        	jf.setVisible(true);
        	jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.setLocationRelativeTo(null);
        }
}
