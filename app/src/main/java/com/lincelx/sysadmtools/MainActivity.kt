package com.lincelx.sysadmtools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.lincelx.sysadmtools.ui.components.AppScaffold
import com.lincelx.sysadmtools.ui.theme.SysadmtoolsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SysadmtoolsTheme {
                AppScaffold(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
