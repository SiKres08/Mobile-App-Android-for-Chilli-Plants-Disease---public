package com.example.deteksi_cabai_kotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.core.content.FileProvider
import com.example.deteksi_cabai_kotlin.ui.theme.Poppins
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    lateinit var interpreter: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            interpreter = Interpreter(loadModelFile())
        } catch (e: Exception) {
            Log.e("TFLite", "Error loading model: ${e.message}")
        }

        setContent {
            MyApp(interpreter)
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = assets.openFd("mobilenetv3_cabai.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength)
        inputStream.close()
        return mappedByteBuffer
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        composable("home") { ComponentActivity() }
        composable("about") { AboutScreen(onBack = { navController.popBackStack() }) }
    }
}

@Composable
fun LanguageDropdownModern(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .width(70.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(Color.White)
            .border(
                BorderStroke(2.dp, Color.Black),
                shape = RoundedCornerShape(25.dp)
            )
            .clickable { expanded = !expanded }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedLanguage,
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = Color.Black
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(74.dp)
                .background(Color.White, RoundedCornerShape(20.dp))
                .border(
                    BorderStroke(2.dp, Color.Black),
                    shape = RoundedCornerShape(20.dp)
                ),
            offset = DpOffset(x = (-12).dp, y = 6.dp)
        )
        {
            listOf("Ind", "Eng").forEach { language ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = language,
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    onClick = {
                        if (language == "Eng") {
                            Toast.makeText(context, "Bahasa belum tersedia", Toast.LENGTH_SHORT).show()
                        } else {
                            onLanguageSelected(language)
                        }
                        expanded = false
                    },
                    modifier = Modifier
                        .height(32.dp)
                        .padding(horizontal = 14.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CustomMenuOverlay(
    showMenu: Boolean,
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        if (showMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
                    .clickable(onClick = onDismiss)
            )
        }

        AnimatedVisibility(
            visible = showMenu,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it }),
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Card(
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                elevation = CardDefaults.cardElevation(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        "Menu",
                        style = TextStyle(
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                        ),
                        modifier = Modifier.padding(
                            start = 68.dp,
                            top = 8.dp
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    MenuItemRow(
                        icon = Icons.Default.Home,
                        text = "Beranda",
                        onClick = { onNavigate("home") }
                    )

                    MenuItemRow(
                        icon = Icons.Default.Info,
                        text = "Tentang",
                        onClick = { onNavigate("about") }
                    )

                    MenuItemRow(
                        icon = Icons.Default.ExitToApp,
                        text = "Keluar",
                        onClick = { (context as? Activity)?.finishAffinity() }
                    )
                    // Spacer untuk mendorong ke bawah
                    Spacer(modifier = Modifier.weight(1f))

                    // Logo dan versi aplikasi
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo), // Ganti dengan logo kamu
                            contentDescription = "Logo Aplikasi",
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Versi 1.0.0",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItemRow(icon: ImageVector, text: String, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                ),
            )
        }
        Divider(color = Color.LightGray, thickness = 1.dp)
    }
}

@Composable
fun MyApp(interpreter: Interpreter) {
    val navController = rememberNavController()
    var showMenu by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Load logo.png dari assets
    val Bitmap = remember { loadBitmapFromAssets(context, "image 3.png") }
    val LogoBitmap = remember { loadBitmapFromAssets(context, "cekcabe.png") }
    val MenuIcon = remember { loadBitmapFromAssets(context, "menu.png") }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Izin kamera diperlukan!", Toast.LENGTH_SHORT).show()
        }
    }

    val imageFile = remember {
        File(context.cacheDir, "captured_image.jpg").apply { createNewFile() }
    }
    val imageUriCamera = remember {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val bitmap = getBitmapFromUri(context, imageUriCamera)
            bitmap?.let {
                val result = classifyImage(interpreter, it)
                Toast.makeText(context, "Analisis selesai dalam waktu: ${result.third} ms", Toast.LENGTH_SHORT).show()
                navigateToResult(context, result.first, result.second, imageUriCamera)
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = getBitmapFromUri(context, it)
            bitmap?.let { bmp ->
                val result = classifyImage(interpreter, bmp)
                Toast.makeText(context, "Analisis selesai dalam waktu: ${result.third} ms", Toast.LENGTH_SHORT).show()
                navigateToResult(context, result.first, result.second, it)
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            var selectedLang by remember { mutableStateOf("Ind") }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MenuIcon?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "ikon menu",
                        modifier = Modifier
                         .height(32.dp)
                         .clickable {
                             showMenu = true
                         }
                    )
                }
                LanguageDropdownModern(
                    selectedLanguage = selectedLang,
                    onLanguageSelected = { selectedLang = it }
                )
            }

            LogoBitmap?.let { bmp ->
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Logo Aplikasi",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))


            Text("Identifikasi Penyakit",
                style = TextStyle(
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            )
            Text("Tanaman Cabai",
                style = TextStyle(
                    fontFamily = Poppins,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp
                )
            )

            Spacer(modifier = Modifier.height(30.dp)) // ganti ke 8.dp misalnya


            Bitmap?.let { bmp ->
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Gambar Cabe",
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Identifikasi sekarang!",
                style = TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
                )
            )
            Text("Pilih gambar dari galeri atau ambil foto.",
                style = TextStyle(
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(42.dp))

            Button(
                onClick = {
                    requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    cameraLauncher.launch(imageUriCamera)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF64444)),
                shape = RoundedCornerShape(25.dp),
                contentPadding = PaddingValues(12.dp) // Padding dalam tombol (bukan margin luar)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Ikon dalam lingkaran
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(Color(0xFFFF8A80), shape = RoundedCornerShape(15.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Teks rata kiri
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Ambil Foto",
                            color = Color.White,
                            style = TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Medium,
                                fontSize = 24.sp
                            )
                        )
                        Text(
                            text = "tanaman cabai",
                            color = Color(0xFFA12626),
                            style = TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }

            Button(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF64444)),
                shape = RoundedCornerShape(25.dp),
                contentPadding = PaddingValues(12.dp) // Padding dalam tombol (bukan margin luar)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Ikon dalam lingkaran
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(Color(0xFFFF8A80), shape = RoundedCornerShape(15.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = "Upload",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Teks rata kiri
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Unggah Gambar",
                            color = Color.White,
                            style = TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Medium,
                                fontSize = 24.sp
                            )
                        )
                        Text(
                            text = "dari galeri",
                            color = Color(0xFFA12626),
                            style = TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
        CustomMenuOverlay(
            showMenu = showMenu,
            onDismiss = { showMenu = false },
            onNavigate = { route ->
                 showMenu = false
                 navController.navigate(route)
                // Handle navigasi atau aksi berdasarkan menuItem
                // Toast.makeText(context, "Klik: $menuItem", Toast.LENGTH_SHORT).show()
            }
        )
        AppNavHost(navController = navController)
    }
}

