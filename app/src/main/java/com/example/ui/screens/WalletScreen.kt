package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionRecord
import com.example.data.local.UserWallet
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RedeemCatalogItem(
    val name: String,
    val valueLabel: String,
    val coinCost: Long,
    val provider: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun WalletScreen(
    wallet: UserWallet,
    transactions: List<TransactionRecord>,
    onRequestRedeem: (optionName: String, cost: Long, targetAccount: String) -> Unit,
    onAddBonusCoins: (amount: Long, title: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val redeemOptions = listOf(
        RedeemCatalogItem("Google Pay (GPay)", "$5.00 USD", 5000L, "Direct to muhur2040@gmail.com", Icons.Default.Payment, AccentEmerald),
        RedeemCatalogItem("Google Pay (GPay)", "$10.00 USD", 9500L, "Direct to muhur2040@gmail.com (Save 500)", Icons.Default.Payment, AccentEmerald),
        RedeemCatalogItem("Google Pay (GPay)", "$25.00 USD", 23500L, "Direct to muhur2040@gmail.com (Save 1.5K)", Icons.Default.Payment, AccentEmerald),
        RedeemCatalogItem("Google Pay (GPay)", "$50.00 USD", 48000L, "Direct to muhur2040@gmail.com (Save 2K)", Icons.Default.Payment, AccentEmerald),
        RedeemCatalogItem("Google Pay (GPay)", "$100.00 USD", 95000L, "Direct to muhur2040@gmail.com (Save 5K)", Icons.Default.Payment, AccentEmerald),
        RedeemCatalogItem("Google Play Gift Card", "$5.00 USD", 5000L, "Google Play Digital Code", Icons.Default.SportsEsports, SecondaryCyan),
        RedeemCatalogItem("Google Play Gift Card", "$10.00 USD", 9500L, "Google Play (Save 500 Coins)", Icons.Default.SportsEsports, SecondaryCyan),
        RedeemCatalogItem("Google Play Gift Card", "$25.00 USD", 23500L, "Google Play (Save 1.5K Coins)", Icons.Default.SportsEsports, SecondaryCyan),
        RedeemCatalogItem("Google Play Gift Card", "$50.00 USD", 48000L, "Google Play (Save 2K Coins)", Icons.Default.SportsEsports, SecondaryCyan),
        RedeemCatalogItem("Google Play Gift Card", "$100.00 USD", 95000L, "Google Play (Save 5K Coins)", Icons.Default.SportsEsports, SecondaryCyan),
        RedeemCatalogItem("PayPal Cashout", "$5.00 USD", 5000L, "PayPal Instant Transfer", Icons.Default.Payment, PrimaryIndigo),
        RedeemCatalogItem("PayPal Cashout", "$10.00 USD", 9800L, "PayPal Instant (Save 200 Coins)", Icons.Default.Payment, PrimaryIndigo),
        RedeemCatalogItem("PayPal Cashout", "$20.00 USD", 19000L, "PayPal Instant (Save 1K Coins)", Icons.Default.Payment, PrimaryIndigo),
        RedeemCatalogItem("PayPal Cashout", "$50.00 USD", 48000L, "PayPal Instant (Save 2K Coins)", Icons.Default.Payment, PrimaryIndigo),
        RedeemCatalogItem("PayPal Cashout", "$100.00 USD", 95000L, "PayPal Instant (Save 5K Coins)", Icons.Default.Payment, PrimaryIndigo),
        RedeemCatalogItem("Amazon eGift Card", "$10.00 USD", 10000L, "Amazon Online Code", Icons.Default.CardGiftcard, AccentGold),
        RedeemCatalogItem("Amazon eGift Card", "$50.00 USD", 49000L, "Amazon Online (Save 1K)", Icons.Default.CardGiftcard, AccentGold),
        RedeemCatalogItem("Amazon eGift Card", "$100.00 USD", 96000L, "Amazon Online (Save 4K)", Icons.Default.CardGiftcard, AccentGold),
        RedeemCatalogItem("Steam Wallet Code", "$5.00 USD", 5000L, "Valve Steam Digital Key", Icons.Default.CardGiftcard, AccentRose),
        RedeemCatalogItem("Steam Wallet Code", "$25.00 USD", 24000L, "Valve Steam (Save 1K)", Icons.Default.CardGiftcard, AccentRose),
        RedeemCatalogItem("Apple Gift Card", "$10.00 USD", 10000L, "App Store & iTunes", Icons.Default.AccountBalanceWallet, AccentEmerald),
        RedeemCatalogItem("Game Diamonds / Pass", "$5.00 USD Value", 4500L, "FreeFire / PUBG / Roblox", Icons.Default.Diamond, AccentGold)
    )

    var selectedItemForRedeem by remember { mutableStateOf<RedeemCatalogItem?>(null) }
    var targetAccountInput by remember { mutableStateOf("muhur2040@gmail.com") }
    var filterTab by remember { mutableIntStateOf(0) } // 0: All, 1: Earned, 2: Redeemed

    val filteredTransactions = transactions.filter {
        when (filterTab) {
            1 -> it.amount > 0
            2 -> it.amount < 0
            else -> true
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("wallet_screen_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_balance_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryIndigo.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1E1B4B),
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AVAILABLE BALANCE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AccentGold.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "1,000 Coins ≈ $1.00",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Coins",
                                tint = AccentGold,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "%,d".format(wallet.coins),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "COINS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold
                            )
                        }

                        val usdEquivalent = wallet.coins / 1000.0
                        Text(
                            text = "≈ $%.2f USD equivalent".format(usdEquivalent),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SecondaryCyan
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.05f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Diamond,
                                        contentDescription = "Gems",
                                        tint = SecondaryCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "${wallet.gems}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Premium Gems",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.05f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Redeem,
                                        contentDescription = "Total",
                                        tint = AccentEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "%,d".format(wallet.totalEarnedCoins),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Total Earned",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Account & Payout Email Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_account_profile_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D30)),
                border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryCyan.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AccentEmerald, PrimaryIndigo))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = "Google Pay",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "GOOGLE PAY CONNECTED ADDRESS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentEmerald
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AccentEmerald.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "CONNECTED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AccentEmerald,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "muhur2040@gmail.com",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Active Google Pay (GPay) & USD Payout address",
                            fontSize = 11.sp,
                            color = SecondaryCyan
                        )
                    }
                }
            }
        }

        // 100 USD Accumulation Capacity & Persistence Protection Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_100usd_capacity_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF142033)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentGold.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Capacity",
                                tint = AccentGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "WALLET ACCUMULATION CAPACITY",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    color = AccentGold
                                )
                                Text(
                                    text = "Up to $100.00 USD (100,000 Points)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentEmerald.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentEmerald.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "1000po = 1USD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress toward $100 USD
                    val capacityProgress = (wallet.coins / 100000f).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { capacityProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = AccentGold,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Balance: %,d Coins (≈ $%.2f USD)".format(wallet.coins, wallet.coins / 1000.0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryCyan
                        )
                        Text(
                            text = "Max Goal: $100.00 USD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Crucial guarantee banner: Points NEVER disappear at $25!
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0D1726),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentEmerald.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Guaranteed",
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PERMANENT SAVING GUARANTEE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AccentEmerald
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Balance NEVER disappears or resets when reaching $25 USD!\n• All earned points are permanently preserved in Room DB + Flash backup.\n• You can accumulate continuously all the way to $100.00 USD (100,000 Points).\n• Standard conversion: 1,000 Points = $1.00 USD (1000po = 1USD).",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            onAddBonusCoins(200L, "Wallet Booster Bonus")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("save_bonus_points_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Bonus",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SAVE +200 BONUS POINTS TO WALLET",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Redemption Options Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "REDEEM REWARDS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Instant & Verified",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2-column or list of redeem options
        items(redeemOptions) { item ->
            val canAfford = wallet.coins >= item.coinCost

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedItemForRedeem = item }
                    .testTag("redeem_option_${item.name.replace(" ", "_")}"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (canAfford) item.accentColor.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(item.accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            tint = item.accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = item.provider,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.valueLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = item.accentColor
                        )
                    }

                    Button(
                        onClick = { selectedItemForRedeem = item },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) item.accentColor else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("redeem_btn_${item.coinCost}")
                    ) {
                        Text(
                            text = "%,d Coins".format(item.coinCost),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canAfford) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Transactions Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TRANSACTION HISTORY",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${filteredTransactions.size} Records",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Earned", "Redeemed").forEachIndexed { index, title ->
                        FilterChip(
                            selected = filterTab == index,
                            onClick = { filterTab = index },
                            label = { Text(title, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        if (filteredTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions found in this filter.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(filteredTransactions) { tx ->
                val isPositive = tx.amount >= 0
                val dateString = remember(tx.timestamp) {
                    SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(tx.timestamp))
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isPositive) AccentEmerald.copy(alpha = 0.2f)
                                    else AccentRose.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPositive) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (isPositive) AccentEmerald else AccentRose,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tx.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (tx.subtitle.isNotEmpty()) {
                                Text(
                                    text = tx.subtitle,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = dateString,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = (if (isPositive) "+" else "") + "%,d".format(tx.amount),
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = if (isPositive) AccentEmerald else AccentRose
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (tx.status == "COMPLETED") AccentEmerald.copy(alpha = 0.15f) else AccentGold.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = tx.status,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tx.status == "COMPLETED") AccentEmerald else AccentGold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Redemption Confirmation Dialog
    selectedItemForRedeem?.let { item ->
        val canAfford = wallet.coins >= item.coinCost

        AlertDialog(
            onDismissRequest = {
                selectedItemForRedeem = null
                targetAccountInput = ""
            },
            title = {
                Text(
                    text = "Redeem ${item.name}",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Value: ${item.valueLabel}",
                        fontWeight = FontWeight.Bold,
                        color = item.accentColor
                    )
                    Text(
                        text = "Cost: %,d Coins".format(item.coinCost),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!canAfford) {
                        Text(
                            text = "⚠️ You need %,d more coins to claim this reward. Play more games in the arcade to earn!".format(item.coinCost - wallet.coins),
                            color = AccentRose,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "Enter your Email or Gamer ID where reward code will be delivered:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = targetAccountInput,
                            onValueChange = { targetAccountInput = it },
                            placeholder = { Text("e.g. gamer@example.com") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("redeem_account_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = item.accentColor
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (canAfford && targetAccountInput.isNotBlank()) {
                            onRequestRedeem(item.name, item.coinCost, targetAccountInput.trim())
                            selectedItemForRedeem = null
                            targetAccountInput = ""
                        }
                    },
                    enabled = canAfford && targetAccountInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = item.accentColor),
                    modifier = Modifier.testTag("confirm_redeem_btn")
                ) {
                    Text("CONFIRM REDEEM", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedItemForRedeem = null
                    targetAccountInput = ""
                }) {
                    Text("CANCEL")
                }
            }
        )
    }
}
