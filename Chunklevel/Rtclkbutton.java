import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class Rtclkbutton
{
  JPopupMenu menu = new JPopupMenu("Popup");
  JMenuItem item;
    
  class RTButton extends JButton
  {
    public RTButton() 
    {
      super();
      addMouseListener(new PopupTriggerListener());
	 item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("Menu item Test1");
      }
    });
    menu.add(item);

    item = new JMenuItem("Test2");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println("Menu item Test2");
      }
    });
    menu.add(item);
    }
    class PopupTriggerListener extends MouseAdapter 
    {
      public void mousePressed(MouseEvent ev) 
	 {
        if (ev.isPopupTrigger()) 
	   {
          menu.show(ev.getComponent(), ev.getX(), ev.getY());
        }
      }
      public void mouseReleased(MouseEvent ev) 
	 {
        if (ev.isPopupTrigger()) 
	   {
          menu.show(ev.getComponent(), ev.getX(), ev.getY());
        }
      }

      public void mouseClicked(MouseEvent ev) 
	 {		 
      }
    }
  }
}