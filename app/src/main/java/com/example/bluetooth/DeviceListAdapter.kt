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

class DeviceListAdapter(private val context: Context, private val devicesList: MutableList<BluetoothDevice>) :
    RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devicesList[position]

        // Check for Bluetooth permission
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
        }
    }

    fun addDevice(device: BluetoothDevice) {
        if (!devicesList.contains(device)) {
            devicesList.add(device)
            notifyDataSetChanged()
            notifyItemInserted(devicesList.size - 1)
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
