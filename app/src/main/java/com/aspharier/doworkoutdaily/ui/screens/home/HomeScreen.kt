package com.aspharier.doworkoutdaily.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.*
import com.aspharier.doworkoutdaily.data.repository.WorkoutRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: WorkoutRepository,
    onNavigateToLog: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DoWorkoutDaily",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Streak Hero Section ──
            StreakHeroCard(
                streak = uiState.currentStreak,
                hasWorkedOutToday = uiState.hasWorkedOutToday
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Today's Status ──
            TodayStatusCard(
                hasWorkedOutToday = uiState.hasWorkedOutToday,
                workoutCount = uiState.todayWorkouts.size,
                onLogWorkout = onNavigateToLog
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Quick Stats Row ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Streak",
                    value = "${uiState.currentStreak}",
                    emoji = "🔥",
                    isStreak = true,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total",
                    value = "${uiState.totalWorkouts}",
                    emoji = "💪",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "This Week",
                    value = "${uiState.thisWeekCount}",
                    emoji = "📅",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Recent Activity ──
            if (uiState.todayWorkouts.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Today's Workouts",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        uiState.todayWorkouts.forEach { workout ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val wType = com.aspharier.doworkoutdaily.data.model.WorkoutType.fromName(workout.workoutType)
                                Text(
                                    text = wType.emoji,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = wType.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${workout.durationMinutes} min",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StreakHeroCard(
    streak: Int,
    hasWorkedOutToday: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Fire Lottie animation
                if (streak > 0) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.Asset("Fire.json")
                    )
                    val progress by animateLottieCompositionAsState(
                        composition,
                        iterations = LottieConstants.IterateForever
                    )
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(100.dp)
                    )
                } else {
                    // Dimmed state when no streak
                    Text(
                        text = "🔥",
                        fontSize = 64.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Streak number with animated counter
                val animatedStreak by animateIntAsState(
                    targetValue = streak,
                    animationSpec = tween(600),
                    label = "streak_counter"
                )

                Text(
                    text = "$animatedStreak",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 72.sp
                )

                Text(
                    text = if (streak == 1) "day streak" else "day streak",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (hasWorkedOutToday) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Done for today!",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayStatusCard(
    hasWorkedOutToday: Boolean,
    workoutCount: Int,
    onLogWorkout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasWorkedOutToday)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (hasWorkedOutToday) "Great work today! 🎉" else "Ready to sweat? 💪",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (hasWorkedOutToday)
                        "$workoutCount workout${if (workoutCount > 1) "s" else ""} logged"
                    else
                        "No workout logged yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            FilledTonalButton(
                onClick = onLogWorkout,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    emoji: String,
    modifier: Modifier = Modifier,
    isStreak: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isStreak) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.Asset("Fire.json")
                )
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Text(text = emoji, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
