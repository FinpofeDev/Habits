//Librerías para las excepciones y para las fechas
import java.time.DateTimeException
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class ObjFecha(
    private var dia:Int = 1,
    private var mes:Int = 1,
    private var year:Int = 2000
){
    init {
        validarFecha(dia,mes,year)
    }

    public fun setDia(dia:Int){
        this.dia = dia
    }
    public fun setMes(mes:Int){
        this.mes = mes
    }
    public fun setYear(year:Int){
        this.year = year
    }
    public fun getDia():Int{
        return  dia
    }
    public fun getMes():Int{
        return mes
    }
    public fun getYear():Int{
        return year
    }

    //obtener fecha como string
    override fun toString(): String {
        val calendario = Calendar.getInstance()
        calendario.set(year,mes-1,dia)//mes empieza desde 0
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formato.format(calendario.time)
    }

    private fun validarFecha(dia:Int, mes:Int, year:Int){
        val calendario = Calendar.getInstance()
        calendario.isLenient = false //fuerza la validación estricta
        try{
            calendario.set(year,mes-1,dia)
            calendario.time//dispara la validación
        } catch(e: Exception){
            throw IllegalArgumentException("Fecha no válida: $dia/$mes/$year")
        }
    }
}

