package droid.smart.com.tamilkuripugal.vo

data class FavouriteKurippu(
    val kurippuId: String,
    val updatedDate: Long,
    val active: Boolean,
    val cloudStatus: String,
    var title: String?,
    val image: String?
)