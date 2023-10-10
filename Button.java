import javax.swing.*;
 
class SimpleButton{
 
   public static void main(String args[]){
 
       JFrame frame = new JFrame();
       JButton button = new JButton("Button");
 
       frame.add(button);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(400,400);
       frame.setLocationRelativeTo(null);
       frame.setVisible(true);
    }
}