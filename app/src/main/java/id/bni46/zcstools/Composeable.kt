package id.bni46.zcstools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.zcs.sdk.Sys
import com.zcs.sdk.pin.PinWorkKeyTypeEnum
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface Composeable : Utils {
    override var masterKey: String
    override var pinKey: String
    override var macKey: String
    override var tdkKey: String
    override var masterKeyIndex: String
    override var workKeyIndex: String
    override var encryptDataIndex: String
    var resultKey: String
    override var encryptData: String
    val keyTitleList: List<String>
    val context: Context
    var loading: Boolean
    val initResponseDto: InitResponseDto
    val sys: Sys
    var logonResponseDto: LogonResponseDto
    private fun getSn(): String {
        val pid = arrayOfNulls<String>(1)
        sys.getPid(pid)
        return "${pid[0]?.takeLast(16)}"
    }


    private suspend fun logon() {
        loading = true
        try {
            val retrofit = NetworkModule.provideNetwok().create(NetworkInterface::class.java)
            val r = retrofit.postLogon(
                LogonRequestDto(
                    mMID = initResponseDto.mmid,
                    mTID = initResponseDto.mtid,
                    serialNo = getSn()
                )
            )
            if (r.secData != null) {
                logonResponseDto = r
            }
            loading = false
        } catch (e: Exception) {
            loading = false
        }
    }

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
        LaunchedEffect(key1 = loading, block = {
            if (loading) {
                nav.navigate("loading")
            } else {
                nav.popBackStack("loading", inclusive = true)
            }
        })
        LazyColumn(horizontalAlignment = Alignment.End) {
            item {
                InjectMasterKey(nav = nav)
            }
            item {
                InjectWorkKey(nav = nav)
            }
            item {
                EncryptData(nav = nav)
            }
        }
    }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start),
                        label = { Text(text = "Master Key Index") },
                        maxLines = 1,
                        value = masterKeyIndex,
                        onValueChange = { masterKeyIndex = it },
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


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InjectWorkKey(nav: NavHostController) {
        val focus = LocalFocusManager.current
        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .border(width = 1.dp, color = Color.Cyan)
                .padding(10.dp)

        ) {
            Column {
                Text(text = "Work Key Injector", fontSize = 25.sp)
                Spacer(modifier = Modifier.height(10.dp))
                keyTitleList.forEach { f ->
                    val index = keyTitleList.indexOf(f)
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = keyTitleList[index]) },
                        maxLines = 3,
                        value = listOf(
                            logonResponseDto.secData?.pinKey ?: pinKey,
                            logonResponseDto.secData?.macKey ?: macKey,
                            logonResponseDto.secData?.dataKey ?: tdkKey
                        )[index],
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focus.clearFocus()
                        }),
                        onValueChange = { v ->
                            when (index) {
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
                    if ((index + 1) == keyTitleList.size) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .weight(1f)
                                    .wrapContentWidth(Alignment.Start),
                                label = { Text(text = "Work Key Index") },
                                maxLines = 1,
                                value = workKeyIndex,
                                onValueChange = { workKeyIndex = it },
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
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EncryptData(nav: NavHostController) {
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