package com.example.di1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.di1.viewModels.ApplicationViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(applicationViewModel: ApplicationViewModel) {
    var text by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Говорим, что будет колонка
    Column(
        modifier = Modifier
            // говорим, что она будет занимать всё доступное место
            .fillMaxSize()
            // говорим какой цвет задника будет
            .background(MaterialTheme.colorScheme.background),
        // выравниваем контент по центру по вертикали
        verticalArrangement = Arrangement.Center,
        // выравниваем контент по центру по горизонтали
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // объявляем текстовый блок
        Text(
            // текст будет "Login"
            text = "Login",
            // цвет текста
            color = MaterialTheme.colorScheme.onBackground,
            // вес текста (хз как это называется правильно)
            fontWeight = FontWeight.Bold,
            // размер текста
            fontSize = 24.sp,
            // отступы от текста 25 dp
            modifier = Modifier.padding(25.dp)
        )
        // текстовое поле
        OutlinedTextField(
            // значение берется из переменной text
            value = text,
            // когда что-то пишем, то значение помещается в переменную
            onValueChange = { text = it },
            // текстик над полем воода
            label = { Text("Your token") },
            // ввод в одну строчку
            singleLine = true,
            // тут мы взаимодействуем с клавиатурой и вместо кнопки "Enter" вставятем кнопку "Готово"
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            // если жмем на "Готово" на клавиатуре, то она прячется и поле ввода становится неактивным
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            )
        )
        // объявляем блок с отступом сверху 12 dp
        Box(
            modifier = Modifier.padding(top = 12.dp)
        ) {
            // вставляем кнопку в блок
            Button(
                onClick = {
                    // по клику снимаем фокус с поля ввода(убираем курсор из него)
                    focusManager.clearFocus()
                    // по клику вызываем функцию login из viewModel
                    applicationViewModel.login()
                }
            ) {
                // вставляем строку (элементы внутри неё распологаются горизонтально) Это антоним к Column
                Row {
                    // в строку вставляем текстик
                    Text(
                        text = "Continue",
                        color = MaterialTheme.colorScheme.onPrimary
                        )
                }
            }
        }
    }
}