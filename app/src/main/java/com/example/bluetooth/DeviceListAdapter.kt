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
import android.util.Log
import androidx.core.app.ActivityCompat

class DeviceListAdapter(private val context: Context, private val devicesList: MutableList<BluetoothDevice>) :
    RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devicesList[position]

        // Adicione mais declarações de registro aqui
        Log.d("DeviceListAdapter", "Device added to list: ${device.name} (${device.address})")

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        // Verifique se o dispositivo é único
        checkIfDeviceIsUnique(device)
        Log.d("DeviceListAdapter", "Device name before assigning: ${device.name}")
        holder.deviceName.text = device.name ?: "Unknown Device"
        holder.deviceAddress.text = device.address

        /*// Check for Bluetooth permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted, proceed with displaying device information
            holder.deviceName.text = device.name
            holder.deviceAddress.text = device.address
        } else {
            // Permission denied, handle the situation gracefully
            holder.deviceName.text = "Permission required"
            holder.deviceAddress.text = "To view device information, grant Bluetooth permission"

            // Consider requesting the permission here or providing a way for the user to grant it
        }*/

    }

    private fun checkIfDeviceIsUnique(device: BluetoothDevice) {
        if (devicesList.contains(device)) {
            Log.d("DeviceListAdapter", "Device already in list: ${device.name} (${device.address})")
        } else {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            Log.d("DeviceListAdapter", "Device is new: ${device.name} (${device.address})")
        }
    }

    fun addDevice(device: BluetoothDevice) {
        Log.d("DeviceListAdapter", "Trying to add device: ${device.name} (${device.address})")
        Log.d("DeviceListAdapter", "Is device in the list? ${devicesList.contains(device)}")
        if (!devicesList.contains(device)) {
            Log.d("DeviceListAdapter", "Device not in the list. Adding...")
            devicesList.add(device)
            notifyDataSetChanged()
            Log.d("DeviceListAdapter", "Device added1: ${device.name} (${device.address})")
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            Log.d("DeviceListAdapter", "Device added2: ${device.name} (${device.address})")
            notifyItemInserted(devicesList.size - 1)
            // Adicione este log para verificar se o dispositivo foi adicionado à lista
            Log.d("DeviceListAdapter", "Device successfully added: ${device.name} (${device.address})")
        }
    }

    override fun getItemCount(): Int {
        return devicesList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName: TextView = itemView.findViewById(R.id.deviceName)
        val deviceAddress: TextView = itemView.findViewById(R.id.deviceAddress)
    }
}
