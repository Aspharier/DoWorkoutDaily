package com.aspharier.doworkoutdaily.ui.screens.streak

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.*
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.aspharier.doworkoutdaily.ui.components.HeatMapCalendar
import java.io.File
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakScreen(
    repository: com.aspharier.doworkoutdaily.data.repository.WorkoutRepository,
    viewModel: StreakViewModel = viewModel(factory = StreakViewModel.Factory(repository))
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var clickedDate by remember { mutableStateOf<LocalDate?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                clickedDate?.let { date ->
                    viewModel.saveSelfie(date, uri.toString())
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Your Streak",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
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
            Spacer(modifier = Modifier.height(8.dp))

            // ── Streak Fire ──
            if (uiState.currentStreak > 0) {
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
                    modifier = Modifier.size(80.dp)
                )
            }

            // ── Stats Grid ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StreakStatCard(
                    label = "Current",
                    value = uiState.currentStreak,
                    emoji = "🔥",
                    modifier = Modifier.weight(1f)
                )
                StreakStatCard(
                    label = "Longest",
                    value = uiState.longestStreak,
                    emoji = "🏆",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StreakStatCard(
                    label = "Total Days",
                    value = uiState.totalDays,
                    emoji = "📅",
                    modifier = Modifier.weight(1f)
                )
                StreakStatCard(
                    label = "Workouts",
                    value = uiState.totalWorkouts,
                    emoji = "💪",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Heat Map ──
            Text(
                text = "Activity",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                HeatMapCalendar(
                    workoutDates = uiState.heatmapData,
                    selfiesData = uiState.selfiesData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onDateClick = { date ->
                        if (date == LocalDate.now() && uiState.selfiesData[date] == null) {
                            val photoFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "selfie_${date}.jpg")
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                            currentPhotoUri = uri
                            clickedDate = date
                            cameraLauncher.launch(uri)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Legend ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Less",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                HeatMapLegend()
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "More",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StreakStatCard(
    label: String,
    value: Int,
    emoji: String,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(600),
        label = "stat_$label"
    )

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
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$animatedValue",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HeatMapLegend() {
    val themeMode = com.aspharier.doworkoutdaily.ui.theme.LocalThemeMode.current
    val colors = if (themeMode == com.aspharier.doworkoutdaily.ui.theme.ThemeMode.AMOLED_BLACK) {
        listOf(
            com.aspharier.doworkoutdaily.ui.theme.HeatmapAmoledEmpty,
            com.aspharier.doworkoutdaily.ui.theme.HeatmapAmoledLevel1,
            com.aspharier.doworkoutdaily.ui.theme.HeatmapAmoledLevel2,
            com.aspharier.doworkoutdaily.ui.theme.HeatmapAmoledLevel3,
            com.aspharier.doworkoutdaily.ui.theme.HeatmapAmoledLevel4
        )
    } else {
        listOf(
            com.aspharier.doworkoutdaily.ui.theme.HeatmapBlossomEmpty,
            com.aspharier.doworkoutdaily.ui.theme.HeatmapBlossomLevel1,
            com.aspharier.doworkoutdaily.ui.theme.HeatmapBlossomLevel2,
            com.aspharier.doworkoutdaily.ui.theme.HeatmapBlossomLevel3,
            com.aspharier.doworkoutdaily.ui.theme.HeatmapBlossomLevel4
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .padding(0.5.dp)
                    .then(
                        Modifier
                            .fillMaxSize()
                            .then(
                                Modifier
                                    .aspectRatio(1f)
                                    .then(
                                        Modifier
                                            .padding(0.dp)
                                    )
                            )
                    )
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = color,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                    )
                }
            }
        }
    }
}
