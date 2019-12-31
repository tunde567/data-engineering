package controllers

import javax.inject.{Inject, Singleton}
import org.apache.avro.{Protocol, Schema}
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc._
import utils.{AvroProtocolToIdl, AvroSchema}

@Singleton
class AvroController @Inject()(cc: ControllerComponents) extends AbstractController(cc)  {

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.avro())
  }

  def avroFromJsonDocument(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val jsonDocument = request.body.asJson.get
    Ok(AvroSchema(Json.toJson(jsonDocument), "TestObject").toString())
  }

  def idlFromAvro(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val jsonDocument: JsValue = request.body.asJson.get
    val jsonDocumentAsString: String = jsonDocument.toString()
    val parser = new Schema.Parser()
    import AvroSchema._
    if (jsonDocument.as[JsObject].keys.contains("protocol"))
      Ok(new AvroProtocolToIdl(Protocol.parse(jsonDocumentAsString)).convert())
    else
      Ok(parser.parse(jsonDocumentAsString).toIdl("AvroSchemaTool"))
  }

  def avroFromIdl(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val idl: String = request.body.asText.get
    Ok(AvroSchema.idlToSchema(idl))
  }
}
