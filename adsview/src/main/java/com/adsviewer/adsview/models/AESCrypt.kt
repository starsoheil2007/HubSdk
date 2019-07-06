package com.adsviewer.adsview.models

import android.util.Base64
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class AESCrypt {

    private lateinit var cipher: Cipher
    private lateinit var key: SecretKeySpec
    private lateinit var spec: AlgorithmParameterSpec


    @Throws(Exception::class)
    fun AESCrypt(password: String) {
        // hash password with SHA-256 and crop the output to 128-bit for key
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(password.toByteArray(charset("UTF-8")))
        val keyBytes = ByteArray(32)
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.size)

        cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
        key = SecretKeySpec(keyBytes, "AES")
        spec = getIV()
    }

    fun getIV(): AlgorithmParameterSpec {
        val iv = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val ivParameterSpec: IvParameterSpec
        ivParameterSpec = IvParameterSpec(iv)

        return ivParameterSpec
    }

    @Throws(Exception::class)
    fun encrypt(plainText: String): String {
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encrypted = cipher.doFinal(plainText.toByteArray(charset("UTF-8")))
        return String(Base64.encode(encrypted, Base64.DEFAULT), Charsets.UTF_8)
    }

    @Throws(Exception::class)
    fun decrypt(cryptedText: String): String {
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        val bytes = Base64.decode(cryptedText, Base64.DEFAULT)
        val decrypted = cipher.doFinal(bytes)
        return String(decrypted, Charsets.UTF_8)
    }
}