package cu.ymv.infodevcuba.models

data class User(
    val user: Int,
    val username: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val avatar: String,
    var tokens: Tokens
)