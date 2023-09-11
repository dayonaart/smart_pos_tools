package id.bni46.smartpostest

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

interface Composeable : Utils {
    override var masterKey: String
    override var pinKey: String
    override var macKey: String
    override var tdkKey: String
    var resultKey: String
    override var encryptData: String
    val keyTitleList: List<String>
    val context: Context

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowDialog(nav: NavHostController) {

        AlertDialog(
            onDismissRequest = {
                nav.popBackStack("dialog", inclusive = true)
            },
            confirmButton = { },
            title = {
                Text(text = "Result")
            },
            text = {
                OutlinedTextField(
                    value = resultKey,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Text(text = "Copy", modifier = Modifier
                            .clickable {
                                val clipboardManager =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val myClip: ClipData = ClipData.newPlainText("note_copy", resultKey)
                                clipboardManager.setPrimaryClip(myClip)
                                Toast
                                    .makeText(context, "copied", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .padding(end = 10.dp))
                    })
            }
        )
    }

    @Composable
    fun MainView(nav: NavHostController) {
        LazyColumn(horizontalAlignment = Alignment.End) {
            item {
                InjectMasterKey(nav = nav)
            }
            items(keyTitleList.size) {
                InjectPinKey(nav = nav, it)
            }
            item {
                EncryptData(nav = nav)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InjectMasterKey(nav: NavHostController) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Master Key") },
            maxLines = 3,
            value = masterKey,
            onValueChange = { masterKey = it }
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = {
                resultKey = setMainKey()
                nav.navigate("dialog")
            }
        ) {
            Text(text = "Inject Master Key")
        }
        Spacer(modifier = Modifier.height(20.dp))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InjectPinKey(nav: NavHostController, it: Int) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = keyTitleList[it]) },
            maxLines = 3,
            value = listOf(pinKey, macKey, tdkKey)[it],
            onValueChange = { v ->
                when (it) {
                    0 -> {
                        pinKey = v
                    }

                    1 -> {
                        macKey = v
                    }

                    2 -> {
                        tdkKey = v
                    }
                }
            }
        )
        if ((it + 1) == keyTitleList.size) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        resultKey = setPinPadUpWorkKey()
                        nav.navigate("dialog")
                    }
                ) {
                    Text(text = "Setup work key")
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EncryptData(nav: NavHostController) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Message Encryption") },
            maxLines = 3,
            value = encryptData,
            onValueChange = { encryptData = it }
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
        Spacer(modifier = Modifier.height(20.dp))
    }

}