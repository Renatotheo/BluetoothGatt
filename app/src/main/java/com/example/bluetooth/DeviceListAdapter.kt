package com.example.bluetooth


import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat

class DeviceListAdapter(
    private val context: Context,
    private val onItemClick: (BluetoothDeviceModel) -> Unit) :
    RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    private val devicesList = mutableListOf<BluetoothDeviceModel>()
    //TRATAR AMANHA A LIMPEZA DA LISTA
    private val devices = mutableListOf<BluetoothDeviceModel>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName: TextView = itemView.findViewById(R.id.deviceName)
        val deviceAddress: TextView = itemView.findViewById(R.id.deviceAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_item, parent, false)
        Log.d("DeviceListAdapter", "onCreateViewHolder chamado")
        Log.d("DeviceListAdapter", "ViewHolder criado: $view")
        return ViewHolder(view)
    }

    //TRATAR AMANHA A LIMPEZA DA LISTA
    /*fun clearDevices() {
        Log.d("DeviceListAdapter", "Limpando lista de dispositivos")
        val size = devices.size
        devices.clear()

        val handler = Handler(Looper.getMainLooper())

        // Notifica o adaptador sobre a remoção dos itens
        handler.post {
            notifyItemRangeRemoved(0, size)
        }
        Log.d("DeviceListAdapter", "Lista de dispositivos limpa")
    }*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devicesList[position]
        Log.d("DeviceListAdapter", "onBindViewHolder chamado para posição: $position")
        Log.d("DeviceListAdapter", "Dispositivo da posição: $device")
        holder.deviceName.text = device.name ?: "Unknown Device"
        holder.deviceAddress.text = device.address
        Log.d("DeviceListAdapter", "ViewHolder - Nome: ${device.name}, Endereço: ${device.address}")

        holder.itemView.setOnClickListener {
            onItemClick.invoke(device)
        }
    }


    override fun getItemCount(): Int {
        Log.d("DeviceListAdapter", "getItemCount chamado")
        Log.d("DeviceListAdapter", "Quantidade de dispositivos na lista: ${devicesList.size}")
        return devicesList.size
    }

    /*fun addDevice(device: BluetoothDeviceModel) {
        notifyDataSetChanged()
        if (!devicesList.contains(device)) {
            devicesList.add(device)
            notifyDataSetChanged()
            Log.d("DeviceListAdapter", "Novo dispositivo adicionado - Nome: ${device.name}, Endereço: ${device.address}")
        }
    }*/

    fun addDevice(device: BluetoothDeviceModel) {
        Log.d("DeviceListAdapter", "addDevice chamado")
        Log.d("DeviceListAdapter", "Dispositivo a ser adicionado: $device")
        if (!devicesList.contains(device)) {
            Log.d("DeviceListAdapter", "Dispositivo não está na lista, adicionando...")
            devicesList.add(device)
            notifyItemInserted(devicesList.size - 1) // Notifica o adaptador sobre o novo item
           // notifyDataSetChanged()
            Log.d("DeviceListAdapter", "Novo dispositivo adicionado - Nome: ${device.name}, Endereço: ${device.address}")
            Log.d("DeviceListAdapter", "Quantidade de dispositivos na lista: ${devicesList.size}")
        }else {
            Log.d("DeviceListAdapter", "Dispositivo já está na lista, ignorando.")
        }
    }

}
