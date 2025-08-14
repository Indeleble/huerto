package com.wyllyw.huertoplan.screens.popups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

data class BancalFormData(
    val name: String = "",
    val width: String = "",
    val height: String = ""
) {
    fun isValid(): Boolean {
        return name.isNotBlank() && 
               width.isNotBlank() && 
               height.isNotBlank() &&
               width.toFloatOrNull()?.let { it > 0 } ?: false &&
               height.toFloatOrNull()?.let { it > 0 } ?: false
    }
    
    fun getWidthFloat(): Float = width.toFloatOrNull() ?: 0f
    fun getHeightFloat(): Float = height.toFloatOrNull() ?: 0f
}

@Composable
fun CreateBancalDialog(
    showDialog: Boolean,
    sectorName: String,
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (name: String, width: Float, height: Float) -> Unit
) {
    if (showDialog) {
        var formData by remember { mutableStateOf(BancalFormData()) }
        
        Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Título
                    Text(
                        text = "Crear Nuevo Bancal",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Subtítulo con sector
                    Text(
                        text = "Sector: $sectorName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Divider(color = MaterialTheme.colorScheme.outline)
                    
                    // Campo Nombre
                    OutlinedTextField(
                        value = formData.name,
                        onValueChange = { formData = formData.copy(name = it) },
                        label = { Text("Nombre del Bancal") },
                        placeholder = { Text("Ej: Bancal de Tomates") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    // Fila para dimensiones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Campo Ancho
                        OutlinedTextField(
                            value = formData.width,
                            onValueChange = { newValue ->
                                // Solo permitir números y punto decimal
                                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    formData = formData.copy(width = newValue)
                                }
                            },
                            label = { Text("Ancho (m)") },
                            placeholder = { Text("2.0") },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        
                        // Campo Alto
                        OutlinedTextField(
                            value = formData.height,
                            onValueChange = { newValue ->
                                // Solo permitir números y punto decimal
                                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    formData = formData.copy(height = newValue)
                                }
                            },
                            label = { Text("Largo (m)") },
                            placeholder = { Text("5.0") },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    
                    // Información adicional
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "💡 Consejos:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "• El bancal aparecerá en el centro de la pantalla\n• Puedes moverlo arrastrando después de crearlo\n• Las dimensiones se muestran en metros",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    
                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón Cancelar
                        TextButton(
                            onClick = { if (!isLoading) onDismiss() },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Text("Cancelar")
                        }
                        
                        // Botón Crear
                        Button(
                            onClick = {
                                if (formData.isValid()) {
                                    onConfirm(
                                        formData.name.trim(),
                                        formData.getWidthFloat(),
                                        formData.getHeightFloat()
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = formData.isValid() && !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Crear Bancal")
                            }
                        }
                    }
                }
            }
        }
    }
}