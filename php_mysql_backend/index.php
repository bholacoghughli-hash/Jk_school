<?php
require_once 'config.php';

// Handle Demo / Sample Form Submissions
$message = "";
$messageType = "success";

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (isset($_POST['action']) && $_POST['action'] === 'admission') {
        // CSRF Token Validation
        if (!validateCSRFToken($_POST['csrf_token'])) {
            $message = "Security Validation Failed. CSRF Token Mismatch.";
            $messageType = "danger";
        } else {
            // Sanitizing Inputs (XSS Protection)
            $name = sanitizeInput($_POST['student_name']);
            $parent = sanitizeInput($_POST['parent_name']);
            $class = sanitizeInput($_POST['class_applied']);
            $phone = sanitizeInput($_POST['phone']);
            $email = sanitizeInput($_POST['email']);

            if (!empty($name) && !empty($phone)) {
                // Secure Prepared Statement (SQL Injection Protection)
                $stmt = $pdo->prepare("INSERT INTO admissions (student_name, parent_name, class_applied, phone, email, status) VALUES (?, ?, ?, ?, ?, 'Pending')");
                try {
                    $stmt->execute([$name, $parent, $class, $phone, $email]);
                    $message = "Success! Admission registered securely for $name. Temporary Ref No: " . rand(10000, 99999);
                    $messageType = "success";
                } catch (Exception $e) {
                    $message = "Database Record failed. Note: Mock submission success fallback triggered.";
                    $messageType = "warning";
                }
            } else {
                $message = "Please fill in all mandatory details.";
                $messageType = "danger";
            }
        }
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JK Montessori School - Enterprise Portal</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome Icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --bg-color: #f8fafc;
            --surface-color: #ffffff;
            --primary-color: #1e3a8a;
            --secondary-color: #3b82f6;
            --text-color: #0f172a;
            --text-muted: #64748b;
            --card-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
        }
        [data-theme="dark"] {
            --bg-color: #0f172a;
            --surface-color: #1e293b;
            --primary-color: #60a5fa;
            --secondary-color: #93c5fd;
            --text-color: #f1f5f9;
            --text-muted: #94a3b8;
            --card-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.3);
        }
        body {
            background-color: var(--bg-color);
            color: var(--text-color);
            font-family: 'Segoe UI', system-ui, sans-serif;
            transition: background-color 0.3s ease, color 0.3s ease;
        }
        .navbar {
            background-color: var(--surface-color) !important;
            box-shadow: 0 1px 3px rgb(0 0 0 / 0.1);
        }
        .hero-banner {
            background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
            color: #ffffff;
            border-radius: 16px;
            padding: 48px 24px;
            text-align: center;
            position: relative;
            overflow: hidden;
            box-shadow: var(--card-shadow);
        }
        .custom-card {
            background-color: var(--surface-color);
            border: none;
            border-radius: 12px;
            box-shadow: var(--card-shadow);
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .custom-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 12px 20px -5px rgb(0 0 0 / 0.15);
        }
        .nav-tabs .nav-link {
            color: var(--text-muted);
            font-weight: 600;
            border: none;
        }
        .nav-tabs .nav-link.active {
            color: var(--primary-color);
            background-color: transparent;
            border-bottom: 3.5px solid var(--primary-color);
        }
        .theme-toggle-btn {
            background: none;
            border: none;
            color: var(--text-color);
            font-size: 1.25rem;
            cursor: pointer;
        }
    </style>
</head>
<body>

<!-- Header Navigation -->
<nav class="navbar navbar-expand-lg navbar-light py-3">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center" href="#">
            <i class="fa-solid fa-school text-warning me-2 fs-3"></i>
            <div>
                <strong style="color: var(--primary-color)">JK Montessori School</strong>
                <div style="font-size: 10px;" class="text-muted">Ghughli, Maharajganj, UP</div>
            </div>
        </a>
        <div class="d-flex align-items-center gap-3">
            <button onclick="toggleTheme()" class="theme-toggle-btn" id="theme-toggle" title="Toggle Light/Dark Theme">
                <i class="fa-solid fa-moon"></i>
            </button>
            <span class="badge bg-primary px-3 py-2">Admission 2026 Active</span>
        </div>
    </div>
</nav>

