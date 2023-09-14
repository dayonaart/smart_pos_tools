package id.bni46.zcstools

import com.zcs.sdk.pin.PinWorkKeyTypeEnum
import com.zcs.sdk.pin.pinpad.PinPadManager
import com.zcs.sdk.util.StringUtils

interface Utils {
    val mPadManager: PinPadManager
    var masterKey: String
    var pinKey: String
    var macKey: String
    var tdkKey: String
    var masterKeyIndex: String
    var workKeyIndex: String
    var encryptDataIndex: String
    var encryptData: String

    fun setMainKey(): String {
        return try {
            val mainKeyByte = StringUtils.convertHexToBytes(masterKey)
            val ret = mPadManager.pinPadUpMastKey(
                masterKeyIndex.toInt(),
                mainKeyByte, mainKeyByte.size.toByte()
            )
            val result = zcsSdkResult.find { it.contains("$ret") }
            "$result"
        } catch (e: Exception) {
            "$e"
        }
    }

    fun setPinPadUpWorkKey(): String {
        return try {
            val pinKeyByte = StringUtils.convertHexToBytes(pinKey)
            val macKeyByte = StringUtils.convertHexToBytes(macKey)
            val tdkeyByte = StringUtils.convertHexToBytes(tdkKey)
            val ret = mPadManager.pinPadUpWorkKey(
                workKeyIndex.toInt(), pinKeyByte, pinKeyByte.size.toByte(),
                macKeyByte, macKeyByte.size.toByte(), tdkeyByte, tdkeyByte.size.toByte()
            )
            val result = zcsSdkResult.find { it.contains("$ret") }
            "$result"
        } catch (e: Exception) {
            "$e"
        }
    }

    fun setEncryptData(pinWorkKeyTypeEnum: PinWorkKeyTypeEnum): String {
        return try {
            val res = byteArrayOf((encryptData.length / 2).toByte())
            val ret = mPadManager.pinPadEncryptData(
                encryptDataIndex.toInt(),
                pinWorkKeyTypeEnum,
                StringUtils.convertHexToBytes(encryptData),
                encryptData.length / 2,
                res
            )
            val result = zcsSdkResult.find { it.contains("$ret") }
            "$result" + ": encryptData: " + ret + " " + StringUtils.convertBytesToHex(res)
        } catch (e: Exception) {
            "$e\nByte Array : ${byteArrayOf(((encryptData.length / 2).toByte()))}\ninput length : ${encryptData.length / 2}"
        }
    }
}
