package com.aspharier.doworkoutdaily.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aspharier.doworkoutdaily.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import coil3.compose.AsyncImage

@Composable
fun HeatMapCalendar(
    workoutDates: Map<LocalDate, Int>,
    selfiesData: Map<LocalDate, String> = emptyMap(),
    modifier: Modifier = Modifier,
    onDateClick: (LocalDate) -> Unit = {}
) {
    val themeMode = LocalThemeMode.current
    val today = remember { LocalDate.now() }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(modifier = modifier) {
        // Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Rounded.ChevronLeft, contentDescription = "Previous Month")
            }
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = { currentMonth = currentMonth.plusMonths(1) },
                enabled = currentMonth.isBefore(YearMonth.now())
            ) {
                Icon(Icons.Rounded.ChevronRight, contentDescription = "Next Month")
            }
        }

        MonthGrid(
            yearMonth = currentMonth,
            workoutDates = workoutDates,
            selfiesData = selfiesData,
            today = today,
            themeMode = themeMode,
            onDateClick = onDateClick
        )
    }
}

@Composable
private fun MonthGrid(
    yearMonth: YearMonth,
    workoutDates: Map<LocalDate, Int>,
    selfiesData: Map<LocalDate, String>,
    today: LocalDate,
    themeMode: ThemeMode,
    onDateClick: (LocalDate) -> Unit
) {
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val startDayOfWeek = firstDay.dayOfWeek.value // 1 = Monday

    Column {
        // Day headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 9.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Calendar grid
        var dayCounter = 1
        val totalCells = daysInMonth + (startDayOfWeek - 1)
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - (startDayOfWeek - 1) + 1

                    if (dayNumber in 1..daysInMonth) {
                        val date = yearMonth.atDay(dayNumber)
                        val count = workoutDates[date] ?: 0
                        val selfiePath = selfiesData[date]
                        val isToday = date == today
                        val isFuture = date.isAfter(today)

                        HeatMapCell(
                            count = if (isFuture) -1 else count,
                            selfiePath = selfiePath,
                            isToday = isToday,
                            themeMode = themeMode,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(1.5.dp)
                                .clickable(enabled = !isFuture) { onDateClick(date) }
                        )
                    } else {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(1.5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeatMapCell(
    count: Int,
    selfiePath: String?,
    isToday: Boolean,
    themeMode: ThemeMode,
    modifier: Modifier = Modifier
) {
    val baseColor = when {
        count < 0 -> Color.Transparent // Future date
        count == 0 -> if (themeMode == ThemeMode.AMOLED_BLACK) HeatmapAmoledEmpty else HeatmapBlossomEmpty
        count == 1 -> if (themeMode == ThemeMode.AMOLED_BLACK) HeatmapAmoledLevel1 else HeatmapBlossomLevel1
        count == 2 -> if (themeMode == ThemeMode.AMOLED_BLACK) HeatmapAmoledLevel2 else HeatmapBlossomLevel2
        count == 3 -> if (themeMode == ThemeMode.AMOLED_BLACK) HeatmapAmoledLevel3 else HeatmapBlossomLevel3
        else -> if (themeMode == ThemeMode.AMOLED_BLACK) HeatmapAmoledLevel4 else HeatmapBlossomLevel4
    }

    val animatedColor by animateColorAsState(
        targetValue = baseColor,
        animationSpec = tween(300),
        label = "heatmap_cell"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(animatedColor)
            .then(
                if (isToday) {
                    Modifier.background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(3.dp)
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selfiePath != null) {
            AsyncImage(
                model = selfiePath,
                contentDescription = "Workout Selfie",
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(3.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        } else if (isToday) {
            Icon(
                imageVector = Icons.Rounded.CameraAlt,
                contentDescription = "Take Selfie",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
