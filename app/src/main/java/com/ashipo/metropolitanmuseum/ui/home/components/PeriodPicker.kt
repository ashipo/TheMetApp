package com.ashipo.metropolitanmuseum.ui.home.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ashipo.metropolitanmuseum.R
import java.util.EnumMap
import kotlin.math.absoluteValue

enum class Epoch(@StringRes val caption: Int) {
    BC(R.string.bc_epoch_suffix),
    AD(R.string.ad_epoch_suffix),
}

private fun Int.getEpoch() =
    if (this < 0) Epoch.BC else Epoch.AD

private val epochSuffix = EnumMap<Epoch, String>(Epoch::class.java)

@Composable
fun PeriodPicker(
    fromYear: Int,
    toYear: Int,
    checked: Boolean,
    onPeriodChange: (IntRange) -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPeriodSelectDialog by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = modifier
            .height(72.dp)
    ) {
        if (epochSuffix.isEmpty()) {
            for (epoch in Epoch.entries) {
                epochSuffix[epoch] = stringResource(epoch.caption)
            }
        }
        val periodTemplate = stringResource(R.string.period_template)
        val periodText = remember(fromYear, toYear) {
            periodTemplate.format(
                fromYear.absoluteValue,
                epochSuffix[fromYear.getEpoch()],
                toYear.absoluteValue,
                epochSuffix[toYear.getEpoch()],
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .clickable(
                    onClickLabel = stringResource(R.string.select_period),
                    role = Role.Button,
                ) {
                    showPeriodSelectDialog = true
                }
                .weight(1f)
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                .height(56.dp)
                .testTag("periodPicker:text")
        ) {
            Text(
                text = stringResource(R.string.search_date),
                style = MaterialTheme.typography.bodyLarge,
            )
            val colorScheme = MaterialTheme.colorScheme
            val periodTextColor = remember(checked) {
                if (checked) {
                    colorScheme.onSurfaceVariant
                } else {
                    colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                }
            }
            Text(
                text = periodText,
                style = MaterialTheme.typography.bodyMedium,
                color = periodTextColor,
            )
        }
        VerticalDivider()
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .toggleable(
                    value = checked,
                    onValueChange = onCheckedChange,
                    role = Role.Checkbox,
                )
                .fillMaxHeight()
                .padding(horizontal = 16.dp)
                .testTag("periodPicker:checkbox")
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = null,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
            )
        }
    }
    if (showPeriodSelectDialog) {
        RangeSelectDialog(
            fromYear,
            toYear,
            onDismissRequest = { showPeriodSelectDialog = false },
            onRangeChange = {
                showPeriodSelectDialog = false
                onPeriodChange(it)
            },
        )
    }
}

private const val MAX_YEAR = 100_000

private fun String.toValidYearOrNull(): Int? =
    toIntOrNull().takeIf { it in 1..<MAX_YEAR }

private fun toSignedYearsOrNull(
    fromYear: String,
    fromEpoch: Epoch,
    toYear: String,
    toEpoch: Epoch,
): IntRange? {
    var fromYearNum = fromYear.toValidYearOrNull()
    var toYearNum = toYear.toValidYearOrNull()
    if (fromYearNum == null || toYearNum == null) {
        return null
    }
    if (fromEpoch == Epoch.BC) {
        fromYearNum = -fromYearNum
    }
    if (toEpoch == Epoch.BC) {
        toYearNum = -toYearNum
    }
    return if (fromYearNum <= toYearNum) {
        fromYearNum..toYearNum
    } else {
        null
    }
}

@Composable
private fun RangeSelectDialog(
    initFromYear: Int,
    initToYear: Int,
    onDismissRequest: () -> Unit,
    onRangeChange: (IntRange) -> Unit,
) {
    var fromYear by rememberSaveable { mutableStateOf(initFromYear.absoluteValue.toString()) }
    var fromEpoch by rememberSaveable { mutableStateOf(initFromYear.getEpoch()) }
    var toYear by rememberSaveable { mutableStateOf(initToYear.absoluteValue.toString()) }
    var toEpoch by rememberSaveable { mutableStateOf(initToYear.getEpoch()) }

    val period: IntRange? = remember(fromYear, fromEpoch, toYear, toEpoch) {
        toSignedYearsOrNull(fromYear, fromEpoch, toYear, toEpoch)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = { period?.let(onRangeChange) },
                enabled = period != null,
                modifier = Modifier.testTag("confirm")
            ) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag("cancel")
            ) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                YearEdit(
                    initialYear = fromYear,
                    epoch = fromEpoch,
                    onYearChange = { fromYear = it },
                    onEpochChange = { fromEpoch = it },
                    label = stringResource(R.string.from_year),
                    modifier = Modifier.testTag("fromYear"),
                )
                YearEdit(
                    initialYear = toYear,
                    epoch = toEpoch,
                    onYearChange = { toYear = it },
                    onEpochChange = { toEpoch = it },
                    label = stringResource(R.string.to_year),
                    modifier = Modifier.testTag("toYear"),
                )
                if (period == null) {
                    Text(
                        text = stringResource(R.string.invalid_period),
                        color = MaterialTheme.colorScheme.error,
                    )
                } else {
                    Text(
                        stringResource(R.string.period_template)
                            .format(fromYear, epochSuffix[fromEpoch], toYear, epochSuffix[toEpoch])
                    )
                }
            }
        },
    )
}

/**
 * Returns `true` if empty or an integer from 0 to [MAX_YEAR]
 */
private fun String.isValidIntermediateValue(): Boolean =
    isEmpty() || toIntOrNull()?.let { it in 0..<MAX_YEAR } == true

@Composable
private fun YearEdit(
    initialYear: String,
    epoch: Epoch,
    onYearChange: (String) -> Unit,
    onEpochChange: (Epoch) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var yearValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialYear))
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        TextField(
            value = yearValue,
            onValueChange = { newValue ->
                if (newValue.text == yearValue.text) {
                    yearValue = newValue
                } else if (newValue.text.isValidIntermediateValue()) {
                    yearValue = newValue
                    onYearChange(newValue.text)
                }
            },
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f)
                .testTag("year")
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.weight(1f, false)
        ) {
            Epoch.entries.forEachIndexed { index, itemEpoch ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index, Epoch.entries.size),
                    onClick = { onEpochChange(itemEpoch) },
                    selected = itemEpoch == epoch,
                    modifier = Modifier.testTag(itemEpoch.name)
                ) {
                    Text(stringResource(itemEpoch.caption))
                }
            }
        }
    }
}
