import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
public class fp{
	public static void main(String args[]){
		final JFrame jf=new JFrame();
		final JPanel p1=new JPanel();
		p1.setLayout(null);

		final JLabel det=new JLabel("Enter your user name and phone number ");
		det.setBounds(30,20,240,20);
		p1.add(det);


		final JLabel name=new JLabel("User Name : ");
		name.setBounds(30,50,230,20);
		p1.add(name);

		final JTextField namet=new JTextField();
		namet.setBounds(150,52,260,30);
		p1.add(namet);

		final JLabel phone=new JLabel("Phone Number : ");
		phone.setBounds(30,80,230,20);
		p1.add(phone);


		final JTextField phone2=new JTextField();
		phone2.setBounds(150,82,260,30);
		p1.add(phone2);
		
		JButton ok=new JButton("Show my password");
		ok.setBounds(150,134,300,30);
		ok.addActionListener(new ActionListener()
		{
    		@Override
    		public void actionPerformed(ActionEvent e)
    		{

			prompt pm=new prompt();	
				String response="";
				String uname=namet.getText(),uphone=phone2.getText();
				try{
				response=Client.ForgotPassword(uname,uphone);
				}
				catch(Exception epc){}
				String arg[]=new String[3];
				if(response=="Invalid Credentials")
					arg[0]=response;
				else 
					arg[0]="Your password is "+response;
				prompt.main(arg);
	
        	}
        	});
        	p1.add(ok);
        	jf.add(p1);
        	jf.setSize(500,300);
        	jf.setVisible(true);
        	jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.setLocationRelativeTo(null);
        }
}
