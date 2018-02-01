import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
public class signup
{
	public static void main(String args[])
	{
	
		final JSeparator sep=new JSeparator();
		final JFrame jf=new JFrame();
		final JPanel p1=new JPanel();
		final JLabel title=new JLabel("Fill in the form");
		title.setBounds(140,0,150,20);
		p1.add(title);
		p1.setLayout(null);
		sep.setBounds(0,20,400,10);
		p1.add(sep);
		
		final JLabel name=new JLabel("User Name : ");
		name.setBounds(30,30,150,20);
		p1.add(name);
		
		final JTextField namet=new JTextField();
		namet.setBounds(150,30,200,20);
		p1.add(namet);
		
		final JLabel pass=new JLabel("Password : ");
		pass.setBounds(30,60,150,20);
		p1.add(pass);
		
		final JPasswordField passt=new JPasswordField();
		passt.setBounds(150,60,200,20);
		p1.add(passt);
		
		
		final JLabel phone=new JLabel("Phone Number : ");
		phone.setBounds(30,90,150,20);
		p1.add(phone);
		
		final JTextField number=new JTextField();
		number.setBounds(150,90,200,20);
		p1.add(number);
		
		final JLabel mail=new JLabel("Mail id : ");
		mail.setBounds(30,120,150,20);
		p1.add(mail);
		
		final JTextField mailt=new JTextField();
		mailt.setBounds(150,120,200,20);
		p1.add(mailt);
		
		final JButton submit=new JButton("Get me an account");
		submit.setBounds(30,150,320,30);
		p1.add(submit);
		submit.addActionListener(new ActionListener()
		{
    		@Override
    		public void actionPerformed(ActionEvent e)
    		{
			try
			{
				String uname=namet.getText(),upass=passt.getText(),umail=mailt.getText(),uphone=number.getText();
				String response=Client.RegisterUser(uname,upass,umail,uphone);
	    			String arg[]=new String[1];
				if(response.contains("Success"))
				{
					jf.dispose();
					login lg=new login();
					lg.main(arg);
				}
				prompt pm=new prompt();
	    			
				arg[0]=response;
	    			pm.main(arg);
			}
			catch(Exception ex)
			{		
			}
        	}
        });
		
		jf.add(p1);
		jf.setSize(600,400);
		jf.setVisible(true);
		jf.setLocationRelativeTo(null);
	}
}
