package Electricity;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class LastBill extends JFrame implements ActionListener{
    JLabel l1;
    JTextArea t1, t2;
    JButton b1;
    JPanel p1;
    LastBill(){
        setSize(500,900);
        setLayout(new BorderLayout());
        
        p1 = new JPanel();
        
        l1 = new JLabel("Generate Bill");
        
        t2 = new JTextArea();
        
        t1 = new JTextArea(50,15);
        JScrollPane jsp = new JScrollPane(t1);
        t1.setFont(new Font("Senserif",Font.ITALIC,18));
        
        b1 = new JButton("Generate Bill");
        
        p1.add(l1);
        p1.add(t2);
        add(p1,"North");
        
        add(jsp,"Center");
        add(b1,"South");
        
        b1.addActionListener(this);
        
        setLocation(350,40);
    }
    public void actionPerformed(ActionEvent ae){
        String meterNumber = t2.getText(); // Assuming t2 is where meter number is input
        t1.setText(""); // Clear previous bill details

        if (meterNumber == null || meterNumber.trim().isEmpty()) {
            t1.setText("\n    Please enter a Meter Number.");
            return;
        }

        // Fetch Customer Details
        String customerQuery = "select name, meter, address, state, city, email, phone from customer where meter = ?";
        try (Conn conn = new Conn(); PreparedStatement psCustomer = conn.c.prepareStatement(customerQuery)) {
            psCustomer.setString(1, meterNumber);
            try (ResultSet rs = psCustomer.executeQuery()) {
                if (rs.next()) {
                    t1.append("\n    Customer Name: " + rs.getString("name"));
                    t1.append("\n    Meter Number:  " + rs.getString("meter"));
                    t1.append("\n    Address:       " + rs.getString("address"));
                    t1.append("\n    State:         " + rs.getString("state"));
                    t1.append("\n    City:          " + rs.getString("city"));
                    t1.append("\n    Email:         " + rs.getString("email"));
                    t1.append("\n    Phone Number:  " + rs.getString("phone"));
                    t1.append("\n-------------------------------------------------------------");
                    t1.append("\n");
                } else {
                    t1.append("\n    Customer details not found for meter: " + meterNumber);
                    t1.append("\n-------------------------------------------------------------");
                    t1.append("\n");
                    // Optionally, do not proceed if customer not found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            t1.append("\n    Error fetching customer details: " + e.getMessage());
        }

        t1.append("\nDetails of the Last Bills\n\n\n");
        
        // Fetch Bill Details
        // Assuming there's an 'amount' column in the 'bill' table. If not, it should be 'total_bill'.
        // Using 'total_bill' as it's more consistent with other classes.
        String billQuery = "select month, total_bill, status from bill where meter_no = ? order by month"; // Added order by month
        try (Conn conn = new Conn(); PreparedStatement psBill = conn.c.prepareStatement(billQuery)) {
            psBill.setString(1, meterNumber);
            try (ResultSet rs = psBill.executeQuery()) {
                boolean foundBills = false;
                while (rs.next()) {
                    foundBills = true;
                    t1.append("       Month: " + rs.getString("month") + 
                              "           Amount: " + rs.getString("total_bill") + 
                              "           Status: " + rs.getString("status") + "\n");
                }
                if (!foundBills) {
                    t1.append("    No bill details found for meter: " + meterNumber);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            t1.append("\n    Error fetching bill details: " + e.getMessage());
        } catch (Exception e) { // Catch any other unexpected errors
            e.printStackTrace();
            t1.append("\n    An unexpected error occurred: " + e.getMessage());
        }
    }
    
    public static void main(String[] args){
        new LastBill().setVisible(true);
    }
}

