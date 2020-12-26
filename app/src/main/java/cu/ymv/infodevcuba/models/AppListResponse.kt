package cu.ymv.infodevcuba.models

data class AppListResponse(
    val count: Int,
    val next: String,
    val previous: String,
    val results: List<App>
)

data class App(
    val id: Int,
    val releases_count: Int,
    val reviews_count: Int,
    val reviews_star_1: Int,
    val reviews_star_2: Int,
    val reviews_star_3: Int,
    val reviews_star_4: Int,
    val reviews_star_5: Int,
    val description: String,
    val categories: List<Categories>,
    val last_release: LastRelease,
    val developer: User,
    val package_name: String,
    val name: String,
    val video_url: String,
    val video_img: String,
    val price: Int,
    val updated: String,
    val last_updated: String,
    val update_from: String,
    val rating: Float,
    val sponsored: Int,
    val public: Boolean,
    val white_list: Boolean,
    val with_db: Boolean,
    val announced: Boolean,
    val deleted: Boolean,
    val download_count: Int,
    val relevance: Int,
    val relevance_download: Int,
    val relevance_visit: Int,
    val relevance_appearance: Int,
    val owner: Int

)

data class Categories(
    val id: Int,
    val name: String,
    val icon: String,
    val group: String,
    val icon_url: String
)

data class LastRelease(
    val id: Int,
    val human_readable_size: String,
    val version_sdk_name: String,
    val version_target_sdk_name: String,
    val permissions: List<Permissions>,
    val screenshots: List<ScreenShots>,
    val apk_file: String,
    val abi: List<Abi>,
    val version_code: Int,
    val version_name: String,
    val version_sdk: Int,
    val version_target_sdk: Int,
    val published: String,
    val sha256: String,
    val changelog: String,
    val size: Int,
    val icon: String,
    val public: Boolean,
    val deleted: Boolean,
    val direct_key: String,
    val beta: Boolean
)

data class Permissions(
    val id: Int,
    val human_readable_name: String,
    val name: String,
    val description: String,
    val icon: String
)

data class Abi(
    val abi: String
)

data class ScreenShots(
    val image: String
)