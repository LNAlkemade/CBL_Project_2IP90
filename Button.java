import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;;
 
class Elements{
    public void main(){  
        JFrame frame = new JFrame("Yahtzee");  
        JPanel panel = new JPanel();  
        panel.setLayout(new FlowLayout());  
        JButton button = new JButton();  
        button.setText("Start Game");  
        panel.add(button);  
        frame.add(panel); 
        frame.add(button, BorderLayout.SOUTH); 
        frame.setSize(200, 300);  
        frame.setLocationRelativeTo(null);  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        frame.setVisible(true);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When the button is clicked, hide it
                button.setVisible(false);
            }
        });
    } 

    public static void main(String[] args){
        new Elements().main();
    }
}