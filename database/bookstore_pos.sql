-- 1. Wipe the slate clean and start fresh
DROP DATABASE IF EXISTS bookstore_pos;
CREATE DATABASE bookstore_pos;
USE bookstore_pos;

-- 2. Create Tables (The Models)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role ENUM('ADMIN', 'MANAGER', 'CASHIER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    genre VARCHAR(50),
    isbn VARCHAR(20) UNIQUE,
    price DECIMAL(10, 2) NOT NULL,
    description TEXT
);

CREATE TABLE inventory (
    book_id INT PRIMARY KEY,
    quantity INT NOT NULL DEFAULT 0,
    low_stock_threshold INT NOT NULL DEFAULT 5,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

CREATE TABLE suppliers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT
);

CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    is_member BOOLEAN DEFAULT FALSE,
    points DECIMAL(10,2) DEFAULT 0.00,
    tier ENUM('BRONZE', 'SILVER', 'GOLD') DEFAULT 'BRONZE'
);

CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    customer_id INT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    discount_applied DECIMAL(10,2) DEFAULT 0.00,
    payment_method ENUM('CASH', 'CARD', 'E_WALLET') NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    book_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id)
);

-- 3. Insert Seed Data
-- ==========================================

-- System Admin Account (Login: admin / Admin@1234)
INSERT INTO users (username, password_hash, full_name, email, role) 
VALUES ('admin', 'dGVzdHNhbHQ=:2y74b6i+q94TjEok0qIuPZ3eC39/QJ+n+bN+8X/b+nQ=', 'System Administrator', 'admin@booknook.com', 'ADMIN');

-- Sample Books
INSERT INTO books (title, author, genre, isbn, price, description) VALUES 
('Noli Me Tangere', 'Jose Rizal', 'History', '978-9715082534', 360.00, 'Classic Philippine novel.'),
('El Filibusterismo', 'Jose Rizal', 'History', '978-9715082541', 400.00, 'Sequel to Noli Me Tangere.'),
('Smaller and Smaller Circles', 'F.H. Batacan', 'Mystery', '978-1616955343', 450.00, 'A gripping serial killer thriller set in Manila.'),
('The Mythology Class', 'Arnold Arre', 'Fantasy', '978-9719306001', 350.00, 'Philippine mythology meets modern day.'),
('Trese: Murder on Balete Drive', 'Budjette Tan', 'Horror', '978-9718161359', 250.00, 'Supernatural investigations in Metro Manila.'),
('Eyes of a Child', 'Richard North Patterson', 'Thriller', '978-0679429883', 1399.00, 'A gripping legal thriller about a devastating custody battle and a shocking murder.'),
('The Broker', 'John Grisham', 'Thriller', '978-0345532008', 580.00, 'A disgraced power broker is pardoned, only to be hunted by international assassins.'),
('Safe House', 'Andrew Vachss', 'Mystery', '978-0375719127', 1100.00, 'An intense, dark mystery following an underground investigator navigating the gritty streets.'),
('Iron House', 'John Hart', 'Thriller', '978-1250007018', 1050.00, 'Two orphaned brothers take drastically different paths in this tale of violence and redemption.'),
('Trail of Secrets', 'Eileen Goudge', 'Drama', '978-1423358961', 2300.00, 'A dramatic novel of interwoven lives, long-buried secrets, and the search for truth.'),
('Point of Origin', 'Patricia Cornwell', 'Crime Fiction', '978-1101207345', 580.00, 'Medical examiner Kay Scarpetta investigates a devastating fire linked to a cunning serial killer.'),
('Against The Wind', 'J.F. Freedman', 'Fiction', '978-0670841158', 1150.00, 'A compelling courtroom drama centered on a controversial and high-stakes criminal trial.'),
('Partner in Crime', 'J.A. Jance', 'Mystery', '978-0061749049', 580.00, 'A suspenseful whodunit featuring two investigators forced to team up to solve a baffling case.'),
('An Accidental Woman', 'Barbara Delinsky', 'Romance', '978-0743204705', 850.00, 'A poignant story of love, betrayal, and starting over when a small town is rocked by a scandal.'),
('Pearls', 'Celia Brayfield', 'Fiction', '978-0701131074', 750.00, 'A sweeping saga of ambition, romance, and the glamour of the international jewelry trade.');

-- Corresponding Inventory (Crucial for the POS to work)
-- Book ID 2 is purposely set to 3 to trigger your "Low Stock Alert" (Red text in the GUI)
INSERT INTO inventory (book_id, quantity, low_stock_threshold) VALUES 
(1, 20, 5),
(2, 3, 5), 
(3, 15, 5),
(4, 30, 10),
(5, 50, 10),
(6, 12, 4),
(7, 25, 5),
(8, 18, 5),
(9, 10, 3),
(10, 8, 2),
(11, 20, 5),
(12, 15, 5),
(13, 22, 5),
(14, 14, 4),
(15, 11, 3);

-- Sample Suppliers
INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES
('National Publishing House', 'Juan Dela Cruz', '09171234567', 'sales@nationalpub.ph', 'Quezon City, Metro Manila'),
('Visayas Book Distributors', 'Maria Santos', '09189876543', 'contact@visayasbooks.ph', 'Cebu City, Cebu');

-- Sample Customers
INSERT INTO customers (name, phone, is_member, points, tier) VALUES
('Walk-in Customer', '', FALSE, 0.00, 'BRONZE'),
('Arlo Veridiano', '09191112222', TRUE, 150.00, 'SILVER');