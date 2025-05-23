class ObjLogros(
    private var nombre: String = "",
    private var descripcion: String = "",
    private var recompensa: String = "",
    private var fechaInicio: ObjFecha = ObjFecha(),
    private var fechaFin: ObjFecha = ObjFecha(),
    private var completado: Boolean = false
) {

    fun setNombre(nombre: String) { this.nombre = nombre }
    fun getNombre(): String = nombre

    fun setDescripcion(descripcion: String) { this.descripcion = descripcion }
    fun getDescripcion(): String = descripcion

    fun setRecompensa(recompensa: String) { this.recompensa = recompensa }
    fun getRecompensa(): String = recompensa

    fun setFechaInicio(fechaInicio: ObjFecha) { this.fechaInicio = fechaInicio }
    fun getFechaInicio(): ObjFecha = fechaInicio

    fun setFechaFin(fechaFin: ObjFecha) { this.fechaFin = fechaFin }
    fun getFechaFin(): ObjFecha = fechaFin

    fun marcarComoCompletado(){completado = true}
    fun estaCompleto():Boolean{return completado}
}
