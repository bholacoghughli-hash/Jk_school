package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {

    // --- Users ---
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    // --- Students ---
    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: Int): Student?

    @Query("SELECT * FROM students WHERE parentPhone = :phone")
    fun getStudentsByParentPhone(phone: String): Flow<List<Student>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    // --- Teachers ---
    @Query("SELECT * FROM teachers")
    fun getAllTeachers(): Flow<List<Teacher>>

    @Query("SELECT * FROM teachers WHERE id = :id LIMIT 1")
    suspend fun getTeacherById(id: Int): Teacher?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: Teacher)

    @Delete
    suspend fun deleteTeacher(teacher: Teacher)

    // --- Attendance ---
    @Query("SELECT * FROM attendance")
    fun getAllAttendance(): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId")
    fun getAttendanceForStudent(studentId: Int): Flow<List<Attendance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)

    // --- Fee Records ---
    @Query("SELECT * FROM fees")
    fun getAllFees(): Flow<List<FeeRecord>>

    @Query("SELECT * FROM fees WHERE studentId = :studentId")
    fun getFeesForStudent(studentId: Int): Flow<List<FeeRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeRecord(fee: FeeRecord)

    // --- Homework ---
    @Query("SELECT * FROM homework ORDER BY id DESC")
    fun getAllHomework(): Flow<List<Homework>>

    @Query("SELECT * FROM homework WHERE className = :className AND section = :section ORDER BY id DESC")
    fun getHomeworkForClass(className: String, section: String): Flow<List<Homework>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomework(homework: Homework)

    // --- Exam Results ---
    @Query("SELECT * FROM exam_results")
    fun getAllExamResults(): Flow<List<ExamResult>>

    @Query("SELECT * FROM exam_results WHERE studentId = :studentId")
    fun getExamResultsForStudent(studentId: Int): Flow<List<ExamResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamResult(result: ExamResult)

    // --- Library ---
    @Query("SELECT * FROM library_books")
    fun getAllBooks(): Flow<List<LibraryBook>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: LibraryBook)

    @Query("SELECT * FROM book_issues")
    fun getAllBookIssues(): Flow<List<BookIssue>>

    @Query("SELECT * FROM book_issues WHERE studentId = :studentId")
    fun getBookIssuesForStudent(studentId: Int): Flow<List<BookIssue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookIssue(issue: BookIssue)

    // --- Transport ---
    @Query("SELECT * FROM transport_routes")
    fun getAllRoutes(): Flow<List<TransportRoute>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: TransportRoute)

    // --- Hostels ---
    @Query("SELECT * FROM hostels")
    fun getAllHostels(): Flow<List<Hostel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHostel(hostel: Hostel)

    // --- Admissions ---
    @Query("SELECT * FROM admissions ORDER BY id DESC")
    fun getAllAdmissions(): Flow<List<AdmissionApplication>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmission(admission: AdmissionApplication)

    // --- Notices ---
    @Query("SELECT * FROM notices ORDER BY id DESC")
    fun getAllNotices(): Flow<List<Notice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: Notice)
}
