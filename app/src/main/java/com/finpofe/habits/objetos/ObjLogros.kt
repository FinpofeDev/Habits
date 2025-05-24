data class ObjLogros(
    var logroId: String? = null,
    var nombre: String = "",
    var descripcion: String = "",
    var recompensa: String = "",
    var fechaInicio: ObjFecha = ObjFecha(),
    var fechaFin: ObjFecha = ObjFecha(),
    var completado: Boolean = false,
)
