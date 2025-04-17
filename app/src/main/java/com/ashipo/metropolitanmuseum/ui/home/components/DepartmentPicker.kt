package com.ashipo.metropolitanmuseum.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ashipo.metropolitanmuseum.Department
import com.ashipo.metropolitanmuseum.R

/**
 * A department selecting component. Has "None" option.
 *
 * @param selectedDepartmentId must be negative if no department is selected
 * @param onSelectDepartment called when a department is selected. Called with `-1` when "None"
 * option is selected.
 * @param departments list of departments
 * @param modifier the [Modifier] to be applied
 */
@Composable
fun DepartmentPicker(
    selectedDepartmentId: Int,
    onSelectDepartment: (Int) -> Unit,
    departments: List<Department>,
    modifier: Modifier = Modifier,
) {
    var showDepartmentSelectDialog by rememberSaveable { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                onClick = { showDepartmentSelectDialog = true }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(56.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.department),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val departmentNone = stringResource(R.string.department_none)
            val departmentName: String = rememberSaveable(selectedDepartmentId, departments) {
                if (selectedDepartmentId < 0) {
                    departmentNone
                } else {
                    departments.find { it.id == selectedDepartmentId }?.name ?: ""
                }
            }
            Text(
                text = departmentName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

    if (showDepartmentSelectDialog) {
        val dismiss = { showDepartmentSelectDialog = false }
        var currentDepartmentId by rememberSaveable { mutableIntStateOf(selectedDepartmentId) }

        AlertDialog(
            onDismissRequest = dismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        dismiss()
                        onSelectDepartment(currentDepartmentId)
                    },
                    modifier = Modifier.testTag("confirm"),
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = dismiss,
                    modifier = Modifier.testTag("cancel"),
                ) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
            text = {
                val firstIndex = remember(selectedDepartmentId) {
                    if (selectedDepartmentId < 0) {
                        return@remember 0
                    }
                    departments.indexOfFirst { it.id == selectedDepartmentId } + 1
                }

                LazyColumn(
                    state = rememberLazyListState(firstIndex),
                ) {
                    item(-1) {
                        DepartmentItem(
                            departmentName = stringResource(R.string.department_none),
                            checked = currentDepartmentId < 0,
                            onCheckedChange = { currentDepartmentId = -1 },
                            modifier = Modifier.testTag("department:none")
                        )
                    }
                    items(
                        items = departments,
                        key = { it.id },
                    ) { department ->
                        DepartmentItem(
                            departmentName = department.name,
                            checked = department.id == currentDepartmentId,
                            onCheckedChange = { currentDepartmentId = department.id },
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun DepartmentItem(
    departmentName: String,
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
            .heightIn(min = 56.dp)
            .toggleable(
                value = checked,
                onValueChange = { onCheckedChange() },
                role = Role.RadioButton
            )

    ) {
        RadioButton(
            selected = checked,
            onClick = onCheckedChange,
        )
        Text(departmentName)
    }
}
