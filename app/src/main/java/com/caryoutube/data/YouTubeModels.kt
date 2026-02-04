package com.caryoutube.data

import com.google.gson.annotations.SerializedName

// Subscriptions
data class SubscriptionListResponse(
    val items: List<SubscriptionItem> = emptyList()
)

data class SubscriptionItem(
    val snippet: SubscriptionSnippet
)

data class SubscriptionSnippet(
    val resourceId: ResourceId
)

data class ResourceId(
    val channelId: String?
)

// Channels
data class ChannelListResponse(
    val items: List<ChannelItem> = emptyList()
)

data class ChannelItem(
    val id: String,
    val snippet: ChannelSnippet?,
    val statistics: ChannelStatistics?,
    val contentDetails: ChannelContentDetails?
)

data class ChannelSnippet(
    val title: String?,
    val thumbnails: Thumbnails?
)

data class ChannelStatistics(
    val subscriberCount: String?
)

data class ChannelContentDetails(
    val relatedPlaylists: RelatedPlaylists?
)

data class RelatedPlaylists(
    val uploads: String?
)

// Playlist Items (Videos)
data class PlaylistItemListResponse(
    val items: List<PlaylistItem> = emptyList()
)

data class PlaylistItem(
    val snippet: PlaylistItemSnippet
)

data class PlaylistItemSnippet(
    val title: String,
    val thumbnails: Thumbnails?,
    val resourceId: VideoResourceId
)

data class VideoResourceId(
    val videoId: String
)

// Common
data class Thumbnails(
    @SerializedName("default") val default: Thumbnail?,
    val medium: Thumbnail?,
    val high: Thumbnail?
)

data class Thumbnail(
    val url: String
)
