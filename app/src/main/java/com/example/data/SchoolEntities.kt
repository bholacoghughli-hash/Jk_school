package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val role: String, // "Admin", "Teacher", "Student", "Parent", "Accountant", "Librarian", "Receptionist", "Transport"
    val fullName: String,
    val phone: String,
    val email: String,
    val profilePhoto: String? = null
)

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rollNo: String,
    val fullName: String,
    val className: String, // "Nursery", "Class 1", ..., "Class 12"
    val section: String, // "A", "B"
    val parentName: String,
    val parentPhone: String,
    val address: String,
    val dob: String,
    val admissionDate: String,
    val status: String // "Active", "Inactive"
)

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val phone: String,
    val email: String,
    val subject: String,
    val qualification: String,
    val salary: Double,
    val status: String // "Active", "Inactive"
)

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val date: String, // YYYY-MM-DD
    val status: String, // "Present", "Absent", "Late"
    val remarks: String? = null
)

@Entity(tableName = "fees")
data class FeeRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val category: String, // "Tuition", "Exam", "Transport", "Hostel", "Library"
    val amount: Double,
    val fine: Double = 0.0,
    val discount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val status: String, // "Paid", "Pending", "Overdue"
    val dueDate: String,
    val paidDate: String? = null,
    val paymentMethod: String? = null // "UPI", "Razorpay", "Card", "Cash"
)

@Entity(tableName = "homework")
data class Homework(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val className: String,
    val section: String,
    val subject: String,
    val title: String,
    val description: String,
    val assignedDate: String,
    val dueDate: String
)

@Entity(tableName = "exam_results")
data class ExamResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val examName: String, // "Quarterly", "Half Yearly", "Annual"
    val subject: String,
    val marksObtained: Double,
    val totalMarks: Double,
    val grade: String,
    val remarks: String? = null
)

@Entity(tableName = "library_books")
data class LibraryBook(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val author: String,
    val category: String,
    val isbn: String,
    val quantity: Int,
    val issuedQty: Int = 0
)

@Entity(tableName = "book_issues")
data class BookIssue(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: Int,
    val studentId: Int,
    val issueDate: String,
    val returnDate: String? = null,
    val status: String // "Issued", "Returned"
)

@Entity(tableName = "transport_routes")
data class TransportRoute(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routeName: String,
    val busNo: String,
    val driverName: String,
    val driverPhone: String,
    val routeFee: Double
)

@Entity(tableName = "hostels")
data class Hostel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hostelName: String,
    val roomNo: String,
    val bedCount: Int,
    val occupiedCount: Int = 0,
    val feePerMonth: Double
)

@Entity(tableName = "admissions")
data class AdmissionApplication(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val parentName: String,
    val className: String,
    val phone: String,
    val email: String,
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val applyDate: String,
    val birthCertUploaded: Boolean = false,
    val photoUploaded: Boolean = false,
    val paymentReceiptUploaded: Boolean = false
)

@Entity(tableName = "notices")
data class Notice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val postedBy: String,
    val date: String,
    val category: String // "General", "Exam", "Event", "Holiday"
)
