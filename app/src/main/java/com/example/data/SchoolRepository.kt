package com.example.data

import kotlinx.coroutines.flow.Flow

class SchoolRepository(private val schoolDao: SchoolDao) {

    val allUsers: Flow<List<User>> = schoolDao.getAllUsers()
    val allStudents: Flow<List<Student>> = schoolDao.getAllStudents()
    val allTeachers: Flow<List<Teacher>> = schoolDao.getAllTeachers()
    val allAttendance: Flow<List<Attendance>> = schoolDao.getAllAttendance()
    val allFees: Flow<List<FeeRecord>> = schoolDao.getAllFees()
    val allHomework: Flow<List<Homework>> = schoolDao.getAllHomework()
    val allExamResults: Flow<List<ExamResult>> = schoolDao.getAllExamResults()
    val allBooks: Flow<List<LibraryBook>> = schoolDao.getAllBooks()
    val allBookIssues: Flow<List<BookIssue>> = schoolDao.getAllBookIssues()
    val allRoutes: Flow<List<TransportRoute>> = schoolDao.getAllRoutes()
    val allHostels: Flow<List<Hostel>> = schoolDao.getAllHostels()
    val allAdmissions: Flow<List<AdmissionApplication>> = schoolDao.getAllAdmissions()
    val allNotices: Flow<List<Notice>> = schoolDao.getAllNotices()

    suspend fun getUserByUsername(username: String): User? = schoolDao.getUserByUsername(username)
    suspend fun insertUser(user: User) = schoolDao.insertUser(user)
    suspend fun deleteUser(user: User) = schoolDao.deleteUser(user)

    suspend fun getStudentById(id: Int): Student? = schoolDao.getStudentById(id)
    fun getStudentsByParentPhone(phone: String): Flow<List<Student>> = schoolDao.getStudentsByParentPhone(phone)
    suspend fun insertStudent(student: Student) = schoolDao.insertStudent(student)
    suspend fun deleteStudent(student: Student) = schoolDao.deleteStudent(student)

    suspend fun getTeacherById(id: Int): Teacher? = schoolDao.getTeacherById(id)
    suspend fun insertTeacher(teacher: Teacher) = schoolDao.insertTeacher(teacher)
    suspend fun deleteTeacher(teacher: Teacher) = schoolDao.deleteTeacher(teacher)

    fun getAttendanceForStudent(studentId: Int): Flow<List<Attendance>> = schoolDao.getAttendanceForStudent(studentId)
    suspend fun insertAttendance(attendance: Attendance) = schoolDao.insertAttendance(attendance)

    fun getFeesForStudent(studentId: Int): Flow<List<FeeRecord>> = schoolDao.getFeesForStudent(studentId)
    suspend fun insertFeeRecord(fee: FeeRecord) = schoolDao.insertFeeRecord(fee)

    fun getHomeworkForClass(className: String, section: String): Flow<List<Homework>> = schoolDao.getHomeworkForClass(className, section)
    suspend fun insertHomework(homework: Homework) = schoolDao.insertHomework(homework)

    fun getExamResultsForStudent(studentId: Int): Flow<List<ExamResult>> = schoolDao.getExamResultsForStudent(studentId)
    suspend fun insertExamResult(result: ExamResult) = schoolDao.insertExamResult(result)

    suspend fun insertBook(book: LibraryBook) = schoolDao.insertBook(book)
    fun getBookIssuesForStudent(studentId: Int): Flow<List<BookIssue>> = schoolDao.getBookIssuesForStudent(studentId)
    suspend fun insertBookIssue(issue: BookIssue) = schoolDao.insertBookIssue(issue)

    suspend fun insertRoute(route: TransportRoute) = schoolDao.insertRoute(route)
    suspend fun insertHostel(hostel: Hostel) = schoolDao.insertHostel(hostel)
    suspend fun insertAdmission(admission: AdmissionApplication) = schoolDao.insertAdmission(admission)
    suspend fun insertNotice(notice: Notice) = schoolDao.insertNotice(notice)
}
