package com.example.ui.screens

import android.os.SystemClock
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSuccess
import kotlin.random.Random

data class BrainPuzzle(
  val level: Int,
  val category: String, // Number Logic, Pattern IQ, Speed Math, Memory Focus
  val question: String,
  val visualHint: String? = null,
  val options: List<String>,
  val correctIndex: Int,
  val explanation: String
)

@Composable
fun BrainPuzzleScreen(
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Infinite level state
  var currentLevel by remember { mutableIntStateOf(1) }
  var iqScore by remember { mutableIntStateOf(105) }
  var streak by remember { mutableIntStateOf(0) }
  var totalSolved by remember { mutableIntStateOf(0) }

  // Brain Power Capabilities (Out of 100)
  var logicPower by remember { mutableIntStateOf(68) }
  var memoryPower by remember { mutableIntStateOf(62) }
  var speedPower by remember { mutableIntStateOf(70) }
  var patternPower by remember { mutableIntStateOf(65) }

  var currentPuzzle by remember(currentLevel) {
    mutableStateOf(generatePuzzleForLevel(currentLevel))
  }

  var selectedOptionIndex by remember(currentLevel) { mutableStateOf<Int?>(null) }
  var isAnswerSubmitted by remember(currentLevel) { mutableStateOf(false) }
  var isCorrectAnswer by remember(currentLevel) { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .testTag("brain_puzzle_screen")
  ) {
    // Top Bar with Level & Close
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Student Brain Power IQ",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = AmberAccent.copy(alpha = 0.2f)
            ) {
              Text(
                text = "Level $currentLevel (∞)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB45309),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = "Daily cognitive training for competitive exams & focus",
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }
      }

      IconButton(onClick = onClose) {
        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
      }
    }

    Column(modifier = Modifier.padding(16.dp)) {
      // IQ & Brain Capabilities Overview Card
      ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Estimated IQ Level",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Row(verticalAlignment = Alignment.Bottom) {
                Text(
                  text = "$iqScore",
                  style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                  )
                )
                Text(
                  text = " IQ Points",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = EmeraldSuccess,
                  modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                )
              }
            }

            // Streak & Solved
            Column(horizontalAlignment = Alignment.End) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.EmojiEvents,
                  contentDescription = null,
                  tint = AmberAccent,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "$streak Streak",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Text(
                text = "$totalSolved Puzzles Solved",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

          // 4 Cognitive Capabilities Bars
          Text(
            text = "Brain Capabilities Breakdown (मानसिक क्षमता):",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(8.dp))

          CapabilityBar(label = "Logic & Problem Solving", score = logicPower, color = MaterialTheme.colorScheme.primary)
          CapabilityBar(label = "Memory & Working Focus", score = memoryPower, color = Color(0xFF0284C7))
          CapabilityBar(label = "Mental Speed & Calculation", score = speedPower, color = EmeraldSuccess)
          CapabilityBar(label = "Pattern & Spatial IQ", score = patternPower, color = Color(0xFF9333EA))
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // The Active Puzzle Card
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
          .fillMaxWidth()
          .border(
            width = 1.5.dp,
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(18.dp)
          )
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          // Category & Level Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = currentPuzzle.category,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }

            Text(
              text = "Level $currentLevel of ∞",
              fontSize = 12.sp,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Question Prompt
          Text(
            text = currentPuzzle.question,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              lineHeight = 24.sp
            )
          )

          // Visual Hint if any
          if (currentPuzzle.visualHint != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = currentPuzzle.visualHint!!,
                style = MaterialTheme.typography.headlineSmall.copy(
                  fontWeight = FontWeight.ExtraBold,
                  letterSpacing = 2.sp,
                  color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 14.dp, horizontal = 12.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Options List
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            currentPuzzle.options.forEachIndexed { index, optionText ->
              val isSelected = selectedOptionIndex == index
              val isCorrect = currentPuzzle.correctIndex == index
              val containerColor by animateColorAsState(
                targetValue = when {
                  !isAnswerSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
                  isAnswerSubmitted && isCorrect -> EmeraldSuccess.copy(alpha = 0.2f)
                  isAnswerSubmitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                  else -> MaterialTheme.colorScheme.surface
                },
                animationSpec = tween(300)
              )

              Surface(
                onClick = {
                  if (!isAnswerSubmitted) {
                    selectedOptionIndex = index
                  }
                },
                shape = RoundedCornerShape(12.dp),
                color = containerColor,
                border = androidx.compose.foundation.BorderStroke(
                  width = if (isSelected || (isAnswerSubmitted && isCorrect)) 2.dp else 1.dp,
                  color = when {
                    isAnswerSubmitted && isCorrect -> EmeraldSuccess
                    isAnswerSubmitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outlineVariant
                  }
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("puzzle_option_$index")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(28.dp)
                      .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "${('A' + index)}",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Text(
                    text = optionText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    ),
                    modifier = Modifier.weight(1f)
                  )
                  if (isAnswerSubmitted) {
                    if (isCorrect) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Correct",
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(20.dp)
                      )
                    } else if (isSelected) {
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Incorrect",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Submit / Next Button
          if (!isAnswerSubmitted) {
            Button(
              onClick = {
                if (selectedOptionIndex == null) {
                  Toast.makeText(context, "Select an answer option", Toast.LENGTH_SHORT).show()
                  return@Button
                }
                isAnswerSubmitted = true
                val correct = selectedOptionIndex == currentPuzzle.correctIndex
                isCorrectAnswer = correct
                if (correct) {
                  iqScore += 2
                  streak += 1
                  totalSolved += 1
                  logicPower = (logicPower + 1).coerceAtMost(99)
                  memoryPower = (memoryPower + 1).coerceAtMost(99)
                  speedPower = (speedPower + 1).coerceAtMost(99)
                  patternPower = (patternPower + 1).coerceAtMost(99)
                  Toast.makeText(context, "Correct Answer! IQ +2 Points 🎉", Toast.LENGTH_SHORT).show()
                } else {
                  streak = 0
                  Toast.makeText(context, "Incorrect. Review the explanation below.", Toast.LENGTH_SHORT).show()
                }
              },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_puzzle_answer_btn")
            ) {
              Text("Submit Answer (उत्तर सबमिट करें)", fontWeight = FontWeight.Bold)
            }
          } else {
            // Explanation Banner
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isCorrectAnswer) EmeraldSuccess.copy(alpha = 0.12f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = if (isCorrectAnswer) "✅ Excellent Work!" else "❌ Explanation:",
                  fontWeight = FontWeight.Bold,
                  color = if (isCorrectAnswer) EmeraldSuccess else MaterialTheme.colorScheme.error,
                  fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = currentPuzzle.explanation,
                  style = MaterialTheme.typography.bodySmall.copy(
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Infinite Next Level Button
            Button(
              onClick = {
                currentLevel += 1
              },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("next_puzzle_level_btn")
            ) {
              Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Go to Level ${currentLevel + 1} (अगला लेवल)", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun CapabilityBar(label: String, score: Int, color: Color) {
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Text(text = "$score%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
    }
    Spacer(modifier = Modifier.height(3.dp))
    LinearProgressIndicator(
      progress = { score / 100f },
      color = color,
      trackColor = MaterialTheme.colorScheme.surfaceVariant,
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp))
    )
  }
}

// Procedural infinite level puzzle generator
fun generatePuzzleForLevel(level: Int): BrainPuzzle {
  val puzzlePool = listOf(
    // Level 1
    BrainPuzzle(
      level = 1,
      category = "Number Sequence IQ",
      question = "Find the missing number in this progressive sequence:",
      visualHint = "2  ➔  4  ➔  8  ➔  16  ➔  ?",
      options = listOf("24", "32", "30", "28"),
      correctIndex = 1,
      explanation = "Each successive term is multiplied by 2 (doubling sequence). 16 × 2 = 32."
    ),
    // Level 2
    BrainPuzzle(
      level = 2,
      category = "Pattern Recognition",
      question = "What number completes the square matrix relationship?",
      visualHint = "Row 1: [ 3 ,  9 ]\nRow 2: [ 5 , 25 ]\nRow 3: [ 7 ,  ? ]",
      options = listOf("42", "49", "56", "35"),
      correctIndex = 1,
      explanation = "In each row, the second number is the square of the first: 3² = 9, 5² = 25, so 7² = 49."
    ),
    // Level 3
    BrainPuzzle(
      level = 3,
      category = "Speed Mental Math",
      question = "A student room rent is ₹4,500. If electricity bill of ₹650 is added and a discount of ₹150 is applied, what is the final payable amount?",
      visualHint = "₹4,500 + ₹650 - ₹150 = ?",
      options = listOf("₹4,900", "₹5,000", "₹5,100", "₹4,850"),
      correctIndex = 1,
      explanation = "₹4,500 + ₹650 = ₹5,150; ₹5,150 - ₹150 = ₹5,000 exactly."
    ),
    // Level 4
    BrainPuzzle(
      level = 4,
      category = "Logic Deduction",
      question = "Three roommates Amit, Bhavesh, and Chirag stay in rooms 101, 102, 103. Amit is not in 101. Chirag is in 103. Which room is Amit in?",
      visualHint = "Chirag = 103 | Amit ≠ 101",
      options = listOf("Room 101", "Room 102", "Room 103", "Cannot be determined"),
      correctIndex = 1,
      explanation = "Since Chirag is in 103, rooms 101 and 102 remain. Amit is not in 101, so Amit must be in Room 102."
    ),
    // Level 5
    BrainPuzzle(
      level = 5,
      category = "Fibonacci Logic",
      question = "Identify the next number in this classical Fibonacci sequence:",
      visualHint = "1 , 1 , 2 , 3 , 5 , 8 , 13 , ?",
      options = listOf("18", "21", "24", "19"),
      correctIndex = 1,
      explanation = "Each number is the sum of the previous two numbers: 8 + 13 = 21."
    ),
    // Level 6
    BrainPuzzle(
      level = 6,
      category = "Spatial Pattern",
      question = "Look at the differences between consecutive terms: 3, 6, 11, 18, 27, ?",
      visualHint = "+3 , +5 , +7 , +9 , +11",
      options = listOf("36", "38", "40", "42"),
      correctIndex = 1,
      explanation = "The difference increases by 2 each step (+3, +5, +7, +9). The next increase is +11: 27 + 11 = 38."
    ),
    // Level 7
    BrainPuzzle(
      level = 7,
      category = "Speed Math IQ",
      question = "If 6 study lamps consume 72 units of electricity in a month, how many units will 10 study lamps consume?",
      visualHint = "6 lamps = 72 units ➔ 10 lamps = ?",
      options = listOf("110 units", "120 units", "130 units", "100 units"),
      correctIndex = 1,
      explanation = "Each lamp consumes 72 / 6 = 12 units. Thus 10 lamps consume 10 × 12 = 120 units."
    ),
    // Level 8
    BrainPuzzle(
      level = 8,
      category = "Prime Logic",
      question = "Find the missing prime number in this prime sequence:",
      visualHint = "11 , 13 , 17 , 19 , 23 , ?",
      options = listOf("27", "29", "31", "25"),
      correctIndex = 1,
      explanation = "29 is the next prime number after 23 (27 and 25 are composite numbers)."
    )
  )

  // For level <= pool size, pick directly; for higher infinite levels, procedurally generate!
  val index = (level - 1) % puzzlePool.size
  val base = puzzlePool[index]

  if (level <= puzzlePool.size) {
    return base.copy(level = level)
  } else {
    // Infinite Procedural Generation
    val multiplier = (level / puzzlePool.size) + 1
    val startVal = level * 3
    val step = level % 5 + 2
    val ans = startVal + step * 3
    return BrainPuzzle(
      level = level,
      category = "Infinite Challenge • Tier $multiplier",
      question = "Solve the procedural progression for Level $level (Step +$step):",
      visualHint = "$startVal  ➔  ${startVal + step}  ➔  ${startVal + step * 2}  ➔  ?",
      options = listOf("${ans - step}", "$ans", "${ans + step}", "${ans + 2}"),
      correctIndex = 1,
      explanation = "Step difference is +$step. ${startVal + step * 2} + $step = $ans."
    )
  }
}
