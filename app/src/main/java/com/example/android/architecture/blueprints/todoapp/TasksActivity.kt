/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp

import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.farhanrahman.file_create_on_broadcast.service.CustomBroadcastReceiverName
import com.farhanrahman.file_create_on_broadcast.service.FileBroadcastReceiver
import com.farhanrahman.file_create_on_broadcast.util.FileManager
import com.farhanrahman.file_create_on_broadcast.util.PermissionUtil
import com.farhanrahman.file_create_on_broadcast.util.PermissionUtil.checkPermissionResult
import com.farhanrahman.file_create_on_broadcast.util.PermissionUtil.permissionToWriteAccepted
import com.google.accompanist.appcompattheme.AppCompatTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


/**
 * Main activity for the todoapp
 */
@AndroidEntryPoint
class TasksActivity : ComponentActivity() {

    private val fileBroadcastReceiver = FileBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCompatTheme {
                TodoNavGraph()
            }
        }
//        Check permission
        PermissionUtil.requestPermission(this)
        if(PermissionUtil.permissions.isEmpty()){
            FileManager.writeFile(this, FileManager.createFile(this))
        }

        registerReceiver(fileBroadcastReceiver, IntentFilter(CustomBroadcastReceiverName.com_context_FINISH_TESTING.stringName))
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode != RESULT_CANCELED) {
            PermissionUtil.checkPermissionResult(this, requestCode, permissions, grantResults)
        }
    }

    override fun onStop() {
        super.onStop()
        if( PermissionUtil.permissionToWriteAccepted){
            FileManager.createFile(this)?.let {
                FileManager.writeFile(this,it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(fileBroadcastReceiver)
    }
}
