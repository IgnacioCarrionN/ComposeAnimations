package dev.carrion.composeanimations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.carrion.composeanimations.ui.theme.ComposeAnimationsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeAnimationsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                    ) {
                        // Custom Animated Button
                        AnimationContainer("Custom animated Button") {
                            CustomAnimatedButton(onClick = { }) {
                                Text(
                                    text = "Click Me",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                        }
                        // Pulsating Dot
                        AnimationContainer("Pulsating Dot") {
                            PulsatingDot(
                                color = Color.Red,
                                size = 50.dp
                            )
                        }

                        // Animated Content
                        AnimationContainer("Animated Content") {
                            var visible by remember { mutableStateOf(true) }
                            LaunchedEffect(Unit) {
                                while (true) {
                                    delay(3000)
                                    visible = !visible
                                }
                            }
                            AnimatedContent(
                                visible = visible,
                                content = {
                                    Text(
                                        text = "Hello, World!",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                },
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        // Custom Transition Card
                        AnimationContainer("Custom Transition Card") {
                            var expanded by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) {
                                while (true) {
                                    delay(1000)
                                    expanded = !expanded
                                }
                            }
                            CustomTransitionCard(
                                expanded = expanded,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Custom transition card",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomAnimatedButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.5f else 1f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = Spring.StiffnessLow
        )
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            isPressed = !isPressed
        }
    }

    Box(
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onClick()
            }
    ) {
        content()
    }
}

@Composable
fun PulsatingDot(
    color: Color,
    size: Dp = 20.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsating")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .background(color, CircleShape)
    )
}

@Composable
fun AnimatedContent(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Box(modifier = modifier) {
            content()
        }
    }
}

@Composable
fun CustomTransitionCard(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(
        targetState = expanded,
        label = "card_transition"
    )

    val cardRoundedCorners by transition.animateDp(
        label = "corner_radius",
        targetValueByState = { isExpanded: Boolean ->
            if (isExpanded) 0.dp else 32.dp
        }
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cardRoundedCorners)
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
}

@Composable
private fun AnimationContainer(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge
        )
        content()
    }
}

