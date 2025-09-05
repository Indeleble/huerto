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
import com.wyllyw.huertoplan.model.Bancal

data class EditBancalFormData(
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
fun EditBancalDialog(
    showDialog: Boolean,
    bancal: Bancal?,
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (name: String, width: Float, height: Float) -> Unit,
    onDelete: () -> Unit
) {
    if (showDialog && bancal != null) {
        var formData by remember(bancal) { 
            mutableStateOf(
                EditBancalFormData(
                    name = bancal.name,
                    width = bancal.width.toString(),
                    height = bancal.height.toString()
                )
            ) 
        }
        var showDeleteConfirmation by remember { mutableStateOf(false) }
        
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
                        text = "Editar Bancal",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Subtítulo con posición
                    Text(
                        text = "Posición: (${bancal.x.toInt()}, ${bancal.y.toInt()})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    
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
                    
                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón Eliminar
                        OutlinedButton(
                            onClick = { showDeleteConfirmation = true },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Eliminar")
                        }
                        
                        // Botón Cancelar
                        TextButton(
                            onClick = { if (!isLoading) onDismiss() },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Text("Cancelar")
                        }
                        
                        // Botón Guardar
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
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
        
        // Dialog de confirmación para eliminar
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Estás seguro de que quieres eliminar el bancal '${bancal.name}'? Esta acción no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteConfirmation = false
                            onDelete()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteConfirmation = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}