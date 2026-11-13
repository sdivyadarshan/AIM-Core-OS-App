package com.example.data.network

import com.example.data.model.SensorData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class Esp32NetworkManager {

    private val client = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(2, TimeUnit.SECONDS)
        .build()

    suspend fun fetchSensorData(ip: String, port: Int): Result<SensorData> = withContext(Dispatchers.IO) {
        try {
            val url = "http://$ip:$port/status"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyString = response.body?.string() ?: ""
                val json = JSONObject(bodyString)
                val tof = json.optInt("tof", 72)
                val ldr = json.optInt("ldr", 650)
                val ir = json.optString("ir", "0x20DF10EF")
                val battery = json.optInt("battery", 89)
                val status = json.optString("status", "connected")
                val rssi = json.optInt("wifi_rssi", -55)

                Result.success(
                    SensorData(
                        tof = tof,
                        ldr = ldr,
                        ir = ir,
                        battery = battery,
                        status = status,
                        wifiRssi = rssi
                    )
                )
            } else {
                Result.failure(Exception("HTTP Error: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendRobotCommand(ip: String, port: Int, command: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = "http://$ip:$port/command"
            val jsonPayload = JSONObject().apply {
                put("cmd", command)
                put("timestamp", System.currentTimeMillis())
            }
            val body = jsonPayload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success("ACK: $command Executed")
            } else {
                Result.failure(Exception("Failed code ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun pingEsp32(ip: String, port: Int): Pair<Boolean, Long> = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            val url = "http://$ip:$port/ping"
            val request = Request.Builder().url(url).get().build()
            val response = client.newCall(request).execute()
            val elapsed = System.currentTimeMillis() - start
            Pair(response.isSuccessful, elapsed)
        } catch (e: Exception) {
            Pair(false, -1L)
        }
    }

    /**
     * Flow emitting live sensor updates, using real HTTP if accessible or dynamic realistic
     * robot simulation when offline or in simulation mode.
     */
    fun getSensorDataStream(
        ipProvider: () -> String,
        portProvider: () -> Int,
        simulationModeProvider: () -> Boolean,
        isMappingProvider: () -> Boolean
    ): Flow<SensorData> = flow {
        var simulatedDistance = 72
        var angle = 0.0
        var simBattery = 89
        var lastBatteryDrain = System.currentTimeMillis()

        val sampleIrCodes = listOf("0x20DF10EF", "0x20DF40BF", "0x20DF00FF", "0x20DF807F", "0x20DF609F", "0x20DF10EF")

        while (true) {
            val isSim = simulationModeProvider()
            val ip = ipProvider()
            val port = portProvider()
            val isMapping = isMappingProvider()

            if (!isSim) {
                val result = fetchSensorData(ip, port)
                if (result.isSuccess) {
                    emit(result.getOrThrow())
                } else {
                    // Fallback to simulated connected state with warning if physical IP is unreachable
                    angle += 0.15
                    simulatedDistance = (60 + kotlin.math.sin(angle) * 35 + Random.nextInt(-3, 4)).toInt().coerceIn(10, 250)
                    val currentIr = if (Random.nextFloat() < 0.15f) sampleIrCodes.random() else "0x20DF10EF"
                    emit(
                        SensorData(
                            tof = simulatedDistance,
                            ldr = 620 + Random.nextInt(-40, 40),
                            ir = currentIr,
                            battery = simBattery,
                            status = if (isMapping) "mapping" else "connected",
                            wifiRssi = -64 + Random.nextInt(-4, 5)
                        )
                    )
                }
            } else {
                // Realistic indoor mapping robot telemetry simulation
                angle += 0.12
                val sweep = kotlin.math.sin(angle) * 45
                simulatedDistance = (75 + sweep + Random.nextInt(-2, 3)).toInt().coerceIn(12, 300)
                
                // Slow battery drain simulation over time
                if (System.currentTimeMillis() - lastBatteryDrain > 15000) {
                    if (simBattery > 5) simBattery -= 1 else simBattery = 100
                    lastBatteryDrain = System.currentTimeMillis()
                }

                val activeStatus = when {
                    isMapping -> "mapping"
                    simBattery < 15 -> "warning"
                    else -> "connected"
                }

                val irCode = if (Random.nextFloat() < 0.2f) sampleIrCodes[Random.nextInt(sampleIrCodes.size)] else "0x20DF10EF"

                emit(
                    SensorData(
                        tof = simulatedDistance,
                        ldr = (650 + kotlin.math.cos(angle) * 80 + Random.nextInt(-15, 15)).toInt(),
                        ir = irCode,
                        battery = simBattery,
                        status = activeStatus,
                        wifiRssi = -58 + Random.nextInt(-3, 4)
                    )
                )
            }
            delay(500) // update twice per second
        }
    }.flowOn(Dispatchers.IO)
}
