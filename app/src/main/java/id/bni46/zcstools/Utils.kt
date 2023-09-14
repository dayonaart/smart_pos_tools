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

import android.annotation.SuppressLint
import android.util.Log
import com.zcs.sdk.Sys
import com.zcs.sdk.pin.pinpad.PinPadManager
import com.zcs.sdk.util.StringUtils
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.Key
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESedeKeySpec

interface Utils {
    val mPadManager: PinPadManager
    var masterKey: String
    var pinKey: String
    var macKey: String
    var tdkKey: String
    var masterKeyIndex: String
    var workKeyIndex: String
    var decryptKeyIndex: String
    var encryptData: String
    var loading: Boolean
    val initResponseDto: InitResponseDto
    val sys: Sys
    var logonResponseDto: LogonResponseDto
    val des_0: ByteArray
        get() = ByteArray(8)

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

    fun setEncryptData(): String {
        return try {
            // The working key ciphertext is obtained by encrypting the Pin plaintext key with the master key
            val encryptedPinkey: ByteArray? = encodeTripleDES(
                StringUtils.convertHexToBytes(encryptData),
                StringUtils.convertHexToBytes(macKey)
            )
            Log.d("Debug", "encrypted_pinkey: " + StringUtils.convertBytesToHex(encryptedPinkey))
            // Generates a check value
            val encryptedPinkeyValue: ByteArray? =
                encodeTripleDES(des_0, StringUtils.convertHexToBytes(encryptData))
            //Combine the working key ciphertext with the check value
            val pinKey =
                StringUtils.convertBytesToHex(encryptedPinkey) + StringUtils.convertBytesToHex(
                    encryptedPinkeyValue
                ).substring(0, 8)
            Log.d("Debug", "pin_key: $pinKey")
            // The working key ciphertext is obtained by encrypting the mac plaintext key with the master key
            val encryptedMackey: ByteArray? = encodeTripleDES(
                StringUtils.convertHexToBytes(encryptData),
                StringUtils.convertHexToBytes(macKey)
            )
            Log.d("Debug", "encrypted_mackey: " + StringUtils.convertBytesToHex(encryptedMackey))
            // Generates a check value
            val encryptedMackeyValue: ByteArray? =
                encodeTripleDES(des_0, StringUtils.convertHexToBytes(encryptData))
            //Combine the working key ciphertext with the check value
            val _macKey =
                StringUtils.convertBytesToHex(encryptedPinkey) + StringUtils.convertBytesToHex(
                    encryptedMackeyValue
                ).substring(0, 8)
            Log.d("Debug", "mac_key: $_macKey")
            // The working key ciphertext is obtained by encrypting the mac plaintext key with the master key
            val encryptedTdkkey: ByteArray? = encodeTripleDES(
                StringUtils.convertHexToBytes(encryptData),
                StringUtils.convertHexToBytes(_macKey)
            )
            Log.d("Debug", "encrypted_tdkkey: " + StringUtils.convertBytesToHex(encryptedTdkkey))
            // Generates a check value
            val encryptedTdkkeyValue: ByteArray? =
                encodeTripleDES(des_0, StringUtils.convertHexToBytes(encryptData))
            //Combine the working key ciphertext with the check value
            val tdkKey =
                StringUtils.convertBytesToHex(encryptedTdkkey) + StringUtils.convertBytesToHex(
                    encryptedTdkkeyValue
                ).substring(0, 8)
            // return
            tdkKey
        } catch (e: Exception) {
            "${e.message}"
        }
    }


    @SuppressLint("GetInstance")
    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeySpecException::class
    )
    fun encodeTripleDES(data: ByteArray?, key: ByteArray): ByteArray? {
        val keys: ByteArray
        if (key.size == 16) {
            keys = ByteArray(24)
            System.arraycopy(key, 0, keys, 0, 16)
            System.arraycopy(key, 0, keys, 16, 8)
        } else {
            keys = key
        }
        val spec = DESedeKeySpec(keys)
        val factory = SecretKeyFactory.getInstance("DESede")
        val secretKey: Key = factory.generateSecret(spec)
        val cipher: Cipher? = Cipher.getInstance("DESede/ECB/NoPadding")
        cipher?.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher?.doFinal(data)
    }

    private fun getSn(): String {
        val pid = arrayOfNulls<String>(1)
        sys.getPid(pid)
        return "${pid[0]?.takeLast(16)}"
    }


    suspend fun logon() {
        try {
            loading = true
            val retrofit = NetworkModule.provideNetwork().create(NetworkInterface::class.java)
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

}
