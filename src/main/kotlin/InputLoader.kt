
object InputLoader {
    fun loadInput(day: String): String =
       InputLoader.javaClass.getResource("$day.txt")!!.readText()
}