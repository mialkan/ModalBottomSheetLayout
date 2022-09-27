package com.mialkan.modalbottomsheetlayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mialkan.modalbottomsheetlayout.ui.theme.ModalBottomSheetLayoutTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ModalBottomSheetLayoutTheme {
                // A surface container using the 'background' color from the theme
                ModalBottomSheetExample()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalBottomSheetExample(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    var fullHeight by remember { mutableStateOf(0f) }
    var contentHeight by remember { mutableStateOf(0f) }

    ModalBottomSheetLayout(
        modifier = modifier,
        sheetState = sheetState,
        sheetContent = {

            if (fullHeight == 0f || contentHeight == 0f) {
                BoxWithConstraints(modifier) {
                    fullHeight = constraints.maxHeight.toFloat()
                    Box(
                        Modifier
                            .onGloballyPositioned {
                                contentHeight = it.size.height.toFloat()
                            }
                    ) {
                        Column {
                            BottomSheetContent()
                        }
                    }
                }
            }
            if (fullHeight != 0f && contentHeight >= fullHeight) {
                ModalBottomSheetTopBar("This is a top app bar for modal bottom sheet", sheetState)
            }
            BottomSheetContent()
        },
        content = {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("This is a ModalBottomSheetLayout example")
                TextButton(onClick = {
                    coroutineScope.launch {
                        sheetState.show()
                    }
                }) {
                    Text("Open ModalBottomSheet")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ModalBottomSheetTopBar(title: String, sheetState: ModalBottomSheetState) {
    val coroutineScope = rememberCoroutineScope()
    var topBarHeight by remember { mutableStateOf(0f) }
    val barHeight = with(LocalDensity.current) {
        if (sheetState.offset.value > topBarHeight) {
            0.dp
        } else {
            (topBarHeight - sheetState.offset.value).toDp()
        }
    }
    BoxWithConstraints {
        Box(
            modifier = Modifier
                .modifyIf(topBarHeight != 0f) {
                    height(barHeight)
                }
                .onGloballyPositioned {
                    if (topBarHeight < it.size.height.toFloat()) {
                        topBarHeight = it.size.height.toFloat()
                    }
                }
        ) {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.surface,
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.semantics { heading() }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun BottomSheetContent() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "This is a Bottom Sheet")
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.verticalScroll(
                rememberScrollState()
            )
        ) {
            (1..60).forEach {
                Text("This line is $it")
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

fun Modifier.modifyIf(condition: Boolean, modify: Modifier.() -> Modifier) =
    if (condition) modify() else this
