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