package com.aspharier.doworkoutdaily.ui.screens.streak

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.draw.clip
import androidx.core.content.FileProvider
import com.aspharier.doworkoutdaily.data.repository.WorkoutRepository
import com.aspharier.doworkoutdaily.ui.components.HeatMapCalendar
import com.aspharier.doworkoutdaily.ui.theme.*
import coil3.compose.AsyncImage
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.CornerRadius
import java.io.File
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakScreen(
    repository: WorkoutRepository,
    viewModel: StreakViewModel = viewModel(factory = StreakViewModel.Factory(repository))
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var clickedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showHologramForPath by remember { mutableStateOf<String?>(null) }
    var hologramDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDeleteDialogForDate by remember { mutableStateOf<LocalDate?>(null) }

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

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            currentPhotoUri?.let { uri ->
                cameraLauncher.launch(uri)
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
                        val hasSelfie = uiState.selfiesData[date] != null
                        if (hasSelfie) {
                            showHologramForPath = uiState.selfiesData[date]
                            hologramDate = date
                        } else if (date == LocalDate.now()) {
                            val photoFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "selfie_${date}.jpg")
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                            currentPhotoUri = uri
                            clickedDate = date
                            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                cameraLauncher.launch(uri)
                            } else {
                                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        }
                    },
                    onDateLongClick = { date ->
                        if (uiState.selfiesData[date] != null) {
                            showDeleteDialogForDate = date
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend and hint
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tap today to take a selfie",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Less",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    HeatMapLegend()
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "More",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showHologramForPath != null && hologramDate != null) {
        Dialog(
            onDismissRequest = { showHologramForPath = null },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .aspectRatio(0.65f),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = hologramDate?.format(java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy")) ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AsyncImage(
                        model = showHologramForPath,
                        contentDescription = "Workout selfie",
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }
        }
    }

    if (showDeleteDialogForDate != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialogForDate = null },
            title = {
                Text(text = "Delete Selfie")
            },
            text = {
                Text(text = "Are you sure you want to delete this workout selfie?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialogForDate?.let { date ->
                            viewModel.deleteSelfie(date)
                        }
                        showDeleteDialogForDate = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialogForDate = null }
                ) {
                    Text("Cancel")
                }
            }
        )
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
    val themeMode = LocalThemeMode.current
    val colors = if (themeMode == ThemeMode.AMOLED_BLACK) {
        listOf(
            HeatmapAmoledEmpty,
            HeatmapAmoledLevel1,
            HeatmapAmoledLevel2,
            HeatmapAmoledLevel3,
            HeatmapAmoledLevel4
        )
    } else {
        listOf(
            HeatmapBlossomEmpty,
            HeatmapBlossomLevel1,
            HeatmapBlossomLevel2,
            HeatmapBlossomLevel3,
            HeatmapBlossomLevel4
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .padding(0.5.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = color,
                        cornerRadius = CornerRadius(3.dp.toPx())
                    )
                }
            }
        }
    }
}
