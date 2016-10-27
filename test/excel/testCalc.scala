package excel

import scala.io.Source


object testCalc extends App{
    val test = Source.fromFile("""D:\123.txt""","UTF-8")
    var content = test.getLines().toList
    var sum = 0.toDouble
    content foreach(x => sum += x.split("===")(0).toDouble)
    println(sum)
}