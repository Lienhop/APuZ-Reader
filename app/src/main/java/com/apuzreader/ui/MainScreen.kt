package com.apuzreader.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apuzreader.models.ApuzData
import java.time.LocalDate
enum class SortOrder { NEWEST, OLDEST }

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    pdfList: List<ApuzData>,
    onLaunchPdf: (String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf(SortOrder.NEWEST) }

    // Add this for debugging
    LaunchedEffect(Unit) {
        Log.d("MainScreen", "Composable recomposed. PDF list size: ${pdfList.size}")
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    Log.d("MainScreen", "Search query changed to: $it")
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Suchen") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
//            Spacer(modifier = Modifier.width(8.dp))
//            IconButton(
//                onClick = {
//                    sortOrder = when (sortOrder) {
//                        SortOrder.NEWEST -> SortOrder.OLDEST
//                        SortOrder.OLDEST -> SortOrder.NEWEST
//                    }
//                    Log.d("MainScreen", "Sort order changed to: $sortOrder")
//                }
//            ) {
//                Icon(
//                    imageVector = when (sortOrder) {
//                        SortOrder.NEWEST -> Icons.Default.KeyboardArrowDown
//                        SortOrder.OLDEST -> Icons.Default.KeyboardArrowUp
//                    },
//                    contentDescription = when (sortOrder) {
//                        SortOrder.NEWEST -> "Sort by newest"
//                        SortOrder.OLDEST -> "Sort by oldest"
//                    }
//                )
//            }
        }

        val filteredAndSortedList by remember(pdfList, searchQuery) {
            derivedStateOf {
                Log.d("MainScreen", "Recalculating filtered and sorted list")
                pdfList
                    .filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                                it.description.contains(searchQuery, ignoreCase = true)
                    }
                    .sortedWith(when (sortOrder) {
                        SortOrder.NEWEST -> compareByDescending { it.date ?: LocalDate.MIN }
                        SortOrder.OLDEST -> compareBy { it.date ?: LocalDate.MAX }
                    })
                    .also { Log.d("MainScreen", "Filtered list size: ${it.size}") }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(filteredAndSortedList) { pdfData ->
                ApuzEdition(
                    title = pdfData.title,
                    description = pdfData.description,
                    imageUrl = pdfData.imageUrl,
                    date = pdfData.date,
                    onClick = { onLaunchPdf(pdfData.pdfUrl, pdfData.title) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}