package controllers

import play.api.mvc._
import com.pharbers.common.controllers.requestArgsQuery.requestArgs
import play.api.libs.json.{ JsValue, Json }
import play.api.libs.json.Json.toJson
import play.api.Logger
import com.pharbers.datacalc.pharmatrust.market.phaMarketCalc
import com.pharbers.datacalc.pharmatrust.product.phaProductCalc
import com.pharbers.datacalc.cpa.market.cpaMarketCalc
import com.pharbers.datacalc.cpa.product.cpaProductCalc
import com.pharbers.datacalc.pharmatrust.market.phaMarketCalc
import com.pharbers.datacalc.pharmatrust.market.phaMarketCalc

object maxController extends Controller {
    /*def index = Action (request => requestArgs(request)(maxController.indexModule))
    
    def indexModule(data : JsValue) : JsValue = {
        toJson(Map("status" -> "ok", "result" -> "aaaa"))
    }*/
    
    def index = Action {
      //Logger.debug("Attempting risky calculation.")
      Ok(views.html.index.render("hello"))
    }
    
    def phaMarket = Action {
        new phaMarketCalc().apply()
        Ok
    }
    
    def phaProduct = Action {
        new phaProductCalc().apply()
        Ok
    }
    
    def cpaMarket = Action {
        //new cpaMarketCalc().apply()
        Ok
    }
    
    def cpaProduct = Action{
        //new cpaProductCalc().apply()
        Ok
    }
}