# Digital_Bookstore_PROJECT
#  The Book Nook - Digital Bookstore POS
**Finals Project - Java Programming (OOP)**
**Author:** Art Lorence Veridiano
**Institution:** Laguna University

##  Project Overview
The Book Nook POS is a comprehensive Point of Sale and Inventory Management System built entirely in Java. It features a custom-built dark theme UI, role-based access control, a secure MySQL database architecture using the DAO design pattern, and dynamic data visualization. 

---

##  1. Demonstration Flow (Presentation Guide)
*For the Panelists:* This system is divided into administrative management and cashier operations. Here is the workflow:

1. **Secure Login:** We start at the Login frame. The system uses SHA-256 cryptographic hashing to verify credentials. (Log in as `admin`).
2. **The Dashboard:** Upon login, the system routes Admins to the Dashboard. Here, you can see live revenue, stock alerts, and a custom-painted `Graphics2D` bar chart reflecting transaction volumes.
3. **Inventory & Suppliers:** Navigate to Inventory. We feature real-time regex searching. If I type "letters" into the stock or price fields when adding a book, the system performs strict input validation to prevent crashes.
4. **Memberships:** The system handles Walk-ins, Silver, Gold, and Platinum members. Upgrades are handled dynamically based on accumulated points.
5. **Point of Sale (POS):** Finally, we switch to the Cashier view. I will search for a book using the dual-filter system (Text + Genre Dropdown), add it to the cart, select a VIP customer to demonstrate polymorphic discounts, and process a secure cash checkout which generates a formatted `.txt` receipt.

---

##  2. Core Technologies & GUI Library
* **Language:** Java (JDK 8+)
* **GUI Library:** Java Swing & AWT. *Note: Default Swing UI was completely overridden using a centralized `UITheme.java` utility to enforce a modern, cohesive Dark Mode across all panels, buttons, and tables.*
* **Database:** MySQL via XAMPP.
* **Database Connector:** JDBC (Java Database Connectivity).

---

##  3. Object-Oriented Programming (OOP) Concepts Used
This project strictly adheres to OOP principles to ensure code modularity and maintainability:

* **Encapsulation:** All models (`Book`, `User`, `Order`) have private fields, accessed only via public Getters and Setters. This protects the data integrity.
* **Inheritance:** The `Member` class extends the base `Customer` class. A Member inherits all base customer traits (name, phone) but adds `Membership` specific data.
* **Polymorphism (Method Overriding):** * The `Customer` class has a method `applyDiscount(double total)` which returns `0.0`. 
  * The `Member` subclass *overrides* this method to return `total * discountPercent`. 
  * *Why it matters:* In `SalesService`, the system calls `customer.applyDiscount(total)` without needing to check if they are a VIP or a Walk-in. The object handles its own math.
* **Abstraction:** The Database logic is abstracted using the **DAO (Data Access Object)** pattern. The UI panels do not contain SQL code; they call methods like `bookDAO.getAll()`, hiding the complex database logic from the presentation layer.

---

##  4. Advanced System Features

### A. The Dashboard Graph (`Graphics2D`)
Instead of relying on external libraries like JFreeChart, the dashboard bar chart is painted natively from scratch. 
* **How it works:** It extends a `JPanel` and overrides the `paintComponent(Graphics g)` method. 
* It casts `Graphics` to `Graphics2D` to enable anti-aliasing (smooth edges). It calculates the height and width of the panel, reads the size of the `List<Order>` fetched from the database, and uses `g2d.fillRoundRect()` to dynamically draw the bars relative to the panel size.

### B. Password Hashing (Security)
Storing plain-text passwords is a security risk. The `PasswordHasher.java` utility secures user accounts:
* **How it works:** It uses `java.security.MessageDigest` to apply an **SHA-256 hash**.
* **Salting:** Before hashing, it generates a 16-byte random "Salt" using `SecureRandom`. This prevents "Rainbow Table" attacks. The salt and the hash are encoded in Base64 and stored in the database as `salt:hash`.

### C. Secure Database Connections (Singleton Pattern)
Opening too many database connections will crash MySQL. 
* **How it works:** `DatabaseConnection.java` uses the **Singleton Pattern**. It has a `private` constructor and a `public static` method. Before giving the system a connection, it checks `if (instance == null || instance.connection.isClosed())`. This guarantees the entire app shares exactly one secure, active connection to the database.

### D. SQL Transactions (ACID Compliance)
In `OrderDAO.java`, when a checkout occurs, the system must save the `Order` and multiple `OrderItems`. 
* **How it works:** It disables auto-saving using `conn.setAutoCommit(false)`. It then attempts to save the receipt and deduct the stock. If *any* part of the transaction fails (e.g., power outage midway), it triggers `conn.rollback()`, erasing the half-finished data so money and inventory are never out of sync.

---

##  5. Setup & Installation
1. Install **XAMPP** and start the Apache and MySQL modules.
2. Open phpMyAdmin (`localhost/phpmyadmin`) and create a database named `bookstore_pos`.
3. Import the `bookstore_pos.sql` file provided in the `database` folder.
4. Open the project folder in VS Code or Eclipse. Ensure the `mysql-connector-j.jar` file is added to your Java Build Path / Referenced Libraries.
5. Run `Main.java` to start the application. 
   * **Default Admin Login:** `admin` / `admin` *(Dev testing bypass active)*.