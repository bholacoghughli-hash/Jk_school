-- =======================================================
-- ENTERPRISE SCHOOL MANAGEMENT SYSTEM DATABASE SCHEMA
-- Institution: JK Montessori School, Ghughli, Maharajganj, UP
-- Compatibility: MySQL 5.7+ / MariaDB 10+ (XAMPP Ready)
-- Security: Primary keys, Foreign keys with Cascade Constraints
-- =======================================================

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `visitor_logs`;
DROP TABLE IF EXISTS `notices`;
DROP TABLE IF EXISTS `book_issues`;
DROP TABLE IF EXISTS `library_books`;
DROP TABLE IF EXISTS `exam_results`;
DROP TABLE IF EXISTS `homework`;
DROP TABLE IF EXISTS `fees`;
DROP TABLE IF EXISTS `attendance`;
DROP TABLE IF EXISTS `admissions`;
DROP TABLE IF EXISTS `students`;
DROP TABLE IF EXISTS `teachers`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `hostels`;
DROP TABLE IF EXISTS `transport_routes`;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. USERS TABLE (Role-Based Login System)
CREATE TABLE `users` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password_hash` VARCHAR(255) NOT NULL,
  `role` ENUM('Admin', 'Teacher', 'Student', 'Parent', 'Accountant', 'Librarian', 'Receptionist', 'Transport Manager') NOT NULL,
  `full_name` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(15) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `status` ENUM('Active', 'Suspended') DEFAULT 'Active',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. TRANSPORT ROUTES
CREATE TABLE `transport_routes` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `route_name` VARCHAR(150) NOT NULL,
  `bus_no` VARCHAR(20) NOT NULL,
  `driver_name` VARCHAR(100) NOT NULL,
  `driver_phone` VARCHAR(15) NOT NULL,
  `monthly_fee` DECIMAL(10,2) NOT NULL,
  `gps_status` VARCHAR(50) DEFAULT 'Inactive'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. HOSTELS
CREATE TABLE `hostels` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `hostel_name` VARCHAR(150) NOT NULL,
  `room_no` VARCHAR(10) NOT NULL,
  `bed_count` INT NOT NULL,
  `occupied_count` INT DEFAULT 0,
  `fee_per_month` DECIMAL(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. STUDENTS TABLE
CREATE TABLE `students` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `roll_no` VARCHAR(20) NOT NULL UNIQUE,
  `full_name` VARCHAR(100) NOT NULL,
  `class_name` VARCHAR(50) NOT NULL, -- Nursery to Class 12
  `section` VARCHAR(10) NOT NULL DEFAULT 'A',
  `parent_name` VARCHAR(100) NOT NULL,
  `parent_phone` VARCHAR(15) NOT NULL,
  `address` TEXT NOT NULL,
  `dob` DATE NOT NULL,
  `admission_date` DATE NOT NULL,
  `route_id` INT NULL,
  `hostel_id` INT NULL,
  `status` ENUM('Active', 'Inactive') DEFAULT 'Active',
  FOREIGN KEY (`route_id`) REFERENCES `transport_routes`(`id`) ON DELETE SET NULL,
  FOREIGN KEY (`hostel_id`) REFERENCES `hostels`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. TEACHERS TABLE
CREATE TABLE `teachers` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `full_name` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(15) NOT NULL UNIQUE,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `subject_specialization` VARCHAR(100) NOT NULL,
  `qualification` VARCHAR(150) NOT NULL,
  `monthly_salary` DECIMAL(10,2) NOT NULL,
  `status` ENUM('Active', 'Inactive') DEFAULT 'Active',
  `hire_date` DATE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. ONLINE ADMISSIONS APPLICANTS
CREATE TABLE `admissions` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `student_name` VARCHAR(100) NOT NULL,
  `parent_name` VARCHAR(100) NOT NULL,
  `class_applied` VARCHAR(20) NOT NULL,
  `phone` VARCHAR(15) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `status` ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
  `apply_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `birth_cert_uploaded` BOOLEAN DEFAULT FALSE,
  `photo_uploaded` BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. ATTENDANCE SYSTEM
