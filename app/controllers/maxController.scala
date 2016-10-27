package controllers

import play.api.mvc._
import com.pharbers.common.controllers.requestArgsQuery.requestArgs
import play.api.libs.json.{ JsValue, Json }
import play.api.libs.json.Json.toJson
import play.api.Logger

object maxController extends Controller {
    /*def index = Action (request => requestArgs(request)(maxController.indexModule))
    
    def indexModule(data : JsValue) : JsValue = {
        toJson(Map("status" -> "ok", "result" -> "aaaa"))
    }*/
    
    def index = Action {
      Logger.debug("Attempting risky calculation.")
      Ok(views.html.index("我的第一个Play For Scala的程序！！！"))
    }
}