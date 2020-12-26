package cu.ymv.infodevcuba.models

data class Tokens(
    val access_token: String,
    val expires_in: String,
    val token_type: String,
    val scope: String,
    val refresh_token: String
)