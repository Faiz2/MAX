package com.pharbers.common.controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.libs.Files.TemporaryFile

import com.pharbers.util.errorcode.ErrorCode
import com.mongodb.casbah.Imports._

//import controllers.common.authCheck

object requestArgsQuery extends Controller {
  def requestArgsWithAuthCheck(request : Request[AnyContent])(func : JsValue => (MongoDBObject => JsValue)) : Result = {
  		try {
  			request.body.asJson.map { x => 
  			   Ok((new authCheck)(x)(func))
  			}.getOrElse (BadRequest("Bad Request for input"))
  		} catch {
  			case _ : Exception => BadRequest("Bad Request for input")
  		}  		   
	}
  	
  def requestArgs(request : Request[AnyContent])(func : JsValue => JsValue) : Result = {
  		try {
  			request.body.asJson.map { x => 
  				Ok(func(x))
  			}.getOrElse (BadRequest("Bad Request for input"))
  		} catch {
  			case _ : Exception => BadRequest("Bad Request for input")
  		}  		   
	}
 
  	def uploadRequestArgs(request : Request[AnyContent])(func : MultipartFormData[TemporaryFile] => JsValue) : Result = {
  		try {
   			request.body.asMultipartFormData.map { x => 
   				Ok(func(x))
  			}.getOrElse (BadRequest("Bad Request for input")) 			  
  		} catch {
  			case _ : Exception => BadRequest("Bad Request for input")
  		}
  	}
}