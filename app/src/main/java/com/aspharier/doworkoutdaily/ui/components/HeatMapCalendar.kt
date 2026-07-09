package com.aspharier.doworkoutdaily.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.alpha
import com.aspharier.doworkoutdaily.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { currentMonth = currentMonth.minusMonths(1) },
                modifier = Modifier
                    .size(26.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronLeft,
                    contentDescription = "Previous Month",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = { currentMonth = currentMonth.plusMonths(1) },
                enabled = currentMonth.isBefore(YearMonth.now()),
                modifier = Modifier
                    .size(26.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = "Next Month",
                    tint = if (currentMonth.isBefore(YearMonth.now())) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.size(14.dp)
                )
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
    
    // Sunday-first offset: Mon=1, Tue=2... Sun=7 -> Sun=0, Mon=1...
    val startDayOfWeek = firstDay.dayOfWeek.value % 7

    Column {
        // Day headers (Sunday-first)
        val daysOfWeekFull = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
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
        val totalCells = daysInMonth + startDayOfWeek
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - startDayOfWeek + 1

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
                                .then(
                                    if (isFuture) Modifier.alpha(0.2f) else Modifier
                                )
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
        count < 0 -> if (themeMode == ThemeMode.AMOLED_BLACK) V2DarkHeat0 else V2LightHeat0
        count == 0 -> if (themeMode == ThemeMode.AMOLED_BLACK) V2DarkHeat0 else V2LightHeat0
        count == 1 -> if (themeMode == ThemeMode.AMOLED_BLACK) V2DarkHeat1 else V2LightHeat1
        count == 2 -> if (themeMode == ThemeMode.AMOLED_BLACK) V2DarkHeat2 else V2LightHeat2
        count == 3 -> if (themeMode == ThemeMode.AMOLED_BLACK) V2DarkHeat3 else V2LightHeat3
        else -> if (themeMode == ThemeMode.AMOLED_BLACK) V2DarkHeat4 else V2LightHeat4
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

    val outlineColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(animatedColor)
            .semantics { contentDescription = cellDescription }
            .then(
                if (isToday) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = outlineColor.copy(alpha = borderAlpha),
                        shape = RoundedCornerShape(4.dp)
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selfiePath != null) {
            // Background Image
            AsyncImage(
                model = selfiePath,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            // Shadow overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                            startY = 10f
                        )
                    )
            )
        }

        // Missed day diagonal line
        if (isPast && count == 0 && selfiePath == null) {
            val lineColor = if (themeMode == ThemeMode.AMOLED_BLACK) V2DarkTextMute else V2LightTextMute
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    color = lineColor.copy(alpha = 0.5f),
                    start = androidx.compose.ui.geometry.Offset(size.width * 0.22f, size.height * 0.78f),
                    end = androidx.compose.ui.geometry.Offset(size.width * 0.78f, size.height * 0.22f),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }

        // Today's Camera Dot
        if (isToday && selfiePath == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        // Day number label
        if (count >= 0) { // Not future
            val textColor = if (selfiePath != null) {
                Color.White
            } else if (isToday) {
                MaterialTheme.colorScheme.primary
            } else if (count > 0) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            }
            
            Text(
                text = "$dayNumber",
                fontSize = 8.5.sp,
                color = textColor,
                modifier = Modifier
                    .then(
                        if (selfiePath != null) Modifier.align(Alignment.BottomStart).padding(horizontal = 3.dp, vertical = 2.dp)
                        else Modifier.align(Alignment.Center)
                    ),
                fontWeight = if (isToday || selfiePath != null) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}