fun loadBitmapFromAssets(context: Context, fileName: String): Bitmap? {
    return try {
        context.assets.open(fileName).use { inputStream ->
            android.graphics.BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        Log.e("Assets", "Load image error: ${e.message}")
        null
    }
}

private fun navigateToResult(context: Context, label: String, confidence: Float, imageUri: Uri?) {
    val intent = Intent(context, ResultActivity::class.java).apply {
        putExtra("label", label)
        putExtra("confidence", confidence)
        putExtra("imageUri", imageUri.toString()) // Kirim URI sebagai string
    }
    context.startActivity(intent)
}

private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE)
                decoder.isMutableRequired = true
            }
        } else {
            android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        Log.e("ImageDecoder", "Error decoding image: ${e.message}")
        null
    }
}

private fun classifyImage(interpreter: Interpreter, bitmap: Bitmap): Triple<String, Float, Long> {
    val labels = listOf("Sehat", "Antraknosa", "Virus Kuning", "Bercak Daun", "Keriting Daun")

    return try {
        val inputBuffer = convertBitmapToByteBuffer(bitmap)
        val outputBuffer = Array(1) { FloatArray(labels.size) }

        val startTime = System.nanoTime()

        interpreter.run(inputBuffer, outputBuffer)

        val endTime = System.nanoTime() // ⏱️ Selesai timing
        val inferenceTimeMs = (endTime - startTime) / 1_000_000 // Konversi ke ms

        val probabilities = outputBuffer[0]
        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        val confidence = if (maxIndex >= 0) probabilities[maxIndex] * 100f else 0f
        val predictedClass = if (maxIndex >= 0) labels[maxIndex] else "Unknown"
        Triple(predictedClass, confidence, inferenceTimeMs)
    } catch (e: Exception) {
        Log.e("TFLite", "Error during classification: ${e.message}")
        Triple("Error", 0.0f, 0L)
    }
}

private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
    val inputSize = 224
    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
    val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
    byteBuffer.order(ByteOrder.nativeOrder())
    for (y in 0 until inputSize) {
        for (x in 0 until inputSize) {
            val pixel = resizedBitmap.getPixel(x, y)
            byteBuffer.putFloat(android.graphics.Color.red(pixel).toFloat())
            byteBuffer.putFloat(android.graphics.Color.green(pixel).toFloat())
            byteBuffer.putFloat(android.graphics.Color.blue(pixel).toFloat())
        }
    }
    return byteBuffer
}
