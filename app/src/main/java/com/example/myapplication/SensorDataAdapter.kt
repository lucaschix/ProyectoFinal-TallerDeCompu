import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.SensorData

class SensorDataAdapter(private val sensorDataList: List<SensorData>) :
    RecyclerView.Adapter<SensorDataAdapter.SensorDataViewHolder>() {

    class SensorDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val temperatura: TextView = itemView.findViewById(R.id.tv_temperatura)
        val humedad: TextView = itemView.findViewById(R.id.tv_humedad)
        val pm2_5: TextView = itemView.findViewById(R.id.tv_pm2_5)
        val pm10: TextView = itemView.findViewById(R.id.tv_pm10)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorDataViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sensor_data_item, parent, false)
        return SensorDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: SensorDataViewHolder, position: Int) {
        val data = sensorDataList[position]
        holder.temperatura.text = "Temperatura: ${data.temperatura}"
        holder.humedad.text = "Humedad: ${data.humedad}"
        holder.pm2_5.text = "PM2.5: ${data.pm2_5}"
        holder.pm10.text = "PM10: ${data.pm10}"
    }

    override fun getItemCount(): Int = sensorDataList.size
}
