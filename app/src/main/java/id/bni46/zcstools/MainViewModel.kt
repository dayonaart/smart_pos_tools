/*
 * Copyright (c) 2023 By Dayona
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package id.bni46.zcstools

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.zcs.sdk.DriverManager
import com.zcs.sdk.SdkResult
import com.zcs.sdk.Sys
import com.zcs.sdk.pin.pinpad.PinPadManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainViewModel(override val context: Context) : MainScreen, Utils, ShowDialog {
    override var masterKey by mutableStateOf("484455B474A6C6115FF62236D8A09C74")
    override var pinKey by mutableStateOf("")
    override var macKey by mutableStateOf("")
    override var tdkKey by mutableStateOf("")
    override var masterKeyIndex by mutableStateOf("0")
    override var workKeyIndex by mutableStateOf("0")
    override var decryptKeyIndex by mutableStateOf("0")
    override var encryptData by mutableStateOf("Data for encrypt")
    override var resultKey by mutableStateOf("")
    override var keyTitleList = listOf("PIN Key", "MAC Key", "TDK Key")
    private val mDriverManager = DriverManager.getInstance()
    private val mSys = mDriverManager.baseSysDevice
    override val mPadManager: PinPadManager = mDriverManager.padManager
    override var loading by mutableStateOf(false)
    override var logonResponseDto by mutableStateOf(LogonResponseDto(null))
    private val driverManager = DriverManager.getInstance()
    override val sys: Sys = driverManager.baseSysDevice

    override var initResponseDto by mutableStateOf(InitResponseDto())
    private fun getSn(): String? {
        val pid = arrayOfNulls<String>(1)
        sys.getPid(pid)
        return pid[0]?.takeLast(16)
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
            val result = zcsSdkResult.find { it.contains("$status") }
            if (status != SdkResult.SDK_OK) {
                Toast.makeText(
                    context,
                    "Can't bind Devices Status $result",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Can't bind Devices ${e.message}", Toast.LENGTH_LONG).show()
        }
        init()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun init() {
        GlobalScope.launch {
            loading = true
            try {
                val retrofit = NetworkModule.provideNetwork().create(NetworkInterface::class.java)
                val r = retrofit.init(InitDto(HSN = "${getSn()}"))
                if (r.mmid.isNotEmpty()) {
                    initResponseDto = r
                }
                loading = false
            } catch (e: Exception) {
                loading = false
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
