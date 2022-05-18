package com.example.flutter_plugin_demo;

import static android.content.Context.BATTERY_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import java.lang.ref.WeakReference;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/** FlutterPluginDemoPlugin */
public class FlutterPluginDemoPlugin implements FlutterPlugin, MethodCallHandler {
  private MethodChannel channel;
  private Context applicationContext;
  private WeakReference<Activity> mActivity;

  /// 保留旧版本的兼容
  public static void registerWith(PluginRegistry.Registrar registerWith) {
    MethodChannel channel = new MethodChannel(registerWith.messenger(), "flutter_plugin_demo");
    channel.setMethodCallHandler(new FlutterPluginDemoPlugin().initPlugin(channel, registerWith));
  }
  public FlutterPluginDemoPlugin initPlugin(MethodChannel methodChannel, PluginRegistry.Registrar registerWith) {
    channel = methodChannel;
    applicationContext = registerWith.context().getApplicationContext();
    mActivity = new WeakReference<>(registerWith.activity());
    return this;
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    this.applicationContext = flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_plugin_demo");
    channel.setMethodCallHandler(this);
  }

  @RequiresApi(api = VERSION_CODES.M)
  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "getBatteryLevel":
        int batteryLevel = getBatteryLevel();
        if (batteryLevel != -1) {
          result.success(batteryLevel);
        } else {
          result.error("UNAVAILABLE", "Battery level not available.", null);
        }
        break;
      case "getPlatformVersion":
        result.success("Android " + Build.VERSION.RELEASE);
        break;
      default:
        result.notImplemented();
    }
  }


  // 获取电量的原生方法
  @RequiresApi(api = VERSION_CODES.M)
  private int getBatteryLevel () {
    int batteryLevel = -1;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      BatteryManager batteryManager = applicationContext.getSystemService(BatteryManager.class);
//      BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
      batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    } else {
      Intent intent = new ContextWrapper(applicationContext).
              registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
      batteryLevel = (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) /
              intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    }
    return batteryLevel;
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
