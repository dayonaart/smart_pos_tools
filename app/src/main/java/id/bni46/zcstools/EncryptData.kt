package id.bni46.zcstools

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.zcs.sdk.pin.PinWorkKeyTypeEnum

interface EncryptData : Utils {
    override var masterKey: String
    override var pinKey: String
    override var macKey: String
    override var tdkKey: String
    override var masterKeyIndex: String
    override var workKeyIndex: String
    override var encryptDataIndex: String
    var resultKey: String
    override var encryptData: String

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EncryptingData(nav: NavHostController) {
        val focus = LocalFocusManager.current
        val encryptionTypeList = listOf(
            PinWorkKeyTypeEnum.MAC_KEY,
            PinWorkKeyTypeEnum.PIN_KEY,
            PinWorkKeyTypeEnum.TDKEY,
            PinWorkKeyTypeEnum.ORTHER,
        )
        val encryptionTypeName = listOf(
            "MAC KEY",
            "PIN KEY",
            "TDKEY",
            "ORTHER",
        )
        var buttonColor by remember {
            mutableStateOf((0..encryptionTypeList.size).map { i ->
                if (i == 0) {
                    Color.Cyan
                } else {
                    Color.White
                }
            })
        }
        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .border(width = 1.dp, color = Color.Yellow)
                .padding(10.dp)
        ) {
            Column {
                Text(text = "Data Encryption", fontSize = 25.sp)
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
                Text(text = "Encryption Type", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    encryptionTypeList.map { t ->
                        val i = encryptionTypeList.indexOf(t)
                        OutlinedButton(
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor[i]),
                            onClick = {
                                buttonColor = (0..encryptionTypeList.size).map {
                                    if (i == it) {
                                        Color.Cyan
                                    } else {
                                        Color.White
                                    }
                                }
                            }) {
                            Text(text = encryptionTypeName[i], fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start),
                        label = { Text(text = "Encrypt Key Index") },
                        maxLines = 1,
                        value = encryptDataIndex,
                        onValueChange = { encryptDataIndex = it },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focus.clearFocus()
                        }),
                    )

                    Spacer(modifier = Modifier.width(30.dp))
                    OutlinedButton(
                        onClick = {
                            resultKey = setEncryptData(
                                encryptionTypeList[buttonColor.indexOf(Color.Cyan)]
                            )
                            nav.navigate("dialog")
                        }
                    ) {
                        Text(text = "Encrypt Data")
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}