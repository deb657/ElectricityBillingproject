package Electricity;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class PayBill extends JFrame implements ActionListener{
    JLabel l1,l2,l3,l4,l5, l6;
    JLabel l11, l12, l13, l14, l15;
    JTextField t1;
    Choice c1,c2;
    JButton b1,b2;
    String meter;
    PayBill(String meter){
        this.meter = meter;
        setLayout(null);
        
        setBounds(550, 220, 900, 600);
        
        JLabel title = new JLabel("Electricity Bill");
        title.setFont(new Font("Tahoma", Font.BOLD, 24));
        title.setBounds(120, 5, 400, 30);
        add(title);
        
        l1 = new JLabel("Meter No");
        l1.setBounds(35, 80, 200, 20);
        add(l1);
        
        JLabel l11 = new JLabel();
        l11.setBounds(300, 80, 200, 20);
        add(l11);
        
        JLabel l2 = new JLabel("Name");
        l2.setBounds(35, 140, 200, 20);
        add(l2);
        
        JLabel l12 = new JLabel();
        l12.setBounds(300, 140, 200, 20);
        add(l12);
        
        l3 = new JLabel("Month");
        l3.setBounds(35, 200, 200, 20);
        add(l3);
        
        c1 = new Choice();
        c1.setBounds(300, 200, 200, 20);
        c1.add("January");
        c1.add("February");
        c1.add("March");
        c1.add("April");
        c1.add("May");
        c1.add("June");
        c1.add("July");
        c1.add("August");
        c1.add("September");
        c1.add("October");
        c1.add("November");
        c1.add("December");
        add(c1);
        
        
        l4 = new JLabel("Units");
        l4.setBounds(35, 260, 200, 20);
        add(l4);
        
        JLabel l13 = new JLabel();
        l13.setBounds(300, 260, 200, 20);
        add(l13);
        
        l5 = new JLabel("Total Bill");
        l5.setBounds(35, 320, 200, 20);
        add(l5);
        
        JLabel l14 = new JLabel();
        l14.setBounds(300, 320, 200, 20);
        add(l14);
        
        l6 = new JLabel("Status");
        l6.setBounds(35, 380, 200, 20);
        add(l6);
        
        JLabel l15 = new JLabel();
        l15.setBounds(300, 380, 200, 20);
        l15.setForeground(Color.RED);
        add(l15);
        
        // Load initial customer and bill details
        loadCustomerDetails(l11, l12); // Pass JLabels to update
        loadBillDetails(c1.getSelectedItem(), l13, l14, l15); // Pass JLabels for bill details
        
        c1.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent ae){
                if (ae.getStateChange() == ItemEvent.SELECTED) {
                    loadBillDetails(c1.getSelectedItem(), l13, l14, l15);
                }
            }
        });
        
        b1 = new JButton("Pay");
        b1.setBounds(100, 460, 100, 25);
        add(b1);
        b2 = new JButton("Back");
        b2.setBounds(230, 460, 100, 25);
        add(b2);
        
        b1.setBackground(Color.BLACK);
        b1.setForeground(Color.WHITE);

        b2.setBackground(Color.BLACK);
        b2.setForeground(Color.WHITE);
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon/bill.png"));
        Image i2 = i1.getImage().getScaledInstance(600, 300,Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel l21 = new JLabel(i3);
        l21.setBounds(400, 120, 600, 300);
        add(l21);
        
        b1.addActionListener(this);
        b2.addActionListener(this);
        
        getContentPane().setBackground(Color.WHITE);        
    }
    
    private void loadCustomerDetails(JLabel meterLabel, JLabel nameLabel) {
        String customerQuery = "select meter, name from customer where meter = ?";
        try (Conn conn = new Conn(); PreparedStatement psCustomer = conn.c.prepareStatement(customerQuery)) {
            psCustomer.setString(1, this.meter);
            try (ResultSet rs = psCustomer.executeQuery()) {
                if (rs.next()) {
                    meterLabel.setText(rs.getString("meter"));
                    nameLabel.setText(rs.getString("name"));
                } else {
                    JOptionPane.showMessageDialog(this, "Customer not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customer details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBillDetails(String month, JLabel unitsLabel, JLabel totalBillLabel, JLabel statusLabel) {
        if (month == null) return; // Or handle as an error
        String billQuery = "select units, total_bill, status from bill where meter_no = ? AND month = ?";
        try (Conn conn = new Conn(); PreparedStatement psBill = conn.c.prepareStatement(billQuery)) {
            psBill.setString(1, this.meter);
            psBill.setString(2, month);
            try (ResultSet rs = psBill.executeQuery()) {
                if (rs.next()) {
                    unitsLabel.setText(rs.getString("units"));
                    totalBillLabel.setText(rs.getString("total_bill"));
                    statusLabel.setText(rs.getString("status"));
                    // Enable Pay button only if status is "Not Paid"
                    b1.setEnabled("Not Paid".equalsIgnoreCase(rs.getString("status"))); 
                } else {
                    unitsLabel.setText("");
                    totalBillLabel.setText("");
                    statusLabel.setText("Not Found");
                    b1.setEnabled(false); // Disable Pay button if bill not found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bill details for month " + month + ": " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            unitsLabel.setText("");
            totalBillLabel.setText("");
            statusLabel.setText("Error");
            b1.setEnabled(false);
        }
    }

    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == b1){ // Pay button
            String selectedMonth = c1.getSelectedItem();
            if (selectedMonth == null || selectedMonth.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a month.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Double check if bill is already paid before attempting to update
            if (!"Not Paid".equalsIgnoreCase(l15.getText())) { // l15 is statusLabel
                 JOptionPane.showMessageDialog(this, "Bill for " + selectedMonth + " is already " + l15.getText() + ".", "Information", JOptionPane.INFORMATION_MESSAGE);
                 return;
            }

            String updateQuery = "update bill set status = 'Paid' where meter_no = ? AND month = ?";
            try (Conn conn = new Conn(); PreparedStatement psUpdate = conn.c.prepareStatement(updateQuery)) {
                psUpdate.setString(1, this.meter);
                psUpdate.setString(2, selectedMonth);
                
                int rowsAffected = psUpdate.executeUpdate();
                if (rowsAffected > 0) {
                    // Successfully updated, now navigate to Paytm
                    this.setVisible(false);
                    new Paytm(meter).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update bill status. Please try again.", "Update Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error during payment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if(ae.getSource()== b2){ // Back button
            this.setVisible(false);
        }        
    }
    
    public static void main(String[] args){
        new PayBill("").setVisible(true);
    }
}
