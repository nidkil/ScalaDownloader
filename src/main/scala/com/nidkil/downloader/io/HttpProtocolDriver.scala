package com.nidkil.downloader.io

import java.io.IOException
import java.net.URL
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import com.nidkil.downloader.utils.Logging
import org.apache.http.impl.client.HttpClientBuilder
import com.nidkil.downloader.datatypes.Chunk
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.client.methods.CloseableHttpResponse

object HttpProtocolDriver {
  val PROTOCOL = "http"
}

/**
 * Implementation of a basic HTTP protocol driver.
 */
class HttpProtocolDriver extends ProtocolDriver with Logging {

  import HttpProtocolDriver._

  def protocol = PROTOCOL

  protected def client: CloseableHttpClient = {
    if (proxy == null) HttpClientBuilder.create.build
    else HttpClientBuilder.create.setProxy(proxy).build
  }

  protected def response(c: Chunk): CloseableHttpResponse = {
    val httpGet = new HttpGet(c.url.toURI)
    
    httpGet.addHeader("range", s"bytes=${c.offset}-${c.offset + c.length - 1}")

    val response = try {
      httpClient.execute(httpGet)
    } catch {
      case e: ClientProtocolException => throw new ProtocolDriverException(s"Protocol error occured initializing the URL [${e.getMessage}]", e)
      case e: IOException => throw new ProtocolDriverException(s"IO error occured loading the URL [${e.getMessage}][info=$toString]", e)
    }

    response
  }

}