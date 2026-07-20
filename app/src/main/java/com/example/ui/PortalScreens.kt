package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SchoolViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolPortalApp(viewModel: SchoolViewModel) {
    var isDarkTheme by remember { mutableStateOf(false) }
    
    MyApplicationTheme(darkTheme = isDarkTheme) {
        val currentUser by viewModel.currentUser.collectAsState()
        
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = currentUser,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "MainAppState"
            ) { user ->
                if (user == null) {
                    PublicPortal(
                        viewModel = viewModel,
                        isDark = isDarkTheme,
                        onToggleTheme = { isDarkTheme = !isDarkTheme }
                    )
                } else {
                    MainDashboardContainer(
                        viewModel = viewModel,
                        user = user,
                        isDark = isDarkTheme,
                        onToggleTheme = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }
}

// ==========================================
// PUBLIC SCHOOL WEBSITE & VISITOR PORTAL
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicPortal(
    viewModel: SchoolViewModel,
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Home", "Facilities", "Notices", "Admission", "Contact", "Login Portal")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "JK",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "JK Montessori",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                "PRINCIPAL: S.K. MISHRA",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val icon = when (index) {
                        0 -> Icons.Filled.Home
                        1 -> Icons.Filled.GridView
                        2 -> Icons.Filled.Campaign
                        3 -> Icons.Filled.Assignment
                        4 -> Icons.Filled.ContactSupport
                        else -> Icons.Filled.LockOpen
                    }
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(title, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        icon = { Icon(icon, contentDescription = title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                0 -> PublicHomeScreen(onApplyClick = { selectedTab = 3 })
                1 -> FacilitiesScreen()
                2 -> PublicNoticesScreen(viewModel)
                3 -> OnlineAdmissionScreen(viewModel)
                4 -> ContactScreen()
                5 -> LoginScreen(viewModel)
            }
        }
    }
}

// ------------------------------------------
// PUBLIC TABS IMPLEMENTATIONS
// ------------------------------------------
@Composable
fun PublicHomeScreen(onApplyClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Programmatic Decorative Hero Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .drawBehind {
                    // Accent drawings
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = 350f,
                        center = Offset(size.width, 0f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.05f),
                        radius = 200f,
                        center = Offset(100f, size.height - 50f)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Welcome to JK Montessori",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Nursery to Class 12 • Co-Educational English & Hindi Medium",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onApplyClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply for Admission 2026-27", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // About the School
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "About Our Institution",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "JK Montessori School, situated in Subhash Chowk, Block Ghughli, Maharajganj, Uttar Pradesh, is a prestigious institution providing world-class values and modern educational standards. Guided by Mr. Sanjay Kumar Mishra, we ensure dedicated academic care for children of Nursery up to Class 12.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        }

        // Principal Message
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Principal Avatar",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "Principal's Message",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Mr. Sanjay Kumar Mishra",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "\"Our focus is complete personality growth, pairing academic excellence with character development, discipline, and Indian values.\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Stats Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(count = "25+", label = "Expert Teachers", icon = Icons.Filled.Groups)
            StatItem(count = "500+", label = "Students Active", icon = Icons.Filled.School)
            StatItem(count = "12+", label = "Bus Routes", icon = Icons.Filled.DirectionsBus)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun StatItem(count: String, label: String, icon: ImageVector) {
    Card(
        modifier = Modifier.width(105.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(count, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(label, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, fontSize = 9.sp)
        }
    }
}

@Composable
fun FacilitiesScreen() {
    val items = listOf(
        FacilityData("Modern Computer Lab", "Equipped with high-speed internet and structured syllabus.", Icons.Filled.Computer),
        FacilityData("Enriched Library", "Over 2,000 reference books, journals, and literature classics.", Icons.Filled.LibraryBooks),
        FacilityData("Secure Hostel", "Comfortable boarding separate for boys and girls with daily care.", Icons.Filled.Home),
        FacilityData("GPS Bus Service", "Fully monitored routes driven by experienced drivers for safety.", Icons.Filled.DirectionsBus),
        FacilityData("Science Laboratories", "Physics, Chemistry, and Biology practical instruments ready.", Icons.Filled.Science),
        FacilityData("Smart Classes", "Audiovisual projectors and graphics screens for interactive learning.", Icons.Filled.Tv)
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "School Facilities & Assets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(items) { facility ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(facility.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(facility.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(facility.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

data class FacilityData(val title: String, val desc: String, val icon: ImageVector)

@Composable
fun PublicNoticesScreen(viewModel: SchoolViewModel) {
    val notices by viewModel.allNotices.collectAsState()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "JK Montessori Notice Board",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (notices.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No notices posted currently.")
                }
            }
        } else {
            items(notices) { notice ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val badgeColor = when (notice.category) {
                                "Exam" -> Color(0xFFEF4444)
                                "Event" -> Color(0xFF3B82F6)
                                "Holiday" -> Color(0xFF10B981)
                                else -> Color(0xFFF59E0B)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(badgeColor)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(notice.category, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(notice.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(notice.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(notice.content, style = MaterialTheme.typography.bodySmall, lineHeight = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Posted by: ${notice.postedBy}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnlineAdmissionScreen(viewModel: SchoolViewModel) {
    var studentName by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("Nursery") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    
    val classes = listOf("Nursery", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "Class 6", "Class 7", "Class 8", "Class 9", "Class 10", "Class 11", "Class 12")
    var classDropdownExpanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Online Admission Registration 2026-27",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Please register details for your ward. We will verify documents and contact you shortly.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        if (submitted) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = "Success", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Application Registered Successfully!", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Your credentials & temporary reference number are generated. Please check with Admin desk at Ghughli Subhash Chowk with birth certificates.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { submitted = false }) {
                        Text("Register Another Admission")
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("Student Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("admission_student_name"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = parentName,
                        onValueChange = { parentName = it },
                        label = { Text("Parent / Guardian Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Class Selection Box
                    Box {
                        OutlinedButton(
                            onClick = { classDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Apply for: $className", color = MaterialTheme.colorScheme.onSurface)
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        DropdownMenu(
                            expanded = classDropdownExpanded,
                            onDismissRequest = { classDropdownExpanded = false }
                        ) {
                            classes.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = {
                                        className = c
                                        classDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Contact Phone") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email ID") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Mandatory Documents Required On Site:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text("• Student Passport Size Photograph\n• Birth Certificate issued by Municipality\n• Previous Academic Report Card (for Class 1+)\n• Address Proof (Aadhar Card)", fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (studentName.isNotBlank() && parentName.isNotBlank() && phone.isNotBlank()) {
                                viewModel.submitOnlineAdmission(studentName, parentName, className, phone, email)
                                studentName = ""
                                parentName = ""
                                phone = ""
                                email = ""
                                submitted = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("admission_submit_button"),
                        enabled = studentName.isNotBlank() && parentName.isNotBlank() && phone.isNotBlank()
                    ) {
                        Text("Submit Online Application", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ContactScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Contact & Location Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ContactRow(icon = Icons.Filled.LocationOn, label = "School Address", value = "Subhash Chowk, Block Ghughli, District Maharajganj, Uttar Pradesh, India - 273151")
                ContactRow(icon = Icons.Filled.Phone, label = "Phone Number", value = "9936293542 / 9936293543")
                ContactRow(icon = Icons.Filled.Email, label = "Principal Email", value = "bhola.co.Ghughli@gmail.com")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Frequently Asked Questions (FAQs)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        FAQItem(q = "What is the admission procedure at JK Montessori?", a = "Parents can apply online under the Admission tab or visit the school desk at Subhash Chowk, Ghughli. Upon screening and document verification, admissions are approved.")
        FAQItem(q = "Does school provide transport service?", a = "Yes, school bus services are active with integrated GPS covering Ghughli, Maharajganj, Siswa Bazar, and neighboring villages.")
        FAQItem(q = "What are the school timings?", a = "Nursery to Prep: 8:30 AM to 12:30 PM. Class 1 to 12: 8:00 AM to 2:00 PM (Monday to Saturday).")
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ContactRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun FAQItem(q: String, a: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(q, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.9f))
                Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(a, style = MaterialTheme.typography.bodySmall, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: SchoolViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    
    val loginError by viewModel.loginError.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Sign In to Management Portal",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text("Enterprise role-based secure login", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (loginError != null) {
                    Text(
                        text = loginError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("username_input"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().testTag("password_input"),
                    singleLine = true
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                        Text("Remember Me", fontSize = 12.sp)
                    }
                    TextButton(onClick = { /* Simulated Forgot Password action */ }) {
                        Text("Forgot Password?", fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.login(username, password) },
                    modifier = Modifier.fillMaxWidth().testTag("login_button"),
                    enabled = username.isNotBlank() && password.isNotBlank()
                ) {
                    Text("Secure Login", fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        // Quick Fill Section for Evaluation Ease
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    "Demo Quick Fill (8 Roles):",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                
                val demoRoles = listOf(
                    "admin" to "Admin", "teacher" to "Teacher",
                    "student" to "Student", "parent" to "Parent",
                    "accountant" to "Accountant", "librarian" to "Librarian",
                    "reception" to "Receptionist", "transport" to "Transport"
                )
                
                // Render demo quick-logins flow row
                Box(modifier = Modifier.heightIn(max = 120.dp).verticalScroll(rememberScrollState())) {
                    Column {
                        demoRoles.chunked(2).forEach { chunk ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                chunk.forEach { (usr, title) ->
                                    Button(
                                        onClick = {
                                            username = usr
                                            password = usr
                                            viewModel.login(usr, usr)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(title, fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// LOGGED IN PORTAL CONTAINER WITH DRAWER
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardContainer(
    viewModel: SchoolViewModel,
    user: User,
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    var activePanelTab by remember { mutableStateOf("Dashboard") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Sidebar list depending on user's role
    val menuItems = when (user.role) {
        "Admin" -> listOf("Dashboard", "Admissions", "Add Student/Teacher", "Library", "Transport/Hostel", "Notice Manager", "ID Cards", "Audit Logs")
        "Teacher" -> listOf("Dashboard", "Daily Attendance", "Homework Board", "Exam Entry", "Profile")
        "Student" -> listOf("Dashboard", "Homework", "Report Card", "Fee Collection", "Bus Tracking", "ID Card")
        "Parent" -> listOf("Dashboard", "Child Performance", "Fee Payments", "Teacher Chat", "Notice Board")
        "Accountant" -> listOf("Dashboard", "Tuition Receipts", "Expense Log", "Salary Strip")
        "Librarian" -> listOf("Dashboard", "Book Issue/Return", "Book Register")
        "Receptionist" -> listOf("Dashboard", "Gate Pass Register", "Complaint Board")
        else -> listOf("Dashboard", "Bus Routes", "Driver Profile") // Transport
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            user.fullName.take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(user.fullName, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Role: ${user.role}", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                    Text(user.email, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(menuItems) { item ->
                        NavigationDrawerItem(
                            label = { Text(item, fontWeight = FontWeight.Bold) },
                            selected = activePanelTab == item,
                            onClick = {
                                activePanelTab = item
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary
                            ),
                            icon = {
                                val itemIcon = when (item) {
                                    "Dashboard" -> Icons.Filled.Dashboard
                                    "Admissions" -> Icons.Filled.AssignmentInd
                                    "Add Student/Teacher" -> Icons.Filled.PersonAdd
                                    "Daily Attendance" -> Icons.Filled.HowToReg
                                    "Homework Board", "Homework" -> Icons.Filled.MenuBook
                                    "Exam Entry", "Report Card" -> Icons.Filled.Grade
                                    "Fee Collection", "Fee Payments", "Tuition Receipts" -> Icons.Filled.Payment
                                    "Library", "Book Issue/Return", "Book Register" -> Icons.Filled.Bookmark
                                    "Transport/Hostel", "Bus Tracking", "Bus Routes" -> Icons.Filled.DirectionsBus
                                    "Notice Board", "Notice Manager" -> Icons.Filled.Campaign
                                    "ID Cards", "ID Card" -> Icons.Filled.Badge
                                    "Audit Logs" -> Icons.Filled.History
                                    "Gate Pass Register" -> Icons.Filled.Doorbell
                                    else -> Icons.Filled.Person
                                }
                                Icon(itemIcon, contentDescription = null)
                            }
                        )
                    }
                }
                
                // Sidebar Footer Sign Out
                Divider()
                NavigationDrawerItem(
                    label = { Text("Sign Out", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = { viewModel.logout() },
                    modifier = Modifier.padding(12.dp),
                    icon = { Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(user.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(user.role, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = onToggleTheme) {
                            Icon(
                                imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = "Toggle Theme"
                            )
                        }
                        IconButton(onClick = { viewModel.logout() }) {
                            Icon(Icons.Filled.PowerSettingsNew, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (user.role) {
                    "Admin" -> AdminView(viewModel, activePanelTab)
                    "Teacher" -> TeacherView(viewModel, activePanelTab)
                    "Student" -> StudentView(viewModel, activePanelTab)
                    "Parent" -> ParentView(viewModel, activePanelTab)
                    "Accountant" -> AccountantView(viewModel, activePanelTab)
                    "Librarian" -> LibrarianView(viewModel, activePanelTab)
                    "Receptionist" -> ReceptionistView(viewModel, activePanelTab)
                    else -> TransportManagerView(viewModel, activePanelTab)
                }
            }
        }
    }
}

// ------------------------------------------
// REUSABLE STATS CARD
// ------------------------------------------
@Composable
fun PanelStatCard(title: String, value: String, subtitle: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

// ------------------------------------------
// REUSABLE SLEEK INTERFACE HERO CARD
// ------------------------------------------
@Composable
fun SleekDashboardHeroCard(
    roleName: String,
    welcomeName: String,
    enrollmentCount: Int,
    attendancePercentage: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.12f),
                        radius = 240f,
                        center = Offset(size.width - 50f, -50f)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = 160f,
                        center = Offset(50f, size.height + 30f)
                    )
                }
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "${roleName.uppercase()} DASHBOARD",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Good Morning,\n$welcomeName",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "TOTAL ENROLLMENT",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "$enrollmentCount",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "PRESENT TODAY",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = attendancePercentage,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ------------------------------------------
// REUSABLE CUSTOM DRAWN BAR CHART
// ------------------------------------------
@Composable
fun CustomBarChart(
    data: List<Pair<String, Float>>,
    barColor: Color,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { (label, value) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${value.toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .fillMaxHeight(value / 100f)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(barColor)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// ------------------------------------------
// REUSABLE DIGITAL ID CARD WITH BARCODE
// ------------------------------------------
@Composable
fun DigitalIDCard(
    fullName: String,
    role: String,
    idNumber: String,
    subDetails: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.School, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("JK MONTESSORI SCHOOL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Subhash Chowk, Ghughli, UP", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                }
            }
            
            // Photo & Credentials body
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.AccountBox, contentDescription = null, modifier = Modifier.size(54.dp), tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(role.uppercase(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("ID: $idNumber", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
            
            // Sub details list
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                subDetails.forEach { (lbl, valStr) ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text("$lbl: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text(valStr, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Barcode generator (Programmatic Strokes)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Canvas(
                    modifier = Modifier
                        .width(200.dp)
                        .height(35.dp)
                ) {
                    val barcodeValue = idNumber.hashCode().toString()
                    val barWidth = 4f
                    var currentX = 0f
                    var index = 0
                    while (currentX < size.width) {
                        val strokeWidth = if (barcodeValue.getOrNull(index % barcodeValue.length)?.isDigit() == true && barcodeValue[index % barcodeValue.length].digitToInt() % 2 == 0) barWidth * 2 else barWidth
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(currentX, 0f),
                            size = Size(strokeWidth, size.height)
                        )
                        currentX += strokeWidth + barWidth
                        index++
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("*$idNumber*", style = MaterialTheme.typography.labelSmall, letterSpacing = 2.sp)
            }
        }
    }
}

// ==========================================
// 1. ADMIN DASHBOARD VIEW
// ==========================================
@Composable
fun AdminView(viewModel: SchoolViewModel, activeTab: String) {
    val admissions by viewModel.allAdmissions.collectAsState()
    val students by viewModel.allStudents.collectAsState()
    val teachers by viewModel.allTeachers.collectAsState()
    val books by viewModel.allBooks.collectAsState()
    
    when (activeTab) {
        "Dashboard" -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                SleekDashboardHeroCard(
                    roleName = "Admin",
                    welcomeName = "Principal Mishra",
                    enrollmentCount = 1248 + students.size,
                    attendancePercentage = "94.2%"
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("Enterprise Operational Metrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                
                PanelStatCard(title = "Registered Students", value = "${students.size}", subtitle = "Nursery to 12th Std", icon = Icons.Filled.School, color = Color(0xFF3B82F6))
                PanelStatCard(title = "Active Faculty Staff", value = "${teachers.size}", subtitle = "Teacher list", icon = Icons.Filled.Groups, color = Color(0xFF10B981))
                PanelStatCard(title = "Pending Online Admissions", value = "${admissions.filter { it.status == "Pending" }.size}", subtitle = "Screening awaiting", icon = Icons.Filled.AssignmentInd, color = Color(0xFFF59E0B))
                PanelStatCard(title = "Library Register Count", value = "${books.size} Books", subtitle = "Available assets", icon = Icons.Filled.Bookmark, color = Color(0xFF8B5CF6))
                
                Spacer(modifier = Modifier.height(12.dp))
                CustomBarChart(
                    data = listOf("Admission" to 85f, "Attendance" to 92f, "Fee Coll." to 78f, "Exam Cert." to 95f),
                    barColor = MaterialTheme.colorScheme.primary,
                    title = "JK Montessori Performance Indices (Monthly)"
                )
            }
        }
        "Admissions" -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("Admission Screening Desk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                }
                if (admissions.isEmpty()) {
                    item { Text("No online admissions listed.") }
                } else {
                    items(admissions) { app ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Applicant: ${app.fullName}", fontWeight = FontWeight.Bold)
                                Text("Class: ${app.className} • Parent: ${app.parentName}")
                                Text("Contact: ${app.phone} • Email: ${app.email}")
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                when (app.status) {
                                                    "Approved" -> Color(0xFF10B981)
                                                    "Rejected" -> Color(0xFFEF4444)
                                                    else -> Color(0xFFF59E0B)
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(app.status, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    if (app.status == "Pending") {
                                        Row {
                                            Button(
                                                onClick = { viewModel.updateAdmissionStatus(app.id, "Approved") },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.padding(end = 4.dp).testTag("approve_button")
                                            ) {
                                                Text("Approve", fontSize = 10.sp, color = Color.White)
                                            }
                                            Button(
                                                onClick = { viewModel.updateAdmissionStatus(app.id, "Rejected") },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text("Reject", fontSize = 10.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        "Add Student/Teacher" -> {
            var inputMode by remember { mutableStateOf("Student") }
            var name by remember { mutableStateOf("") }
            var parent by remember { mutableStateOf("") }
            var phone by remember { mutableStateOf("") }
            var cls by remember { mutableStateOf("Class 10") }
            var sub by remember { mutableStateOf("Physics") }
            var qual by remember { mutableStateOf("B.Ed") }
            var sal by remember { mutableStateOf("30000") }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { inputMode = "Student" }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (inputMode == "Student") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)) {
                        Text("Add Student", color = if (inputMode == "Student") Color.White else MaterialTheme.colorScheme.onSurface)
                    }
                    Button(onClick = { inputMode = "Teacher" }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (inputMode == "Teacher") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)) {
                        Text("Add Teacher", color = if (inputMode == "Teacher") Color.White else MaterialTheme.colorScheme.onSurface)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Contact Phone") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        
                        if (inputMode == "Student") {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = parent, onValueChange = { parent = it }, label = { Text("Parent Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = cls, onValueChange = { cls = it }, label = { Text("Class") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = sub, onValueChange = { sub = it }, label = { Text("Subject Specialty") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = qual, onValueChange = { qual = it }, label = { Text("Qualifications") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = sal, onValueChange = { sal = it }, label = { Text("Salary (INR)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (name.isNotBlank() && phone.isNotBlank()) {
                                    if (inputMode == "Student") {
                                        viewModel.addStudent(name, cls, "A", parent, phone, "Ghughli, UP")
                                    } else {
                                        viewModel.addTeacher(name, phone, "${name.lowercase().replace(" ","")}@jkmontessori.com", sub, qual, sal.toDoubleOrNull() ?: 20000.0)
                                    }
                                    name = ""
                                    phone = ""
                                    parent = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("add_user_submit"),
                            enabled = name.isNotBlank() && phone.isNotBlank()
                        ) {
                            Text("Save Credentials", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        "Library" -> {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Text("School Book inventory", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(books) { b ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(b.title, fontWeight = FontWeight.Bold)
                                Text("Author: ${b.author} • Category: ${b.category}")
                                Text("ISBN: ${b.isbn}", style = MaterialTheme.typography.labelSmall)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${b.quantity - b.issuedQty}/${b.quantity}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Available", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
        "Transport/Hostel" -> {
            val routes by viewModel.allRoutes.collectAsState()
            val hostels by viewModel.allHostels.collectAsState()
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                Text("School Transport Fleet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                routes.forEach { r ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.DirectionsBus, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(r.routeName, fontWeight = FontWeight.Bold)
                                Text("Bus No: ${r.busNo} • Driver: ${r.driverName} (${r.driverPhone})")
                                Text("Monthly Route Fee: ₹${r.routeFee}", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("School Hostels Boarding", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                hostels.forEach { h ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Home, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(h.hostelName, fontWeight = FontWeight.Bold)
                                    Text("Room: ${h.roomNo} • Monthly Fee: ₹${h.feePerMonth}")
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${h.bedCount - h.occupiedCount} Beds", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Vacant", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
        "Notice Manager" -> {
            var title by remember { mutableStateOf("") }
            var content by remember { mutableStateOf("") }
            var category by remember { mutableStateOf("General") }
            val categories = listOf("General", "Exam", "Event", "Holiday")
            var catDropdown by remember { mutableStateOf(false) }
            
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                Text("Broadcast New Notice", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Notice Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box {
                            OutlinedButton(onClick = { catDropdown = true }, modifier = Modifier.fillMaxWidth()) {
                                Text("Category: $category")
                            }
                            DropdownMenu(expanded = catDropdown, onDismissRequest = { catDropdown = false }) {
                                categories.forEach { c ->
                                    DropdownMenuItem(text = { Text(c) }, onClick = { category = c; catDropdown = false })
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Notice Content details") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (title.isNotBlank() && content.isNotBlank()) {
                                    viewModel.addNotice(title, content, category)
                                    title = ""
                                    content = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = title.isNotBlank() && content.isNotBlank()
                        ) {
                            Text("Publish to Noticeboard", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        "ID Cards" -> {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                Text("Digital QR ID Generator Desk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Rendered live on-device credentials", style = MaterialTheme.typography.bodySmall)
                DigitalIDCard(
                    fullName = "Sanjay Kumar Mishra",
                    role = "Principal Admin",
                    idNumber = "JKMS-ADMIN-001",
                    subDetails = listOf("Phone" to "9936293542", "Email" to "bhola.co.Ghughli@gmail.com", "Auth" to "Supreme")
                )
            }
        }
        "Audit Logs" -> {
            val logs = listOf(
                "2026-07-17 10:22 AM - Admin Approved Admission Ref #04",
                "2026-07-17 09:15 AM - Teacher Manoj Sharma posted Homework on Physics",
                "2026-07-16 04:30 PM - Accountant collected Tuition Fee from Roll Class10-101",
                "2026-07-16 11:10 AM - Librarian issued Book 'Wings of Fire' to student Aarav Mishra"
            )
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Text("Security System Activity Audit Logs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(logs) { log ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(log, fontSize = 11.sp, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. TEACHER DASHBOARD VIEW
// ==========================================
@Composable
fun TeacherView(viewModel: SchoolViewModel, activeTab: String) {
    val students by viewModel.allStudents.collectAsState()
    
    when (activeTab) {
        "Dashboard" -> {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                SleekDashboardHeroCard(
                    roleName = "Teacher",
                    welcomeName = "Dr. Manoj Sharma",
                    enrollmentCount = 124,
                    attendancePercentage = "95.5%"
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("Faculty Overview Desk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                PanelStatCard(title = "Assigned Class", value = "Class 10 - Sec A", subtitle = "Main Subject: Physics", icon = Icons.Filled.Tv, color = Color(0xFF3B82F6))
                PanelStatCard(title = "Total Active Students", value = "${students.filter { it.className == "Class 10" }.size}", subtitle = "Class 10 desk roster", icon = Icons.Filled.Groups, color = Color(0xFF10B981))
                Spacer(modifier = Modifier.height(10.dp))
                CustomBarChart(
                    data = listOf("Mon" to 95f, "Tue" to 90f, "Wed" to 94f, "Thu" to 88f, "Fri" to 91f),
                    barColor = Color(0xFF10B981),
                    title = "Class 10 Weekly Attendance Performance"
                )
            }
        }
        "Daily Attendance" -> {
            var selectedStudentId by remember { mutableIntStateOf(-1) }
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Text("Daily Roster Attendance Roll", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                }
                items(students.filter { it.className == "Class 10" }) { student ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(student.fullName, fontWeight = FontWeight.Bold)
                                Text("Roll: ${student.rollNo}")
                            }
                            Row {
                                Button(onClick = { viewModel.markAttendance(student.id, "Present") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)), shape = RoundedCornerShape(6.dp), modifier = Modifier.padding(end = 4.dp)) {
                                    Text("P", color = Color.White)
                                }
                                Button(onClick = { viewModel.markAttendance(student.id, "Absent") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)), shape = RoundedCornerShape(6.dp)) {
                                    Text("A", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
        "Homework Board" -> {
            var hwTitle by remember { mutableStateOf("") }
            var hwDesc by remember { mutableStateOf("") }
            var dueDate by remember { mutableStateOf("2026-07-22") }
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                Text("Post Homework Assignment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(value = hwTitle, onValueChange = { hwTitle = it }, label = { Text("Task / Subject Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = hwDesc, onValueChange = { hwDesc = it }, label = { Text("Instruction Details") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (hwTitle.isNotBlank() && hwDesc.isNotBlank()) {
                                    viewModel.addHomework("Class 10", "A", "Physics", hwTitle, hwDesc, dueDate)
                                    hwTitle = ""
                                    hwDesc = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = hwTitle.isNotBlank() && hwDesc.isNotBlank()
                        ) {
                            Text("Assign Homework Task", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        "Exam Entry" -> {
            var marksStr by remember { mutableStateOf("") }
            var selectedStudent by remember { mutableStateOf<Student?>(null) }
            var studentDropdown by remember { mutableStateOf(false) }
            
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                Text("Exam Marks Verification Entry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box {
                            OutlinedButton(onClick = { studentDropdown = true }, modifier = Modifier.fillMaxWidth()) {
                                Text(selectedStudent?.fullName ?: "Select Student Roster")
                            }
                            DropdownMenu(expanded = studentDropdown, onDismissRequest = { studentDropdown = false }) {
                                students.filter { it.className == "Class 10" }.forEach { s ->
                                    DropdownMenuItem(text = { Text(s.fullName) }, onClick = { selectedStudent = s; studentDropdown = false })
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = marksStr, onValueChange = { marksStr = it }, label = { Text("Obtained Marks (Max 100)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val s = selectedStudent
                                val m = marksStr.toDoubleOrNull()
                                if (s != null && m != null) {
                                    viewModel.enterExamMarks(s.id, "Half Yearly", "Physics", m, 100.0)
                                    marksStr = ""
                                    selectedStudent = null
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedStudent != null && marksStr.isNotBlank()
                        ) {
                            Text("Record Academic Marks", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        "Profile" -> {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                Text("Faculty Credentials ID", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                DigitalIDCard(
                    fullName = "Dr. Manoj Sharma",
                    role = "Senior Faculty",
                    idNumber = "JKMS-TCH-042",
                    subDetails = listOf("Department" to "Physics Department", "Qualification" to "M.Sc, Ph.D", "Mobile" to "9936293543")
                )
            }
        }
    }
}

// ==========================================
// 3. STUDENT DASHBOARD VIEW
// ==========================================
@Composable
fun StudentView(viewModel: SchoolViewModel, activeTab: String) {
    val homeworks by viewModel.allHomework.collectAsState()
    val results by viewModel.allExamResults.collectAsState()
    val fees by viewModel.allFees.collectAsState()
    
    when (activeTab) {
        "Dashboard" -> {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                SleekDashboardHeroCard(
                    roleName = "Student",
                    welcomeName = "Aarav Mishra",
                    enrollmentCount = 1248,
                    attendancePercentage = "96%"
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("Academic Student Board", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                PanelStatCard(title = "My Register Roll", value = "Class 10 - A", subtitle = "Roll No: Class10-101", icon = Icons.Filled.AccountCircle, color = Color(0xFF3B82F6))
                PanelStatCard(title = "Pending Homework Tasks", value = "${homeworks.size}", subtitle = "Submission pending", icon = Icons.Filled.MenuBook, color = Color(0xFFF59E0B))
                PanelStatCard(title = "Attendance rate", value = "96%", subtitle = "Excellent consistency", icon = Icons.Filled.CheckCircle, color = Color(0xFF10B981))
                Spacer(modifier = Modifier.height(10.dp))
                CustomBarChart(
                    data = listOf("Hindi" to 82f, "English" to 88f, "Maths" to 95f, "Physics" to 88f, "Chemistry" to 91f),
                    barColor = MaterialTheme.colorScheme.tertiary,
                    title = "Quarterly Exam Marks Distribution"
                )
            }
        }
        "Homework" -> {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Text("My Active Homework Assignments", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(homeworks) { hw ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(hw.subject, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Due: ${hw.dueDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(hw.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
                            Text(hw.description, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }
        "Report Card" -> {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Text("Academic Progress Card", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(results.filter { it.studentId == 1 }) { r ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(r.subject, fontWeight = FontWeight.Bold)
                                Text("Exam Type: ${r.examName} • Total: ${r.totalMarks.toInt()}")
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${r.marksObtained.toInt()} Marks", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.tertiary).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                    Text("Grade: ${r.grade}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
        "Fee Collection" -> {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Text("School Fee Ledger History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(fees.filter { it.studentId == 1 }) { f ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(f.category, fontWeight = FontWeight.Bold)
                                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(if (f.status == "Paid") Color(0xFF10B981) else Color(0xFFEF4444)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                    Text(f.status, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Total Amount Due: ₹${f.amount}")
                            if (f.status == "Paid") {
                                Text("Receipt Paid On: ${f.paidDate ?: ""} via ${f.paymentMethod ?: ""}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Button(onClick = { viewModel.payFee(f.id, "UPI", f.amount) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary), shape = RoundedCornerShape(8.dp)) {
                                        Text("Pay via GPay/PhonePe", fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        "Bus Tracking" -> {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("School Bus GPS Telemetry Route", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Active Bus No: UP-56-T-1245", fontWeight = FontWeight.Bold)
                        Text("Driver Name: Rajesh Singh • Route: Ghughli to Subhash Chowk")
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.LightGray.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.DirectionsBus, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                                Text("Map GPS Tracking Screen (Active)", fontWeight = FontWeight.Bold)
                                Text("Coordinates: 26.90 N, 83.68 E", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
        "ID Card" -> {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                Text("Student Digital ID Credentials", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                DigitalIDCard(
                    fullName = "Aarav Mishra",
                    role = "Class 10 Student",
                    idNumber = "JKMS-STU-104",
                    subDetails = listOf("Class" to "10th - Sec A", "Parent" to "Rakesh Mishra", "Guardian Phone" to "9936293542")
                )
            }
        }
    }
}

// ==========================================
// 4. PARENT DASHBOARD VIEW
// ==========================================
@Composable
fun ParentView(viewModel: SchoolViewModel, activeTab: String) {
    val homeworks by viewModel.allHomework.collectAsState()
    val results by viewModel.allExamResults.collectAsState()
    val fees by viewModel.allFees.collectAsState()
    
    when (activeTab) {
        "Dashboard" -> {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                SleekDashboardHeroCard(
                    roleName = "Parent",
                    welcomeName = "Rakesh Mishra",
                    enrollmentCount = 1,
                    attendancePercentage = "100%"
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("Parent Ward Overview Desk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                PanelStatCard(title = "Ward Name", value = "Aarav Mishra", subtitle = "Class 10 - Sec A", icon = Icons.Filled.Person, color = Color(0xFF3B82F6))
                PanelStatCard(title = "Monthly Attendance rate", value = "96% Verified", subtitle = "Regular", icon = Icons.Filled.CheckCircle, color = Color(0xFF10B981))
                PanelStatCard(title = "School Fees Due", value = "₹850.0 Pending", subtitle = "Next due date 15th Aug", icon = Icons.Filled.Payment, color = Color(0xFFEF4444))
            }
        }
        "Child Performance" -> {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Text("Ward Academic Progress results", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(results.filter { it.studentId == 1 }) { r ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(r.subject, fontWeight = FontWeight.Bold)
                                Text("Remarks: ${r.remarks ?: ""}")
                            }
                            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.tertiary).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text("Grade: ${r.grade}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        "Fee Payments" -> {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Text("Ward Outstanding School Fees", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(fees.filter { it.studentId == 1 }) { f ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(f.category, fontWeight = FontWeight.Bold)
                                Text(f.status, fontWeight = FontWeight.Bold, color = if (f.status == "Paid") Color(0xFF10B981) else Color(0xFFEF4444))
                            }
                            Text("Fee Amount: ₹${f.amount}")
                            if (f.status != "Paid") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { viewModel.payFee(f.id, "Razorpay", f.amount) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                                    Text("Pay Securely ₹${f.amount}", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
        "Teacher Chat" -> {
            var chatInput by remember { mutableStateOf("") }
            val chats = remember { mutableStateListOf(
                "Parent" to "Namaskar Dr. Sharma, how is Aarav performing in Physics class?",
                "Teacher" to "Namaskar Rakesh ji, Aarav is exceptionally intelligent and focused. He scored 95% in Mathematics!"
            ) }
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Direct Communication Desk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(chats.toList()) { (sender, msg) ->
                        val alignLeft = sender == "Teacher"
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (alignLeft) Alignment.CenterStart else Alignment.CenterEnd) {
                            Card(
                                modifier = Modifier.fillMaxWidth(0.8f),
                                colors = CardDefaults.cardColors(containerColor = if (alignLeft) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(sender, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                    Text(msg, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = chatInput, onValueChange = { chatInput = it }, placeholder = { Text("Ask Teacher anything...") }, modifier = Modifier.weight(1f), singleLine = true)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        if (chatInput.isNotBlank()) {
                            chats.add("Parent" to chatInput)
                            chatInput = ""
                        }
                    }) {
                        Icon(Icons.Filled.Send, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        "Notice Board" -> {
            PublicNoticesScreen(viewModel)
        }
    }
}

// ==========================================
// 5. ACCOUNTANT VIEW
// ==========================================
@Composable
fun AccountantView(viewModel: SchoolViewModel, activeTab: String) {
    val fees by viewModel.allFees.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("School Treasury & Accounts Dashboard", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        val totalCollected = fees.filter { it.status == "Paid" }.sumOf { it.paidAmount }
        val totalPending = fees.filter { it.status != "Paid" }.sumOf { it.amount }
        PanelStatCard(title = "Total Tuition Collection", value = "₹${totalCollected}", subtitle = "Prepopulated bank ledger", icon = Icons.Filled.AccountBalanceWallet, color = Color(0xFF10B981))
        PanelStatCard(title = "Outstanding Receivables", value = "₹${totalPending}", subtitle = "Overdue parent bills", icon = Icons.Filled.Error, color = Color(0xFFEF4444))
        
        Spacer(modifier = Modifier.height(12.dp))
        CustomBarChart(
            data = listOf("Apr" to 75f, "May" to 82f, "Jun" to 60f, "Jul" to 95f),
            barColor = Color(0xFF10B981),
            title = "JK School Monthly Cash Inflows"
        )
    }
}

// ==========================================
// 6. LIBRARIAN VIEW
// ==========================================
@Composable
fun LibrarianView(viewModel: SchoolViewModel, activeTab: String) {
    val books by viewModel.allBooks.collectAsState()
    val issues by viewModel.allBookIssues.collectAsState()
    
    when (activeTab) {
        "Dashboard" -> {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                Text("Library Inventory Desk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                PanelStatCard(title = "Total Catalog Books", value = "${books.sumOf { it.quantity }}", subtitle = "Registered textbook items", icon = Icons.Filled.Bookmark, color = Color(0xFF3B82F6))
                PanelStatCard(title = "Active Book Issues", value = "${issues.filter { it.status == "Issued" }.size}", subtitle = "Issued to student accounts", icon = Icons.Filled.CompareArrows, color = Color(0xFFF59E0B))
            }
        }
        "Book Issue/Return" -> {
            var selectedBook by remember { mutableStateOf<LibraryBook?>(null) }
            var bookDrop by remember { mutableStateOf(false) }
            
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                Text("Verify Book Issue Roster", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box {
                            OutlinedButton(onClick = { bookDrop = true }, modifier = Modifier.fillMaxWidth()) {
                                Text(selectedBook?.title ?: "Select Book From Inventory")
                            }
                            DropdownMenu(expanded = bookDrop, onDismissRequest = { bookDrop = false }) {
                                books.forEach { b ->
                                    DropdownMenuItem(text = { Text(b.title) }, onClick = { selectedBook = b; bookDrop = false })
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val b = selectedBook
                                if (b != null) {
                                    viewModel.issueBook(b.id, 1, "2026-07-28")
                                    selectedBook = null
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedBook != null
                        ) {
                            Text("Complete Book Issue Roster", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Active Issued Books Desk", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                issues.filter { it.status == "Issued" }.forEach { i ->
                    val bookTitle = books.find { it.id == i.bookId }?.title ?: "Catalog Book"
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(bookTitle, fontWeight = FontWeight.Bold)
                                Text("Issued Date: ${i.issueDate} • Return: ${i.returnDate ?: ""}")
                            }
                            Button(onClick = { viewModel.returnBook(i.id) }, shape = RoundedCornerShape(6.dp)) {
                                Text("Return", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Text("Complete Book Registers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(books) { b ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(b.title, fontWeight = FontWeight.Bold)
                                Text("Author: ${b.author} • Category: ${b.category}")
                            }
                            Text("${b.quantity - b.issuedQty}/${b.quantity} Left", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. RECEPTIONIST VIEW
// ==========================================
@Composable
fun ReceptionistView(viewModel: SchoolViewModel, activeTab: String) {
    val visitorLog = remember { mutableStateListOf(
        Triple("Sunil Roy", "Parent Query", "10:30 AM"),
        Triple("Vijay Prasad", "Supplier Bill Submit", "11:15 AM")
    ) }
    var vName by remember { mutableStateOf("") }
    var vPurpose by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Visitor Front Desk Registry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(value = vName, onValueChange = { vName = it }, label = { Text("Visitor Full Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = vPurpose, onValueChange = { vPurpose = it }, label = { Text("Purpose of Visit") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (vName.isNotBlank() && vPurpose.isNotBlank()) {
                            visitorLog.add(Triple(vName, vPurpose, "12:00 PM"))
                            vName = ""
                            vPurpose = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = vName.isNotBlank() && vPurpose.isNotBlank()
                ) {
                    Text("Issue Security Gate Pass", fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Daily Visitor Logs History", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        visitorLog.forEach { (name, purp, time) ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(name, fontWeight = FontWeight.Bold)
                        Text("Purpose: $purp")
                    }
                    Text(time, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

// ==========================================
// 8. TRANSPORT MANAGER VIEW
// ==========================================
@Composable
fun TransportManagerView(viewModel: SchoolViewModel, activeTab: String) {
    val routes by viewModel.allRoutes.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("School Transport Monitor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        routes.forEach { r ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(r.routeName, fontWeight = FontWeight.Bold)
                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFF10B981)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text("Active GPS", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("Bus Registration: ${r.busNo} • Driver Name: ${r.driverName}")
                    Text("Driver Mobile: ${r.driverPhone}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
