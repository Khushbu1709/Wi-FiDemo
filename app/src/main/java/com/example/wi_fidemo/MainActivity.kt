package com.example.wi_fidemo

import android.annotation.SuppressLint
import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.*
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wi_fidemo.adapter.WifiAdapter
import com.example.wi_fidemo.databinding.ActivityMainBinding
import com.example.wi_fidemo.model.wifiModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var isConnected: Boolean = false
    lateinit var wifiManager: WifiManager
    lateinit var wifiList: ArrayList<wifiModel>
    var wifiAdapter: WifiAdapter? = null
    var currentPosition: Int = 0

    companion object {
        var TAG: String = MainActivity::class.java.simpleName
    }

    private var wifiScanBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        var sb: StringBuilder? = null

        @SuppressLint("MissingPermission", "NewApi")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION == action) {
                sb = java.lang.StringBuilder()

                val wiFiScan: List<ScanResult> = wifiManager.scanResults
                val wifiInfo = wifiManager.connectionInfo;
                val frequency = wifiInfo.frequency

                for (scanResult in wiFiScan) {
                    sb!!.append("\n").append(scanResult.SSID).append(" _ ")
                        .append(scanResult.capabilities)
                    wifiList.add(wifiModel(scanResult.SSID, scanResult.level.toString()))
                    Log.d(TAG, "data---->${frequency}")
                }
                binding.progressbar.visibility = View.INVISIBLE

                val layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                wifiAdapter = WifiAdapter(this@MainActivity, wifiList) { position ->
                    currentPosition = position

                    Log.d(TAG, "devicename---->${wifiList[position].name}")

                    val dialogBuilder = AlertDialog.Builder(this@MainActivity)
                    val inflater: LayoutInflater = layoutInflater
                    val dialogView: View = inflater.inflate(R.layout.item_dialog, null)
                    dialogBuilder.setView(dialogView)
                    val deviceName = dialogView.findViewById<View>(R.id.txtDeviceName) as TextView
                    deviceName.text = wifiList[position].name
                    var editText = dialogView.findViewById<EditText>(R.id.edtPass) as EditText

                    dialogBuilder.setPositiveButton(R.string.connect) { dialog, _ ->
                        var password = editText.text.toString()

                        connect(password, wifiList[position].name)
                    }


                    dialogBuilder.setNegativeButton(
                        R.string.cancel
                    ) { _, _ ->
                        Toast.makeText(this@MainActivity, "Not Connected", Toast.LENGTH_SHORT)
                            .show()
                    }

                    dialogBuilder.setCancelable(false)
                    val alertDialog = dialogBuilder.create()
                    alertDialog.show()

                }
                binding.recyclerView.layoutManager = layoutManager
                binding.recyclerView.adapter = wifiAdapter
            }

        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        wifiManager = getSystemService(WIFI_SERVICE) as WifiManager
        wifiList = arrayListOf()


        if (wifiManager.isWifiEnabled) {
            binding.imageView.setImageResource(R.drawable.wificonnected)
            binding.textNetworkConnection.text = getString(R.string.wi_fi_connect)
        } else if (!wifiManager.isWifiEnabled) {
            wifiList.clear()
            binding.imageView.setImageResource(R.drawable.wifi)
            binding.textNetworkConnection.text = getString(R.string.wi_fi_disconnect)

        }

        binding.imageView.setOnClickListener {
            if (wifiManager.isWifiEnabled) {
                wifiList.clear()
                binding.imageView.setImageResource(R.drawable.wifi)
                binding.textNetworkConnection.text = getString(R.string.wi_fi_disconnect)
                if (VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
                    startActivity(panelIntent)
                } else {
                    wifiManager.isWifiEnabled = false
                }
            } else if (!wifiManager.isWifiEnabled) {
                binding.imageView.setImageResource(R.drawable.wificonnected)
                binding.textNetworkConnection.text = getString(R.string.wi_fi_connect)
                if (VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
                    startActivity(panelIntent)
                } else {
                    wifiManager.isWifiEnabled = true
                }
            }
        }

        binding.txtScanWiFi.setOnClickListener {
            if (wifiManager.isWifiEnabled) {
                binding.progressbar.visibility = View.VISIBLE
                binding.imageView.visibility = View.INVISIBLE
                binding.textNetworkConnection.visibility = View.INVISIBLE
               wifiManager.startScan()
                val intentFilter = IntentFilter()
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                registerReceiver(wifiScanBroadcastReceiver, intentFilter)
                Log.d(TAG, "register")

            } else {
                binding.recyclerView.setHasFixedSize(true)
                binding.progressbar.visibility = View.INVISIBLE
                binding.imageView.setImageResource(R.drawable.wifi)
                wifiList.clear()
                binding.textNetworkConnection.text = getString(R.string.wi_fi_disconnect)
                binding.imageView.visibility = View.VISIBLE
                binding.textNetworkConnection.visibility = View.VISIBLE
                Toast.makeText(this, "Please wi-fi enable", Toast.LENGTH_SHORT).show()
            }
        }
        wifiAdapter?.notifyItemInserted(currentPosition)

    }

    override fun onPostResume() {
        super.onPostResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanBroadcastReceiver, intentFilter)
        Log.d(TAG, "register")
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(wifiScanBroadcastReceiver)
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.Q)
    fun connect(pass: String, name: String) {

        val networkSSID = "Team_KR_2.4G"
        val password = "T#@mKR@1234"

        if (VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
                .setSsid(name)
                .setWpa2Passphrase(pass)
                .build()

            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(wifiNetworkSpecifier)
                .build()

            val connectivityManager =
                applicationContext!!.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.requestNetwork(
                networkRequest,
                ConnectivityManager.NetworkCallback()
            )
        } else {

            val conf = WifiConfiguration()
            conf.SSID = "\"" + name + "\""
            conf.wepKeys[0] = "\"" + pass + "\"";
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

            conf.preSharedKey = "\"" + pass + "\"";
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiManager.addNetwork(conf)


            val list = wifiManager.configuredNetworks
            for (i in list) {
                if (i.SSID != null && i.SSID == "\"" + name + "\"") {
//                     wifiManager.disconnect()
                    wifiManager.enableNetwork(i.networkId, true)
                    wifiManager.reconnect()
                    break

                }
            }

        }
    }
}


