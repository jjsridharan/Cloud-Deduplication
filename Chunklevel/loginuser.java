import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
public class loginuser{

	public static void main(String args[]){
		final JSeparator sep=new JSeparator();
		final JFrame jf=new JFrame();
		final JPanel p1=new JPanel();
		final JLabel title=new JLabel("Login Details");
		title.setBounds(140,0,150,20);
		p1.add(title);
		p1.setLayout(null);
		sep.setBounds(0,20,400,10);
		p1.add(sep);
		
		final JLabel name=new JLabel("User Name : ");
		name.setBounds(30,30,150,20);
		p1.add(name);
		
		final JTextField namet=new JTextField();
		namet.setBounds(150,32,200,20);
		p1.add(namet);
		
		final JLabel pass=new JLabel("Password : ");
		pass.setBounds(30,60,150,20);
		p1.add(pass);
		
		final JPasswordField passt=new JPasswordField();
		passt.setBounds(150,62,200,20);
		p1.add(passt);
		
		final JButton submit=new JButton("Login to my account");
		submit.setBounds(30,95,320,30);
		p1.add(submit);
		submit.addActionListener(new ActionListener()
		{
    		@Override
    		public void actionPerformed(ActionEvent e)
    		{
			try
			{
				String uname=namet.getText(),upass=passt.getText();
				String response=Client.Login(uname,upass);
				String arg[]=new String[2];
				prompt pm=new prompt();	
				if(!response.contains("Invalid"))
				{
					arg[0]="Successfully Logged in!";
					arg[1]=response;
					jf.dispose();
					MainUI ui=new MainUI();
					ui.main(arg);	
				}
				else
				{
					arg[0]=response;
				}			
		    		pm.main(arg);
			}
			catch(Exception ex)
			{	
				ex.printStackTrace();
			}
        	}
        	});
        	
		jf.add(p1);
		jf.setSize(500,300);
		jf.setVisible(true);
		jf.setLocationRelativeTo(null);
	}
}
