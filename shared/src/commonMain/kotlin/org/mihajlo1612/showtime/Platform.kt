package org.mihajlo1612.showtime

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform