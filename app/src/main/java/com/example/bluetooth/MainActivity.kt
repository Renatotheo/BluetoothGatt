package com.example.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView




class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var deviceListAdapter: DeviceListAdapter
    private val REQUEST_BLUETOOTH_SCAN_PERMISSION = 3
    private val REQUEST_BLUETOOTH_CONNECT_PERMISSION = 4
    private val REQUEST_FINE_LOCATION_PERMISSION = 1
    private val devicesList = mutableListOf<BluetoothDevice>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Solicita permissão para Bluetooth
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Solicitando permissão BLUETOOTH")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH),
                REQUEST_BLUETOOTH_PERMISSION
            )
        }else {
            initRecyclerView()
            Log.d("MainActivity", "Permissão BLUETOOTH já concedida")
        }

        // Adiciona um temporizador para aguardar a inicialização do adaptador de layout
        Handler().postDelayed({
            // Verifica se o Bluetooth está ativado
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            Log.d("MainActivity", "BluetoothAdapter: ${bluetoothAdapter}")
            if (!bluetoothAdapter.isEnabled) {
                // Solicita a ativação do Bluetooth
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                // Bluetooth já está ativado, inicializa o RecyclerView e o adaptador

            }
        }, 1000)
    }

    private fun initRecyclerView() {
        Log.d("MainActivity", "initRecyclerView()")
        recyclerView = findViewById(R.id.recyclerView)

        // Verifique se a permissão BLUETOOTH foi concedida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
            == PackageManager.PERMISSION_GRANTED
        ) {
            recyclerView.layoutManager = LinearLayoutManager(this)
            // Permissão concedida, inicialize o adaptador
            deviceListAdapter = DeviceListAdapter(this, devicesList)
            recyclerView.adapter = deviceListAdapter

            val connectButton: Button = findViewById(R.id.connectButton)
            connectButton.setOnClickListener {
                Log.d("scanCallback", "Botão clicado")
                startBluetoothScan()
            }

            // Configure o LayoutManager após a inicialização do adaptador
            recyclerView.layoutManager = LinearLayoutManager(this)
        } else {
            // Permissão negada, você pode exibir um aviso ao usuário
            Log.d("MainActivity", "Permissão BLUETOOTH não concedida")
        }
    }



    private fun startBluetoothScan() {
        Log.d("MainActivity", "Iniciando varredura Bluetooth")

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicite a permissão ACCESS_FINE_LOCATION
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION_PERMISSION
            )
        } else {
            // ...
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MainActivity", "Permissão BLUETOOTH_SCAN não concedida. Solicitando permissão...")

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                REQUEST_BLUETOOTH_SCAN_PERMISSION
            )
        } else {
            Log.d("MainActivity", "Permissão BLUETOOTH_SCAN concedida, iniciando varredura...")
            bluetoothAdapter.bluetoothLeScanner.startScan(scanCallback)
        }

    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d("scanCallback", "Dispositivo encontrado")
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Solicitar a permissão BLUETOOTH_CONNECT
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    REQUEST_BLUETOOTH_CONNECT_PERMISSION
                )
            } else {
                // Permissão concedida, acessar o nome e o endereço
                val device = result.device
                Log.d("MainActivity", "Found device: ${device.name} (${device.address})")

                // Verifique se a lista de dispositivos não está vazia
                if (devicesList.isNotEmpty()) {
                    deviceListAdapter.addDevice(device)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("scanCallback", "Erro no scan: $errorCode")

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_BLUETOOTH_SCAN_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
                Log.d("MainActivity", "Permissão BLUETOOTH_SCAN concedida, iniciando varredura...")
                initRecyclerView()
                startBluetoothScan()
            } else {
                Log.d("MainActivity", "Permissão BLUETOOTH_SCAN negada.")
                // Exibir um aviso ao usuário
                val alertDialog = AlertDialog.Builder(this)
                    .setTitle("Permissão BLUETOOTH_SCAN")
                    .setMessage("A permissão BLUETOOTH_SCAN é necessária para que o aplicativo pesquise por dispositivos Bluetooth. Deseja conceder a permissão?")
                    .setPositiveButton("Conceder", DialogInterface.OnClickListener { dialog, which ->
                        // Solicitar a permissão novamente
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                            REQUEST_BLUETOOTH_SCAN_PERMISSION
                        )
                    })
                    .setNegativeButton("Não", null)
                    .create()

                alertDialog.show()
            }
        }else if (requestCode == REQUEST_FINE_LOCATION_PERMISSION) {
            // ... (código já presente no seu código)
        }
    }

    companion object {
        const val REQUEST_BLUETOOTH_PERMISSION = 1
        const val REQUEST_ENABLE_BT = 2
    }
}
