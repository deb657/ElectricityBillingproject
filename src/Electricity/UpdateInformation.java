
package Electricity;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;

public class UpdateInformation extends JFrame implements ActionListener{
    JTextField t1, t2, t3, t4, t5, t6, t7;
    JLabel l11, l12;
    JButton b1, b2;
    String meter;
    UpdateInformation(String meter){
        this.meter = meter;
        
        setBounds(500, 220, 1050, 450);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        
        JLabel title = new JLabel("UPDATE CUSTOMER INFORMATION");
        title.setBounds(110, 0, 400, 30);
        title.setFont(new Font("Tahoma", Font.PLAIN, 20));
        add(title);
        
        JLabel l1 = new JLabel("Name");
        l1.setBounds(30, 70, 100, 20);
        add(l1);
        
        l11 = new JLabel();
        l11.setBounds(230, 70, 200, 20);
        add(l11);
        
        JLabel l2 = new JLabel("Meter Number");
        l2.setBounds(30, 110, 100, 20);
        add(l2);
        
        l12 = new JLabel();
        l12.setBounds(230, 110, 200, 20);
        add(l12);
        
        JLabel l3 = new JLabel("Address");
        l3.setBounds(30, 150, 100, 20);
        add(l3);
        
        t1 = new JTextField();
        t1.setBounds(230, 150, 200, 20);
        add(t1);
        
        JLabel l4 = new JLabel("City");
        l4.setBounds(30, 190, 100, 20);
        add(l4);
        
        t2 = new JTextField();
        t2.setBounds(230, 190, 200, 20);
        add(t2);
        
        JLabel l5 = new JLabel("State");
        l5.setBounds(30, 230, 100, 20);
        add(l5);
        
        t3 = new JTextField();
        t3.setBounds(230, 230, 200, 20);
        add(t3);
        
        JLabel l6 = new JLabel("Email");
        l6.setBounds(30, 270, 100, 20);
        add(l6);
        
        t4 = new JTextField();
        t4.setBounds(230, 270, 200, 20);
        add(t4);
        
        JLabel l7 = new JLabel("Phone");
        l7.setBounds(30, 310, 100, 20);
        add(l7);
        
        t5 = new JTextField();
        t5.setBounds(230, 310, 200, 20);
        add(t5);
        
        b1 = new JButton("Update");
        b1.setBackground(Color.BLACK);
        b1.setForeground(Color.WHITE);
        b1.setBounds(70, 360, 100, 25);
        b1.addActionListener(this);
        add(b1);
        
        b2 = new JButton("Back");
        b2.setBackground(Color.BLACK);
        b2.setForeground(Color.WHITE);
        b2.setBounds(230, 360, 100, 25);
        b2.addActionListener(this);
        add(b2);
        
        // Load customer information
        loadCustomerInfo();
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/update.jpg"));
        Image i2  = i1.getImage().getScaledInstance(400, 300, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel l8  = new JLabel(i3);
        l8.setBounds(550, 50, 400, 300);
        add(l8);
    }
    
    
    private void loadCustomerInfo() {
        String query = "select name, meter, address, city, state, email, phone from customer where meter = ?";
        try (Conn conn = new Conn(); PreparedStatement pstmt = conn.c.prepareStatement(query)) {
            pstmt.setString(1, this.meter);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    l11.setText(rs.getString("name")); // Name label
                    l12.setText(rs.getString("meter")); // Meter number label
                    t1.setText(rs.getString("address")); // Address text field
                    t2.setText(rs.getString("city"));    // City text field
                    t3.setText(rs.getString("state"));   // State text field
                    t4.setText(rs.getString("email"));   // Email text field
                    t5.setText(rs.getString("phone"));   // Phone text field
                } else {
                    JOptionPane.showMessageDialog(this, "Customer details not found for meter: " + this.meter, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error loading customer details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == b1){ // Update button
            String address = t1.getText();
            String city = t2.getText();
            String state = t3.getText();
            String email = t4.getText();
            String phone = t5.getText();

            // Basic validation
            if (address.isEmpty() || city.isEmpty() || state.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String updateQuery = "update customer set address = ?, city = ?, state = ?, email = ?, phone = ? where meter = ?";
            
            try (Conn conn = new Conn(); PreparedStatement pstmt = conn.c.prepareStatement(updateQuery)) {
                pstmt.setString(1, address);
                pstmt.setString(2, city);
                pstmt.setString(3, state);
                pstmt.setString(4, email);
                pstmt.setString(5, phone);
                pstmt.setString(6, this.meter); // Meter number for the WHERE clause
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Details Updated Successfully");
                    this.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update details. Meter number might not exist or data is unchanged.", "Update Failed", JOptionPane.WARNING_MESSAGE);
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error updating details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        }else if(ae.getSource() == b2){ // Back button
            this.setVisible(false);
        }
    }
    
    public static void main(String[] args){
        new UpdateInformation("").setVisible(true);
        
    }
}
