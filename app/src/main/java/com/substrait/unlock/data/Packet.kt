// file: com/example/unlock/data/Packet.kt

package com.substrait.unlock.data

import com.google.gson.annotations.SerializedName

/**
 * Represents the entire packet structure, loaded from a JSON file.
 * This is the root object for a packet.
 */
data class PacketImage(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("created") val created: String,
    @SerializedName("sourceContent") val sourceContent: List<PacketContent>
)

/**
 * Represents a single content item within a packet.
 * For the MVP, this will only handle external HTML pages.
 */
data class PacketContent(
    @SerializedName("origin") val origin: String,
    @SerializedName("format") val format: String,
    @SerializedName("access") val access: String,
    @SerializedName("url") val url: String,
    @SerializedName("title") val title: String,
    @SerializedName("context") val context: String
)