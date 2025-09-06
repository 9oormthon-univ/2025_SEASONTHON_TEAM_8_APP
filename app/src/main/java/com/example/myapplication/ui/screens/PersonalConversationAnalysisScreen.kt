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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.BackgroundColor
import com.example.myapplication.ui.theme.MainColor1
import com.example.myapplication.ui.theme.MainColor2
import com.example.myapplication.ui.theme.PointColor1
import com.example.myapplication.ui.theme.PointColor2

/**
 * 개인 톡방 분석 화면
 * 
 * 1:1 대화의 분석 결과를 보여주는 화면입니다.
 * 캐릭터 분석, 관계 분석, 말투 분석, 우정 분석 등을 포함합니다.
 */
@Composable
fun PersonalConversationAnalysisScreen(
    onNavigateBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
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
                text = "개인 톡방 분석",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        // 설명 텍스트
        Text(
            text = "최근 500~1000개의 대화를 분석합니다",
            fontSize = 14.sp,
            color = MainColor1,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 캐릭터 분석 섹션
        PersonalCharacterAnalysisSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 관계 분석 섹션
        PersonalRelationshipAnalysisSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 말투 분석 섹션
        ToneAnalysisSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 우정 분석 섹션
        FriendshipAnalysisSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 대화 패턴 인사이트 섹션
        ConversationPatternInsightsSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 관계 성장 가이드 섹션
        RelationshipGrowthGuideSection()
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PersonalCharacterAnalysisSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1E)
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
            PersonalCharacterItem(
                name = "정수경",
                role = "스마일러",
                emoji = "😊",
                description = "대화 속에서 웃음(ㅋㅋ, ㅎㅎ)과 밝은 반응이 많아 톡방 분위기를 환하게 만듦",
                avatarColor = MainColor1
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 김민재 - 에너지볼
            PersonalCharacterItem(
                name = "김민재",
                role = "에너지볼",
                emoji = "⚡",
                description = "대화를 주도하고 활발하게 리액션하며 방 분위기를 계속 끌어올리는 캐릭터라서",
                avatarColor = PointColor1
            )
        }
    }
}

@Composable
fun PersonalCharacterItem(
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
                color = MainColor1,
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
fun PersonalRelationshipAnalysisSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1E)
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
                    text = "당신과의 관계는? (Your Relationship with?)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "정수경 & 000",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MainColor1
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "관계 분석:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            
            Text(
                text = "000는 대화를 활발하게 주도하고 리액션이 크며, 분위기를 끌어올리는 역할을 합니다. 정수경님은 웃음과 짧은 반응으로 그 흐름을 잘 받아주고 있어요. 다만 000의 에너지가 너무 빠르게 오가면 정수경님은 간결하게만 대응하는 경우가 있어 대화가 얕아 보일 때도 있습니다.",
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
                text = "000의 에너지에 짧게만 반응하지 말고, 가끔은 구체적인 경험이나 감정을 덧붙이면 균형이 맞습니다. 예를 들어 \"ㅋㅋ 대박\" 대신 \"ㅋㅋ 진짜 웃기다, 나도 어제 비슷한 일 있었어\" 식으로 말하면 대화가 더 풍성해지고 서로 만족감이 커집니다.",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ToneAnalysisSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1E)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "당신의 말투는? (Your Tone of Voice?)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "내 말투는 대체로 밝고 짧은 문장이 많으며 감탄사와 감정 표현을 자주 사용합니다. 질문형 말투로 상대방의 반응을 이끌어내는 편이지만, 가끔은 단답형으로 끝내서 오해를 살 수 있습니다. 장난스럽고 친근한 뉘앙스가 자주 나타나고, 상대의 말을 이어주는 반응이 많아 대화를 유쾌하게 유지합니다. 진지한 주제보다는 일상적인 대화에서 더 활발하고, 즉흥적인 스타일이 강하게 드러납니다. 이런 특징 때문에 가볍고 친근해 보이지만, 때로는 성의 없어 보일 수 있다는 점이 아쉬움으로 남습니다. 정리하자면 내 말투는 분위기를 즐겁게 만들지만, 상황에 따라 조금 더 길고 구체적인 표현을 보완할 필요가 있습니다.",
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun FriendshipAnalysisSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1E)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "우리의 우정은? (Our Friendship?)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "우리는 활발하게 대화를 이어가는 빈도에서 친밀함이 잘 드러나고 있어요. 서로 사적인 이야기를 공유할 만큼 신뢰가 있고, 장난과 애칭, 감정 표현이 자연스럽게 오가는 건 높은 친밀도의 증거입니다. 말투가 자유롭다는 건 서로가 편하다는 의미이기도 합니다. 일정과 약속을 공유한다는 점에서 실생활에서도 얽힘이 있다는 걸 알 수 있어요. 다만 대화가 짧을 때는 \"가볍게만 대하는 것 아닌가?\"라는 느낌을 줄 수 있습니다. 기본적으로는 친근하지만 깊은 고민을 나누는 빈도는 상대적으로 적습니다. 우정의 신뢰도는 높지만, 대화의 깊이는 중간 정도라고 볼 수 있습니다. 전체적으로는 갈등보다는 웃음 위주의 분위기가 강하고, 결론적으로 우리 관계는 편안하고 꾸준한 우정 단계라고 할 수 있습니다.",
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ConversationPatternInsightsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1E)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "대화 패턴 인사이트 (Conversation Pattern Insights)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "우리 톡방 대화는 특정 시간대, 특히 저녁과 밤에 집중되는 경향이 있습니다. 사진과 짧은 반응이 높은 비율을 차지하며, 주도권은 주로 한두 명이 잡고 대화를 끌어갑니다. 이모지와 특수문자를 적극적으로 활용하고, 일상적인 이야기들이 대화의 큰 부분을 차지합니다. 특정 주제에서는 집중도가 높아 활발하게 의견이 오가지만, 전체적으로는 즉흥적이고 가벼운 톤이 강합니다. 질문형 메시지 이후에는 반응이 빠르게 이어지고, 그룹 내 역할도 명확히 나뉘어 있습니다. 깊은 토론보다는 순간적인 반응과 일상 공유가 주류를 이룹니다. 결과적으로 우리 톡방은 즉흥적이고 일상 공유형 톡방의 성격이 강하다고 볼 수 있습니다.",
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun RelationshipGrowthGuideSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1E)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "관계 성장 가이드 (Relationship Growth Guide)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "관계를 한 단계 더 깊게 발전시키려면 대화 주제를 일상에서 가치관이나 목표로 확장해보는 게 좋아요. 대화 속에서 서로에게 피드백 문장을 늘리고, 약속은 말로만 하지 말고 기록하거나 리마인드를 설정하는 게 도움이 됩니다. 한 사람만 대화를 주도하지 않고 번갈아 주도권을 주는 것도 균형을 맞추는 데 효과적입니다. 서로 감정 상태를 체크하는 질문을 던지는 것만으로도 이해가 깊어질 수 있습니다. 짧은 대답보다는 한두 문장을 덧붙이는 연습이 필요하고, 작은 성공이나 좋은 소식을 자주 공유하는 것도 우정을 단단히 합니다. 대화 캐릭터들의 특성을 긍정적으로 인정하면서, 규칙적인 대화 시간(예: 금요일 근황 공유)을 마련하는 것도 좋습니다. 이런 습관을 쌓으면 우리 관계는 더 깊고 안정적인 우정으로 성장할 수 있을 거예요.",
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
    }
}
