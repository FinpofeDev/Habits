//Librerías para las excepciones y para las fechas
import java.time.DateTimeException
import java.time.LocalDate

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

    public fun toLocalDate(): LocalDate{
        return java.time.LocalDate.of(year,mes,dia)
    }

    private fun validarFecha(dia:Int, mes:Int, year:Int){
        try{
            java.time.LocalDate.of(year,mes,dia)
        } catch (e: java.time.DateTimeException){
            throw IllegalArgumentException("Fecha no válida: $dia/$mes/$year")
        }
    }
}

