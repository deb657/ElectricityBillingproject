
package Electricity;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;

public class ViewInformation extends JFrame implements ActionListener{
    JButton b1;
    ViewInformation(String meter){
        setBounds(600,250, 850, 650);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        JLabel title = new JLabel("VIEW CUSTOMER INFORMATION");
        title.setBounds(250, 0, 500, 40);
        title.setFont(new Font("Tahoma", Font.PLAIN, 20));
        add(title);
        
        JLabel l1 = new JLabel("Name");
        l1.setBounds(70, 80, 100, 20);
        add(l1);
        
        JLabel l11 = new JLabel();
        l11.setBounds(250, 80, 100, 20);
        add(l11);
        
        JLabel l2 = new JLabel("Meter Number");
        l2.setBounds(70, 140, 100, 20);
        add(l2);
        
        JLabel l12 = new JLabel();
        l12.setBounds(250, 140, 100, 20);
        add(l12);
        
        JLabel l3 = new JLabel("Address");
        l3.setBounds(70, 200, 100, 20);
        add(l3);
        
        JLabel l13 = new JLabel();
        l13.setBounds(250, 200, 100, 20);
        add(l13);
        
        JLabel l4 = new JLabel("City");
        l4.setBounds(70, 260, 100, 20);
        add(l4);
        
        JLabel l14 = new JLabel();
        l14.setBounds(250, 260, 100, 20);
        add(l14);
        
        JLabel l5 = new JLabel("State");
        l5.setBounds(500, 80, 100, 20);
        add(l5);
        
        JLabel l15 = new JLabel();
        l15.setBounds(650, 80, 100, 20);
        add(l15);
        
        JLabel l6 = new JLabel("Email");
        l6.setBounds(500, 140, 100, 20);
        add(l6);
        
        JLabel l16 = new JLabel();
        l16.setBounds(650, 140, 150, 20);
        add(l16);
        
        JLabel l7 = new JLabel("Phone");
        l7.setBounds(500, 200, 100, 20);
        add(l7);
        
        JLabel l17 = new JLabel();
        l17.setBounds(650, 200, 100, 20);
        add(l17);
        
        loadCustomerInfo(meter);
        
        b1 = new JButton("Back");
        b1.setBackground(Color.BLACK);
        b1.setForeground(Color.WHITE);
        b1.setBounds(350, 340, 100, 25);
        b1.addActionListener(this);
        add(b1);
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/viewcustomer.jpg"));
        Image i2 = i1.getImage().getScaledInstance(600, 300, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel l8  = new JLabel(i3);
        l8.setBounds(20, 350, 600, 300);
        add(l8);
    }
    
    
    private void loadCustomerInfo(String meterNumber) {
        String query = "select name, meter, address, city, state, email, phone from customer where meter = ?";
        try (Conn conn = new Conn(); PreparedStatement pstmt = conn.c.prepareStatement(query)) {
            pstmt.setString(1, meterNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    l11.setText(rs.getString("name"));    // Name
                    l12.setText(rs.getString("meter"));   // Meter Number
                    l13.setText(rs.getString("address")); // Address
                    l14.setText(rs.getString("city"));    // City
                    l15.setText(rs.getString("state"));   // State
                    l16.setText(rs.getString("email"));   // Email
                    l17.setText(rs.getString("phone"));   // Phone
                } else {
                    JOptionPane.showMessageDialog(this, "Customer details not found for meter: " + meterNumber, "Error", JOptionPane.ERROR_MESSAGE);
                    // Optionally clear labels or set to "Not Found"
                    l11.setText(""); l12.setText(""); l13.setText(""); l14.setText("");
                    l15.setText(""); l16.setText(""); l17.setText("");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error loading customer details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { // Catch other unexpected errors
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void actionPerformed(ActionEvent ae){
        if (ae.getSource() == b1) { // Back button
            this.setVisible(false);
        }
    }
    
    public static void main(String[] args){
        new ViewInformation("").setVisible(true);
    }
}
