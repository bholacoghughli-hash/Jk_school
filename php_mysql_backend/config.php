<?php
/**
 * DATABASE CONNECTION CONFIGURATION & CYBER SECURITY CORE
 * Institution: JK Montessori School, Ghughli, Maharajganj, UP
 * Security Protections: SQL Injection, XSS, CSRF, and Secure Sessions
 */

// Establish strict session parameters
if (session_status() == PHP_SESSION_NONE) {
    ini_set('session.cookie_httponly', 1);
    ini_set('session.use_only_cookies', 1);
    ini_set('session.cookie_secure', 0); // Set to 1 if deploying with HTTPS
    session_start();
}

// Database parameters
define('DB_HOST', 'localhost');
define('DB_USER', 'root');
define('DB_PASS', '');
define('DB_NAME', 'jk_school_database');

try {
    // Establishes secure UTF-8 PDO Connection (Protects against SQL Injection natively)
    $pdo = new PDO(
        "mysql:host=" . DB_HOST . ";dbname=" . DB_NAME . ";charset=utf8mb4",
        DB_USER,
        DB_PASS,
        [
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES   => false,
        ]
    );
} catch (PDOException $e) {
    // Secure failure log: does not expose structural credentials in browser
    error_log("Database Connection Failed: " . $e->getMessage());
    die("Application Security Gateway Blocked Connection. Check server configuration.");
}

/**
 * CSRF Protection Token Generator & Validator
 */
function getCSRFToken() {
    if (empty($_SESSION['csrf_token'])) {
        $_SESSION['csrf_token'] = bin2hex(random_bytes(32));
    }
    return $_SESSION['csrf_token'];
}

function validateCSRFToken($token) {
    return isset($_SESSION['csrf_token']) && hash_equals($_SESSION['csrf_token'], $token);
}

/**
 * XSS Protection Output Sanitizer
 */
function sanitizeInput($data) {
    return htmlspecialchars(trim($data), ENT_QUOTES, 'UTF-8');
}
?>
