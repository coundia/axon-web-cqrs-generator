package com.groupe2cs.bizyhub.shared.application;

import com.groupe2cs.bizyhub.shared.application.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService  {

private final MailSender mailSender;

	public void notifyByEmail(String to, String subject, String content) {

		log.info("MAIL → To: {}, Subject: {}, Content: {}", to, subject, content);
		mailSender.send(to, subject, content);

	}
}
