import com.amazonaws.services.lambda.runtime.Context
import org.pichugroup.pichuparkingapi.getParkingLots
import org.pichugroup.schema.PichuParkingAPIResponse

class PichuHandler {
    fun handleRequest(event: PlaceHolderEvent? = null, context: Context? = null): PichuParkingAPIResponse {
        return getParkingLots()
    }
}

data class PlaceHolderEvent(
    var id: String = "",
    var message: String = "",
)