package com.example.myapplication.adapter

import java.util.UUID

class Item (
    val id: String = UUID.randomUUID().toString(),
    val titulo: String,
    val desc: String,
    val src: String,
    val latitude: Double,
    val longitude: Double
    )