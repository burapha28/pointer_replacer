/*
 * Copyright (C) 2016-2018 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.afterroot.pointerdash.utils

import java.io.Serializable
import java.util.*

//[START Data Classes for FireStore]

//Collection 'users'
data class User(var name: String,
                var email: String,
                var uid: String) : Serializable {
    constructor() : this("", "", "")
}

//Collection 'pointers'
data class Pointer(var name: String,
                   var filename: String,
                   var description: String,
                   var uploadedBy: String,
                   var time: Date?) : Serializable {
    constructor() : this("", "", "", "", null)
}

//[END]

object DatabaseFields {
    val USERS = "users"

    val FIELD_NAME = "name"
    val FIELD_EMAIL = "email"
    val FIELD_UID = "uid"
}