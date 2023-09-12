package id.bni46.zcstools

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.zcs.sdk.DriverManager
import com.zcs.sdk.SdkResult
import com.zcs.sdk.pin.pinpad.PinPadManager


class MainViewModel(override val context: Context) : Composeable, Utils {
    override var masterKey by mutableStateOf("")
    override var pinKey by mutableStateOf("")
    override var macKey by mutableStateOf("")
    override var tdkKey by mutableStateOf("")
    override var masterKeyIndex by mutableStateOf("0")
    override var workKeyIndex by mutableStateOf("0")
    override var encryptDataIndex by mutableStateOf("0")
    override var encryptData by mutableStateOf("Data for encrypt")
    override var resultKey by mutableStateOf("")
    override var keyTitleList = listOf("PIN Key", "MAC Key", "TDK Key")
    private val mDriverManager = DriverManager.getInstance()
    private val mSys = mDriverManager.baseSysDevice
    override val mPadManager: PinPadManager = mDriverManager.padManager


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
