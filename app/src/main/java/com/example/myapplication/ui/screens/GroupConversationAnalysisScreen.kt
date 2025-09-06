package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.BackgroundColor
import com.example.myapplication.ui.theme.MainColor1
import com.example.myapplication.ui.theme.MainColor2
import com.example.myapplication.ui.theme.PointColor1
import com.example.myapplication.ui.theme.PointColor2

/**
 * 단체 톡방 분석 화면
 * 
 * 그룹 대화의 분석 결과를 보여주는 화면입니다.
 * 캐릭터 분석, MVP, 관계 분석, 조언 등을 포함합니다.
 */
@Composable
fun GroupConversationAnalysisScreen(
    onNavigateBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor) // Theme.kt의 BackgroundColor 사용
            .verticalScroll(scrollState)
    ) {
        // 상단 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "뒤로 가기",
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "단체 톡방 분석",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        // 설명 텍스트
        Text(
            text = "최근 500~1000개의 대화를 분석합니다",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 캐릭터 분석 섹션
        CharacterAnalysisSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 대화의 MVP 섹션
        MVPSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 관계 분석 섹션
        RelationshipAnalysisSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 정리 섹션
        SummarySection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 종합 조언 섹션
        OverallAdviceSection()
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CharacterAnalysisSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1E) // 카드 배경은 유지
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "캐릭터 분석 (Character Analysis)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 정수경 - 스마일러
            CharacterItem(
                name = "정수경 (Jung Soo-kyung)",
                role = "스마일러 (Smiler)",
                emoji = "😊",
                description = "대화 속에서 웃음(ㅋㅋ, ㅎㅎ)과 밝은 반응이 많아 톡방 분위기를 환하게 만듦",
                avatarColor = MainColor1 // Theme.kt의 MainColor1 사용
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 김민재 - 에너지볼
            CharacterItem(
                name = "김민재 (Kim Min-jae)",
                role = "에너지볼 (Energy Ball)",
                emoji = "⚡",
                description = "대화를 주도하고 활발하게 리액션하며 방 분위기를 계속 끌어올리는 캐릭터라서",
                avatarColor = PointColor1 // Theme.kt의 PointColor1 사용
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 한서연 - 따뜻한 햇살
            CharacterItem(
                name = "한서연 (Han Seo-yeon)",
                role = "따뜻한 햇살 (Warm Sunshine)",
                emoji = "☀️",
                description = "조언과 배려가 묻어나는 말투로 톡방에 안정감을 주기 때문이다",
                avatarColor = PointColor2 // Theme.kt의 PointColor2 사용
            )
        }
    }
}

@Composable
fun CharacterItem(
    name: String,
    role: String,
    emoji: String,
    description: String,
    avatarColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // 아바타
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(avatarColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(2),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = emoji,
                    fontSize = 16.sp
                )
            }
            
            Text(
                text = role,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MainColor1, // Theme.kt의 MainColor1 사용
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun MVPSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1E) // 카드 배경은 유지
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "대화의 MVP (MVP of Conversation)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // MVP 정보
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MVP: ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = "정수경 (Jung Soo-kyung) (427개 메시지)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainColor1 // Theme.kt의 MainColor1 사용
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "이유:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            
            Text(
                text = "가장 활발하게 대화에 참여하며, 분위기를 이어가고 실수 방지, 팀워크 관련 멘트 등을 자주 언급했음.",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "대표 메시지 예시:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            
            // 메시지 예시
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "😎",
                        fontSize = 16.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    Text(
                        text = "심심해",
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = "ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ",
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 요약
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "👍",
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "분위기 유지를 하면서 동시에 실수 방지/긍정적 마인드를 강조하는 모습이 MVP 역할로 보임.",
                    fontSize = 13.sp,
                    color = MainColor1 // Theme.kt의 MainColor1 사용
                )
            }
        }
    }
}

@Composable
fun RelationshipAnalysisSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1E) // 카드 배경은 유지
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "당신과의 관계는? (Your Relationship with?)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 정수경 & 000
            RelationshipItem(
                title = "정수경 & 000",
                analysis = "000는 대화를 활발하게 주도하고 리액션이 크며, 분위기를 끌어올리는 역할을 합니다. 정수경님은 웃음과 짧은 반응으로 그 흐름을 잘 받아주고 있어요. 다만 000의 에너지가 너무 빠르게 오가면 정수경님은 간결하게만 대응하는 경우가 있어 대화가 얕아 보일 때도 있습니다.",
                advice = "000의 에너지에 짧게만 반응하지 말고, 가끔은 구체적인 경험이나 감정을 덧붙이면 균형이 맞습니다. 예를 들어 'ㅋㅋ 대박' 대신 'ㅋㅋ 진짜 웃기다, 나도 어제 비슷한 일 있었어' 식으로 말하면 대화가 더 풍성해지고 서로 만족감이 커집니다."
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 정수경 & ㅁㅁㅁ
            RelationshipItem(
                title = "정수경 & ㅁㅁㅁ",
                analysis = "ㅁㅁ은 따뜻하고 조언적인 말투로 그룹 내에서 안정감을 주는 역할을 합니다. 정수경님은 장난스럽고 밝은 톤으로 잘 반응하지만, ㅁㅁ의 멘토 같은 톤에 비해 가볍게 느껴질 수 있습니다.",
                advice = "ㅁㅁ이 의견을 낼 때, 짧은 반응보다 '맞아요, 그래서 저도 이렇게 해볼까 생각했어요' 같은 구체적인 리액션이 좋습니다. 그렇게 하면 존중받는다는 느낌을 줄 수 있고, 신뢰 관계가 더욱 깊어집니다."
            )
        }
    }
}

@Composable
fun RelationshipItem(
    title: String,
    analysis: String,
    advice: String
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⭐",
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MainColor1 // Theme.kt의 MainColor1 사용
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "관계 분석:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        
        Text(
            text = analysis,
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "해결 및 조언:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        
        Text(
            text = advice,
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun SummarySection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1E) // 카드 배경은 유지
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⭐",
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "정리 - 정수경님 말투의 강점과 보완점",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainColor1 // Theme.kt의 MainColor1 사용
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "강점:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            
            Text(
                text = "밝고 활발한 분위기를 만들며, 친근감 있는 리액션으로 상대방을 편하게 함.",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "보완점:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            
            Text(
                text = "간혹 짧고 가벼운 답변이 성의 없어 보일 수 있어, 가끔은 두세 문장으로 풀어내는 습관이 필요.",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun OverallAdviceSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1E) // 카드 배경은 유지
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🍃",
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "종합 조언",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainColor1 // Theme.kt의 MainColor1 사용
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "정수경님은 톡방의 '스마일러' 같은 존재라서, 이미 긍정적인 영향력이 커요. 다만 000와는 에너지의 깊이 맞추기, ㅁㅁㅁ과는 조언에 대한 구체적 반응이 핵심이에요. 이 두 가지를 신경 쓰면 관계는 훨씬 단단하고, 서로가 '통한다'는 만족감을 느낄 수 있을 겁니다.",
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
    }
}