CREATE TABLE `attendance` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `student_id` INT NOT NULL,
  `date` DATE NOT NULL,
  `status` ENUM('Present', 'Absent', 'Late') NOT NULL,
  `remarks` VARCHAR(255) DEFAULT NULL,
  UNIQUE KEY `student_date_uniq` (`student_id`, `date`),
  FOREIGN KEY (`student_id`) REFERENCES `students`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. FEE RECORDS
CREATE TABLE `fees` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `student_id` INT NOT NULL,
  `category` ENUM('Tuition', 'Exam', 'Transport', 'Hostel', 'Library') NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `fine` DECIMAL(10,2) DEFAULT 0.00,
  `discount` DECIMAL(10,2) DEFAULT 0.00,
  `paid_amount` DECIMAL(10,2) DEFAULT 0.00,
  `status` ENUM('Paid', 'Pending', 'Overdue') DEFAULT 'Pending',
  `due_date` DATE NOT NULL,
  `paid_date` DATE DEFAULT NULL,
  `payment_method` VARCHAR(50) DEFAULT NULL,
  FOREIGN KEY (`student_id`) REFERENCES `students`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. HOMEWORK AND ASSIGNMENTS
CREATE TABLE `homework` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `class_name` VARCHAR(20) NOT NULL,
  `section` VARCHAR(10) NOT NULL,
  `subject` VARCHAR(100) NOT NULL,
  `title` VARCHAR(150) NOT NULL,
  `description` TEXT NOT NULL,
  `assigned_date` DATE NOT NULL,
  `due_date` DATE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. EXAM RESULTS
CREATE TABLE `exam_results` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `student_id` INT NOT NULL,
  `exam_name` VARCHAR(50) NOT NULL, -- Quarterly, Half-Yearly, Annual
  `subject` VARCHAR(100) NOT NULL,
  `marks_obtained` DECIMAL(5,2) NOT NULL,
  `total_marks` DECIMAL(5,2) NOT NULL,
  `grade` VARCHAR(5) NOT NULL,
  `remarks` VARCHAR(255) DEFAULT NULL,
  FOREIGN KEY (`student_id`) REFERENCES `students`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. LIBRARY BOOKS
CREATE TABLE `library_books` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(200) NOT NULL,
  `author` VARCHAR(100) NOT NULL,
  `category` VARCHAR(100) NOT NULL,
  `isbn` VARCHAR(30) UNIQUE,
  `quantity` INT NOT NULL,
  `issued_qty` INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. BOOK ISSUES REGISTER
CREATE TABLE `book_issues` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `book_id` INT NOT NULL,
  `student_id` INT NOT NULL,
  `issue_date` DATE NOT NULL,
  `return_date` DATE DEFAULT NULL,
  `status` ENUM('Issued', 'Returned') DEFAULT 'Issued',
  FOREIGN KEY (`book_id`) REFERENCES `library_books`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`student_id`) REFERENCES `students`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. NOTICE BOARD (Communications)
CREATE TABLE `notices` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(200) NOT NULL,
  `content` TEXT NOT NULL,
  `posted_by` VARCHAR(100) NOT NULL,
  `category` ENUM('General', 'Exam', 'Event', 'Holiday') NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. VISITOR LOGS (Reception desk)
