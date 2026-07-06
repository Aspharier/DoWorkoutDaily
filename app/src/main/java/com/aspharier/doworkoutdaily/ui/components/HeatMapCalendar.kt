package com.aspharier.doworkoutdaily.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import coil3.compose.AsyncImage
import androidx.compose.foundation.border
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun HeatMapCalendar(
    workoutDates: Map<LocalDate, Int>,
    selfiesData: Map<LocalDate, String> = emptyMap(),
    modifier: Modifier = Modifier,
    onDateClick: (LocalDate) -> Unit = {},
    onDateLongClick: (LocalDate) -> Unit = {}
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

        AnimatedContent(
            targetState = currentMonth,
            transitionSpec = {
                if (targetState.isAfter(initialState)) {
                    (slideInHorizontally { width -> width } + fadeIn(tween(220))).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut(tween(220))
                    )
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn(tween(220))).togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut(tween(220))
                    )
                }
            },
            label = "month_transition"
        ) { targetMonth ->
            MonthGrid(
                yearMonth = targetMonth,
                workoutDates = workoutDates,
                selfiesData = selfiesData,
                today = today,
                themeMode = themeMode,
                onDateClick = onDateClick,
                onDateLongClick = onDateLongClick
            )
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun MonthGrid(
    yearMonth: YearMonth,
    workoutDates: Map<LocalDate, Int>,
    selfiesData: Map<LocalDate, String>,
    today: LocalDate,
    themeMode: ThemeMode,
    onDateClick: (LocalDate) -> Unit,
    onDateLongClick: (LocalDate) -> Unit
) {
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val startDayOfWeek = firstDay.dayOfWeek.value // 1 = Monday

    Column {
        // Day headers
        val daysOfWeekFull = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeekFull.forEach { dayFull ->
                Text(
                    text = dayFull.take(1),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = dayFull },
                    textAlign = TextAlign.Center,
                    fontSize = 9.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Calendar grid
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
                        val isPast = date.isBefore(today)

                        HeatMapCell(
                            dayNumber = dayNumber,
                            count = if (isFuture) -1 else count,
                            selfiePath = selfiePath,
                            isToday = isToday,
                            isPast = isPast,
                            themeMode = themeMode,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(1.5.dp)
                                .combinedClickable(
                                    enabled = !isFuture,
                                    onClick = { onDateClick(date) },
                                    onLongClick = { onDateLongClick(date) }
                                )
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
    dayNumber: Int,
    count: Int,
    selfiePath: String?,
    isToday: Boolean,
    isPast: Boolean,
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

    val borderAlpha by if (isToday) {
        val infiniteTransition = rememberInfiniteTransition(label = "today_pulse")
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "border_alpha"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    val cellDescription = when {
        count < 0 -> "Future date"
        isToday -> "Today" + (if (selfiePath != null) ", selfie logged" else "") + (if (count == 0) ", no workouts yet" else ", $count workouts")
        isPast && count == 0 && selfiePath == null -> "Day $dayNumber, missed workout"
        else -> "Day $dayNumber" + (if (selfiePath != null) ", selfie logged" else "") + ", $count workouts"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(animatedColor)
            .semantics { contentDescription = cellDescription }
            .then(
                if (isToday) {
                    Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(3.dp)
                        )
                        .border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha),
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
        }

        if (isPast && count == 0 && selfiePath == null) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Missed",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            )
        } else if (isToday && selfiePath == null) {
            Icon(
                imageVector = Icons.Rounded.CameraAlt,
                contentDescription = "Take Selfie",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Day number label
        if (count >= 0) { // Not future
            Text(
                text = "$dayNumber",
                fontSize = 7.sp,
                color = if (isToday)
                    MaterialTheme.colorScheme.primary
                else if (count > 0 || selfiePath != null)
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(2.dp),
                fontWeight = if (isToday) FontWeight.Bold else null
            )
        }
    }
}
