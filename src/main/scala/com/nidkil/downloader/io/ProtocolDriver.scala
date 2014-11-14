/*
 * Copyright 2014 nidkil
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nidkil.downloader.io

import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

import org.apache.http.Header
import org.apache.http.HttpHeaders
import org.apache.http.HttpHost
import org.apache.http.HttpResponse
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpHead
import org.apache.http.impl.client.CloseableHttpClient

import com.nidkil.downloader.datatypes.Chunk
import com.nidkil.downloader.datatypes.RemoteFileInfo
import com.nidkil.downloader.utils.Logging

/**
 * Interface to be implemented by HTTP protocol drivers.
 */
trait ProtocolDriver extends Logging {

  protected var httpClient: CloseableHttpClient = null
  protected var httpResponse: CloseableHttpResponse = null
  
  // If a proxy is required, holds the proxy information 
  var proxy: HttpHost = null

  def protocol: String
  
  protected def client: CloseableHttpClient
  
  protected def response(c: Chunk): CloseableHttpResponse
  
  /** Retrieves a connection. **/
  def connection(c: Chunk): Option[HttpResponse] = {
    require(c != null, "Chunk cannot be null")

    if(!c.url.getProtocol().equalsIgnoreCase(protocol)) {
      throw new ProtocolDriverException(s"Only ${protocol.toUpperCase} is supported [${c.url.getProtocol}]");
    }
    
    httpClient = client
    httpResponse = response(c)
    
    Option(httpResponse)
  }

  def close = {
    if(httpResponse != null) httpResponse.close
    if(httpClient != null) httpClient.close
  }

  def inputstream: InputStream = {
    httpResponse.getEntity.getContent
  }
  
  def statusCode: Int = {
    if(httpResponse == null) throw new ProtocolDriverException("Status is only available after connection is called")
    else httpResponse.getStatusLine.getStatusCode
  }

  def statusReason: String = {
    if(httpResponse == null) throw new ProtocolDriverException("Status is only available after connection is called")
    else httpResponse.getStatusLine.getReasonPhrase
  }

  /** Convenience method to check the status of a response. **/
  def statusOk: Boolean = {
    // Check if the response is within the 200 range of the HTTP status code 
    ((httpResponse.getStatusLine.getStatusCode / 100) == 2)
  }
  
  /** Retrieves information about the remote file. **/
  def remoteFileInfo(u: URL, debug: Boolean = false): RemoteFileInfo = {
    val head = new HttpHead(u.toURI)
    val httpGet = client.execute(head)
    val headers = httpGet.getAllHeaders

    if(debug) logger.debug(s"HTTP headers:\n${debugHeaders(headers)}")
    
    // Mime type of the download file
    val mimeType = headers.find(_.getName == HttpHeaders.CONTENT_TYPE) match {
      case None => throw new IllegalStateException("Unkown content type")
      case Some(header) => header.getValue
    }

    // Does the server support download ranges
    val acceptRanges = headers.find(_.getName == HttpHeaders.ACCEPT_RANGES) match {
      case Some(header) => header.getValue match {
        case "none" => throw new IllegalStateException("Server does not accept range requests")
        case "bytes" => true
        case value: String => {
          logger.warn(s"Server returned a header of '${HttpHeaders.ACCEPT_RANGES}: $value', only values 'bytes' can be handled")
          true
        }
      }
      case None => false
    }

    // The size of the download file
    val contentLength = headers.find(_.getName == HttpHeaders.CONTENT_LENGTH) match {
      case None => throw new IllegalStateException("Unkown content length")
      case Some(header) => header.getValue.toLong
    }

    // When was the download file last modified
    val lastModified = headers.find(_.getName == HttpHeaders.LAST_MODIFIED) match {
      case None => throw new IllegalStateException("Unkown last modified date")
      case Some(header) => {
        // Hypertext Transfer Protocol � HTTP/1.1 RFC (RFC2616) section 3.3.1
        // All HTTP date/time stamps MUST be represented in Greenwich Mean Time (GMT), 
        // without exception. For the purposes of HTTP, GMT is exactly equal to UTC 
        // (Coordinated Universal Time). This is indicated in the first two formats 
        // by the inclusion of �GMT� as the three-letter abbreviation for time zone, 
        // and MUST be assumed when reading the asctime format.
        val sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US)
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
        sdf.parse(header.getValue.toString)
      }
    }

    httpGet.close
    
    new RemoteFileInfo(u, mimeType, acceptRanges, contentLength, lastModified)
  }
  
  /** Convenience method for debugging HTTP headers. **/
  private def debugHeaders(headers: Array[Header]): String = {
    val result = new StringBuilder()
    headers.foreach { h => 
      if(result.length > 0) result.append("\n")
      result.append(s"\t${h.getName}: ${h.getValue}")
    }
    result.toString
  }

}