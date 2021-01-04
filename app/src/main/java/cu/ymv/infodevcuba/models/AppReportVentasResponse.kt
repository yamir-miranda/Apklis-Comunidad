package cu.ymv.infodevcuba.models

data class AppReportVentasResponse(
    val count : Int,
    val next : String,
    val previous : String,
    val results : List<Results>
)

data class Results (

    val id : Int,
    val seller : Seller,
    val products : List<Int>,
    val buyer : String,
    val buyer_phone : String,
    val description : String,
    val trans_id : Int,
    val tm_id : String,
    val external_id : String,
    val bank_id : String,
    val bank : Int,
    val ammount : Double,
    val date : String,
    val state : String,
    val send : Boolean
)

data class Seller (

    val id : Int,
    val municipality : String,
    val province : String,
    val user : String,
    val phone_number : String,
    val active : Boolean,
    val type : String,
    val name : String,
    val address : String,
    val account : String,
    val cid : String,
    val nit : String,
    val license : String,
    val email : String,
    val joined : String
)

data class ReportVentas (
    val bank: Int,
    val phone_number : String,
    val date : String
)