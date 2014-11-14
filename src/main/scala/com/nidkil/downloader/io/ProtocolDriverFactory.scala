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

import com.nidkil.downloader.utils.Logging

/**
 * Retrieves the correct HTTP protocol driver based on the specified protocol.
 * 
 * Implementations of the HTTP protocol driver must be named the following
 * way and placed in the same package as the factory to be able to be loaded
 * correctly:
 * 
 * <Protocol><ProtocolDriver>
 * 
 * Note: The protocol name must start with a capital and the rest must be lower 
 * case.
 */
object ProtocolDriverFactory extends Logging {
  
  private val PROTOCOL_IMPL = "ProtocolDriver"

	def loadDriver(protocol: String): ProtocolDriver = {
    val packageName = getClass.getPackage.getName
    val camelCaseProtocol = protocol.substring(0, 1).toUpperCase.concat(protocol.substring(1).toLowerCase)
	  val classFQN = packageName.concat(".").concat(camelCaseProtocol).concat(PROTOCOL_IMPL)	
  	val classLoader = getClass.getClassLoader
  	
  	try {
  	  classLoader.loadClass(classFQN).newInstance.asInstanceOf[ProtocolDriver]
  	} catch {
  	  case e: ClassNotFoundException => {
  	    val msg = s"Make sure the protocol implementation is in a subpackage relative to '${getClass.getPackage.getName}', the package is named '${protocol.toLowerCase}' and the class is called '$PROTOCOL_IMPL'"
  	    throw new ProtocolDriverException(msg, e)
  	  }
  	}
  }

}