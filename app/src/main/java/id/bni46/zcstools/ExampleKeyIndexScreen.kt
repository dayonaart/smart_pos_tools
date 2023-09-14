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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

interface ExampleKeyIndexScreen {

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun ExampleIndexKey(onSelect: (key: String) -> Unit) {
        var buttonColor by remember {
            mutableStateOf((0..exampleKeyIdList.size).map { i ->
                if (i == 0) {
                    Color.Cyan
                } else {
                    Color.White
                }
            })
        }
        onSelect(exampleKeyIdList[buttonColor.indexOf(Color.Cyan)].value)
        Box(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .border(width = 1.dp, color = Color.Red)
                .padding(10.dp)
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                exampleKeyIdList.forEach {
                    val i = exampleKeyIdList.indexOf(it)
                    OutlinedButton(
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor[i]),
                        modifier = Modifier
                            .weight(1F)
                            .padding(horizontal = 5.dp, vertical = 5.dp)
                            .height(25.dp),
                        contentPadding = PaddingValues(vertical = 1.dp),
                        onClick = {
                            buttonColor = (0..exampleKeyIdList.size).map { b ->
                                if (i == b) {
                                    Color.Cyan
                                } else {
                                    Color.White
                                }
                            }
                        }) {
                        Text(text = it.name)
                    }
                }
            }
        }
    }
}