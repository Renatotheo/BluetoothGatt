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
    private val REQUEST_BLUETOOTH_ADMIN_PERMISSION = 5
    private val devicesList = mutableListOf<BluetoothDeviceModel>()
    private val SCAN_TIMEOUT_MILLIS: Long = 5000 // 15 segundos
    private var isScanning = false
    private val handler = Handler()
    private lateinit var bluetoothManager: BluetoothManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "onCreate() chamado")
        initBluetoothAdapter()
        bluetoothManager = BluetoothManager(this,bluetoothAdapter)


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
            != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Solicitando permissão BLUETOOTH")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                REQUEST_BLUETOOTH_PERMISSION
            )
        } else {
            initRecyclerView()
            Log.d("MainActivity", "Permissão BLUETOOTH já concedida")
        }


        // Adiciona um temporizador para aguardar a inicialização do adaptador de layout
        Handler().postDelayed({
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            Log.d("MainActivity", "BluetoothAdapter: ${bluetoothAdapter}")
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                Log.d("MainActivity", "Bluetooth já está ativado")
                initRecyclerView()
            }
        }, 1000)
    }



    private fun initBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        Log.d("MainActivity", "BluetoothAdapter: $bluetoothAdapter")

        if (!bluetoothAdapter.isEnabled) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_ADMIN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                // Se a permissão BLUETOOTH_ADMIN não for concedida, solicite a permissão
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                    REQUEST_BLUETOOTH_ADMIN_PERMISSION
                )
            }
        } else {
            Log.d("MainActivity", "Bluetooth já está ativado")
            initRecyclerView()
        }
    }

    private fun initRecyclerView() {
        Log.d("MainActivity", "initRecyclerView()")
        recyclerView = findViewById(R.id.recyclerView)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
            == PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Permissão BLUETOOTH concedida")
            recyclerView.layoutManager = LinearLayoutManager(this)
            deviceListAdapter = DeviceListAdapter(this){ selectedDevice ->
                Log.d("MainActivity", "Dispositivo selecionado: $selectedDevice")
                connectToDevice(selectedDevice)
            }
            recyclerView.adapter = deviceListAdapter

            val connectButton: Button = findViewById(R.id.connectButton)
            connectButton.setOnClickListener {
                Log.d("MainActivity", "Botão Connect clicado")
                startBluetoothScan()
            }

            recyclerView.layoutManager = LinearLayoutManager(this)
        } else {
            Log.d("MainActivity", "Permissão BLUETOOTH não concedida")
        }
    }

    private fun connectToDevice(device: BluetoothDeviceModel) {
        Log.d("MainActivity", "connectToDevice chamado com dispositivo: $device")
        // Verifica se a permissão BLUETOOTH_CONNECT está concedida
        // Inicia a conexão GATT
        bluetoothManager.connectToDevice(device)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Inicia a conexão GATT
            //bluetoothManager.connectToDevice(device)
        } else {
            // Permissão não concedida, solicitar a permissão
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                REQUEST_BLUETOOTH_CONNECT_PERMISSION
            )
        }
    }

    private fun startBluetoothScan() {
        Log.d("MainActivity", "Iniciando varredura Bluetooth")

        //TRATAR AMANHA A LIMPEZA DA LISTA
        //deviceListAdapter.clearDevices()

       /* runOnUiThread {
            deviceListAdapter.clearDevices()
        }*/
        Log.d("MainActivity", "Lista de dispositivos limpa antes da varredura")

        // Adicione um log para verificar se a permissão ACCESS_FINE_LOCATION foi concedida
        Log.d("MainActivity", "Permissão ACCESS_FINE_LOCATION concedida: " +
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED))

        // Adicione um log para verificar se a permissão BLUETOOTH_ADMIN foi concedida
        Log.d("MainActivity", "Permissão BLUETOOTH_ADMIN concedida: " +
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                        == PackageManager.PERMISSION_GRANTED))

        // Adicione um log para verificar se a varredura está em andamento
        Log.d("MainActivity", "Varredura está em andamento: $isScanning")

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MainActivity", "Solicitando permissão ACCESS_FINE_LOCATION")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION_PERMISSION
            )
        } else {
            Log.d("MainActivity", "Permissão ACCESS_FINE_LOCATION concedida")
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                    REQUEST_BLUETOOTH_ADMIN_PERMISSION
                )
            } else {
                Log.d("MainActivity", "Permissão BLUETOOTH_ADMIN concedida")
                isScanning = true // Marca que a varredura está em andamento
                bluetoothAdapter.bluetoothLeScanner.startScan(scanCallback)

                // Adicione um log para verificar se a varredura foi iniciada
                Log.d("MainActivity", "Varredura Bluetooth iniciada com sucesso")

                // Adiciona um temporizador para parar a varredura após o tempo especificado
                handler.postDelayed({
                    stopBluetoothScan()
                }, SCAN_TIMEOUT_MILLIS)
            }
        }
    }

    private fun stopBluetoothScan() {
        // Para a varredura se estiver em andamento
        if (isScanning) {
            isScanning = false
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_ADMIN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
                Log.d("MainActivity", "Varredura Bluetooth parada após ${SCAN_TIMEOUT_MILLIS / 1000} segundos")
            } else {
                // Trate a falta de permissão BLUETOOTH_ADMIN conforme necessário
            }
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d("scanCallback", "onScanResult() chamado")
            val device = result.device
            val name = if (device.name.isNullOrEmpty()) "Nome Desconhecido" else device.name
            val bluetoothDeviceModel = BluetoothDeviceModel(name, device.address)
            Log.d("MainActivity", "BluetoothDeviceModel criado: $bluetoothDeviceModel")
           // Log.d("MainActivity", "Dispositivo encontrado: ${device.name} (${device.address})")
            Log.d("MainActivity", "Dispositivo encontrado: $name (${device.address})")

            runOnUiThread {
                Log.d("MainActivity2", "Adicionando dispositivo à lista: $bluetoothDeviceModel")
                deviceListAdapter.addDevice(bluetoothDeviceModel)
            }

            // Verifica se a permissão BLUETOOTH_CONNECT está concedida
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
            } else {
                // Permissão não concedida, solicitar a permissão
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    REQUEST_BLUETOOTH_CONNECT_PERMISSION
                )
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
                initRecyclerView()
                startBluetoothScan()
            } else {
                Log.d("MainActivity", "Permissão BLUETOOTH_SCAN negada.")
                val alertDialog = AlertDialog.Builder(this)
                    .setTitle("Permissão BLUETOOTH_SCAN")
                    .setMessage("A permissão BLUETOOTH_SCAN é necessária para pesquisar dispositivos Bluetooth. Deseja conceder a permissão?")
                    .setPositiveButton("Conceder") { _, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                            REQUEST_BLUETOOTH_SCAN_PERMISSION
                        )
                    }
                    .setNegativeButton("Não", null)
                    .create()

                alertDialog.show()
            }
        } else if (requestCode == REQUEST_FINE_LOCATION_PERMISSION) {

        }
    }

    companion object {
        const val REQUEST_BLUETOOTH_PERMISSION = 1
        const val REQUEST_ENABLE_BT = 2
    }
}

