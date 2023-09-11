package id.bni46.smartpostest.ui.theme

import com.zcs.sdk.pin.PinWorkKeyTypeEnum
import com.zcs.sdk.pin.pinpad.PinPadManager
import com.zcs.sdk.util.StringUtils

interface Utils {
    val mPadManager: PinPadManager
    var masterKey: String
    var pinKey: String
    var macKey: String
    var tdkKey: String
    var encryptData: String

    fun setMainKey(): String {
        return try {
            val mainKeyByte = StringUtils.convertHexToBytes(masterKey)
            "${
                mPadManager.pinPadUpMastKey(
                    0,
                    mainKeyByte, mainKeyByte.size.toByte()
                )
            }"
        } catch (e: Exception) {
            "$e"
        }
    }

    fun setWorkKey(): String {
        return try {
            val pinKeyByte = StringUtils.convertHexToBytes(pinKey)
            val macKeyByte = StringUtils.convertHexToBytes(macKey)
            val tdkeyByte = StringUtils.convertHexToBytes(tdkKey)
            "${
                mPadManager.pinPadUpWorkKey(
                    0, pinKeyByte, pinKeyByte.size.toByte(),
                    macKeyByte, macKeyByte.size.toByte(), tdkeyByte, tdkeyByte.size.toByte()
                )
            }"
        } catch (e: Exception) {
            "$e"
        }
    }

    fun setEncryptData(): String {
        return try {
            val res = byteArrayOf((encryptData.length / 2).toByte())
            val ret = mPadManager.pinPadEncryptData(
                0,
                PinWorkKeyTypeEnum.MAC_KEY,
                StringUtils.convertHexToBytes(encryptData),
                encryptData.length / 2,
                res
            )
            "encryptData: " + ret + " " + StringUtils.convertBytesToHex(res)
        } catch (e: Exception) {
            "$e"
        }
    }
}