package org.galaxio.gatling.kafka.request.builder

import com.sksamuel.avro4s.{FromRecord, RecordFormat, SchemaFor}
import io.gatling.core.session.Expression
import org.apache.kafka.common.header.Headers

trait Sender[K, V] {

  def send(requestName: Expression[String], payload: Expression[V]): RequestBuilder[Nothing, V]

  def send(requestName: Expression[String], key: Option[Expression[K]], payload: Expression[V]): RequestBuilder[K, V]

  def send(
      requestName: Expression[String],
      key: Option[Expression[K]],
      payload: Expression[V],
      headers: Option[Expression[Headers]],
      silent: Option[Boolean],
  ): RequestBuilder[K, V]

}

object Sender extends LowPriorSender {

  implicit def Avro4sSender[K, V](implicit
      schema: SchemaFor[V],
      format: RecordFormat[V],
      fromRecord: FromRecord[V],
      headers: Headers,
  ): Sender[K, V] = new Sender[K, V] {

    override def send(requestName: Expression[String], payload: Expression[V]): RequestBuilder[Nothing, V] =
      new KafkaAvro4sRequestBuilder[Nothing, V](
        Avro4sAttributes(
          requestName = requestName,
          key = None,
          payload = payload,
          schema = schema,
          format = format,
          fromRecord = fromRecord,
          headers = None,
          silent = None,
        ),
      )

    override def send(
        requestName: Expression[String],
        key: Option[Expression[K]],
        payload: Expression[V],
    ): RequestBuilder[K, V] =
      new KafkaAvro4sRequestBuilder[K, V](
        Avro4sAttributes(
          requestName = requestName,
          key = key,
          payload = payload,
          schema = schema,
          format = format,
          fromRecord = fromRecord,
          headers = None,
          silent = None,
        ),
      )

    override def send(
        requestName: Expression[String],
        key: Option[Expression[K]],
        payload: Expression[V],
        headers: Option[Expression[Headers]],
        silent: Option[Boolean],
    ): RequestBuilder[K, V] =
      new KafkaAvro4sRequestBuilder[K, V](
        Avro4sAttributes(
          requestName = requestName,
          key = key,
          payload = payload,
          schema = schema,
          format = format,
          fromRecord = fromRecord,
          headers = headers,
          silent = silent,
        ),
      )

  }

}
