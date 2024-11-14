package com.wyllyw.huertoplan.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wyllyw.huertoplan.model.User
import com.wyllyw.huertoplan.viewmodel.UserViewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SectorScreen(navController: NavController, viewModel: UserViewModel) {

    Scaffold(
        topBar = {
            BarraSuperior(navController, false)
        },
    ) {
        SecondBodyContent(navController = navController, viewModel)
    }

}

@Composable
fun SecondBodyContent(navController: NavController, viewModel: UserViewModel) {

    val user: User by viewModel.user.collectAsStateWithLifecycle()

    Column {
        Spacer(modifier = Modifier.size(80.dp))
        Text("Usuario: ${user.name}")
        Button(onClick = { viewModel.changeName("Felipon") }) {
            Text("Boton 2")
        }
    }
}