package com.ashipo.metropolitanmuseum.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ashipo.metropolitanmuseum.R

/**
 * @param maxTags maximum number of tags, 0 for no limit
 * @param maxTagLength maximum length for a single tag
 */
@Composable
fun TagsPicker(
    title: String,
    tags: List<String>,
    onTagsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    maxTags: Int = 0,
    maxTagLength: Int = 50,
) {
    var showSelectDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable(
                onClickLabel = stringResource(R.string.show_select_dialog),
                role = Role.Button,
            ) {
                showSelectDialog = true
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .heightIn(56.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = tags.joinToString().ifBlank { stringResource(R.string.none) },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
    if (showSelectDialog) {
        TagSelectDialog(
            initialTags = tags,
            maxTags = maxTags,
            maxTagLength = maxTagLength,
            description = description,
            onDismissRequest = { showSelectDialog = false },
            onConfirmRequest = {
                showSelectDialog = false
                onTagsChange(it)
            },
        )
    }
}

@Composable
private fun <T : Any> rememberMutableStateListOf(elements: List<T>): SnapshotStateList<T> {
    return rememberSaveable(saver = snapshotStateListSaver()) {
        elements.toMutableStateList()
    }
}

private fun <T : Any> snapshotStateListSaver() = listSaver<SnapshotStateList<T>, T>(
    save = { stateList -> stateList.toList() },
    restore = { it.toMutableStateList() },
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagSelectDialog(
    initialTags: List<String>,
    maxTags: Int,
    maxTagLength: Int,
    description: String?,
    onDismissRequest: () -> Unit,
    onConfirmRequest: (List<String>) -> Unit,
) {
    val tags = rememberMutableStateListOf(initialTags)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = { onConfirmRequest(tags) },
                modifier = Modifier.testTag("confirm"),
            ) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag("cancel"),
            ) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        text = {
            Column {
                var tagText by rememberSaveable { mutableStateOf("") }
                val canAdd = maxTags == 0 || tags.size < maxTags
                val addTag = addTag@{
                    if (!canAdd) {
                        return@addTag
                    }
                    val trimmed = tagText.trim()
                    if (trimmed.isNotBlank()) {
                        tags.add(trimmed)
                    }
                    tagText = ""
                }
                TextField(
                    value = tagText,
                    onValueChange = {
                        tagText = it.take(maxTagLength)
                    },
                    label = {
                        val label = if (canAdd) {
                            stringResource(R.string.add)
                        } else {
                            stringResource(R.string.cant_add)
                        }
                        Text(label)
                    },
                    supportingText = if (maxTags == 0) null else {
                        { Text("${tags.size} / $maxTags") }
                    },
                    trailingIcon = if (tagText.isEmpty()) {
                        null
                    } else {
                        {
                            IconButton({
                                tagText = ""
                            }) {
                                Icon(Icons.Default.Clear, stringResource(R.string.clear))
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { addTag() }
                    ),
                )
                FlowRow(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    for (i in tags.indices) {
                        Tag(
                            tags[i],
                            { tags.removeAt(i) },
                            Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
                description?.let { Text(it) }
            }
        },
    )
}

@Composable
private fun Tag(
    text: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    InputChip(
        label = { Text(text) },
        selected = false,
        onClick = onRemove,
        trailingIcon = {
            Icon(
                Icons.Default.Clear,
                stringResource(R.string.remove),
                Modifier.size(InputChipDefaults.IconSize),
            )
        },
        modifier = modifier,
    )
}
