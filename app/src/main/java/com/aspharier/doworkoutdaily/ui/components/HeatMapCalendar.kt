package com.aspharier.doworkoutdaily.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aspharier.doworkoutdaily.data.model.WorkoutSession
import com.aspharier.doworkoutdaily.ui.theme.AcidLime
import com.aspharier.doworkoutdaily.ui.theme.Cream
import com.aspharier.doworkoutdaily.ui.theme.HeatL1
import com.aspharier.doworkoutdaily.ui.theme.HeatL2
import com.aspharier.doworkoutdaily.ui.theme.HeatL3
import com.aspharier.doworkoutdaily.ui.theme.HypeMagenta
import com.aspharier.doworkoutdaily.ui.theme.Ink
import com.aspharier.doworkoutdaily.ui.theme.JetBrainsMono
import com.aspharier.doworkoutdaily.ui.theme.Paper
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

@Composable
fun HeatMapCalendar(
    sessions: List<WorkoutSession>,
    currentMonth: YearMonth,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val lastDayOfMonth = currentMonth.atEndOfMonth()
    val today = LocalDate.now()
    
    val endWeekSunday = lastDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    val totalWeeks = 16
    val startDate = endWeekSunday.minusWeeks((totalWeeks - 1).toLong())
    
    val daysInWeek = 7
    val cellSpacing = 2.5.dp

    val sessionMap = sessions.associateBy { it.date }

    Column(modifier = modifier.fillMaxWidth()) {
        // Month labels row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val startMonth = startDate.month.name.take(3)
            val endMonth = lastDayOfMonth.month.name.take(3)
            
            Text(
                text = startMonth,
                fontFamily = JetBrainsMono,
                fontSize = 10.sp,
                color = Ink,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = endMonth,
                fontFamily = JetBrainsMono,
                fontSize = 10.sp,
                color = Ink,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            // Day labels column
            Column(
                modifier = Modifier
                    .width(18.dp)
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.spacedBy(cellSpacing)
            ) {
                val days = listOf("S", "M", "T", "W", "T", "F", "S")
                days.forEach { day ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            color = Ink,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            // Heatmap grid (Stretches across 100% width)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(cellSpacing)
            ) {
                for (week in 0 until totalWeeks) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(cellSpacing)
                    ) {
                        for (dayOffset in 0 until daysInWeek) {
                            val currentDate = startDate.plusDays((week * 7 + dayOffset).toLong())
                            val isFuture = currentDate.isAfter(today)
                            val isToday = currentDate.isEqual(today)
                            val session = sessionMap[currentDate.toString()]
                            val hasSession = session != null
                            val intensity = session?.totalEntries ?: 0
                            
                            val bgColor = when {
                                isFuture -> Cream.copy(alpha = 0.3f)
                                isToday -> HypeMagenta
                                intensity == 0 -> Paper
                                intensity in 1..2 -> HeatL1
                                intensity in 3..5 -> HeatL2
                                else -> HeatL3
                            }
                            
                            val borderColor = if (isToday) Ink else Color.Transparent
                            val borderWidth = if (isToday) 1.5.dp else 0.dp
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(bgColor)
                                    .border(borderWidth, borderColor, RoundedCornerShape(3.dp))
                                    .clickable(!isFuture) { onDayClick(currentDate) }
                            ) {
                                // Draw X for missed past days
                                if (!isFuture && !hasSession && !isToday) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val padding = 2.5.dp.toPx()
                                        val strokeW = 1.2.dp.toPx()
                                        
                                        drawLine(
                                            color = HypeMagenta,
                                            start = Offset(padding, padding),
                                            end = Offset(size.width - padding, size.height - padding),
                                            strokeWidth = strokeW,
                                            cap = StrokeCap.Round
                                        )
                                        drawLine(
                                            color = HypeMagenta,
                                            start = Offset(size.width - padding, padding),
                                            end = Offset(padding, size.height - padding),
                                            strokeWidth = strokeW,
                                            cap = StrokeCap.Round
                                        )
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
