package id.bni46.zcstools

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

interface EncryptDataScreen : Utils {
    override var masterKey: String
    override var pinKey: String
    override var macKey: String
    override var tdkKey: String
    override var decryptKeyIndex: String
    var resultKey: String
    override var encryptData: String

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EncryptingData(nav: NavHostController) {
        val focus = LocalFocusManager.current
        Box(
            modifier = Modifier
                .padding(bottom = 30.dp)
                .border(width = 1.dp, color = Color.Yellow)
                .padding(10.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Data Encryption",
                    fontSize = 25.sp,
                    modifier = Modifier.align(alignment = Alignment.Start)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Data Encryption") },
                    maxLines = 3,
                    value = encryptData,
                    onValueChange = { encryptData = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focus.clearFocus()
                    }),
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        resultKey = setEncryptData()
                        nav.navigate("dialog")
                    }
                ) {
                    Text(text = "Encrypt Data")
                }
            }
        }
    }
}