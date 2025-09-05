package com.example.myapplication.ui.components.ConversationScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun ConversationAnalysisCard(
    type: String,
    modifier: Modifier = Modifier,
    elevation: Dp = 4.dp,
    onStartClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .width(300.dp)
            .heightIn(min = 500.dp, max = 530.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val imageResId = when (type) {
                "group" -> R.drawable.image_conversation_analysis_group
                else -> R.drawable.image_conversation_analysis_persnor
            }
            val buttonText = when (type) {
                "group" -> "단체 톡방 분석 시작하기"
                else -> "개인 톡방 분석 시작하기"
            }
            val options = when (type) {
                "group" -> listOf("대화의 MVP는?", "모두의 성격 보기", "관계 분석하고 조언받기", "내용 요약")
                else -> listOf("내 말투 습관은?", "친구의 성격 보기", "관계 분석하고 조언받기", "내용 요약")
            }

            Image(
                painter = painterResource(id = imageResId),
                contentDescription = if (type == "group") "단체 톡방 분석" else "개인 톡방 분석",
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .heightIn(min = 330.dp, max = 350.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    AnalysisOptionButton(text = options[0])
                    AnalysisOptionButton(text = options[1])
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    AnalysisOptionButton(text = options[2])
                    AnalysisOptionButton(text = options[3])
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AnalysisOptionButton(
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { /* 옵션 선택 동작 */ },
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF9C88FF)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}
