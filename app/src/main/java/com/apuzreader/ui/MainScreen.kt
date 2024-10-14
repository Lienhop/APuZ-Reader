package com.apuzreader.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apuzreader.models.ApuzData

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    pdfList: List<ApuzData>,
    onLaunchPdf: (String, String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(pdfList) { pdfData ->
            ApuzEdition(
                title = pdfData.title,
                description = pdfData.description,
                imageUrl = pdfData.imageUrl,
                onClick = { onLaunchPdf(pdfData.pdfUrl, pdfData.title) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}