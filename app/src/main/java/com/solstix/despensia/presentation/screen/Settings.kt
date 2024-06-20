package com.solstix.despensia.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    chefLevel: String = "Básico",
    setChefLevel: (String) -> Unit,
    utensils: List<String> = emptyList(),
    setUtensils: (List<String>) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val difficultyList = arrayOf("Básico", "Intermedio", "Avanzado")
    var selectedLevel by remember { mutableStateOf(chefLevel) }
    val isHorno = remember { mutableStateOf(false) }
    val isSarten = remember { mutableStateOf(false) }
    val isMicroondas = remember { mutableStateOf(false) }
    val isOllaExpress = remember { mutableStateOf(false) }
    val isBatidora = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        utensils.forEach {
            if (it.equals("horno")) {
                isHorno.value = true
            } else if (it.equals("sarten")) {
                isSarten.value = true;
            } else if (it.equals("microondas")) {
                isMicroondas.value = true;
            } else if (it.equals("olla express")) {
                isOllaExpress.value = true;
            } else if (it.equals("batidora")) {
                isBatidora.value = true;
            }
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Nivel de cocina",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    value = selectedLevel,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    difficultyList.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                selectedLevel = item
                                expanded = false
                            }
                        )
                    }
                }
            }


        }
        Text(
            text = "Utensilios",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Horno", fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Checkbox(
                checked = isHorno.value,
                onCheckedChange = { isHorno.value = it },
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Sartén", fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Checkbox(
                checked = isSarten.value,
                onCheckedChange = { isSarten.value = it },
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Batidora", fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Checkbox(
                checked = isBatidora.value,
                onCheckedChange = { isBatidora.value = it },
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Olla express", fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Checkbox(
                checked = isOllaExpress.value,
                onCheckedChange = { isOllaExpress.value = it },
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Microondas", fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Checkbox(
                checked = isMicroondas.value,
                onCheckedChange = { isMicroondas.value = it },
            )
        }

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 32.dp),
            onClick = {
                val temporal: MutableList<String> = mutableListOf()

                if (isHorno.value) {
                    temporal.add("horno")
                }
                if (isSarten.value) {
                    temporal.add("sarten")
                }
                if (isMicroondas.value) {
                    temporal.add("microondas")
                }
                if (isOllaExpress.value) {
                    temporal.add("olla express")
                }
                if (isBatidora.value) {
                    temporal.add("batidora")
                }
                setChefLevel(selectedLevel)
                setUtensils(temporal)
            }) {
            Text(text = "Guardar")
        }
    }
}