import com.example.myapplication.SensorData
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    // Aquí Retrofit espera un ArrayList de objetos SensorData
    @GET("/get_latest_data")
    fun getLatestData(): Call<List<SensorData>>


}
