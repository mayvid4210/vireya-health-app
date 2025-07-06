package com.example.vireya

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

val pixelFontCalendar = FontFamily(Font(R.font.vt323))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    prescriptions: List<PrescriptionData> = defaultPrescriptions
) {
    val greenColor = Color(0xFF2E7D32)
    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val context = LocalContext.current

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val checkedStates = remember { mutableStateMapOf<Pair<String, LocalDate>, Boolean>() }

    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7 // Sunday = 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Your Calendar",
                        fontFamily = pixelFontCalendar,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = greenColor)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                IconButton(onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                }) {
                    Text("←", fontFamily = pixelFontCalendar, color = greenColor)
                }
                Text(
                    text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " ${currentMonth.year}",
                    fontFamily = pixelFontCalendar,
                    style = MaterialTheme.typography.titleLarge,
                    color = greenColor
                )
                IconButton(onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                }) {
                    Text("→", fontFamily = pixelFontCalendar, color = greenColor)
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                    Text(
                        day,
                        modifier = Modifier.weight(1f),
                        fontFamily = pixelFontCalendar,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentPadding = PaddingValues(2.dp)
            ) {
                items(firstDayOfMonth) {
                    Box(modifier = Modifier.size(40.dp)) {}
                }

                items((1..daysInMonth).toList()) { day ->
                    val date = currentMonth.atDay(day)

                    Column(
                        modifier = Modifier
                            .padding(2.dp)
                            .background(if (date == today) Color(0xFFD0F0C0) else Color(0xFFF1F8E9))
                            .padding(6.dp)
                            .clickable {
                                selectedDate = date
                                Toast.makeText(context, "Here's your meds for the day!", Toast.LENGTH_SHORT).show()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("$day", fontFamily = pixelFontCalendar, fontWeight = FontWeight.Bold)
                    }
                }
            }

            selectedDate?.let { date ->
                val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                val meds = prescriptions.filter {
                    it.days.contains(dayName, ignoreCase = true) || it.days.equals("Daily", ignoreCase = true)
                }

                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Text(
                        text = "Medications for ${date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }} ${date.dayOfMonth}",
                        fontFamily = pixelFontCalendar,
                        fontWeight = FontWeight.Bold,
                        color = greenColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                    ) {
                        items(meds.size) { index ->
                            val med = meds[index]
                            val checked = checkedStates[med.name to date] ?: false

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .background(Color(0xFFE8F5E9))
                                    .padding(9.dp)
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = {
                                        checkedStates[med.name to date] = it
                                        if (it) Toast.makeText(context, "✔️ ${med.name} taken. Good job!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = greenColor)
                                )

                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(med.name, fontFamily = pixelFontCalendar, fontWeight = FontWeight.Bold)
                                    Text("Time: ${med.time}", fontFamily = pixelFontCalendar)
                                    Text("Tips: ${med.tips}", fontFamily = pixelFontCalendar)
                                    Text("Duration: ${med.duration}", fontFamily = pixelFontCalendar, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

val defaultPrescriptions = listOf(
    PrescriptionData("Paracetamol", "8:00 AM", "Take after meals", "Mon, Wed, Fri", "1 week"),
    PrescriptionData("Vitamin D", "9:00 AM", "Sunlight advised", "Daily", "1 month"),
    PrescriptionData("Ibuprofen", "10:00 AM", "Drink water", "Daily", "5 days"),
    PrescriptionData("Aspirin", "12:00 PM", "Take with food", "Daily", "2 weeks"),
    PrescriptionData("Amoxicillin", "2:00 PM", "Complete full course", "Daily", "10 days"),
    PrescriptionData("Cetirizine", "4:00 PM", "Avoid driving", "Tue, Thu", "2 weeks"),
    PrescriptionData("Metformin", "6:00 PM", "Monitor blood sugar", "Daily", "3 weeks"),
    PrescriptionData("Omeprazole", "8:00 PM", "Before bedtime", "Daily", "1 week"),
    PrescriptionData("Calcium", "10:00 PM", "Take with milk", "Sat, Sun", "1 month")
)



