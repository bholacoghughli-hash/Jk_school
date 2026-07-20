# JK Montessori School Enterprise Management System

Welcome to the **JK Montessori School Management System**. This is an enterprise-grade, highly secure, fully integrated dual-platform solution:
1. **A Native Android Client Application** (built using Jetpack Compose, MVVM Architecture, and a reactive Room local database).
2. **An Apache-Ready PHP 8 & MySQL Web Backend** (fully protected against SQL Injection, CSRF, and XSS vulnerabilities).

---

## 🏫 Institution Details
*   **School Name:** JK Montessori School
*   **Principal:** Mr. Sanjay Kumar Mishra
*   **Campus Address:** Subhash Chowk, Block Ghughli, District Maharajganj, Uttar Pradesh, India - 273151
*   **Office Phone:** 9936293542
*   **Admin Email:** bhola.co.Ghughli@gmail.com
*   **Classes Covered:** Nursery to Class 12

---

## 🛠️ Technology Stack & Architecture

### 1. Android Application (Compiled & Ready)
*   **UI Framework:** Jetpack Compose (Material Design 3 themed)
*   **State Management:** Kotlin Coroutines, StateFlow streams, and Architecture ViewModel
*   **Local Persistence:** Room Database with KSP annotation processor
*   **Visual Enhancements:** Programmatic Canvas drawing statistics charts, customizable dark/light theme toggle, digital QR Code / Barcode generator.

### 2. Web & Database Backend (`/php_mysql_backend/` folder)
*   **Server Engine:** PHP 8 / Apache (XAMPP Ready)
*   **Database Engine:** MySQL / MariaDB (Relational schemas with foreign keys)
*   **Frontend Design:** Bootstrap 5, FontAwesome vector symbols, and pure JavaScript theme state logic.

---

## 🔐 Cyber Security Safeguards Implemented

*   **SQL Injection (SQLi) Prevention:** All database queries utilize Native PDO Prepared Statements with parameterized execution. Direct SQL query interpolations are strictly forbidden.
*   **Cross-Site Scripting (XSS) Protection:** Output variables are dynamically escaped via `htmlspecialchars()` using UTF-8 encoding.
*   **Cross-Site Request Forgery (CSRF) Mitigation:** Forms include randomized cryptographically secure tokens generated with `random_bytes()` and checked on POST requests.
*   **Session Security:** Session cookies are configured with `httponly` and session identifiers are automatically regenerated on authentications.

---

## 📁 Project Folder Structure
```text
├── app/
│   └── src/main/java/com/example/
│       ├── MainActivity.kt        # Entry point initializing Room & ViewModels
│       ├── data/
│       │   ├── SchoolEntities.kt  # Room SQLite entities representing school data
│       │   ├── SchoolDao.kt       # Type-safe SQL query definitions
│       │   ├── SchoolDatabase.kt  # Room database instantiation controller
│       │   └── SchoolRepository.kt# Repository pattern abstraction layer
│       ├── ui/
│       │   ├── PortalScreens.kt   # Unified Public web portal & 8 user dashboards
│       │   └── theme/
│       │       ├── Color.kt       # Premium Navy/Gold theme colors
│       │       └── Theme.kt       # Dynamic Light & Dark scheme handler
│       └── AndroidManifest.xml    # System configuration & entry activities
├── php_mysql_backend/
│   ├── database.sql               # Relational MySQL Schema with foreign keys & sample seed data
│   ├── config.php                 # Safe database connection, CSRF & XSS helpers
│   └── index.php                  # Bootstrap 5 landing page & responsive registration web portal
└── README.md                      # This deployment guide
```

---

## 🔌 Web Server (XAMPP) Installation & Deployment Guide

Follow these steps to deploy the web administration system on your local machine:

1.  **Download and Install XAMPP:**
    Ensure you have downloaded [XAMPP for PHP 8](https://www.apachefriends.org/) and installed it on your system.

2.  **Move the Project Files:**
    Copy the entire `php_mysql_backend` folder from this project directory and paste it into your local XAMPP's root directory:
    *   **Windows:** `C:\xampp\htdocs\jk_school\`
    *   **macOS:** `/Applications/XAMPP/htdocs/jk_school/`

3.  **Start Apache and MySQL Services:**
    Launch the **XAMPP Control Panel** and click **Start** next to both **Apache** and **MySQL** modules.

4.  **Create and Import the Database:**
    *   Open your web browser and navigate to `http://localhost/phpmyadmin/`.
    *   Click on **New** in the left sidebar, enter `jk_school_database` as the database name, and click **Create**.
    *   Select the newly created database `jk_school_database`.
    *   Go to the **Import** tab at the top.
    *   Click **Choose File**, select the `database.sql` file from your `jk_school` directory, and scroll down to click **Import**.

5.  **Access the Application Portal:**
    Open your browser and navigate to: `http://localhost/jk_school/index.php`. The premium portal is live and ready to accept admissions!

---

## 🔑 Login Portal Credentials (Demo Quick-Fill Enabled)

For seamless evaluation, the login forms on both the Android app and web portal feature **1-Click Quick Fill Buttons**. Alternatively, you can use the credentials listed below:

| Portal Role | Username | Password | Full Name (Assigned Mock Identity) | Core Tab Highlights |
| :--- | :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin` | Mr. Sanjay Kumar Mishra (Principal) | Admission Screeners, Notice Broadcasting, Add Students, ID Cards |
| **Teacher** | `teacher` | `teacher` | Dr. Manoj Sharma (Physics) | Student Marks Entry, Daily Attendance Roll, Homework Assigning |
| **Student** | `student` | `student` | Aarav Mishra (Class 10-A) | Online Fee UPI Payments, Report Cards, Bus Tracking, Digital ID Card |
| **Parent** | `parent` | `parent` | Rakesh Mishra (Guardian) | Child Attendance Track, Direct Teacher Chat, Fees Ledger |
| **Accountant**| `accountant` | `accountant`| Suresh Prasad | Cashflow Bar Charts, Collection Summaries, Expense Logs |
| **Librarian** | `librarian` | `librarian`| Anjali Verma | Issued/Returned Register, Catalog Inventory Tracking |
| **Reception** | `reception` | `reception`| Kirti Singh | Security Gate Pass Generator, Visitor Registers |
| **Transport** | `transport` | `transport`| Ramu Yadav | Bus Tracking Management, Route Coordinates Logs |
