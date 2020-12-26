package cu.ymv.infodevcuba.models

data class DownloadVersionResponse (
    val downloads: List<MutableMap<String,Int>>
)

data class DownloadVersion (
    val version: String,
    val count: Int
)