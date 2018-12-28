package de.markhaehnel.rbtv.rocketbeanstv.util

import io.lindstrom.m3u8.model.MasterPlaylist
import io.lindstrom.m3u8.model.Variant

fun MasterPlaylist.highestBandwith(): Variant {
    return this.variants().maxBy { it -> it.bandwidth() }!!
}