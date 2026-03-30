package com.kyilmaz.neurocomet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.Month
import java.time.Period
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * Reusable dialog for age verification using a **date-of-birth** picker.
 *
 * Uses three neutral drop-down fields (Month / Day / Year) instead of a direct
 * age input.  This is the COPPA / KOSA recommended approach because:
 *  - It avoids hinting which age threshold unlocks access.
 *  - It is harder for a child to game than typing "18".
 *  - The collected DOB is discarded after computing [Audience]; only the
 *    audience tier is persisted.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeVerificationDialog(
    onDismiss: () -> Unit,
    onConfirm: (Audience) -> Unit,
    onSkip: (() -> Unit)? = null,
    title: String = stringResource(R.string.age_verify_title),
    subtitle: String = stringResource(R.string.age_verify_subtitle),
    @Suppress("UNUSED_PARAMETER") initialAge: String = ""    // kept for source compat; ignored
) {
    val today = remember { LocalDate.now() }
    val locale = remember { Locale.getDefault() }
    val months = remember(locale) {
        Month.entries.map { it.getDisplayName(TextStyle.FULL, locale) }
    }
    val monthAbbreviations = remember(locale) {
        Month.entries.map { it.getDisplayName(TextStyle.SHORT, locale) }
    }
    val years = remember { (today.year downTo today.year - 120).toList() }

    var selectedMonth by remember { mutableIntStateOf(-1) }      // 1-based (Jan=1)
    var selectedDay by remember { mutableIntStateOf(-1) }
    var selectedYear by remember { mutableIntStateOf(-1) }
    var error by remember { mutableStateOf<String?>(null) }

    // Compute valid day range whenever month/year change
    val maxDay = remember(selectedMonth, selectedYear) {
        if (selectedMonth in 1..12 && selectedYear > 0) {
            YearMonth.of(selectedYear, selectedMonth).lengthOfMonth()
        } else 31
    }
    val days = remember(maxDay) { (1..maxDay).toList() }

    // Clamp day if month/year change shortened the range
    if (selectedDay > maxDay) selectedDay = -1

    val labelMonth = stringResource(R.string.age_verify_month)
    val labelDay = stringResource(R.string.age_verify_day)
    val labelYear = stringResource(R.string.age_verify_year)
    val errorFillAll = stringResource(R.string.age_verify_error_fill_all)
    val errorInvalidDob = stringResource(R.string.age_verify_error_invalid_dob)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(subtitle)

                // --- Month (full width) ---
                DobDropdown(
                    label = labelMonth,
                    options = months,
                    selectedDisplay = if (selectedMonth > 0) monthAbbreviations[selectedMonth - 1] else "",
                    onSelect = { idx -> selectedMonth = idx + 1; error = null },
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Day + Year side by side ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DobDropdown(
                        label = labelDay,
                        options = days.map { it.toString() },
                        selectedDisplay = if (selectedDay > 0) selectedDay.toString() else "",
                        onSelect = { idx -> selectedDay = days[idx]; error = null },
                        modifier = Modifier.weight(1f)
                    )

                    DobDropdown(
                        label = labelYear,
                        options = years.map { it.toString() },
                        selectedDisplay = if (selectedYear > 0) selectedYear.toString() else "",
                        onSelect = { idx -> selectedYear = years[idx]; error = null },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (error != null) {
                    Text(
                        text = error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (selectedMonth < 1 || selectedDay < 1 || selectedYear < 1) {
                    error = errorFillAll
                    return@Button
                }
                val dob = runCatching { LocalDate.of(selectedYear, selectedMonth, selectedDay) }.getOrNull()
                if (dob == null || dob.isAfter(today)) {
                    error = errorInvalidDob
                    return@Button
                }
                val age = Period.between(dob, today).years
                val audience = audienceForAge(age)
                onConfirm(audience)
                onDismiss()
            }) {
                Text(stringResource(R.string.age_verify_confirm))
            }
        },
        dismissButton = {
            Column {
                if (onSkip != null) {
                    TextButton(onClick = {
                        onSkip.invoke()
                        onDismiss()
                    }) { Text(stringResource(R.string.age_verify_skip)) }
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        }
    )
}

/**
 * Small dropdown helper for a single DOB field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DobDropdown(
    label: String,
    options: List<String>,
    selectedDisplay: String,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedDisplay,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, maxLines = 1) },
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun audienceForAge(age: Int): Audience = when {
    age < 13 -> Audience.UNDER_13
    age < 18 -> Audience.TEEN
    else -> Audience.ADULT
}
