package DB
import com.mongodb.casbah.Imports._
import com.pharbers.util.dao.from
import com.pharbers.util.dao._data_connection

object testDB extends App{
  def insert = {
        ((12, "qianpeng") :: (13, "liwei") :: Nil) map { x =>
            val builder = MongoDBObject.newBuilder
            builder += "age" -> x._1
            builder += "name" -> x._2
            _data_connection.getCollection("col_test") += builder.result
        }
    }
    
    def query(key : String, value : AnyRef) : Int = {
        def conditions =
          value match {
            case str : String => (key -> value.asInstanceOf[String])
            case _ => ???
          }
      
        (from db() in "col_test" where conditions 
            select (x => x.getAs[Number]("age").get.intValue)).toList.head
    }
    
    def insertOther = {
        ((22, "yangyuan", "address") :: Nil) map { x =>
            val builder = MongoDBObject.newBuilder
            builder += "age" -> x._1
            builder += "name" -> x._2
            builder += "address" -> x._3
            _data_connection.getCollection("col_test") += builder.result
        }
    }
    
    def opt : Option[Int] = 
        (from db() in "col_test" where ("name" -> "qianpeng") 
            select (x => x.getAs[Number]("age").get.intValue)).toList match {
          case head :: Nil => Some(head)
          case Nil => None
          case _ => ???
        }
    
    
    def update = 
        (from db() in "col_test" where ("name" -> "qianpeng") 
            select (x => x)).toList match {
          case head :: Nil => {
            head += "age" -> 1.asInstanceOf[Number]
            _data_connection.getCollection("col_test").update(DBObject("name" -> "qianpeng"), head)
          }
          case _ => ???
        }
    
    
      val lst = for (x <- 1 to 9) yield x
      
      // java
      def sum_java : Int = {
          var sum : Int = 0
          for (x <- lst) {
              sum += x
          }
          return sum
      }
    
      // scala
      def sum_scala(lst : List[Int], c : Int)(func : (Int, Int) => Int) : Int = lst match {
        case Nil => c
        case head :: tail => sum_scala(tail, func(head, c))(func)
      }
      
      println(sum_scala(lst.toList, 0)((x, y) => x + y))
      
      val a = """abcde,asdf"""
      
//      println(a.split(","))
//      
//      println(a)
      
      insert
      //insertOther
//      println(query("address", "address"))
//      println(opt.map (x => x * x).getOrElse(0))
//      
//      update
      //println(query("name", "qianpeng"))
      //pm2016!*
}