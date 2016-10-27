package com.pharbers.util

import java.security.MessageDigest

object MD5 {
  def md5(str: String): String = {
    val hash = MessageDigest.getInstance("MD5").digest(str.getBytes)
    hash.map("%02x".format(_)).mkString
  }
}