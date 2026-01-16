package rw.delasoft.qtmailguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import rw.delasoft.qtmailguard.presentation.screen.EmailScreen
import rw.delasoft.qtmailguard.ui.theme.QTMailGuardTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QTMailGuardTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EmailScreen()
                }
            }
        }
    }
}
