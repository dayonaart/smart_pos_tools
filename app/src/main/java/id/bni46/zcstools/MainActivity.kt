package id.bni46.zcstools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import id.bni46.zcstools.ui.theme.SmartPosTestTheme

class MainActivity : ComponentActivity(), Loading {
    private val mainViewModel = MainViewModel(this)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.initSdk()
        setContent {
            val nav = rememberNavController()
            SmartPosTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(topBar = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .background(color = Color.DarkGray)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Id Pay Tools",
                                fontWeight = FontWeight.Bold,
                                fontSize = 25.sp
                            )
                        }
                    }) {
                        NavHost(
                            modifier = Modifier
                                .padding(it)
                                .padding(horizontal = 10.dp),
                            navController = nav,
                            startDestination = "main"
                        ) {
                            composable("main") {
                                mainViewModel.MainView(nav)
                            }
                            dialog(
                                "dialog",
                                dialogProperties = DialogProperties(
                                    dismissOnBackPress = true,
                                    dismissOnClickOutside = true
                                ),
                            ) {
                                (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(
                                    0f
                                )
                                mainViewModel.ShowingDialog(nav)
                            }
                            dialog(
                                "loading",
                                dialogProperties = DialogProperties(
                                    dismissOnBackPress = false,
                                    dismissOnClickOutside = false
                                ),
                            ) {
                                CircleLoading()
                            }
                        }
                    }

                }
            }
        }
    }
}