<div class="container my-4">
    <?php if (!empty($message)): ?>
        <div class="alert alert-<?= $messageType; ?> alert-dismissible fade show" role="alert">
            <?= $message; ?>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <?php endif; ?>

    <!-- Interactive Navigation Tabs -->
    <ul class="nav nav-tabs mb-4 justify-content-center" id="portalTabs" role="tablist">
        <li class="nav-item">
            <button class="nav-link active" id="home-tab" data-bs-toggle="tab" data-bs-target="#home" type="button" role="tab">Home</button>
        </li>
        <li class="nav-item">
            <button class="nav-link" id="facilities-tab" data-bs-toggle="tab" data-bs-target="#facilities" type="button" role="tab">Facilities</button>
        </li>
        <li class="nav-item">
            <button class="nav-link" id="notices-tab" data-bs-toggle="tab" data-bs-target="#notices" type="button" role="tab">Notice Board</button>
        </li>
        <li class="nav-item">
            <button class="nav-link" id="admission-tab" data-bs-toggle="tab" data-bs-target="#admission" type="button" role="tab">Apply Admission</button>
        </li>
        <li class="nav-item">
            <button class="nav-link" id="contact-tab" data-bs-toggle="tab" data-bs-target="#contact" type="button" role="tab">Contact Desk</button>
        </li>
    </ul>

    <div class="tab-content" id="portalTabsContent">
        
        <!-- HOME TAB -->
        <div class="tab-pane fade show active" id="home" role="tabpanel">
            <div class="hero-banner mb-4">
                <h2>Welcome to JK Montessori School</h2>
                <p class="lead">Nurturing Minds, Building Futures • Nursery to Class 12</p>
                <div class="d-flex justify-content-center gap-3 mt-3 flex-wrap">
                    <span class="badge bg-warning text-dark p-2"><i class="fa-solid fa-phone me-1"></i> Helpline: 9936293542</span>
                    <span class="badge bg-light text-dark p-2"><i class="fa-solid fa-envelope me-1"></i> bhola.co.Ghughli@gmail.com</span>
                </div>
            </div>

            <div class="row g-4">
                <div class="col-md-8">
                    <div class="card custom-card p-4 h-100">
                        <h4>About Our School</h4>
                        <p class="text-muted mt-2">JK Montessori School, located in Subhash Chowk, Block Ghughli, District Maharajganj, Uttar Pradesh, is committed to creating empathetic leaders, independent thinkers, and well-behaved, high-performing students. Under the expert leadership of Principal Mr. Sanjay Kumar Mishra, we offer co-educational support from Nursery to standard 12th.</p>
                        <hr>
                        <h5 class="text-primary">Principal's Address</h5>
                        <p class="fst-italic text-muted">"At JK Montessori, our mission goes far beyond textbooks. We integrate moral discipline, modern sciences, athletic teamwork, and robust life skills so our graduates shine nationally. We welcome you to experience our safe boarding, GPS buses, and smart laboratories." <br><strong>- Mr. Sanjay Kumar Mishra (Principal)</strong></p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card custom-card p-4 text-center h-100">
                        <i class="fa-solid fa-user-tie text-primary fs-1 mb-3"></i>
                        <h4>Principal Admin Office</h4>
                        <p class="text-muted">Mr. Sanjay Kumar Mishra</p>
                        <p class="small text-muted mb-4"><i class="fa-solid fa-location-arrow me-1"></i> Subhash Chowk, Ghughli, UP</p>
                        <button class="btn btn-primary w-100" onclick="document.getElementById('admission-tab').click()">Apply for Admission 2026</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- FACILITIES TAB -->
        <div class="tab-pane fade" id="facilities" role="tabpanel">
            <h4 class="mb-3">Our Core Infrastructure & Facilities</h4>
            <div class="row g-4">
                <div class="col-md-4">
                    <div class="card custom-card p-3 h-100">
                        <i class="fa-solid fa-computer fs-2 text-primary mb-2"></i>
                        <h5>Modern IT Lab</h5>
                        <p class="text-muted small">Equipped with 30+ terminal nodes, broadband internet, and safe child tracking systems.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card custom-card p-3 h-100">
                        <i class="fa-solid fa-bus fs-2 text-warning mb-2"></i>
                        <h5>GPS Bus Tracking</h5>
                        <p class="text-muted small">School bus fleet covering major routes in Maharajganj, Siswa Bazar, and Ghughli Block.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card custom-card p-3 h-100">
                        <i class="fa-solid fa-bed fs-2 text-success mb-2"></i>
                        <h5>Safe Boarding Hostel</h5>
                        <p class="text-muted small">Separate boys and girls spacious hostels with hygienic food, indoor recreation, and security guards.</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- NOTICES TAB -->
        <div class="tab-pane fade" id="notices" role="tabpanel">
            <h4 class="mb-3">Notice Board & Broadcasts</h4>
            <div class="card custom-card p-4">
                <div class="list-group list-group-flush">
                    <div class="list-group-item bg-transparent py-3">
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="badge bg-danger">Exam Notice</span>
                            <small class="text-muted">2026-07-16</small>
                        </div>
                        <h5 class="mt-2">Half-Yearly Examination Timetable</h5>
                        <p class="text-muted small mb-1">Timetable and syllabus checklist of half-yearly exams are ready for class 10 and 12.</p>
                        <small class="text-primary">- By Principal Mr. Sanjay Kumar Mishra</small>
                    </div>
                    <div class="list-group-item bg-transparent py-3">
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="badge bg-primary">Event Notice</span>
                            <small class="text-muted">2026-07-15</small>
                        </div>
                        <h5 class="mt-2">Independence Day Hoisting Program</h5>
                        <p class="text-muted small mb-1">Attend in official uniform on August 15th at 8:00 AM at subhash chowk campus premises.</p>
                        <small class="text-primary">- By Admin Desk</small>
                    </div>
                </div>
            </div>
        </div>

        <!-- ADMISSION TAB -->
        <div class="tab-pane fade" id="admission" role="tabpanel">
            <div class="row">
                <div class="col-md-8 mx-auto">
                    <div class="card custom-card p-4">
                        <h4 class="mb-2">Online Admission Registration 2026-27</h4>
                        <p class="text-muted small">Enter student details and upload supporting documents. Our desk will call you.</p>
                        
                        <form method="POST">
                            <input type="hidden" name="action" value="admission">
                            <!-- Anti-CSRF Token Integration -->
                            <input type="hidden" name="csrf_token" value="<?= getCSRFToken(); ?>">

                            <div class="mb-3">
                                <label class="form-label font-weight-bold">Student Full Name *</label>
                                <input type="text" name="student_name" class="form-control" placeholder="Applicant Name" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Parent / Guardian Name *</label>
                                <input type="text" name="parent_name" class="form-control" placeholder="Father's or Mother's Name" required>
                            </div>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Apply for Class *</label>
                                    <select name="class_applied" class="form-select">
                                        <option value="Nursery">Nursery</option>
                                        <option value="Class 1">Class 1</option>
                                        <option value="Class 5">Class 5</option>
                                        <option value="Class 10" selected>Class 10</option>
                                        <option value="Class 12">Class 12</option>
                                    </select>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Parent Phone Number *</label>
                                    <input type="tel" name="phone" class="form-control" placeholder="9936293542" required>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Email Address</label>
                                <input type="email" name="email" class="form-control" placeholder="bhola.co.Ghughli@gmail.com">
                            </div>

                            <button type="submit" class="btn btn-primary w-100 mt-2">Submit Secure Admission Application</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- CONTACT TAB -->
        <div class="tab-pane fade" id="contact" role="tabpanel">
            <h4 class="mb-3">Reach Our Admin Desk</h4>
            <div class="row g-4">
                <div class="col-md-6">
                    <div class="card custom-card p-4 h-100">
                        <h5>School Campus Location</h5>
                        <hr>
                        <p><i class="fa-solid fa-location-dot text-primary me-2"></i> <strong>JK Montessori School</strong><br>Subhash Chowk, Block Ghughli, District Maharajganj, Uttar Pradesh, India - 273151</p>
                        <p><i class="fa-solid fa-phone text-success me-2"></i> <strong>Helpline:</strong> 9936293542 / 9936293543</p>
                        <p><i class="fa-solid fa-envelope text-warning me-2"></i> <strong>Email:</strong> bhola.co.Ghughli@gmail.com</p>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card custom-card p-4 h-100">
                        <h5>Frequently Asked Questions</h5>
                        <div class="accordion accordion-flush" id="faqAccordion">
                            <div class="accordion-item bg-transparent">
                                <h2 class="accordion-header">
                                    <button class="accordion-button collapsed bg-transparent text-body" type="button" data-bs-toggle="collapse" data-bs-target="#faq1">What are school timings?</button>
                                </h2>
                                <div id="faq1" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body text-muted small">Monday to Saturday: 8:00 AM to 2:00 PM for higher standards. Pre-Primary timings are 8:30 AM to 12:30 PM.</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>

<footer class="text-center py-4 mt-5 border-top bg-light">
    <div class="container text-muted small">
        &copy; 2026 JK Montessori School, Ghughli. All rights reserved. <br>
        Crafted Securely with PHP 8, MySQL Relational Constraints & Bootstrap 5.
    </div>
</footer>

<!-- Bootstrap 5 Bundle JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function toggleTheme() {
        const html = document.documentElement;
        const currentTheme = html.getAttribute('data-theme');
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        html.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);

        const btn = document.getElementById('theme-toggle');
        btn.innerHTML = newTheme === 'dark' ? '<i class="fa-solid fa-sun"></i>' : '<i class="fa-solid fa-moon"></i>';
    }

    // Load persisted theme choice on startup
    const savedTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);
    const btn = document.getElementById('theme-toggle');
    if (savedTheme === 'dark' && btn) {
        btn.innerHTML = '<i class="fa-solid fa-sun"></i>';
    }
</script>
</body>
</html>
