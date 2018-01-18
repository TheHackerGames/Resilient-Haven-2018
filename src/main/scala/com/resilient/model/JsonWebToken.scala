package com.resilient.model

case class JsonWebToken(app: String,
                        iss: String,
                        iat: Int,
                        exp: Int,
                        sub: String)