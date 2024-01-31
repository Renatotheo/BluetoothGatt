package com.example.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import androidx.core.app.ActivityCompat
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast

class BluetoothManager(private val context: Context,private val bluetoothAdapter: BluetoothAdapter) {


    private val bluetoothManager: android.bluetooth.BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager


    private var bluetoothGatt: BluetoothGatt? = null

    fun connectToDevice(device: BluetoothDeviceModel) {
        Log.d("BluetoothManager", "connectToDevice chamado com dispositivo: $device")


        if (device == null) {
            Log.e("BluetoothManager", "Dispositivo nulo. Não é possível conectar.")
            return
        }
        // Verifica se a permissão BLUETOOTH_CONNECT está concedida
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val bluetoothDevice = bluetoothAdapter.getRemoteDevice(device.address)
            Log.d("BluetoothManager", "Tentando conectar a: ${bluetoothDevice.name} (${bluetoothDevice.address})")
            bluetoothGatt = bluetoothDevice.connectGatt(
                context, false, gattCallback, BluetoothDevice.TRANSPORT_LE
            )
            if (bluetoothGatt == null) {
                Log.e("BluetoothManager", "Falha ao iniciar a conexão GATT.")
            } else {
                Log.d("BluetoothManager", "Conexão GATT iniciada com sucesso.")
            }
        } else {
            // Permissão não concedida, solicitar a permissão
            Log.e("BluetoothManager", "Permissão BLUETOOTH_CONNECT não concedida. Solicitando permissão.")
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                REQUEST_BLUETOOTH_CONNECT_PERMISSION
            )
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt?,
            status: Int,
            newState: Int
        ) {
            super.onConnectionStateChange(gatt, status, newState)

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d("BluetoothManager", "Conectado ao dispositivo: ${gatt?.device?.address}")
                    (context as? Activity)?.runOnUiThread {
                        Toast.makeText(context, "Conexão estabelecida", Toast.LENGTH_SHORT).show()
                    }

                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d("BluetoothManager", "Desconectado do dispositivo: ${gatt?.device?.address}")


                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BluetoothManager", "Descoberta de serviços concluída com sucesso.")
            } else {
                Log.e("BluetoothManager", "Falha na descoberta de serviços. Status: $status")
            }
        }

        // Outros métodos de callback para gerenciar a comunicação GATT
    }

    companion object {
        const val REQUEST_BLUETOOTH_CONNECT_PERMISSION = 1001
    }
}