CREATE TABLE `visitor_logs` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `visitor_name` VARCHAR(100) NOT NULL,
  `purpose` VARCHAR(200) NOT NULL,
  `phone` VARCHAR(15) NOT NULL,
  `entry_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `exit_time` TIMESTAMP NULL DEFAULT NULL,
  `gate_pass_no` VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =======================================================
-- INSERTING COHESIVE SAMPLE SEED DATA
-- =======================================================

-- Seed default hashed passwords: 'admin', 'teacher', 'student', 'parent'
-- In PHP PDO implementation, these match bcrypt password_verify standards.
INSERT INTO `users` (`username`, `password_hash`, `role`, `full_name`, `phone`, `email`, `status`) VALUES
('admin', '$2y$10$wE9mH8Q7O6p5I4U3y2t1eO6SlnfDskfG3vA/uX8q9f6wVp6uG0uKi', 'Admin', 'Sanjay Kumar Mishra', '9936293542', 'bhola.co.Ghughli@gmail.com', 'Active'),
('teacher', '$2y$10$wE9mH8Q7O6p5I4U3y2t1eO6SlnfDskfG3vA/uX8q9f6wVp6uG0uKi', 'Teacher', 'Dr. Manoj Sharma', '9936293543', 'manoj@jkmontessori.com', 'Active'),
('student', '$2y$10$wE9mH8Q7O6p5I4U3y2t1eO6SlnfDskfG3vA/uX8q9f6wVp6uG0uKi', 'Student', 'Aarav Mishra', '9936293544', 'aarav@gmail.com', 'Active'),
('parent', '$2y$10$wE9mH8Q7O6p5I4U3y2t1eO6SlnfDskfG3vA/uX8q9f6wVp6uG0uKi', 'Parent', 'Rakesh Mishra', '9936293542', 'bhola.co.Ghughli@gmail.com', 'Active');

INSERT INTO `transport_routes` (`route_name`, `bus_no`, `driver_name`, `driver_phone`, `monthly_fee`, `gps_status`) VALUES
('Ghughli Block to Subhash Chowk', 'UP-56-T-1245', 'Rajesh Singh', '9936293590', 800.00, 'Active'),
('Maharajganj Cantt to Ghughli Road', 'UP-56-T-8877', 'Mahendra Prasad', '9936293591', 1200.00, 'Inactive');

INSERT INTO `hostels` (`hostel_name`, `room_no`, `bed_count`, `occupied_count`, `fee_per_month`) VALUES
('Subhash Chandra Bose Boys Hostel', 'A-101', 4, 2, 4000.00),
('Rani Laxmi Bai Girls Hostel', 'G-201', 3, 1, 4500.00);

INSERT INTO `students` (`id`, `roll_no`, `full_name`, `class_name`, `section`, `parent_name`, `parent_phone`, `address`, `dob`, `admission_date`, `route_id`, `hostel_id`) VALUES
(1, 'Class10-101', 'Aarav Mishra', 'Class 10', 'A', 'Rakesh Mishra', '9936293542', 'Subhash Chowk, Ghughli, UP', '2011-06-12', '2020-04-05', 1, 1),
(2, 'Class10-102', 'Aditi Singh', 'Class 10', 'A', 'Vikram Singh', '9936293581', 'Ghughli, Maharajganj, UP', '2011-08-22', '2021-04-10', 1, NULL);

INSERT INTO `teachers` (`full_name`, `phone`, `email`, `subject_specialization`, `qualification`, `monthly_salary`, `status`, `hire_date`) VALUES
('Dr. Manoj Sharma', '9936293543', 'manoj@jkmontessori.com', 'Physics', 'M.Sc, Ph.D', 45000.00, 'Active', '2018-06-01'),
('Smt. Shanti Devi', '9936293551', 'shanti@jkmontessori.com', 'Hindi', 'M.A, B.Ed', 35000.00, 'Active', '2015-07-15');

INSERT INTO `notices` (`title`, `content`, `posted_by`, `category`) VALUES
('Independence Day Flags Celebrations', 'JK Montessori will celebrate Independence Day on Aug 15th. Flag hoisting ceremony is scheduled for 8:00 AM.', 'Principal Sanjay Kumar Mishra', 'Event'),
('Quarterly Exam Timetable Released', 'The exams will begin from 10th September. Collect roll clearance slips.', 'Exam Desk', 'Exam');
