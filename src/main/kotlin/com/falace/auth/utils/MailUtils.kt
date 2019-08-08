package com.falace.auth.utils

import org.springframework.mail.SimpleMailMessage

fun composeEmail(recipient: String, title: String, text: String): SimpleMailMessage {
    val msg = SimpleMailMessage()
    with(msg) {
        setTo(recipient)
        setSubject(title)
        setText(text)
    }
    return msg
}