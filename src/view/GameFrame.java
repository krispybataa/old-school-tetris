package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {
    private JPanel contentPane;
    private BorderLayout borderLayout = new BorderLayout();

    public GameFrame(){
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try{
            initialize();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initialize(){
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout);
     }

     @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if(e.getID() == WindowEvent.WINDOW_CLOSING){
            System.exit(0);
        }
     }
}
