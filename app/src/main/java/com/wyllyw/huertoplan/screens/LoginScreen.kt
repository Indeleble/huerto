package com.wyllyw.huertoplan.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.wyllyw.huertoplan.navigation.AppScreens
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock

import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wyllyw.huertoplan.presentation.viewmodel.UserViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.TextFieldDefaults

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SingUpScreen (navController: NavController, viewModel: UserViewModel = hiltViewModel()) {

    //Declaramos estructura base la pantalla de login
    Scaffold(
        topBar = {
            BarraSuperior(navController, "Login",false)
        },
    ) {
        BodyContent(navController, viewModel)
    }

}

@Composable
fun BodyContent(navController: NavController, viewModel: UserViewModel) {
    
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        var credentials by remember { mutableStateOf(Credentials()) }
        var newUserName by remember { mutableStateOf("") }
        var showCreateUser by remember { mutableStateOf(false) }
        val context = LocalContext.current
        
        // Observar estados del ViewModel
        val user by viewModel.user.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()
        
        // Efecto para navegar cuando el usuario se cree exitosamente
        if (user != null && !isLoading) {
            navController.navigate(AppScreens.BancalesScreen.route) {
                popUpTo(AppScreens.SingUpScreen.route) { inclusive = true }
            }
        }
        
        // Mostrar error si existe
        error?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            if (!showCreateUser) {
                // Pantalla de Login
                LoginField(
                    value = credentials.login,
                    onChange = { data -> credentials = credentials.copy(login = data) },
                    modifier = Modifier.fillMaxWidth()
                )
                PasswordField(
                    value = credentials.pwd,
                    onChange = { data -> credentials = credentials.copy(pwd = data) },
                    submit = {
                        if (!checkCredentials(credentials, context, navController, viewModel)) 
                            credentials = Credentials()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                LabeledCheckbox(
                    label = "Remember Me",
                    onCheckChanged = {
                        credentials = credentials.copy(remember = !credentials.remember)
                    },
                    isChecked = credentials.remember
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (!checkCredentials(credentials, context, navController, viewModel)) 
                            credentials = Credentials()
                    },
                    enabled = credentials.isNotEmpty() && !isLoading,
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Login")
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Botón para cambiar a crear usuario
                OutlinedButton(
                    onClick = { showCreateUser = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("Crear Usuario")
                }
            } else {
                // Pantalla de Crear Usuario
                Text(
                    "Crear Nuevo Usuario",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                
                LoginField(
                    value = newUserName,
                    onChange = { newUserName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Nombre de Usuario",
                    placeholder = "Ingresa el nombre del nuevo usuario"
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Button(
                    onClick = {
                        if (newUserName.isNotBlank()) {
                            viewModel.createUser(newUserName)
                        } else {
                            Toast.makeText(context, "Por favor ingresa un nombre", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = newUserName.isNotBlank() && !isLoading,
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Crear Usuario")
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Botón para volver al login
                OutlinedButton(
                    onClick = { 
                        showCreateUser = false
                        newUserName = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("Volver al Login")
                }
            }
        }
    }
}

fun checkCredentials(creds: Credentials, context: Context, navController: NavController, viewModel: UserViewModel): Boolean {
    if (creds.isNotEmpty()) {

        //TODO: Implementar autenticación real - por ahora solo buscar usuario por nombre
        viewModel.getUserById(creds.login)
        
        // La navegación se maneja en BodyContent cuando user != null
        return true
    } else {
        Toast.makeText(context, "Wrong Credentials", Toast.LENGTH_SHORT).show()
        return false
    }
}

//Clase para definir los campos de login
data class Credentials(
    var login: String = "",
    var pwd: String = "",
    var remember: Boolean = false
) {
    fun isNotEmpty(): Boolean {
        return login.isNotEmpty() && pwd.isNotEmpty()
    }
}

@Composable
fun LabeledCheckbox(
    label: String,
    onCheckChanged: () -> Unit,
    isChecked: Boolean
) {
    Row(
        Modifier
            .clickable(onClick = onCheckChanged)
            .padding(4.dp)
    ) {
        Checkbox(
            checked = isChecked, 
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
        Spacer(Modifier.size(6.dp))
        Text(
            label,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun LoginField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "User",
    placeholder: String = "Enter your Login"
) {
    val focusManager = LocalFocusManager.current
    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Person,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )
    }

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun PasswordField(
    value: String,
    onChange: (String) -> Unit,
    submit: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    placeholder: String = "Enter your Password"
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Check,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )
    }
    val trailingIcon = @Composable {
        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
                if (isPasswordVisible) Icons.Default.Check else Icons.Default.Lock,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = { submit() }
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}



