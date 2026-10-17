package com.example.ui.screens.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calculator.ConversionUnit
import com.example.data.calculator.UnitCategory
import com.example.data.calculator.UnitConverter
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TealAccent

@Composable
fun UnitConverterTab(modifier: Modifier = Modifier) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val categories = UnitConverter.categories
    val currentCategory = categories[selectedCategoryIndex]

    var fromUnitIndex by remember { mutableStateOf(0) }
    var toUnitIndex by remember { mutableStateOf(if (currentCategory.units.size > 1) 1 else 0) }
    var inputValueStr by remember { mutableStateOf("1") }

    // Reset unit indices when category changes
    LaunchedEffect(selectedCategoryIndex) {
        fromUnitIndex = 0
        toUnitIndex = if (currentCategory.units.size > 1) 1 else 0
    }

    val fromUnit = currentCategory.units.getOrElse(fromUnitIndex) { currentCategory.units.first() }
    val toUnit = currentCategory.units.getOrElse(toUnitIndex) { currentCategory.units.last() }

    val inputVal = inputValueStr.toDoubleOrNull() ?: 0.0
    val convertedVal = UnitConverter.convert(inputVal, fromUnit, toUnit)
    val convertedStr = UnitConverter.format(convertedVal)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Category Selector Chips
        item {
            Text(
                text = "اختر كمية التحويل الفيزيائية أو الرياضية:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
            )
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories.size) { idx ->
                    val cat = categories[idx]
                    val isSelected = idx == selectedCategoryIndex
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryIndex = idx },
                        label = { Text("${cat.icon} ${cat.nameArabic}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Input & Output Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "القيمة المراد تحويلها",
                        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    OutlinedTextField(
                        value = inputValueStr,
                        onValueChange = { inputValueStr = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("unit_input_field"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // From Unit Dropdown
                        UnitDropdown(
                            label = "من وحدة:",
                            units = currentCategory.units,
                            selectedIndex = fromUnitIndex,
                            onSelect = { fromUnitIndex = it },
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                val temp = fromUnitIndex
                                fromUnitIndex = toUnitIndex
                                toUnitIndex = temp
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = "تبديل الوحدتين", tint = NavyPrimary)
                        }

                        // To Unit Dropdown
                        UnitDropdown(
                            label = "إلى وحدة:",
                            units = currentCategory.units,
                            selectedIndex = toUnitIndex,
                            onSelect = { toUnitIndex = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    // Converted Result Display
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDF4),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF16A34A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "النتيجة بعد التحويل:",
                                style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$convertedStr  ${toUnit.symbol}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534),
                                    fontSize = 22.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$inputValueStr ${fromUnit.symbol} = $convertedStr ${toUnit.symbol}",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF15803D))
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(
    label: String,
    units: List<ConversionUnit>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedUnit = units.getOrElse(selectedIndex) { units.first() }

    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = NavyPrimary, fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = "${selectedUnit.symbol} (${selectedUnit.nameArabic})",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(10.dp),
                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                units.forEachIndexed { idx, u ->
                    DropdownMenuItem(
                        text = { Text("${u.symbol} - ${u.nameArabic}", fontSize = 12.sp) },
                        onClick = {
                            onSelect(idx)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
