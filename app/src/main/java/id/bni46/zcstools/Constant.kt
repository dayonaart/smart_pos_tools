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

import com.zcs.sdk.SdkResult
import java.lang.reflect.Field
import java.util.Locale
import kotlin.reflect.KProperty1


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

@Suppress("UNCHECKED_CAST")
fun <R> readInstanceProperty(instance: Any, propertyName: String): R {
    val property = instance::class.members
        .first { it.name == propertyName } as KProperty1<Any, *>
    return property.get(instance) as R
}

private val zcsResultCodeField: Array<Field> = SdkResult::class.java.fields
val zcsSdkResult = zcsResultCodeField
    .map {
        "${it.name.uppercase(Locale.getDefault())} = ${it.get(it.name)}"
    }
val exampleKeyIdList = KeyId::class.members.filter { it.name.contains("Key") }
    .map {
        ExampleKey(
            name = it.name.uppercase(Locale.getDefault()), value = "${
                readInstanceProperty<Int>(
                    KeyId,
                    it.name
                )
            }"
        )
    }

data class ExampleKey(val name: String, val value: String)