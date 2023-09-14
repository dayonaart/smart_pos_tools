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

interface InjectWorkKeyScreen : Utils, ExampleKeyIndexScreen {
    override var masterKey: String
    override var pinKey: String
    override var macKey: String
    override var tdkKey: String
    override var workKeyIndex: String
    var resultKey: String
    override var encryptData: String
    val keyTitleList: List<String>
    override var logonResponseDto: LogonResponseDto

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InjectWorkingKey(nav: NavHostController) {
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
                            resultKey(logonResponseDto.secData?.pinKey) ?: pinKey,
                            resultKey(logonResponseDto.secData?.macKey) ?: macKey,
                            resultKey(logonResponseDto.secData?.dataKey) ?: tdkKey
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
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            ExampleIndexKey(onSelect = {
                                workKeyIndex = it
                            })
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .weight(1f)
                                        .wrapContentWidth(Alignment.Start),
                                    label = { Text(text = "Work Key Index") },
                                    maxLines = 1,
                                    value = workKeyIndex,
                                    readOnly = true,
                                    onValueChange = { },
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
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    private fun resultKey(key: String?): String? {
        return try {
            key?.substring(1, key.length)
        } catch (e: Exception) {
            null
        }
    }
}