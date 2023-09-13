package id.bni46.zcstools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

interface ShowDialog {
    val context: Context
    val resultKey: String

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowingDialog(nav: NavHostController) {
        AlertDialog(
            onDismissRequest = {
                nav.popBackStack("dialog", inclusive = true)
            },
            confirmButton = { },
            title = {
                Text(text = "Result")
            },
            text = {
                OutlinedTextField(
                    value = resultKey,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Text(text = "Copy", modifier = Modifier
                            .clickable {
                                val clipboardManager =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val myClip: ClipData = ClipData.newPlainText("note_copy", resultKey)
                                clipboardManager.setPrimaryClip(myClip)
                                Toast
                                    .makeText(context, "copied", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .padding(end = 10.dp))
                    })
            }
        )
    }

}