package com.example.deteksi_cabai_kotlin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deteksi_cabai_kotlin.ui.theme.Poppins

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Bagian Atas: Tombol Kembali
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBack() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.kembali),
                    contentDescription = "Kembali",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("kembali",
                    style = TextStyle(
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bagian Tengah: Judul dan Deskripsi
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    "About",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    ),
                    modifier = Modifier.padding(vertical = 26.dp)
                )

                Text(
                    text = "Hai, ini adalah aplikasi klasifikasi penyakit pada tanaman cabai, dibuat untuk proyek Tugas Akhir developer. " +
                            "Developer harap aplikasi yang dibuat ini dapat bermanfaat untuk masyarakat luas.\n\n" +
                            "Terima Kasih!",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(60.dp))
                Text(
                    text = "~ Dev: Kresna Mahardhika",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Bagian Bawah: Logo dan Versi
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(60.dp)
                        .padding(bottom = 8.dp)
                )
                Text("Versi 1.0.0", style = TextStyle(
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ), color = Color.Gray)
            }
        }
    }
}

