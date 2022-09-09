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
                            BottomSheetContent(sheetState)
                        }
                    }
                }
            }
            if (fullHeight != 0f && contentHeight >= fullHeight) {
                ModalBottomSheetTopBar("This is a top app bar for modal bottom sheet", sheetState)
            }
            BottomSheetContent(sheetState)
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
                            contentDescription = "",
                        )
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetContent(sheetState: ModalBottomSheetState) {
    val coroutineScope = rememberCoroutineScope()
    Column {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp).verticalScroll(
                rememberScrollState()
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "This is a Bottom Sheet")
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris erat erat, mattis ac enim id, vulputate lacinia mi. Aenean auctor pulvinar purus non lobortis. Maecenas consectetur rhoncus efficitur. Vestibulum ut ante vel purus dictum fringilla id ac erat. Pellentesque ut velit ipsum. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Donec accumsan ipsum bibendum leo auctor, ac rhoncus neque mattis. Nunc vel augue erat.")
            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = {
                coroutineScope.launch {
                    sheetState.animateTo(
                        if (sheetState.currentValue == ModalBottomSheetValue.HalfExpanded) {
                            ModalBottomSheetValue.Expanded
                        } else {
                            ModalBottomSheetValue.HalfExpanded
                        }
                    )
                }
            }) {
                Text(
                    when (sheetState.currentValue) {
                        ModalBottomSheetValue.HalfExpanded -> "Change state to Expanded"
                        ModalBottomSheetValue.Expanded -> "Change state to Half Expanded"
                        else -> sheetState.currentValue.toString()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            TextButton(onClick = {
                coroutineScope.launch {
                    sheetState.hide()
                }
            }) {
                Text("Close Sheet")
            }

            Spacer(modifier = Modifier.height(50.dp))
            Text(text = "If you see this text, this bottom sheet is full expanded")
            Spacer(modifier = Modifier.height(20.dp))

            (1..20).forEach {
                Text("This line is $it")
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

fun Modifier.modifyIf(condition: Boolean, modify: Modifier.() -> Modifier) =
    if (condition) modify() else this
