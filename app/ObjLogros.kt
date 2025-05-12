class ObjLogros {
    private var nombre:String
    private var descripcion:String
    private var recompensa:String
    private var fechaInicio=ObjFecha()
    private var fechaFin=ObjFecha()

    public  fun setNombre(nombre:String){
            this.nombre = nombre
    }
    public fun setDescripcion(descripcion:String){
            this.descripcion = descripcion
    }
    public fun setRecompensa(recompensa:String){
            this.recompensa = recompensa
    }
    public fun setFechaInicio(fechaInicio:ObjFecha){
            this.fechaInicio = fechaInicio
    }
    public fun setFechaFin(fechaFin:ObjFecha){
            this.fechaFin = fechaFin
    }
    public fun getNombre():String{
            return nombre
    }
    public fun getDescripcion():String{
            return descripcion
    }
    public fun getRecompensa():String{
            return recompensa
    }
    public fun getFechaInicio():ObjFecha{
            return fechaInicio
    }
    public fun getFechaFin():objFech{
            return fechaFin
    }
}