# Electricity Billing System

## Description

The Electricity Billing System is a Java-based desktop application designed to manage electricity consumption data for customers. It provides functionalities for administrators to manage customer accounts, meter information, and billing, and for customers to view their bills and (simulated) make payments.

## Features

*   **User Authentication:** Secure login and signup functionalities for both Admins and Customers.
*   **Customer Management:**
    *   Register new customers.
    *   View detailed customer information.
    *   Update existing customer information.
*   **Meter Information Management:** Record and manage meter details for customers.
*   **Bill Calculation:** Automatically calculate electricity bills based on units consumed and predefined tax structures.
*   **Bill Generation & Viewing:**
    *   Generate detailed monthly bills for customers.
    *   View past bill details and their payment status.
*   **Online Bill Payment (Simulated):** A simulated payment process using a mock Paytm interface.
*   **Deposit Details:** View a history of bill payments and deposits.

## Technologies Used

*   **Java:** Core programming language.
    *   **Java Swing:** For the Graphical User Interface (GUI).
*   **MySQL:** Relational database management system to store all application data.
*   **rs2xml.jar:** Library used for displaying data in JTables (from ResultSets).
*   **MySQL Connector/J:** JDBC driver for MySQL database connectivity.

## Prerequisites

*   **Java Development Kit (JDK):** Version 8 or higher.
*   **MySQL Server:** Version 5.7 or higher recommended.
*   **Java IDE:** Apache NetBeans IDE is recommended (project files for NetBeans are included). However, any IDE that supports Java Swing applications and Ant projects (e.g., IntelliJ IDEA, Eclipse with Ant plugin) can be used.

## Setup Instructions

### 1. Database Setup

a.  **Ensure MySQL Server is Running:** Start your MySQL server if it's not already running.

b.  **Create Database:** Create a new database named `ebs`.
    ```sql
    CREATE DATABASE ebs;
    USE ebs;
    ```

c.  **Create Tables:** Execute the following SQL statements to create the necessary tables.
    *(Note: These schemas are inferred from the application's code. You may need to adjust data types or constraints based on specific requirements.)*

    ```sql
    CREATE TABLE login (
        meter_no VARCHAR(20) PRIMARY KEY,
        username VARCHAR(50) UNIQUE,
        name VARCHAR(100),
        password VARCHAR(50),
        user VARCHAR(20) -- 'Admin' or 'Customer'
    );

    CREATE TABLE customer (
        name VARCHAR(100),
        meter VARCHAR(20) PRIMARY KEY,
        address VARCHAR(255),
        city VARCHAR(50),
        state VARCHAR(50),
        email VARCHAR(100),
        phone VARCHAR(20),
        FOREIGN KEY (meter) REFERENCES login(meter_no)
    );

    CREATE TABLE meter_info (
        meter_number VARCHAR(20) PRIMARY KEY,
        meter_location VARCHAR(50),
        meter_type VARCHAR(50),
        phase_code VARCHAR(20),
        bill_type VARCHAR(50),
        days VARCHAR(10), -- Represents bill cycle days, e.g., "30"
        FOREIGN KEY (meter_number) REFERENCES login(meter_no)
    );

    CREATE TABLE tax (
        id INT AUTO_INCREMENT PRIMARY KEY, -- Added an ID for uniqueness if needed
        cost_per_unit DECIMAL(10, 2),
        meter_rent DECIMAL(10, 2),
        service_charge DECIMAL(10, 2),
        service_tax DECIMAL(10, 2),
        swacch_bharat_cess DECIMAL(10, 2),
        fixed_tax DECIMAL(10, 2)
    );
    -- You'll need to insert one row of tax data for the system to work
    -- Example:
    -- INSERT INTO tax (cost_per_unit, meter_rent, service_charge, service_tax, swacch_bharat_cess, fixed_tax) VALUES (9.00, 47.00, 22.00, 56.00, 6.00, 18.00);


    CREATE TABLE bill (
        meter_no VARCHAR(20),
        month VARCHAR(20),
        units VARCHAR(20), -- Consider changing to INT if appropriate
        total_bill VARCHAR(20), -- Consider changing to DECIMAL(10,2) if appropriate
        status VARCHAR(20), -- e.g., 'Paid', 'Not Paid'
        PRIMARY KEY (meter_no, month),
        FOREIGN KEY (meter_no) REFERENCES login(meter_no)
    );
    ```

d.  **Configure Database Credentials:**
    *   Open the `src/db.properties` file.
    *   Update the `db.user` and `db.password` properties with your MySQL username and password if they are different from the current values (root, Devi@657).
        ```properties
        db.url=jdbc:mysql:///ebs
        db.user=your_mysql_username
        db.password=your_mysql_password
        ```

### 2. Project Setup

a.  **Clone the Repository:**
    ```bash
    git clone <repository_url>
    cd <repository_directory>
    ```

b.  **Open in IDE:**
    *   Open the project using your preferred Java IDE (e.g., Apache NetBeans).
    *   NetBeans should automatically recognize the project structure.

c.  **Libraries/Classpath:**
    *   Ensure the JDBC driver `mysql-connector-java-8.0.27.jar` and the table display library `rs2xml.jar` are correctly included in your project's build path or library configuration.
    *   These JAR files are located in the root directory of this project. If your IDE does not automatically add them, you may need to add them manually to the project libraries.
        *   In NetBeans: Right-click on "Libraries" in the project tree -> "Add JAR/Folder" -> Select the JARs.

## How to Run

1.  **Compile the Project:** Your IDE should handle compilation automatically, or you can use Ant if `build.xml` is fully configured.
2.  **Run the Application:**
    *   The main entry point of the application is `Electricity.Login`.
    *   In your IDE, locate this class and run it.

This will launch the Login window, from where users can access the system's functionalities based on their roles.
