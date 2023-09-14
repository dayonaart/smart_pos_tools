package id.bni46.zcstools

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

interface InjectMasterKeyScreen : Utils, ExampleKeyIndexScreen {
    override var masterKey: String
    override var pinKey: String
    override var macKey: String
    override var tdkKey: String
    override var masterKeyIndex: String
    override var encryptData: String
    var resultKey: String

    @OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
    @Composable
    fun InjectMasterKey(nav: NavHostController) {
        val focus = LocalFocusManager.current
        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .border(width = 1.dp, color = Color.Magenta)
                .padding(10.dp)
        ) {
            Column {
                Text(text = "Master Key Injector", fontSize = 25.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Master Key") },
                    maxLines = 3,
                    value = masterKey,
                    onValueChange = { masterKey = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focus.clearFocus()
                    })
                )
                Spacer(modifier = Modifier.height(10.dp))
                ExampleIndexKey(onSelect = {
                    masterKeyIndex = it
                })
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start),
                        readOnly = true,
                        label = { Text(text = "Master Key Index") },
                        maxLines = 1,
                        value = masterKeyIndex,
                        onValueChange = { },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focus.clearFocus()
                        })
                    )
                    Spacer(modifier = Modifier.width(30.dp))
                    OutlinedButton(
                        onClick = {
                            GlobalScope.launch {
                                logon()
                                resultKey = setMainKey()
                                delay(0.5.seconds)
                                launch(Dispatchers.Main) {
                                    nav.navigate("dialog")
                                }
                            }
                        }
                    ) {
                        Text(text = "Inject Master Key")
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}