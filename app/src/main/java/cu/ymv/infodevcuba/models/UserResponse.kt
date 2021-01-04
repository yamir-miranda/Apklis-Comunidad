package cu.ymv.infodevcuba.models

data class UserResponse (

    val id : Int,
    val avatar : String,
    val is_developer : Boolean,
    val is_developer_active : Boolean,
    val seller : Seller,
    val phone_number : String,
    val code : String,
    val payed : List<String>,
    val wishlist : List<String>,
    val sha1 : String,
    val last_login : String,
    val is_superuser : Boolean,
    val username : String,
    val first_name : String,
    val last_name : String,
    val is_staff : Boolean,
    val is_active : Boolean,
    val date_joined : String,
    val email : String,
    val beta_tester : Boolean,
    val sales_admin : Boolean,
    val theme : String,
    val province : Int,
    val favorite_apps : List<Int>
)

