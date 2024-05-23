package com.example.di1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.di1.viewModels.ApplicationViewModel

@Composable
fun MainScreen(
    applicationViewModel: ApplicationViewModel = viewModel()
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        when (applicationViewModel.uiState.collectAsState().value) {
            ApplicationViewModel.ApplicationState.CheckAuth -> CheckAuth(applicationViewModel = applicationViewModel)
            ApplicationViewModel.ApplicationState.LoginForm -> LoginScreen(applicationViewModel = applicationViewModel)
            ApplicationViewModel.ApplicationState.Profile -> TODO()
        }
    }
}