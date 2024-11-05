package com.apuzreader.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apuzreader.models.ApuzData
import kotlinx.coroutines.launch
import java.time.LocalDate
enum class SortOrder { NEWEST, OLDEST }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    pdfList: List<ApuzData>,
    onLaunchPdf: (String, String) -> Unit,
    onToggleFavorite: (ApuzData) -> Unit,
    drawerState: DrawerState
) {
    var showFavorites by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val sortOrder by remember { mutableStateOf(SortOrder.NEWEST) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onShowAllClick = {
                    showFavorites = false
                    scope.launch {
                        drawerState.close()
                    }
                },
                onShowFavoritesClick = {
                    showFavorites = true
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        },
        content = {
            Column(modifier = modifier.fillMaxSize()) {
                // Top App Bar
                TopAppBar(
                    title = { Text(if (showFavorites) "Favoriten" else "Übersicht") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch {
                            drawerState.open()
                        } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                    placeholder = { Text("Suchen") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )

                val displayList = pdfList
                    .sortedWith(when (sortOrder) {
                        SortOrder.NEWEST -> compareByDescending { it.date ?: LocalDate.MIN }
                        SortOrder.OLDEST -> compareBy { it.date ?: LocalDate.MAX }
                    })
                    .filter { if (showFavorites) it.isFavorite else true }
                    .filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                                it.description.contains(searchQuery, ignoreCase = true)
                    }

                if (showFavorites && displayList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Fügen Sie Favoriten hinzu, um sie hier zu sehen.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                else{
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(displayList) { pdfData ->
                            ApuzEdition(
                                title = pdfData.title,
                                description = pdfData.description,
                                imageUrl = pdfData.imageUrl,
                                date = pdfData.date,
                                onReadPdf = { onLaunchPdf(pdfData.pdfUrl, pdfData.title) },
                                onToggleFavorite = { onToggleFavorite(pdfData) },
                                isFavorite = pdfData.isFavorite,
                                showFavorites = showFavorites
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun DrawerMenuItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        fontSize = 22.sp,
    )
}

@Composable
fun DrawerContent(
    onShowAllClick: () -> Unit,
    onShowFavoritesClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.45f)  // Fill only a third of the screen width
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))  // Opaque background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "APuZ Reader",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            // Divider
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            DrawerMenuItem("Übersicht", onShowAllClick)
            DrawerMenuItem("Favoriten", onShowFavoritesClick)
        }
    }
}