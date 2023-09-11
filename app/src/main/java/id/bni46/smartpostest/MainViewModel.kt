package id.bni46.smartpostest

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.zcs.sdk.DriverManager
import com.zcs.sdk.SdkResult
import com.zcs.sdk.pin.PinWorkKeyTypeEnum
import com.zcs.sdk.util.StringUtils


class MainViewModel(private val context: Context) {
    private var masterKey by mutableStateOf("484455B474A6C6115FF62236D8A09C74")
    private var pinKey by mutableStateOf("BF1CA957FE63B286E2134E08A8F3DDA903E0686F")
    private var macKey by mutableStateOf("8670685795c8d2ea0000000000000000d2db51f1")
    private var tdkKey by mutableStateOf("00A0ABA733F2CBB1E61535EDCFDC34A93AA3EA2D")
    private var encryptData by mutableStateOf("Message for encrypt")
    private var keyStateList = listOf(pinKey, macKey, tdkKey)
    private var keyTitleList = listOf("PIN Key", "MAC Key", "TDK Key")
    private var resultKey by mutableStateOf("")
    private val mDriverManager = DriverManager.getInstance()
    private val mSys = mDriverManager.baseSysDevice
    private val mPadManager = mDriverManager.padManager

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
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Rounded.Star, contentDescription = null)
                        }
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
                resultKey = "${setMainKey()}"
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
            value = keyStateList[it],
            onValueChange = { v ->
                when (it) {
                    0 -> {
                        pinKey = v
                    }

                    1 -> {
                        macKey = v
                    }

                    3 -> {
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
                        resultKey = "${setWorkKey()}"
                        nav.navigate("dialog")
                    }
                ) {
                    Text(text = "Inject Pin Key")
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

    }

    private fun setMainKey(): Int {
        val mainKey = "484455B474A6C6115FF62236D8A09C74"
        val mainKeyByte = StringUtils.convertHexToBytes(mainKey)
        // index: means Key index, 0x00~0x0F
        // key: The key length is a multiple of 8.
        return mPadManager.pinPadUpMastKey(
            KeyId.mainKey,
            mainKeyByte, mainKeyByte.size.toByte()
        )
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
            Text(text = "Inject Master Key")
        }
        Spacer(modifier = Modifier.height(20.dp))
    }

    private fun setWorkKey(): Int {
        val pinKeyByte = StringUtils.convertHexToBytes(pinKey)
        val macKeyByte = StringUtils.convertHexToBytes(macKey)
        val tdkeyByte = StringUtils.convertHexToBytes(tdkKey)
        return mPadManager.pinPadUpWorkKey(
            KeyId.pinKey, pinKeyByte, pinKeyByte.size.toByte(),
            macKeyByte, macKeyByte.size.toByte(), tdkeyByte, tdkeyByte.size.toByte()
        )
    }

    private fun setEncryptData(): String {
        return try {
            val dataForDes = "11111111111111111111111111111111";
            val res = byteArrayOf((dataForDes.length / 2).toByte())
            val ret = mPadManager.pinPadEncryptData(
                0,
                PinWorkKeyTypeEnum.MAC_KEY,
                StringUtils.convertHexToBytes(dataForDes),
                dataForDes.length / 2,
                res
            )
            "encryptData: " + ret + " " + StringUtils.convertBytesToHex(res)
        } catch (e: Exception) {
            "$e"
        }
    }

    fun initSdk() {
        try {
            mSys.showLog(true)
            var status = mSys.sdkInit()
            if (status != SdkResult.SDK_OK) {
                mSys.sysPowerOn()
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            status = mSys.sdkInit()
            if (status != SdkResult.SDK_OK) {
                Toast.makeText(context, "Can't bind Devices Status $status", Toast.LENGTH_LONG)
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Can't bind Devices ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}


object KeyId {
    /**
     * Main key.
     */
    @JvmStatic
    var mainKey = 1

    /**
     * MAC key.
     */
    @JvmStatic
    var macKey = 11

    /**
     * PIN key.
     */
    @JvmStatic
    var pinKey = 12

    /**
     * TDK key.
     */
    @JvmStatic
    var tdkKey = 13

    /**
     * DEK key.
     */
    @JvmStatic
    var dekKey = 14

    /**
     * CBC MAC key.
     */
    @JvmStatic
    var cbcKey = 15
}
