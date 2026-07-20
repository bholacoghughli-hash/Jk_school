package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SchoolViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SchoolRepository
    val database: SchoolDatabase

    // --- Active Session ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // --- State Streams ---
    val allUsers: StateFlow<List<User>>
    val allStudents: StateFlow<List<Student>>
    val allTeachers: StateFlow<List<Teacher>>
    val allAttendance: StateFlow<List<Attendance>>
    val allFees: StateFlow<List<FeeRecord>>
    val allHomework: StateFlow<List<Homework>>
    val allExamResults: StateFlow<List<ExamResult>>
    val allBooks: StateFlow<List<LibraryBook>>
    val allBookIssues: StateFlow<List<BookIssue>>
    val allRoutes: StateFlow<List<TransportRoute>>
    val allHostels: StateFlow<List<Hostel>>
    val allAdmissions: StateFlow<List<AdmissionApplication>>
    val allNotices: StateFlow<List<Notice>>

    init {
        database = SchoolDatabase.getDatabase(application)
        repository = SchoolRepository(database.schoolDao())

        val stateFlowScope = SharingStarted.WhileSubscribed(5000)

        allUsers = repository.allUsers.stateIn(viewModelScope, stateFlowScope, emptyList())
        allStudents = repository.allStudents.stateIn(viewModelScope, stateFlowScope, emptyList())
        allTeachers = repository.allTeachers.stateIn(viewModelScope, stateFlowScope, emptyList())
        allAttendance = repository.allAttendance.stateIn(viewModelScope, stateFlowScope, emptyList())
        allFees = repository.allFees.stateIn(viewModelScope, stateFlowScope, emptyList())
        allHomework = repository.allHomework.stateIn(viewModelScope, stateFlowScope, emptyList())
        allExamResults = repository.allExamResults.stateIn(viewModelScope, stateFlowScope, emptyList())
        allBooks = repository.allBooks.stateIn(viewModelScope, stateFlowScope, emptyList())
        allBookIssues = repository.allBookIssues.stateIn(viewModelScope, stateFlowScope, emptyList())
        allRoutes = repository.allRoutes.stateIn(viewModelScope, stateFlowScope, emptyList())
        allHostels = repository.allHostels.stateIn(viewModelScope, stateFlowScope, emptyList())
        allAdmissions = repository.allAdmissions.stateIn(viewModelScope, stateFlowScope, emptyList())
        allNotices = repository.allNotices.stateIn(viewModelScope, stateFlowScope, emptyList())

        // Auto-Seed Database if empty
        viewModelScope.launch {
            allUsers.first { true } // Trigger collection
            val userList = database.schoolDao().getAllUsers().first()
            if (userList.isEmpty()) {
                seedInitialData()
            }
        }
    }

    // --- Authentication ---
    fun login(username: String, passwordText: String) {
        viewModelScope.launch {
            _loginError.value = null
            // For simple evaluation, we check if password text matches.
            // In production, we'd compare bcrypt/pbkdf2 hashes.
            val user = repository.getUserByUsername(username)
            if (user != null && user.passwordHash == passwordText) {
                _currentUser.value = user
            } else {
                _loginError.value = "Invalid Username or Password!"
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun clearLoginError() {
        _loginError.value = null
    }

    // --- Database Mutators (Forms) ---
    fun addNotice(title: String, content: String, category: String) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val author = _currentUser.value?.fullName ?: "System"
            repository.insertNotice(Notice(title = title, content = content, postedBy = author, date = dateStr, category = category))
        }
    }

    fun addHomework(className: String, section: String, subject: String, title: String, description: String, dueDate: String) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.insertHomework(Homework(className = className, section = section, subject = subject, title = title, description = description, assignedDate = dateStr, dueDate = dueDate))
        }
    }

    fun markAttendance(studentId: Int, status: String, remarks: String? = null) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.insertAttendance(Attendance(studentId = studentId, date = dateStr, status = status, remarks = remarks))
        }
    }

    fun payFee(feeId: Int, payMethod: String, amount: Double) {
        viewModelScope.launch {
            val record = allFees.value.find { it.id == feeId }
            if (record != null) {
                val newPaid = record.paidAmount + amount
                val newStatus = if (newPaid >= record.amount + record.fine - record.discount) "Paid" else "Pending"
                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                repository.insertFeeRecord(record.copy(
                    paidAmount = newPaid,
                    status = newStatus,
                    paidDate = dateStr,
                    paymentMethod = payMethod
                ))
            }
        }
    }

    fun addStudent(fullName: String, className: String, section: String, parentName: String, parentPhone: String, address: String) {
        viewModelScope.launch {
            val rollNo = "${className.replace(" ", "")}-${(100 + (allStudents.value.size + 1))}"
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val student = Student(
                rollNo = rollNo,
                fullName = fullName,
                className = className,
                section = section,
                parentName = parentName,
                parentPhone = parentPhone,
                address = address,
                dob = "2015-05-15",
                admissionDate = dateStr,
                status = "Active"
            )
            repository.insertStudent(student)
        }
    }

    fun submitOnlineAdmission(fullName: String, parentName: String, className: String, phone: String, email: String) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val app = AdmissionApplication(
                fullName = fullName,
                parentName = parentName,
                className = className,
                phone = phone,
                email = email,
                applyDate = dateStr,
                birthCertUploaded = true,
                photoUploaded = true,
                paymentReceiptUploaded = true
            )
            repository.insertAdmission(app)
        }
    }

    fun updateAdmissionStatus(appId: Int, status: String) {
        viewModelScope.launch {
            val app = allAdmissions.value.find { it.id == appId }
            if (app != null) {
                repository.insertAdmission(app.copy(status = status))
                if (status == "Approved") {
                    // Create actual student on approval
                    addStudent(
                        fullName = app.fullName,
                        className = app.className,
                        section = "A",
                        parentName = app.parentName,
                        parentPhone = app.phone,
                        address = "Ghughli, Maharajganj"
                    )
                }
            }
        }
    }

    fun addTeacher(fullName: String, phone: String, email: String, subject: String, qualification: String, salary: Double) {
        viewModelScope.launch {
            val teacher = Teacher(
                fullName = fullName,
                phone = phone,
                email = email,
                subject = subject,
                qualification = qualification,
                salary = salary,
                status = "Active"
            )
            repository.insertTeacher(teacher)
        }
    }

    fun issueBook(bookId: Int, studentId: Int, returnDateStr: String) {
        viewModelScope.launch {
            val book = allBooks.value.find { it.id == bookId }
            if (book != null && book.quantity > book.issuedQty) {
                val issueDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                repository.insertBook(book.copy(issuedQty = book.issuedQty + 1))
                repository.insertBookIssue(BookIssue(
                    bookId = bookId,
                    studentId = studentId,
                    issueDate = issueDateStr,
                    returnDate = returnDateStr,
                    status = "Issued"
                ))
            }
        }
    }

    fun returnBook(issueId: Int) {
        viewModelScope.launch {
            val issue = allBookIssues.value.find { it.id == issueId }
            if (issue != null && issue.status == "Issued") {
                val book = allBooks.value.find { it.id == issue.bookId }
                if (book != null) {
                    repository.insertBook(book.copy(issuedQty = (book.issuedQty - 1).coerceAtLeast(0)))
                }
                val returnDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                repository.insertBookIssue(issue.copy(status = "Returned", returnDate = returnDateStr))
            }
        }
    }

    fun enterExamMarks(studentId: Int, examName: String, subject: String, marks: Double, total: Double) {
        viewModelScope.launch {
            val pct = (marks / total) * 100
            val grade = when {
                pct >= 90 -> "A+"
                pct >= 80 -> "A"
                pct >= 70 -> "B"
                pct >= 60 -> "C"
                pct >= 50 -> "D"
                else -> "F"
            }
            repository.insertExamResult(ExamResult(
                studentId = studentId,
                examName = examName,
                subject = subject,
                marksObtained = marks,
                totalMarks = total,
                grade = grade,
                remarks = "Entered by Teacher"
            ))
        }
    }

    // --- Seeder ---
    private suspend fun seedInitialData() {
        // 1. Add Default Users for all 8 panels
        val defaultUsers = listOf(
            User(username = "admin", passwordHash = "admin", role = "Admin", fullName = "Sanjay Kumar Mishra", phone = "9936293542", email = "bhola.co.Ghughli@gmail.com"),
            User(username = "teacher", passwordHash = "teacher", role = "Teacher", fullName = "Dr. Manoj Sharma", phone = "9936293543", email = "manoj@jkmontessori.com"),
            User(username = "student", passwordHash = "student", role = "Student", fullName = "Aarav Mishra", phone = "9936293544", email = "aarav@gmail.com"),
            User(username = "parent", passwordHash = "parent", role = "Parent", fullName = "Rakesh Mishra", phone = "9936293542", email = "bhola.co.Ghughli@gmail.com"),
            User(username = "reception", passwordHash = "reception", role = "Receptionist", fullName = "Kirti Singh", phone = "9936293546", email = "kirti@jkmontessori.com"),
            User(username = "accountant", passwordHash = "accountant", role = "Accountant", fullName = "Suresh Prasad", phone = "9936293547", email = "suresh@jkmontessori.com"),
            User(username = "librarian", passwordHash = "librarian", role = "Librarian", fullName = "Anjali Verma", phone = "9936293548", email = "anjali@jkmontessori.com"),
            User(username = "transport", passwordHash = "transport", role = "Transport Manager", fullName = "Ramu Yadav", phone = "9936293549", email = "ramu@jkmontessori.com")
        )
        for (u in defaultUsers) repository.insertUser(u)

        // 2. Add Teachers
        val teachers = listOf(
            Teacher(fullName = "Dr. Manoj Sharma", phone = "9936293543", email = "manoj@jkmontessori.com", subject = "Physics", qualification = "M.Sc, Ph.D", salary = 45000.0, status = "Active"),
            Teacher(fullName = "Smt. Shanti Devi", phone = "9936293551", email = "shanti@jkmontessori.com", subject = "Hindi Literature", qualification = "M.A, B.Ed", salary = 35000.0, status = "Active"),
            Teacher(fullName = "Mr. Amit Patel", phone = "9936293552", email = "amit@jkmontessori.com", subject = "Mathematics", qualification = "M.Tech", salary = 42000.0, status = "Active"),
            Teacher(fullName = "Miss Sophia D'Souza", phone = "9936293553", email = "sophia@jkmontessori.com", subject = "English Communication", qualification = "M.A English", salary = 38000.0, status = "Active")
        )
        for (t in teachers) repository.insertTeacher(t)

        // 3. Add Students
        val students = listOf(
            Student(rollNo = "Class10-101", fullName = "Aarav Mishra", className = "Class 10", section = "A", parentName = "Rakesh Mishra", parentPhone = "9936293542", address = "Subhash Chowk, Ghughli, Maharajganj", dob = "2011-06-12", admissionDate = "2020-04-05", status = "Active"),
            Student(rollNo = "Class10-102", fullName = "Aditi Singh", className = "Class 10", section = "A", parentName = "Vikram Singh", parentPhone = "9936293581", address = "Ghughli, Maharajganj", dob = "2011-08-22", admissionDate = "2021-04-10", status = "Active"),
            Student(rollNo = "Class12-101", fullName = "Rohan Gupta", className = "Class 12", section = "A", parentName = "Satish Gupta", parentPhone = "9936293582", address = "Subhash Chowk, Ghughli", dob = "2009-02-14", admissionDate = "2019-07-01", status = "Active"),
            Student(rollNo = "Class12-102", fullName = "Neha Pandey", className = "Class 12", section = "B", parentName = "Anil Pandey", parentPhone = "9936293583", address = "Block Road, Ghughli", dob = "2009-11-30", admissionDate = "2022-04-15", status = "Active")
        )
        for (s in students) repository.insertStudent(s)

        // 4. Add Notice Board
        val notices = listOf(
            Notice(title = "Independence Day Celebrations", content = "JK Montessori School will celebrate Independence Day on August 15th at 8:00 AM. Attendance is mandatory for all Nursery to Class 12 students in school uniform.", postedBy = "Principal Mr. Sanjay Kumar Mishra", date = "2026-07-15", category = "Event"),
            Notice(title = "Half-Yearly Examination Timetable", content = "The Half-Yearly examination begins from September 10th. All subject schedules and syllabus outlines are available in the downloads folder.", postedBy = "Exam Coordinator", date = "2026-07-16", category = "Exam"),
            Notice(title = "Monsoon Holiday Notice", content = "School will remain closed on July 20th and July 21st due to heavy rain forecast. Online study material will be uploaded by respective subject teachers.", postedBy = "Principal's Office", date = "2026-07-17", category = "Holiday")
        )
        for (n in notices) repository.insertNotice(n)

        // 5. Add Transport Routes
        val routes = listOf(
            TransportRoute(routeName = "Ghughli Block to Subhash Chowk", busNo = "UP-56-T-1245", driverName = "Rajesh Singh", driverPhone = "9936293590", routeFee = 800.0),
            TransportRoute(routeName = "Maharajganj Cantt to Ghughli G.S. Road", busNo = "UP-56-T-8877", driverName = "Mahendra Prasad", driverPhone = "9936293591", routeFee = 1200.0),
            TransportRoute(routeName = "Siswa Bazar Outer to School", busNo = "UP-56-T-0045", driverName = "Shyam Yadav", driverPhone = "9936293592", routeFee = 1500.0)
        )
        for (r in routes) repository.insertRoute(r)

        // 6. Add Hostels
        val hostels = listOf(
            Hostel(hostelName = "Subhash Chandra Bose Boys Hostel", roomNo = "A-101", bedCount = 4, occupiedCount = 2, feePerMonth = 4000.0),
            Hostel(hostelName = "Subhash Chandra Bose Boys Hostel", roomNo = "A-102", bedCount = 4, occupiedCount = 3, feePerMonth = 4000.0),
            Hostel(hostelName = "Rani Laxmi Bai Girls Hostel", roomNo = "G-201", bedCount = 3, occupiedCount = 1, feePerMonth = 4500.0)
        )
        for (h in hostels) repository.insertHostel(h)

        // 7. Add Library Books
        val books = listOf(
            LibraryBook(title = "The Alchemist", author = "Paulo Coelho", category = "Literature & Fiction", isbn = "978-0062315007", quantity = 10, issuedQty = 2),
            LibraryBook(title = "Wings of Fire", author = "Dr. A.P.J. Abdul Kalam", category = "Autobiography", isbn = "978-8173711466", quantity = 8, issuedQty = 3),
            LibraryBook(title = "NCERT Physics Class 12", author = "NCERT Board", category = "Science Textbook", isbn = "978-8174505663", quantity = 30, issuedQty = 15),
            LibraryBook(title = "General Mathematics Volume 1", author = "Dr. R.S. Aggarwal", category = "Mathematics Study", isbn = "978-9352835421", quantity = 15, issuedQty = 6)
        )
        for (b in books) repository.insertBook(b)

        // 8. Add Book Issues
        val bookIssues = listOf(
            BookIssue(bookId = 1, studentId = 1, issueDate = "2026-07-01", returnDate = "2026-07-15", status = "Returned"),
            BookIssue(bookId = 2, studentId = 1, issueDate = "2026-07-10", returnDate = "2026-07-24", status = "Issued"),
            BookIssue(bookId = 3, studentId = 2, issueDate = "2026-07-05", returnDate = "2026-07-20", status = "Issued")
        )
        for (bi in bookIssues) repository.insertBookIssue(bi)

        // 9. Add Fee Records
        val fees = listOf(
            FeeRecord(studentId = 1, category = "Tuition", amount = 2500.0, fine = 0.0, discount = 200.0, paidAmount = 2300.0, status = "Paid", dueDate = "2026-07-10", paidDate = "2026-07-08", paymentMethod = "UPI"),
            FeeRecord(studentId = 1, category = "Transport", amount = 800.0, fine = 50.0, discount = 0.0, paidAmount = 0.0, status = "Pending", dueDate = "2026-07-15"),
            FeeRecord(studentId = 2, category = "Tuition", amount = 2500.0, fine = 100.0, discount = 0.0, paidAmount = 0.0, status = "Overdue", dueDate = "2026-07-10")
        )
        for (f in fees) repository.insertFeeRecord(f)

        // 10. Add Exam Results
        val results = listOf(
            ExamResult(studentId = 1, examName = "Quarterly", subject = "Physics", marksObtained = 88.0, totalMarks = 100.0, grade = "A", remarks = "Excellent academic focus!"),
            ExamResult(studentId = 1, examName = "Quarterly", subject = "Mathematics", marksObtained = 95.0, totalMarks = 100.0, grade = "A+", remarks = "Outperforming class topper."),
            ExamResult(studentId = 2, examName = "Quarterly", subject = "Physics", marksObtained = 72.0, totalMarks = 100.0, grade = "B", remarks = "Keep practicing problem solving.")
        )
        for (res in results) repository.insertExamResult(res)

        // 11. Add Homework Tasks
        val homeworks = listOf(
            Homework(className = "Class 10", section = "A", subject = "Physics", title = "Ohm's Law Experiments", description = "Draft a 2-page report demonstrating current-voltage measurements in a circuit.", assignedDate = "2026-07-15", dueDate = "2026-07-20"),
            Homework(className = "Class 10", section = "A", subject = "Mathematics", title = "Quadratic Equations Proofs", description = "Solve problems 1-15 in Chapter 4 on quadratic equation standard formulas.", assignedDate = "2026-07-16", dueDate = "2026-07-22")
        )
        for (hw in homeworks) repository.insertHomework(hw)

        // 12. Add Attendance Records
        val attendance = listOf(
            Attendance(studentId = 1, date = "2026-07-15", status = "Present"),
            Attendance(studentId = 1, date = "2026-07-16", status = "Present"),
            Attendance(studentId = 1, date = "2026-07-17", status = "Present"),
            Attendance(studentId = 2, date = "2026-07-15", status = "Present"),
            Attendance(studentId = 2, date = "2026-07-16", status = "Absent", remarks = "Fever"),
            Attendance(studentId = 2, date = "2026-07-17", status = "Late", remarks = "Bus delay")
        )
        for (att in attendance) repository.insertAttendance(att)

        // 13. Add Admission Applications
        val admissions = listOf(
            AdmissionApplication(fullName = "Kunal Sharma", parentName = "Devendra Sharma", className = "Class 1", phone = "9936293551", email = "kunal@gmail.com", status = "Pending", applyDate = "2026-07-15", birthCertUploaded = true, photoUploaded = true, paymentReceiptUploaded = true),
            AdmissionApplication(fullName = "Meera Roy", parentName = "Amit Roy", className = "Nursery", phone = "9936293552", email = "meera@gmail.com", status = "Approved", applyDate = "2026-07-16", birthCertUploaded = true, photoUploaded = true, paymentReceiptUploaded = true)
        )
        for (ad in admissions) repository.insertAdmission(ad)
    }
}
