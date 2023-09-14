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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController

interface MainScreen : Utils, InjectMasterKeyScreen, InjectWorkKeyScreen, EncryptDataScreen {
    override var masterKey: String
    override var pinKey: String
    override var macKey: String
    override var tdkKey: String
    override var masterKeyIndex: String
    override var resultKey: String
    override var encryptData: String
    override val keyTitleList: List<String>

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
                InjectWorkingKey(nav = nav)
            }
            item {
                EncryptingData(nav = nav)
            }
        }
    }
}