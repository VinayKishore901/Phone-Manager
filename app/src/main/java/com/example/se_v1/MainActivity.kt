package com.example.se_v1

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*


var t : String = ""
var pass = "default"
var cols = listOf<String>(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone._ID
).toTypedArray()

lateinit var player : MediaPlayer
lateinit var locationManager: LocationManager
private var locationGps: Location? = null
private  var locationNetwork: Location? = null
private var hasGps = false
private var hasNetwork = false
lateinit var c : Context
var pswd: String =""


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreference = getSharedPreferences("numbers",0)
        val number1 = sharedPreference.getString("number1","username")
        val number2 = sharedPreference.getString("number2","password")

        tv1.hint = number1
        tv2.hint = number2
        pswd = number2.toString()



        c=this
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS,Manifest.permission.WAKE_LOCK,Manifest.permission.SEND_SMS,Manifest.permission.READ_CONTACTS,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_NOTIFICATION_POLICY,Manifest.permission.WAKE_LOCK),111)

        }
        else {
            receiveMsg()
        }

6
        val notificationManager: NotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && !notificationManager.isNotificationPolicyAccessGranted()
        ) {
            val intent = Intent(
                Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
            )
            startActivity(intent)
        }



        btn.setOnClickListener(View.OnClickListener {
            val mPrefs = getSharedPreferences("numbers", 0)
            val editor = mPrefs.edit()
            editor.putString("number1",tv1.text.toString())
            editor.putString("number2",tv2.text.toString())
            editor.commit()
            Toast.makeText(this , "Data Saved", Toast.LENGTH_SHORT).show()

        })

        htw.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,MainActivity2::class.java)
            startActivity(intent)
        })













        if(t!=""){
            Toast.makeText(this,t,Toast.LENGTH_LONG).show()
            if(t=="1"){
                Toast.makeText(this,"firstcase",Toast.LENGTH_LONG).show()
            }
        }



    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            try {
                receiveMsg()
                throw Exception()
            }
            finally {
                Toast.makeText(this@MainActivity,"",Toast.LENGTH_LONG).show()
            }
    }

    fun sendlocation(phonenumber: String, currentlocation: Location){
        var smsManager = SmsManager.getDefault()
        var smsBody = StringBuffer()
        smsBody.append("http://maps.google.com/maps?q=")
        smsBody.append(currentlocation.latitude)
        smsBody.append(",")
        smsBody.append(currentlocation.longitude)
        smsManager.sendTextMessage(phonenumber,null,smsBody.toString(),null,null)


    }



    private fun receiveMsg() {
    try {


        var br = object : BroadcastReceiver() {
            @SuppressLint("InvalidWakeLockTag")
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceive(context: Context?, intent: Intent?) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    for (sms in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {



                        t = sms.displayMessageBody
                        var num = sms.originatingAddress
                        var list = t.split(".")


                        if ((pswd != list[0] && list.size ==1)|| list.size>3){
                            Toast.makeText(applicationContext,"This text is not for this app",Toast.LENGTH_SHORT).show()
                        }
                        else if(pswd != list[0] && list.size==2  && list[1]==""){
                            Toast.makeText(applicationContext,"This text is not for this app",Toast.LENGTH_SHORT).show()
                        }

                        else if (pswd == list[0]) {
                            Toast.makeText(applicationContext, list[1], Toast.LENGTH_LONG).show()

                            if ("contact" == list[1]) {
                                var x = list[2]
                                Toast.makeText(this@MainActivity, x, Toast.LENGTH_LONG).show()
                                var rs = contentResolver.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    cols,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "='" + x + "'",
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                                )
                                if (rs?.moveToNext()!!) {
                                    Toast.makeText(
                                        applicationContext,
                                        rs.getString(1),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val message = rs.getString(1)
                                    val sms: SmsManager = SmsManager.getDefault()
                                    val msg = sms.divideMessage(message)
                                    sms.sendTextMessage(num, null, message, null, null)
                                }
                            } else if ("location" == list[1]) {

                                Toast.makeText(this@MainActivity, "try", Toast.LENGTH_LONG).show()

                                locationManager =
                                    getSystemService(Context.LOCATION_SERVICE) as LocationManager
                                hasGps =
                                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                                hasNetwork =
                                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                                if (hasGps || hasNetwork) {
                                    if (hasGps) {
                                        Log.d("locationGps", "hasGps")
                                        if (ActivityCompat.checkSelfPermission(
                                                this@MainActivity,
                                                android.Manifest.permission.ACCESS_FINE_LOCATION
                                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                                this@MainActivity,
                                                android.Manifest.permission.ACCESS_COARSE_LOCATION
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
                                        locationManager.requestLocationUpdates(
                                            LocationManager.GPS_PROVIDER,
                                            5000,
                                            0F,
                                            object : LocationListener {
                                                override fun onLocationChanged(location: Location?) {
                                                    if (location != null) {
                                                        locationGps = location
                                                    }
                                                }

                                                override fun onStatusChanged(
                                                    provider: String?,
                                                    status: Int,
                                                    extras: Bundle?
                                                ) {
                                                }

                                                override fun onProviderEnabled(provider: String?) {
                                                }

                                                override fun onProviderDisabled(provider: String?) {
                                                }

                                            })
                                        var LocalGpsLocation =
                                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                        if (LocalGpsLocation != null) {
                                            locationGps = LocalGpsLocation
                                        }
                                    }
                                    if (hasNetwork) {
                                        Log.d("locationGps", "hasGps")
                                        locationManager.requestLocationUpdates(
                                            LocationManager.NETWORK_PROVIDER,
                                            5000,
                                            0F,
                                            object : LocationListener {
                                                override fun onLocationChanged(location: Location?) {
                                                    if (location != null) {
                                                        locationNetwork = location
                                                    }
                                                }

                                                override fun onStatusChanged(
                                                    provider: String?,
                                                    status: Int,
                                                    extras: Bundle?
                                                ) {
                                                }

                                                override fun onProviderEnabled(provider: String?) {
                                                }

                                                override fun onProviderDisabled(provider: String?) {
                                                }

                                            })
                                        var LocalNetworkLocation =
                                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                        if (LocalNetworkLocation != null) {
                                            locationGps = LocalNetworkLocation
                                        }
                                    }

                                }


                                //YFUHFHJDKDJBCJMBNMZX  FNBSDBFJDSBBDJFJK
                                Toast.makeText(
                                    this@MainActivity,
                                    "http://maps.google.com/maps?q=+${locationGps?.longitude},${locationGps?.longitude}",
                                    Toast.LENGTH_LONG
                                ).show()
                                val sms: SmsManager = SmsManager.getDefault()
                                sms.sendTextMessage(
                                    num,
                                    null,
                                    "http://maps.google.com/maps?q=+${locationGps?.latitude},${locationGps?.longitude}",
                                    null,
                                    null
                                )


                            } else if ("mode" == list[1]) {
                                Toast.makeText(this@MainActivity, "opt 3", Toast.LENGTH_LONG).show()

                                val audioManager: AudioManager =
                                    getSystemService(AUDIO_SERVICE) as AudioManager
                                var streamMaxVolume =
                                    audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)


                                when (audioManager.getRingerMode()) {
                                    AudioManager.RINGER_MODE_SILENT -> {
                                        Log.i("MyApp", "Silent mode")
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
                                        audioManager.setStreamVolume(
                                            AudioManager.STREAM_MUSIC,
                                            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                                            0
                                        )
                                        for (i in 1..6) {
                                            audioManager.adjustStreamVolume(
                                                AudioManager.STREAM_RING,
                                                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI
                                            )
                                        }


                                    }
                                    AudioManager.RINGER_MODE_VIBRATE -> {
                                        Log.i("MyApp", "Vibrate mode")
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
                                        audioManager.setStreamVolume(
                                            AudioManager.STREAM_MUSIC,
                                            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                                            0
                                        )
                                        for (i in 1..6) {
                                            audioManager.adjustStreamVolume(
                                                AudioManager.STREAM_RING,
                                                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI
                                            )
                                        }
                                    }
                                    AudioManager.RINGER_MODE_NORMAL -> {
                                        Log.i(
                                            "MyApp",
                                            "Normal mode"
                                        )
                                        for (i in 1..6) {
                                            audioManager.adjustStreamVolume(
                                                AudioManager.STREAM_RING,
                                                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI
                                            )
                                        }
                                    }

                                }


                                //  //  //

                            }

                            if ("lock" == list[1]) {
                                Toast.makeText(this@MainActivity, "try", Toast.LENGTH_LONG).show()
                                val mDevicePolicyManager: DevicePolicyManager =
                                    getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                                mDevicePolicyManager.lockNow()


                            }

                            if ("ring" == list[1]) {
                                Toast.makeText(this@MainActivity, "entered", Toast.LENGTH_LONG)
                                    .show()
                                player = MediaPlayer.create(this@MainActivity, R.raw.songsss)
                                player.start()
                            }

                            if ("changepin" == list[1]) {
                                Toast.makeText(this@MainActivity, "try", Toast.LENGTH_LONG).show()
                                val mDevicePolicyManager: DevicePolicyManager =
                                    getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                                try {
                                    mDevicePolicyManager.resetPassword(
                                        list[2],
                                        DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY
                                    )
                                    mDevicePolicyManager.lockNow()
                                } finally {
                                    mDevicePolicyManager.lockNow()
                                }


                            }

                            if ("wipedata" == list[1]) {
                                val mDevicePolicyManager: DevicePolicyManager =
                                    getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                                mDevicePolicyManager.wipeData(0)
                            }

                            if ("battery" == list[1]) {
                                var z: Int
                                if (Build.VERSION.SDK_INT >= 21) {
                                    val bm =
                                        context!!.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                                    z = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                                } else {
                                    val iFilter =
                                        IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                                    val batteryStatus =
                                        context!!.registerReceiver(null, iFilter)
                                    val level =
                                        batteryStatus?.getIntExtra(
                                            BatteryManager.EXTRA_LEVEL,
                                            -1
                                        ) ?: -1
                                    val scale =
                                        batteryStatus?.getIntExtra(
                                            BatteryManager.EXTRA_SCALE,
                                            -1
                                        ) ?: -1
                                    val batteryPct =
                                        level / scale.toDouble()
                                    z = (batteryPct * 100).toInt()
                                }
                                val smsManager = SmsManager.getDefault()
                                smsManager.sendTextMessage(
                                    num,
                                    null,
                                    "The current battery level of your android device is $z %",
                                    null,
                                    null
                                )

                            }


                        } else {
                            val smsManager =
                                SmsManager.getDefault()
                            smsManager.sendTextMessage(
                                num,
                                null,
                                "The service you are trying to reach is failed due to incorrect password",
                                null,
                                null
                            )

                        }

                    }

                }

            }
        }
        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }
    catch(exception : ArrayIndexOutOfBoundsException ) {
        Toast.makeText(this,"not for this app",Toast.LENGTH_SHORT).show()
    }
    catch (e :Exception){
        Toast.makeText(this,"not for this app",Toast.LENGTH_SHORT).show()

    }
    finally {
        Toast.makeText(this@MainActivity,"cctoalpha Running fine ",Toast.LENGTH_LONG).show()
    }
    }
}