/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javachatclient.Struct;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author ubuntu
 */
public class OnlineUserRenderer implements ListCellRenderer{

    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
     
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
        isSelected, cellHasFocus);
        
        UserStruct uS = (UserStruct) value;
        String Path = System.getProperty("user.dir");
        //System.out.println(Path + "/images/");
        if (uS.status == 1){
            //renderer.setBackground(Color.green);
            ImageIcon imgThisImg = new ImageIcon(Path + "/images/circlegreen.png");
            Image img = imgThisImg.getImage();
            Image resizedImg = img.getScaledInstance(10, 10, java.awt.Image.SCALE_SMOOTH);
            imgThisImg = new ImageIcon(resizedImg);
            renderer.setIcon(imgThisImg);
        }
        else{
            ImageIcon imgThisImg = new ImageIcon(Path + "/images/circlered.png");
            Image img = imgThisImg.getImage();
            Image resizedImg = img.getScaledInstance(10, 10, java.awt.Image.SCALE_SMOOTH);
            imgThisImg = new ImageIcon(resizedImg);
            renderer.setIcon(imgThisImg);
            //renderer.setBackground(Color.gray);
        }
        
        return renderer;
    }

    
}
