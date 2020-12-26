package cu.ymv.infodevcuba.models

data class User(
    val user: String,
    val username: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val fullname: String,
    val avatar: String,
    val cid: String,
    val reg_num: String,
    val bussiness: String,
    val province: String,
    val apps: String,
    val description: String,
    val sha1: String,
    var tokens: Tokens
)