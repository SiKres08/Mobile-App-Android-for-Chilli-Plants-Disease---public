package com.example.deteksi_cabai_kotlin

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.example.deteksi_cabai_kotlin.R
import com.example.deteksi_cabai_kotlin.ui.theme.Poppins
import android.text.Html

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val label = intent.getStringExtra("label") ?: "Tidak diketahui"
        val confidence = intent.getFloatExtra("confidence", 0f)
        val imageUri = intent.getStringExtra("imageUri")

        setContent {
            MaterialTheme {
                ResultScreen(label = label, confidence = confidence, imageUriString = imageUri)
            }
        }
    }
}

@Composable
fun ResultScreen(label: String, confidence: Float, imageUriString: String?) {
    val context = LocalContext.current

    val bitmap: Bitmap? = remember(imageUriString) {
        imageUriString?.let {
            try {
                val uri = Uri.parse(it)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE)
                        decoder.isMutableRequired = true
                    }
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                Log.e("ResultScreen", "Gagal decode gambar: ${e.message}")
                null
            }
        }
    }

    val scrollState = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            val activity = context as? ComponentActivity

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { activity?.finish() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kembali),
                    contentDescription = "Kembali",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "kembali",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Gambar Penyakit",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(240.dp)
                        .aspectRatio(3f / 4f)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            }

            Text(
                text = "Hasil Identifikasi:",
                fontSize = 18.sp,
                style = TextStyle(
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 24.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 36.sp,
                style = TextStyle(
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )

            Text(
                text = "Keyakinan: ${"%.2f".format(confidence * 1)}%",
                style = TextStyle(
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = Color(0xFFF64444)
            )


            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth())
            {
                Text(
                    text = "Ciri-ciri:",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = getCiriByLabel(label),
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Faktor Penyebab:",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = getPenyebabByLabel(label),
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Cara Penanganan:",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = getPenangananByLabel(label),
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Obat yang cocok:",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = getObatByLabel(label),
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                    )
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

fun getCiriByLabel(label: String): String {
    return when (label) {
        "Sehat" -> """
            • Daun berwarna hijau cerah atau agak gelap.
            • Daun runcing dan mekar.
            • Buah cerah dan licin.
        """.trimIndent()

        "Antraknosa" -> """
            • Buah cabai muncul bercak coklat kehitaman 
              yang membusuk melingkar (konsetrik).
            • Permukaan bercak bisa mengering atau 
              berlendir tergantung kelembapan.
            • Serangan berat membuat buah busuk dan gugur.
            • Infeksi lanjut membuat buah keriput, busuk, 
              dan rontok.
        """.trimIndent()

        "Virus Kuning" -> """
            • Daun muda menguning, menggulung ke atas, 
              dan kerdil.
            • Pertumbuhan tanaman lambat dan gagal 
              berbuah.
            • Banyak kutu kebul (whitefly) di sekitar tanaman.
            • Produksi bunga dan buah sangat berkurang atau 
              gagal panen.
            Peringatan!: Jika warna daun menguning namun tulang daun hijau, bisa jadi terkena Mosaic Virus
        """.trimIndent()

        "Bercak Daun" -> """
            • Daun menunjukkan bercak bulat/oval dengan 
              pusat coklat keabu-abuan dan tepi lebih gelap.
            • Pertumbuhan tanaman terhambat dan bunga 
              rontok.
            • Daun menguning, mengering, dan rontok jika 
              infeksi parah.
        """.trimIndent()

        "Keriting Daun" -> """
            • Daun muda menjadi keriting, kaku, dan menebal.
            • Tanaman menjadi kerdil atau terhambat, tidak 
              berbunga atau buah kecil.
            • Warna daun menjadi kusam, kadang muncul 
              mosaik (bercak hijau pucat & tua).
            • Serangga seperti thrips dan aphids sering 
              ditemukan.
        """.trimIndent()

        else -> "Ha? Ciri-ciri?."
    }
}

fun getPenyebabByLabel(label: String): String {
    return when (label) {
        "Sehat" -> "Tanah dalam kondisi yang baik dan tanaman terawat dengan baik."
        "Antraknosa" -> "Disebabkan oleh Jamur Colletotrichum capsici atau Colletotrichum gloeosporioides yang menyebar melalui air hujan , angin, dan alat pertanian yang terkontaminasi."
        "Virus Kuning" -> "Virus dari kelompok Begomovirus atau bisa juga dari kutu kebul (Bemisa tabaci)."
        "Bercak Daun" -> "Jamur Cercospora capsici atau Alternaria solani, dengan kelembapan tinggi dan drainase yang buruk dapat memicu infeksi dengan cepat."
        "Keriting Daun" -> "Virus seperti Potyvirus, atau dari serangga thrips yang ditularkan melalui gigitannya."
        else -> "Tidak ada penyebab"
    }
}

fun getPenangananByLabel(label: String): String {
    return when (label) {
        "Sehat" -> """
            • Rawat tanaman dengan baik.
            • Siram dengan air bersih setiap pagi dan 
              sore.
            • Rawat seperti anak sendiri.
        """.trimIndent()

        "Antraknosa" -> """
             
            Budidaya:
            • Gunakan benih tahan antraknosa atau yang 
              telah diberi perlakuan fungisida.
            • Gunakan mulsa plastik untuk mengurangi 
              kelembapan tanah.
            • Lakukan rotasi tanaman dengan non-inang 
              seperti  jagung atau kacang.
            • Lakukan rotasi tanaman dengan non-inang 
              seperti  jagung atau kacang.
            • Lakukan aplikasi agen pengendali hayati
              Tricoderma.
               
            Sanitasi:
            • Cabut dan bakar buah yang terinfeksi.
            • Bersihkan alat setelah digunakan di lahan 
              yang terpapar.
        """.trimIndent()

        "Virus Kuning" -> """
             
            Budidaya:
            • Tanam varietas dengan daya tahan tinggi 
              jika tersedia (contoh: varietas Bhaskara, 
              Lado F1).
            • Rotasi tanaman non-inang selama 2–3 musim 
              tanam.
               
            Sanitasi:
            • Cabut dan bakar buah yang terinfeksi.
            • Singkirkan gulma di sekitar kebun.
        """.trimIndent()

        "Bercak Daun" -> """
            • Pangkas daun bawah yang menunjukkan gejala.
            • Perbaiki jarak tanam agar tidak terlalu rapat.
            • Siram tanaman pagi hari agar daun sempat 
              kering sebelum malam.
            • Lakukan aplikasi agen pengendali hayati
              Tricoderma.
        """.trimIndent()

        "Keriting Daun" -> """
             
            Budidaya:
            • Tanam varietas dengan daya tahan tinggi 
              jika tersedia (contoh: varietas Bhaskara, 
              Lado F1).
            • Jaga jarak tanam dan perhatikan drainase.
             
            Sanitasi:
            • Cabut tanaman terserang berat.
            • Bersihkan gulma sebagai inang alternatif 
              virus dan thrips.
        """.trimIndent()

        else -> "Tidak perlu penanganan."
    }
}

fun getObatByLabel(label: String): String {
    return when (label) {
        "Sehat" -> """
            - 
        """.trimIndent()

        "Antraknosa" ->
            """ 
            Fungisida kontak:
            • Klorotalonil -- (2–3 gram/liter)
            • Mancozeb -- (2 gram/liter)
            • semprot tiap 5–7 hari setelah buah tumbuh
               
            Sanitasi:
            • Azoksistrobin -- (0,75–1 ml/liter)
            • Difenokonazol -- (1 ml/liter)
            • Rotasi agar tidak resisten
             
            Waktu Aplikasi:
            Saat buah mulai terbentuk dan musim hujan berlangsung
        """.trimIndent()

        "Virus Kuning" -> """
            Insektisida sistemik untuk kutu kebul:
            • Imidakloprid -- (0,5–1 ml/liter)
            • Tiametoksam -- (0,5–1 ml/liter)
            • Abamektin -- (1 ml/liter)
               
            Aplikasi:
            Sejak dini, ulangi tiap 5–7 hari tergantung populasi kutu kebul.
        """.trimIndent()

        "Bercak Daun" -> """
            Sanitasi:
            • Propineb -- (2 gram/liter)
            • Klorotalonil -- (2–3 gram/liter)
            • Difenokonazol -- (1 ml/liter)
             
            Waktu Aplikasi:
            Aplikasi tiap 5 hari, dan ulangi setelah hujan.
        """.trimIndent()

        "Keriting Daun" -> """
            Insektisida untuk thrips:
            • Spinetoram -- (1 ml/liter)
            • Emamektin benzoat -- <b>(1 g/liter)
            • Abamektin -- (1 ml/liter)
             
            Aplikasi:
            Aplikasi tiap 5 hari saat populasi tinggi.
        """.trimIndent()

        else -> "Tidak perlu obat."
    }
}