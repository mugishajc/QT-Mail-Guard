package rw.delasoft.qtmailguard.presentation.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import rw.delasoft.qtmailguard.domain.model.Email
import rw.delasoft.qtmailguard.presentation.component.EmailCard
import rw.delasoft.qtmailguard.presentation.component.EmailHistoryItem
import rw.delasoft.qtmailguard.presentation.component.EmptyState
import rw.delasoft.qtmailguard.presentation.component.ShimmerEmailCard
import rw.delasoft.qtmailguard.presentation.component.ShimmerVerificationBadge
import rw.delasoft.qtmailguard.presentation.state.EmailEvent
import rw.delasoft.qtmailguard.presentation.viewmodel.EmailViewModel

@Composable
fun EmailScreen(
    viewModel: EmailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showHistorySheet by remember { mutableStateOf(false) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.processSelectedFile(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is EmailEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            EmailTopBar(
                hasEmail = uiState.email != null,
                hasHistory = uiState.emails.isNotEmpty(),
                onClear = { viewModel.clearCurrentEmail() },
                onShowHistory = { showHistorySheet = true }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = Triple(
                    uiState.isParsing || uiState.isVerifying,
                    uiState.email,
                    uiState.errorMessage
                ),
                transitionSpec = {
                    fadeIn(tween(300)) + slideInVertically { it / 4 } togetherWith
                            fadeOut(tween(200)) + slideOutVertically { -it / 4 }
                },
                label = "content_animation"
            ) { (isProcessing, email, error) ->
                when {
                    isProcessing -> {
                        LoadingContent(
                            isParsing = uiState.isParsing,
                            isVerifying = uiState.isVerifying
                        )
                    }
                    email != null -> {
                        EmailContent(
                            email = email,
                            verificationResult = uiState.verificationResult,
                            onLoadAnother = {
                                filePicker.launch(arrayOf("application/octet-stream", "*/*"))
                            }
                        )
                    }
                    else -> {
                        EmptyContent(
                            onSelectFile = {
                                filePicker.launch(arrayOf("application/octet-stream", "*/*"))
                            }
                        )
                    }
                }
            }
        }

        if (showHistorySheet) {
            HistoryBottomSheet(
                emails = uiState.emails,
                onDismiss = { showHistorySheet = false },
                onSelect = { email ->
                    viewModel.selectEmailFromHistory(email)
                    showHistorySheet = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmailTopBar(
    hasEmail: Boolean,
    hasHistory: Boolean,
    onClear: () -> Unit,
    onShowHistory: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "QTMail Guard",
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            AnimatedVisibility(visible = hasEmail) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear"
                    )
                }
            }
        },
        actions = {
            AnimatedVisibility(visible = hasHistory) {
                IconButton(onClick = onShowHistory) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun LoadingContent(
    isParsing: Boolean,
    isVerifying: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.height(24.dp).width(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = when {
                    isParsing -> "Parsing email file..."
                    isVerifying -> "Verifying integrity..."
                    else -> "Processing..."
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isVerifying) {
            ShimmerVerificationBadge()
            Spacer(modifier = Modifier.height(16.dp))
        }

        ShimmerEmailCard()
    }
}

@Composable
private fun EmailContent(
    email: Email,
    verificationResult: rw.delasoft.qtmailguard.domain.model.VerificationResult?,
    onLoadAnother: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            EmailCard(
                email = email,
                verificationResult = verificationResult
            )
        }

        item {
            FilledTonalButton(
                onClick = onLoadAnother,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.FileOpen,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Load Another File")
            }
        }
    }
}

@Composable
private fun EmptyContent(onSelectFile: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyState(
            title = "No Email Loaded",
            description = "Select a .pb file to view and verify an email"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onSelectFile) {
            Icon(
                imageVector = Icons.Default.FileOpen,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Select Email File")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryBottomSheet(
    emails: List<Email>,
    onDismiss: () -> Unit,
    onSelect: (Email) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            Text(
                text = "Previously Loaded Emails",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn {
                items(emails, key = { it.id }) { email ->
                    EmailHistoryItem(
                        email = email,
                        onClick = { onSelect(email) }
                    )
                }
            }
        }
    }
}
